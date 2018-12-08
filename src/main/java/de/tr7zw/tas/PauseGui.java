package de.tr7zw.tas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;

public class PauseGui extends GuiScreen{
	private Minecraft mc = Minecraft.getMinecraft();
	
	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(0, width/2-100, height/2-5, "Next Frame (While Playback)"));
		buttonList.add(new GuiButton(1, width/2-100, height/2+15, "Copy Pitch/Yaw to Clipboard"));
		buttonList.add(new GuiButton(2, width/2-100, height/2+35, "Done"));
		
	}
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case 0:
			TASInput.next=true;
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            break;
		case 1:
			setClipboardString(Float.toString(mc.thePlayer.rotationPitch)+";"+Float.toString(mc.thePlayer.rotationYaw));
			break;
		case 2:
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            break;
		}
	}
	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		drawDefaultBackground();
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
	}
	
}
