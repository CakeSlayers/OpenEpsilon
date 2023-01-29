package studio.coni.epsilon.event.decentralized.events.client

import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import studio.coni.epsilon.event.decentralized.EventData
import net.minecraft.client.gui.ScaledResolution

object Render2DDecentralizedEvent : DataDecentralizedEvent<Render2DDecentralizedEvent.Render2DEventData>() {
    class Render2DEventData(val resolution: ScaledResolution) : EventData(this)
}
