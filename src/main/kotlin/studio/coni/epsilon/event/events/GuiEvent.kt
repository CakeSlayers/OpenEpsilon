package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.client.gui.GuiScreen

sealed class GuiEvent : Event() {
    abstract val screen: GuiScreen?

    class Closed(override val screen: GuiScreen) : GuiEvent()

    class Displayed(override var screen: GuiScreen?) : GuiEvent()
}