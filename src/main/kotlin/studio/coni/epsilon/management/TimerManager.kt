package studio.coni.epsilon.management

import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.common.collections.lastValueOrNull
import studio.coni.epsilon.common.collections.synchronized
import studio.coni.epsilon.common.extensions.tickLength
import studio.coni.epsilon.common.extensions.timer
import studio.coni.epsilon.event.events.RunGameLoopEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.util.Wrapper.mc
import studio.coni.epsilon.util.threads.runSafe
import java.util.*
import kotlin.math.roundToInt

object TimerManager {
    private val modifiers = TreeMap<AbstractModule, Modifier>().synchronized()
    private var modified = false

    var totalTicks = Int.MIN_VALUE
    var tickLength = 50.0f; private set

    init {
        listener<RunGameLoopEvent.Start>(Int.MAX_VALUE, true) {
            runSafe {
                synchronized(modifiers) {
                    modifiers.values.removeIf { it.endTick < totalTicks }
                    modifiers.lastValueOrNull()?.let {
                        mc.timer.tickLength = it.tickLength
                    } ?: return@runSafe null
                }

                modified = true
            } ?: run {
                modifiers.clear()
                if (modified) {
                    mc.timer.tickLength = 50.0f
                    modified = false
                }
            }

            tickLength = mc.timer.tickLength
        }

        listener<RunGameLoopEvent.Tick>(Int.MAX_VALUE, true) {
            totalTicks++
        }
    }

    fun AbstractModule.resetTimer() {
        modifiers.remove(this)
    }

    fun AbstractModule.modifyTimer(tickLength: Float, timeoutTicks: Int = 1) {
        runSafe {
            modifiers[this@modifyTimer] = Modifier(tickLength, totalTicks + studio.coni.epsilon.util.graphics.RenderUtils3D.partialTicks.roundToInt() + timeoutTicks)
        }
    }

    private class Modifier(
        val tickLength: Float,
        val endTick: Int
    )
}