package uk.co.deixel.deixutils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;


/**
 * This enum serves 3 purposes
 * 
 * 1. In the code, use Settings.<THING>.get()/getString()/getInt() to access config settings - prevents potential typos 
 * 2. Provides config name tab-completion for /config command
 * 3. Provides parameter tab-completion for /config command
 */

public enum Settings {
    SEND_TO_DISCORD("send-to-discord", Type.BOOLEAN),
    LISTEN_TO_DISCORD("listen-to-discord", Type.BOOLEAN),
    PIPE_TO_DISCORD("pipe-to-discord", Type.STRING),
    PIPE_FROM_DISCORD("pipe-from-discord", Type.STRING),
    SLEEP_PERCENT("sleep-percent", Type.INTEGER),
    DISCORD_JOIN("discord.join", Type.BOOLEAN),
    DISCORD_LEAVE("discord.leave", Type.BOOLEAN),
    DISCORD_CHAT("discord.chat", Type.BOOLEAN),
    DISCORD_DEATH("discord.death", Type.BOOLEAN),
    DISCORD_DIFFICULTY("discord.difficulty", Type.BOOLEAN),
    PLAYER_ONLY_PRESSURE_PLATE("player-only-pressure-plate", Type.BOOLEAN),
    VILLAGER_LOCK("villager-lock", Type.BOOLEAN),
    VILLAGER_LOCK_BLOCK("villager-lock-block", Type.STRING),
    STOP_ENDERMAN_GRIEF("stop-enderman-grief", Type.BOOLEAN),
    DIFFICULTY_RESET_ON_LEAVE("difficulty-reset-on-leave", Type.BOOLEAN),
    ;

    private final String text;
    Type type;

    DeixUtils plugin = JavaPlugin.getPlugin(DeixUtils.class);
    FileConfiguration config = plugin.getConfig();

    Settings(final String text, Type type) {
        this.text = text;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return text;
    }

    public boolean get() {
        if(Type.BOOLEAN != getType()) {
            throw new WrongTypeException(Type.BOOLEAN, getType());
        }
        return config.getBoolean(text);
    }

    public String getString() {
        if(Type.STRING != getType()) {
            throw new WrongTypeException(Type.STRING, getType());
        }
        return config.getString(text);
    }

    public int getInt() {
        if(Type.INTEGER != getType()) {
            throw new WrongTypeException(Type.INTEGER, getType());
        }
        return config.getInt(text);
    }

    public enum Type {
        STRING,
        BOOLEAN,
        INTEGER
    }

    public class WrongTypeException extends RuntimeException {
        public WrongTypeException(Type expectedType, Type actualType) {
            super("Attempted to read " + expectedType + " but found " + actualType);
        }
    }
 }