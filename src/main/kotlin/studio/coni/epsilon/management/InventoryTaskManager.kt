package studio.coni.epsilon.management

import studio.coni.epsilon.event.SafeClientEvent
import studio.coni.epsilon.event.events.ConnectionEvent
import studio.coni.epsilon.event.events.RunGameLoopEvent
import studio.coni.epsilon.event.listener
import studio.coni.epsilon.event.safeListener
import studio.coni.epsilon.util.TickTimer
import studio.coni.epsilon.util.TpsCalculator
import studio.coni.epsilon.util.inventory.ClickFuture
import studio.coni.epsilon.util.inventory.InventoryTask
import studio.coni.epsilon.util.inventory.StepFuture
import studio.coni.epsilon.util.inventory.removeHoldingItem
import studio.coni.epsilon.util.onPacketReceive
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.network.play.server.SPacketConfirmTransaction
import java.util.*

object InventoryTaskManager {
    private val confirmMap = HashMap<Short, ClickFuture>()
    private val taskQueue = PriorityQueue<InventoryTask>()
    private val timer = TickTimer()
    private var lastTask: InventoryTask? = null

    init {
        onPacketReceive {
            if (it.packet !is SPacketConfirmTransaction) return@onPacketReceive
            synchronized(InventoryTaskManager) {
                confirmMap.remove(it.packet.actionNumber)?.confirm()
            }
        }

        safeListener<RunGameLoopEvent.Render> {
            if (lastTask == null && taskQueue.isEmpty()) return@safeListener
            if (!timer.tick(0L)) return@safeListener

            lastTaskOrNext()?.let {
                runTask(it)
            }
        }

        listener<ConnectionEvent.Disconnect> {
            reset()
        }
    }

    fun addTask(task: InventoryTask) {
        synchronized(InventoryTaskManager) {
            taskQueue.add(task)
        }
    }

    fun runNow(event: SafeClientEvent, task: InventoryTask) {
        event {
            if (!player.inventory.itemStack.isEmpty) {
                removeHoldingItem()
            }

            while (!task.finished) {
                task.runTask(event)?.let {
                    handleFuture(it)
                }
            }

            timer.reset((task.postDelay * TpsCalculator.getMultiplier()).toLong())
        }
    }

    private fun SafeClientEvent.lastTaskOrNext(): InventoryTask? {
        return lastTask ?: run {
            val newTask = synchronized(InventoryTaskManager) {
                taskQueue.poll()?.also { lastTask = it }
            } ?: return null

            if (!player.inventory.itemStack.isEmpty) {
                removeHoldingItem()
                return null
            }

            newTask
        }
    }

    private fun SafeClientEvent.runTask(task: InventoryTask) {
        if (mc.currentScreen is GuiContainer && !task.runInGui && !player.inventory.itemStack.isEmpty) {
            timer.reset(500L)
            return
        }

        if (task.delay == 0L) {
            runNow(this, task)
        } else {
            task.runTask(this)?.let {
                handleFuture(it)
                timer.reset((task.delay * TpsCalculator.getMultiplier()).toLong())
            }
        }

        if (task.finished) {
            timer.reset((task.postDelay * TpsCalculator.getMultiplier()).toLong())
            lastTask = null
            return
        }
    }

    private fun handleFuture(future: StepFuture) {
        if (future is ClickFuture) {
            synchronized(InventoryTaskManager) {
                confirmMap[future.id] = future
            }
        }
    }

    private fun reset() {
        synchronized(InventoryTaskManager) {
            confirmMap.clear()
            lastTask?.cancel()
            lastTask = null
            taskQueue.clear()
        }
    }

}