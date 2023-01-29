package studio.coni.epsilon.mixin.mixins.accessor;

import net.minecraft.util.BitArray;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.IBlockStatePalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStateContainer.class)
public interface AccessorBlockStateContainer {
    @Accessor("storage")
    BitArray epsilonGetStorage();

    @Accessor("palette")
    IBlockStatePalette epsilonGetPalette();
}
