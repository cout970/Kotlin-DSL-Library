package example_mod

import items
import kdl.api.API
import kdl.api.IModReference
import net.fabricmc.api.ModInitializer

val ExampleModRef: IModReference = API.mod {
    modid = "example_mod"
    name = "Example mod"
    description = "A simple mod showing the features of the API"
}

@Suppress("unused")
class ExampleMod : ModInitializer {

    override fun onInitialize() {
        items()
        blocks()
        furnaceBlock()
        Storage
    }
}

