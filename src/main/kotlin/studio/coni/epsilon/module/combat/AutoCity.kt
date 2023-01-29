package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.OnUpdateWalkingPlayerEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.management.FriendManager
import studio.coni.epsilon.management.GUIManager
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.module.player.PacketMine
import studio.coni.epsilon.util.*
import studio.coni.epsilon.util.combat.CombatUtils
import studio.coni.epsilon.util.combat.CrystalUtils
import studio.coni.epsilon.util.combat.SurroundUtils
import studio.coni.epsilon.util.extension.eyesPosition
import studio.coni.epsilon.util.extension.flooredPosition
import studio.coni.epsilon.util.extension.prevPosVector
import studio.coni.epsilon.util.graphics.RenderUtils3D
import studio.coni.epsilon.util.math.vector.distanceTo
import studio.coni.epsilon.util.math.vector.toBlockPos
import studio.coni.epsilon.util.math.vector.toVec3d
import studio.coni.epsilon.util.math.vector.toVec3dCenter
import studio.coni.epsilon.util.text.ChatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

object AutoCity : Module(
    name = "AutoCity",
    category = Category.Combat,
    description = "make you as annoying as city boss"
) {
    private val range by setting("Range", 5f, 0f..8f, 1f)
    private val autoSwitch by setting("AutoSwitch", false)
    private val message by setting("Message", true)

    private var miningPos: BlockPos? = null
    private var target: EntityPlayer? = null

    init {
        safeListener<OnUpdateWalkingPlayerEvent.Pre> {
            findClosestTarget()

            if (target == null) {
                if (message) ChatUtil.sendNoSpamWarningMessage("AutoCity no target...", 1259)
                return@safeListener
            }

            miningPos = findHoleBlock(target!!)

            val slot = ItemUtil.findItemInHotBar(Items.DIAMOND_PICKAXE)
            val center = target?.flooredPosition ?: return@safeListener

            if (mc.player.heldItemMainhand.item != Items.DIAMOND_PICKAXE && slot == -1) {
                if (message) ChatUtil.printErrorChatMessage("No pickaxe found in hotbar.")
                disable()
                return@safeListener
            } else {
                if (autoSwitch) ItemUtil.swapToSlot(slot)
            }

            val pos = miningPos ?: run {
                disable()
                return@safeListener
            }

            if (mc.world.isAirBlock(miningPos!!) && mc.world.isAirBlock(center)) {
                disable()
                return@safeListener
            }

            target?.let {
                if (it.prevPosVector.distanceTo(pos) > 2.0) {
                    disable()
                    return@safeListener
                }
            }

            PacketMine.mineBlock(AutoCity, miningPos!!)
            if (!mc.world.isAirBlock(center) && mc.world.isAirBlock(miningPos)) {
                PacketMine.mineBlock(AutoCity, center)
            }
        }
    }


    private fun findClosestTarget() {
        val playerList = mc.world.playerEntities
        target = null
        for (target in playerList) {
            if (target == mc.player) {
                continue
            }
            if (FriendManager.isFriend(target)) {
                continue
            }
            if (target.isDead) {
                continue
            }
            if (target.health <= 0) {
                continue
            }
            if (this.target == null) {
                this.target = target
                continue
            }
            if (mc.player.getDistance(target) < mc.player.getDistance(this.target)) {
                this.target = target
            }
        }
    }


    private fun SafeClientEvent.findHoleBlock(entity: Entity): BlockPos? {
        val pos = entity.positionVector.toBlockPos()
        var closestPos = 114.514 to BlockPos.ORIGIN
        for (facing in EnumFacing.HORIZONTALS) {
            val offsetPos = pos.offset(facing)
            val dist = mc.player.distanceTo(offsetPos)
            if (dist > range || dist > closestPos.first) continue
            if (world.getBlockState(offsetPos).block == Blocks.BEDROCK) continue
            if (!checkPos(offsetPos, facing)) continue
            closestPos = dist to offsetPos
        }
        return if (closestPos.second != BlockPos.ORIGIN) closestPos.second else null
    }

    private fun checkPos(pos: BlockPos, facingIn: EnumFacing): Boolean {
        if (CrystalUtils.isValidBasePos(mc.world, pos.down()) && CrystalUtils.isValidSpace(mc.world, pos.up())) return true
        for (facing in EnumFacing.HORIZONTALS) {
            if (facing == facingIn.opposite) continue
            if (!CrystalUtils.isValidBasePos(mc.world, pos.offset(facing))) continue
            return true
        }
        return false
    }

    override fun onDisable() {
        PacketMine.reset(AutoCity)
    }
}