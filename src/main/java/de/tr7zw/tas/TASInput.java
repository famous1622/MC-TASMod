package de.tr7zw.tas;

import java.awt.AWTException;
import java.awt.Robot;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.NoteBlockEvent;

public class TASInput implements PlaybackMethod {

	private Minecraft mc = Minecraft.getMinecraft();
	private TAS tas;
	private ArrayList<KeyFrame> keyFrames;
	public static int step = 0;
	private Method leftClick;
	private Method rightClick;
	private KeyFrame frame;
	private int InvCont=1;
	public static boolean donePlaying=true;
	public static boolean breaking=false;
	private int calcstate=0;
	

	public TASInput(TAS tas, ArrayList<KeyFrame> keyFrames) {
		this.tas = tas;
		this.keyFrames = keyFrames;
	}
	public Float recalcYaw(float Yaw){
		while(Yaw>=180)Yaw-=360;
		while(Yaw<-180)Yaw+=360;
		return Yaw;
		
	}
	private float uncalc(float yaw){
		if(recalcYaw(mc.player.rotationYaw)>=0&&(recalcYaw(mc.player.rotationYaw)-yaw)>180){
			calcstate++;
		}
		if(recalcYaw(mc.player.rotationYaw)<0&&(recalcYaw(mc.player.rotationYaw)-yaw)<-180){
			calcstate--;
		}
		return yaw+(360*calcstate);
	}

	@Override
	public void updatePlayerMoveState() {				//When done playing, the game will pause...
		if(step >= keyFrames.size()||donePlaying){
			if(!donePlaying){
				donePlaying = true;
				mc.player.motionX = 0;
				mc.player.motionY = 0;
				mc.player.motionZ = 0;
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreen() {
				});
			}
		}
		frame = keyFrames.get(step++);
		
		if (breaking){
			step=keyFrames.size();
		}
		
		mc.gameSettings.keyBindAttack.pressed = (frame.leftClick.equalsIgnoreCase("pLK")||frame.leftClick.equalsIgnoreCase("hLK"));
		mc.gameSettings.keyBindUseItem.pressed = (frame.rightClick.equalsIgnoreCase("pRK")||frame.rightClick.equalsIgnoreCase("hRK"));
		mc.gameSettings.keyBindForward.pressed = frame.forwardKeyDown;
		mc.gameSettings.keyBindBack.pressed = frame.backKeyDown;
		mc.gameSettings.keyBindLeft.pressed = frame.leftKeyDown;
		mc.gameSettings.keyBindRight.pressed = frame.rightKeyDown;
		mc.gameSettings.keyBindJump.pressed = frame.jump;
		mc.gameSettings.keyBindSneak.pressed = frame.sneak;
		mc.gameSettings.keyBindSprint.pressed = frame.sprint;				//Read Sprint Key from File
		mc.player.inventory.currentItem=frame.slot;					//Read Inventory Slot from File etc...
		mc.player.rotationPitch = frame.pitch;
		mc.player.rotationYaw = uncalc(frame.yaw);
		Playback.LKbreak=false;
		Playback.RKbreak=false;
	}

	/*public class LeftClickKeyBind extends KeyBinding{

		public LeftClickKeyBind(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
			super(p_i45001_1_, p_i45001_2_, p_i45001_3_);
		}

		@Override
		public boolean isKeyDown() {
			if(frame == null)return false;
			return frame.leftClick;
		}

		@Override
		public boolean isPressed() {
			if(frame == null)return false;
			return frame.leftClick;
		}

	}

	public class RightClickKeyBind extends KeyBinding{

		public RightClickKeyBind(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
			super(p_i45001_1_, p_i45001_2_, p_i45001_3_);
		}

		@Override
		public boolean isKeyDown() {
			if(frame == null)return false;
			return frame.rightClick;
		}

		@Override
		public boolean isPressed() {
			if(frame == null)return false;
			return frame.rightClick;
		}

	}*/
}
