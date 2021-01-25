package uk.co.deixel.deixutils.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.deixel.deixutils.DeixUtils;


public class ResetBeds implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,Command command, String label, String[] args) {
        JavaPlugin.getPlugin(DeixUtils.class).getEventListener().resetBeds();
        return true;
    }
    
}