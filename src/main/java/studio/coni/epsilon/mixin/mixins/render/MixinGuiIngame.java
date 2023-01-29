package studio.coni.epsilon.mixin.mixins.render;

import studio.coni.epsilon.module.render.Crosshair;
import studio.coni.epsilon.util.Utils;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderAttackIndicator", at = @At(value = "HEAD"), cancellable = true)
    private void injectCrosshair(CallbackInfo ci) {
        if (!Utils.INSTANCE.nullCheck())
        if (Crosshair.INSTANCE.isEnabled() && Crosshair.INSTANCE.getCorsshair())
            ci.cancel();
    }
}