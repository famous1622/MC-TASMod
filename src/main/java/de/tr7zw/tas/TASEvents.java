package de.tr7zw.tas;

import de.tr7zw.tas.commands.Recordc;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Events for the TASmod
 *
 * @author ScribbleLP
 */
public class TASEvents {
    /**
     * Variable to enable fall damage<br>
     * If true, fall damage will be enabled
     */
    public static boolean FallDamage;
    public static boolean StopRecOnWorldClose;
    private static String[] arguments;
    private Minecraft mc = Minecraft.getMinecraft();
    private String[] Buttons = null;


    public void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setArguments(String[] args) {
        arguments = args;
    }

    public void readingFile(String[] args, int stopAt) {
        File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                "tasfiles" + File.separator + args[0] + ".tas");
        try {
            BufferedReader Buff = new BufferedReader(new FileReader(file));
            String s;
            int line = 0;
            while (true) {
                if ((s = Buff.readLine()).equalsIgnoreCase("END") || Playback.donePlaying) {
                    break;
                } else if (s.startsWith("#") || s.startsWith("/")) {
                    continue;
                } else if (line == stopAt) {
                    Buttons = s.split(";");
                    Buff.close();
                    return;
                }
                line++;
            }
            Buff.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Cancel Fall Damage
    @SubscribeEvent
    public void onPlayerFalling(LivingFallEvent ev) {
        ev.setCanceled(!FallDamage);
    }

    //Message when joining the server
    @SubscribeEvent
    public void onOpenServer(PlayerEvent.PlayerLoggedInEvent ev) {
        sendMessage("TASmod enabled, type in /tasmod for more info");
    }

    //When hitting save and quit, the recording (with /record) stops
    @SubscribeEvent
    public void onCloseServer(PlayerEvent.PlayerLoggedOutEvent ev) {
        if (!Recorder.donerecording && StopRecOnWorldClose) {
            Recordc.recorder.stopRecording();
            Playback.donePlaying = true;
            return;
        }
    }
}

