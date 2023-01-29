package studio.coni.epsilon.event

import studio.coni.epsilon.ForgeRegister
import studio.coni.epsilon.event.decentralized.events.client.Render3DDecentralizedEvent
import studio.coni.epsilon.event.events.Render3DEvent
import studio.coni.epsilon.management.WorldManager
import studio.coni.epsilon.util.graphics.GlStateUtils
import studio.coni.epsilon.util.graphics.ProjectionUtils
import studio.coni.epsilon.util.graphics.RenderUtils3D
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ForgeAccessor {

    fun subscribe() {
        ForgeRegister.forceRegister(ForgeAccessor)
    }

    @SubscribeEvent
    fun onRender3D(event: RenderWorldLastEvent) {
        ProjectionUtils.updateMatrix()
        RenderUtils3D.prepareGL()
        Render3DDecentralizedEvent.post(Render3DDecentralizedEvent.Render3DEventData(event.partialTicks))
        EventBus.post(Render3DEvent(event.partialTicks))
        RenderUtils3D.releaseGL()
        GlStateUtils.useProgramForce(0)
    }

    @SubscribeEvent
    fun onLoadWorld(event: WorldEvent.Load) {
        if (event.world.isRemote) {
            event.world.addEventListener(WorldManager)
            studio.coni.epsilon.event.events.WorldEvent.Load.post()
        }
    }

    @SubscribeEvent
    fun onUnloadWorld(event: WorldEvent.Unload) {
        if (event.world.isRemote) {
            event.world.removeEventListener(WorldManager)
            studio.coni.epsilon.event.events.WorldEvent.Unload.post()
        }
    }

}