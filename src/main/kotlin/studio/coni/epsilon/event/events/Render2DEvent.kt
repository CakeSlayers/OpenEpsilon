package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.client.gui.ScaledResolution

class Render2DEvent(val resolution: ScaledResolution) : Event()