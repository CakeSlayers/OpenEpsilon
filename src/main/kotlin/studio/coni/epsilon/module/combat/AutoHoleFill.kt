package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.isReplaceable
import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.OnUpdateWalkingPlayerEvent
import studio.coni.epsilon.event.events.Render3DEvent
import studio.coni.epsilon.event.events.RunGameLoopEvent
import studio.coni.epsilon.event.events.WorldEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.event.safeConcurrentListener
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.management.EntityManager
import studio.coni.epsilon.management.HoleManager
import studio.coni.epsilon.management.HotbarManager.spoofHotbar
import studio.coni.epsilon.management.PlayerPacketManager
import studio.coni.epsilon.management.PlayerPacketManager.sendPlayerPacket
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.*
import studio.coni.epsilon.util.combat.HoleType
import studio.coni.epsilon.util.extension.eyePosition
import studio.coni.epsilon.util.graphics.ESPRenderer
import studio.coni.epsilon.util.graphics.Easing
import studio.coni.epsilon.util.graphics.RenderUtils3D
import studio.coni.epsilon.util.inventory.slot.HotbarSlot
import studio.coni.epsilon.util.inventory.slot.firstBlock
import studio.coni.epsilon.util.inventory.slot.hotbarSlots
import studio.coni.epsilon.util.math.RotationUtils.getRotationTo
import studio.coni.epsilon.util.math.isInSight
import studio.coni.epsilon.util.math.sq
import studio.coni.epsilon.util.math.vector.distance
import studio.coni.epsilon.util.math.vector.distanceSqTo
import studio.coni.epsilon.util.math.vector.toVec3d
import studio.coni.epsilon.util.threads.runIf
import studio.coni.epsilon.util.threads.runSynchronized
import it.unimi.dsi.fastutil.longs.Long2LongMaps
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2LongMaps
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

internal object AutoHoleFill :
    Module(name = "AutoHoleFill", description = "Automatically fills hole while enemy walks into hole", category = Category.Combat) {

    private val bedrockHole by setting("Bedrock Hole", true)
    private val obbyHole by setting("Obby Hole", true)
    private val twoBlocksHole by setting("2 Blocks Hole", true)
    private val fourBlocksHole by setting("4 Blocks Hole", true)
    private val targetHole by setting("Target Hole", false)
    private val predictTicks by setting("Predict Ticks", 8, 0..50, 1)
    private val detectRange by setting("Detect Range", 5.0f, 0.0f..16.0f, 0.25f)
    private val hRange by setting("H Range", 0.5f, 0.0f..4.0f, 0.1f)
    private val vRange by setting("V Range", 3.0f, 0.0f..8.0f, 0.1f)
    private val distanceBalance by setting("Distance Balance", 1.0f, -5.0f..5.0f, 0.1f)
    private val fillDelay by setting("Fill Delay", 50, 0..1000, 10)
    private val fillTimeout by setting("Fill Timeout", 100, 0..1000, 10)
    private val fillRange by setting("Fill Range", 5.0f, 1.0f..6.0f, 0.1f)
    private val rotation by setting("Rotation", true)
    private val targetColor by setting("Target Color", ColorRGB(32, 255, 32))
    private val otherColor by setting("Other Color", ColorRGB(255, 222, 32))
    private val filledColor by setting("Filled Color", ColorRGB(255, 32, 32))
    private val blocks by setting("Block", Block.Obsidian)

    private val placeMap = Long2LongMaps.synchronize(Long2LongOpenHashMap())
    private val updateTimer = TickTimer()
    private val placeTimer = TickTimer()

    private var holeInfos = emptyList<IntermediateHoleInfo>()
    private var nextHole: BlockPos? = null
    private val renderBlockMap = Object2LongMaps.synchronize(Object2LongOpenHashMap<BlockPos>())
//    private val renderer = ESPRenderer().apply { aFilled = 33; aOutline = 233 }

    override fun onDisable() {
        holeInfos = emptyList()
        nextHole = null
        renderBlockMap.clear()
//        renderer.replaceAll(Collections.emptyList())
    }

    init {

        listener<WorldEvent.BlockUpdate> {
            if (!it.newState.isReplaceable) {
                placeMap.remove(it.pos.toLong())
                if (it.pos == nextHole) nextHole = null
                renderBlockMap.runSynchronized {
                    replace(it.pos, System.currentTimeMillis())
                }
            }
        }

        listener<Render3DEvent> {
            val list = ArrayList<ESPRenderer.Info>()
            renderBlockMap.runSynchronized {
                object2LongEntrySet().mapTo(list) {
                    val color = when {
                        it.key == nextHole -> targetColor
                        it.longValue == -1L -> otherColor
                        else -> filledColor
                    }

                    if (it.longValue == -1L) {
                        ESPRenderer.Info(it.key, color)
                    } else {
                        val progress = Easing.IN_CUBIC.dec(Easing.toDelta(it.longValue, 500L))
                        val size = progress * 0.5
                        val n = 0.5 - size
                        val p = 0.5 + size
                        val box = AxisAlignedBB(
                            it.key.x + n, it.key.y + n, it.key.z + n,
                            it.key.x + p, it.key.y + p, it.key.z + p,
                        )
                        ESPRenderer.Info(box, color.alpha((255.0f * progress).toInt()))
                    }
                }
            }

            list.forEach {
                RenderUtils3D.drawFullBox(it.box, 1f, it.color.toArgb())
            }

//            renderer.replaceAll(list)
//            renderer.render(false)
        }

        safeListener<OnUpdateWalkingPlayerEvent.Pre> {
            if (rotation) {
                (nextHole ?: getRotationPos(holeInfos))?.let {
                    sendPlayerPacket {
                        rotate(getRotationTo(it.toVec3d(0.5, -0.5, 0.5)))
                    }
                }
            }
        }

        safeConcurrentListener<RunGameLoopEvent.Tick> {
            val slot = player.hotbarSlots.firstBlock(if (blocks == Block.Obsidian) Blocks.OBSIDIAN else Blocks.WEB)
            val place = placeTimer.tick(fillDelay) && slot != null

            if (place || updateTimer.tickAndReset(25L)) {
                val newHoleInfo = getHoleInfos()
                holeInfos = newHoleInfo

                val current = System.currentTimeMillis()
                placeMap.runSynchronized {
                    values.removeIf { it <= current }
                    nextHole?.let {
                        if (!containsKey(it.toLong())) nextHole = null
                    }
                }

                if (place) {
                    getPos(newHoleInfo, rotation)?.let {
                        nextHole = it
                        placeBlock(slot!!, it)
                    }
                } else {
                    updatePosRender(newHoleInfo)
                }
            }
        }
    }

    private fun SafeClientEvent.updatePosRender(holeInfos: List<IntermediateHoleInfo>) {
        val sqRange = detectRange.sq
        val set = LongOpenHashSet()

        for (entity in world.playerEntities) {
            if (entity == player) continue
            if (!entity.isEntityAlive) continue
            if (entity.isFakeOrSelf) continue
            if (entity.isFriend) continue
            if (player.getDistanceSq(entity) > sqRange) continue

            val current = entity.positionVector
            val predict = entity.calcPredict(current)

            for (holeInfo in holeInfos) {
                if (entity.posY <= holeInfo.blockPos.y + 0.5) continue
                if (holeInfo.toward && holeInfo.playerDist - entity.horizontalDist(holeInfo.center) < distanceBalance) continue

                if (holeInfo.detectBox.contains(current) || !holeInfo.toward && (holeInfo.detectBox.contains(predict) || holeInfo.detectBox.calculateIntercept(current, predict) != null)) {
                    set.add(holeInfo.blockPos.toLong())
                    renderBlockMap.putIfAbsent(holeInfo.blockPos, -1L)
                }
            }
        }

        renderBlockMap.runSynchronized {
            object2LongEntrySet().removeIf {
                it.longValue == -1L && !placeMap.containsKey(it.key.toLong()) && !set.contains(it.key.toLong())
            }
        }
    }

    private fun SafeClientEvent.getPos(holeInfos: List<IntermediateHoleInfo>, checkRotation: Boolean): BlockPos? {
        val sqRange = detectRange.sq

        val placeable = Object2FloatOpenHashMap<BlockPos>()

        for (entity in world.playerEntities) {
            if (entity == player) continue
            if (!entity.isEntityAlive) continue
            if (entity.isFakeOrSelf) continue
            if (entity.isFriend) continue
            if (player.getDistanceSq(entity) > sqRange) continue

            val current = entity.positionVector
            val predict = entity.calcPredict(current)

            for (holeInfo in holeInfos) {
                if (entity.posY <= holeInfo.blockPos.y + 0.5) continue
                val dist = entity.horizontalDist(holeInfo.center)
                if (holeInfo.toward && holeInfo.playerDist - dist < distanceBalance) continue

                if (holeInfo.detectBox.contains(current) || !holeInfo.toward && (holeInfo.detectBox.contains(predict) || holeInfo.detectBox.calculateIntercept(current, predict) != null)) {

                    placeable[holeInfo.blockPos] = dist.toFloat()
                    renderBlockMap.putIfAbsent(holeInfo.blockPos, -1L)
                }
            }
        }

        val eyePos = PlayerPacketManager.position.add(0.0, player.getEyeHeight().toDouble(), 0.0)

        val targetPos = placeable.object2FloatEntrySet().asSequence().runIf(checkRotation) {
                filter {
                    AxisAlignedBB(
                        it.key.x.toDouble(), it.key.y - 1.0, it.key.z.toDouble(),
                        it.key.x + 1.0, it.key.y.toDouble(), it.key.z + 1.0,
                    ).isInSight(eyePos, PlayerPacketManager.rotation) != null
                }
            }.minByOrNull { it.floatValue }?.key

        renderBlockMap.runSynchronized {
            object2LongEntrySet().removeIf {
                it.longValue == -1L && !placeMap.containsKey(it.key.toLong()) && !placeable.containsKey(it.key)
            }
        }

        return targetPos
    }

    private fun SafeClientEvent.getRotationPos(holeInfos: List<IntermediateHoleInfo>): BlockPos? {
        val sqRange = detectRange.sq

        var minDist = Double.MAX_VALUE
        var minDistPos: BlockPos? = null

        for (entity in world.playerEntities) {
            if (entity == player) continue
            if (!entity.isEntityAlive) continue
            if (entity.isFakeOrSelf) continue
            if (entity.isFriend) continue
            if (player.getDistanceSq(entity) > sqRange) continue

            val current = entity.positionVector
            val predict = entity.calcPredict(current)

            for (holeInfo in holeInfos) {
                if (entity.posY <= holeInfo.blockPos.y + 0.5) continue

                val dist = entity.horizontalDist(holeInfo.center)
                if (dist >= minDist) continue
                if (holeInfo.toward && holeInfo.playerDist - dist < distanceBalance) continue

                if (holeInfo.detectBox.contains(current) || !holeInfo.toward && (holeInfo.detectBox.contains(predict) || holeInfo.detectBox.calculateIntercept(current, predict) != null)) {

                    minDistPos = holeInfo.blockPos
                    minDist = dist
                }
            }
        }

        return minDistPos
    }

    private fun SafeClientEvent.getHoleInfos(): List<IntermediateHoleInfo> {
        val eyePos = player.eyePosition
        val rangeSq = fillRange.sq
        val entities = EntityManager.entity.filter {
            it.preventEntitySpawning && it.isEntityAlive
        }

        return HoleManager.holeInfos.asSequence().filter {
                when (it.type) {
                    HoleType.BEDROCK -> bedrockHole
                    HoleType.OBBY -> obbyHole
                    HoleType.TWO -> twoBlocksHole
                    HoleType.FOUR -> fourBlocksHole
                    else -> false
                }
            }.filter { holeInfo ->
                holeInfo.holePos.any {
                    eyePos.distanceSqTo(it) <= rangeSq
                }
            }.filter { holeInfo ->
                entities.none {
                    it.entityBoundingBox.intersects(holeInfo.boundingBox)
                }
            }.filter {
                targetHole || it.origin != HoleSnap.hole?.origin
            }.mapNotNull { holeInfo ->
                holeInfo.holePos.asSequence().filter { !placeMap.containsKey(it.toLong()) }.minByOrNull { eyePos.distanceSqTo(it) }?.let {
                        val box = AxisAlignedBB(holeInfo.boundingBox.minX - hRange, holeInfo.boundingBox.minY, holeInfo.boundingBox.minZ - hRange, holeInfo.boundingBox.maxX + hRange, holeInfo.boundingBox.maxY + vRange, holeInfo.boundingBox.maxZ + hRange)

                        if (player.entityBoundingBox.intersects(box)) {
                            null
                        } else {
                            val dist = player.horizontalDist(holeInfo.center)
                            val prevDist = distance(player.lastTickPosX, player.lastTickPosZ, holeInfo.center.x, holeInfo.center.z)
                            IntermediateHoleInfo(holeInfo.center, it, box, dist, holeInfo.origin == HoleSnap.hole?.origin || dist - prevDist < -0.15)
                        }
                    }
            }.toList()
    }

    private fun Entity.horizontalDist(vec3d: Vec3d): Double {
        return distance(this.posX, this.posZ, vec3d.x, vec3d.z)
    }

    private fun Entity.calcPredict(current: Vec3d): Vec3d {
        return if (predictTicks == 0) {
            current
        } else {
            Vec3d(this.posX + (this.posX - this.lastTickPosX) * predictTicks, this.posY + (this.posY - this.lastTickPosY) * predictTicks, this.posZ + (this.posZ - this.lastTickPosZ) * predictTicks)
        }
    }

    private fun SafeClientEvent.placeBlock(slot: HotbarSlot, pos: BlockPos) {
        val target = pos.down()

        val packet = CPacketPlayerTryUseItemOnBlock(target, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5f, 1.0f, 0.5f)

        player.spoofSneak {
            spoofHotbar(slot) {
                connection.sendPacket(packet)
            }
        }

        connection.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))

        placeMap[pos.toLong()] = System.currentTimeMillis() + fillTimeout
        placeTimer.reset()
    }

    private class IntermediateHoleInfo(val center: Vec3d, val blockPos: BlockPos, val detectBox: AxisAlignedBB, val playerDist: Double, val toward: Boolean)

    enum class Block {
        Obsidian, Web
    }
}