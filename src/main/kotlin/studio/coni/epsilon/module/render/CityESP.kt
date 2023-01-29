package studio.coni.epsilon.module.render

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.extensions.boundingBox
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.ColorRGB
import studio.coni.epsilon.util.combat.CrystalUtils
import studio.coni.epsilon.util.flooredPosition
import studio.coni.epsilon.util.graphics.RenderUtils3D
import studio.coni.epsilon.util.onRender3D
import studio.coni.epsilon.util.onTick
import studio.coni.epsilon.util.threads.runSafe
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

object CityESP : Module(
    name = "CityESP",
    category = Category.Render,
    description = "Render obsidian around player"
) {
    private val range by setting("Range", 6.0, 0.0..50.0, 0.1)
    private val underBlock by setting("UnderBlock", true)
    private val color by setting("Color", ColorRGB(255, 0, 0, 70))
    private val check by setting("CheckCrystal", true)
    private val rMode by setting("Render", RenderMode.Solid)
    private var cityPos = emptyList<BlockPos>()


    init {
        onTick {
            runSafe {
                val newList = ArrayList<BlockPos>()

                for (player in mc.world.playerEntities) {
                    if (player.getDistance(mc.player) > range) continue
                    val doubleTargetPos = player.flooredPosition
                    val posList =
                        if (underBlock)
                            listOf(doubleTargetPos.add(1, 0, 0),
                                doubleTargetPos.add(0, 0, 1),
                                doubleTargetPos.add(-1, 0, 0),
                                doubleTargetPos.add(0, -1, 0),
                                doubleTargetPos.add(0, 0, -1))
                        else
                            listOf(doubleTargetPos.add(1, 0, 0),
                                doubleTargetPos.add(0, 0, 1),
                                doubleTargetPos.add(-1, 0, 0),
                                doubleTargetPos.add(0, 0, -1))
                    for (pos in posList) {
                        if (mc.world.getBlockState(pos).block != Blocks.OBSIDIAN) continue
                        if (check && !ableToPlaceCrystalNearby(pos)) continue
                        newList.add(pos)
                    }
                }

                cityPos = newList
            }
        }

        onRender3D {
            cityPos.forEach {
                drawBlock(it)
            }
        }

    }

    private fun ableToPlaceCrystalNearby(pos: BlockPos): Boolean {
        EnumFacing.HORIZONTALS.forEach {
            if (CrystalUtils.isPlaceable(mc.world, pos.offset(it).down(), false)) {
                return true
            }
        }
        return false
    }

    private fun drawBlock(blockPos: BlockPos) {
        if (rMode == RenderMode.Solid || rMode == RenderMode.SolidFlat) {
            if (rMode == RenderMode.SolidFlat) {
                val bb = blockPos.up().boundingBox
                RenderUtils3D.drawBoundingFilledBox(AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1, bb.maxZ), color.toArgb())
            } else {
                RenderUtils3D.drawBoundingFilledBox(blockPos, color)
            }
        } else {
            if (rMode == RenderMode.Full) {
                RenderUtils3D.drawFullBox(blockPos, 1f, color.toArgb())
            } else if (rMode == RenderMode.Outline) {
                RenderUtils3D.drawBoundingBox(blockPos, 1f, color.toArgb())
            }
        }
    }

    override fun onDisable() {
        cityPos = emptyList()
    }

    enum class RenderMode {
        Solid, SolidFlat, Full, Outline
    }

}