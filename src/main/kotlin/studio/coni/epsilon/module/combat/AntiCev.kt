package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.isReplaceable
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.management.CombatManager
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.EntityUtil.placeBlockScaffold
import studio.coni.epsilon.util.ItemUtil
import studio.coni.epsilon.util.combat.CrystalUtils
import studio.coni.epsilon.util.extension.betterPosition
import studio.coni.epsilon.util.extension.flooredPosition
import studio.coni.epsilon.util.graphics.RenderUtils3D
import studio.coni.epsilon.util.onRender3D
import studio.coni.epsilon.util.onTick
import studio.coni.epsilon.util.text.ChatUtil
import studio.coni.epsilon.util.threads.runSafe
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

@CombatManager.CombatModule
object AntiCev : Module(
    name = "AntiCev",
    category = Category.Combat,
    description = "Use end crystal or block to prevent ecv"
) {

    val mode by setting("Mode", Mode.Trap)
    val render by setting("Render", false)
    private val TRAP = arrayOf(
        BlockPos(0.0, 0.0, -1.0),
        BlockPos(1.0, 0.0, 0.0),
        BlockPos(0.0, 0.0, 1.0),
        BlockPos(-1.0, 0.0, 0.0),
        BlockPos(0.0, 2.0, 0.0)
    )
    private var renderBlockPos: BlockPos? = null

    init {
        onTick {
            runSafe {
                val headPos = player.flooredPosition.up(2)

                val lastSlot = player.inventory.currentItem
                val crystal = ItemUtil.findItemInHotBar(Items.END_CRYSTAL)
                val obby = ItemUtil.findBlockInHotBar(Blocks.OBSIDIAN)

                renderBlockPos = null

                if (isTrap(player)) {
                    when (mode) {
                        Mode.Crystal -> {

                            val offhand = mc.player.heldItemOffhand.item === Items.END_CRYSTAL
                            if (!offhand) {
                                if (crystal == -1) {
                                    disable()
                                    ChatUtil.sendNoSpamErrorMessage("Can not find crystal in hotbar", 12333)
                                    return@runSafe
                                }
                            }
                            val crystalPos = findPlaceablePos(headPos) ?: return@runSafe
                            if (!offhand)
                                ItemUtil.switchToSlot(crystal)
                            mc.player.connection.sendPacket(
                                CPacketPlayerTryUseItemOnBlock(
                                    crystalPos,
                                    EnumFacing.UP,
                                    if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                                    0f,
                                    0f,
                                    0f
                                )
                            )
                            renderBlockPos = crystalPos
                            if (!offhand)
                                ItemUtil.switchToSlot(lastSlot)
                        }
                        Mode.Trap -> {
                            if (!world.isAirBlock(headPos) && world.getBlockState(headPos.up()).isReplaceable) {
                                ItemUtil.switchToSlot(obby)

                                placeBlockScaffold(headPos.up())
                                renderBlockPos = headPos.up()

                                ItemUtil.switchToSlot(lastSlot)
                            }
                        }
                    }
                }
            }
        }

        onRender3D {
            if (renderBlockPos != null) RenderUtils3D.drawBoundingFilledBox(renderBlockPos!!, GUIManager.firstColor.alpha(69))
        }
    }

    private fun SafeClientEvent.isTrap(player: EntityPlayer): Boolean {
        val playerPos = player.betterPosition
        return TRAP.all {
            !world.isAirBlock(playerPos.add(it))
        }
    }

    private fun findPlaceablePos(pos: BlockPos): BlockPos? {
        EnumFacing.HORIZONTALS.forEach {
            val faceDirection = pos.offset(it).down()
            if (CrystalUtils.isPlaceable(mc.world, faceDirection, false)) {
                return faceDirection
            }
        }
        return null
    }

    enum class Mode(val standardName: String) {
        Crystal("Crystal"),
        Trap("Trap")
    }

}