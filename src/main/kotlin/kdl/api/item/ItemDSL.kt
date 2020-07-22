package kdl.api.item

import kdl.api.KDL
import kdl.api.model.JsonModel
import net.minecraft.util.Identifier

interface ItemDSL {
    /**
     * Defines a single block to be added into the game
     */
    fun item(definition: ItemBuilder.() -> Unit): Identifier
}

@KDL
class ItemBuilder {
    /**
     * The internal name of the block
     */
    var name: String? = null

    /**
     * Localized name of the item when no localization file is found
     */
    var defaultLocalizedName: String? = null

    var display: DisplayModel? = null
}


sealed class DisplayModel

class ItemSpriteModel(val path: String) : DisplayModel()

class BlockCubeModel(
    val up: String,
    val down: String,
    val north: String,
    val south: String,
    val east: String,
    val west: String,
    val particle: String
) : DisplayModel() {
    constructor(default: String) : this(default, default, default, default, default, default, default)
}

class CustomDisplayModel(val model: JsonModel) : DisplayModel()
