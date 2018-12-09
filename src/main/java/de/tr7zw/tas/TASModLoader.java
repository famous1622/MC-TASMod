package de.tr7zw.tas;

import java.io.File;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@cpw.mods.fml.common.Mod(modid = "tasmod", name = "Tool Asisted Speedrun Mod", version = "1.4")
public class TASModLoader {

	@cpw.mods.fml.common.Mod.Instance
	public static TASModLoader instance = new TASModLoader();
	//Key Bindings
	public static net.minecraft.client.settings.KeyBinding PauseKey =new net.minecraft.client.settings.KeyBinding("Pause Playback", Keyboard.KEY_P, "TASmod");
	
	@cpw.mods.fml.common.Mod.EventHandler
	public void init(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TAS());
		new File (Minecraft.getMinecraft().mcDataDir,"saves"+File.separator+"tasfiles").mkdir();
		MinecraftForge.EVENT_BUS.register(new InfoGui());
		ClientRegistry.registerKeyBinding(PauseKey);
		FMLCommonHandler.instance().bus().register(new TASKeyBindings());
	}
	
}
