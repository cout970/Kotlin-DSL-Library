package kdl.api

import kdl.api.block.BlockDSL
import kdl.api.item.ItemDSL
import net.minecraft.util.Identifier
import org.apache.logging.log4j.Logger

@DslMarker
annotation class KDL

@KDL
class ModDsl {
    /** The modid is required and must not collide with other mods */
    var modid: String? = null

    /** Human readable name of the mod */
    var name: String? = null

    /** Short description of the mod */
    var description: String? = null
}

interface IModReference {
    val modid: String
    var name: String
    var description: String

    /**
     * Definition of the blocks of the mod, can be called multiple times
     */
    fun blocks(dsl: BlockDSL.() -> Unit)

    /**
     * Definition of the blocks of the mod, can be called multiple times
     */
    fun items(dsl: ItemDSL.() -> Unit)

    /**
     * Creates an id for a path inside this mod
     */
    fun id(path: String): Identifier

    /**
     * Gets the mod logger
     */
    fun logger(): Logger
}