package studio.coni.epsilon.hud.info

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.hud.HUDModule
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.util.Timer
import studio.coni.epsilon.util.graphics.font.renderer.MainFontRenderer
import studio.coni.epsilon.util.onPacketReceive

object LagNotification : HUDModule(
    name = "LagNotification",
    category = Category.InfoHUD,
    description = "Notify you when server is no responding"
) {

    val timeOut by setting("TimeOut Sceound", 2f, 0.1f..10f, 0.1f)

    val timer = Timer()

    init {
        onPacketReceive {
            timer.reset()
        }
    }

    override fun onRender() {
        resize {
            width = 150
            height = (MainFontRenderer.getHeight() + 1).toInt()
        }
        val seconds = (System.currentTimeMillis() - timer.time).toFloat() / 1000.0f % 60.0f
        if (seconds >= timeOut) {
            MainFontRenderer.drawString("Server no responding " + String.format("%.1f", seconds) + " seconds.", x.toFloat(), y.toFloat(), GUIManager.firstColor)
        }
    }
}