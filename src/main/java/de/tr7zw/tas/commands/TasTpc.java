package de.tr7zw.tas.commands;


import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.tr7zw.tas.Playback;
import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASInput;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TasTpc extends CommandBase {


    public static TAS recorder = new TAS();
    private Minecraft mc = Minecraft.getMinecraft();
    private boolean check = false;

    public List<String> getFilenames() {
        List<String> tab = new ArrayList<String>();
        File folder = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + "tasfiles");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            tab.add(listOfFiles[i].getName().replaceAll("\\.tas", ""));
        }
        return tab;
    }

    public void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "tastp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tastp or /tptas <Filename (without .tas. If not set, it generates a filename)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("tptas");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (Recorder.donerecording && Playback.donePlaying && TASInput.donePlaying) {
            if (args.length == 0) {
                sendMessage(TextFormatting.RED + "Wrong usage! Use this command to tp to the start of a TASfile. Add a tasfilename behind this");
                return;
            }
            if (args.length == 1) {
                new TAS().teleportToTAS(args);
                return;
            }
            if (args.length > 1) sendMessage(ChatFormatting.RED + "Too many arguments");
            return;
        } else if (!Recorder.donerecording) {
            sendMessage(TextFormatting.RED + "A recording is running. /record or /fail to abort recording");
            return;
        } else if (!Playback.donePlaying || !TASInput.donePlaying) {
            sendMessage(TextFormatting.RED + "A playback is running. /play to abort");
            return;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          BlockPos targetPos) {
        List<String> tab = new ArrayList<String>();
        if (args.length == 1) {
            tab = getFilenames();
            if (tab.isEmpty()) {
                sendMessage(TextFormatting.RED + "No files in directory");
                return null;
            }
            return getListOfStringsMatchingLastWord(args, tab);
        } else {
            return super.getTabCompletions(server, sender, args, targetPos);
        }
    }
}
