package uk.co.deixel.deixutils;

import java.io.FileOutputStream;
import java.util.logging.Logger;

import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EventListener implements Listener {
    DeixUtils plugin = JavaPlugin.getPlugin(DeixUtils.class);
    FileConfiguration config = plugin.getConfig();
    Logger logger = plugin.getLogger();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(Settings.DISCORD_CHAT.get()) {
            String playerName = event.getPlayer().getDisplayName();
            String message = event.getMessage();
            String pipeMessage = "**" + playerName + "**: " + message;
            sendToDiscord(pipeMessage);

        }
    }

    @EventHandler 
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(Settings.DISCORD_JOIN.get()) {
            String playerName = event.getPlayer().getDisplayName();
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            int maxPlayers = plugin.getServer().getMaxPlayers();
            sendToDiscord("**" + playerName + "** joined! (" + onlinePlayers + "/" + maxPlayers+")");

        }
        Player player = event.getPlayer();
        Difficulty difficulty = player.getServer().getWorld("world").getDifficulty();
        player.sendMessage("Welcome " + player.getDisplayName() + "! The difficulty is currently set to " + difficulty.toString().toLowerCase() + ".");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(Settings.DISCORD_LEAVE.get()) {
            String playerName = event.getPlayer().getDisplayName();
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            int maxPlayers = plugin.getServer().getMaxPlayers();
            sendToDiscord("**" + playerName + "** left! (" + (onlinePlayers - 1) + "/" + maxPlayers+")");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(Settings.DISCORD_DEATH.get()) {
            sendToDiscord(event.getDeathMessage());
        }
    }

    int playersInBed = 0;
    boolean skippedToMorning = false;
    BukkitTask skipTask;

    public void debugBeds() {
        logger.fine("DEBUG INFO FOR BEDS");
        logger.fine("Players in bed: " + playersInBed );
        logger.fine("skippedToMorning: " + skippedToMorning);
        logger.fine("skipTask set: " + (skipTask != null));
    }

    public void resetBeds() {
        playersInBed = 0;
        skippedToMorning = false;
        skipTask = null;
        plugin.getServer().broadcastMessage("Beds reset!");
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        if(event.getSkipReason().ordinal() == TimeSkipEvent.SkipReason.NIGHT_SKIP.ordinal()) {
            skippedToMorning = true;
            logger.fine("Skipped to morning");
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            int playersInWorld = event.getBed().getWorld().getPlayers().size();
            playersInBed++;
            int percentRequired = Settings.SLEEP_PERCENT.getInt();
            int percentInBed = (int) ( ( (double) playersInBed / (double) playersInWorld ) *100 );
            plugin.getServer().broadcastMessage(event.getPlayer().getName() + " is in bed (" + percentInBed + "%)");

            if(percentInBed >= percentRequired && skipTask == null) {
                skipTask = new SkipToMorning(event.getBed().getWorld()).runTaskLater(plugin, 100); //Mimic the behaviour of normal beds by waiting 5 seconds before jumping to morning
                logger.fine("Scheduled skip to morning in 5 seconds");
            }
        }
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        int playersInWorld = event.getBed().getWorld().getPlayers().size();
        playersInBed--;
        if(playersInBed < 0) {
            playersInBed = 0;
        }
        if(!skippedToMorning) {
            int percentInBed = (int) ( ( (double) playersInBed / (double) playersInWorld ) *100 );
            plugin.getServer().broadcastMessage(event.getPlayer().getName() + " has left bed (" + percentInBed + "%)");
            if(skipTask != null && percentInBed < Settings.SLEEP_PERCENT.getInt()) {
                skipTask.cancel();
                logger.fine("Cancelling skip to morning");
                skipTask = null;
            }
        }
        else if(skippedToMorning && playersInBed == 0) {
            skippedToMorning = false;
        }
    }

    private class SkipToMorning extends BukkitRunnable {
        World world;
        public SkipToMorning(World world) {
            this.world = world;
        }

        public void run() {
            skippedToMorning = true;
            world.setTime(0);
            if(world.hasStorm()) {
                world.setWeatherDuration(1);
            }
            plugin.getServer().broadcastMessage("Good Morning!");
            skipTask = null;
        }
    }

    @EventHandler
    public void onBlockInteract(EntityInteractEvent event) {
        if(Tag.WOODEN_PRESSURE_PLATES.isTagged(event.getBlock().getType())) {
            if(event.getEntityType() != EntityType.PLAYER) {
                if(Settings.PLAYER_ONLY_PRESSURE_PLATE.get()) {
                    event.setCancelled(true);
                }
            }
        }

        // If it's a villager trying to interact with a wooden door (and the config is enabled), then...
        if(event.getBlock().getType() == Material.AIR 
        && event.getEntityType() == EntityType.VILLAGER
        && Settings.VILLAGER_LOCK.get()) {
            Block targetDoor = null;
            outerloop:
            for(int x = -1; x <= 1; x++) {
                for(int z = -1; z <= 1; z++) {
                    Block searchBlock = event.getBlock().getRelative(x, 0, z);
                    if(Tag.WOODEN_DOORS.isTagged(searchBlock.getType())) {
                        logger.fine("Found target door at " + searchBlock.getLocation());
                        targetDoor = searchBlock;
                        break outerloop;
                    }
                }
            }
            if(null != targetDoor) {
                Block blockToCheck = targetDoor.getRelative(0, -2, 0);
                logger.fine("Block to check is " + blockToCheck.getType() + " at " + blockToCheck.getLocation());
                /* This attempts to resolve a minecraft:xyz name to a Material and returns null if it fails
                    which we just treat as the lock not being enabled */
                Material lockMaterial = Material.matchMaterial(Settings.VILLAGER_LOCK_BLOCK.getString());
                logger.fine("lockMaterial = " + lockMaterial);
                if(blockToCheck.getType() == lockMaterial) {
                    logger.info("Cancelling door open");
                    event.setCancelled(true);
                }

            }
        }
        
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;        
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && Tag.SIGNS.isTagged(event.getClickedBlock().getType())){
            Sign sign = (Sign) event.getClickedBlock().getState();
            try {
                String firstLine = sign.getLine(0);
                if(firstLine.toLowerCase().equals("[difficulty]")) {
                    String secondLine = sign.getLine(1).toUpperCase();
                    Difficulty newDiff = Difficulty.valueOf(secondLine);
                    Difficulty currDiff = plugin.getServer().getWorld("world").getDifficulty();
                    Player player = event.getPlayer();
                    if(newDiff.ordinal() != currDiff.ordinal()) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "difficulty " + newDiff.toString().toLowerCase());
                        String msg = " changed the difficulty to " + newDiff.toString().toLowerCase() + "!";
                        plugin.getServer().broadcastMessage(player.getDisplayName() + msg);
                        if(Settings.DISCORD_DIFFICULTY.get()) {
                            sendToDiscord("**" + player.getDisplayName() + "**" + msg);
                        }
                    }
                    else {
                        player.sendMessage("Difficulty is already set to " + currDiff.toString().toLowerCase() + "!");
                    }
                }
            }
            catch(IndexOutOfBoundsException ex) { // Sign doesn't have line we try to access, harmless
                // No-op
            }
            catch(IllegalArgumentException|NullPointerException ex) { // Specified difficulty is invalid
                event.getPlayer().sendMessage("ERROR: There's something wrong with this sign!");
            }
        }
    }

    @EventHandler
    public void onBlockChanged(EntityChangeBlockEvent event) {
        if(event.getEntityType() == EntityType.ENDERMAN) {
            if(Settings.STOP_ENDERMAN_GRIEF.get()) {
                logger.fine("endergrief cancelled");
                event.setCancelled(true);
            }
        }
    }

    public void sendToDiscord(String msg) {
        if(Settings.SEND_TO_DISCORD.get()) {
            /*
            The pipe is polled every second for new data, then anything waiting is just consumed in one block.
            This means if >1 message is sent within that polling window, the messages are joined together.
            To fix this, append each message with a null byte that we can then use to split things up at other end
            */
            /*byte[] tempMsgBytes = msg.getBytes();
            byte[] msgBytes = new byte[tempMsgBytes.length + 1];
            int i;
            for(i = 0; i < tempMsgBytes.length; i++) {
                msgBytes[i] = tempMsgBytes[i];
            }
            msgBytes[i] = 0;*/
            try {
                FileOutputStream outPipe = new FileOutputStream(Settings.PIPE_TO_DISCORD.getString());
                //outPipe.write(msgBytes);
                outPipe.write(msg.getBytes());
                outPipe.close();
            }
            catch (Exception e) {
                logger.severe(e.toString());
            }
        }
    }
    
}