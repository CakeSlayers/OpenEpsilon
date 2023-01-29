package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.PlayerMoveEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.TimeUnit

internal object AutoJump : Module(
    name = "AutoJump",
    category = Category.Movement,
    description = "Automatically jumps if possible"
) {
    private val delay = setting("Tick Delay", 10, 0..40, 1)

    private val timer = TickTimer(TimeUnit.TICKS)

    init {
        safeListener<PlayerMoveEvent.Pre> {
            if (player.isInWater || player.isInLava) player.motionY = 0.1
            else if (player.onGround && timer.tickAndReset(delay.value)) player.jump()
        }
    }
}