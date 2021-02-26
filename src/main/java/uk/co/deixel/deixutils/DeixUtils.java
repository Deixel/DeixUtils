package uk.co.deixel.deixutils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import uk.co.deixel.deixutils.commands.Commands;
public class DeixUtils extends JavaPlugin {
    
    BukkitTask task;
    FileConfiguration config;
    EventListener listener;
    HashMap<UUID, Location> lastDeathLocations = new HashMap<UUID, Location>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        listener = new EventListener();

        getServer().getPluginManager().registerEvents(listener, this);
        getLogger().info("Registered EventListener");

        if(config.getBoolean(Settings.LISTEN_TO_DISCORD.toString())) {
            startDiscord();
        }
        
        int commandCount = 0;
        for(Commands cmd : Commands.values()) {
            getCommand(cmd.getName()).setExecutor(cmd.getExecutor());
            if(cmd.getCompleter() != null) {
                getCommand(cmd.getName()).setTabCompleter(cmd.getCompleter());
            }
            commandCount++;
        }
        getLogger().info("Added " + commandCount + " commands");

        int recipeCount = 0;
        for(DyeColor colour : DyeColor.values()) {
            Material wool = Material.valueOf(colour.name() + "_WOOL");
            Material dye = Material.valueOf(colour.name() + "_DYE");

            ItemStack resultStack = new ItemStack(wool, 8);
            NamespacedKey key = new NamespacedKey(this, "recipe_dye_" + colour.name() + "_wool");
            ShapelessRecipe recipe = new ShapelessRecipe(key, resultStack).addIngredient(dye);
            for(int i = 0; i < 8; i++) {
                recipe.addIngredient(new MaterialChoice(Tag.WOOL));
            }  
            getServer().addRecipe(recipe);
            recipeCount++;
        }
        getLogger().info("Added " + recipeCount + " recipies");
        getLogger().info("Ready!");
    }

    public void onDisable() {
        getLogger().info("Later losers!");
        if(task != null) {
            task.cancel();
        }        
    }

    public EventListener getEventListener() {
        return listener;
    }

    public void startDiscord() {
        config.set(Settings.LISTEN_TO_DISCORD.toString(), true);
        config.set(Settings.SEND_TO_DISCORD.toString(), true);
        saveConfig();

        getLogger().info("Will send and listen for Discord messages...");

        task = new PipeListener(this).runTaskTimerAsynchronously(this, 0L, 20L);
    }

    public void stopDiscord() {
        config.set(Settings.LISTEN_TO_DISCORD.toString(), false);
        config.set(Settings.SEND_TO_DISCORD.toString() , false);
        saveConfig();
        if(task != null) {
            getLogger().info("Will stop sending and listening for Discord messages...");
            task.cancel();
        }
    }

    public void setDeathLocation(Player player, Location location) {
        lastDeathLocations.put(player.getUniqueId(), location);
    }

    public Location getDeathLocation(Player player) {
        return lastDeathLocations.get(player.getUniqueId());
    }
}