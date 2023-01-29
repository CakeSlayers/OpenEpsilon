package studio.coni.epsilon.module.render

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.equippedProgressMainHand
import studio.coni.epsilon.common.extensions.equippedProgressOffHand
import studio.coni.epsilon.common.extensions.itemStackMainHand
import studio.coni.epsilon.common.extensions.prevEquippedProgressMainHand
import studio.coni.epsilon.event.decentralized.decentralizedListener
import studio.coni.epsilon.event.decentralized.events.client.RenderItemAnimationDecentralizedEvent
import studio.coni.epsilon.event.decentralized.events.player.OnUpdateWalkingPlayerDecentralizedEvent
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.Utils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.ItemSword
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumHandSide


object Animations : Module(
    name = "Animations",
    category = Category.Render,
    description = "Render old version animation with minecraft"
) {

    private val disableOffhand by setting("NoOffHand", false)
    private val oldMode by setting("1.8Mode", true)
    private val oldHit by setting("OldHit", true)
    private val hitThreshold by setting("HitHold", 0.9f, 0f..1f, 0.1f)

    init {
        decentralizedListener(OnUpdateWalkingPlayerDecentralizedEvent.Pre) {
            if (oldHit) {
                if (mc.player.heldItemMainhand.item is ItemSword
                    && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= hitThreshold
                ) {
                    mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0F
                    mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.heldItemMainhand
                }
            }
            if (disableOffhand) {
                mc.entityRenderer.itemRenderer.equippedProgressOffHand = -0.9f
            }
        }

        decentralizedListener(RenderItemAnimationDecentralizedEvent.Render) { event ->
            if (!oldMode || Utils.nullCheck()) return@decentralizedListener

            val oldBlock =
                mc.gameSettings.keyBindUseItem.isKeyDown && !mc.player.heldItemMainhand.isEmpty && mc.player.heldItemMainhand.item is ItemSword && mc.player.isHandActive

            if (oldBlock && event.stack.item == Items.SHIELD && event.hand == EnumHand.OFF_HAND) {
                event.cancel()
            }
        }

        decentralizedListener(RenderItemAnimationDecentralizedEvent.Transform) { event ->
            if (!oldMode || Utils.nullCheck()) return@decentralizedListener


            val oldBlock = (mc.gameSettings.keyBindUseItem.isKeyDown
                    && !mc.player.heldItemMainhand.isEmpty && mc.player.heldItemMainhand.item is ItemSword)
            if (event.hand == EnumHand.MAIN_HAND && oldBlock) {
                val i = if (mc.player.primaryHand == EnumHandSide.RIGHT) 1F else -1F
                GlStateManager.translate(0.15f * i, 0.3f, 0.0f)
                GlStateManager.rotate(5f * i, 0.0f, 0.0f, 0.0f)

                if (i > 0F)
                    GlStateManager.translate(0.56f, -0.52f, -0.72f * i)
                else
                    GlStateManager.translate(0.56f, -0.52f, 0.5F)

                GlStateManager.translate(0.0f, 0.2f * -0.6f, 0.0f)
                GlStateManager.rotate(45.0f * i, 0.0f, 1.0f, 0.0f)

                GlStateManager.scale(1.625f, 1.625f, 1.625f)

                GlStateManager.translate(-0.5f, 0.2f, 0.0f)
                GlStateManager.rotate(30.0f * i, 0.0f, 1.0f, 0.0f)
                GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(30.0f * i, 0.0f, 1.0f, 0.0f)
            }
        }
    }
}