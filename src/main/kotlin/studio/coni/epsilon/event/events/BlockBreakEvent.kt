package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.util.math.BlockPos

class BlockBreakEvent(val breakerID: Int, val position: BlockPos, val progress: Int) : Event()