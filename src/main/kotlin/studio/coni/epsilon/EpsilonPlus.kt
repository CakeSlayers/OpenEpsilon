package studio.coni.epsilon

import studio.coni.epsilon.config.ConfigManager
import studio.coni.epsilon.event.EventBus
import studio.coni.epsilon.event.ForgeAccessor
import studio.coni.epsilon.gui.def.AsyncRenderEngine
import studio.coni.epsilon.gui.def.ThemeContainer
import studio.coni.epsilon.management.*
import studio.coni.epsilon.module.client.HUDEditor
import studio.coni.epsilon.module.client.RootGUI
import studio.coni.epsilon.notification.NotificationManager
import studio.coni.epsilon.util.Logger
import studio.coni.epsilon.util.ScaleHelper
import studio.coni.epsilon.util.TpsCalculator
import studio.coni.epsilon.util.graphics.ProjectionUtils
import studio.coni.epsilon.util.graphics.RenderUtils3D
import studio.coni.epsilon.util.graphics.ResolutionHelper
import studio.coni.epsilon.util.graphics.font.renderer.IconRenderer
import studio.coni.epsilon.util.graphics.font.renderer.MainFontRenderer
import studio.coni.epsilon.util.graphics.shaders.WindowBlurShader
import org.lwjgl.opengl.Display

object EpsilonPlus {

    const val MOD_NAME = "Epsilon+"
    const val MOD_ID = "epsilon"
    const val VERSION = "4.1u230128"

    const val INFO = "$MOD_NAME Build $VERSION - Epsilon Plus"

    const val DEFAULT_COMMAND_PREFIX = "."
    const val DEFAULT_CONFIG_PATH = "EpsilonPlus/"

    val mainThread: Thread = Thread.currentThread().also {
        it.priority = Thread.MAX_PRIORITY
    }

    var isReady = false


    fun preInit() {
        Logger.info("Pre initializing EpsilonPlus")
        Display.setTitle("$MOD_NAME $VERSION")
        ModuleManager
        CommandManager
//        TextManager.readText()
//        TextManager.setText()
        MainFontRenderer
        IconRenderer
        Fonts
    }

    fun postInit() {
        Logger.info("Post initializing EpsilonPlus")
        ConfigManager.loadAll(true)
        RootGUI.disable(notification = false, silent = true)
        HUDEditor.disable(notification = false, silent = true)
        ThemeContainer

        register(WindowBlurShader)
        register(SpartanCore)
        register(TpsCalculator)
        register(ResolutionHelper)
        register(ScaleHelper)
        register(ProjectionUtils)
        register(RenderUtils3D)

        register(ChatMessageManager)
        register(CombatManager)
        register(CommandManager)
        register(DisplayManager)
        register(EntityManager)
        register(FriendManager)
        register(GUIManager)
        register(HoleManager)
        register(HotbarManager)
        register(InputManager)
        register(InventoryTaskManager)
        register(PlayerPacketManager)
        register(ModuleManager)
        register(NotificationManager)
        register(TimerManager)
        register(TotemPopManager)

        ForgeAccessor.subscribe()

        DisplayManager.setIcon(DisplayManager.Icon.Epsilon)

        AsyncRenderEngine.init()

        isReady = true
    }

    private fun register(obj: Any) {
        EventBus.subscribe(obj)
    }

}