package studio.coni.epsilon.module.client

import org.lwjgl.input.Keyboard
import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.Render3DEvent
import studio.coni.epsilon.event.events.TickEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.text.ChatUtil

/**
 * @author trdyun
 * Created in 2023/1/29
 */
internal object Test : Module(
    name = "Test",
    category = Category.Client,
    visibleOnArray = false,
    description = "wow!",
    keyBind = Keyboard.KEY_M
) {

}