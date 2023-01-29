package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.ArrowVelocityEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.onPacketSend
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging

object BowMcBomb : Module(name = "BowMcBomb", category = Category.Misc, description = "Make you arrow move super fast") {

    private val velocity by setting("Velocity", 1f, 0.1f..100f, 0.1f)
    private val spoof by setting("Spoof", 50, 0..500, 1)


    init {
        listener<ArrowVelocityEvent> {
            it.cancel()
            it.velocity = velocity
        }

        onPacketSend { event ->
            if (event.packet is CPacketPlayerDigging) {
                if (event.packet.action == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    if (mc.player.inventory.getCurrentItem().item is ItemBow) {
                        repeat(spoof) {
                            mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.1f, mc.player.posZ, false))
                            mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10000f, mc.player.posZ, true))
                        }
                    }
                }
            }
        }
    }

}
