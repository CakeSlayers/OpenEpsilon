package studio.coni.epsilon.setting.impl.other

import studio.coni.epsilon.language.TextUnit
import studio.coni.epsilon.setting.AbstractSetting
import java.util.*

class ListenerSetting(
    override val name: String,
    override val defaultValue: () -> Unit,
    override val visibility: () -> Boolean,
    moduleName: String,
    description: String
) : AbstractSetting<() -> Unit>() {
    override var value: () -> Unit = defaultValue
    override val description = TextUnit(
        "module_"
                + moduleName.lowercase(Locale.getDefault()).replace(" ", "_")
                + "_" + name.lowercase(Locale.getDefault()).replace(" ", "_"),
        description
    )
}