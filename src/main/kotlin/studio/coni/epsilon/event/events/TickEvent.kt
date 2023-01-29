package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event

object TickEvent : Event() {
    object Pre : Event()
}

object RenderTick : Event()
object SpartanTick : Event()
