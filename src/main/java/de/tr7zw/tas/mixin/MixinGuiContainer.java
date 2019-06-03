package de.tr7zw.tas.mixin;

import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.duck.CBGuiContainer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen implements CBGuiContainer {

    private Recorder recorder;

    @Shadow
    @Nullable
    public abstract Slot getSlotUnderMouse();

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (recorder != null) {
            recorder.guiClicked(mouseX, mouseY, mouseButton, this.getSlotUnderMouse());
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (recorder != null) {
            recorder.guiTyped(typedChar, keyCode, this.getSlotUnderMouse());
        }
    }

    @Override
    public void setRecorder(Recorder newRecorder) {
        recorder = newRecorder;
    }
}
