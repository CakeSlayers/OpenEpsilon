package studio.coni.epsilon.module.client

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.module.Module

internal object NotificationRender : Module(
    name = "Notification",
    alias = arrayOf("Information"),
    category = Category.Client,
    description = "Setting for notification"
) {

    val duration by setting("Duration", 3, 2..10, 1)
    val yDisplacement by setting("Y Displacement", 0, 0..150, 1)

}