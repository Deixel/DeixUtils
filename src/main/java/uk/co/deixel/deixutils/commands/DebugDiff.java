package uk.co.deixel.deixutils.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.deixel.deixutils.DeixUtils;
import org.bukkit.Difficulty;


public class DebugDiff implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,Command command, String label, String[] args) {
        Player player = (Player) sender;
        Difficulty difficulty = sender.getServer().getWorld("world").getDifficulty();
        String diff = "Unknown";
        switch(difficulty) {
            case PEACEFUL: diff = "Peaceful"; break;
            case EASY: diff = "Easy"; break;
            case NORMAL: diff = "Normal"; break;
            case HARD: diff = "Hard"; break;
        }
        sender.sendMessage("The difficulty is currently set to " + diff);
                return true;
    }
    
}