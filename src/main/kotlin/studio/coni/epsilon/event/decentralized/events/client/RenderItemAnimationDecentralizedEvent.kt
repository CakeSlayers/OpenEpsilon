package studio.coni.epsilon.event.decentralized.events.client

import studio.coni.epsilon.event.decentralized.CancellableEventData
import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand

object RenderItemAnimationDecentralizedEvent :
    DataDecentralizedEvent<RenderItemAnimationDecentralizedEvent.RenderItemAnimationData>() {
    class RenderItemAnimationData(
        val stack: ItemStack,
        val hand: EnumHand,
        val coordinate: Float,
        override val father: DataDecentralizedEvent<*>
    ) :
        CancellableEventData(this)

    object Transform : DataDecentralizedEvent<RenderItemAnimationData>()
    object Render : DataDecentralizedEvent<RenderItemAnimationData>()
}