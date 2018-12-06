package de.tr7zw.tas;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;

public class TASInput extends MovementInputFromOptions{

	private Minecraft mc = Minecraft.getMinecraft();
	private TAS tas;
	private ArrayList<KeyFrame> keyFrames;
	public static int step = 0;
	private Method leftClick;
	private Method rightClick;
	private KeyFrame frame;
	private int InvCont=1;
	public static boolean donePlaying = true;
	public static boolean breaking =true;

	public TASInput(TAS tas, ArrayList<KeyFrame> keyFrames) {
		super(Minecraft.getMinecraft().gameSettings);
		this.tas = tas;
		this.keyFrames = keyFrames;
	}
	@Override
	public void updatePlayerMoveState() {				//When done playing, the game will pause...
		if(step >= keyFrames.size()){
			if(!donePlaying){
				donePlaying=true;
				mc.thePlayer.motionX = 0;
				mc.thePlayer.motionY = 0;
				mc.thePlayer.motionZ = 0;
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
		
		
		KeyBinding.setKeyBindState(-100, frame.leftClick);			//Read Leftclick from File
		KeyBinding.setKeyBindState(-99, frame.rightClick);			//Read RightClick from File
		KeyBinding.setKeyBindState(29, frame.sprint);				//Read Sprint Key from File
		
		mc.thePlayer.inventory.currentItem=frame.slot;					//Read Inventory Slot from File etc...
		
		
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;

		if (frame.forwardKeyDown)
		{
			++this.moveForward;
			frame.forwardKeyDown = true;
		}
		else
		{
			frame.forwardKeyDown = false;
		}

		if (frame.backKeyDown)
		{
			--this.moveForward;
			frame.backKeyDown = true;
		}
		else
		{
			frame.backKeyDown = false;
		}

		if (frame.leftKeyDown)
		{
			++this.moveStrafe;
			frame.leftKeyDown = true;
		}
		else
		{
			frame.leftKeyDown = false;
		}

		if (frame.rightKeyDown)
		{
			--this.moveStrafe;
			frame.rightKeyDown = true;
		}
		else
		{
			frame.rightKeyDown = false;
		}

		this.jump = frame.jump;
		this.sneak = frame.sneak;

		if (this.sneak)
		{
			this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
			this.moveForward = (float)((double)this.moveForward * 0.3D);
		}
		mc.thePlayer.rotationPitch = frame.pitch;
		mc.thePlayer.rotationYaw = frame.yaw;
		
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
