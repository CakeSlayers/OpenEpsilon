package studio.coni.epsilon.launch

import studio.coni.epsilon.EpsilonPlus


object InitManager {

    @JvmStatic
    fun onMinecraftInit() {

    }

    @JvmStatic
    fun onFinishingInit() {

    }

    @JvmStatic
    fun preInitHook() {
        EpsilonPlus.preInit()
    }

    @JvmStatic
    fun postInitHook() {
        EpsilonPlus.postInit()
    }

}