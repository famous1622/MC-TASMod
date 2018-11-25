package de.tr7zw.tas;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@cpw.mods.fml.common.Mod(modid = "tasmod", name = "Tool Asisted Speedrun Mod", version = "1.3")
public class TASModLoader {

	@cpw.mods.fml.common.Mod.Instance
	public static TASModLoader instance = new TASModLoader();
	
	@cpw.mods.fml.common.Mod.EventHandler
	public void init(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TAS());
	}
	
}
