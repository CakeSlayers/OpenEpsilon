package studio.coni.epsilon.util

import studio.coni.epsilon.common.extensions.tickLength
import studio.coni.epsilon.common.extensions.timer
import studio.coni.epsilon.common.interfaces.Helper

class WorldTimer : Helper {
    private var overrideSpeed = 1.0f

    private fun useTimer() {
        if (overrideSpeed != 1.0f && overrideSpeed > 0.1f) {
            mc.timer.tickLength = 50.0f / overrideSpeed
        }
    }

    fun setOverrideSpeed(f: Float) {
        overrideSpeed = f
        useTimer()
    }

    fun resetTime() {
        Wrapper.mc.timer.tickLength = 50f
    }
}
