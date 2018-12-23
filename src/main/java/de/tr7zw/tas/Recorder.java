package de.tr7zw.tas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import scala.reflect.internal.Types.RecoverableCyclicReference;

public class Recorder {

	private ArrayList<Object> recording = new ArrayList<>();
	private Minecraft mc = Minecraft.getMinecraft();
	public static int recordstep=0;
	public static boolean donerecording=true;
	private static boolean lkchecker=false;
	private static boolean rkchecker=false;
	
	public Recorder() {
		recording.add("#StartLocation: " + mc.player.getPositionVector().toString());
		mc.player.movementInput = new RecordingInput(mc.gameSettings, recording);
	}
	
	public static Float recalcYaw(float Yaw){
		while(Yaw>=180)Yaw-=360;
		while(Yaw<-180)Yaw+=360;
		return Yaw;
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
		for(int i = 0; i < recording.size(); i++){
			Object o = recording.get(i);
			if(o instanceof String){
				output.append(o + "\n");
			}else if(o instanceof KeyFrame){
				KeyFrame frame = (KeyFrame) o;
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
	
	private static class RecordingInput extends MovementInputFromOptions{

		ArrayList<Object> recording;
		Minecraft mc = Minecraft.getMinecraft();
		
		public RecordingInput(GameSettings p_i1237_1_, ArrayList<Object> recording) {
			super(p_i1237_1_);
			this.recording = recording;
		}

		@Override
		public void updatePlayerMoveState() {
			super.updatePlayerMoveState();
			MovementInput input = this;
			String leftclack=" ";
			String rightclack=" ";
			boolean lefty=GameSettings.isKeyDown(mc.gameSettings.keyBindAttack);
			boolean righty=GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem);
			if (!lkchecker&&!lefty){
				leftclack=" ";
			}
			else if (!lkchecker&&lefty){
				leftclack="pLK";
			}
			else if(lkchecker&&lefty){
				leftclack="hLK";
			}
			else if(lkchecker&&!lefty){
				leftclack="rLK";
			}
			
			
			if (!rkchecker&&!righty){
				rightclack=" ";
			}
			else if (!rkchecker&&righty){
				rightclack="pRK";
			}
			else if(rkchecker&&righty){
				rightclack="hRK";
			}
			else if(rkchecker&&!righty){
				rightclack="rRK";
			}
			//Read from the player movement
			
			recording.add(new KeyFrame(input.forwardKeyDown, input.backKeyDown, input.leftKeyDown, input.rightKeyDown, input.jump, input.sneak, GameSettings.isKeyDown(mc.gameSettings.keyBindSprint),
					mc.player.rotationPitch, recalcYaw(mc.player.rotationYaw), leftclack,
					rightclack ,mc.player.inventory.currentItem));
			if (!donerecording)recordstep++;
			rkchecker=righty;
			lkchecker=lefty;
		}
		
	}
	
}
