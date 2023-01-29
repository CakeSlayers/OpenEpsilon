package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.entityID
import studio.coni.epsilon.event.events.CrystalSetDeadEvent
import studio.coni.epsilon.event.events.PacketEvent
import studio.coni.epsilon.event.events.PlayerMoveEvent
import studio.coni.epsilon.event.events.RunGameLoopEvent
import studio.coni.epsilon.management.EntityManager
import studio.coni.epsilon.management.FriendManager
import studio.coni.epsilon.management.HoleManager
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.combat.HoleInfo
import studio.coni.epsilon.event.*
import studio.coni.epsilon.util.extension.betterPosition
import studio.coni.epsilon.util.isFakeOrSelf
import studio.coni.epsilon.util.math.sq
import studio.coni.epsilon.util.text.ChatUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.SPacketEntityStatus
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

internal object AutoBurrow : Module(
    name = "AutoBurrow",
    description = "Enables burrow when enemy tries to walk into your hole",
    category = Category.Combat,
    priority = 1000
) {
    private val predictTicks by setting("Predict Ticks", 10, 0..50, 1)
    private val detectRange by setting("Detect Range", 5.0f, 0.0f..10.0f, 0.25f)
    private val hRange by setting("H Range", 1.0f, 0.0f..4.0f, 0.1f)
    private val vRange by setting("V Range", 2.0f, 0.0f..8.0f, 0.1f)
    private val feetPopTimeout by setting("Feet Pop Timeout", 1000, 0..3000, 100)
    private val toggleDelay by setting("Toggle Delay", 2000, 0..5000, 50)
    private val notification by setting("Notification", true)

    private val popTimer = TickTimer()
    private val feetCrystalTimer = TickTimer()
    private val toggleTimer = TickTimer()
    private var stopMotion = false

    override fun onDisable() {
        popTimer.reset(-69420L)
        feetCrystalTimer.reset(-69420L)
        toggleTimer.reset(-69420L)
        stopMotion = false
    }

    init {
        listener<PacketEvent.Receive> {
            if (it.packet is SPacketEntityStatus
                && it.packet.opCode.toInt() == 35
                && it.packet.entityID == mc.player?.entityId
            ) {
                popTimer.reset()
            }
        }

        safeListener<CrystalSetDeadEvent> {
            if (player.entityBoundingBox.intersects(it.x - 1.0, it.y, it.z - 1.0, it.x + 1.0, it.y + 2.0, it.z + 1.0)) {
                feetCrystalTimer.reset()
            }
        }

        safeConcurrentListener<RunGameLoopEvent.Tick> {
            if (toggleTimer.tick(toggleDelay) && canBurrow() && shouldBurrow()) {
                Burrow.override = player.betterPosition
                Burrow.enable()
                if (notification) ChatUtil.printChatMessage("[AntiBurrow] triggered")

                stopMotion = true
                toggleTimer.reset()
            }
        }

        safeParallelListener<PlayerMoveEvent.Pre> {
            if (stopMotion) {
                player.motionX = 0.0
                player.motionZ = 0.0
                stopMotion = false
            }
        }
    }

    private fun SafeClientEvent.canBurrow(): Boolean {
        val pos = player.betterPosition

        return player.onGround
                && (player.posY - player.prevPosY).sq <= 0.01
                && (feetPopTimeout == 0 || popTimer.tick(feetPopTimeout) || feetCrystalTimer.tick(feetPopTimeout))
                && canPlace(pos)
                && isValidHole(HoleManager.getHoleInfo(pos))
    }

    private fun SafeClientEvent.canPlace(pos: BlockPos): Boolean {
        return pos.up(2).let { world.getBlockState(it).getCollisionBoundingBox(world, it) == null }
                && EntityManager.checkEntityCollision(AxisAlignedBB(pos), player)
    }

    private fun isValidHole(holeInfo: HoleInfo): Boolean {
        return holeInfo.isHole && !holeInfo.isTrapped && holeInfo.isHole
    }

    private fun SafeClientEvent.shouldBurrow(): Boolean {
        val sqRange = detectRange * detectRange
        val flooredPos = player.betterPosition
        val box = getBox(flooredPos)
        var foundAny = false

        for (entity in world.playerEntities) {
            if (entity == player) continue
            if (!entity.isEntityAlive) continue
            if (entity.isFakeOrSelf) continue
            if (player.getDistanceSq(entity) > sqRange) continue
            if (isAtSamePos(flooredPos, entity)) return false
            foundAny = foundAny || !FriendManager.isFriend(entity) && calcIntercept(box, entity)
        }

        return foundAny
    }

    private fun getBox(flooredPos: BlockPos): AxisAlignedBB {
        return AxisAlignedBB(
            (flooredPos.x - hRange).toDouble(), flooredPos.y.toDouble(), (flooredPos.z - hRange).toDouble(),
            flooredPos.x + 1.0 + hRange, (flooredPos.y + vRange).toDouble(), flooredPos.z + 1.0 + hRange
        )
    }

    private fun isAtSamePos(pos: BlockPos, entity: EntityPlayer): Boolean {
        return pos.x == MathHelper.floor(entity.posX)
                && pos.y == MathHelper.floor(entity.posY + 0.5)
                && pos.z == MathHelper.floor(entity.posZ)
    }

    private fun calcIntercept(box: AxisAlignedBB, entity: EntityPlayer): Boolean {
        val current = entity.positionVector
        if (box.contains(current)) return true
        if (predictTicks == 0) return false

        val motion = current.subtract(entity.prevPosX, entity.prevPosY, entity.prevPosZ)
        val predict = current.add(motion.scale(predictTicks.toDouble()))
        return box.calculateIntercept(current, predict) != null
    }
}