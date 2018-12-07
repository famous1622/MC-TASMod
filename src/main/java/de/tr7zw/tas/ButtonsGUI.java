package de.tr7zw.tas;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.GuiOpenEvent;

public class ButtonsGUI extends GuiScreen{
	private Minecraft mc = Minecraft.getMinecraft();
	
	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void initButtons(GuiScreen event){
		if (mc.currentScreen instanceof GuiIngameMenu){
			new GuiButtonExt(1, 20, 60, "Copy Pitch/Yaw");
			sendMessage("hi");
		}
	}
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case 1:
			setClipboardString(Float.toString(mc.thePlayer.rotationPitch)+";"+Float.toString(mc.thePlayer.rotationYaw));
			break;
		}
	}

}
