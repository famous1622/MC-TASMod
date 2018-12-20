package de.tr7zw.tas.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

import de.tr7zw.tas.Playback;
import de.tr7zw.tas.TAS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Playc extends CommandBase{

	private	 List<String> tab = new ArrayList<String>();
	private Minecraft mc = Minecraft.getMinecraft();

	public List<String> emptyList(List<String> full){
		while(full.size()!=0){
			full.remove(0);
		}
		return full;
	}
	
	public List<String> getFilenames(){
		List<String> tab = new ArrayList<String>();
		File folder = new File(Minecraft.getMinecraft().mcDataDir,"saves"+File.separator+"tasfiles");
		File[] listOfFiles = folder.listFiles();
		for(int i = 0;i<listOfFiles.length;i++){
			tab.add(listOfFiles[i].getName().replaceAll("\\.tas", ""));
		}
		return tab;
	}
	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@Override
	public String getName() {
		return "play";
	}
	@Override
	public String getUsage(ICommandSender sender) {
		return "/play <filename> (without .tas)";
	}
	
	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public List<String> getAliases() {
		return ImmutableList.of("p");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
				"tasfiles"+ File.separator + args[0] + ".tas");
		
		if (Playback.donePlaying){
			if (args.length==0)sendMessage(ChatFormatting.RED+"/play <filename> (without .tas)");
			
			if (file.exists()){
				if (args.length==1){
					Playback.donePlaying=false;
					new TAS().teleportToTAS(args);
					new TAS().playTAS(args);
				}
			
				else if(args.length==2&&args[1].equals("notp")){
					Playback.donePlaying=false;
					new TAS().playTAS(args);
				}
			}
			else{
				sendMessage(TextFormatting.RED+"File '"+args[0]+"' does not exist");
			}
			if (args.length>2)sendMessage(ChatFormatting.RED+"Too many arguments");
		}
		//Abort Playback
		else if(!Playback.donePlaying){
			Playback.donePlaying=true;
			KeyBinding.setKeyBindState(-100, false);
			KeyBinding.setKeyBindState(-99, false);
			KeyBinding.setKeyBindState(29, false);
		}
	}
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos targetPos) {
		if(args.length==1){
			emptyList(tab);
			tab.addAll(getFilenames());
			if (tab.isEmpty()){
				sendMessage(ChatFormatting.RED+"No files in directory");
			}
		}
		else if (args.length==2){
			emptyList(tab);
			tab.add("notp");
			
		}
		return tab;
		 
	}
}
