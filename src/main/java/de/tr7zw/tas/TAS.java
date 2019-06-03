package de.tr7zw.tas;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import de.tr7zw.tas.duck.PlaybackInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TAS {

    private Minecraft mc = Minecraft.getMinecraft();
    private boolean loaded = false;
    private ArrayList<KeyFrame> keyFrames = new ArrayList<>();
    private int line = 0;
    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;
    private float pitch = 0;
    private float yaw = 0;
    private String FileName = "null";
    private boolean genname = true;
    private boolean recording = false;


    public void loadData(File tasData) {
        loaded = true;
        keyFrames = new ArrayList<>();
        line = 0;
        try {
            try (Stream<String> stream = Files.lines(tasData.toPath())) {
                stream.forEach(s -> parseLine(s, ++line));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clearData() {
        loaded = false;
        keyFrames = new ArrayList<>();
    }

    public void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void parseLine(String line, int lineid) {                //Reading a line of a file
        if (line.startsWith("#") || line.startsWith("//") || line.equalsIgnoreCase("END")) return;//Comments
        String[] args = line.split(";");
        int repeats = 1;
        try {
            repeats = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }
        try {
            KeyFrame frame = new KeyFrame(
                    mode, args[1].equalsIgnoreCase("W"),    //up
                    args[2].equalsIgnoreCase("S"), //down
                    args[3].equalsIgnoreCase("A"), //left
                    args[4].equalsIgnoreCase("D"), //right
                    args[5].equalsIgnoreCase("Space"), //jump
                    args[6].equalsIgnoreCase("Shift"), //sneak
                    args[7].equalsIgnoreCase("Ctrl"), //sprint
                    Float.parseFloat(args[8]), //pitch
                    Float.parseFloat(args[9]), //yaw
                    args[10], //leftclick
                    args[11], //rightclick
                    Integer.parseInt(args[12]), MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y); //hotbar

            for (int i = 0; i < repeats; i++) {

                keyFrames.add(frame);
            }
        } catch (Exception ex) {
            System.err.println("Error parsing line " + lineid);
            sendMessage("Error parsing line " + lineid);
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent ev) {
        if (ev.phase == Phase.START && mc.player != null && ((PlaybackInput)mc.player.movementInput).getPlayback() instanceof TASInput) {
            if (TASInput.donePlaying) {
                clearData();
            }
        }
    }

    @SubscribeEvent()
    public void onMenu(net.minecraftforge.client.event.GuiOpenEvent ev) {
        if (ev.getGui() instanceof GuiMainMenu) {
            clearData();
        }
    }

    public Recorder recorder = null;

    public void startRecord() {
        genname = true;
        record();
    }

    public void startRecord(String[] args) {
        if (args.length == 1) {                                    //Check for bad characters in filenames
            FileName = args[0];
            if (FileName.contains("/")
                    || FileName.contains(".")
                    || FileName.contains("\r")
                    || FileName.contains("\t")
                    || FileName.contains("\0")
                    || FileName.contains("\f")
                    || FileName.contains("`")
                    || FileName.contains("?")
                    || FileName.contains("*")
                    || FileName.contains("\\")
                    || FileName.contains("<")
                    || FileName.contains(">")
                    || FileName.contains("|")
                    || FileName.contains("\"")
                    || FileName.contains(":")) {
                sendMessage(TextFormatting.RED + "Invalid character(s) for your filename");
                FileName = "null";
                genname = true;
                return;
            } else genname = false;

        } else {
            genname = true;
            sendMessage(TextFormatting.RED + "Too many arguments!");
            return;
        }
        record();
    }

    private void record() {
        recording = true;
        sendMessage("Starting the tas recording!");
        x = mc.player.posX;                            //Saving the position and headrotation where the command was issued... Is needed for '.f'
        y = mc.player.posY;
        z = mc.player.posZ;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
        pitch = mc.player.rotationPitch;
        yaw = mc.player.rotationYaw;
        recorder = new Recorder();
        Recorder.donerecording = false;
        Recorder.recordstep = 0;
        TASInput.step = 0;
        Playback.frame = 0;
        MinecraftForge.EVENT_BUS.register(recorder);
    }

    public void stopRecording() {
        if (Recorder.donerecording) {

            sendMessage(TextFormatting.RED + "No recording running!");
            return;
        }
        sendMessage("Stopped the tas recording!");
        MinecraftForge.EVENT_BUS.unregister(recorder);

        if (genname || FileName.equals("null")) {
				/*File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + "recording_" + System.currentTimeMillis() +".tas");*/
            File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles" + File.separator + "recording_" + System.currentTimeMillis() + ".tas");
            Recorder.donerecording = true;
            recorder.saveData(file);
            recorder = null;
            return;
        } else if (genname == false) {
				/*File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + FileName +".tas");*/
            File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles" + File.separator + FileName + ".tas");
            Recorder.donerecording = true;
            recorder.saveData(file);
            recorder = null;
            return;
        }
    }

    public void abortRecording() {
        if (recorder == null) {
            sendMessage(TextFormatting.RED + "No recording running!");
            return;
        }
        sendMessage("Aborting recording!");
        MinecraftForge.EVENT_BUS.unregister(recorder);
        mc.player.sendChatMessage("/tp " + x + " " + y + " " + z + " " + yaw + " " + pitch);            //Teleports you where the /record command was issued
        recorder = null;
        Recorder.donerecording = true;
        return;
    }

    public void playTAS(String[] args) { //Command to play back the tas recordingS
        ((PlaybackInput)mc.player.movementInput).setPlayback(new Playback(args));
        Playback.donePlaying = false;
        Recorder.recordstep = 0;
        Playback.frame = 0;
        TASInput.step = 0;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
    }

    public void playTAS(String[] args, File file) {            //Command to play back the tas recording
        Recorder.recordstep = 0;
        Playback.frame = 0;
        TASInput.step = 0;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
        TASInput.breaking = false;
        loadData(file);
        TASInput.donePlaying = false;
        ((PlaybackInput)mc.player.movementInput).setPlayback(new TASInput(this, keyFrames));
        sendMessage("Loaded File");
    }

    public void abortTAS() {            //Command to break the current playback (.p)
        if (TASInput.donePlaying) {
            sendMessage(TextFormatting.RED + "No playback running!");
        } else {
            TASInput.donePlaying = true;
            sendMessage(TextFormatting.GREEN + "Aborting playback...");
        }
    }

    public void teleportToTAS(String[] args) {            //Command to Teleport you to the start of the TASfile
        if (Minecraft.getMinecraft().getIntegratedServer() != null && !loaded) {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles" + File.separator + args[0] + ".tas");
            if (file.exists()) {
                line = 0;
                try {
                    BufferedReader Buff = new BufferedReader(new FileReader(file));
                    String[] Location = Buff.readLine().split("\\(|(, )|\\)");

                    mc.player.sendChatMessage("/tp " + Location[1] + " " +
                            Location[2] + " " +
                            Location[3]);
                    sendMessage("Teleporting...");
                    Buff.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }


            } else {
                sendMessage(TextFormatting.RED + "File not found: " + file.getAbsolutePath());
            }
        }
    }

    public void openWorkFolder() {        //Command for opening the correct directory
        try {
            Desktop.getDesktop().open(new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Unused legacy method... was used in earlier versions
    public void showHelp(String[] args) {                //Command for help! Will probably added to real commands
        if (args.length == 1 || args[1].equals("1")) {            //Output for '.help' /.help 1'
            sendMessage(TextFormatting.YELLOW + "This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in your minecraft world, which then can be played back."
                    + "\n" + TextFormatting.AQUA + "Mod by tr7zw and ScribbleLP");
            sendMessage(TextFormatting.GOLD + "Enter '.help 2' for commands");
        } else if (args.length == 2 && args[1].equals("2")) {        //Output for '.help 2'
            sendMessage(TextFormatting.GOLD + "Commands:\n"
                    + TextFormatting.YELLOW + ".r" + TextFormatting.AQUA + " (Filename)" + TextFormatting.GREEN + " -Starts a recording (Filename is optional)\n\n"
                    + TextFormatting.YELLOW + ".s" + TextFormatting.GREEN + " -Stops the recording\n\n"
                    + TextFormatting.YELLOW + ".f" + TextFormatting.GREEN + " -Aborts the recording and tp's you back where you started\n\n"
                    + TextFormatting.YELLOW + ".p" + TextFormatting.AQUA + " <filename>" + TextFormatting.GREEN + "  -Plays back the recording, don't add a .tas to the filename\n\n"
                    + TextFormatting.YELLOW + ".b" + TextFormatting.GREEN + " -Aborts the TAS-playback\n\n"
                    + TextFormatting.YELLOW + ".tp" + TextFormatting.AQUA + " <filename>" + TextFormatting.GREEN + " -Teleports you to the starting location. Can be found in the first line of the .tas file\n\n"
                    + TextFormatting.YELLOW + ".fd" + TextFormatting.AQUA + " (info)" + TextFormatting.GREEN + " -Disables FallDamage, since taking damage has a chance of desyncing the TAS\n\n"
                    + TextFormatting.YELLOW + ".folder" + TextFormatting.GREEN + " -Opens the directory where the .tas files will be saved\n\n"
                    + TextFormatting.YELLOW + ".help" + TextFormatting.AQUA + " <1,2>" + TextFormatting.GREEN + " -Well guess what this does...");
        } else sendMessage(TextFormatting.RED + "Too many arguments... Did you mean '.help 2'?");
    }
}
