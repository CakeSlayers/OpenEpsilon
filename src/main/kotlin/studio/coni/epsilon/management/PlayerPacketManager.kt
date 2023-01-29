package studio.coni.epsilon.management

import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.common.interfaces.Helper
import studio.coni.epsilon.event.decentralized.events.player.OnUpdateWalkingPlayerDecentralizedEvent
import studio.coni.epsilon.event.events.RenderEntityEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.util.math.Vec2f
import studio.coni.epsilon.util.onPacketPostSend
import studio.coni.epsilon.util.onTick
import studio.coni.epsilon.common.extensions.*
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

object PlayerPacketManager : Helper {

    private val pendingPacket = AtomicReference<Packet?>()

    var position: Vec3d = Vec3d.ZERO; private set
    var prevPosition: Vec3d = Vec3d.ZERO; private set

    var eyePosition: Vec3d = Vec3d.ZERO; private set
    var boundingBox: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0); private set

    var rotation = Vec2f.ZERO; private set
    var prevRotation = Vec2f.ZERO; private set

    private var clientSidePitch = Vec2f.ZERO

    init {
        onTick {
            runSafeTask {
                prevPosition = position
                prevRotation = rotation
            }
        }

        onPacketPostSend { event ->
            runSafeTask {
                if (event.packet is CPacketPlayer) {

                    if (event.packet.moving) {
                        position = Vec3d(event.packet.x, event.packet.y, event.packet.z)
                        eyePosition = Vec3d(event.packet.x, event.packet.y + player!!.getEyeHeight(), event.packet.z)

                        val halfWidth = player!!.width / 2.0
                        boundingBox = AxisAlignedBB(
                            event.packet.x - halfWidth, event.packet.y, event.packet.z - halfWidth,
                            event.packet.x + halfWidth, event.packet.y + player!!.height, event.packet.z + halfWidth,
                        )
                    }
                    if (event.packet.rotating) {
                        rotation = Vec2f(event.packet.yaw, event.packet.pitch)
                    }
                }
            }
        }

        listener<RenderEntityEvent.All.Post> { event ->
            if (event.entity != player || event.entity.isRiding) return@listener

            with(event.entity) {
                prevRotationPitch = clientSidePitch.x
                rotationPitch = clientSidePitch.y
            }
        }

        listener<RenderEntityEvent.All.Pre> { event ->
            if (event.entity != player || event.entity.isRiding) return@listener

            with(event.entity) {
                clientSidePitch = Vec2f(prevRotationPitch, rotationPitch)
                prevRotationPitch = prevRotation.y
                rotationPitch = rotation.y
            }
        }
    }


    fun applyPacket(event: OnUpdateWalkingPlayerDecentralizedEvent.OnUpdateWalkingPlayerData) {
        val packet = pendingPacket.getAndSet(null)
        if (packet != null) {
            event.apply(packet)
        }
    }

    inline fun sendPacket(priority: Int, block: Packet.Builder.() -> Unit) {
        Packet.Builder.retain(priority).apply(block).build()?.let {
            sendPacket(it)
        }
    }

    inline fun AbstractModule.sendPlayerPacket(block: Packet.Builder.() -> Unit) {
        sendPlayerPacket(this.priority, block)
    }

    inline fun sendPlayerPacket(priority: Int, block: Packet.Builder.() -> Unit) {
        Packet.Builder(priority).apply(block).build()?.let {
            sendPlayerPacket(it)
        }
    }

    fun sendPlayerPacket(packet: Packet) {
        pendingPacket.updateAndGet {
            if (it == null || it.priority < packet.priority) {
                packet
            } else {
                it
            }
        }
    }

    fun sendPacket(packet: Packet) {
        pendingPacket.updateAndGet {
            if (it == null || it.priority < packet.priority) {
                packet
            } else {
                it
            }
        }
    }

    class Packet private constructor(val priority: Int, val position: Vec3d?, val rotation: Vec2f?, val onGround: Boolean?, val cancelMove: Boolean, val cancelRotate: Boolean, val cancelAll: Boolean) {
        class Builder(private var priority: Int) {
            private var position: Vec3d? = null
            private var rotation: Vec2f? = null
            private var onGround: Boolean? = null

            private var cancelMove = false
            private var cancelRotate = false
            private var cancelAll = false
            private var empty = true

            fun onGround(onGround: Boolean) {
                this.onGround = onGround
                this.empty = false
            }

            fun move(position: Vec3d) {
                this.position = position
                this.cancelMove = false
                this.empty = false
            }

            fun rotate(rotation: Vec2f) {
                this.rotation = rotation
                this.cancelRotate = false
                this.empty = false
            }

            fun cancelAll() {
                this.cancelMove = true
                this.cancelRotate = true
                this.cancelAll = true
                this.empty = false
            }

            fun cancelMove() {
                this.position = null
                this.cancelMove = true
                this.empty = false
            }

            fun cancelRotate() {
                this.rotation = null
                this.cancelRotate = true
                this.empty = false
            }

            fun build(): Packet? {
                val packet = if (!empty) Packet(priority, position, rotation, onGround, cancelMove, cancelRotate, cancelAll) else null

                priority = 0
                position = null
                rotation = null
                onGround = null

                cancelMove = false
                cancelRotate = false
                cancelAll = false
                empty = true

                pool.add(this)
                return packet
            }

            companion object {
                private val pool = ConcurrentLinkedQueue<Builder>()

                fun retain(priority: Int): Builder {
                    return (pool.poll() ?: Builder(priority)).apply {
                        this.priority = priority
                    }
                }
            }
        }
    }
}