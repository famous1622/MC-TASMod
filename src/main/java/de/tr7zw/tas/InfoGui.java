package de.tr7zw.tas;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class InfoGui extends Gui{
	private Minecraft mc = Minecraft.getMinecraft();
	public static boolean enabled=true;

	public void copytoClipboard(String myString){
		StringSelection stringSelection = new StringSelection(myString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
	public static void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void drawStuff(RenderGameOverlayEvent.Post event){
		if (event.isCancelable() || event.type != ElementType.HOTBAR) {
			return;
		}
		int height=10;
		if (enabled){
			new Gui().drawString(mc.fontRenderer, (Math.round((mc.thePlayer.posX-0.5))+" "+Math.round((mc.thePlayer.posY-1.62))+" "+Math.round((mc.thePlayer.posZ-0.5))), 10, height+1, 0xFFFFFF);
			new Gui().drawString(mc.fontRenderer, "Pitch: "+Float.toString(mc.thePlayer.rotationPitch), 10, height+11, 0xFFFFFF);
			new Gui().drawString(mc.fontRenderer, "Yaw: "+Float.toString(mc.thePlayer.rotationYaw), 16, height+21, 0xFFFFFF);
			new Gui().drawString(mc.fontRenderer, Integer.toString(TASInput.step), 20, 315, 0xFFFFFF);
			
		}
	}

}
