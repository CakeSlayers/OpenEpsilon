package studio.coni.epsilon.event.decentralized.events.player

import studio.coni.epsilon.event.decentralized.CancellableEventData
import studio.coni.epsilon.event.decentralized.DataDecentralizedEvent
import studio.coni.epsilon.management.PlayerPacketManager
import studio.coni.epsilon.util.math.Vec2f
import net.minecraft.util.math.Vec3d

object OnUpdateWalkingPlayerDecentralizedEvent :
    DataDecentralizedEvent<OnUpdateWalkingPlayerDecentralizedEvent.OnUpdateWalkingPlayerData>() {
    class OnUpdateWalkingPlayerData(
        position: Vec3d,
        rotation: Vec2f,
        onGround: Boolean,
        override val father: DataDecentralizedEvent<*>
    ) : CancellableEventData(this) {

        var position = position; private set
        var rotation = rotation; private set

        var onGround = onGround
            @JvmName("isOnGround") get
            private set

        var cancelMove = false; private set
        var cancelRotate = false; private set
        var cancelAll = false; private set

        fun apply(packet: PlayerPacketManager.Packet) {
            cancel()

            packet.position?.let {
                this.position = it
            }
            packet.rotation?.let {
                this.rotation = it
            }
            packet.onGround?.let {
                this.onGround = it
            }

            this.cancelMove = packet.cancelMove
            this.cancelRotate = packet.cancelRotate
            this.cancelAll = packet.cancelAll
        }
    }

    object Pre : DataDecentralizedEvent<OnUpdateWalkingPlayerData>()

    object Post : DataDecentralizedEvent<OnUpdateWalkingPlayerData>()

}