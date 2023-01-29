package studio.coni.epsilon.gui.def.components

import studio.coni.epsilon.util.graphics.font.renderer.MainFontRenderer

object Scale {
    val panelHeight
        get() = (MainFontRenderer.getHeight() + 9).toInt()

    val moduleButtonHeight
        get() = (MainFontRenderer.getHeight() + 8).toInt()

    val settingHeight
        get() = (MainFontRenderer.getHeight() + 5).toInt()
}