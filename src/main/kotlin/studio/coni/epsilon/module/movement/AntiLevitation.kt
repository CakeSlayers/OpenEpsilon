package studio.coni.epsilon.module.movement

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.TickEvent
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.module.Module
import net.minecraft.init.MobEffects

internal object AntiLevitation : Module(
    name = "AntiLevitation",
    description = "Removes levitation potion effect",
    category = Category.Movement
) {
    init {
        safeListener<TickEvent.Pre> {
            player.removeActivePotionEffect(MobEffects.LEVITATION)
        }
    }
}