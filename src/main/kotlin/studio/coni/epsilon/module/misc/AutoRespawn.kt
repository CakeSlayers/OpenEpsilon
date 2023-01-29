package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.GuiEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.text.ChatUtil
import net.minecraft.client.gui.GuiGameOver
import net.minecraft.util.math.BlockPos

internal object AutoRespawn : Module(
    name = "AutoRespawn",
    description = "Automatically respawn after dying",
    category = Category.Misc
) {
    private val respawn by setting("Respawn", true)
    private val deathCoords by setting("Save Death Coords", true)
    private val antiGlitchScreen by setting("Anti Glitch Screen", true)

    init {
        safeListener<GuiEvent.Displayed> {
            if (it.screen !is GuiGameOver) return@safeListener

            if (deathCoords && player.health <= 0.0f) {
                //WaypointManager.add("Death - " + InfoCalculator.getServerType())
                ChatUtil.printChatMessage("You died at ${player.position.asString()}")
            }

            if (respawn || antiGlitchScreen && player.health > 0.0f) {
                player.respawnPlayer()
                it.screen = null
            }
        }
    }

    private fun BlockPos.asString(): String {
        return "${this.x}, ${this.y}, ${this.z}"
    }
}