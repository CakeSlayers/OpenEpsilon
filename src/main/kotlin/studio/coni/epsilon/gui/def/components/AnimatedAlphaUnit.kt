package studio.coni.epsilon.gui.def.components

import studio.coni.epsilon.gui.IFloatAnimatable
import studio.coni.epsilon.util.graphics.AnimationUtil

class AnimatedAlphaUnit : IFloatAnimatable {

    override var currentValue: Float = 0F

    fun update(isHoovered: Boolean, speed: Float = 0.3F) {
        currentValue = if (isHoovered) AnimationUtil.animate(100F, currentValue, speed)
        else AnimationUtil.animate(0F, currentValue, 0.15F)
    }

}