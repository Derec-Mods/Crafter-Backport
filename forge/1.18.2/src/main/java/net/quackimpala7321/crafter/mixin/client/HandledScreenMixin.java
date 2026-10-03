package net.quackimpala7321.crafter.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.quackimpala7321.crafter.screen.CrafterScreen;
import net.quackimpala7321.crafter.screen.slot.CrafterInputSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Unique
    private final HandledScreen thisHandledScreen = (HandledScreen) (Object) this;

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    public void drawSlotMixin(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (!(thisHandledScreen instanceof CrafterScreen thisCrafterScreen)) return;

        if (slot instanceof CrafterInputSlot crafterInputSlot) {
            if (thisCrafterScreen.getScreenHandler().isSlotDisabled(slot.id)) {
                thisCrafterScreen.drawDisabledSlot(matrices, crafterInputSlot);
                ci.cancel();
            }
        }
    }
}
