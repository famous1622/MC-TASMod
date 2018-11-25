package de.tr7zw.tas;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;
import ibxm.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import scala.reflect.internal.Phase;

public class TAS {

	private Minecraft mc = Minecraft.getMinecraft();
	private boolean loaded = false;
	private ArrayList<KeyFrame> keyFrames = new ArrayList();
	private int line = 0;
	private double x=0.0;
	private double y=0.0;
	private double z=0.0;
	private float pitch=0;
	private float yaw=0;
	private boolean FallDamage=true;
	private String FileName= "null";
	private boolean genname=true;
	

	public void loadData(File tasData){
		loaded = true;
		keyFrames = new ArrayList();
		line = 0;
		try{
				BufferedReader stream= new BufferedReader(new FileReader(tasData));
				String s;
				while ((s=stream.readLine())!= null){
				parseLine(s, ++line);
				}
				stream.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//keyFrames.add(new KeyFrame(false, false, false, false, false, false, 0, 0, false, false));
	}

	public void clearData(){
		if(mc.thePlayer != null){
			mc.thePlayer.movementInput = new MovementInputFromOptions(mc.gameSettings);
		}
		/*mc.gameSettings.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
		mc.gameSettings.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");*/
		loaded = false;
		keyFrames = new ArrayList();
	}

	public static void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void parseLine(String line, int lineid){				//Reading a line of a file
		if(line.startsWith("#") || line.startsWith("//"))return;//Comments
		String[] args = line.split(";");
		int repeats = 1;
		try{
			repeats = Integer.parseInt(args[0]);
		}catch(Exception ex){}
		try{
			KeyFrame frame = new KeyFrame(args[1].equalsIgnoreCase("W"),	//up
					args[2].equalsIgnoreCase("S"), //down
					args[3].equalsIgnoreCase("A"), //left
					args[4].equalsIgnoreCase("D"), //right
					args[5].equalsIgnoreCase("Space"), //jump
					args[6].equalsIgnoreCase("Shift"), //sneak
					args[7].equalsIgnoreCase("Ctrl"), //sprint
					Float.parseFloat(args[8]), //pitch
					Float.parseFloat(args[9]), //yaw
					args[10].equalsIgnoreCase("LK"), //leftclick
					args[11].equalsIgnoreCase("RK"), //rightclick
					Integer.parseInt(args[12])); //hotbar
			
			for(int i = 0; i < repeats; i++){
				
				keyFrames.add(frame);
			}
		}catch(Exception ex){
			System.err.println("Error parsing line " + lineid);
			sendMessage("Error parsing line " + lineid);
			ex.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.PlayerTickEvent ev)
	{
		//if(ev.phase == Phase.START && loaded && mc.player != null && !(mc.player.movementInput instanceof TASInput))
		//	mc.player.movementInput = new TASInput(this, keyFrames);
		if(ev.phase == TickEvent.Phase.START &&loaded&& mc.thePlayer != null && mc.thePlayer.movementInput instanceof TASInput){
			TASInput input = (TASInput) mc.thePlayer.movementInput;
			if(input.donePlaying){
				clearData();
			}
		}
	}

	@SubscribeEvent()
	public void onMenu(net.minecraftforge.client.event.GuiOpenEvent ev)
	{
		if(ev.gui instanceof GuiMainMenu){
			TASInput.donePlaying=true;
			clearData();
		}
	}

	public Recorder recorder = null;

	@SubscribeEvent
	public void onChatSend(ServerChatEvent ev)
	{
		if(ev.message.startsWith(".r")){							//Command to start a tas recording
			ev.setCanceled(true);
			String[] args = ev.message.split(" ");
			if(recorder != null){										//error messages
				sendMessage("A recording is running!");
				return;
			}
			if(!TASInput.donePlaying){		
				sendMessage("A record is playing!");
				return;
			}
			if(args.length == 1){
				sendMessage("No filename set! Generating one...");
				genname=true;
			}
			else if (args.length == 2){									//Check for bad characters in filenames
				FileName=args[1];
				if(FileName.contains("/")
						||FileName.contains(".")
						||FileName.contains("\r")
						||FileName.contains("\t")
						||FileName.contains("\0")
						||FileName.contains("\f")
						||FileName.contains("`")
						||FileName.contains("?")
						||FileName.contains("*")
						||FileName.contains("\\")
						||FileName.contains("<")
						||FileName.contains(">")
						||FileName.contains("|")
						||FileName.contains("\"")
						||FileName.contains(":")){
					sendMessage(EnumChatFormatting.RED+"Invalid character(s) for your filename");
					FileName="null";
					genname=true;
					return;
				}else genname=false;
				
			}else {
				genname=true;
				sendMessage(EnumChatFormatting.RED+"Too many arguments!");
				return;
			}
			
			sendMessage("Starting the tas recording!");
			x=mc.thePlayer.posX;							//Saving the position and headrotation where the command was issued... Is needed for '.f'
			y=mc.thePlayer.posY;
			z=mc.thePlayer.posZ;
			pitch=mc.thePlayer.rotationPitch;
			yaw=mc.thePlayer.rotationYaw;
			recorder = new Recorder();
			MinecraftForge.EVENT_BUS.register(recorder);
			return;
		}
		if(ev.message.equals(".s")){					//Command to stop the tas recording you made with .r...
			ev.setCanceled(true);						//The file is saved to the .minecraft/saves/world directory with a generated or custom filename
			if(recorder == null){
				sendMessage(EnumChatFormatting.RED+"No recording running!");
				return;
			}
			sendMessage("Stopped the tas recording!");
			MinecraftForge.EVENT_BUS.unregister(recorder);
			if (genname==true||FileName.equals("null")){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + "recording_" + System.currentTimeMillis() +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
			else if (genname==false){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + FileName +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
		}
		if(ev.message.equals(".f")){					//Command to stop the recording, don't save it to a file,
			ev.setCanceled(true);						//and teleports you back to the start, where you entered .r (basically a bit like a savestate)
			if(recorder == null){
				sendMessage(EnumChatFormatting.RED+"No recording running!");
				return;
			}
			sendMessage("Aborting recording!");
			MinecraftForge.EVENT_BUS.unregister(recorder);
			mc.thePlayer.setPositionAndRotation(x,y,z,yaw,pitch);			//Teleports you where the .r command was issued
			recorder = null;
			return;
		}
		if(ev.message.startsWith(".p")){			//Command to play back the tas recording
			ev.setCanceled(true);
			String[] args = ev.message.split(" ");
			if(args.length != 2){
				sendMessage(EnumChatFormatting.RED+"Example: .p <filename>  (without .tas)");
				return;
			}
			if(Minecraft.getMinecraft().getIntegratedServer() != null){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + args[1] + ".tas");
				if(file.exists()){
					loadData(file);
					TASInput.donePlaying=false;
					TASInput.breaking=false;
					mc.thePlayer.movementInput = new TASInput(this, keyFrames);
					sendMessage("Loaded File");
				}else{
					sendMessage(EnumChatFormatting.RED+"File not found: " + file.getAbsolutePath());
				}
			}
		}
		if(ev.message.equals(".b")){				//Command to break the current playback (.p)
			ev.setCanceled(true);
			if (TASInput.breaking==true){
				sendMessage(EnumChatFormatting.RED+"No playback running!");
			}
			else {
				TASInput.breaking=true;
				TASInput.donePlaying=true;
				sendMessage(EnumChatFormatting.GREEN+"Aborting playback...");
			}
		}
		
		if(ev.message.startsWith(".tp")){ 			//Command to Teleport you to the start of the TASfile
			ev.setCanceled(true);
			String[] args = ev.message.split(" ");
			if(args.length != 2){
				sendMessage(EnumChatFormatting.RED+"Wrong usage. Example: .tp <filename>");
				return;
			}
			if(Minecraft.getMinecraft().getIntegratedServer() != null){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + args[1] + ".tas");
				if(file.exists()){
					line = 0;
					try{
						BufferedReader Buff = new BufferedReader(new FileReader(file));
						String[] Location= Buff.readLine().split("\\(|(, )|\\)");
						
						mc.thePlayer.setPositionAndUpdate(Double.parseDouble(Location[1]),
											Double.parseDouble(Location[2]),
											Double.parseDouble(Location[3]));
						sendMessage("Teleporting...");
						Buff.close();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
					
				}else{
					sendMessage(EnumChatFormatting.RED+"File not found: " + file.getAbsolutePath());
				}
			}
		}
		if(ev.message.startsWith(".fd")){			//Command to disable Fall Damage...
			ev.setCanceled(true);
			String[] args = ev.message.split(" ");
			if(args.length == 2){
				if(args[1].equalsIgnoreCase("info")&&FallDamage)sendMessage(EnumChatFormatting.GREEN+"Fall Damage is enabled.");
				if(args[1].equalsIgnoreCase("info")&&FallDamage==false)sendMessage(EnumChatFormatting.GREEN+"Fall Damage is currently"+ EnumChatFormatting.RED+EnumChatFormatting.BOLD+" disabled."+EnumChatFormatting.RESET+EnumChatFormatting.GREEN+" Taking Fall Damage has a chance to desync the TAS");
				return;
			}
			else if(args.length==1){
				if(FallDamage){
					FallDamage=false;
					sendMessage(EnumChatFormatting.GREEN+"Fall Damage"+EnumChatFormatting.RED+EnumChatFormatting.BOLD+" disabled.");
				}
				else if(!FallDamage){
					FallDamage=true;
					sendMessage(EnumChatFormatting.GREEN+"Fall Damage enabled.");
				}
			}
			else sendMessage(EnumChatFormatting.RED+"Wrong usage! Either '.fd' or '.fd info'");
		}
		if(ev.message.equals(".folder")){		//Command for opening the correct directory
			ev.setCanceled(true);
			try {
				Desktop.getDesktop().open(new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(ev.message.startsWith(".help")){				//Command for help! Will probably added to real commands
			ev.setCanceled(true);
			String[] args = ev.message.split(" ");
			if (args.length==1||args[1].equals("1")){			//Output for '.help' /.help 1'
			sendMessage("This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in your minecraft world, which then can be played back.");
			sendMessage(EnumChatFormatting.AQUA+"Mod by tr7zw and ScribbleLP");
			sendMessage(EnumChatFormatting.GOLD+"Enter '.help 2' for commands");
			}
			else if (args.length==2&&args[1].equals("2")){		//Output for '.help 2'
				sendMessage(EnumChatFormatting.GOLD+"Commands:");
				sendMessage(EnumChatFormatting.YELLOW+".r"+EnumChatFormatting.AQUA+" (Filename)"+EnumChatFormatting.RESET+" -Starts a recording (Filename is optional)");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".s"+EnumChatFormatting.RESET+" -Stops the recording");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".f"+EnumChatFormatting.RESET+" -Aborts the recording and tp's you back where you started");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".p"+EnumChatFormatting.AQUA+" <filename>"+EnumChatFormatting.RESET+"  -Plays back the recording, don't add a .tas to the filename");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".b"+EnumChatFormatting.RESET+" -Aborts the TAS-playback");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".tp"+EnumChatFormatting.AQUA+" <filename>"+EnumChatFormatting.RESET+ " -Teleports you to the starting location. Can be found in the first line of the .tas file");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".fd"+EnumChatFormatting.AQUA+" (info)"+EnumChatFormatting.RESET+" -Disables FallDamage, since taking damage has a chance of desyncing the TAS");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".folder"+EnumChatFormatting.RESET+" -Opens the directory where the .tas files will be saved");
				sendMessage("");
				sendMessage(EnumChatFormatting.YELLOW+".help"+EnumChatFormatting.AQUA+" <1,2>"+EnumChatFormatting.RESET+" -Well guess what this does...");
			} else sendMessage(EnumChatFormatting.RED+"Too many arguments... Did you mean '.help 2'?");
		}
	}
	@SubscribeEvent
	public void onCloseServer(WorldEvent.Unload ev){ 		//When hitting save and quit, recording (with .r) stops
		if(recorder!=null){
			MinecraftForge.EVENT_BUS.unregister(recorder);
			//SAVE
			if (genname==true||FileName.equals("null")){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + "recording_" + System.currentTimeMillis() +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
			else if (genname==false){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + FileName +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
		}
	}
	@SubscribeEvent
	public void onOpenServer(WorldEvent.Load ev){ 		//When joining the world, help plays
			sendMessage(EnumChatFormatting.GREEN+"TASmod enabled, type in .help for more info");
	}
	//Cancel Fall Damage
	@SubscribeEvent
	public void onPlayerFalling(LivingFallEvent ev){
			ev.setCanceled(!FallDamage);
	}

}
