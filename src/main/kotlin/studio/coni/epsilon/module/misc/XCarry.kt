package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.windowID
import studio.coni.epsilon.event.decentralized.decentralizedListener
import studio.coni.epsilon.event.decentralized.events.network.PacketDecentralizedEvent
import studio.coni.epsilon.module.Module
import net.minecraft.network.play.client.CPacketCloseWindow

object XCarry : Module(
    name = "XCarry",
    category = Category.Misc,
    alias = arrayOf("ExtraInventory"),
    description = "The crafting slots in your inventory become extra storage space"
) {

    private val forceCancel = setting("Force", false)

    init {
        decentralizedListener(PacketDecentralizedEvent.Send) { event ->
            if (event.packet !is CPacketCloseWindow) return@decentralizedListener
            if (event.packet.windowID == mc.player.inventoryContainer.windowId || forceCancel.value) event.cancel()
        }
    }

}