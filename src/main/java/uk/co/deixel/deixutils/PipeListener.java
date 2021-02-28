package uk.co.deixel.deixutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        /* 
         * There's a bug in Java where opening a pipe without anything to be read causes a hang.
         * Fix this by writing something to the pipe before opening it for read.
         */
        logger.info("Sending message to prep pipe");
        try {
            FileOutputStream tempOutStream = new FileOutputStream(Settings.PIPE_FROM_DISCORD.getString());
            //outPipe.write(msgBytes);
            tempOutStream.write("Starting Discord Integration".getBytes());
            tempOutStream.close();
        }
        catch (Exception e) {
            logger.severe(e.toString());
        }

        
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