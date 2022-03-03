package com.client.epsilon.launch

import com.client.epsilon.launch.InitManager
import com.client.epsilon.launch.MixinLoader
import com.mrcrayfish.controllable.asm.ControllableAccessTransformer
import java.util.Map
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@IFMLLoadingPlugin.TransformerExclusions(value={"com.mrcrayfish.controllable.asm"})
@IFMLLoadingPlugin.SortingIndex(value=1001)
@IFMLLoadingPlugin.Name(value="EpsilonFMLLoader")
@IFMLLoadingPlugin.MCVersion(value="1.12.2")
class FMLCoreMod : IFMLLoadingPlugin {
	
	val Companion = Companion(null)
	val log = LogManager.getLogger("Epsilon Loader")

    init {
        log.info("Loading Epsilon ModLauncher")
        InitManager.INSTANCE.load()
        log.info("Loading Epsilon MixinLoader")
        MixinLoader.INSTANCE.load()
    }

    override fun getASMTransformerClass(): Array<String> {
        return arrayOf()
    }

    override fun getModContainerClass(): String? {
        return null
    }

    override fun getSetupClass(): String? {
        return null
    }

    override fun injectData(data: Map<String, Any>) {}

    override fun getAccessTransformerClass(): String? {
        return null
    }

}