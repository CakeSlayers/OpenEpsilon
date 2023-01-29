package studio.coni.epsilon.gui.def.components.elements

import studio.coni.epsilon.gui.def.AsyncRenderEngine
import studio.coni.epsilon.gui.def.components.AbstractElement
import studio.coni.epsilon.gui.def.components.AnimatedAlphaUnit
import studio.coni.epsilon.gui.def.components.Panel
import studio.coni.epsilon.gui.def.components.elements.other.SBBox
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.gui.*
import studio.coni.epsilon.setting.AbstractSetting
import studio.coni.epsilon.util.ColorRGB

class ColorPicker(
    override var father: IFatherComponent,
    override val setting: AbstractSetting<ColorRGB>,
    override var x: Int = father.x,
    override var y: Int = father.y,
    override var height: Int = father.height,
    override var width: Int = father.width,
    override val panel: Panel,
) : AbstractElement(), IChildComponent, IFloatAnimatable,
    ISettingSupplier<ColorRGB>, IPanelProvider, IDescriptorContainer {

    override val animatedAlphaUnit = AnimatedAlphaUnit()
    override var currentValue: Float = 0f

    val sbBox = SBBox(setting)

    var sliding = false

    override fun onRender(mouseX: Int, mouseY: Int, partialTicks: Float) {
        AsyncRenderEngine.currentTheme.colorPicker(this, mouseX, mouseY, partialTicks)
    }

    override fun drawDescription(mouseX: Int, mouseY: Int) {
        if (isHoovered(mouseX, mouseY) && getDescription() != "") {
            GUIManager.defaultGUI.currentDescription = getDescription()
        }
    }

    override fun getDescription(): String {
        return setting.description.currentText
    }

    override fun isVisible(): Boolean {
        return setting.isVisible
    }

    override fun onMouseClicked(x: Int, y: Int, button: Int): Boolean {
        if (!setting.isVisible || !isHoovered(x, y) || !panel.hooveredInDrawnPanel(x, y)) return false
        if (button == 0) {
            sliding = true
            return true
        } else if (button == 1) {
            sbBox.x = x
            sbBox.y = y
            AsyncRenderEngine.currentSBBOX = sbBox
            AsyncRenderEngine.noClear = true
            sbBox.animatedWidth = 0f
            sbBox.animatedHeight = 0f
            return true
        }
        return false
    }

    override fun onMouseReleased(x: Int, y: Int, state: Int) {
        sliding = false
    }

}