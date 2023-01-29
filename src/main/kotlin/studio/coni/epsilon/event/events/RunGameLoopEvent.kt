package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event

sealed class RunGameLoopEvent : Event() {
    object Start : RunGameLoopEvent()
    object Tick : RunGameLoopEvent()
    object Render : RunGameLoopEvent()
    object End : RunGameLoopEvent()
}