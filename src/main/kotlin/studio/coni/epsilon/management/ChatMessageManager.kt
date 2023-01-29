package studio.coni.epsilon.management

import studio.coni.epsilon.event.events.BaritoneCommandEvent
import studio.coni.epsilon.event.events.ChatEvent
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.management.CommandManager.runCommand
import studio.coni.epsilon.mixin.mixins.accessor.network.AccessorCPacketChatMessage
import studio.coni.epsilon.util.Wrapper
import studio.coni.epsilon.util.text.MessageDetection
import net.minecraft.network.play.client.CPacketChatMessage
import java.util.*

object ChatMessageManager {

    init {
        listener<PacketEvent.Send> {
            if (it.packet is CPacketChatMessage) {
                if (it.packet.message.shouldCancel()) it.cancel()
                ChatEvent(it.packet.message).let { event ->
                    event.post()
                    if (event.cancelled) it.cancel()
                    else (it.packet as AccessorCPacketChatMessage).setMessage(event.message)
                }
            }
        }
    }

    private fun String.shouldCancel(): Boolean {
        MessageDetection.Command.BARITONE.removedOrNull(this)?.let {
            BaritoneCommandEvent(it.toString().substringBefore(' ').lowercase(Locale.ROOT)).post()
        }

        return this.runCommand()
    }

    fun Any.sendServerMessage(message: String) {
        Wrapper.player?.sendChatMessage(message)
    }

}