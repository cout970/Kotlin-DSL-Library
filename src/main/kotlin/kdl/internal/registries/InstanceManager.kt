package kdl.internal.registries

import kdl.api.block.BlockBuilder
import kdl.api.block.BlockEntityBuilder
import kdl.api.gui.GuiBuilder
import kdl.api.item.ItemBuilder
import kdl.internal.block.KDLBlock
import kdl.internal.block.KDLBlockWithEntity
import kdl.internal.block.blockentity.KDLBlockEntity
import kdl.internal.block.blockentity.KDLTickableBlockEntity
import kdl.internal.gui.KDLScreen
import kdl.internal.gui.KDLScreenHandler
import kdl.internal.item.KDLBlockItem
import kdl.internal.item.KDLItem
import kdl.internal.registries.InstanceManager.blockOverrides
import net.minecraft.block.AbstractBlock
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

/**
 * This object controls the creation of instances for Blocks, Items, BlockEntities, etc.
 * If the provided API is not enough for you needs you can bypass the API using this object.
 *
 * You can make subclasses of the KDL classes and register a constructor function.
 * For example, to use a custom block class you need:
 * - Create a Class that extends [KDLBlock]
 * - Add a constructor function to [blockOverrides] like this:
 * ```kotlin
 * InstanceManager.blockOverrides[id("my_mod", "block_name")] = { builder, settings -> MyCustomBlock(builder, settings) }
 * ```
 */
object InstanceManager {

    val blockOverrides = mutableMapOf<Identifier, (BlockBuilder, AbstractBlock.Settings) -> KDLBlock>()
    val blockEntityOverrides = mutableMapOf<Identifier, (BlockEntityBuilder) -> KDLBlockEntity>()
    val itemOverrides = mutableMapOf<Identifier, (ItemBuilder, Item.Settings) -> KDLItem>()
    val blockItemOverrides = mutableMapOf<Identifier, (KDLBlock, ItemBuilder, Item.Settings) -> KDLBlockItem>()

    fun newKDLBlock(
        id: Identifier,
        builder: BlockBuilder,
        settings: AbstractBlock.Settings,
        withBlockEntity: Boolean
    ): KDLBlock {
        blockOverrides[id]?.let { func ->
            return func(builder, settings)
        }

        KDLBlock._constructor_config_ = builder
        return if (withBlockEntity)
            KDLBlockWithEntity(settings)
        else
            KDLBlock(settings)
    }

    fun newKDLBlockEntity(
        id: Identifier,
        config: BlockEntityBuilder,
        tickable: Boolean
    ): KDLBlockEntity {
        blockEntityOverrides[id]?.let { func ->
            return func(config)
        }

        return if (tickable)
            KDLTickableBlockEntity(config)
        else
            KDLBlockEntity(config)
    }

    fun newKDLItem(
        id: Identifier,
        builder: ItemBuilder,
        settings: Item.Settings
    ): KDLItem {
        itemOverrides[id]?.let { func ->
            return func(builder, settings)
        }

        return KDLItem(settings)
    }

    fun newKDLBlockItem(
        id: Identifier,
        block: KDLBlock,
        builder: ItemBuilder,
        settings: Item.Settings
    ): KDLBlockItem {
        blockItemOverrides[id]?.let { func ->
            return func(block, builder, settings)
        }

        return KDLBlockItem(block, settings)
    }

    fun newKDLScreenHandler(config: GuiBuilder, syncId: Int, playerInv: PlayerInventory, pos: BlockPos?): KDLScreenHandler {
        return KDLScreenHandler(config, syncId, playerInv, pos)
    }

    fun newKDLScreen(config: GuiBuilder, handler: KDLScreenHandler, playerInv: PlayerInventory, title: Text): KDLScreen {
        return KDLScreen(config.screenConfig, handler, playerInv, title)
    }
}