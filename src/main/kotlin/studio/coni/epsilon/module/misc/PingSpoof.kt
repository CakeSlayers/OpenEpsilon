package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.interfaces.DisplayEnum
import studio.coni.epsilon.concurrent.onMainThreadSafeSuspend
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.threads.defaultScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketKeepAlive

internal object PingSpoof : Module(
    name = "PingSpoof",
    category = Category.Misc,
    description = "Cancels or adds delay to your ping packets"
) {
    private val mode by setting("Mode", Mode.NORMAL)
    private val delay by setting("Delay", 100, 0..1000, 5)
    private val multiplier by setting("Multiplier", 1, 1..100, 1)

    private enum class Mode(override val displayName: CharSequence) : DisplayEnum {
        NORMAL("Normal"),
        CC("CC")
    }

    private val packetTimer = TickTimer()

    override fun getHudInfo(): String {
        return (delay * multiplier).toString()
    }

    override fun onDisable() {
        packetTimer.reset(-114514L)
    }

    init {
        listener<PacketEvent.Receive> {
            when (it.packet) {
                is SPacketKeepAlive -> {
                    packetTimer.reset()
                    it.cancel()
                    defaultScope.launch {
                        delay((delay * multiplier).toLong())
                        onMainThreadSafeSuspend {
                            connection.sendPacket(CPacketKeepAlive(it.packet.id))
                        }
                    }
                }
                is SPacketConfirmTransaction -> {
                    if (mode == Mode.CC && it.packet.windowId == 0 && !it.packet.wasAccepted() && !packetTimer.tickAndReset(1L)) {
                        packetTimer.reset(-114514L)
                        it.cancel()
                        defaultScope.launch {
                            delay((delay * multiplier).toLong())
                            onMainThreadSafeSuspend {
                                connection.sendPacket(CPacketConfirmTransaction(it.packet.windowId, it.packet.actionNumber, true))
                            }
                        }
                    }
                }
            }
        }
    }
}
