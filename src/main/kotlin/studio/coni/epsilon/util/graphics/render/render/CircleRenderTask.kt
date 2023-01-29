package studio.coni.epsilon.util.graphics.render.render

import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.graphics.RenderUtils2D
import studio.coni.epsilon.util.graphics.render.RenderTask

class CircleRenderTask(
    private val x: Float,
    private val y: Float,
    private val r: Float,
    private val color: ColorRGB,
    private val filled: Boolean = true
) : RenderTask {

    override fun onRender() {
        if (filled) RenderUtils2D.drawFilledCircle(x.toDouble(), y.toDouble(), r.toDouble(), color)
        else RenderUtils2D.drawOutlineCircle(x, y, r, color)
    }

}