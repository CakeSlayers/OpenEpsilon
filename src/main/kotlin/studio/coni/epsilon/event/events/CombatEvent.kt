package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.entity.EntityLivingBase

sealed class CombatEvent : Event() {
    abstract val entity: EntityLivingBase?

    class UpdateTarget(val prevEntity: EntityLivingBase?, override val entity: EntityLivingBase?) : CombatEvent()
}
