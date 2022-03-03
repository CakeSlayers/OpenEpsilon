package com.client.epsilon

import com.client.epsilon.config.ConfigManager
import com.client.epsilon.event.EventBus.register
import com.client.epsilon.launch.Launch
import com.client.epsilon.manager.CommandManager
import com.client.epsilon.manager.Fonts
import com.client.epsilon.manager.GUIManager
import com.client.epsilon.manager.ModuleManager
import com.client.epsilon.module.general.HUDEditor
import com.client.epsilon.module.general.RootGUI
import com.client.epsilon.util.Logger
import com.client.epsilon.util.graphics.font.renderer.MainFontRenderer
import org.lwjgl.opengl.Display

@SpartanMod(name="Epsilon", id="com/loader/epsilon", version="3.0-beta", description="A Utility mod for Minecraft", mixinFile="mixins.epsilon.json")	
object Aquarius : Loadable{

    const val MOD_NAME = "Epsilon" // As
    const val VERSION = "3.0-beta" //Au

    const val DEFAULT_COMMAND_PREFIX = "." //Aw
    const val DEFAULT_CONFIG_PATH = "Epsilon/" //Ax
    const val SCAN_GROUP = "com/loader/epsilon" //At
    @Override
    public void preInit() {
        boolean bl = false
        IE.Lx.OQ().info("Pre initializing Epsilon")
        Display.setTitle((String)"Epsilon 3.0-beta")
        bp.e9.hQ()
        bp.e9.hP()
    }
@Override
    public void postInit() {
	Object object
	bl = false
		IE.Lx.OQ().info("Post initializing Epsilon")
		/*
        object = 1p.Ib
        bl = true
        boolean bl2 = false
        List<1n> list = "Loading all Spartan configs"
        boolean bl3 = false
        IE.Lx.OQ().info((String)((Object)list))
        list = object
        bl3 = false
        List<1n> list2 = list
        List<1n> list3 = ((1p)((Object)list)).KX()
        boolean bl4 = false
        BuildersKt.runBlocking$default(null, (Function2)new 1r(list3, bl, null), (int)1, null)
        list = object
        bl3 = false
        list2 = list
        list3 = ((1p)((Object)list)).KY()
        bl4 = false
        BuildersKt.runBlocking$default(null, (Function2)new 1r(list3, bl, null), (int)1, null)
        list = ((1p)object).KZ()
        bl3 = false
        BuildersKt.runBlocking$default(null, (Function2)new 1r(list, bl, null), (int)1, null)
        ((1p)object).Lc().oA()
        bt.Uq.00q()
        eW.WR.03R()
        hq.8P.Mz(false, true)
        gd.06d.Mz(false, true)
        this.register(Oc.uq)
        this.register(eW.WR)
        this.register(IZ.9L)
        this.register(LU.vy)
        this.register(IO.ag)
        this.register(LG.Wk)
        this.register(LK.2R)
        this.register(bv.jJ)
        this.register(bx.Pe)
        this.register(d3.08e)
        this.register(d5.f4)
        this.register(d9.TP)
        this.register(dS.06W)
        this.register(dU.08H)
        this.register(ec.5O)
        this.register(eo.Xg)
        this.register(es.xl)
        this.register(eu.aa)
        this.register(eK.XE)
        this.register(eA.hF)
        this.register(G2.iH)
        this.register(fk.s0)
        this.register(fs.XV)
        29.076.0j3()
        d5.f4.iN(d6.PJ)
        7k.hS.lm()
        Az = true // Guess: Az : Ready -> Ready = true
		*/
    }


    @Override
    public void preMinecraftInit() {
        Loadable.DefaultImpls.preMinecraftInit(this)
    }

    @Override
    public void postMinecraftInit() {
        Loadable.DefaultImpls.postMinecraftInit(this)
    }

    @Override
    public void register(Object yee) {
        Loadable.DefaultImpls.register(this, yee)
    }
}