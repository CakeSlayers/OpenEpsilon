package studio.coni.epsilon.util.graphics.render.render

import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.graphics.font.renderer.IFontRenderer
import studio.coni.epsilon.util.graphics.font.renderer.MainFontRenderer
import studio.coni.epsilon.util.graphics.render.RenderTask

class TextRenderTask(
    private val text: String,
    private val x: Float,
    private val y: Float,
    private val colorRGB: ColorRGB,
    private val scale: Float = 1.0F,
    private val isShadow: Boolean = true,
    private val font: IFontRenderer = MainFontRenderer
) : RenderTask {

    override fun onRender() {
        if (isShadow) font.drawStringWithShadow(text, x, y, colorRGB, scale)
        else font.drawString(text, x, y, colorRGB, scale)
    }

}
