package studio.coni.epsilon.module.setting

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.gui.def.ThemeContainer
import studio.coni.epsilon.module.Module

object ThemeSetting : Module(
    name = "Theme",
    alias = arrayOf("Theme", "Skin"),
    category = Category.Setting,
    description = "Change the theme of this client"
) {

    val theme = setting("Theme", Themes.Metro, "The theme of GUI").valueListen { _, input ->
        ThemeContainer.update(input)
    }

    enum class Themes {
        Metro,
        Hyper,
        Flat,
        Rainbow
    }

}