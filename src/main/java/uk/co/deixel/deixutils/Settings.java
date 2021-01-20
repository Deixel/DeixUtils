package uk.co.deixel.deixutils;

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
    PLAYER_ONLY_PRESSURE_PLATE("player-only-pressure-plate", Type.BOOLEAN)
    ;

    private final String text;
    Type type;

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


    public enum Type {
        STRING,
        BOOLEAN,
        INTEGER
    }
}