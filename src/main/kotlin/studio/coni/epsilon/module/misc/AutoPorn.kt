package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.module.Module
import java.awt.Desktop
import java.net.URI

object AutoPorn : Module(name = "AutoPorn", category = Category.Misc, description = "Auto go to porn website") {
    private val site by setting("Site", Site.nhentai)

    override fun onEnable() {
        if (site == Site.nhentai) {
            Desktop.getDesktop().browse(URI("https://nhentai.net/random/"))
        } else {
            Desktop.getDesktop().browse(URI("www." + site.name + ".com"))
        }
        disable(notification = false, silent = true)
    }

    enum class Site {
        nhentai, Pornhub, XVideos, Redtube, XHamster, YouPorn, Tube8, ThisAV
    }
}