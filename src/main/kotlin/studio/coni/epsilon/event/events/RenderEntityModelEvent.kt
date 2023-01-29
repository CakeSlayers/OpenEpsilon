package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity

class RenderEntityModelEvent(
    var modelBase: ModelBase,
    var entity: Entity,
    var limbSwing: Float,
    var limbSwingAmount: Float,
    var age: Float,
    var headYaw: Float,
    var headPitch: Float,
    var scale: Float
) : Cancellable()

