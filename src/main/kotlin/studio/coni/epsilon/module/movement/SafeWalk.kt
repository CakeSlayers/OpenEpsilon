package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.decentralized.decentralizedListener
import studio.coni.epsilon.event.decentralized.events.client.ClientTickDecentralizedEvent
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.Utils
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.math.BlockPos


object SafeWalk : Module(
    name = "SafeWalk",
    category = Category.Movement,
    description = "Keeps you from walking off edges"
) {
    private val sneak by setting("Mode", Mode.Normal)
    val shouldSneak get() = this.isEnabled && sneak == Mode.Normal


    init {
        decentralizedListener(ClientTickDecentralizedEvent) {
            if (Utils.nullCheck()) return@decentralizedListener
            if (sneak == Mode.Eagle) KeyBinding.setKeyBindState(
                mc.gameSettings.keyBindSneak.keyCode,
                mc.world.isAirBlock(
                    BlockPos(
                        mc.player.posX,
                        mc.player.posY - 1.0,
                        mc.player.posZ
                    )
                )
            )
        }
    }

    override fun onDisable() {
        if (mc.player == null)
            return

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
    }


    enum class Mode(val standardName: String) {
        Eagle("Eagle"),
        Normal("Normal")
    }
}