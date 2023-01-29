package studio.coni.epsilon.module.player

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.atTrue
import studio.coni.epsilon.event.events.ConnectionEvent
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.events.TickEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.hud.info.LagNotification
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.notification.Notification
import studio.coni.epsilon.notification.NotificationManager
import studio.coni.epsilon.notification.NotificationType
import studio.coni.epsilon.process.PauseProcess.pauseBaritone
import studio.coni.epsilon.process.PauseProcess.unpauseBaritone
import studio.coni.epsilon.util.math.MathUtils
import studio.coni.epsilon.util.math.Vec2f
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.TimeUnit
import studio.coni.epsilon.util.WebUtils
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.math.Vec3d

internal object LagBackCheck : Module(
    name = "LagBackCheck",
    description = "Displays a warning when the server is lagging",
    category = Category.Player
) {
    private val pauseBaritone0 = setting("Pause Baritone", true)
    private val pauseBaritone by pauseBaritone0
    val pauseTakeoff by setting("Pause Elytra Takeoff", true)
    val pauseAutoWalk by setting("Pause Auto Walk", true)
    private val feedback by setting("Pause Feedback", true, pauseBaritone0.atTrue())


    private val pingTimer = TickTimer(TimeUnit.SECONDS)
    private val lastPacketTimer = TickTimer()
    private val lastRubberBandTimer = TickTimer()

    var paused = false; private set

    override fun onDisable() {
        unpause()
    }

    init {

        safeListener<TickEvent> {
            if (mc.isIntegratedServerRunning) {
                unpause()
            } else {
                val timeoutMillis = LagNotification.timeOut.toLong() * 1000L
                when {
                    lastPacketTimer.tick(timeoutMillis) -> {
                        if (pingTimer.tickAndReset(1L)) WebUtils.update()
                        pause()
                    }
                    !lastRubberBandTimer.tick(timeoutMillis) -> {
                        pause()
                    }
                    else -> {
                        unpause()
                    }
                }
            }
        }

        safeListener<PacketEvent.Receive>(2000) {
            lastPacketTimer.reset()

            if (it.packet !is SPacketPlayerPosLook || player.ticksExisted < 20) return@safeListener

            val dist = Vec3d(it.packet.x, it.packet.y, it.packet.z).subtract(player.positionVector).length()
            val rotationDiff = Vec2f(it.packet.yaw, it.packet.pitch).minus(Vec2f(player.posX, player.posY)).length()

            if (dist in 0.5..64.0 || rotationDiff > 1.0) lastRubberBandTimer.reset()
        }

        listener<ConnectionEvent.Connect> {
            lastPacketTimer.reset(69420L)
            lastRubberBandTimer.reset(-69420L)
        }
    }

    private fun pause() {
        if (!paused && pauseBaritone && feedback) {
            NotificationManager.show(Notification(title = "LagNotifier", message = "Paused due to lag!", type = NotificationType.WARNING))
        }

        pauseBaritone()
        paused = true
    }

    private fun unpause() {
        if (paused && pauseBaritone && feedback) {
            NotificationManager.show(Notification(title = "LagNotifier", message = "Unpaused!", type = NotificationType.INFO))
        }
        unpauseBaritone()
        paused = false
    }

    private fun timeDifference(timeIn: Long) = MathUtils.round((System.currentTimeMillis() - timeIn) / 1000.0, 1)
}
