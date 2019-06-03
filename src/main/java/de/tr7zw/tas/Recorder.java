package de.tr7zw.tas;

import com.google.common.io.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.Slot;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Recorder {

    /**
     * Indicates on which line the recorder is currently at
     */
    public static int recordstep = 0;
    /**
     * Variable to see if a recording is currently running.<br>
     * If true, the recording is stopped
     */
    public static boolean donerecording = true;
    /**
     * Shows the status of the leftclick.
     * 0=unpressed, 1=pressed, 2=quickpress <br> Used for destinction if leftclick is held or pressed.
     */
    private static int clicklefty = 0;
    /**
     * Shows the status of the rightclick.
     * 0=unpressed, 1=pressed, 2=quickpress <br> Used for destinction if rightclick is held or pressed.
     */
    private static int clickrighty = 0;
    /**
     * Used to check if a leftclick was held and needs to print rLK in the next tick <br> Used for destinction if leftclick is held or pressed.
     */
    private static boolean needsunpressLK = false;
    /**
     * Used to check if a rightclick was held and needs to print rRK in the next tick <br> Used for destinction if leftclick is held or pressed.
     */
    private static boolean needsunpressRK = false;
    private static float tickpitch;
    private static float tickyaw;
    private ArrayList<Object> recording = new ArrayList<>();
    private Minecraft mc = Minecraft.getMinecraft();
    /**
     * Variable to check if leftclick was pressed before.<br>
     * Used for destinction if leftclick is held or pressed. <br>
     * If it's true, leftclick was pressed.
     */
    private boolean lkchecker = false;
    /**
     * Variable to check if rightclick was pressed before.<br>
     * Used for destinction if rightclick is held or pressed.<br>
     * If it's true, rightclick was pressed.
     */
    private boolean rkchecker = false;
    private String leftclack = " ";
    private String rightclack = " ";


    public Recorder() {
        recording.add("#StartLocation: " + mc.player.getPositionVector().toString());
        needsunpressLK = false;
        needsunpressRK = false;

    }

    private static void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Make it, so the yaw is saved between -180 and +180 so it fits with the debug screen
     *
     * @param Yaw to recalculate (something >180 or <-180?)
     * @return calculated Yaw
     */
    public Float recalcYaw(float Yaw) {
        while (Yaw >= 180) Yaw -= 360;
        while (Yaw < -180) Yaw += 360;
        return Yaw;
    }

    /**
     * Main recording function
     */
    @SubscribeEvent
    public void onClientTickEND(TickEvent.ClientTickEvent ev) {

        if (ev.phase == Phase.END && !donerecording) {

        }


    }

    /**
     * Testmethod to let certain inputs record at the end of the tick
     */
    @SubscribeEvent
    public void onClientTickSTART(TickEvent.ClientTickEvent ev) {
        if (ev.phase == Phase.END && !donerecording) {
            GameSettings gameset = mc.gameSettings;
            leftclack = " ";
            rightclack = " ";
            //Printing the correct string for leftclick from onMouseClick
            if (clicklefty == 2) {                        //Scenario for clicking and releasing within a tick
                leftclack = "pLK";
                needsunpressLK = true;
            } else if (clicklefty == 1 && !lkchecker) {        //Scenario for clicking and not releasing within a tick
                leftclack = "pLK";
                needsunpressLK = true;
            } else if (clicklefty == 1 && lkchecker) {        //Scenario for holding the button when entering a tick. This would be the case if the above (Scenario for clicking and not releasing within a tick) was the tick beforehand
                leftclack = "hLK";
                needsunpressLK = true;
            } else if (needsunpressLK) {                //Scenario when a button was held or pressed and now it's unpressed.
                leftclack = "rLK";
                needsunpressLK = false;

            }

            //Same as above, just for rightclick
            if (clickrighty == 2) {
                rightclack = "pRK";
                needsunpressRK = true;
            } else if (clickrighty == 1 && !rkchecker) {
                rightclack = "pRK";
                needsunpressRK = true;
            } else if (clickrighty == 1 && rkchecker) {
                rightclack = "hRK";
                needsunpressRK = true;
            } else if (needsunpressRK) {
                rightclack = "rRK";
                needsunpressRK = false;
            }
            tickpitch = mc.player.rotationPitch;
            tickyaw = recalcYaw(mc.player.rotationYaw);


            //Recording the movement
            recording.add(new KeyFrame(gameset.keyBindForward.isKeyDown(), gameset.keyBindBack.isKeyDown(), gameset.keyBindLeft.isKeyDown(), gameset.keyBindRight.isKeyDown(),
                    gameset.keyBindJump.isKeyDown(), gameset.keyBindSneak.isKeyDown(), gameset.keyBindSprint.isKeyDown(),
                    gameset.keyBindDrop.isKeyDown(), tickpitch, tickyaw,
                    leftclack, rightclack,
                    mc.player.inventory.currentItem, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y));

            /*Check if leftclick was pressed and not released
             * if it was pressed and immediately released in one tick, clicklefty would equal 2 and thus lkchecker would be false*/
            lkchecker = clicklefty == 1;

            //Same for clickrighty
            rkchecker = clickrighty == 1;

            //resetting values after the recording is done
            clicklefty = 0;
            clickrighty = 0;
            //Increment the tickcounter
            if (!donerecording) recordstep++;

        }
    }

    /**
     * Method to check if Mousebuttons are pressed or held.
     */
    @SubscribeEvent
    public void onMouseClick(TickEvent.RenderTickEvent ev) {        //Complicated method to check if the mousebuttons are pressed or held... This bit was located in TASEvents once and I decided to move it here...
        if (!donerecording && ev.phase == Phase.START) {
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)) {
                //set to pressed
                clicklefty = 1;
            } else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack) && clicklefty == 1 && !lkchecker) {
                //set to quick press (e.g. pressing 2 times in 2 ticks)
                clicklefty = 2;
            } else if (!(clicklefty == 2)) {
                //set to unpressed
                clicklefty = 0;
            }
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)) {
                //set to pressed
                clickrighty = 1;
            } else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem) && clickrighty == 1 && !rkchecker) {
                //set to quick press (e.g. pressing 2 times in 2 ticks)
                clickrighty = 2;
            } else if (!(clickrighty == 2)) {
                //set to unpressed
                clickrighty = 0;
            }
        }
    }

    public void saveData(File file) {
        mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
        StringBuilder output = new StringBuilder();
        String W;                                            //Well this is... Not the best solution for this buut hey it works I guess...
        String S;
        String A;
        String D;
        String Space;
        String Shift;
        String Ctrl;
        String LK;
        String RK;
        Object buff1 = recording.get(1);
        Object buff2 = recording.get(2);
        for (int i = 0; i < recording.size(); i++) {
            Object o = recording.get(i);
            if (o instanceof String) {
                output.append(o + "\n");
            } else if (o instanceof KeyFrame) {
                KeyFrame frame = (KeyFrame) o;
                //This here was a wierd way to delay certain inputs, to test if this syncs or desyncs the TAS
				/*KeyFrame buff1frame= (KeyFrame) buff1;
				KeyFrame buff2frame= (KeyFrame) buff2;
				buff1frame.leftClick=frame.leftClick;
				frame.leftClick=buff2frame.leftClick;
				buff2frame.leftClick=buff1frame.leftClick;*/


                //I think this was to quicken inputs e.g. rightclick was now one tick before you clicked it
				/*buff1frame.rightClick=frame.rightClick;
				frame.rightClick=buff2frame.rightClick;
				buff2frame.rightClick=buff1frame.rightClick;*/

                if (frame.forwardKeyDown) W = "W";
                else W = " ";
                if (frame.backKeyDown) S = "S";
                else S = " ";
                if (frame.leftKeyDown) A = "A";
                else A = " ";
                if (frame.rightKeyDown) D = "D";
                else D = " ";
                if (frame.jump) Space = "Space";
                else Space = " ";
                if (frame.sneak) Shift = "Shift";
                else Shift = " ";
                if (frame.sprint) Ctrl = "Ctrl";
                else Ctrl = " ";
                //This was used before the onMouseClick function. Didn't work so well.
                //if(frame.leftClick==true)LK="LK";else LK=" ";
                //if(frame.rightClick==true)RK="RK";else RK=" ";

                //Writing to the file

                output.append("1;")
                        .append(W).append(";")
                        .append(S).append(";")
                        .append(A).append(";")
                        .append(D).append(";")
                        .append(Space).append(";")
                        .append(Shift).append(";")
                        .append(Ctrl).append(";")
                        .append(frame.pitch).append(";")
                        .append(frame.yaw).append(";")
                        .append(frame.leftClick).append(";")
                        .append(frame.rightClick).append(";")
                        .append(frame.slot).append(";")
                        .append(frame.mouseX).append(';')
                        .append(frame.mouseY).append(';')
                        .append(frame.drop).append(';')
                        .append('\n');

            }
        }
        output.append("END");
        try {
            Files.write(output.toString().getBytes(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Saved to: " + file.getAbsolutePath()));
        } catch (Exception exX) {
            exX.printStackTrace();
        }
    }

    private void clicksnStuff() {

    }

    public void guiClicked(int mouseX, int mouseY, int mouseButton, Slot slotUnderMouse) {
    }

    public void guiTyped(char typedChar, int keyCode, Slot slotUnderMouse) {
    }
}
