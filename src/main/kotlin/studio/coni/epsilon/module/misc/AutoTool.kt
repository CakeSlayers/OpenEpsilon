package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.InteractEvent
import studio.coni.epsilon.event.events.PlayerAttackEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.combat.CombatUtils
import studio.coni.epsilon.util.combat.CombatUtils.equipBestWeapon
import studio.coni.epsilon.util.inventory.equipBestTool
import net.minecraft.entity.EntityLivingBase

internal object AutoTool : Module(
    name = "AutoTool",
    description = "Automatically switch to the best tools when mining or attacking",
    category = Category.Misc
) {
    private val swapWeapon by setting("Switch Weapon", false)
    private val preferWeapon by setting("Prefer", CombatUtils.PreferWeapon.SWORD)

    init {
        safeListener<InteractEvent.Block.LeftClick> {
            if (!player.isCreative && world.getBlockState(it.pos).getBlockHardness(world, it.pos) != -1.0f) {
                equipBestTool(world.getBlockState(it.pos))
            }
        }

        safeListener<PlayerAttackEvent> {
            if (swapWeapon && it.entity is EntityLivingBase) {
                equipBestWeapon(preferWeapon)
            }
        }
    }
}