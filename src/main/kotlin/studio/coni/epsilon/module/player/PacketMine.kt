package studio.coni.epsilon.module.player

import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.collections.lastEntryOrNull
import studio.coni.epsilon.common.collections.synchronized
import studio.coni.epsilon.common.extensions.canBreakBlock
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.*
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.event.safeConcurrentListener
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.management.HotbarManager.spoofHotbar
import studio.coni.epsilon.management.PlayerPacketManager
import studio.coni.epsilon.management.PlayerPacketManager.sendPlayerPacket
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.extension.AxisAlignedBB.interp
import studio.coni.epsilon.util.extension.eyePosition
import studio.coni.epsilon.util.graphics.Easing
import studio.coni.epsilon.util.graphics.RenderUtils3D
import studio.coni.epsilon.util.inventory.findBestTool
import studio.coni.epsilon.util.inventory.slot.hotbarSlots
import studio.coni.epsilon.util.items.isTool
import studio.coni.epsilon.util.math.MathUtils.scale
import studio.coni.epsilon.util.math.RotationUtils.getRotationTo
import studio.coni.epsilon.util.math.fastCeil
import studio.coni.epsilon.util.math.isInSight
import studio.coni.epsilon.util.math.sq
import studio.coni.epsilon.util.math.vector.toVec3dCenter
import studio.coni.epsilon.util.onRender3D
import studio.coni.epsilon.util.threads.runSafe
import studio.coni.epsilon.util.world.getBlock
import studio.coni.epsilon.util.world.getMiningSide
import studio.coni.epsilon.util.world.isAir
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.init.Blocks
import net.minecraft.init.Enchantments
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.util.*
import kotlin.collections.set

internal object PacketMine :
    Module(name = "PacketMine", alias = arrayOf("InstantMine"), category = Category.Player, description = "Break block with packet", priority = 2000) {
    private val instantMine0 = setting("Instant Mine", false)
    private val instantMine by instantMine0
    private val rotation by setting("Rotation", false)
    private val rotateTime by setting("Rotate Time", 100, 0..1000, 10, PacketMine::rotation)
    private val spamPackets0 = setting("Spam Packets", true)
    private val silent by setting("Silent", true)
    private val spamPackets by spamPackets0
    private val noSwing by setting("No Swing", false)
    private val noAnimation by setting("No Animation", false)
    private val swingMode by setting("Swing Mode", SwingMode.Client)
    private val packetDelay by setting("Packet Delay", 50, 0..1000, 5)
    private val breakOffset by setting("Break Offset", 200, -5000..5000, 50)
    private val range by setting("Range", 8.0f, 0.0f..10.0f, 0.25f)

    private val clickTimer = TickTimer()
//    private val renderer = ESPRenderer().apply { aFilled = 31; aOutline = 233 }
    private val packetTimer = TickTimer()
    private var miningInfo0: MiningInfo? = null
    val miningInfo: IMiningInfo?
        get() = miningInfo0

    private val miningQueue = TreeMap<AbstractModule, BlockPos>().synchronized()

    enum class SwingMode {
        Client {
            override fun swingHand(event: SafeClientEvent, hand: EnumHand) {
                event.player.swingArm(hand)
            }
        },
        Packet {
            override fun swingHand(event: SafeClientEvent, hand: EnumHand) {
                event.connection.sendPacket(CPacketAnimation(hand))
            }
        };

        abstract fun swingHand(event: SafeClientEvent, hand: EnumHand)
    }


    override fun isActive(): Boolean {
        return isEnabled && miningInfo0 != null
    }

    override fun onDisable() {
        miningQueue.clear()
        reset()
    }

    init {

        listener<ConnectionEvent.Disconnect> {
            miningQueue.clear()
            reset()
        }

        /*
        listener<InputEvent.Mouse> {
            if (it.button == 0 && it.state && mc.currentScreen == null) {
                if (!clickTimer.tickAndReset(250L)) reset(this)
            }
        }
         */

        onRender3D {
            miningInfo0?.let {
                val multiplier = Easing.OUT_CUBIC.inc(Easing.toDelta(it.startTime, it.length))
                val box = AxisAlignedBB(it.pos).scale(multiplier.toDouble())

                var color = if (it.isAir) ColorRGB(32, 255, 32)
                else ColorRGB(255, 32, 32)

                if (!silent) {
                    color = if ((!it.isAir || PacketMine.world?.getBlockState(it.pos)?.block != Blocks.AIR)
                        && (instantMine && it.mined || System.currentTimeMillis() > it.endTime))
                        ColorRGB(32, 255, 32)
                    else ColorRGB(255, 32, 32)
                }

                RenderUtils3D.drawFullBox(box.interp(), 1f, color.alpha(40).toArgb())
//                renderer.add(box, color)
//                renderer.render(true)
            }
        }

        safeListener<PacketEvent.Receive> { event ->
            val miningInfo = miningInfo0 ?: return@safeListener

            if (event.packet is SPacketBlockChange && event.packet.blockPosition == miningInfo.pos) {
                val newBlockState = event.packet.blockState
                val current = world.getBlock(miningInfo.pos)
                val new = newBlockState.block

                if (new != current) {
                    if (new == Blocks.AIR) {
                        miningInfo.isAir = true
                        miningInfo.mined = true

                        if (!instantMine) {
                            reset()
                            miningQueue.values.remove(miningInfo.pos)
                        }
                    } else {
                        miningInfo.isAir = false

                        if (instantMine) {
                            findBestTool(newBlockState)?.let {
                                spoofHotbar(it) {
                                    sendMiningPacket(miningInfo)
                                }
                            }
                        }
                    }
                }
            }
        }

        safeListener<OnUpdateWalkingPlayerEvent.Pre> {
            if (rotation) {
                miningInfo0?.let {
                    if (!it.isAir && (instantMine && it.mined || it.endTime - System.currentTimeMillis() <= rotateTime)) {
                        sendPlayerPacket {
                            rotate(getRotationTo(it.pos.toVec3dCenter()))
                        }
                    }
                }
            }
        }

        listener<InteractEvent.Block.LeftClick> {
            mineBlock(this, it.pos)
            if (miningInfo0?.pos == it.pos) it.cancel()
        }

        listener<InteractEvent.Block.Damage> {
            mineBlock(PacketMine, it.pos)
            if (it.pos == miningInfo0?.pos) it.cancel()
        }

        safeConcurrentListener<TickEvent> {
            updateMining()
        }

        safeListener<RunGameLoopEvent.Tick> {
            val miningInfo = miningInfo0 ?: return@safeListener
            if (player.getDistanceSqToCenter(miningInfo.pos) > range.sq) {
                reset()
                return@safeListener
            }

            val blockState = world.getBlockState(miningInfo.pos)
            miningInfo.isAir = blockState.block == Blocks.AIR

            if (spamPackets) {
                if (packetTimer.tick(packetDelay)) {
                    val slot = findBestTool(blockState)
                    if (slot != null && isFinished(miningInfo, blockState) && checkRotation(miningInfo)) {
                        spoofHotbar(slot) {
                            sendMiningPacket(miningInfo)
                        }
                        swingMode.swingHand(this, EnumHand.MAIN_HAND)
                    } else {
                        sendMiningPacket(miningInfo)
                    }
                }
            } else {
                if (isFinished(miningInfo, blockState) && checkRotation(miningInfo)) {
                    findBestTool(blockState)?.let {
                        spoofHotbar(it) {
                            sendMiningPacket(miningInfo)
                            swingMode.swingHand(this, EnumHand.MAIN_HAND)
                        }
                    }
                    reset(miningInfo.owner)
                }
            }
        }
    }

    fun mineBlock(module: AbstractModule, pos: BlockPos) {
        runSafe {
            miningQueue[module] = pos
            updateMining()
        }
    }

    fun reset(module: AbstractModule) {
        runSafe {
            miningQueue.remove(module)
            updateMining()
        }
    }

    private fun SafeClientEvent.updateMining() {
        var lastPair = miningQueue.lastEntryOrNull()

        while (lastPair != null && (lastPair.key.isDisabled || !instantMine && world.isAir(lastPair.value))) {
            lastPair = miningQueue.pollLastEntry()
        }

        lastPair?.let {
            mineBlock(it.key, it.value)
        } ?: run {
            reset()
        }
    }

    private fun SafeClientEvent.mineBlock(owner: AbstractModule, pos: BlockPos) {
        if (world.canBreakBlock(pos) && pos != miningInfo0?.pos) {
            if (player.getDistanceSqToCenter(pos) > range * range) return

            val breakTime = calcBreakTime(pos)
            if (breakTime == -1L) return

            reset()
            val side = getMiningSide(pos) ?: run {
                val vector = player.eyePosition.subtract(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                EnumFacing.getFacingFromVector(vector.x.toFloat(), vector.y.toFloat(), vector.z.toFloat())
            }
            miningInfo0 = MiningInfo(owner, pos, side, breakTime + breakOffset)
            packetTimer.reset(-69420)

            connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side))
            connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side))
            if (noAnimation) connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, side))
            if (!noSwing) swingMode.swingHand(this, EnumHand.MAIN_HAND)
        }
    }

    private fun SafeClientEvent.checkRotation(miningInfo: MiningInfo): Boolean {
        val eyeHeight = player.getEyeHeight().toDouble()
        return !rotation || AxisAlignedBB(miningInfo.pos).isInSight(PlayerPacketManager.position.add(0.0, eyeHeight, 0.0), rotation = PlayerPacketManager.rotation) != null
    }

    private fun isFinished(miningInfo: MiningInfo, blockState: IBlockState): Boolean {
        return if (!silent) false
        else (!miningInfo.isAir || blockState.block != Blocks.AIR) && (instantMine && miningInfo.mined || System.currentTimeMillis() > miningInfo.endTime)
    }

    private fun SafeClientEvent.calcBreakTime(pos: BlockPos): Long {
        val blockState = world.getBlockState(pos)

        val hardness = blockState.getBlockHardness(world, pos)
        val breakSpeed = getBreakSpeed(blockState)
        if (breakSpeed == -1.0f) {
            return -1L
        }

        val relativeDamage = breakSpeed / hardness / 30.0f
        val ticks = (0.7f / relativeDamage).fastCeil()

        return ticks * 50L
    }

    private fun SafeClientEvent.getBreakSpeed(blockState: IBlockState): Float {
        var maxSpeed = 1.0f

        for (slot in player.hotbarSlots) {
            val stack = slot.stack

            if (stack.isEmpty || !stack.item.isTool) {
                continue
            } else {
                var speed = stack.getDestroySpeed(blockState)

                if (speed <= 1.0f) {
                    continue
                } else {
                    val efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)
                    if (efficiency > 0) {
                        speed += efficiency * efficiency + 1.0f
                    }
                }

                if (speed > maxSpeed) {
                    maxSpeed = speed
                }
            }
        }

        return maxSpeed
    }

    private fun SafeClientEvent.sendMiningPacket(miningInfo: MiningInfo) {
        connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, miningInfo.pos, miningInfo.side))
        if (noAnimation && !miningInfo.mined) connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, miningInfo.pos, miningInfo.side))
        packetTimer.reset()
    }

    private fun reset() {
        packetTimer.reset(-69420)
        miningInfo0?.let {
            miningInfo0 = null
        }
    }

    interface IMiningInfo {
        val pos: BlockPos
        val side: EnumFacing
    }

    private class MiningInfo(val owner: AbstractModule, override val pos: BlockPos, override val side: EnumFacing, val length: Long) :
        IMiningInfo {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + length
        var isAir = false
        var mined = false
    }
}