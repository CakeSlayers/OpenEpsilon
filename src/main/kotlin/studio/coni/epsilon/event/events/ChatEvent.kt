package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable

class ChatEvent(var message: String) : Cancellable()