package studio.coni.epsilon.module.combat

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.event.decentralized.decentralizedListener
import studio.coni.epsilon.event.decentralized.events.player.OnUpdateWalkingPlayerDecentralizedEvent
import studio.coni.epsilon.module.Module
import studio.coni.epsilon.util.Timer
import studio.coni.epsilon.util.threads.runSafe
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack

object AutoTotem : Module(name = "AutoTotem", category = Category.Combat, description = "Auto switch totem to offhand") {


    private val force by setting("Force", false)
    private val soft by setting("Soft", false)
    private val pauseInInventory by setting("PauseInInventory", true)
    private val pauseInContainers by setting("PauseInContainer", false)
    private val hotbar by setting("HotBar", false)
    private val health by setting("Health", 20, 0..36, 1)
    private val delay by setting("Delay", 0, 0..100, 1)

    var timer = Timer()
    private var totems = 0

    init {
        decentralizedListener(OnUpdateWalkingPlayerDecentralizedEvent.Pre) {
            runSafe {

                if (pauseInContainers && mc.currentScreen is GuiContainer && mc.currentScreen !is GuiInventory) {
                    return@runSafe
                }

                if (pauseInInventory && mc.currentScreen is GuiInventory) {
                    return@runSafe
                }

                totems = mc.player.inventory.mainInventory.stream().filter { itemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
                if (mc.player.heldItemOffhand.item === Items.TOTEM_OF_UNDYING) {
                    totems++
                }

                if (totems == 0) {
                    return@runSafe
                }

                var shouldEquip = mc.player.health <= health && !force

                if (force) {
                    shouldEquip = true
                }

                if (shouldEquip) {
                    val slot = getTotemSlot()
                    if (slot == -1) return@runSafe
                    if (if (soft) mc.player.heldItemOffhand.item == Items.AIR else mc.player.heldItemOffhand.item !== Items.TOTEM_OF_UNDYING) {
                        if (delay == 0 || timer.passed(delay)) {
                            mc.playerController.windowClick(0, if (slot < 9) slot + 36 else slot, 0, ClickType.PICKUP, mc.player)
                            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player)
                            mc.playerController.windowClick(0, if (slot < 9) slot + 36 else slot, 0, ClickType.PICKUP, mc.player)
                            mc.playerController.updateController()
                            timer.reset()
                        }
                    }
                }
                if (hotbar) {
                    if (mc.player.inventory.getStackInSlot(0).item != Items.TOTEM_OF_UNDYING) {
                        for (i in 9..34) {
                            if (mc.player.inventory.getStackInSlot(i).item == Items.TOTEM_OF_UNDYING) {
                                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, mc.player)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getTotemSlot(): Int {
        var i = 0
        while (i < 36) {
            val item = mc.player.inventory.getStackInSlot(i).item
            if (item === Items.TOTEM_OF_UNDYING) {
                if (i < 9) {
                    i += 36
                }
                return i
            }
            i++
        }
        return -1
    }


    override fun getHudInfo(): String {
        return totems.toString()
    }
}