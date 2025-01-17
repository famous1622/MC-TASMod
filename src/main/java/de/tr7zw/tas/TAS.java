package de.tr7zw.tas;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TAS {

	private Minecraft mc = Minecraft.getMinecraft();
	private boolean loaded = false;
	private ArrayList<KeyFrame> keyFrames = new ArrayList<>();
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
		keyFrames = new ArrayList<>();
		line = 0;
		try{
			try (Stream<String> stream = Files.lines(tasData.toPath())) {
				stream.forEach(s -> parseLine(s, ++line));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//keyFrames.add(new KeyFrame(false, false, false, false, false, false, 0, 0, false, false));
	}

	public void clearData(){
		if(mc.player != null){
			mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
		}
		/*mc.gameSettings.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
		mc.gameSettings.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");*/
		loaded = false;
		keyFrames = new ArrayList<>();
	}

	public static void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
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
	public void onPlayerTick(TickEvent.PlayerTickEvent ev)
	{
		//if(ev.phase == Phase.START && loaded && mc.player != null && !(mc.player.movementInput instanceof TASInput))
		//	mc.player.movementInput = new TASInput(this, keyFrames);
		if(ev.phase == Phase.START && mc.player != null && mc.player.movementInput instanceof TASInput){
			TASInput input = (TASInput) mc.player.movementInput;
			if(input.donePlaying){
				clearData();
			}
		}
	}

	@SubscribeEvent()
	public void onMenu(net.minecraftforge.client.event.GuiOpenEvent ev)
	{
		if(ev.getGui() instanceof GuiMainMenu){
			clearData();
		}
	}

	public Recorder recorder = null;
	
	@SubscribeEvent
	public void onChatSend(ServerChatEvent ev)
	{
		if(ev.getMessage().startsWith(".r")){							//Command to start a tas recording
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(recorder != null){										//error messages
				sendMessage("A recording is running!");
				return;
			}
			if(mc.player.movementInput instanceof TASInput){		
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
					sendMessage(TextFormatting.RED+"Invalid character(s) for your filename");
					FileName="null";
					genname=true;
					return;
				}else genname=false;
				
			}else {
				genname=true;
				sendMessage(TextFormatting.RED+"Too many arguments!");
				return;
			}
			
			sendMessage("Starting the tas recording!");
			x=mc.player.posX;							//Saving the position and headrotation where the command was issued... Is needed for '.f'
			y=mc.player.posY;
			z=mc.player.posZ;
			pitch=mc.player.rotationPitch;
			yaw=mc.player.rotationYaw;
			recorder = new Recorder();
			MinecraftForge.EVENT_BUS.register(recorder);
			return;
		}
		if(ev.getMessage().equals(".s")){				//Command to stop the tas recording you made with .r...
			ev.setCanceled(true);						//The file is saved to the .minecraft/saves/world directory with a generated or custom filename
			if(recorder == null){
				sendMessage(TextFormatting.RED+"No recording running!");
				return;
			}
			sendMessage("Stopped the tas recording!");
			MinecraftForge.EVENT_BUS.unregister(recorder);
			
			if (genname==true||FileName.equals("null")){
				/*File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + "recording_" + System.currentTimeMillis() +".tas");*/
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles" + File.separator + "recording_" + System.currentTimeMillis() +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
			else if (genname==false){
				/*File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + FileName +".tas");*/
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles" + File.separator + FileName +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
		}
		if(ev.getMessage().equals(".f")){				//Command to stop the recording, don't save it to a file,
			ev.setCanceled(true);						//and teleports you back to the start, where you entered .r (basically a bit like a savestate)
			if(recorder == null){
				sendMessage(TextFormatting.RED+"No recording running!");
				return;
			}
			sendMessage("Aborting recording!");
			MinecraftForge.EVENT_BUS.unregister(recorder);
			mc.player.setPositionAndRotation(x,y,z,yaw,pitch);			//Teleports you where the .r command was issued
			recorder = null;
			return;
		}
		if(ev.getMessage().startsWith(".p")){			//Command to play back the tas recording
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(args.length != 2){
				sendMessage(TextFormatting.RED+"Example: .p <filename>  (without .tas)");
				return;
			}
			if(Minecraft.getMinecraft().getIntegratedServer() != null && !loaded){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles" + File.separator + args[1] + ".tas");
				if(file.exists()){
					loadData(file);
					TASInput.donePlaying=false;
					mc.player.movementInput = new TASInput(this, keyFrames);
					sendMessage("Loaded File");
				}else{
					sendMessage(TextFormatting.RED+"File not found: " + file.getAbsolutePath());
				}
			}
		}
		if(ev.getMessage().equals(".b")){				//Command to break the current playback (.p)
			ev.setCanceled(true);
			if (TASInput.donePlaying==true){
				sendMessage(TextFormatting.RED+"No playback running!");
			}
			else {
				TASInput.donePlaying=true;
				sendMessage(TextFormatting.GREEN+"Aborting playback...");
			}
		}
		
		if(ev.getMessage().startsWith(".tp")){ 			//Command to Teleport you to the start of the TASfile
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(args.length != 2){
				sendMessage(TextFormatting.RED+"Wrong usage. Example: .tp <filename>");
				return;
			}
			if(Minecraft.getMinecraft().getIntegratedServer() != null && !loaded){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles"+ File.separator + args[1] + ".tas");
				if(file.exists()){
					line = 0;
					try{
						BufferedReader Buff = new BufferedReader(new FileReader(file));
						String[] Location= Buff.readLine().split("\\(|(, )|\\)");
						
						mc.player.setPosition(Double.parseDouble(Location[1]),
											Double.parseDouble(Location[2]),
											Double.parseDouble(Location[3]));
						sendMessage("Teleporting...");
						Buff.close();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
					
				}else{
					sendMessage(TextFormatting.RED+"File not found: " + file.getAbsolutePath());
				}
			}
		}
		if(ev.getMessage().startsWith(".fd")){			//Command to disable Fall Damage...
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(args.length == 2){
				if(args[1].equalsIgnoreCase("info")&&FallDamage)sendMessage(TextFormatting.GREEN+"Fall Damage is enabled.");
				if(args[1].equalsIgnoreCase("info")&&FallDamage==false)sendMessage(TextFormatting.GREEN+"Fall Damage is currently"+ TextFormatting.RED+TextFormatting.BOLD+" disabled."+TextFormatting.RESET+TextFormatting.GREEN+" Taking Fall Damage has a chance to desync the TAS");
				return;
			}
			else if(args.length==1){
				if(FallDamage){
					FallDamage=false;
					sendMessage(TextFormatting.GREEN+"Fall Damage"+TextFormatting.RED+TextFormatting.BOLD+" disabled.");
				}
				else if(!FallDamage){
					FallDamage=true;
					sendMessage(TextFormatting.GREEN+"Fall Damage enabled.");
				}
			}
			else sendMessage(TextFormatting.RED+"Wrong usage! Either '.fd' or '.fd info'");
		}
		if(ev.getMessage().equals(".folder")){		//Command for opening the correct directory
			ev.setCanceled(true);
			try {
				Desktop.getDesktop().open(new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(ev.getMessage().startsWith(".help")){				//Command for help! Will probably added to real commands
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if (args.length==1||args[1].equals("1")){			//Output for '.help' /.help 1'
			sendMessage(TextFormatting.YELLOW+"This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in your minecraft world, which then can be played back."
					+ "\n"+TextFormatting.AQUA+"Mod by tr7zw and ScribbleLP");
			sendMessage(TextFormatting.GOLD+"Enter '.help 2' for commands");
			}
			else if (args.length==2&&args[1].equals("2")){		//Output for '.help 2'
				sendMessage(TextFormatting.GOLD+"Commands:\n"
						+ TextFormatting.YELLOW+".r"+TextFormatting.AQUA+" (Filename)"+TextFormatting.GREEN+" -Starts a recording (Filename is optional)\n\n"
						+ TextFormatting.YELLOW+".s"+TextFormatting.GREEN+" -Stops the recording\n\n"
						+ TextFormatting.YELLOW+".f"+TextFormatting.GREEN+" -Aborts the recording and tp's you back where you started\n\n"
						+ TextFormatting.YELLOW+".p"+TextFormatting.AQUA+" <filename>"+TextFormatting.GREEN+"  -Plays back the recording, don't add a .tas to the filename\n\n"
						+ TextFormatting.YELLOW+".b"+TextFormatting.GREEN+" -Aborts the TAS-playback\n\n"
						+ TextFormatting.YELLOW+".tp"+TextFormatting.AQUA+" <filename>"+TextFormatting.GREEN+ " -Teleports you to the starting location. Can be found in the first line of the .tas file\n\n"
						+ TextFormatting.YELLOW+".fd"+TextFormatting.AQUA+" (info)"+TextFormatting.GREEN+" -Disables FallDamage, since taking damage has a chance of desyncing the TAS\n\n"
						+ TextFormatting.YELLOW+".folder"+TextFormatting.GREEN+" -Opens the directory where the .tas files will be saved\n\n"
						+ TextFormatting.YELLOW+".help"+TextFormatting.AQUA+" <1,2>"+TextFormatting.GREEN+" -Well guess what this does...");
			} else sendMessage(TextFormatting.RED+"Too many arguments... Did you mean '.help 2'?");
		}
	}
	@SubscribeEvent
	public void onCloseServer(PlayerEvent.PlayerLoggedOutEvent ev){ 		//When hitting save and quit, recording (with .r) stops
		if(recorder!=null){
			MinecraftForge.EVENT_BUS.unregister(recorder);
			//SAVE
			if (genname==true||FileName.equals("null")){
				/*File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + "recording_" + System.currentTimeMillis() +".tas");*/
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles" + File.separator + "recording_" + System.currentTimeMillis() +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
			else if (genname==false){
				/*File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + FileName +".tas");*/
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						"tasfiles" + File.separator + FileName +".tas");
				recorder.saveData(file);
				recorder = null;
			return;
			}
		}
	}
	@SubscribeEvent
	public void onOpenServer(PlayerEvent.PlayerLoggedInEvent ev){ 		//When joining the world, help plays
			sendMessage(TextFormatting.GREEN+"TASmod enabled, type in .help for more info");
	}
	//Cancel Fall Damage
	@SubscribeEvent
	public void onPlayerFalling(LivingFallEvent ev){
			ev.setCanceled(!FallDamage);
	}

}
