package studio.coni.epsilon.module.render

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.RenderEntityEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.EntityUtil
import studio.coni.epsilon.util.onRender3D
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11

object Chams : Module(
    name = "Chams",
    category = Category.Render,
    description = "See entities through walls"
) {

    private val players by setting("Player", true)
    private val animals by setting("Animal", false)
    private val mobs by setting("Mob", false)


    private fun isValidEntity(entity: Entity): Boolean {
        return entity is EntityLivingBase && players && entity is EntityPlayer || if (EntityUtil.isPassive(
                entity
            )
        ) animals else mobs
    }

    init {
        listener<RenderEntityEvent.All.Pre> {
            if (isValidEntity(it.entity)) {
                GL11.glDepthRange(0.0, 0.01)
            }
        }
        listener<RenderEntityEvent.All.Post> {
            if (isValidEntity(it.entity)) {
                GL11.glDepthRange(0.0, 1.0)
            }
        }

        onRender3D {

        }
    }

}