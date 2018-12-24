package de.tr7zw.tas;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;

import de.tr7zw.tas.commands.Recordc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

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
	public static int LKpressed=3;
	public static int RKpressed=3;
	private boolean contLK=false;
	private boolean contRK=false;
	
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
	/**
	 * I realised rightclick and leftclick was delayed by 1 tick... this is my attempt to solve the issue
	 * It basically executes the end of the tick and stops at the start of the next tick... I don't even know myself how this works...
	 * it sounded good in my head, but then...
	 * @param ev
	 */
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent ev){
		if (!Playback.donePlaying){
			if (ev.phase ==Phase.END){
				if(LKpressed<3&&LKpressed>=0){
					if(LKpressed==1)contLK=true;
					robLeftClick(LKpressed);
				}
				if(RKpressed<3&&RKpressed>=0){
					if (RKpressed==1)contRK=true;
					robRightClick(RKpressed);
				}
			}
			if(ev.phase==Phase.START){
				if(LKpressed==1&&contLK){
					LKpressed=3;
					contLK=false;
				}
				if(RKpressed==1&&contRK){
					RKpressed=3;
					contRK=false;
				}
			}
		}
	}
	
	public void robLeftClick(int pressed){
		try {
			Robot rob=new Robot();
			rob.setAutoDelay(0);
			if (pressed==0&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
			}
			else if (pressed==1&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
			}
			else if (pressed==2&&mc.inGameHasFocus){
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
			}
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	public void robRightClick(int pressed){
		try {
			Robot rob=new Robot();
			rob.setAutoDelay(0);
			if (pressed==0&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			}
			else if (pressed==1&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			}
			else if (pressed==2&&mc.inGameHasFocus){
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}

