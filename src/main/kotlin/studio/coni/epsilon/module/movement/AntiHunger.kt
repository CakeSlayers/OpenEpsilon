package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.common.extensions.onGround
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer

internal object AntiHunger : Module(
    name = "AntiHunger",
    category = Category.Movement,
    description = "Reduces hunger lost when moving around"
) {
    private val cancelMovementState = setting("Cancel Movement State", true)

    init {
        listener<PacketEvent.Send> {
            if (mc.player == null) return@listener
            if (cancelMovementState.value && it.packet is CPacketEntityAction) {
                if (it.packet.action == CPacketEntityAction.Action.START_SPRINTING || it.packet.action == CPacketEntityAction.Action.STOP_SPRINTING) {
                    it.cancel()
                }
            }
            if (it.packet is CPacketPlayer) {
                // Trick the game to think that tha player is flying even if he is on ground. Also check if the player is flying with the Elytra.
                it.packet.onGround =
                    (mc.player.fallDistance <= 0 || mc.playerController.isHittingBlock) && mc.player.isElytraFlying
            }
        }
    }
}