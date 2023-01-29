package studio.coni.epsilon.gui.def

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.gui.IFloatAnimatable
import studio.coni.epsilon.gui.SpartanScreen
import studio.coni.epsilon.gui.def.components.IAnimatable
import studio.coni.epsilon.gui.def.components.Panel
import studio.coni.epsilon.gui.def.components.ParticleRenderer
import studio.coni.epsilon.gui.def.components.elements.ModuleButton
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.module.client.RootGUI
import studio.coni.epsilon.module.setting.GuiSetting
import studio.coni.epsilon.module.setting.MenuSetting
import studio.coni.epsilon.util.ScaleHelper
import studio.coni.epsilon.util.graphics.Easing
import studio.coni.epsilon.util.graphics.RenderUtils2D
import studio.coni.epsilon.util.graphics.shaders.WindowBlurShader
import studio.coni.epsilon.util.math.Vec2f
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11.glScalef
import org.lwjgl.opengl.GL11.glTranslatef


object DefaultRootScreen : SpartanScreen() {

    var openTime = 0L
    val panels = mutableListOf<Panel>()

    init {
        var startX = 5
        Category.values().forEach {
            if (it != Category.Hidden) {
                if (!it.isHUD) {
                    panels.add(Panel(it, startX, 5, 100, 15, false))
                    startX += 105
                }
            }
        }
    }

    override fun onUpdate(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (GuiSetting.moving) {
            Vec2f(
                (((ScaleHelper.width - mouseX) / ScaleHelper.width.toFloat() - 0.5f) * (ScaleHelper.width * GuiSetting.movingRate)),
                (((ScaleHelper.height - mouseY) / ScaleHelper.height.toFloat() - 0.5f) * (ScaleHelper.height * GuiSetting.movingRate)),
            )
        } else {
            Vec2f.ZERO
        }.apply {
            panels.reversed().forEach {
                it.translate(x.toInt(), y.toInt())
                it.onRender(mouseX, mouseY, partialTicks)
            }
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (mc.player == null) MenuSetting.drawBackground(mouseX, mouseY, this)
        if (GUIManager.isBlur) WindowBlurShader.render(width.toFloat(), height.toFloat())

        if (GUIManager.isShadow) {
            RenderUtils2D.drawGradientRect(
                0f, 0f, width.toFloat(), height.toFloat(),
                GUIManager.firstColor.alpha(0),
                GUIManager.firstColor.alpha(0),
                GUIManager.firstColor.alpha(128),
                GUIManager.firstColor.alpha(128)
            )
        }
        if (GUIManager.isParticle) {
            ParticleRenderer.tick(10)
            ParticleRenderer.render()
        }
        mouseDrag(mouseX, mouseY)
        zoomAnimation()
        if (!GuiSetting.asyncGUI) AsyncRenderEngine.update(mouseX, mouseY, partialTicks)

        AsyncRenderEngine.render()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        AsyncRenderEngine.currentSBBOX?.let {
            if (it.onMouseClicked(mouseX, mouseY, mouseButton)) return
        }
        for (panel in panels) {
            if (panel.onMouseClicked(mouseX, mouseY, mouseButton)) {
                AsyncRenderEngine.clearSBBox()
                return
            }
            if (!panel.isActive) continue
            for (part in panel.children) {
                if (part.onMouseClicked(mouseX, mouseY, mouseButton)) {
                    AsyncRenderEngine.clearSBBox()
                    return
                }
                if (!(part as ModuleButton).isActive) continue
                for (component in part.children) {
                    if (!component.isVisible()) continue
                    if (component.onMouseClicked(mouseX, mouseY, mouseButton)) {
                        AsyncRenderEngine.clearSBBox()
                        return
                    }
                }
            }
        }
        AsyncRenderEngine.clearSBBox()
    }

    override fun onGuiClosed() {
        //reset animation
        panels.forEach { panel ->
            panel.children.forEach {
                if (it is ModuleButton) {
                    for (component in it.children) {
                        if (component is IFloatAnimatable) {
                            component.currentValue = 0f
                        }
                        if (component is IAnimatable) {
                            component.animatedAlphaUnit.currentValue = 0F
                        }
                    }
                }
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            RootGUI.disable(notification = false)
            return
        }
        panels.forEach {
            if (it.keyTyped(typedChar, keyCode)) return
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        panels.forEach {
            it.onMouseReleased(mouseX, mouseY, state)
        }
    }

    private fun mouseDrag(mouseX: Int, mouseY: Int) {
        val dWheel = Mouse.getDWheel()
        if (dWheel != 0) {
            val hooveredPanel = panels.firstOrNull { it.hooveredInDrawnPanel(mouseX, mouseY) }
            if (hooveredPanel != null) {
                hooveredPanel.targetOffsetY = hooveredPanel.targetOffsetY + dWheel / 3
            } else {
                if (dWheel < 0) {
                    panels.forEach { component: Panel -> component.y -= 10 }
                } else if (dWheel > 0) {
                    panels.forEach { component: Panel -> component.y += 10 }
                }
            }
        }
    }

    var zoomScale = 1f

    private fun zoomAnimation() {
        val scale = Easing.OUT_BACK.dec(Easing.toDelta(openTime, 300L), 1.0f, 1.25f).also { zoomScale = it }
        val resolution = ScaledResolution(mc)
        val centerX = (resolution.scaledWidth_double * 0.5f).toFloat()
        val centerY = (resolution.scaledHeight_double * 0.5f).toFloat()
        glTranslatef(centerX, centerY, 0.0f)
        glScalef(scale, scale, 1.0f)
        glTranslatef(-centerX, -centerY, 0.0f)
    }


}