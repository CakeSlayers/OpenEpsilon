package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event

sealed class ConnectionEvent : Event() {
    object Connect : ConnectionEvent()
    object Disconnect : ConnectionEvent()
}