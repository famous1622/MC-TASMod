package de.tr7zw.tas;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
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

	private Minecraft mc = Minecraft.getMinecraft();
	
	public Float recalcYaw(float Yaw){
		while(Yaw>=180)Yaw-=360;
		while(Yaw<-180)Yaw+=360;
		return Yaw;
		
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
			new Gui().drawCenteredString(mc.fontRenderer, (Math.round((mc.player.posX-0.5))+" "+Math.round((mc.player.posY-1.62))+" "+Math.round((mc.player.posZ-0.5))), 50, 10, 0xFFFFFF); 	//Coordinates
			new Gui().drawString(mc.fontRenderer, "Pitch: "+Float.toString(mc.player.rotationPitch), 16, 20, 0xFFFFFF);				//Show the current Pitch
			new Gui().drawString(mc.fontRenderer, "Yaw: "+Float.toString(recalcYaw(mc.player.rotationYaw)), 22, 30, 0xFFFFFF);		//Show the current Yaw (This comes from the modversion for 1.7.10 since 1.7 has just SOUTH as a yaw in F3)
			//Draw the Tickcounter. Changes the value depending if it's playback or a recording
			if (Recorder.recordstep==0){
				new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(TASInput.step+1), 30, height-24, 0xFFFFFF);
			}else if(TASInput.step==0){
				new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(Recorder.recordstep+1), 30, height-24, 0xFFFFFF);
			}
		}
	}

}
