package uk.co.deixel.deixutils.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCoords implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Location currentLoc = player.getLocation();
                    player.chat(currentLoc.getBlockX() + ", " + currentLoc.getBlockY() + ", " + currentLoc.getBlockZ());
                    return true;
                }
                else {
                    sender.sendMessage("You must be a player!");
                }
                return true;
    }
    
}