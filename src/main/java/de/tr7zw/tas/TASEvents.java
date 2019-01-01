package de.tr7zw.tas;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;

import de.tr7zw.tas.commands.Playc;
import de.tr7zw.tas.commands.Recordc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
	
	
	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
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
			Playback.donePlaying=true;
			return;
		}
	}
	
	@SubscribeEvent
	public void onMouseClick(TickEvent.RenderTickEvent ev){
		if (!Recorder.donerecording&&ev.phase == Phase.START){
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)){
				//set to pressed
				Recorder.clicklefty=1;
			}
			else if(!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)&&Recorder.clicklefty==1&&!Recorder.lkchecker){
				//set to quick press
				Recorder.clicklefty=2;
			}
			else if(!(Recorder.clicklefty==2)){
				Recorder.clicklefty=0;
			}
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)){
				//set to pressed
				Recorder.clickrighty=1;
			}
			else if(!GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)&&Recorder.clickrighty==1&&!Recorder.rkchecker){
				//set to quick press
				Recorder.clickrighty=2;
			}
			else if(!(Recorder.clickrighty==2)){
				Recorder.clickrighty=0;
			}
		}
		if (!Playback.donePlaying&&ev.phase == Phase.START&&mc.inGameHasFocus){
			if (!Playback.LKbreak&&Playback.leftclick<3){
				robLeftClick(Playback.leftclick);
				Playback.LKbreak=true;
			}
			if (!Playback.RKbreak&&Playback.leftclick<3){
				robRightClick(Playback.rightclick);
				Playback.RKbreak=true;
			}
		}
			
	}
	/*@SubscribeEvent
	public void resetClicksOnTick(TickEvent.PlayerTickEvent ev){
		if(ev.phase==Phase.START&&!Recorder.donerecording){
			Recorder.pitch=mc.player.rotationPitch;
			Recorder.yaw=mc.player.rotationYaw;
		}
	}*/
}

