package de.tr7zw.tas;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import de.tr7zw.tas.commands.Failc;
import de.tr7zw.tas.commands.Playc;
import de.tr7zw.tas.commands.Recordc;
import de.tr7zw.tas.commands.Tasmodc;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;


@Mod(modid = "tasmod", name = "Tool Asisted Speedrun Mod", version = "1.9")

public class TASModLoader {

	@Instance
	public static TASModLoader instance = new TASModLoader();
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TASEvents());
		new File (Minecraft.getMinecraft().mcDataDir,"saves"+File.separator+"tasfiles").mkdir();
		
	}
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new Tasmodc());
		event.registerServerCommand(new Playc());
		event.registerServerCommand(new Recordc());
		event.registerServerCommand(new Failc());
		MinecraftForge.EVENT_BUS.register(new InfoGui());
	}
	
}
