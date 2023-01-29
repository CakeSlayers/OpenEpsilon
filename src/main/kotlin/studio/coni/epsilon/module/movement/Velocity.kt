package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.packetMotionX
import studio.coni.epsilon.common.extensions.packetMotionY
import studio.coni.epsilon.common.extensions.packetMotionZ
import studio.coni.epsilon.event.events.EntityCollisionEvent
import studio.coni.epsilon.event.events.PlayerPushEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.onPacketReceive
import studio.coni.epsilon.util.onTick
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketExplosion


object Velocity :
    Module(name = "Velocity", category = Category.Movement, description = "Increase your downwards velocity when falling") {

    private val horizontal by setting("Horizontal", 0.0f, 0f..100f, 0.1f)
    private val vertical by setting("Vertical", 0.0f, 0f..100f, 0.1f)
    val blocks by setting("Blocks", true)
    val liquid by setting("Liquid", false)

    init {
        onTick {
            mc.player.entityCollisionReduction = 1.0f
        }
        onPacketReceive { event ->
            if (event.packet is SPacketEntityVelocity) {
                val velocity = event.packet
                if (velocity.entityID == mc.player.entityId) {
                    if (horizontal == 0f && vertical == 0f) event.cancel()
                    velocity.packetMotionX = (horizontal * velocity.motionX / 100.0).toInt()
                    velocity.packetMotionY = (vertical * velocity.motionY / 100.0).toInt()
                    velocity.packetMotionZ = (horizontal * velocity.motionZ / 100.0).toInt()
                }
            } else if (event.packet is SPacketExplosion) {
                if (horizontal == 0f && vertical == 0f) event.cancel()
                val velocity = event.packet
                velocity.packetMotionX = horizontal * velocity.motionX / 100f
                velocity.packetMotionY = vertical * velocity.motionY / 100f
                velocity.packetMotionZ = horizontal * velocity.motionX / 100f
            }
        }

        listener<EntityCollisionEvent> { event ->
            if (event.entity == mc.player) {
                if (horizontal == 0f && vertical == 0f) {
                    event.cancel()
                    return@listener
                }
                event.x = -event.x * horizontal / 100.0
                event.y = 0.0
                event.z = -event.z * horizontal / 100.0
            }
        }

        listener<PlayerPushEvent> { event ->
            if (blocks && event.type == PlayerPushEvent.Type.BLOCK) {
                event.cancel()
            }
            if (liquid && event.type == PlayerPushEvent.Type.LIQUID) {
                event.cancel()
            }
        }
    }

    override fun getHudInfo(): String {
        return "H" + java.lang.String.format("%.1f", horizontal) + "%" + " V" + java.lang.String.format("%.1f", vertical) + "%"
    }
}