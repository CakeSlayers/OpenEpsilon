package studio.coni.epsilon.gui.def.components.elements

import studio.coni.epsilon.gui.*
import studio.coni.epsilon.gui.def.AsyncRenderEngine
import studio.coni.epsilon.gui.def.components.AbstractElement
import studio.coni.epsilon.gui.def.components.AnimatedAlphaUnit
import studio.coni.epsilon.gui.def.components.Panel
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.setting.AbstractSetting

class StringField(
    override var father: IFatherComponent,
    override val setting: AbstractSetting<String>,
    override var x: Int = father.x,
    override var y: Int = father.y,
    override var height: Int = father.height,
    override var width: Int = father.width,
    override val panel: Panel
) : AbstractElement(), IChildComponent, ISettingSupplier<String>, IPanelProvider, IDescriptorContainer {

    var editing = false

    override val animatedAlphaUnit = AnimatedAlphaUnit()

    override fun onRender(mouseX: Int, mouseY: Int, partialTicks: Float) {
        AsyncRenderEngine.currentTheme.stringField(this, mouseX, mouseY, partialTicks)
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
        return true
    }

}