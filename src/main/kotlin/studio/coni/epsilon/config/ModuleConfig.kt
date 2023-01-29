package studio.coni.epsilon.config

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.common.extensions.isNotExist
import studio.coni.epsilon.config.ConfigManager.gsonPretty
import studio.coni.epsilon.config.ConfigManager.jsonParser
import studio.coni.epsilon.hud.HUDModule
import studio.coni.epsilon.setting.impl.number.DoubleSetting
import studio.coni.epsilon.setting.impl.number.FloatSetting
import studio.coni.epsilon.setting.impl.number.IntegerSetting
import studio.coni.epsilon.setting.impl.number.LongSetting
import studio.coni.epsilon.setting.impl.other.ColorSetting
import studio.coni.epsilon.setting.impl.other.KeyBindSetting
import studio.coni.epsilon.setting.impl.primitive.BooleanSetting
import studio.coni.epsilon.setting.impl.primitive.EnumSetting
import studio.coni.epsilon.setting.impl.primitive.StringSetting
import studio.coni.epsilon.util.ColorRGB
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.lwjgl.input.Keyboard
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter

@Suppress("NOTHING_TO_INLINE")
class ModuleConfig(
    val module: AbstractModule
) : Config("${module.name}.json") {
    override val dirPath = EpsilonPlus.DEFAULT_CONFIG_PATH + "config/modules/" + module.category.showName

    override fun saveConfig() {
        if (configFile.isNotExist()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        }
        val moduleObject = JsonObject()
        moduleObject.addProperty("Enabled", module.isEnabled)
        if (module is HUDModule) {
            moduleObject.addProperty("HUD_X", module.x)
            moduleObject.addProperty("HUD_Y", module.y)
        }
        if (configs.isNotEmpty()) {
            configs.forEach { setting ->
                when (setting) {
                    is KeyBindSetting -> moduleObject.addProperty(setting.name, getKey(setting))
                    is BooleanSetting -> moduleObject.addProperty(setting.name, setting.value)
                    is ColorSetting -> moduleObject.addProperty(setting.name, setting.value.rgba)
                    is IntegerSetting -> moduleObject.addProperty(setting.name, setting.value)
                    is LongSetting -> moduleObject.addProperty(setting.name, setting.value)
                    is FloatSetting -> moduleObject.addProperty(setting.name, setting.value)
                    is DoubleSetting -> moduleObject.addProperty(setting.name, setting.value)
                    is StringSetting -> moduleObject.addProperty(setting.name, setting.value)
                    is EnumSetting -> moduleObject.addProperty(setting.name, setting.currentName())
                }
            }
        }
        val saveJSon = PrintWriter(FileWriter(configFile))
        saveJSon.println(gsonPretty.toJson(moduleObject))
        saveJSon.close()
    }

    private fun getKey(setting: KeyBindSetting): String {
        val list = mutableListOf<String>()
        setting.value.key.forEach {
            list.add(Keyboard.getKeyName(it))
        }
        return list.joinToString(separator = " + ")
    }

    override fun loadConfig() {
        if (configFile.exists()) {
            val loadJson = BufferedReader(FileReader(configFile))
            val map = mutableMapOf<String, JsonElement>()
            jsonParser.parse(loadJson).asJsonObject.entrySet().forEach {
                map[it.key] = it.value
            }
            loadJson.close()
            //Enabled
            val enabled = map["Enabled"]?.asBoolean ?: module.isEnabled
            if (module.isEnabled && !enabled) module.disable(notification = false, silent = false)
            else if (module.isDisabled && enabled) module.enable(notification = false, silent = false)

            configs.forEach { setting ->
                when (setting) {
                    is KeyBindSetting -> {
                        val binds = mutableListOf<Int>()
                        map[setting.name]?.asString?.split(" + ")?.forEach {
                            binds.add(Keyboard.getKeyIndex(it))
                        }
                        if (binds.size != 0) setting.value.key = binds.toIntArray()
                    }
                    is BooleanSetting -> map[setting.name].let { if (it != null) setting.value = it.asBoolean }
                    is IntegerSetting -> map[setting.name].let { if (it != null) setting.value = it.asInt }
                    is LongSetting -> map[setting.name].let { if (it != null) setting.value = it.asLong }
                    is FloatSetting -> map[setting.name].let { if (it != null) setting.value = it.asFloat }
                    is DoubleSetting -> map[setting.name].let { if (it != null) setting.value = it.asDouble }
                    is StringSetting -> map[setting.name].let { if (it != null) setting.value = it.asString }
                    is EnumSetting -> map[setting.name].let { if (it != null) setting.setWithName(it.asString) }
                    is ColorSetting -> map[setting.name].let { if (it != null) setting.value = ColorRGB(it.asInt) }
                }
            }
            if (module is HUDModule) {
                module.hudFrame.x = map["HUD_X"]?.asInt!!
                module.hudFrame.y = map["HUD_Y"]?.asInt!!
            }

        } else saveConfig()
    }

}