package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable

class PlayerPushEvent(var type: Type) : Cancellable() {

    enum class Type {
        BLOCK, LIQUID
    }
}
