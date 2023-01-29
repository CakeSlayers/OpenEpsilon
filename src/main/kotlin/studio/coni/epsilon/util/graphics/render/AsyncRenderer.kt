package studio.coni.epsilon.util.graphics.render

import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.hud.HUDModule
import studio.coni.epsilon.management.SpartanCore.addAsyncUpdateListener
import studio.coni.epsilon.util.Wrapper.mc
import studio.coni.epsilon.util.onRender2D

@Suppress("NOTHING_TO_INLINE")
open class AsyncRenderer(private val onUpdate: AsyncRenderer.() -> Unit) : RawAsyncRenderer() {

    override fun update() {
        tempTasks.clear()
        onUpdate.invoke(this)
        synchronized(tasks) {
            tasks.clear()
            tasks.addAll(tempTasks)
        }
    }

}

fun AbstractModule.asyncRender(
    noRender2D: Boolean = this is HUDModule,
    onUpdate: AsyncRenderer.() -> Unit
): AsyncRenderer =
    asyncRendererOf(onUpdate).also { renderer ->
        onAsyncUpdate {
            if (mc.player == null || mc.world == null) return@onAsyncUpdate
            renderer.update()
        }
        if (!noRender2D) onRender2D {
            renderer.render()
        }
    }

fun Any.asyncRender(
    noRender2D: Boolean = this is HUDModule,
    onUpdate: AsyncRenderer.() -> Unit
): AsyncRenderer =
    asyncRendererOf(onUpdate).also { renderer ->
        addAsyncUpdateListener {
            if (mc.player == null || mc.world == null) return@addAsyncUpdateListener
            renderer.update()
        }
        if (!noRender2D) onRender2D {
            renderer.render()
        }
    }

fun asyncRendererOf(onUpdate: AsyncRenderer.() -> Unit): AsyncRenderer = AsyncRenderer(onUpdate)