package uk.co.deixel.deixutils.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NetherCoords implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,Command command, String label, String[] args) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Location currentLoc = player.getLocation();
                    if(currentLoc.getWorld().getEnvironment() == World.Environment.THE_END) {
                        sender.sendMessage("Does not work in The End!");
                        return true;
                    }
                    else if(currentLoc.getWorld().getEnvironment() == World.Environment.NETHER) {
                        int x = currentLoc.getBlockX() * 8;
                        int z = currentLoc.getBlockZ() * 8;
                        sender.sendMessage("Portal location in Overworld: " + x + ", " + z);
                        return true;
                    }
                    else {
                        int x = currentLoc.getBlockX() / 8;
                        int z = currentLoc.getBlockZ() / 8;
                        sender.sendMessage("Portal location in Nether: " + x + ", " + z);
                        return true;
                    }
                }
                else {
                    sender.sendMessage("You must be a player!");
                }
                return true;
    }
    
}