package de.tr7zw.tas.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

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

public class Recordc extends CommandBase{
	
	private	 List<String> tab = new ArrayList<String>();
	private Minecraft mc = Minecraft.getMinecraft();
	public static TAS recorder= new TAS();
	private boolean check=false;
	
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
		return "record";
	}
	@Override
	public String getUsage(ICommandSender sender) {
		return "/record or /r or /rec [filename] (without .tas. If not set, it generates a filename)";
	}
	
	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public List<String> getAliases() {
		return ImmutableList.of("r","rec");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (Recorder.donerecording){
			if (args.length==0){
				sendMessage("No filename set! Generating one...");
				recorder.startRecord();
			}
			if (args.length==1){
				recorder.startRecord(args);
			}
			if (args.length>1)sendMessage(ChatFormatting.RED+"Too many arguments");
		}
		else if(!Recorder.donerecording){
			recorder.stopRecording();;
		}
	}
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos targetPos) {
		if(args.length==1){
			if (!check){
				sendMessage(TextFormatting.BOLD+""+TextFormatting.RED+"WARNING!"+TextFormatting.RESET+TextFormatting.RED+
						" Existing Filenames will be overwritten! /fail to abort the recording");
				check=true;
			}
			tab=getFilenames();
		}
		if (tab.isEmpty()){
			sendMessage(ChatFormatting.RED+"No files in directory");
		}
		return tab;
		 
	}
}

