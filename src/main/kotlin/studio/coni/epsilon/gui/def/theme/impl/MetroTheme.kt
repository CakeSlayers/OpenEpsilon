package studio.coni.epsilon.gui.def.theme.impl

import studio.coni.epsilon.gui.IDescriptorContainer
import studio.coni.epsilon.gui.IFatherExtendable
import studio.coni.epsilon.gui.SpartanScreen
import studio.coni.epsilon.gui.def.AsyncRenderEngine.draw
import studio.coni.epsilon.gui.def.AsyncRenderEngine.drawRect
import studio.coni.epsilon.gui.def.AsyncRenderEngine.drawString
import studio.coni.epsilon.gui.def.AsyncRenderEngine.inArea
import studio.coni.epsilon.gui.def.DefaultRootScreen
import studio.coni.epsilon.gui.def.components.AbstractElement
import studio.coni.epsilon.gui.def.components.Panel
import studio.coni.epsilon.gui.def.components.Scale
import studio.coni.epsilon.gui.def.components.elements.other.SBBox
import studio.coni.epsilon.gui.def.theme.IDefaultBothTheme
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.module.setting.GuiSetting
import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.ColorUtils
import studio.coni.epsilon.util.ScaleHelper
import studio.coni.epsilon.util.Utils.last
import studio.coni.epsilon.util.Utils.next
import studio.coni.epsilon.util.graphics.AnimationUtil
import studio.coni.epsilon.util.graphics.GlStateUtils
import studio.coni.epsilon.util.graphics.RenderUtils2D
import studio.coni.epsilon.util.graphics.VertexHelper
import studio.coni.epsilon.util.graphics.font.renderer.IconRenderer
import studio.coni.epsilon.util.graphics.font.renderer.MainFontRenderer
import studio.coni.epsilon.util.math.Vec2d
import studio.coni.epsilon.util.math.Vec2f
import studio.coni.epsilon.gui.def.components.elements.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object MetroTheme : IDefaultBothTheme, SpartanScreen() {

    override val name = "Metro"
    val backgroundColor = ColorRGB(41, 41, 41, 255)
    val dark = ColorRGB(29, 29, 29, 255)
    val black = ColorRGB(15, 15, 15, 255)
    private val white = ColorRGB(255, 255, 255, 255)
    private val gray = ColorRGB(160, 160, 160, 255)
    private val shadow = ColorRGB(110, 110, 110, 255)

    private fun SBBox.drawAlphaRect() {
        drawRect(
            alphaStart.x,
            alphaStart.y,
            alphaEnd.x,
            alphaEnd.y,
            setting.value.alpha(255),
            setting.value.alpha(255),
            setting.value.alpha(0),
            setting.value.alpha(0)
        )
        val pointerPos = Vec2f(
            alphaStart.x,
            alphaStart.y + (1f - (setting.value.a / 255f)) * 64f
        )

        draw {
            RenderUtils2D.drawBorderedRect(pointerPos.x, pointerPos.y, pointerPos.x + 10, pointerPos.y + 1, 1.5f, ColorRGB(0, 0, 0), white)
        }
    }

    private fun SBBox.drawAlphaBackground() {
        val startX = alphaStart.x
        var startY = alphaStart.y

        for (index in 1..12) {
            if (index % 2 == 1) {
                drawRect(startX, startY, startX + 5, startY + 5, white)
                drawRect(startX + 5, startY, startX + 10, startY + 5, ColorRGB(222, 222, 222, 255))
            } else {
                drawRect(startX, startY, startX + 5, startY + 5, ColorRGB(222, 222, 222, 255))
                drawRect(startX + 5, startY, startX + 10, startY + 5, white)
            }
            startY += 5
        }

        drawRect(startX, startY, startX + 5, startY + 4, white)
        drawRect(startX + 5, startY, startX + 10, startY + 4, ColorRGB(222, 222, 222, 255))
    }

    private fun SBBox.drawColorField() {
        draw {
            //RenderUtils2D.prepareGl()

            GlStateUtils.texture2d(false)
            GlStateUtils.blend(true)
            GlStateUtils.alpha(false)
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            )
            GlStateUtils.smooth(true)

            // Saturation
            val rightColor = ColorUtils.hsbToRGB(setting.value.hue, 1.0f, 1.0f, 1.0f)
            val leftColor = ColorRGB(255, 255, 255)

            VertexHelper.begin(GL11.GL_TRIANGLE_STRIP)
            VertexHelper.put(fieldStart.toVec2d(), leftColor) // Top left
            VertexHelper.put(Vec2f(fieldStart.x, fieldEnd.y).toVec2d(), leftColor) // Bottom left
            VertexHelper.put(Vec2f(fieldEnd.x, fieldStart.y).toVec2d(), rightColor) // Top right
            VertexHelper.put(fieldEnd.toVec2d(), rightColor) // Bottom right
            VertexHelper.end()

            // Brightness
            val topColor = ColorRGB(0, 0, 0, 0)
            val bottomColor = ColorRGB(0, 0, 0, 255)
            VertexHelper.begin(GL11.GL_TRIANGLE_STRIP)
            VertexHelper.put(fieldStart.toVec2d(), topColor) // Top left
            VertexHelper.put(Vec2d(fieldStart.x, fieldEnd.y), bottomColor) // Bottom left
            VertexHelper.put(Vec2d(fieldEnd.x, fieldStart.y), topColor) // Top right
            VertexHelper.put(fieldEnd.toVec2d(), bottomColor) // Bottom right
            VertexHelper.end()

            GlStateUtils.blend(false)
            GlStateUtils.alpha(true)
            GlStateUtils.texture2d(true)

            //RenderUtils2D.releaseGl()

            // Circle pointer
            val relativeBrightness =
                ((1.0f - (1.0f - setting.value.saturation) * setting.value.brightness) * 255.0f).toInt()
            val circleColor = ColorRGB(relativeBrightness, relativeBrightness, relativeBrightness)
            val circlePos = Vec2f(
                this.fieldStart.x + 64f * setting.value.saturation,
                this.fieldStart.y + 64f * (1.0f - setting.value.brightness)
            )
            RenderUtils2D.drawCircleOutline(circlePos, 4.0f, 32, 1.5f, circleColor)
        }
    }

    override fun saturationBrightnessBox(box: SBBox, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(box) {
            //Background
            animatedWidth = AnimationUtil.animate(width.toFloat(), animatedWidth, 0.2f)
            animatedHeight = AnimationUtil.animate(height.toFloat(), animatedHeight, 0.2f)

            draw {
                GlStateManager.enableDepth()
                GlStateManager.depthMask(true)
                GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
                GlStateManager.depthFunc(GL11.GL_ALWAYS)
            }

            drawRect(x.toFloat(), y.toFloat(), x + animatedWidth, y + animatedHeight, backgroundColor)

            draw {
                GlStateManager.depthFunc(GL11.GL_EQUAL)
            }
            //Filed
            drawColorField()

            //Alpha
            drawAlphaBackground()
            drawAlphaRect()

            draw {
                GlStateManager.depthFunc(GL11.GL_LEQUAL)
            }
        }
    }

    override fun actionButton(actionButton: ActionButton, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(actionButton) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)
            //String
            drawString(
                setting.name,
                x + 3f,
                y + height / 2 - MainFontRenderer.getHeight(0.9f) / 2f,
                white,
                scale = 0.9f
            )

        }
    }

    override fun bindButton(bindButton: BindButton, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(bindButton) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)
            //String

            val color = if (GuiSetting.dynamicRainbow.value && GuiSetting.rainbow.value)
                ColorUtils.rainbowRGB(y * -5, GuiSetting.saturation.value, GuiSetting.brightness.value)
            else
                GUIManager.firstColor

            drawString(
                "Bind " + getKey().replace("CONTROL", "CTRL", true).replace("MENU", "ALT", true),
                x + 3f,
                y + height / 2 - MainFontRenderer.getHeight(0.9f) / 2f,
                colorRGB = if (accepting) color else white,
                scale = 0.9f
            )
        }
    }

    override fun booleanButton(booleanButton: BooleanButton, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(booleanButton) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)

            drawString(
                setting.name,
                x + 15f + 6.5f,
                y + height / 2 - MainFontRenderer.getHeight(0.9f) / 2f,
                scale = 0.9f,
                colorRGB = white
            )

            //Color
            val color = if (GuiSetting.dynamicRainbow.value && GuiSetting.rainbow.value)
                ColorUtils.rainbowRGB(y * -5, GuiSetting.saturation.value, GuiSetting.brightness.value)
            else
                GUIManager.firstColor

            val hsbColor = if (setting.value) color.toHSB()
            else gray.toHSB()
            booleanButton.currentValue =
                AnimationUtil.animate(if (setting.value) 8.0 else 0.0, booleanButton.currentValue.toDouble(), 0.2)
                    .toFloat()

            //Sync
            val x = this.x
            val y = this.y
            draw {
                RenderUtils2D.drawRoundedRectangle(
                    x.toDouble() + 3,
                    y.toDouble() + height / 2.0 - 3,
                    15.0,
                    6.8,
                    3.0,
                    ColorUtils.hsbToRGB(hsbColor.h, hsbColor.s, hsbColor.b - 0.3f)
                )
                RenderUtils2D.drawFilledCircle(
                    x.toDouble() + 6.3 + booleanButton.currentValue,
                    y + height / 2.0 + 0.4,
                    4.4,
                    ColorUtils.hsbToRGB(hsbColor.h, hsbColor.s, hsbColor.b - 0.1f)
                )
            }
        }
    }

    override fun colorPicker(colorPicker: ColorPicker, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(colorPicker) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)

            val startX = x + 3f
            val currentHsb = setting.value.toHSB()
            val availableLength = width - 6
            if (this.sliding) {
                setting.value =
                    ColorUtils.hsbToRGB(
                        MathHelper.clamp((mouseX - (startX)) / availableLength, 0f, 0.99f),
                        setting.value.saturation,
                        setting.value.brightness
                    ).alpha(setting.value.a)
            }
            currentValue = AnimationUtil.animate(currentHsb.h, currentValue, 0.1f)
            val currentSliderPos = availableLength * currentValue

            //Color
            //RenderUtils2D.drawHsbColoredRect(startX, y + 6f, x + width - 3f, y + height.toFloat() - 1)

            val step = ((x + width - 3f) - startX) / 6f

            val color1 = ColorRGB(255, 0, 0) // 0.0
            val color2 = ColorRGB(255, 255, 0) // 0.1666
            val color3 = ColorRGB(0, 255, 0) // 0.3333
            val color4 = ColorRGB(0, 255, 255) // 0.5
            val color5 = ColorRGB(0, 0, 255) // 0.6666
            val color6 = ColorRGB(255, 0, 255) // 0.8333

            //1
            drawRect(
                startX,
                y + 6f,
                startX + step,
                y + height.toFloat() - 1,
                color2,
                color1,
                color1,
                color2
            )

            //2
            drawRect(
                startX + step,
                y + 6f,
                startX + step * 2,
                y + height.toFloat() - 1,
                color3,
                color2,
                color2,
                color3
            )

            //3
            drawRect(
                startX + step * 2,
                y + 6f,
                startX + step * 3,
                y + height.toFloat() - 1,
                color4,
                color3,
                color3,
                color4
            )

            //4
            drawRect(
                startX + step * 3,
                y + 6f,
                startX + step * 4,
                y + height.toFloat() - 1,
                color5,
                color4,
                color4,
                color5
            )

            //5
            drawRect(
                startX + step * 4,
                y + 6f,
                startX + step * 5,
                y + height.toFloat() - 1,
                color6,
                color5,
                color5,
                color6
            )

            //6
            drawRect(
                startX + step * 5,
                y + 6f,
                startX + step * 6,
                y + height.toFloat() - 1,
                color1,
                color6,
                color6,
                color1
            )

            //Sync
            val x = this.x
            val y = this.y

            draw {
                RenderUtils2D.drawRectFilled(
                    startX + currentSliderPos,
                    y + 6f,
                    startX + currentSliderPos + 1f,
                    y + height.toFloat() - 1,
                    backgroundColor
                )
            }

            drawString(
                setting.name,
                x + 3f,
                y + height / 2 - MainFontRenderer.getHeight(0.6f) / 2f - 5f,
                scale = 0.6f,
                colorRGB = white
            )
        }
    }

    override fun <T : Enum<T>> enumButton(enumButton: EnumButton<T>, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(enumButton) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)
            if (isHoovered(mouseX, mouseY)) {
                val nameWidth = MainFontRenderer.getWidth(setting.value.name)
                val lastName = setting.value.last().name
                val nextName = setting.value.next().name
                //pushMatrix()
                val cachedX = x
                val cachedY = y
                draw {
                    RenderUtils2D.glScissor(
                        cachedX,
                        cachedY,
                        cachedX + width,
                        cachedY + height,
                        ScaleHelper.scaledResolution
                    )
                    GL11.glEnable(GL11.GL_SCISSOR_TEST)
                }
                drawString(
                    setting.value.name,
                    x + width / 2 - nameWidth / 2,
                    y + height / 2 - MainFontRenderer.getHeight() / 2,
                    colorRGB = white
                )
                drawString(
                    "$nextName   ",
                    x + width / 2 - nameWidth / 2 - MainFontRenderer.getWidth("$nextName   ", 0.8f),
                    y + height / 2 - MainFontRenderer.getHeight(0.8f) / 2,
                    colorRGB = white.alpha((white.a / 1.5).toInt()),
                    scale = 0.8f
                )
                drawString(
                    lastName,
                    x + width / 2 + nameWidth / 2 + MainFontRenderer.getWidth("   ", 0.8f),
                    y + height / 2 - MainFontRenderer.getHeight(0.8f) / 2,
                    colorRGB = white.alpha((white.a / 1.5).toInt()),
                    scale = 0.8f
                )
                draw {
                    GL11.glDisable(GL11.GL_SCISSOR_TEST)
                }
                //popMatrix()
            } else {
                //Name
                drawString(
                    setting.name,
                    x + 3f,
                    y + height / 2 - MainFontRenderer.getHeight(0.9f) / 2f,
                    scale = 0.9f,
                    colorRGB = white
                )
                drawString(
                    setting.value.name,
                    x + width - 3 - MainFontRenderer.getWidth(setting.value.name, 0.9f),
                    y + height / 2 - MainFontRenderer.getHeight(0.9f) / 2f,
                    scale = 0.9f,
                    colorRGB = white.alpha((white.a / 1.5).toInt())
                )
            }
        }
    }

    override fun moduleButton(moduleButton: ModuleButton, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(moduleButton) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)

            currentValue = AnimationUtil.animate(if (module.isEnabled) 255F else 0F, currentValue, 0.2F)

            val color = if (GuiSetting.dynamicRainbow.value && GuiSetting.rainbow.value)
                ColorUtils.rainbowRGB(y * -5, GuiSetting.saturation.value, GuiSetting.brightness.value)
            else
                GUIManager.firstColor

            drawRect(
                x.toFloat(),
                y.toFloat(),
                x + width.toFloat(),
                y + height.toFloat(),
                color.alpha(currentValue.toInt())
            )


            if (children.size > 3) {
                //Sync field
                val x = this.x
                val y = this.y
                draw {
                    RenderUtils2D.drawRectFilled(x + width.toFloat(), y.toFloat(), x + width - IconRenderer.getWidth("7", 1.5f) - 0.5f, y + height.toFloat(), dark)
                    IconRenderer.drawCenteredString("7", x + width.toFloat() - IconRenderer.getWidth("7", 0.8f), y.toFloat() + height / 2f - IconRenderer.getWidth("7", 1.5f) / 2f, white, 1.2f, false)
                }
            }

            drawString(
                module.name,
                x + 3f,
                y + height / 2 - MainFontRenderer.getHeight() / 2f,
                if (module.isEnabled) backgroundColor else white
            )

            // RenderUtils2D.drawLine(Vec2d(x.toDouble() + 3, y.toDouble() + 3), Vec2d(x.toDouble() + 6, y.toDouble()), 10f, white)

        }
    }

    override fun <T> numberSlider(
        numberSlider: NumberSlider<T>,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float
    ) where T : Comparable<T>, T : Number {
        with(numberSlider) {
            //Background
            renderBackground()
            if (!setting.isVisible) sliding = false

            val startX = x + 3
            val availableLength = width - 24

            if (this.sliding) {
                setting.setByPercent(MathHelper.clamp((mouseX - (startX)) / availableLength.toFloat(), 0f, 1f))
            }

            val color = if (GuiSetting.dynamicRainbow.value && GuiSetting.rainbow.value)
                ColorUtils.rainbowRGB(y * -5, GuiSetting.saturation.value, GuiSetting.brightness.value)
            else
                GUIManager.firstColor

            val hsbColor = color.toHSB()

            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)
            //All
            drawRect(startX.toFloat(), y + height - 4f, startX + availableLength.toFloat(), y + height - 2f, shadow)
            //Current
            val endWidth = availableLength * setting.getPercentBar()

            if (System.currentTimeMillis() - lastUpdateTime >= 17) {
                lastUpdateTime = System.currentTimeMillis()
                currentValue = AnimationUtil.animate(endWidth, currentValue, 0.2f)
            }

            drawRect(
                startX.toFloat(),
                y + height - 4f,
                startX + currentValue,
                y + height - 2f,
                color
            )
            val y = this.y
            draw {
                //Circle
                RenderUtils2D.drawFilledCircle(
                    startX + currentValue.toDouble(),
                    y + height - 3.5,
                    3.0,
                    ColorUtils.hsbToRGB(hsbColor.h, hsbColor.s, hsbColor.b - 0.2f)
                )
            }
            drawString(
                setting.name,
                startX.toFloat(),
                y + (height / 2 - MainFontRenderer.getHeight() / 2f) * 0.65f,
                scale = 0.65f,
                colorRGB = white
            )
            drawString(
                setting.getDisplay(),
                x + width - 3 - MainFontRenderer.getWidth(setting.getDisplay(), 0.775f),
                y + height - MainFontRenderer.getHeight(0.775f) - 2,
                scale = 0.775f,
                colorRGB = white
            )
        }
    }

//    fun renderTooltips(x: Float, y: Float, display: String) {
//        val width = MainFontRenderer.getWidth(display, 0.775f)
//        drawRect(x - (width / 2.0f), y - MainFontRenderer.getHeight(0.775f) - 2, x + (width / 2.0f), y, gray.alpha(180))
//        RenderUtils2D.drawTriangleFilled(Vec2d(x.toDouble(), y + 4.0), Vec2d(x.toDouble() - 4, y.toDouble()), Vec2d(x.toDouble() + 4, y.toDouble()), gray.alpha(180))
//        MainFontRenderer.drawString(display, x - width / 2, y - 8, scale = 0.775f, color = white)
//    }

    override fun stringField(stringField: StringField, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(stringField) {
            //Background
            renderBackground()
            //Animation
            renderAnimation(mouseX, mouseY, partialTicks)

            drawString(
                setting.name,
                x + 3f,
                y + height / 2 - MainFontRenderer.getHeight(0.9f) / 2f,
                scale = 0.9f,
                colorRGB = if (editing) white else white
            )
        }
    }

    var startY = 0

    override fun panel(panel: Panel, mouseX: Int, mouseY: Int, partialTicks: Float) {
        with(panel) {
            //Title background
            drawRect(
                translatedX.toFloat(),
                translatedY.toFloat(),
                translatedX + width.toFloat(),
                translatedY + height.toFloat(),
                dark
            )
            //Title
            drawString(
                category.showName,
                translatedX + (width / 2f - MainFontRenderer.getWidth(category.showName) / 2f),
                translatedY + height / 2f - MainFontRenderer.getHeight() / 2f,
                white
            )

            if (category.iconCode.isNotEmpty()) draw {
                IconRenderer.drawString(category.iconCode, translatedX + (height / 2f - (IconRenderer.getWidth(category.iconCode, 1.8f) / 2f)), translatedY.toFloat() + (height / 2f - (IconRenderer.getHeight(1.8f) / 2f)), white, 1.8f, false)
            }

            startY = extendableStartY

            this.renderExtendable(mouseX, mouseY, partialTicks, this)

            //Sync
            val endY = this.endY
            val x = this.translatedX
            val y = this.translatedY

            if (endY >= upLimit) {
                draw {
                    studio.coni.epsilon.management.TextureManager.renderShadowRect(x, y, width, endY - y, 10)
                }
            }
            if (panel.isActive) {
                drawRect(
                    x.toFloat(),
                    y + height.toFloat(),
                    x + width.toFloat(),
                    y + height + 10f,
                    dark.alpha(100),
                    dark.alpha(100),
                    dark.alpha(0),
                    dark.alpha(0)
                )
                drawRect(
                    x.toFloat(),
                    endY - 10f,
                    x + width.toFloat(),
                    endY.toFloat(),
                    dark.alpha(0),
                    dark.alpha(0),
                    dark.alpha(100),
                    dark.alpha(100)
                )
//                drawRect(x.toFloat(), endY.toFloat(), x + width.toFloat(), endY + 8f, black)
//                val message = ModuleManager.getModulesByCategory(panel.category).size.toString() + " modules"
//                drawString(message, x.toFloat() + width / 2f - MainFontRenderer.getWidth(message) / 2f, endY.toFloat(), white, scale = 0.8f)
            }
        }
    }

    private fun IFatherExtendable.renderExtendable(
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float,
        panel: Panel
    ) {
        //Copy the list
        val visibleChildren = ArrayList(this.visibleChildren)
        if (visibleChildren.isEmpty()) return

        //If father is not paused we update it
        if (this.timer.passed(16)) {
            val max =
                visibleChildren.size * (if (visibleChildren.first() is ModuleButton) Scale.moduleButtonHeight else Scale.settingHeight)

            if (this.isActive) this.target = AnimationUtil.animate(max.toDouble() * 1.1, target.toDouble(), 0.2).toInt()
            else {
                if (visibleChildren.all { if (it is IFatherExtendable) it.target == 0 else true }) this.target =
                    AnimationUtil.animate(0.0, target.toDouble(), 0.2).toInt()
            }
            if (this.target > max) this.target = max
            else if (this.target < 0) this.target = 0
            this.timer.reset()
        }

        this.current = 0

        visibleChildren.forEach { child ->
            if (current < this.target) {

                //Logger.fatal("$current $target")
                val offset = this.target - this.current

                child.x = if (this is Panel) this.translatedX else this.x
                child.y = startY

                //Create a local variable to sync
                val cachedChildX = child.x
                val cachedChildY = child.y

                val cachedUpLimit = panel.upLimit
                val cachedDownLimit = panel.downLimit

                val startCutY = max(cachedUpLimit, cachedChildY)
                val endCutY = min(cachedChildY + offset, cachedDownLimit)

                val cancelRender =
                    cachedChildY > panel.downLimit || cachedChildY + offset < panel.upLimit || endCutY <= startCutY

                if (!cancelRender) draw {
                    val centerX = (ScaleHelper.scaledResolution.scaledWidth_double * 0.5f).toFloat()
                    val centerY = (ScaleHelper.scaledResolution.scaledHeight_double * 0.5f).toFloat()
                    val scale = DefaultRootScreen.zoomScale

                    val x = (centerX - (centerX - cachedChildX) * scale).toInt()
                    val y = (centerY - (centerY - startCutY) * scale).toInt()
                    val x1 = ceil(centerX - (centerX - (cachedChildX + child.width)) * scale).toInt()
                    val y1 = ceil(centerY - (centerY - endCutY) * scale).toInt()
                    RenderUtils2D.glScissor(
                        x,
                        y,
                        x1,
                        y1,
                        ScaleHelper.scaledResolution
                    )
                    GL11.glEnable(GL11.GL_SCISSOR_TEST)
                }

                if (!cancelRender) {
                    child.onRender(mouseX, mouseY, partialTicks)
                    if (GuiSetting.descriptions) {
                        val centerX = (ScaleHelper.scaledResolution.scaledWidth_double * 0.5f).toFloat()
                        val centerY = (ScaleHelper.scaledResolution.scaledHeight_double * 0.5f).toFloat()
                        val scale = DefaultRootScreen.zoomScale

                        val x = (centerX - (centerX - cachedChildX) * scale).toInt()
                        val y = (centerY - (centerY - startCutY) * scale).toInt()
                        val x1 = ceil(centerX - (centerX - (cachedChildX + child.width)) * scale).toInt()
                        val y1 = ceil(centerY - (centerY - endCutY) * scale).toInt()

                        if (child is IDescriptorContainer && inArea(mouseX, mouseY, x, y, x1, y1))
                            child.drawDescription(mouseX, mouseY)
                    }
                }

                val delta = if (offset < child.height) offset else child.height

                startY += delta
                this.current += delta

                if (!cancelRender) draw {
                    GL11.glDisable(GL11.GL_SCISSOR_TEST)
                }

                if (child is IFatherExtendable) {
                    child.renderExtendable(mouseX, mouseY, partialTicks, panel)
                }
                panel.lastEndY = startY
            }
        }
        if (this is Panel) {
            panel.lastEndY = startY
            adjust()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    fun AbstractElement.renderAnimation(mouseX: Int, mouseY: Int, partialTicks: Float) {
        animatedAlphaUnit.update(isHoovered(mouseX, mouseY))
        drawRect(
            x.toFloat(),
            y.toFloat(),
            x + width.toFloat(),
            y + height.toFloat(),
            ColorRGB(128, 128, 128, animatedAlphaUnit.currentValue.toInt())
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    fun AbstractElement.renderBackground() {
        //Background
        if (this !is ModuleButton) drawRect(x.toFloat(), y.toFloat(), x + width.toFloat(), y + height.toFloat(), dark)
        else drawRect(x.toFloat(), y.toFloat(), x + width.toFloat(), y + height.toFloat(), backgroundColor)
    }

}