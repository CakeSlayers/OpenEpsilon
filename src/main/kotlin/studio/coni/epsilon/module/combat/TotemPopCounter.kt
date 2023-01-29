package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.module.Module

object TotemPopCounter : Module(
    name = "TotemPopCounter",
    category = Category.Combat,
    description = "Counts the times your enemy pops"
) {
    val notification by setting("Notification", false)
    val chat by setting("UesChat", true)
    val rawChat by setting("RawChat Message", false) { chat }
}