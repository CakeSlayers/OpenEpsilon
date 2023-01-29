package studio.coni.epsilon.common.extensions

import studio.coni.epsilon.mixin.mixins.accessor.player.AccessorPlayerControllerMP
import studio.coni.epsilon.module.movement.NoFall
import studio.coni.epsilon.util.Wrapper
import net.minecraft.block.BlockAir
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.util.math.BlockPos

var PlayerControllerMP.blockHitDelay: Int
    get() = (this as AccessorPlayerControllerMP).blockHitDelay
    set(value) {
        (this as AccessorPlayerControllerMP).blockHitDelay = value
    }


val PlayerControllerMP.curBlockDamageMP: Float get() = (this as AccessorPlayerControllerMP).curBlockDamageMP

val PlayerControllerMP.currentPlayerItem: Int get() = (this as AccessorPlayerControllerMP).currentPlayerItem

fun PlayerControllerMP.syncCurrentPlayItems() = (this as AccessorPlayerControllerMP).epsilonInvokeSyncCurrentPlayItem()

val EntityPlayerSP.isBlockUnder: Boolean get() {
    for (i in (this.posY - 1.0).toInt() downTo 1) {
        val pos = BlockPos(this.posX, i.toDouble(), NoFall.mc.player.posZ)
        if (Wrapper.mc.world.getBlockState(pos).block is BlockAir) continue
        return true
    }
    return false
}