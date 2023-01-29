package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.events.PlayerMoveEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.management.HoleManager
import studio.coni.epsilon.management.TimerManager.modifyTimer
import studio.coni.epsilon.management.TimerManager.resetTimer
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.module.movement.Speed
import studio.coni.epsilon.module.movement.Step
import studio.coni.epsilon.util.MovementUtils.applySpeedPotionEffects
import studio.coni.epsilon.util.MovementUtils.isCentered
import studio.coni.epsilon.util.MovementUtils.resetMove
import studio.coni.epsilon.util.MovementUtils.speed
import studio.coni.epsilon.util.combat.HoleInfo
import studio.coni.epsilon.util.extension.betterPosition
import studio.coni.epsilon.util.isFlying
import studio.coni.epsilon.util.math.RotationUtils
import studio.coni.epsilon.util.math.fastCeil
import studio.coni.epsilon.util.math.toRadian
import studio.coni.epsilon.util.math.vector.distanceSq
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.MovementInputFromOptions
import net.minecraftforge.client.event.InputUpdateEvent
import kotlin.math.*

internal object HoleSnap : Module(
    name = "HoleSnap",
    description = "Move you into the hole nearby",
    category = Category.Combat,
    priority = 1200
) {
    private val vRange by setting("V Range", 5, 1..8, 1)
    private val hRange by setting("H Range", 4.0f, 1.0f..8.0f, 0.25f)
    private val timer by setting("Timer", 2.0f, 1.0f..4.0f, 0.01f)
    private val postTimer by setting("Post Timer", 0.25f, 0.01f..1.0f, 0.01f) { timer > 1.0f }
    private val maxPostTicks by setting("Max Post Ticks", 20, 0..50, 1) { timer > 1.0f && postTimer < 1.0f }
    private val timeoutTicks by setting("Timeout Ticks", 10, 0..100, 5)
    private val disableStrafe by setting("Disable Speed", false)
    private val disableStep by setting("Disable Step", false)

    var hole: HoleInfo? = null; private set
    private var stuckTicks = 0
    private var ranTicks = 0
    private var enabledTicks = 0

    override fun isActive(): Boolean {
        return isEnabled && hole != null
    }

    override fun onDisable() {
        hole = null
        stuckTicks = 0
        ranTicks = 0
        enabledTicks = 0
        resetTimer()
    }

    init {
//        safeListener<Render3DEvent>(1) {
//            hole?.let {
//                val posFrom = EntityUtil.getInterpolatedPos(player, RenderUtils3D.partialTicks)
//                val color = ColorRGB(32, 255, 32, 255)
//
//                RenderUtils3D.putVertex(posFrom.x, posFrom.y, posFrom.z, color)
//                RenderUtils3D.putVertex(it.center.x, it.center.y, it.center.z, color)
//
//                glLineWidth(2.0f)
//                glDisable(GL_DEPTH_TEST)
//                RenderUtils3D.draw(GL_LINES)
//                glLineWidth(1.0f)
//                glEnable(GL_DEPTH_TEST)
//            }
//        }

        listener<PacketEvent.Receive> {
            if (it.packet is SPacketPlayerPosLook) disable()
        }

        listener<InputUpdateEvent>(-69) {
            if (it.movementInput is MovementInputFromOptions && isActive()) {
                it.movementInput.resetMove()
            }
        }

        safeListener<PlayerMoveEvent.Pre>(-10) {
            /*
            if (!HolePathFinder.isActive() && ++enabledTicks > timeoutTicks) {
                disable()
                return@safeListener
            }
             */

            if (!player.isEntityAlive || player.isFlying) return@safeListener

            val currentSpeed = player.speed

            if (shouldDisable(currentSpeed)) {
                val ticks = ranTicks
                disable()

                if (timer > 0.0f && postTimer < 1.0f && ticks > 0) {
                    val x = (postTimer * ticks - timer * postTimer * ticks) / (timer * (postTimer - 1.0f))
                    val postTicks = min(maxPostTicks, x.fastCeil())
                    modifyTimer(50.0f / postTimer, postTicks)
                }
                return@safeListener
            }

            hole = findHole()?.also {
                enabledTicks = 0

                modifyTimer(50.0f / timer)
                ranTicks++
                if (disableStrafe) Speed.disable(notification = false)
                if (disableStep) Step.disable(notification = false)

                val playerPos = player.positionVector
                val yawRad = RotationUtils.getRotationTo(playerPos, it.center).x.toRadian()
                val dist = hypot(it.center.x - playerPos.x, it.center.z - playerPos.z)
                val baseSpeed = player.applySpeedPotionEffects(0.2873)
                val speed = if (player.onGround) baseSpeed else max(currentSpeed + 0.02, baseSpeed)
                val cappedSpeed = min(speed, dist)

                player.motionX = -sin(yawRad) * cappedSpeed
                player.motionZ = cos(yawRad) * cappedSpeed

                if (player.collidedHorizontally) stuckTicks++
                else stuckTicks = 0
            }
        }
    }

    private fun SafeClientEvent.shouldDisable(currentSpeed: Double) =
        hole?.let { player.posY < it.origin.y } ?: false
            || stuckTicks > 5 && currentSpeed < 0.05
            || HoleManager.getHoleInfo(player).let {
            it.isHole && player.isCentered(it.center)
        }

    private fun SafeClientEvent.findHole(): HoleInfo? {
        val playerPos = player.betterPosition
        val hRangeSq = hRange * hRange

        return HoleManager.holeInfos.asSequence()
            .filterNot { it.isTrapped }
            .filter { playerPos.y > it.origin.y }
            .filter { playerPos.y - it.origin.y <= vRange }
            .filter { distanceSq(player.posX, player.posZ, it.center.x, it.center.z) <= hRangeSq }
            .filter { it.canEnter(world, playerPos) }
            .minByOrNull { distanceSq(player.posX, player.posZ, it.center.x, it.center.z) }
    }
}