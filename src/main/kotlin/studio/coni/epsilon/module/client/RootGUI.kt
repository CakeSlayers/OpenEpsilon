package studio.coni.epsilon.module.client

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.common.Category
import studio.coni.epsilon.config.ConfigManager
import studio.coni.epsilon.gui.def.DefaultRootScreen
import studio.coni.epsilon.menu.main.MainMenu
import studio.coni.epsilon.module.Module
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

internal object RootGUI : Module(
    name = "RootGUI",
    visibleOnArray = false,
    alias = arrayOf("Gui", "ClickGUI", "MainGUI"),
    category = Category.Client,
    description = "The root GUI of Epsilon",
    keyBind = Keyboard.KEY_RSHIFT
) {

    private var lastGuiScreen: GuiScreen? = null
    private val zoomIn by setting("ZoomIn ", true)

    private var cancelZoomIn = false

    override fun onEnable() {
        if (EpsilonPlus.isReady) {
            val screen = getStyledScreen()
            if (mc.currentScreen != screen) {
                mc.displayGuiScreen(screen)
                lastGuiScreen = screen
            }
            if (cancelZoomIn) {
                cancelZoomIn = false
            } else {
                val zoomAnimationTime = if (zoomIn) System.currentTimeMillis() else 0L
                DefaultRootScreen.openTime = zoomAnimationTime
            }
        }
    }

    override fun onDisable() {
        if (mc.currentScreen != null && mc.currentScreen == lastGuiScreen) {
            if (mc.player == null) {
                MainMenu.notReset = true
                mc.displayGuiScreen(MainMenu)
            } else mc.displayGuiScreen(null)
        }
        lastGuiScreen = null
        ConfigManager.saveAll(true)
    }

    private fun getStyledScreen(): GuiScreen {
        /*
        return when (style.value) {
            GuiSetting.Style.Default -> DefaultRootScreen
        }
         */
        return DefaultRootScreen
    }

}