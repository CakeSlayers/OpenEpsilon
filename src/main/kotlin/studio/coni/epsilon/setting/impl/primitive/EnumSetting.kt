package studio.coni.epsilon.setting.impl.primitive

import studio.coni.epsilon.setting.MutableSetting
import studio.coni.epsilon.util.Utils.last
import studio.coni.epsilon.util.Utils.next

class EnumSetting<T : Enum<T>>(
    name: String,
    value: T,
    visibility: (() -> Boolean) = { true },
    moduleName: String,
    description: String = ""
) : MutableSetting<T>(name, value, moduleName, description, visibility) {

    private val enumClass: Class<T> = value.declaringClass
    private val enumValues: Array<out T> = enumClass.enumConstants

    fun nextValue() {
        value = value.next()
    }

    fun lastValue() {
        value = value.last()
    }

    fun currentName(): String {
        return value.name
    }

    fun setWithName(nameIn: String) {
        enumValues.firstOrNull { it.name.equals(nameIn, true) }?.let {
            value = it
        }
    }
}