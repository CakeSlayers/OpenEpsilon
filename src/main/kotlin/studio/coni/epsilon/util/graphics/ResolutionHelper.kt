package studio.coni.epsilon.util.graphics

import studio.coni.epsilon.util.Wrapper
import net.minecraft.client.gui.ScaledResolution

object ResolutionHelper {
    val height get() = ScaledResolution(Wrapper.mc).scaledHeight
    val width get() = ScaledResolution(Wrapper.mc).scaledWidth
}