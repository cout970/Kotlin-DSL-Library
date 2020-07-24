package kdl.api.block

import kdl.api.KDL
import kdl.api.block.blockentity.ModuleBuilder
import kdl.api.gui.GuiBuilder
import kdl.api.item.ItemBuilder
import net.minecraft.util.Identifier

interface BlockDSL {

    /**
     * Defines a single block to be added into the game
     */
    fun block(definition: BlockBuilder.() -> Unit): Identifier
}

@KDL
class BlockBuilder {
    /**
     * The internal name of the block
     */
    var name: String? = null

    /**
     * The material of the block
     */
    var material: Identifier? = null

    /**
     * Item that represents the block in inventories
     */
    var blockItem: ItemBuilder? = null

    /**
     * Use the blockEntity builder function instead
     */
    var blockEntityConfig: BlockEntityBuilder? = null

    /**
     * Use the blockState builder function instead
     */
    var blockStateConfig: BlockStateBuilder? = null

    /**
     * Use the gui builder function instead
     */
    var guiConfig: (GuiBuilder.() -> Unit)? = null

    // Events

    var onUse: (BlockOnUse.() -> Unit)? = null
    var onBreak: (BlockOnBreak.() -> Unit)? = null
    var onBroken: (BlockOnBroken.() -> Unit)? = null
    var onDestroyedByExplosion: (BlockOnDestroyedByExplosion.() -> Unit)? = null
    var onEntityLand: (BlockOnEntityLand.() -> Unit)? = null
    var onLandedUpon: (BlockOnLandedUpon.() -> Unit)? = null
    var onPlaced: (BlockOnPlaced.() -> Unit)? = null
    var onSteppedOn: (BlockOnSteppedOn.() -> Unit)? = null
    var onBlockAdded: (BlockOnBlockAdded.() -> Unit)? = null
    var onEntityCollision: (BlockOnEntityCollision.() -> Unit)? = null
    var onProjectileHit: (BlockOnProjectileHit.() -> Unit)? = null
    var onStacksDropped: (BlockOnStacksDropped.() -> Unit)? = null
    var onStateReplaced: (BlockOnStateReplaced.() -> Unit)? = null
    var onSyncedBlockEvent: (BlockOnSyncedBlockEvent.() -> Unit)? = null
    var placementState: (BlockPlacementState.() -> Unit)? = null

    fun item(func: ItemBuilder.() -> Unit) {
        val builder = blockItem ?: ItemBuilder()
        blockItem = builder.apply(func)
    }

    /**
     * Defines a BlockEntity for this block
     */
    fun blockEntity(func: BlockEntityBuilder.() -> Unit) {
        val builder = blockEntityConfig ?: BlockEntityBuilder()
        blockEntityConfig = builder.apply(func)
    }

    /**
     * Defines a blockstate json
     */
    fun blockState(func: BlockStateBuilder.() -> Unit) {
        val builder = blockStateConfig ?: BlockStateBuilder()
        blockStateConfig = builder.apply(func)
    }

    /**
     * Defines a gui for this block
     */
    fun gui(func: GuiBuilder.() -> Unit) {
        guiConfig = func
    }
}

@KDL
class BlockEntityBuilder {

    val modules = mutableMapOf<Identifier, ModuleBuilder<*>>()
    var renderDistance: Double = 64.0
    var type: Identifier? = null

    fun <T> module(func: ModuleBuilder<T>.() -> Unit) {
        val dsl = ModuleBuilder<T>().apply(func)
        if (dsl.moduleType == null) {
            error("Module defined without id")
        }
        modules[dsl.type] = dsl
    }
}
