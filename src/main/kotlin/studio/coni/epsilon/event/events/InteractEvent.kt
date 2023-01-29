package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

sealed class InteractEvent : Cancellable() {
    sealed class Block(
        val pos: BlockPos,
        val side: EnumFacing,
        val hand: EnumHand
    ) : InteractEvent() {
        class LeftClick(pos: BlockPos, side: EnumFacing) : Block(pos, side, EnumHand.MAIN_HAND)

        class RightClick(pos: BlockPos, side: EnumFacing) : Block(pos, side, EnumHand.MAIN_HAND)

        class Damage(pos: BlockPos, side: EnumFacing) : Block(pos, side, EnumHand.MAIN_HAND)
    }

    sealed class Item(
        val hand: EnumHand
    ) : InteractEvent() {
        class RightClick(hand: EnumHand) : Item(hand)
    }
}