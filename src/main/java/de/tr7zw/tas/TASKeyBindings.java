package de.tr7zw.tas;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

public class TASKeyBindings {
	private Minecraft mc = Minecraft.getMinecraft();
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent event) {
		if (TASModLoader.PauseKey.isPressed()){
			mc.displayGuiScreen(new PauseGui());
		}
	}
}