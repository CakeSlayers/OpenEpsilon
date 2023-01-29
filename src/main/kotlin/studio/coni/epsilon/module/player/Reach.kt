package studio.coni.epsilon.module.player

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.module.Module

object Reach : Module(
    name = "Reach",
    category = Category.Player,
    description = "Allows you to reach farther distances"
) {
    val reachAdd = setting("ReachAdd", 0.5f, 0f..3f, 0.01f)
}