package studio.coni.epsilon.module.client

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.common.Category
import studio.coni.epsilon.config.ConfigManager
import studio.coni.epsilon.gui.def.DefaultHUDEditorScreen
import studio.coni.epsilon.menu.main.MainMenu
import studio.coni.epsilon.module.Module
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

internal object HUDEditor : Module(
    name = "HUDEditor",
    category = Category.Client,
    visibleOnArray = false,
    description = "Edit your HUD",
    keyBind = Keyboard.KEY_GRAVE
) {

    private var lastGuiScreen: GuiScreen? = null

    fun isHUDEditor(): Boolean {
        return when (mc.currentScreen) {
            null -> false
            lastGuiScreen, getStyledScreen() -> true
            else -> false
        }
    }

    override fun onEnable() {
        if (EpsilonPlus.isReady) {
            val screen = getStyledScreen()
            if (mc.currentScreen != screen) {
                mc.displayGuiScreen(screen)
                lastGuiScreen = screen
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
            GuiSetting.Style.Default -> DefaultHUDEditorScreen
        }
         */
        return DefaultHUDEditorScreen
    }

}