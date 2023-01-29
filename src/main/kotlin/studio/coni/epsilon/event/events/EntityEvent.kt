package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.entity.EntityLivingBase

sealed class EntityEvent(val entity: EntityLivingBase) : Event() {
    class UpdateHealth(entity: EntityLivingBase, val prevHealth: Float, val health: Float) : EntityEvent(entity)

    class Death(entity: EntityLivingBase) : EntityEvent(entity)
}