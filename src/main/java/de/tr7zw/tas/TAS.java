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

	public void parseLine(String line, int lineid){
		if(line.startsWith("#") || line.startsWith("//"))return;//Comments
		String[] args = line.split(";");
		int repeats = 1;
		try{
			repeats = Integer.parseInt(args[0]);
		}catch(Exception ex){}
		try{
			KeyFrame frame = new KeyFrame(args[1].equalsIgnoreCase("W"),
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
					Integer.parseInt(args[12]));
			
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
		if(ev.getMessage().startsWith(".r")){							//Start Recording
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(recorder != null){
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
			else if (args.length == 2){
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
					sendMessage("§cInvalid character(s) for your filename");
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
			x=mc.player.posX;
			y=mc.player.posY;
			z=mc.player.posZ;
			pitch=mc.player.rotationPitch;
			yaw=mc.player.rotationYaw;
			recorder = new Recorder();
			MinecraftForge.EVENT_BUS.register(recorder);
			return;
		}
		if(ev.getMessage().equals(".s")){
			ev.setCanceled(true);
			if(recorder == null){
				sendMessage(TextFormatting.RED+"No recording running!");
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
		if(ev.getMessage().equals(".f")){
			ev.setCanceled(true);
			if(recorder == null){
				sendMessage(TextFormatting.RED+"No recording running!");
				return;
			}
			sendMessage("§Aborting recording!");
			MinecraftForge.EVENT_BUS.unregister(recorder);
			mc.player.setPositionAndRotation(x,y,z,yaw,pitch);
			recorder = null;
			return;
		}
		if(ev.getMessage().startsWith(".p")){
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(args.length != 2){
				sendMessage(TextFormatting.RED+"Example: .p <filename>  (without .tas)");
				return;
			}
			if(Minecraft.getMinecraft().getIntegratedServer() != null && !loaded){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + args[1] + ".tas");
				if(file.exists()){
					loadData(file);
					mc.player.movementInput = new TASInput(this, keyFrames);
					sendMessage("Loaded File");
				}else{
					sendMessage(TextFormatting.RED+"File not found: " + file.getAbsolutePath());
				}
			}
		}
		if(ev.getMessage().startsWith(".tp")){
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(args.length != 2){
				sendMessage(TextFormatting.RED+"Wrong usage: Example: .tp <filename>");
				return;
			}
			if(Minecraft.getMinecraft().getIntegratedServer() != null && !loaded){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + args[1] + ".tas");
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
		if(ev.getMessage().startsWith(".fd")){
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if(args.length == 2){
				if(args[1].equalsIgnoreCase("info")&&FallDamage)sendMessage(TextFormatting.GREEN+"Fall Damage is enabled.");
				if(args[1].equalsIgnoreCase("info")&&FallDamage==false)sendMessage(TextFormatting.GREEN+"Fall Damage is currently"+ TextFormatting.RED+TextFormatting.BOLD+"disabled."+TextFormatting.RESET+TextFormatting.GREEN+" Taking Fall Damage has a chance to desyncs the TAS");
				return;
			}
			else if(args.length==1){
				if(FallDamage){
					FallDamage=false;
					sendMessage(TextFormatting.GREEN+"Fall Damage"+TextFormatting.RED+TextFormatting.BOLD+"disabled.");
				}
				else if(!FallDamage){
					FallDamage=true;
					sendMessage(TextFormatting.GREEN+"Fall Damage enabled.");
				}
			}
			else sendMessage(TextFormatting.RED+"Wrong usage! Either '.fd' or '.fd info'");
		}
		if(ev.getMessage().equals(".folder")){
			ev.setCanceled(true);
			try {
				Desktop.getDesktop().open(new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(ev.getMessage().startsWith(".help")){
			ev.setCanceled(true);
			String[] args = ev.getMessage().split(" ");
			if (args.length==1||args[1].equals("1")){
			sendMessage(TextFormatting.YELLOW+"This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in your minecraft world, which then can be played back."
					+ "\n"+TextFormatting.AQUA+"Mod by tr7zw and ScribbleLP");
			sendMessage(TextFormatting.GOLD+"Enter '.help 2' for commands");
			}
			else if (args.length==2&&args[1].equals("2")){
				sendMessage("§6Commands:\n"
						+ TextFormatting.YELLOW+".r"+TextFormatting.AQUA+" (Filename)"+TextFormatting.GREEN+" (Record) Starts a recording (Filename is optional)\n"
						+ TextFormatting.YELLOW+".s"+TextFormatting.GREEN+" (Stop) Stops the recording\n"
						+ TextFormatting.YELLOW+".f"+TextFormatting.GREEN+" (Fail) Aborts the recording and tp's you back where you started\n"
						+ TextFormatting.YELLOW+".p"+TextFormatting.AQUA+"<filename>"+TextFormatting.GREEN+" (Play) Plays back the recording, don't add a .tas to the filename\n"
						+ TextFormatting.YELLOW+".tp"+TextFormatting.AQUA+"<filename>"+TextFormatting.GREEN+ "(Teleport) Teleports you to the starting location. Can be found in the first line of the .tas file\n"
						+ TextFormatting.YELLOW+".fd"+TextFormatting.AQUA+"(info)"+TextFormatting.GREEN+"(FallDamage) Disables FallDamage, since taking damage has a chance of desyncing the TAS\n"
						+ TextFormatting.YELLOW+".folder"+TextFormatting.GREEN+"Opens the directory where the .tas files will be saved\n"
						+ TextFormatting.YELLOW+".help"+TextFormatting.AQUA+"<1,2>"+TextFormatting.GREEN+"Well guess what this does...");
			} else sendMessage(TextFormatting.RED+"Too many arguments... Did you mean '.help 2'?");
		}
	}
	@SubscribeEvent
	public void onCloseServer(PlayerEvent.PlayerLoggedOutEvent ev){ 		//When hitting save and quit, recording stops
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
	public void onOpenServer(PlayerEvent.PlayerLoggedInEvent ev){ 		//When joining the world, help plays
			sendMessage(TextFormatting.GREEN+"TASmod enabled, type in .help for more info");
	}
	//Cancel Fall Damage
	@SubscribeEvent
	public void onPlayerFalling(LivingFallEvent ev){
			ev.setCanceled(!FallDamage);
	}

}
