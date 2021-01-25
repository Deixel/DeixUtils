package uk.co.deixel.deixutils.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public enum Commands {
    NETHERCOORDS("nethercoords", new NetherCoords()),
    SAYCOORDS("saycoords", new SayCoords()),
    TOGGLEDISCORD("togglediscord", new ToggleDiscord()),
    CONFIG("config", new Config(), new Config.ConfigTabCompleter()),
    RESETBEDS("resetbeds", new ResetBeds()),
    DEBUGBEDS("debugbeds", new DebugBeds());

    private final String name;
    private final CommandExecutor executor;
    private TabCompleter completer;
    
    Commands(final String name, final CommandExecutor executor, TabCompleter completer) {
        this.name = name;
        this.executor = executor;
        this.completer = completer;
    }
    Commands(final String name, final CommandExecutor executor) {
        this(name, executor, null);
    }

    public TabCompleter getCompleter() {
        return completer;
    }
 
    public CommandExecutor getExecutor() {
        return executor;
    }

    public String getName() {
        return name;
    }
}