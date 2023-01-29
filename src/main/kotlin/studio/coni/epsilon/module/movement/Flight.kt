package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.OnUpdateWalkingPlayerEvent
import studio.coni.epsilon.event.events.PlayerMoveEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.management.PlayerPacketManager
import studio.coni.epsilon.management.PlayerPacketManager.sendPlayerPacket
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.MovementUtils
import studio.coni.epsilon.util.MovementUtils.calcMoveYaw
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.TimeUnit
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

internal object Flight : Module(
    name = "Flight",
    category = Category.Movement,
    description = "Makes the player fly",
    priority = 500
) {
    private val speed by setting("Speed", 1.0f, 0.0f..10.0f, 0.1f)
    private val upSpeed by setting("Up Speed", 1.0f, 0.1f..10.0f, 0.05f)
    private val downSpeed by setting("Down Speed", 1.0f, 0.1f..10.0f, 0.05f)
    private val glideSpeed by setting("Glide Speed", 0.05, 0.0..0.3, 0.001)
    private val antiKick by setting("Anti Kick", true)

    private val antiKickTimer = TickTimer(TimeUnit.TICKS)

    init {
        safeListener<PlayerMoveEvent.Pre>(-500) {
            if (MovementUtils.isInputting) {
                val yaw = calcMoveYaw()
                player.motionX = -sin(yaw) * speed
                player.motionZ = cos(yaw) * speed
            } else {
                player.motionX = 0.0
                player.motionZ = 0.0
            }

            val jump = player.movementInput.jump
            val sneak = player.movementInput.sneak

            if (jump xor sneak) {
                if (jump) {
                    player.motionY = upSpeed.toDouble()
                } else {
                    player.motionY = -downSpeed.toDouble()
                }
            } else {
                player.motionY = -glideSpeed
            }
        }

        safeListener<OnUpdateWalkingPlayerEvent.Pre> {
            if (antiKick && antiKickTimer.tickAndReset(50L)) {
                if (!world.checkBlockCollision(player.entityBoundingBox.grow(0.0625).expand(0.0, -0.55, 0.0))) {
                    sendPlayerPacket {
                        move(Vec3d(player.posX, PlayerPacketManager.position.y - 0.04, player.posZ))
                    }
                }
            }
        }
    }
}
