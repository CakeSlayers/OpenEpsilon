package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.atTrue
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.events.PlayerTravelEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.MovementUtils.calcMoveYaw
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityBoat
import net.minecraft.entity.passive.AbstractHorse
import net.minecraft.entity.passive.EntityHorse
import net.minecraft.entity.passive.EntityPig
import net.minecraft.network.play.client.CPacketInput
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketVehicleMove
import net.minecraft.network.play.server.SPacketMoveVehicle
import net.minecraft.util.EnumHand
import net.minecraft.world.chunk.EmptyChunk
import kotlin.math.cos
import kotlin.math.sin

internal object EntitySpeed : Module(
    name = "EntitySpeed",
    category = Category.Movement,
    description = "Abuse client-sided movement to shape sound barrier breaking rideables"
) {
    private val speed = setting("Speed", 1.0f, 0.1f..25.0f, 0.1f)
    private val antiStuck = setting("Anti Stuck", true)
    private val flight = setting("Flight", false)
    private val glideSpeed = setting("Glide Speed", 0.1f, 0.0f..1.0f, 0.01f, flight.atTrue())
    private val upSpeed = setting("Up Speed", 1.0f, 0.0f..5.0f, 0.1f, flight.atTrue())
    private val opacity = setting("Boat Opacity", 1.0f, 0.0f..1.0f, 0.01f)
    private val forceInteract = setting("Force Interact", false)
    private val interactTickDelay = setting(
        "Interact Delay",
        2,
        1..20,
        1,
        description = "Force interact packet delay, in ticks.",
        forceInteract.atTrue()
    )

    override fun getHudInfo(): String {
        return speed.toString()
    }

    init {
        safeListener<PacketEvent.Send> {
            val ridingEntity = player.ridingEntity

            if (!forceInteract.value || ridingEntity !is EntityBoat) return@safeListener

            if (it.packet is CPacketPlayer.Rotation || it.packet is CPacketInput) {
                it.cancel()
            }

            if (it.packet is CPacketVehicleMove) {
                if (player.ticksExisted % interactTickDelay.value == 0) {
                    playerController.interactWithEntity(player, ridingEntity, EnumHand.MAIN_HAND)
                }
            }
        }

        safeListener<PacketEvent.Receive> {
            if (!forceInteract.value || player.ridingEntity !is EntityBoat || it.packet !is SPacketMoveVehicle) return@safeListener
            it.cancel()
        }

        safeListener<PlayerTravelEvent> {
            player.ridingEntity?.let {
                if (it is EntityPig || it is AbstractHorse || it is EntityBoat && it.controllingPassenger == player) {
                    steerEntity(it)
                    if (flight.value) fly(it)
                }
            }
        }
    }

    private fun SafeClientEvent.steerEntity(entity: Entity) {
        val yawRad = calcMoveYaw()

        val motionX = -sin(yawRad) * speed.value
        val motionZ = cos(yawRad) * speed.value

        if (studio.coni.epsilon.util.MovementUtils.isInputting && !isBorderingChunk(entity, motionX, motionZ)) {
            entity.motionX = motionX
            entity.motionZ = motionZ
        } else {
            entity.motionX = 0.0
            entity.motionZ = 0.0
        }

        if (entity is EntityHorse || entity is EntityBoat) {
            entity.rotationYaw = player.rotationYaw

            // Make sure the boat doesn't turn etc (params: isLeftDown, isRightDown, isForwardDown, isBackDown)
            if (entity is EntityBoat) entity.updateInputs(false, false, false, false)
        }
    }

    private fun fly(entity: Entity) {
        if (!entity.isInWater) entity.motionY = -glideSpeed.value.toDouble()
        if (mc.gameSettings.keyBindJump.isKeyDown) entity.motionY += upSpeed.value / 2.0
    }

    private fun SafeClientEvent.isBorderingChunk(entity: Entity, motionX: Double, motionZ: Double): Boolean {
        return antiStuck.value && world.getChunk(
            (entity.posX + motionX).toInt() shr 4,
            (entity.posZ + motionZ).toInt() shr 4
        ) is EmptyChunk
    }

    @JvmStatic
    fun getOpacity(): Float = opacity.value
}
