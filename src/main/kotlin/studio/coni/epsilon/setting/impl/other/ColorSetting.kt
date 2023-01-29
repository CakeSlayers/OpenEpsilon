package studio.coni.epsilon.setting.impl.other

import studio.coni.epsilon.setting.MutableSetting
import studio.coni.epsilon.util.ColorRGB

class ColorSetting(
    name: String,
    value: ColorRGB,
    visibility: (() -> Boolean) = { true },
    moduleName: String,
    description: String = ""
) : MutableSetting<ColorRGB>(name, value, moduleName, description, visibility) {

    fun setColor(valueIn: Int) {
        this.value = ColorRGB(valueIn)
    }

    fun setColor(red: Int, green: Int, blue: Int, alpha: Int = 255) {
        this.value = ColorRGB(red, green, blue, alpha)
    }

    fun setColor(red: Float, green: Float, blue: Float, alpha: Float = 1F) {
        this.value = ColorRGB(red, green, blue, alpha)
    }

    fun getHex(): Int {
        return this.value.rgba
    }

}
