package studio.coni.epsilon.gui

import studio.coni.epsilon.event.decentralized.IDecentralizedEvent
import studio.coni.epsilon.event.decentralized.Listenable
import studio.coni.epsilon.language.TextUnit
import studio.coni.epsilon.util.ColorRGB
import net.minecraft.client.gui.GuiScreen

open class SpartanScreen : GuiScreen(), Listenable {

    override val subscribedListener = ArrayList<Triple<IDecentralizedEvent<*>, (Any) -> Unit, Int>>()

    var colorPicker: ISettingSupplier<ColorRGB>? = null
    var description: TextUnit? = null

    open fun onUpdate(mouseX: Int, mouseY: Int, partialTicks: Float) {
    }

}