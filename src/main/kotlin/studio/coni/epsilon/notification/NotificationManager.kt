package studio.coni.epsilon.notification

import studio.coni.epsilon.event.decentralized.IDecentralizedEvent
import studio.coni.epsilon.event.decentralized.Listenable
import studio.coni.epsilon.management.SpartanCore.addAsyncUpdateListener
import studio.coni.epsilon.module.client.NotificationRender
import studio.coni.epsilon.util.Timer
import studio.coni.epsilon.util.onRender2D
import java.util.concurrent.LinkedBlockingQueue

object NotificationManager : Listenable {

    override val subscribedListener = ArrayList<Triple<IDecentralizedEvent<*>, (Any) -> Unit, Int>>()

    private var lastRenderTime = System.currentTimeMillis()

    val timer = Timer()

    init {
        onRender2D {
            if (NotificationRender.isEnabled) render()
        }

        addAsyncUpdateListener {
            if (NotificationRender.isEnabled && System.currentTimeMillis() - lastRenderTime <= 10000) {
                if (timer.passed(13)) {
                    timer.reset()
                    notifications.forEach {
                        it.update()
                    }
                }
            }
        }
    }

    internal var notifications = LinkedBlockingQueue<Notification>()

    fun show(notificationIn: Notification) {
        if (NotificationRender.isEnabled) {
            notifications.add(notificationIn)
            lastRenderTime = System.currentTimeMillis()
        }
    }

    fun render() {
        notifications.forEach {
            it.draw()
        }
    }

}