package studio.coni.epsilon.gui.def.components.elements

import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.gui.def.AsyncRenderEngine
import studio.coni.epsilon.gui.def.components.AbstractElement
import studio.coni.epsilon.gui.def.components.AnimatedAlphaUnit
import studio.coni.epsilon.gui.def.components.Panel
import studio.coni.epsilon.gui.def.components.Scale
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.management.SpartanCore
import studio.coni.epsilon.setting.impl.number.NumberSetting
import studio.coni.epsilon.setting.impl.other.ColorSetting
import studio.coni.epsilon.setting.impl.other.KeyBindSetting
import studio.coni.epsilon.setting.impl.other.ListenerSetting
import studio.coni.epsilon.setting.impl.primitive.BooleanSetting
import studio.coni.epsilon.setting.impl.primitive.EnumSetting
import studio.coni.epsilon.gui.*
import studio.coni.epsilon.setting.impl.primitive.StringSetting
import studio.coni.epsilon.util.Timer

class ModuleButton(
    val module: AbstractModule,
    override var father: IFatherComponent,
    override var x: Int = father.x,
    override var y: Int = father.y,
    override var height: Int = father.height,
    override var width: Int = father.width,
    override val panel: Panel
) : AbstractElement(), IChildComponent, IFloatAnimatable, IFatherExtendable, IPanelProvider, IDescriptorContainer {

    override var isActive: Boolean = false
    override var children: MutableList<IChildComponent> = mutableListOf()
    override var visibleChildren = listOf<IChildComponent>()
    override var target = 0
    override var current = 0
    override var isPaused = false
    override val timer = Timer()

    init {
        SpartanCore.registerExtendable(this)
        module.config.configs.forEach {
            when (it) {
                is KeyBindSetting -> children.add(BindButton(father = this, setting = it, panel = panel))
                is ListenerSetting -> children.add(ActionButton(father = this, setting = it, panel = panel))
                is BooleanSetting -> children.add(BooleanButton(father = this, setting = it, panel = panel))
                is ColorSetting -> children.add(ColorPicker(father = this, setting = it, panel = panel))
                is EnumSetting -> children.add(EnumButton(father = this, setting = it, panel = panel))
                is NumberSetting -> children.add(NumberSlider(father = this, setting = it, panel = panel))
                is StringSetting -> children.add(StringField(father = this, setting = it, panel = panel))
            }
        }
    }

    override val animatedAlphaUnit = AnimatedAlphaUnit()
    override var currentValue: Float = 0F

//    init {
//    }

    override fun onRender(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.height = Scale.moduleButtonHeight
        this.width = father.width
        this.children.forEach {
            it.height = Scale.settingHeight
            it.width = this.width
        }
        AsyncRenderEngine.currentTheme.moduleButton(this, mouseX, mouseY, partialTicks)
    }

    override fun drawDescription(mouseX: Int, mouseY: Int) {
        if (isHoovered(mouseX, mouseY) && getDescription() != "") {
            GUIManager.defaultGUI.currentDescription = getDescription()
        }
    }

    override fun getDescription(): String {
        return module.description.currentText
    }

    override fun onMouseClicked(x: Int, y: Int, button: Int): Boolean {
        if (!isHoovered(x, y) || !panel.hooveredInDrawnPanel(x, y)) return false
        when (button) {
            0 -> module.toggle()
            1 -> {
                isActive = !isActive
            }
        }
        return true
    }

    override fun onMouseReleased(x: Int, y: Int, state: Int) {
        for (setting in children) {
            setting.onMouseReleased(x, y, state)

            if (setting is ColorPicker) {
                setting.sbBox.onMouseReleased(x, y, state)
            }
        }
    }

    override fun keyTyped(char: Char, key: Int): Boolean {
        for (setting in children) {
            if (setting.keyTyped(char, key)) return true
        }
        return false
    }

}