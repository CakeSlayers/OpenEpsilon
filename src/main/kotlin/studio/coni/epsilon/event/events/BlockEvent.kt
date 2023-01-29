package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

sealed class BlockEvent(var pos: BlockPos, var facing: EnumFacing) : Cancellable() {
    class Click(pos: BlockPos, facing: EnumFacing) : BlockEvent(pos, facing)
    class Damage(pos: BlockPos, facing: EnumFacing) : BlockEvent(pos, facing)
    class Place(pos: BlockPos, facing: EnumFacing, var vec: Vec3d, var hand: EnumHand) : BlockEvent(pos, facing)
}