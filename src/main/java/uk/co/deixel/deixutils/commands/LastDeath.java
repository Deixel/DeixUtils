package uk.co.deixel.deixutils.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.deixel.deixutils.DeixUtils;

public class LastDeath implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Location lastDeathLocation = JavaPlugin.getPlugin(DeixUtils.class).getDeathLocation(player);
                    if(lastDeathLocation != null) {
                        player.sendMessage("Your last death was at " + lastDeathLocation.getBlockX() + ", " + lastDeathLocation.getBlockY() + ", " + lastDeathLocation.getBlockZ());
                    }
                    else {
                        player.sendMessage("Sorry, you haven't died recently");
                    }

                }
                else {
                    sender.sendMessage("You must be a player!");
                }
                return true;
    }
    
}