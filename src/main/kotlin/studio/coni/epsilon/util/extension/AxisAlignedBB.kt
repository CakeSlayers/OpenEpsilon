package studio.coni.epsilon.util.extension

import studio.coni.epsilon.common.extensions.renderPosX
import studio.coni.epsilon.common.extensions.renderPosY
import studio.coni.epsilon.common.extensions.renderPosZ
import studio.coni.epsilon.common.interfaces.Helper
import net.minecraft.util.math.AxisAlignedBB

object AxisAlignedBB : Helper {

    fun AxisAlignedBB.interp(): AxisAlignedBB {
        return this.offset(
            -mc.renderManager.renderPosX,
            -mc.renderManager.renderPosY,
            -mc.renderManager.renderPosZ
        )
    }

}