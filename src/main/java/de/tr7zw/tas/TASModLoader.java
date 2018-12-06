package de.tr7zw.tas;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@cpw.mods.fml.common.Mod(modid = "tasmod", name = "Tool Asisted Speedrun Mod", version = "1.2")
public class TASModLoader {

	@cpw.mods.fml.common.Mod.Instance
	public static TASModLoader instance = new TASModLoader();
	
	@cpw.mods.fml.common.Mod.EventHandler
	public void init(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TAS());
		new File (Minecraft.getMinecraft().mcDataDir,"saves"+File.separator+"tasfiles").mkdir();
		MinecraftForge.EVENT_BUS.register(new InfoGui());
	}
	@cpw.mods.fml.common.Mod.EventHandler
	public void startup(FMLServerStartingEvent ev){
		
	}
	
}
