package studio.coni.epsilon.event.decentralized.events.client

import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import studio.coni.epsilon.event.decentralized.EventData

object InputUpdateDecentralizedEvent : DataDecentralizedEvent<InputUpdateDecentralizedEvent.InputUpdateEventData>() {
    class InputUpdateEventData(val key: Int, val character: Char) : EventData(this)
}