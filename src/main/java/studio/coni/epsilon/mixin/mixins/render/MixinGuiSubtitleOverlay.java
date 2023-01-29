package studio.coni.epsilon.mixin.mixins.render;

import studio.coni.epsilon.event.EventBus;
import studio.coni.epsilon.event.decentralized.events.client.Render2DDecentralizedEvent;
import studio.coni.epsilon.event.events.Render2DEvent;
import studio.coni.epsilon.event.events.RenderTick;
import studio.coni.epsilon.util.ScaleHelper;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSubtitleOverlay.class)
public class MixinGuiSubtitleOverlay {

    @Inject(method = "renderSubtitles", at = @At("HEAD"))
    public void onRender2D(ScaledResolution resolution, CallbackInfo ci) {
        ScaleHelper.INSTANCE.setScaledResolution(resolution);
        Render2DDecentralizedEvent.INSTANCE.post(new Render2DDecentralizedEvent.Render2DEventData(resolution));
        EventBus.post(new Render2DEvent(resolution));
        RenderTick.INSTANCE.post();
    }

}
