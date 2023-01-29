package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.rotationPitch
import studio.coni.epsilon.common.extensions.rotationYaw
import studio.coni.epsilon.common.extensions.runSafeTask
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.onPacketReceive
import net.minecraft.network.play.server.SPacketPlayerPosLook

object NoRotate : Module(
    name = "NoRotate",
    category = Category.Misc,
    description = "Prevents you from processing server rotations"
) {

    init {
        onPacketReceive {
            runSafeTask {
                if (it.packet is SPacketPlayerPosLook) {
                    it.packet.rotationYaw = mc.player.rotationYaw
                    it.packet.rotationPitch = mc.player.rotationPitch
                }
            }
        }
    }

}