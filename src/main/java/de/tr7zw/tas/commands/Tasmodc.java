package de.tr7zw.tas.commands;

import net.minecraft.command.ICommand;

import java.util.ArrayList;
import java.util.List;

import de.tr7zw.tas.TAS;
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
	
	@Override
	public String getName() {
		return "tasmod";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/tasmod [1,2]";
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
			sendMessage(TextFormatting.YELLOW+"This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in your minecraft world, which then can be played back."
					+ "\n"+TextFormatting.AQUA+"Mod by tr7zw and ScribbleLP");
			sendMessage(TextFormatting.GOLD+"Enter '/tasmod help 2' for commands");
			}
			else if (args.length==1&&args[0].equals("help2")
					||(args.length==1&&args[0].equals("2"))
					||(args.length==2&&(args[0].equals("help")&&args[1].equals("2")))){		//Output for '.help 2'
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
			}
			else if (args.length==1&&args[0].equals("folder")){
				new TAS().openWorkFolder();
			}
			else if (args.length==1&&args[0].equals("falldamage")){
				new TAS().controlFallDamage(args);
			}
			else if (args.length==2&&args[0].equals("falldamage")&&args[1].equals("info")){
				new TAS().controlFallDamage(args);
			}
		}
		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos targetPos) {
			List<String> tab = new ArrayList<String>();
				if(args.length==1){
					tab.add("help");
					tab.add("folder");
					tab.add("falldamage");
				}
				else if(args.length==2){
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


