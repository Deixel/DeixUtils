package uk.co.deixel.deixutils.commands;

import org.bukkit.Difficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class DebugDiff implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,Command command, String label, String[] args) {
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