package studio.coni.epsilon.util.world

import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.util.extension.eyePosition
import studio.coni.epsilon.util.math.Vec3f
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class PlaceInfo(
    val pos: BlockPos,
    val side: EnumFacing,
    val dist: Double,
    val hitVecOffset: Vec3f,
    val hitVec: Vec3d,
    val placedPos: BlockPos
) {
    companion object {
        fun SafeClientEvent.newPlaceInfo(pos: BlockPos, side: EnumFacing): PlaceInfo {
            val hitVecOffset = getHitVecOffset(side)
            val hitVec = getHitVec(pos, side)

            return PlaceInfo(pos, side, player.eyePosition.distanceTo(hitVec), hitVecOffset, hitVec, pos.offset(side))
        }
    }
}