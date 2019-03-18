package de.tr7zw.tas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import scala.reflect.internal.Types.RecoverableCyclicReference;

public class Recorder {

	private ArrayList<Object> recording = new ArrayList<>();
	private Minecraft mc = Minecraft.getMinecraft();
	public static int recordstep=0;
	/**
	 * Variable to see if a recording is currently running.<br>
	 * If true, the recording is stopped
	 */
	public static boolean donerecording=true;
	public static boolean lkchecker=false;
	public static boolean rkchecker=false;
	private int clicklefty=0;
	private int clickrighty=0;
	private boolean needsunpressLK=false;
	private boolean needsunpressRK=false;
	private float tickpitch;
	private float tickyaw;

	
	private static void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Recorder() {
		recording.add("#StartLocation: " + mc.player.getPositionVector().toString());
		needsunpressLK=false;
		needsunpressRK=false;
	}
	
	public static Float recalcYaw(float Yaw){
		while(Yaw>=180)Yaw-=360;
		while(Yaw<-180)Yaw+=360;
		return Yaw;
	}
	@SubscribeEvent
	public void onClientTickSTART(TickEvent.ClientTickEvent ev) {
		if(ev.phase==Phase.START&&!donerecording) {
			
		}
	}
	@SubscribeEvent
	public void onClientTickEND(TickEvent.ClientTickEvent ev) {
		if(ev.phase==Phase.START&&!donerecording) {
			tickpitch=mc.player.prevRotationPitch;
			tickyaw=recalcYaw(mc.player.rotationYaw);
			GameSettings gameset=mc.gameSettings;
			String leftclack=" ";
			String rightclack=" ";
			
			if(clicklefty==2){
				leftclack="pLK";
				needsunpressLK=true;
			}
			else if(clicklefty==1&&!lkchecker){
				leftclack="pLK";
				needsunpressLK=true;
			}
			else if(clicklefty==1&&lkchecker){
				leftclack="hLK";
				needsunpressLK=true;
			}
			else if(needsunpressLK){
				leftclack="rLK";
				needsunpressLK=false;
				
			}
			
			
			if(clickrighty==2){
				rightclack="pRK";
				needsunpressRK=true;
			}
			else if(clickrighty==1&&!rkchecker){
				rightclack="pRK";
				needsunpressRK=true;
			}
			else if(clickrighty==1&&rkchecker){
				rightclack="hRK";
				needsunpressRK=true;
			}
			else if(needsunpressRK){
				rightclack="rRK";
				needsunpressRK=false;
			}

			
			//Read from the player movement
			
			recording.add(new KeyFrame(gameset.keyBindForward.isKeyDown(), gameset.keyBindBack.isKeyDown(), gameset.keyBindLeft.isKeyDown(), gameset.keyBindRight.isKeyDown(), gameset.keyBindJump.isKeyDown(), gameset.keyBindSneak.isKeyDown(), GameSettings.isKeyDown(mc.gameSettings.keyBindSprint),
					tickpitch, tickyaw, leftclack, rightclack,mc.player.inventory.currentItem));
			if (!donerecording)recordstep++;

			if (clicklefty==1){
				lkchecker=true;
			}else lkchecker=false;
			
			if (clickrighty==1){
				rkchecker=true;
			}else rkchecker=false;
			clicklefty=0;
			clickrighty=0;
		}
	}
	@SubscribeEvent
	public void onMouseClick(TickEvent.RenderTickEvent ev){		//Complicated handler to check if the mouse is pressed or held...
		if (!donerecording&&ev.phase == Phase.START){
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)){
				//set to pressed
				clicklefty=1;
			}
			else if(!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)&&clicklefty==1&&!lkchecker){
				//set to quick press
				clicklefty=2;
			}
			else if(!(clicklefty==2)){
				clicklefty=0;
			}
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)){
				//set to pressed
				clickrighty=1;
			}
			else if(!GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)&&clickrighty==1&&!rkchecker){
				//set to quick press
				clickrighty=2;
			}
			else if(!(clickrighty==2)){
				clickrighty=0;
			}
		}
	}
	public void saveData(File file){
		mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
		StringBuilder output = new StringBuilder();
		String W;											//Well this is... Not the best solution for this buut hey it works I guess... I will definetely fix this once I know what I'm doing but for now it helps me visualize
		String S;
		String A;
		String D;
		String Space;
		String Shift;
		String Ctrl;
		String LK;
		String RK;
		Object buff1=recording.get(1);
		Object buff2=recording.get(2);
		for(int i = 0; i < recording.size(); i++){
			Object o = recording.get(i);
			if(o instanceof String){
				output.append(o + "\n");
			}else if(o instanceof KeyFrame){
				KeyFrame frame = (KeyFrame) o;
				/* This here was a wierd way to delay certain inputs, to test if this syncs or desyncs the TAS
				KeyFrame buff1frame= (KeyFrame) buff1;
				KeyFrame buff2frame= (KeyFrame) buff2;
				buff1frame.leftClick=frame.leftClick;
				frame.leftClick=buff2frame.leftClick;
				buff2frame.leftClick=buff1frame.leftClick;
					
				buff1frame.rightClick=frame.rightClick;
				frame.rightClick=buff2frame.rightClick;
				buff2frame.rightClick=buff1frame.rightClick;
				*/
				if (frame.forwardKeyDown==true)W="W";else W=" ";
				if(frame.backKeyDown==true)S="S";else S=" ";
				if(frame.leftKeyDown==true)A="A";else A=" ";
				if(frame.rightKeyDown==true)D="D";else D=" ";
				if(frame.jump==true)Space="Space";else Space=" ";
				if(frame.sneak==true)Shift="Shift";else Shift=" ";
				if(frame.sprint==true)Ctrl="Ctrl";else Ctrl=" ";
				//if(frame.leftClick==true)LK="LK";else LK=" ";
				//if(frame.rightClick==true)RK="RK";else RK=" ";
				
				//Writing to the file
				
				output.append("1;" + W + ";" + S + ";" + A + ";" + D + ";"
						+ Space + ";" + Shift + ";" + Ctrl + ";" + frame.pitch + ";" + frame.yaw + ";" + frame.leftClick + ";" + frame.rightClick
						+ ";" + Integer.toString(frame.slot) +";\n");
			}
		}
		output.append("END");
		try {
			Files.write(output.toString().getBytes(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Saved to: " + file.getAbsolutePath()));
		}catch(Exception exX){
			exX.printStackTrace();
		}
	}
}
