package uk.co.deixel.deixutils;

import java.io.FileOutputStream;

import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EventListener implements Listener {
    DeixUtils plugin = JavaPlugin.getPlugin(DeixUtils.class);
    FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(config.getBoolean(Settings.DISCORD_CHAT.toString())) {
            String playerName = event.getPlayer().getDisplayName();
            String message = event.getMessage();
            String pipeMessage = "**" + playerName + "**: " + message;
            sendToDiscord(pipeMessage);
        }
    }

    @EventHandler 
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(config.getBoolean(Settings.DISCORD_JOIN.toString())) {
            String playerName = event.getPlayer().getName();
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            int maxPlayers = plugin.getServer().getMaxPlayers();
            sendToDiscord("**" + playerName + "** joined! (" + onlinePlayers + "/" + maxPlayers+")");
        }
       }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(config.getBoolean(Settings.DISCORD_LEAVE.toString())) {
            String playerName = event.getPlayer().getDisplayName();
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            int maxPlayers = plugin.getServer().getMaxPlayers();
            sendToDiscord("**" + playerName + "** left! (" + (onlinePlayers - 1) + "/" + maxPlayers+")");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(config.getBoolean(Settings.DISCORD_DEATH.toString())) {
            sendToDiscord(event.getDeathMessage());
        }
    }

    int playersInBed = 0;
    boolean skippedToMorning = false;
    BukkitTask skipTask;

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        if(event.getSkipReason().ordinal() == TimeSkipEvent.SkipReason.NIGHT_SKIP.ordinal()) {
            skippedToMorning = true;
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            int playersInWorld = event.getBed().getWorld().getPlayers().size();
            playersInBed++;
            int percentRequired = config.getInt(Settings.SLEEP_PERCENT.toString());
            int percentInBed = (int) ( ( (double) playersInBed / (double) playersInWorld ) *100 );
            plugin.getServer().broadcastMessage(event.getPlayer().getName() + " is in bed (" + percentInBed + "%)");

            if(percentInBed >= percentRequired && skipTask == null) {
                skipTask = new SkipToMorning(event.getBed().getWorld()).runTaskLater(plugin, 100); //Mimic the behaviour of normal beds by waiting 5 seconds before jumping to morning
            }

        }
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        int playersInWorld = event.getBed().getWorld().getPlayers().size();
        playersInBed--;
        if(!skippedToMorning) {
            int percentInBed = (int) ( ( (double) playersInBed / (double) playersInWorld ) *100 );
            plugin.getServer().broadcastMessage(event.getPlayer().getName() + " has left bed (" + percentInBed + "%)");
            if(skipTask != null && percentInBed < config.getInt(Settings.SLEEP_PERCENT.toString())) {
                skipTask.cancel();
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
                if(config.getBoolean(Settings.PLAYER_ONLY_PRESSURE_PLATE.toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void sendToDiscord(String msg) {
        if(config.getBoolean(Settings.SEND_TO_DISCORD.toString())) {
            DeixUtils plugin = JavaPlugin.getPlugin(DeixUtils.class);
            try {
                FileOutputStream outPipe = new FileOutputStream(plugin.getConfig().getString(Settings.PIPE_TO_DISCORD.toString()));
                outPipe.write(msg.getBytes());
                outPipe.close();
            }
            catch (Exception e) {
                plugin.getLogger().severe(e.toString());
            }
        }
    }
    
}