package de.tr7zw.tas.commands;

import net.minecraft.command.ICommand;

import java.util.ArrayList;
import java.util.List;

import de.tr7zw.tas.InfoGui;
import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Tasmodc extends CommandBase{

	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<String> emptyList(List<String> full){
		while(full.size()!=0){
			full.remove(0);
		}
		return full;
	}
	
	@Override
	public String getName() {
		return "tasmod";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/tasmod falldamage [info], folder, help [1,2]";
	}
    @Override
	public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length==0||args.length==1&&args[0].equals("help")
				||(args.length==1&&args[0].equals("1"))
				||(args.length==2&&(args[0].equals("help")&&args[1].equals("1")))){			//Output for '.help' /.help 1'
			sendMessage(TextFormatting.YELLOW+"This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in .minecraft/saves/tasfiles, which then can be played back."
					+ "\n"+TextFormatting.AQUA+"Mod by tr7zw and ScribbleLP");
			sendMessage(TextFormatting.GOLD+"Enter '/tasmod help 2' for commands");
			}
			else if (args.length==1&&args[0].equals("help2")
					||(args.length==1&&args[0].equals("2"))
					||(args.length==2&&(args[0].equals("help")&&args[1].equals("2")))){		//Output for '.help 2'
				sendMessage(TextFormatting.GOLD+"Commands:\n"
						+ TextFormatting.YELLOW+"/record,/rec,/r"+TextFormatting.AQUA+" (Filename)"+TextFormatting.GREEN+" -Starts a recording (Filename is optional)\n\n"
						+ TextFormatting.YELLOW+"/record (again)"+TextFormatting.GREEN+" -Stops the recording\n\n"
						+ TextFormatting.YELLOW+"/fail,/f"+TextFormatting.GREEN+" -Aborts the recording and tp's you back where you started\n\n"
						+ TextFormatting.YELLOW+"/play,/p"+TextFormatting.AQUA+" <filename>"+TextFormatting.GREEN+"  -Plays back the recording, don't add a .tas to the filename\n\n"
						+ TextFormatting.YELLOW+"/play (again)"+TextFormatting.GREEN+" -Aborts the TAS-playback\n\n"
						+ TextFormatting.YELLOW+"/tasmod falldamage"+TextFormatting.AQUA+" (info)"+TextFormatting.GREEN+" -Disables FallDamage, since taking damage has a chance of desyncing the TAS\n\n"
						+ TextFormatting.YELLOW+"/tasmod folder"+TextFormatting.GREEN+" -Opens the directory where the .tas files will be saved\n\n"
						+ TextFormatting.YELLOW+"/tasmod gui"+TextFormatting.GREEN+" Enables disables the gui");
			}
			else if (args.length==1&&args[0].equals("folder")){
				new TAS().openWorkFolder();
			}
			else if (args.length==1&&args[0].equals("falldamage")){
				if(!TASEvents.FallDamage){
					sendMessage(TextFormatting.GREEN+"Falldamage enabled");
					TASEvents.FallDamage=true;
				}
				else if(TASEvents.FallDamage){
					sendMessage(TextFormatting.GREEN+"Falldamage "+TextFormatting.RED+TextFormatting.BOLD+"disabled");
					TASEvents.FallDamage=false;
				}
			}
			else if (args.length==2&&args[0].equals("falldamage")&&args[1].equals("info")){
				if(TASEvents.FallDamage){
					sendMessage(TextFormatting.GREEN+"Fall Damage is enabled.");
				}
				else if(!TASEvents.FallDamage){
				sendMessage(TextFormatting.GREEN+"Fall Damage is currently"+ TextFormatting.RED+TextFormatting.BOLD+" disabled."+TextFormatting.RESET+TextFormatting.GREEN+" Taking Fall Damage has a chance to desync the TAS");
				}
			}
			else if (args.length==1&&args[0].equals("gui")){
				if (InfoGui.Infoenabled)InfoGui.Infoenabled=false;
				else if (!InfoGui.Infoenabled)InfoGui.Infoenabled=true;
			}
			else if (args.length==1&&args[0].equals("strokes")){
				if (InfoGui.Strokesenabled)InfoGui.Strokesenabled=false;
				else if (!InfoGui.Strokesenabled)InfoGui.Strokesenabled=true;
			}
		}
		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos targetPos) {
			List<String> tab = new ArrayList<String>();
				if(args.length==1){
					emptyList(tab);
					tab.add("help");
					tab.add("folder");
					tab.add("falldamage");
					tab.add("gui");
					tab.add("strokes");
				}
				else if(args.length==2){
					emptyList(tab);
					if(args[0].equals("help")){
						tab.add("2");
					}
					else if(args[0].equals("falldamage")){
						tab.add("info");
					}
				}
			return tab;
		}
}


