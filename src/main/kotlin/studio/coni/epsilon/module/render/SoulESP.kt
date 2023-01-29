package studio.coni.epsilon.module.render

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.RenderEntityEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.management.CombatManager
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.module.setting.CombatSetting
import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.graphics.AnimationUtil
import studio.coni.epsilon.util.graphics.GlStateUtils
import studio.coni.epsilon.util.graphics.shaders.GLSLSandbox
import studio.coni.epsilon.util.graphics.shaders.use
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

object SoulESP : Module(
    name = "SoulESP",
    category = Category.Render,
    description = "cnmb nmsl"
) {

    private val chamsColor by setting("Chams Color", ColorRGB(255, 32, 255))

    private val alphaCache = mutableMapOf<EntityPlayer, Float>()

    val shader = GLSLSandbox("/assets/minecraft/shaders/menu/PurpleNoise.fsh")

    init {
        listener<RenderEntityEvent.Model.Pre> {
            if (it.cancelled || it.entity !is EntityPlayer) return@listener

            GL11.glDepthRange(0.0, 0.01)

            var currentAlpha = alphaCache.getOrPut(it.entity) {
                if (it.entity.isDead) 0f else 0.5f
            }
            if (it.entity.isDead) currentAlpha -= 0.01f else currentAlpha += 0.01f
            currentAlpha = currentAlpha.coerceIn(0f..0.5f)
            alphaCache[it.entity] = currentAlpha
            GL11.glColor4f(chamsColor.rFloat, chamsColor.gFloat, chamsColor.bFloat, currentAlpha)

            GlStateUtils.texture2d(false)
            GlStateUtils.lighting(false)
            GlStateUtils.blend(true)
            GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
            )

            val width = mc.displayWidth.toFloat()
            val height = mc.displayHeight.toFloat()
            val mouseX = Mouse.getX() - 1.0f
            val mouseY = height - Mouse.getY() - 1.0f

            shader.bind()
            GL20.glUniform2f(shader.resolutionUniform, width, height)
            GL20.glUniform2f(shader.mouseUniform, mouseX / width, (height - 1.0f - mouseY) / height)
            GL20.glUniform1f(shader.timeUniform, ((System.currentTimeMillis() - 0x22) / 1000.0).toFloat())
        }

        listener<RenderEntityEvent.Model.Post> {
            if (it.cancelled || it.entity !is EntityPlayer) return@listener

            GlStateUtils.useProgramForce(0)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateUtils.texture2d(true)
            GlStateUtils.lighting(true)
        }

        listener<RenderEntityEvent.All.Post> {
            if (!it.cancelled && it.entity is EntityPlayer) {
                GL11.glDepthRange(0.0, 1.0)
            }
        }
    }

}