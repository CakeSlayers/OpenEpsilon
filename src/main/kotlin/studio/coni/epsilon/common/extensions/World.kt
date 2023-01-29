package studio.coni.epsilon.common.extensions

import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.util.Wrapper
import studio.coni.epsilon.util.math.fastCeil
import studio.coni.epsilon.util.math.fastFloor
import studio.coni.epsilon.util.math.sq
import studio.coni.epsilon.util.math.vector.toVec3dCenter
import studio.coni.epsilon.util.world.isReplaceable
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max

inline fun World.canBreakBlock(pos: BlockPos): Boolean {
    val blockState = this.getBlockState(pos)
    return blockState.block == Blocks.AIR || blockState.getBlockHardness(this, pos) != -1.0f
}

fun World.getGroundPos(entity: Entity): BlockPos {
    return getGroundPos(entity.entityBoundingBox)
}

fun World.getGroundPos(boundingBox: AxisAlignedBB): BlockPos {
    val center = boundingBox.center

    val cx = center.x.fastFloor()
    val cz = center.z.fastFloor()

    var rx = cx
    var ry = Int.MIN_VALUE
    var rz = cz

    val pos = BlockPos.PooledMutableBlockPos.retain()

    for (x in boundingBox.minX.fastFloor()..boundingBox.maxX.fastFloor()) {
        for (z in boundingBox.minZ.fastFloor()..boundingBox.maxZ.fastFloor()) {
            for (y in (boundingBox.minY - 0.5).fastFloor() downTo -1) {
                if (y < ry) break

                pos.setPos(x, y, z)
                val box = this.getBlockState(pos).getCollisionBoundingBox(this, pos)
                if (box != null && (ry == Int.MIN_VALUE || y > ry || (x - cx).sq <= (rx - cx).sq && (z - cz).sq <= (rz - cz).sq)) {
                    rx = x
                    ry = y
                    rz = z
                }
            }
        }
    }

    pos.release()

    return BlockPos(rx, if (ry == Int.MIN_VALUE) -999 else ry, rz)
}


fun World.getGroundLevel(entity: Entity): Double {
    return getGroundLevel(entity.entityBoundingBox)
}

fun World.getGroundLevel(boundingBox: AxisAlignedBB): Double {
    var maxY = Double.MIN_VALUE
    val pos = BlockPos.PooledMutableBlockPos.retain()

    for (x in (boundingBox.minX- 0.1).fastFloor()..(boundingBox.maxX + 0.1).fastFloor()) {
        for (z in (boundingBox.minZ- 0.1).fastFloor()..(boundingBox.maxZ + 0.1).fastFloor()) {
            for (y in (boundingBox.minY - 0.5).fastFloor() downTo -1) {
                if (y < maxY.fastCeil() - 1) break

                pos.setPos(x, y, z)
                val box = this.getBlockState(pos).getCollisionBoundingBox(this, pos)
                if (box != null) {
                    maxY = max(maxY, y + box.maxY)
                }
            }
        }
    }

    return if (maxY == Double.MIN_VALUE) -999.0 else maxY
}

fun World.isVisible(
    pos: BlockPos,
    tolerance: Double = 1.0
) = Wrapper.player?.let {
    val center = pos.toVec3dCenter()
    val result = rayTraceBlocks(it.getPositionEyes(1.0f), center, false, true, false)

    result != null
            && (result.blockPos == pos
            || (result.hitVec != null && result.hitVec.distanceTo(center) <= tolerance))
} ?: false

fun World.isLiquid(pos: BlockPos): Boolean {
    return this.getBlockState(pos).isLiquid
}

fun World.isWater(pos: BlockPos): Boolean {
    return this.getBlockState(pos).isWater
}

fun World.isPlaceable(pos: BlockPos, ignoreSelfCollide: Boolean = false) =
    this.getBlockState(pos).isReplaceable
            && this.checkNoEntityCollision(AxisAlignedBB(pos), if (ignoreSelfCollide) Wrapper.player else null)

fun SafeClientEvent.hasNeighbor(pos: BlockPos): Boolean {
    return EnumFacing.values().any {
        !world.getBlockState(pos.offset(it)).isReplaceable
    }
}
