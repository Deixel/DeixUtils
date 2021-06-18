package uk.co.deixel.deixutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.scheduler.BukkitRunnable;

public class PipeListener extends BukkitRunnable{

    FileInputStream inPipe;
    DeixUtils plugin;
    Logger logger;

    public PipeListener(DeixUtils plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        logger.info("Created PipeListener");
        
        try {
            inPipe = new FileInputStream(Settings.PIPE_FROM_DISCORD.getString());
        }
        catch (FileNotFoundException ex) {
            logger.severe(ex.toString());
        }
        logger.info("Created inPipe");
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