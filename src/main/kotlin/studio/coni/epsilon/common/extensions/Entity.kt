package studio.coni.epsilon.common.extensions

import studio.coni.epsilon.mixin.mixins.accessor.entity.AccessorEntityLivingBase
import net.minecraft.entity.EntityLivingBase

fun EntityLivingBase.onItemUseFinish() {
    (this as AccessorEntityLivingBase).epsilonInvokeOnItemUseFinish()
}