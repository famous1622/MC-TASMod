package de.tr7zw.tas;

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
	private int step = 0;
	private Method leftClick;
	private Method rightClick;
	private KeyFrame frame;
	private int InvCont=1;
	public static boolean donePlaying = false;

	public TASInput(TAS tas, ArrayList<KeyFrame> keyFrames) {
		super(Minecraft.getMinecraft().gameSettings);
		this.tas = tas;
		this.keyFrames = keyFrames;
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
		mc.player.inventory.currentItem=frame.slot;					//Read Inventory Slot from File etc...
		
		InvCont=1;
		
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
		mc.player.rotationYaw = frame.yaw;
		
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
