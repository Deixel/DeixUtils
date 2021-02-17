package uk.co.deixel.deixutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.scheduler.BukkitRunnable;

public class PipeListener extends BukkitRunnable{

    FileInputStream inPipe;
    DeixUtils plugin;

    public PipeListener(DeixUtils plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Created PipeListener");
        try {
            inPipe = new FileInputStream(Settings.PIPE_FROM_DISCORD.getString());
        }
        catch (FileNotFoundException ex) {
            plugin.getLogger().severe(ex.toString());
        }
        plugin.getLogger().info("Created inPipe");
    }
    
    public void run() {
        byte[] buff;
            try {
                int availableBytes = inPipe.available();
                if(availableBytes > 0) {
                    buff = new byte[availableBytes];
                    inPipe.read(buff);
                    String message = new String(buff);
                    plugin.getServer().broadcastMessage(message);
                }       
            }
            catch (IOException ex) {
                plugin.getLogger().severe(ex.toString());
            }
    }

}