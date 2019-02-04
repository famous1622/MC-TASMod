package de.tr7zw.tas;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.gameevent.InputEvent;
/**
 * Newer version of TASInput.java<br>
 * Executes the input directly after reading the line in the file
 * @author ScribbleLP
 *
 */
public class Playback extends MovementInputFromOptions{

	private Minecraft mc = Minecraft.getMinecraft();
	//Variables for the Playback
	private boolean forward;
	private boolean backward;
	private boolean left;
	private boolean right;
	private boolean Jump;
	private boolean Sneak;
	private boolean sprint;
	public static int leftclick=3;
	public static int rightclick=3;
	private float pitch;
	private float yaw;
	private int hotbarslot;
	public static boolean LKbreak;
	public static boolean RKbreak;
	//Used for calculating the yaw
	private int calcstate=0;
	//The arguments from entering the command in Playc
	private String[] args;
	/**
	 * Current frame of the Playback
	 */
	public static int frame=0;
	/**
	 * Variable to see if a playback is currently running.<br>
	 * If true, the playback stopped
	 */
	public static boolean donePlaying=true;
	
	public Playback(String[] Helloargs) {
		super(Minecraft.getMinecraft().gameSettings);
		args=Helloargs;
	}
	//TODO Remake the integers... 0,1,2,3 is stupid to memorize
	
	public void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	//TODO
	public void moveMouse(float Pitch, float Yaw){
		int fakepitch=(int) Pitch;
		int fakeyaw=(int) Yaw;
		fakepitch=fakepitch-(int)mc.player.cameraPitch;
		fakeyaw=fakeyaw-(int)mc.player.cameraYaw;
		try {
			Robot rob= new Robot();
			rob.setAutoDelay(0);
			rob.mouseMove(MouseInfo.getPointerInfo().getLocation().y+fakeyaw, MouseInfo.getPointerInfo().getLocation().x+fakepitch);
		} catch (AWTException e) {
			e.printStackTrace();
		}
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
	
	public void readingFile(String[] args, int stopAt){
		File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
				"tasfiles"+ File.separator + args[0] + ".tas");
		try{
			BufferedReader Buff = new BufferedReader(new FileReader(file));
			String s;
			int line=0;
			while (true){
				if((s=Buff.readLine()).equalsIgnoreCase("END")){
					break;
				}
				else if(s.startsWith("#")||s.startsWith("/")){
					continue;
				}
				else if(line==stopAt){
					handleData(s.split(";"));
					Buff.close();
					return;
				}
				line++;
			}
			sendMessage("Finished Playback");
			leftclick=2;
			rightclick=2;
			donePlaying=true;
			Buff.close();
			return;
		} catch (NullPointerException ex){
				sendMessage(TextFormatting.RED+"Error while reading the file. Couldn't find an END");
				donePlaying=true;
				ex.printStackTrace();
				
			
		} catch (IOException e) {
			donePlaying=true;
			e.printStackTrace();
		}
	}

	private void handleData(String[] field) {
		forward=field[1].equalsIgnoreCase("W");
		backward=field[2].equalsIgnoreCase("S");
		left=field[3].equalsIgnoreCase("A");
		right=field[4].equalsIgnoreCase("D");
		Jump=field[5].equalsIgnoreCase("Space");
		Sneak=field[6].equalsIgnoreCase("Shift");
		sprint=field[7].equalsIgnoreCase("Ctrl");
		pitch=Float.parseFloat(field[8]);
		yaw=Float.parseFloat(field[9]);
		if(field[10].equalsIgnoreCase("LK"))leftclick=0;
		else if(field[10].equalsIgnoreCase("pLK"))leftclick=1;
		else if(field[10].equalsIgnoreCase("rLK"))leftclick=2;
		else leftclick=3;
		if(field[11].equalsIgnoreCase("RK"))rightclick=0;
		else if(field[11].equalsIgnoreCase("pRK"))rightclick=1;
		else if(field[11].equalsIgnoreCase("rRK"))rightclick=2;
		else rightclick=3;
		hotbarslot=Integer.parseInt(field[12]);
	}
	

	@Override
	public void updatePlayerMoveState() {
		if(!donePlaying){
			readingFile(args, frame++);
		}
		if (donePlaying){
			super.updatePlayerMoveState();
			return;
		}
		if(mc.gameSettings.keyBindAttack.getKeyCode()==-100){
			if(leftclick<3){
				robLeftClick(leftclick);
			}
		}else KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(),leftclick<3);
		
		if(mc.gameSettings.keyBindUseItem.getKeyCode()==-99){
			if(rightclick<3){
				robRightClick(rightclick);
			}
		}else KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(),rightclick<3);
		//KeyBinding.setKeyBindState(-100, leftclick);			//Read Leftclick from File
		//KeyBinding.setKeyBindState(-99, rightclick);			//Read RightClick from File
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), sprint);					//Read Sprint Key from File
		mc.player.inventory.currentItem=hotbarslot;				//Read Inventory Slot from File etc...
		
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;

		if (forward)
		{
		++this.moveForward;
		this.forwardKeyDown = true;
		}
		else
		{
			this.forwardKeyDown = false;
		}

		if (backward)
		{
			--this.moveForward;
			this.backKeyDown = true;
		}
		else
		{
			this.backKeyDown = false;
		}

		if (left)
		{
			++this.moveStrafe;
			this.leftKeyDown = true;
		}
		else
		{
			this.leftKeyDown = false;
		}
	
		if (right)
		{
		--this.moveStrafe;
		this.rightKeyDown = true;
		}
		else
		{
			this.rightKeyDown = false;
		}
		this.jump=Jump;
		this.sneak=Sneak;
	
		if (this.sneak)
		{
			this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
			this.moveForward = (float)((double)this.moveForward * 0.3D);
		}
		mc.player.rotationPitch = pitch;
		mc.player.rotationYaw = uncalc(yaw);
		

	}
}


