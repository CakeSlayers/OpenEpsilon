package studio.coni.epsilon.event

import studio.coni.epsilon.event.events.ConnectionEvent
import studio.coni.epsilon.event.events.RunGameLoopEvent
import studio.coni.epsilon.event.events.WorldEvent
import studio.coni.epsilon.util.Wrapper
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.network.NetHandlerPlayClient

abstract class AbstractClientEvent : Event() {
    val mc = Wrapper.mc
    abstract val world: WorldClient?
    abstract val player: EntityPlayerSP?
    abstract val playerController: PlayerControllerMP?
    abstract val connection: NetHandlerPlayClient?
}

open class ClientEvent : AbstractClientEvent() {
    final override val world: WorldClient? = mc.world
    final override val player: EntityPlayerSP? = mc.player
    final override val playerController: PlayerControllerMP? = mc.playerController
    final override val connection: NetHandlerPlayClient? = mc.connection

    inline operator fun <T> invoke(block: ClientEvent.() -> T) = run(block)
}

open class SafeClientEvent internal constructor(
    override val world: WorldClient,
    override val player: EntityPlayerSP,
    override val playerController: PlayerControllerMP,
    override val connection: NetHandlerPlayClient
) : AbstractClientEvent() {
    inline operator fun <T> invoke(block: SafeClientEvent.() -> T) = run(block)

    companion object {
        var instance: SafeClientEvent? = null; private set

        init {
            listener<ConnectionEvent.Disconnect>(Int.MAX_VALUE, true) {
                reset()
            }

            listener<WorldEvent.Unload>(Int.MAX_VALUE, true) {
                reset()
            }

            listener<RunGameLoopEvent.Tick>(Int.MAX_VALUE, true) {
                update()
            }
        }

        fun update() {
            val world = Wrapper.world ?: return
            val player = Wrapper.player ?: return
            val playerController = Wrapper.mc.playerController ?: return
            val connection = Wrapper.mc.connection ?: return

            instance = SafeClientEvent(world, player, playerController, connection)
        }

        fun reset() {
            instance = null
        }
    }
}
