package de.tr7zw.tas;

import java.awt.MouseInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Gui-Overlay with useful information
 * @author ScribbleLP
 *
 */
public class InfoGui extends Gui{
	private int line = 0;
	public static boolean enabled;
	private static String[] arguments=null;

	private Minecraft mc = Minecraft.getMinecraft();
	private String[] Buttons=null;
	
	public Float recalcYaw(float Yaw){
		while(Yaw>=180)Yaw-=360;
		while(Yaw<-180)Yaw+=360;
		return Yaw;
	}
	
	public void setArguments(String[] args){
			arguments=args;
	}
	
	public void readingFile(String[] args, int stopAt){
		File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
				"tasfiles"+ File.separator + args[0] + ".tas");
		try{
			BufferedReader Buff = new BufferedReader(new FileReader(file));
			String s;
			int line=0;
			while (true){
				if((s=Buff.readLine()).equalsIgnoreCase("END")||Playback.donePlaying){
					break;
				}
				else if(s.startsWith("#")||s.startsWith("/")){
					continue;
				}
				else if(line==stopAt){
					Buttons=s.split(";");
					Buff.close();
					return;
				}
				line++;
			}
			Buff.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void drawStuff(RenderGameOverlayEvent.Post event){
		if (event.isCancelable() || event.getType() != ElementType.HOTBAR) {
			return;
		}
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		if (enabled&&!(mc.gameSettings.showDebugInfo)){
			new Gui().drawCenteredString(mc.fontRenderer, (mc.player.posX-0.5)+" "+Math.round((mc.player.posY))+" "+(mc.player.posZ-0.5), 130, 10, 0xFFFFFF); 	//Coordinates
			new Gui().drawString(mc.fontRenderer, "Pitch: "+Float.toString(mc.player.rotationPitch), 16, 20, 0xFFFFFF);				//Show the current Pitch
			new Gui().drawString(mc.fontRenderer, "Yaw: "+Float.toString(recalcYaw(mc.player.rotationYaw)), 22, 30, 0xFFFFFF);		//Show the current Yaw (This comes from the modversion for 1.7.10 since 1.7 has just SOUTH as a yaw in F3)
			
			if(mc.inGameHasFocus)new Gui().drawString(mc.fontRenderer, Integer.toString(MouseInfo.getPointerInfo().getLocation().x)+" "+Integer.toString(MouseInfo.getPointerInfo().getLocation().y), 22, 40, 0xFFFFFF);
			drawKeyStrokes(height, width);
			//Draw the Tickcounter. Changes the value depending if it's playback or a recording
			if (Recorder.recordstep==0&&TASInput.step==0){
				new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(Playback.frame+1), 30, height-24, 0xFFFFFF);
			}else if(Playback.frame==0&&TASInput.step==0){
				new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(Recorder.recordstep+1), 30, height-24, 0xFFFFFF);
			}else if(Recorder.recordstep==0&&Playback.frame==0){
				new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(TASInput.step+1), 30, height-24, 0xFFFFFF);
			}
		}
	}
	public void drawKeyStrokes(int height, int width){
		if (!Playback.donePlaying){
		
			readingFile(arguments, Playback.frame);
			if(!Buttons[1].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[1], 3, height-13, 0xFFFFFF);
			}
			if(!Buttons[2].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[2], 11, height-13, 0xFFFFFF);
			}
			if(!Buttons[3].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[3], 19, height-13, 0xFFFFFF);
			}
			if(!Buttons[4].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[4], 27, height-13, 0xFFFFFF);
			}
			if(!Buttons[5].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[5], 35, height-13, 0xFFFFFF);
			}
			if(!Buttons[6].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[6], 67, height-13, 0xFFFFFF);
			}
			if(!Buttons[7].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[7], 92, height-13, 0xFFFFFF);
			}
			if(!Buttons[10].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[10], 112, height-13, 0xFFFFFF);
				new Gui().drawString(mc.fontRenderer, Integer.toString(Playback.leftclick), 112, height-23, 0xFFFFFF);

			}
			if(!Buttons[11].equalsIgnoreCase(" ")){
				new Gui().drawString(mc.fontRenderer, Buttons[11], 127, height-13, 0xFFFFFF);
				new Gui().drawString(mc.fontRenderer, Integer.toString(Playback.rightclick), 127, height-23, 0xFFFFFF);
			}
		}
		else if (Playback.donePlaying){
			if(mc.gameSettings.keyBindForward.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "W", 3, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindBack.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "S", 11, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindLeft.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "A", 19, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindRight.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "D", 27, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindJump.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "Space", 35, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindSneak.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "Shift", 67, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindSprint.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "Ctrl", 92, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindAttack.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "LK", 112, height-13, 0xFFFFFF);
			}
			if(mc.gameSettings.keyBindUseItem.isKeyDown()){
				new Gui().drawString(mc.fontRenderer, "RK", 127, height-13, 0xFFFFFF);
			}
		}
	}

}
