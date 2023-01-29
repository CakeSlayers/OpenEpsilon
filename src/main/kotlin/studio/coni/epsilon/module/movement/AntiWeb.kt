package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.isInWeb
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.AddCollisionBoxEvent
import studio.coni.epsilon.event.events.PlayerMoveEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.isFlying
import studio.coni.epsilon.util.math.fastFloor
import studio.coni.epsilon.util.world.getBlock
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

internal object AntiWeb : Module(
    name = "AntiWeb",
    description = "Prevents walking into web",
    category = Category.Movement
) {
    private val speedMultiplier by setting("Speed Multiplier", 0.8f, 0.1f..1.0f, 0.1f)

    init {
        safeListener<AddCollisionBoxEvent> {
            if (it.entity == player && it.block == Blocks.WEB) {
                it.collidingBoxes.add(
                    AxisAlignedBB(
                        it.pos.x.toDouble(), it.pos.y.toDouble(), it.pos.z.toDouble(),
                        it.pos.x + 1.0, it.pos.y + 1.0, it.pos.z + 1.0
                    )
                )
            }
        }

        safeListener<PlayerMoveEvent.Pre>(-2000) {
            if (!player.isFlying && player.onGround && player.motionY <= 0.0 && player.motionY >= -0.08 && !player.isInWeb && isAboveWeb()) {
                it.x = player.motionX * speedMultiplier
                it.z = player.motionZ * speedMultiplier
            }
        }
    }

    private fun SafeClientEvent.isAboveWeb(): Boolean {
        val box = player.entityBoundingBox
        val pos = BlockPos.PooledMutableBlockPos.retain()
        val y = (player.posY - 0.08).fastFloor()

        for (x in box.minX.fastFloor()..box.maxX.fastFloor()) {
            for (z in box.minZ.fastFloor()..box.maxZ.fastFloor()) {
                if (world.getBlock(pos.setPos(x, y, z)) != Blocks.WEB) {
                    pos.release()
                    return false
                }
            }
        }

        pos.release()
        return true
    }
}