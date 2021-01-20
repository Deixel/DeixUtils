package uk.co.deixel.deixutils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.deixel.deixutils.DeixUtils;
import uk.co.deixel.deixutils.Settings;

public class Config implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Syntax: /config <config> <value>");
            return true;
        }
        Settings setting;
        try {
            setting = Settings.valueOf(args[0]);
        } catch (final Exception ex) {
            sender.sendMessage(ChatColor.RED + "Error: Unable to find a setting with name " + args[0]);
            return true;
        }

        final DeixUtils plugin = JavaPlugin.getPlugin(DeixUtils.class);

        switch(setting.getType()) {
            case BOOLEAN:
                if(!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                    sender.sendMessage(setting.name() + " expects a boolean, but '" + args[1] + "' does not resolve to a boolean.");
                    return true;
                }
                boolean boolValue = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set(setting.toString(), boolValue);
                break;
            case INTEGER:
                try {
                    int intValue = Integer.parseInt(args[1]);
                    plugin.getConfig().set(setting.toString(), intValue);
                } 
                catch (final NumberFormatException ex) {
                    sender.sendMessage(setting.name() + " expects an integer, but '" + args[1] + "' does not resolve to a integer.");
                    return true;
                }
                break;
            case STRING:
                plugin.getConfig().set(setting.toString(), args[1]);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Error: Unknown data type");
        }

        plugin.saveConfig();
        sender.sendMessage("Set " + setting.name() + " (" + setting.toString() + ") to " + setting.getType().name() + " '" + args[1] + "'");

        return true;
    }

    public static class ConfigTabCompleter implements TabCompleter {

        @Override
        public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
            if(args.length == 1) {
                final List<String> result = new ArrayList<String>();
                for(final Settings setting : Settings.values()) {
                    result.add(setting.name());
                }
                return result;
            }
            else if(args.length == 2 ) {
                final List<String> result = new ArrayList<String>();
                Settings.Type type;
                try {
                    type = Settings.valueOf(args[0]).getType();
                }
                catch (IllegalArgumentException ex) {
                    return null;
                }
                switch(type) {
                    case BOOLEAN:
                        result.add("true");
                        result.add("false");
                        break;
                    case INTEGER:
                        result.add("INTEGER");
                        break;
                    case STRING: 
                        result.add("STRING");
                        break;
                    default:
                        return null;
                }
                return result;
            }
            return null;
        }
    }
    
}