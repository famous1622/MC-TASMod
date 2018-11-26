package de.tr7zw.tas;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TASGui extends Gui{
	private int line = 0;
	private ArrayList<KeyFrame> keyFrames = new ArrayList<>();

	private Minecraft mc = Minecraft.getMinecraft();
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent ev){
		new Gui().drawString(mc.fontRenderer, Integer.toString(TASInput.step), 10, 314, 0xFFFFFF);
	}

	
}
