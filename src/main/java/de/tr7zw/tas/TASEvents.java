package de.tr7zw.tas;

import java.io.File;

import de.tr7zw.tas.commands.Recordc;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**Events for the TASmod
 * @author ScribbleLP
 */
public class TASEvents {
	private Minecraft mc = Minecraft.getMinecraft();
	/**
	 * Variable to enable fall damage<br>
	 * If true, fall damage will be enabled
	 */
	public static boolean FallDamage;
	public static boolean StopRecOnWorldClose;
	
	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	//Cancel Fall Damage
	@SubscribeEvent
	public void onPlayerFalling(LivingFallEvent ev){
			ev.setCanceled(!FallDamage);
	}
	
	//Message when joining the server
	@SubscribeEvent
	public void onOpenServer(PlayerEvent.PlayerLoggedInEvent ev){
			sendMessage("TASmod enabled, type in /tasmod for more info");
	}
	//When hitting save and quit, the recording (with /record) stops
	@SubscribeEvent
	public void onCloseServer(PlayerEvent.PlayerLoggedOutEvent ev){ 		
		if(!Recorder.donerecording&&StopRecOnWorldClose){
			Recordc.recorder.stopRecording();
			return;
		}
	}
}

