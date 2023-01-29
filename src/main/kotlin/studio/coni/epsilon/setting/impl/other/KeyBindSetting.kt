package studio.coni.epsilon.setting.impl.other

import studio.coni.epsilon.common.extensions.copy
import studio.coni.epsilon.common.key.KeyBind
import studio.coni.epsilon.management.InputManager.register
import studio.coni.epsilon.setting.MutableSetting

class KeyBindSetting(
    name: String,
    value: KeyBind,
    visibility: (() -> Boolean) = { true },
    moduleName: String,
    description: String = ""
) : MutableSetting<KeyBind>(name, value, moduleName, description, visibility) {

    private val defKeyCodes = value.key.copy()

    override fun reset() {
        this.value.key = defKeyCodes
    }

    init {
        this.value.register()
    }

}