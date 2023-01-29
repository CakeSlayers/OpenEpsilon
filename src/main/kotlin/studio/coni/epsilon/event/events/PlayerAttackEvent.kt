package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable
import net.minecraft.entity.Entity

class PlayerAttackEvent(val entity: Entity) : Cancellable()