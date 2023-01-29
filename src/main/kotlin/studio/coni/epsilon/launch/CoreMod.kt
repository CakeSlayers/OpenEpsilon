package studio.coni.epsilon.launch

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

/**
 * @author trdyun
 * Created in 2023/1/28
 */


@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.Name("EpsilonFMLLoader")
@IFMLLoadingPlugin.MCVersion("1.12.2")
class CoreMod : IFMLLoadingPlugin {


    init {
        MixinLoader.load()
    }

    override fun getModContainerClass(): String? = null

    override fun getASMTransformerClass(): Array<String> = emptyArray()

    override fun getSetupClass(): String? = null

    override fun injectData(data: Map<String?, Any?>) {
        return
    }

    override fun getAccessTransformerClass(): String? {
        return null
    }

}
