package de.tr7zw.tas.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASInput;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class Playc extends CommandBase{

	private	 List<String> tab = new ArrayList<String>();
	private Minecraft mc = Minecraft.getMinecraft();
	
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
		if (TASInput.donePlaying){
			if (args.length==0)sendMessage(ChatFormatting.RED+"/play <filename> (without .tas)");
			if (args.length==1){
				new TAS().playTAS(args);
			}
			if (args.length>1)sendMessage(ChatFormatting.RED+"Too many arguments");
		}
		else if(!TASInput.donePlaying){
			new TAS().abortTAS();
		}
	}
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos targetPos) {
		if(args.length < 1) return null;
		if(args.length==1){
			tab=getFilenames();
		}
		if (tab.isEmpty()){
			sendMessage(ChatFormatting.RED+"No files in directory");
		}
		return tab;
		 
	}
}