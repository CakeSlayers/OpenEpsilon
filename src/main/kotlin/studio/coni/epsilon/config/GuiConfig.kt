package studio.coni.epsilon.config

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.common.extensions.isNotExist
import studio.coni.epsilon.gui.SpartanGUI

class GuiConfig(
    val gui: SpartanGUI
) : Config("${gui.name}.json") {
    override val dirPath = EpsilonPlus.DEFAULT_CONFIG_PATH + "config/gui/"

    override fun saveConfig() {
        if (configFile.isNotExist()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        }
    }

    override fun loadConfig() {
        if (configFile.exists()) {
        } else saveConfig()
    }

}