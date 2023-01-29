package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Cancellable
import net.minecraft.network.Packet

sealed class PacketEvent(val packet: Packet<*>?) : Cancellable() {
    class Receive(packet: Packet<*>?) : PacketEvent(packet)

    class PostReceive(packet: Packet<*>?) : PacketEvent(packet)

    class Send(packet: Packet<*>?) : PacketEvent(packet)

    class PostSend(packet: Packet<*>?) : PacketEvent(packet)
}