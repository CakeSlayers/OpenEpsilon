package studio.coni.epsilon.management

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.common.extensions.runSafeTask
import studio.coni.epsilon.concurrent.MainThreadExecutor
import studio.coni.epsilon.event.EventBus
import studio.coni.epsilon.event.decentralized.IDecentralizedEvent
import studio.coni.epsilon.event.decentralized.Listenable
import studio.coni.epsilon.event.decentralized.decentralizedListener
import studio.coni.epsilon.event.decentralized.events.player.OnUpdateWalkingPlayerDecentralizedEvent
import studio.coni.epsilon.event.events.OnUpdateWalkingPlayerEvent
import studio.coni.epsilon.event.events.ResolutionUpdateEvent
import studio.coni.epsilon.event.events.SpartanTick
import studio.coni.epsilon.gui.IFatherExtendable
import studio.coni.epsilon.gui.def.DefaultRootScreen
import studio.coni.epsilon.module.setting.GuiSetting
import studio.coni.epsilon.setting.AbstractSetting
import studio.coni.epsilon.util.Timer
import studio.coni.epsilon.util.Wrapper.mc
import studio.coni.epsilon.util.graphics.GlStateUtils
import studio.coni.epsilon.util.onTick
import studio.coni.epsilon.util.threads.SpartanJob
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

@Suppress("NOTHING_TO_INLINE")
object SpartanCore : Listenable {

    override val subscribedListener = ArrayList<Triple<IDecentralizedEvent<*>, (Any) -> Unit, Int>>()

    @OptIn(ObsoleteCoroutinesApi::class)
    private object SpartanScope : CoroutineScope by CoroutineScope(
        newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors(), "Spartan Core Scope")
    )

    val bus = CopyOnWriteArrayList<Any>()
    val settingBus = CopyOnWriteArrayList<AbstractSetting<*>>()
    val extendableBus = CopyOnWriteArrayList<IFatherExtendable>()

    val asyncListeners = ConcurrentHashMap<Any, () -> Unit>()

    private val timer = Timer()
    var updates = 0
    private var lastClearTime = 0L

    val asyncTaskQueue = LinkedBlockingQueue<SpartanJob>()

    private var prevWidth = -1
    private var prevHeight = -1

    init {
        decentralizedListener(OnUpdateWalkingPlayerDecentralizedEvent.Pre) {
            OnUpdateWalkingPlayerEvent.Pre(it.position, it.rotation, it.onGround).post()
        }
        decentralizedListener(OnUpdateWalkingPlayerDecentralizedEvent.Post) {
            OnUpdateWalkingPlayerEvent.Post(it.position, it.rotation, it.onGround).post()
        }

        subscribe()

        onTick {
            if (timer.passed(1000) && mc.currentScreen == DefaultRootScreen) {
                timer.reset()
                updateSettings()
            }
            if (prevWidth != mc.displayWidth || prevHeight != mc.displayHeight) {
                prevWidth = mc.displayWidth
                prevHeight = mc.displayHeight
                ResolutionUpdateEvent(mc.displayWidth, mc.displayHeight).post()
                GlStateUtils.useProgramForce(0)
            }
        }
        SpartanScope.launch(Dispatchers.Default) {
            while (true) {
                if (EpsilonPlus.isReady) {
                    while (true) {
                        val task = asyncTaskQueue.poll()
                        if (task != null) task.execute()
                        else break
                    }
                    SpartanTick.post()
                }
                if (!GuiSetting.unlimited) {
                    delay(10)
                }
            }
        }
        SpartanScope.launch(Dispatchers.Default) {
            var ticks = 0
            while (true) {
                val tookTime = measureTimeMillis {
                    ticks++
                    if (System.currentTimeMillis() - lastClearTime >= 1000) {
                        lastClearTime = System.currentTimeMillis()
                        updates = ticks
                        ticks = 0
                    }
                    if (EpsilonPlus.isReady) {
                        extendableBus.forEach {
                            runSafeTask(false) {
                                it.updateChildren()
                            }
                        }
                        asyncListeners.values.forEach {
                            runSafeTask(false) {
                                it.invoke()
                            }
                        }
                    }
                }
                if (!GuiSetting.unlimited) {
                    val limit = (1000 / GuiSetting.concurrentRefreshRate).toLong()
                    if (tookTime < limit) delay(limit - tookTime)
                }
            }
        }
    }

    inline fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        noinline block: suspend CoroutineScope.() -> Unit
    ): Job {
        return SpartanScope.launch(context, start, block)
    }

    inline fun addScheduledTask(mode: Executor = Executor.Spartan, noinline task: () -> Unit): SpartanJob {
        return SpartanJob(task).also {
            when (mode) {
                Executor.Spartan -> asyncTaskQueue.add(it)
                Executor.Main -> MainThreadExecutor.add {
                    it.execute()
                }
                Executor.Coroutines -> SpartanScope.launch {
                    it.execute()
                }
            }
        }
    }

    inline fun Any.removeAsyncUpdateListener(noinline listener: () -> Unit) {
        if (asyncListeners.contains(listener) && asyncListeners.containsKey(this)) asyncListeners.remove(this, listener)
    }

    inline fun Any.addAsyncUpdateListener(noinline listener: () -> Unit) {
        if (!asyncListeners.contains(listener) && !asyncListeners.containsKey(this)) asyncListeners[this] = listener
    }

    private val canUpdateSettings = AtomicBoolean(true)

    @Synchronized
    fun updateSettings() {
        if (canUpdateSettings.get()) {
            canUpdateSettings.set(false)
            addScheduledTask {
                settingBus.forEach {
                    it.isVisible = it.visibility.invoke()
                }
                canUpdateSettings.set(true)
            }
        }
    }

    inline fun register(it: Any) {
        bus.add(it)
        if (it is Listenable) it.subscribe()
        EventBus.subscribe(it)
    }

    inline fun unregister(it: Any) {
        bus.remove(it)
        if (it is Listenable) it.unsubscribe()
        EventBus.unsubscribe(it)
    }

    inline fun registerSetting(it: AbstractSetting<*>) {
        settingBus.add(it)
    }

    inline fun registerExtendable(it: IFatherExtendable) {
        extendableBus.add(it)
    }

    enum class Executor {
        Main,
        Spartan,
        Coroutines
    }

}