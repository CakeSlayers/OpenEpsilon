package studio.coni.epsilon.event.decentralized.events.client

import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import studio.coni.epsilon.event.decentralized.EventData

object KeyDecentralizedEvent : DataDecentralizedEvent<KeyDecentralizedEvent.KeyEventData>() {
    class KeyEventData(val key: Int, val character: Char) : EventData(this)
}