package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import net.minecraft.entity.passive.AbstractChestHorse
import net.minecraft.network.play.client.CPacketUseEntity

internal object MountBypass : Module(
    name = "MountBypass",
    category = Category.Misc,
    description = "Might allow you to mount chested animals on servers that block it"
) {
    init {
        listener<PacketEvent.Send> {
            if (it.packet !is CPacketUseEntity || it.packet.action != CPacketUseEntity.Action.INTERACT_AT) return@listener
            if (it.packet.getEntityFromWorld(mc.world) is AbstractChestHorse) it.cancel()
        }
    }
}