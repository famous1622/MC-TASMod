package de.tr7zw.tas;

import net.minecraft.inventory.Container;

public class KeyFrame {

	public boolean forwardKeyDown;		//Where all the Keys are saved!
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneak;
	public float pitch;
	public float yaw;
	public boolean leftClick;
	public boolean rightClick;
	public boolean sprint;
    public boolean drop;
    public int slot;
    
	public KeyFrame(boolean forwardKeyDown, boolean backKeyDown, boolean leftKeyDown, boolean rightKeyDown,
			boolean jump, boolean sneak, boolean sprint, float pitch, float yaw, boolean leftClick, boolean rightClick, int slot) {
		super();
		this.forwardKeyDown = forwardKeyDown;
		this.backKeyDown = backKeyDown;
		this.leftKeyDown = leftKeyDown;
		this.rightKeyDown = rightKeyDown;
		this.jump = jump;
		this.sneak = sneak;
		this.pitch = pitch;
		this.yaw = yaw;
		//if(this.pitch > 90)this.pitch = 90;
		//if(this.pitch < -90)this.pitch = -90;
		//if(this.yaw > 180)this.yaw = 180;
		//if(this.yaw < -180)this.yaw = -180;
		this.leftClick = leftClick;
		this.rightClick = rightClick;
		this.sprint = sprint;
		this.slot = slot;
	}
    
}
