package studio.coni.epsilon.setting.impl.primitive

import studio.coni.epsilon.setting.MutableSetting

class StringSetting(
    name: String,
    value: String,
    visibility: (() -> Boolean) = { true },
    moduleName: String,
    description: String = ""
) : MutableSetting<String>(name, value, moduleName, description, visibility)