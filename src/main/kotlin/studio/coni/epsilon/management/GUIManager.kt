package studio.coni.epsilon.management

import studio.coni.epsilon.config.ConfigManager
import studio.coni.epsilon.gui.SpartanGUI
import studio.coni.epsilon.gui.def.DefaultHUDEditorScreen
import studio.coni.epsilon.gui.def.DefaultRootScreen
import studio.coni.epsilon.module.setting.GuiSetting
import studio.coni.epsilon.util.ColorHSB
import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.Timer
import studio.coni.epsilon.util.graphics.render.asyncRender
import studio.coni.epsilon.util.onRender2D

object GUIManager {
    private var hue = 0.01f

    val white = ColorRGB(255, 255, 255, 255)
    val black = ColorRGB(0, 0, 0, 255)
    val defaultGUI = SpartanGUI(name = "DefaultGUI", rootGUI = DefaultRootScreen, hudEditor = DefaultHUDEditorScreen).also { ConfigManager.register(it.config) }
    val isParticle get() = GuiSetting.backgroundEffect.value == GuiSetting.BackgroundEffect.Particle
    val isRainbow get() = GuiSetting.rainbow.value
    val isBlur get() = GuiSetting.background.value == GuiSetting.Background.Blur || GuiSetting.background.value == GuiSetting.Background.Both
    val isShadow get() = GuiSetting.background.value == GuiSetting.Background.Shadow || GuiSetting.background.value == GuiSetting.Background.Both
    private val firstGUIColor get() = GuiSetting.firstGuiColor.value
    val firstTextColor = GuiSetting.getTextColor()[0]
    val primaryTextColor = GuiSetting.getTextColor()[1]
    private val rainbowTimer = Timer()

    init {
        onRender2D {
            asyncRender {
                if (rainbowTimer.passed(10)) {
                    rainbowTimer.reset()
                    if (isRainbow) {
                        hue += GuiSetting.rainbowSpeed.value / 1000.0f
                        if (hue > 1.0f) --hue
                    }
                }
            }.render()
        }
    }

    private val rainbowColor: ColorRGB
        get() {
            return ColorHSB(hue, GuiSetting.saturation.value, GuiSetting.brightness.value).toRGB()
        }


    val firstColor: ColorRGB
        get() {
            return if (isRainbow) rainbowColor
            else firstGUIColor
        }

}