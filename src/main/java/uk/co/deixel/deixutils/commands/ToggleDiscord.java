package uk.co.deixel.deixutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.deixel.deixutils.DeixUtils;
import uk.co.deixel.deixutils.Settings;

public class ToggleDiscord implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DeixUtils plugin = JavaPlugin.getPlugin(DeixUtils.class);
        if(Settings.LISTEN_TO_DISCORD.get() && Settings.SEND_TO_DISCORD.get()) {
            plugin.stopDiscord();
        }
        else {
            plugin.startDiscord();
        }
        return true;
    }
    
}