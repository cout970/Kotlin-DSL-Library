package kdl.internal

import kdl.api.IModReference
import kdl.api.block.BlockDSL
import kdl.api.item.ItemDSL
import kdl.internal.block.KDLBlockDSL
import kdl.internal.item.KDLItemDSL
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ModReference(
    override val modid: String,
    override var name: String,
    override var description: String
) : IModReference {

    private val logger = LogManager.getLogger(modid)

    override fun blocks(dsl: BlockDSL.() -> Unit) {
        try {
            KDLBlockDSL(this).apply(dsl)
        } catch (e: Exception) {
            logger.error("Exception in block definition", e)
            throw e
        }
    }

    override fun items(dsl: ItemDSL.() -> Unit) {
        try {
            KDLItemDSL(this).apply(dsl)
        } catch (e: Exception) {
            logger.error("Exception in item definition", e)
            throw e
        }
    }

    override fun id(path: String): Identifier = Identifier(modid, path)

    override fun logger(): Logger = logger
}