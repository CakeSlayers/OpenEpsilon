package studio.coni.epsilon.event.decentralized.events.network

import studio.coni.epsilon.event.decentralized.CancellableEventData
import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import net.minecraft.network.Packet

object PacketDecentralizedEvent {

    data class PacketEventData(val packet: Packet<*>?, override val father: DataDecentralizedEvent<*>) :
        CancellableEventData(father)

    object Send : DataDecentralizedEvent<PacketEventData>()

    object Receive : DataDecentralizedEvent<PacketEventData>()

    object PostSend : DataDecentralizedEvent<PacketEventData>()

    object PostReceive : DataDecentralizedEvent<PacketEventData>()
}