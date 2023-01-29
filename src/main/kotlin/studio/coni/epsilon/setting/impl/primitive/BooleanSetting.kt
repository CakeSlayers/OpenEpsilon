package studio.coni.epsilon.setting.impl.primitive

import studio.coni.epsilon.setting.MutableSetting

open class BooleanSetting(
    name: String,
    value: Boolean,
    visibility: (() -> Boolean) = { true },
    moduleName: String,
    description: String = ""
) : MutableSetting<Boolean>(name, value, moduleName, description, visibility)