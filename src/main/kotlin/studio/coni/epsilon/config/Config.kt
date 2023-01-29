package studio.coni.epsilon.config

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.setting.AbstractSetting
import java.io.File

@Suppress("NOTHING_TO_INLINE")
abstract class Config(
    val configName: String,
    val configs: MutableList<AbstractSetting<*>> = mutableListOf()
) {
    open val dirPath = EpsilonPlus.DEFAULT_CONFIG_PATH + "config/"
    private inline val savePath get() = "$dirPath/$configName"

    private var file: File? = null

    abstract fun saveConfig()
    abstract fun loadConfig()

    protected val configFile
        get() = file ?: File(savePath).also {
            file = it
        }
}