package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable
import net.minecraft.entity.Entity

class EntityCollisionEvent(val entity: Entity, var x: Double, var y: Double, var z: Double) : Cancellable()
