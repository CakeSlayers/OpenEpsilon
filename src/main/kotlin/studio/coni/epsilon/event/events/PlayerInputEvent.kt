package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MovementInput

class PlayerInputEvent(val player: EntityPlayer, val movementInput: MovementInput) : Event()
