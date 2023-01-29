package studio.coni.epsilon.mixin.mixins.network;

import studio.coni.epsilon.event.SafeClientEvent;
import studio.coni.epsilon.event.events.ConnectionEvent;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConnecting.class)
public class MixinGuiConnecting {

    @Inject(method = "connect", at = @At("HEAD"))
    private void onPreConnect(CallbackInfo info) {
        SafeClientEvent.Companion.update();
        ConnectionEvent.Connect.INSTANCE.post();
    }


}
