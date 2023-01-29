package studio.coni.epsilon.mixin.mixins.accessor.render;

import net.minecraft.client.renderer.DestroyBlockProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DestroyBlockProgress.class)
public interface AccessorDestroyBlockProgress {
    @Accessor("miningPlayerEntId")
    int epsilonGetEntityID();
}
