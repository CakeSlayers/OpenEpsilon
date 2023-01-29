package studio.coni.epsilon.util.graphics.render.render

import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.graphics.RenderUtils2D
import studio.coni.epsilon.util.graphics.VertexHelper
import studio.coni.epsilon.util.graphics.render.RenderTask
import org.lwjgl.opengl.GL11.GL_LINES

class LineRenderTask(
    private val startX: Float,
    private val startY: Float,
    private val endX: Float,
    private val endY: Float,
    private val color: ColorRGB,
    private val color2: ColorRGB = color,
) : RenderTask {

    override fun onRender() {
        RenderUtils2D.prepareGl()

        VertexHelper.begin(GL_LINES)

        VertexHelper.put(startX.toDouble(), startY.toDouble(), color)
        VertexHelper.put(endX.toDouble(), endY.toDouble(), color2)

        VertexHelper.end()

        RenderUtils2D.releaseGl()
    }

}