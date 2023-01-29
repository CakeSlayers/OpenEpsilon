package studio.coni.epsilon.event.decentralized.events.client

import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import studio.coni.epsilon.event.decentralized.EventData

object Render3DDecentralizedEvent : DataDecentralizedEvent<Render3DDecentralizedEvent.Render3DEventData>() {
    class Render3DEventData(val partialTicks: Float) : EventData(this)
}