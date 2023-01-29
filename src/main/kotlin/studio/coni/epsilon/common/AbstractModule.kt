package studio.coni.epsilon.common

import studio.coni.epsilon.common.extensions.notAtValue
import studio.coni.epsilon.common.interfaces.Alias
import studio.coni.epsilon.common.interfaces.Helper
import studio.coni.epsilon.common.interfaces.Nameable
import studio.coni.epsilon.common.key.KeyBind
import studio.coni.epsilon.config.ConfigManager
import studio.coni.epsilon.config.ModuleConfig
import studio.coni.epsilon.event.decentralized.IDecentralizedEvent
import studio.coni.epsilon.event.decentralized.Listenable
import studio.coni.epsilon.language.InnerLanguage
import studio.coni.epsilon.language.TextUnit
import studio.coni.epsilon.management.SpartanCore
import studio.coni.epsilon.module.client.HUDEditor
import studio.coni.epsilon.module.client.RootGUI
import studio.coni.epsilon.notification.Notification
import studio.coni.epsilon.notification.NotificationManager
import studio.coni.epsilon.notification.NotificationType
import studio.coni.epsilon.setting.AbstractSetting
import studio.coni.epsilon.setting.SettingRegister
import studio.coni.epsilon.util.IDRegistry
import java.util.*

@Suppress("LeakingThis")
abstract class AbstractModule(
    override var name: String,
    override val alias: Array<String> = emptyArray(),
    val category: Category,
    description: String,
    val priority: Int,
    keyCode: Int,
    visibility: Boolean,
) : Nameable, Alias, Listenable, SettingRegister<AbstractModule>, Helper, Comparable<AbstractModule> {

    override val subscribedListener = ArrayList<Triple<IDecentralizedEvent<*>, (Any) -> Unit, Int>>()

    var currentConfig = "Default"

    val description = TextUnit("module_" + name.lowercase(Locale.getDefault()).replace(" ", "_"), description)
    val keyBind: KeyBind = KeyBind(keyCode, action = { toggle() })
    var isEnabled = false
    val isDisabled get() = !isEnabled
    var toggleTime = System.currentTimeMillis()
    private val keyListeners = mutableListOf<(Int) -> Unit>()

    val id = idRegistry.register()

    override fun compareTo(other: AbstractModule): Int {
        val result = this.priority.compareTo(other.priority)
        if (result != 0) return result
        return this.id.compareTo(other.id)
    }

    open fun isActive(): Boolean {
        return isEnabled
    }

    val config = ModuleConfig(this).also {
        ConfigManager.register(it)
    }

    enum class Visibility {
        ON, OFF
    }

    val reset: () -> Unit = {
        if (category != Category.Setting && this != RootGUI && this != HUDEditor) disable(notification = false)
        config.configs.forEach {
            it.reset()
        }
    }

    val visibilitySetting by setting(
        "Visibility",
        if (!visibility || category == Category.Setting) Visibility.OFF else Visibility.ON,
        "Determine whether the module should be displayed on array",
        category.notAtValue(Category.Setting)
    )

    private val keyBindSetting by setting(
        "Bind",
        keyBind,
        "Bind a key to toggle this module",
        category.notAtValue(Category.Setting)
    )

    private val resetSetting by setting("Reset", reset, "Click here to reset this module")

    fun ch(description: String): AbstractModule {
        return des(InnerLanguage.Chinese, description)
    }

    fun jp(description: String): AbstractModule {
        return des(InnerLanguage.Japanese, description)
    }

    fun ru(description: String): AbstractModule {
        return des(InnerLanguage.Russian, description)
    }

    private fun des(language: InnerLanguage, description: String): AbstractModule {
        this.description.add(language, description)
        return this
    }

    fun saveConfig() {
        config.saveConfig()
        NotificationManager.show(
            Notification(
                message = "Saved config for module ${this.name}",
                type = NotificationType.DEBUG
            )
        )
    }

    fun loadConfig() {
        config.loadConfig()
        NotificationManager.show(
            Notification(
                message = "Loaded config for module ${this.name}",
                type = NotificationType.DEBUG
            )
        )
    }

    open fun onEnable() {
    }

    open fun onDisable() {
    }

    fun enable(notification: Boolean = true, silent: Boolean = false) {
        toggleTime = System.currentTimeMillis()
        if (category == Category.Setting) {
            if (notification) NotificationManager.show(
                Notification(
                    message = "You aren't allowed to enable an always disabled setting modules.",
                    type = NotificationType.WARNING
                )
            )
            return
        }
        if (notification) NotificationManager.show(
            Notification(
                module = this,
                message = this.name + " is Enabled",
                type = NotificationType.MODULE
            )
        )
        isEnabled = true
        SpartanCore.register(this)
        if (!silent) onEnable()
    }

    fun disable(notification: Boolean = true, silent: Boolean = false) {
        toggleTime = System.currentTimeMillis()
        if (notification) NotificationManager.show(
            Notification(
                module = this,
                message = this.name + " is Disabled",
                type = NotificationType.MODULE
            )
        )

        isEnabled = false
        SpartanCore.unregister(this)
        if (!silent) onDisable()
    }

    abstract fun onAsyncUpdate(block: () -> Unit)

    fun toggle() {
        if (isEnabled) disable()
        else enable()
    }

    open fun getHudInfo(): String? {
        return null
    }

    override fun <S : AbstractSetting<*>> AbstractModule.setting(setting: S): S {
        SpartanCore.registerSetting(setting)
        this.config.configs.add(setting)
        return setting
    }

    protected companion object {
        val idRegistry = IDRegistry()
    }

}