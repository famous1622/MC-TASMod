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

public class TASInput extends MovementInputFromOptions{

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
		super(Minecraft.getMinecraft().gameSettings);
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
	public void robLeftClick(int pressed){
		try {
			Robot rob=new Robot();
			rob.setAutoDelay(0);
			if (pressed==0&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
			}
			else if (pressed==1&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
			}
			else if (pressed==2&&mc.inGameHasFocus){
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
			}
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	public void robRightClick(int pressed){
		try {
			Robot rob=new Robot();
			rob.setAutoDelay(0);
			if (pressed==0&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			}
			else if (pressed==1&&mc.inGameHasFocus){
				rob.mousePress(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			}
			else if (pressed==2&&mc.inGameHasFocus){
				rob.mouseRelease(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updatePlayerMoveState() {				//When done playing, the game will pause...
		if(step >= keyFrames.size()){
			if(!donePlaying){
				donePlaying = true;
				mc.player.motionX = 0;
				mc.player.motionY = 0;
				mc.player.motionZ = 0;
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreen() {
				});
			}
			super.updatePlayerMoveState();
			return;
		}
		frame = keyFrames.get(step++);
		
		if (breaking){
			step=keyFrames.size();
		}
		/*if(!(mc.gameSettings.keyBindAttack instanceof LeftClickKeyBind)){
			try{
				mc.gameSettings.keyBindAttack = new LeftClickKeyBind("key.attack", -100, "key.categories.gameplay");
				mc.gameSettings.keyBindUseItem = new RightClickKeyBind("key.use", -99, "key.categories.gameplay");
			}catch(Exception ex){
				ex.printStackTrace(); 
			}
		}*/
		
		
		if(mc.gameSettings.keyBindAttack.getKeyCode()==-100){
			if(frame.leftClick.equalsIgnoreCase("pLK")){
			Playback.leftclick=1;
				robLeftClick(1);
			}
			if(frame.leftClick.equalsIgnoreCase("rLK")){
				Playback.leftclick=2;
				robLeftClick(2);
			}
			else{
				Playback.leftclick=3;
			}
		}else KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(),(frame.leftClick.equalsIgnoreCase("pLK")||frame.leftClick.equalsIgnoreCase("hLK")));
		
		if(mc.gameSettings.keyBindUseItem.getKeyCode()==-99){
			if(frame.rightClick.equalsIgnoreCase("pRK")){
				Playback.rightclick=1;
				robRightClick(1);
			}
			if(frame.rightClick.equalsIgnoreCase("rRK")){
				Playback.rightclick=2;
				robRightClick(2);
			}
			else{
				Playback.rightclick=3;
			}
		}else KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(),(frame.rightClick.equalsIgnoreCase("pRK")||frame.rightClick.equalsIgnoreCase("hRK")));
		
		KeyBinding.setKeyBindState(29, frame.sprint);				//Read Sprint Key from File
		mc.player.inventory.currentItem=frame.slot;					//Read Inventory Slot from File etc...
		
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;

		if (frame.forwardKeyDown)
		{
			++this.moveForward;
			this.forwardKeyDown = true;
		}
		else
		{
			this.forwardKeyDown = false;
		}

		if (frame.backKeyDown)
		{
			--this.moveForward;
			this.backKeyDown = true;
		}
		else
		{
			this.backKeyDown = false;
		}

		if (frame.leftKeyDown)
		{
			++this.moveStrafe;
			this.leftKeyDown = true;
		}
		else
		{
			this.leftKeyDown = false;
		}

		if (frame.rightKeyDown)
		{
			--this.moveStrafe;
			this.rightKeyDown = true;
		}
		else
		{
			this.rightKeyDown = false;
		}

		this.jump = frame.jump;
		this.sneak = frame.sneak;

		if (this.sneak)
		{
			this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
			this.moveForward = (float)((double)this.moveForward * 0.3D);
		}
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
