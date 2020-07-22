package kdl.api.model

import net.minecraft.client.render.model.json.ModelElement

class JsonModel {
    var parent: String? = null
    val textures: MutableMap<String, String> = mutableMapOf()
    var elements: MutableList<ModelElement> = mutableListOf()
}