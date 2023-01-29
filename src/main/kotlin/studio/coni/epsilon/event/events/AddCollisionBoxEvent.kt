package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

class AddCollisionBoxEvent(
    val entity: Entity?,
    val entityBox: AxisAlignedBB,
    val pos: BlockPos,
    val block: Block,
    val collidingBoxes: MutableList<AxisAlignedBB>
) : Event()