package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.text.ChatUtil
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemWrittenBook
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.SoundCategory

internal object AntiCrasher : Module(
    name = "AntiCrasher",
    category = Category.Misc,
    description = "Prevents being kicked by clicking on books or offhand leg"
) {
    init {
        listener<PacketEvent.PostSend> {
            if (it.packet !is CPacketClickWindow) return@listener
            if (it.packet.clickedItem.item !is ItemWrittenBook) return@listener

            ChatUtil.sendNoSpamWarningMessage(
                name + " Don't click the book \""
                        + it.packet.clickedItem.displayName
                        + "\", shift click it instead!"
            )
            mc.player.openContainer.slotClick(it.packet.slotId, it.packet.usedButton, it.packet.clickType, mc.player)
        }

        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketSoundEffect) return@listener
            if (it.packet.category == SoundCategory.PLAYERS
                && (it.packet.sound === SoundEvents.ITEM_ARMOR_EQUIP_GENERIC
                        || it.packet.sound === SoundEvents.ITEM_SHIELD_BLOCK)
            ) {
                it.cancel()
            }
        }
    }
}