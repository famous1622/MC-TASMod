package de.tr7zw.tas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class KeyFrame {
    public boolean forwardKeyDown;        //Where all the Keys are saved!
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneak;
    public float pitch;
    public float yaw;
    public String leftClick;
    public String rightClick;
    public boolean sprint;
    public boolean drop;
    public int mouseX;
    public int mouseY;
    public int slot;
    public boolean gui_clicked;
    public int gui_mouseX;
    public int gui_mouseY;
    public int gui_mouseButton;
    public int gui_slotUnderMouse;
    public boolean gui_typed;
    public char gui_typedChar;
    public int gui_keyCode;

    public KeyFrame(boolean forwardKeyDown, boolean backKeyDown, boolean leftKeyDown, boolean rightKeyDown,
                    boolean jump, boolean sneak, boolean sprint, boolean drop, float pitch, float yaw,
                    String leftClick, String rightClick, int slot, int mousex, int mousey,
                    int gui_slotUnderMouse, boolean gui_clicked, int gui_mouseX, int gui_mouseY, int gui_mouseButton,
                    boolean gui_typed, char gui_typedChar, int gui_keyCode) {
        super();
        this.forwardKeyDown = forwardKeyDown;
        this.backKeyDown = backKeyDown;
        this.leftKeyDown = leftKeyDown;
        this.rightKeyDown = rightKeyDown;
        this.jump = jump;
        this.sneak = sneak;
        this.pitch = pitch;
        this.yaw = yaw;
        this.drop = drop;
        this.mouseX = mousex;
        this.mouseY = mousey;
        //if(this.pitch > 90)this.pitch = 90;
        //if(this.pitch < -90)this.pitch = -90;
        //if(this.yaw > 180)this.yaw = 180;
        //if(this.yaw < -180)this.yaw = -180;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.sprint = sprint;
        this.slot = slot;
        this.gui_clicked = gui_clicked;
        this.gui_mouseX = gui_mouseX;
        this.gui_mouseY = gui_mouseY;
        this.gui_mouseButton = gui_mouseButton;
        this.gui_slotUnderMouse = gui_slotUnderMouse;
        this.gui_typed = gui_typed;
        this.gui_typedChar = gui_typedChar;
        this.gui_keyCode = gui_keyCode;
    }


}
