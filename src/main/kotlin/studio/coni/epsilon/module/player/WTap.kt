package studio.coni.epsilon.module.player

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.decentralized.decentralizedListener
import studio.coni.epsilon.event.decentralized.events.client.ClientTickDecentralizedEvent
import studio.coni.epsilon.event.decentralized.events.network.PacketDecentralizedEvent
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.Timer
import studio.coni.epsilon.util.Utils
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketUseEntity
import kotlin.random.Random


object WTap : Module(
    name = "WTap",
    category = Category.Player,
    description = "Make you wtap easier"
) {

    private val weaponOnly by setting("WeaponOnly", true)
    val range by setting("Range", 4.5, 0.0..10.0, 0.1)
    val min by setting("MinDelay", 150, 0..1000, 1)
    val max by setting("MaxDelay", 300, 0..1000, 1)

    var timer = Timer()
    private var wait = false
    private var lastRandom: Int

    init {
        lastRandom = Random.nextInt(if (min > max) max - 1 else min, max)

        decentralizedListener(ClientTickDecentralizedEvent) {
            if (Utils.nullCheck()) return@decentralizedListener
            if (timer.passed(lastRandom)) {
                wait = false
                timer.reset()
                lastRandom = Random.nextInt(if (min > max) max - 1 else min, max)
            }
        }

        decentralizedListener(PacketDecentralizedEvent.Send) { event ->
            if (Utils.nullCheck()) return@decentralizedListener
            if (wait) return@decentralizedListener
            if (event.packet is CPacketUseEntity && event.packet.action == CPacketUseEntity.Action.ATTACK) {
                val packet = event.packet
                if (packet.getEntityFromWorld(mc.world) is EntityLivingBase) {
                    val target =
                        packet.getEntityFromWorld(mc.world) as EntityLivingBase? ?: return@decentralizedListener
                    if (weaponOnly) {
                        if (mc.player.heldItemMainhand.item !is ItemSword) {
                            return@decentralizedListener
                        }
                    }
                    if (mc.player.getDistance(target) >= range || target === mc.player || mc.player.foodStats.foodLevel < 6) {
                        return@decentralizedListener
                    }
                    if (mc.player.isSprinting) {
                        mc.player.isSprinting = false
                        mc.player.isSprinting = true
                    }
                    mc.player.connection.sendPacket(
                        CPacketEntityAction(
                            mc.player,
                            CPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketEntityAction(
                            mc.player,
                            CPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketEntityAction(
                            mc.player,
                            CPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    wait = true
                }
            }
        }
    }

}