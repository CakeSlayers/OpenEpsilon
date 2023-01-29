package studio.coni.epsilon.event.decentralized.events.render

import studio.coni.epsilon.event.decentralized.CancellableEventData
import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent

object RenderOverlayDecentralizedEvent : DataDecentralizedEvent<RenderOverlayDecentralizedEvent.RenderOverlayData>() {

    data class RenderOverlayData(val type: OverlayType) : CancellableEventData(this)

    enum class OverlayType {
        FIRE, BLOCK, WATER
    }

}