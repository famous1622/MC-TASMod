package de.tr7zw.tas;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "tasmod", name = "Tool Assisted Speedrun Mod", version = "1.7")
public class TASModLoader {

	@Instance
	public static TASModLoader instance = new TASModLoader();
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TAS());
		new File (Minecraft.getMinecraft().mcDataDir,"saves"+File.separator+"tasfiles").mkdir();
	}
	
}
