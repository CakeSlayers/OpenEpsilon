package studio.coni.epsilon.gui.def

import studio.coni.epsilon.gui.def.theme.IDefaultBothTheme
import studio.coni.epsilon.gui.def.theme.impl.FlatTheme
import studio.coni.epsilon.gui.def.theme.impl.MetroTheme
import studio.coni.epsilon.module.setting.ThemeSetting
import studio.coni.epsilon.util.create

object ThemeContainer {

    private val currentTheme = create<IDefaultBothTheme>()

    fun syncTheme(): IDefaultBothTheme? {
        return currentTheme.value
    }

    private val themes: Map<ThemeSetting.Themes, IDefaultBothTheme> = mapOf(
        Pair(ThemeSetting.Themes.Metro, MetroTheme),
        Pair(ThemeSetting.Themes.Flat, FlatTheme)
//        Pair(ThemeSetting.Themes.Hyper, HyperTheme),
//        Pair(ThemeSetting.Themes.Rainbow, RainbowTheme),
    )

    init {
        update(ThemeSetting.Themes.Metro)
    }

    fun update(style: ThemeSetting.Themes) {
        val theme = themes[style]
        if (theme != null && theme != currentTheme) {
            currentTheme.value = theme
        }
    }

}