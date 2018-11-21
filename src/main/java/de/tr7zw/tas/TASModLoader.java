package de.tr7zw.tas;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "tasmod", name = "Tool Asisted Speedrun Mod", version = "1.6")
public class TASModLoader {

	@Instance
	public static TASModLoader instance = new TASModLoader();
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TAS());
	}
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new Commands());
	}
	
}
