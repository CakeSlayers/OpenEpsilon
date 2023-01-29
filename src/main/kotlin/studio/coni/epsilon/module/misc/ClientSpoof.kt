package studio.coni.epsilon.module.misc

import studio.coni.epsilon.common.Category
import studio.coni.epsilon.module.Module

internal object ClientSpoof : Module(
    name = "ClientSpoof",
    description = "Fakes a modless client when connecting",
    category = Category.Misc
) {

    val client by setting("Client", Client.Vanilla)

    enum class Client {
        Lunar,
        Vanilla
    }
}
