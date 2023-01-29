package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.events.OnUpdateWalkingPlayerEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.management.FriendManager
import studio.coni.epsilon.management.FriendManager.isFriend
import studio.coni.epsilon.mixin.mixins.accessor.AccessorMinecraft
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.ItemUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.util.math.RayTraceResult
import org.lwjgl.input.Mouse


object MiddleClick : Module(
    name = "MiddleClick",
    category = Category.Misc,
    description = "Bind you mouse middle button with some feature",
    visibleOnArray = false
) {
    private var friend by setting("Friend", true)
    private var pearl by setting("Pearl", false)
    private var lastSlot = 0
    private var clicked = false

    init {
        listener<OnUpdateWalkingPlayerEvent.Pre> {
            if (!Mouse.isButtonDown(2)) {
                clicked = false
                return@listener
            }

            val result = mc.objectMouseOver ?: return@listener

            if (!clicked) {
                clicked = true
                if (friend && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit is EntityPlayer) {
                    if (isFriend(result.entityHit)) {
                        FriendManager.removeFriend(result.entityHit)
                    } else {
                        FriendManager.addFriend(result.entityHit)
                    }
                } else if (pearl && ItemUtil.findItemInHotBar(Items.ENDER_PEARL) != -1 && result.typeOfHit == RayTraceResult.Type.MISS) {
                    lastSlot = mc.player.inventory.currentItem
                    mc.player.inventory.currentItem = ItemUtil.findItemInHotBar(Items.ENDER_PEARL)
                    (mc as AccessorMinecraft).invokeRightClickMouse()
                    mc.player.inventory.currentItem = lastSlot
                }
            }
        }
    }

    override fun onDisable() {
        lastSlot = -1
    }
}