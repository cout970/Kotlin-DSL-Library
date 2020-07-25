package kdl.internal.block

import kdl.api.block.BlockBuilder
import kdl.api.block.BlockDSL
import kdl.api.block.BlockEntityBuilder
import kdl.api.gui.GuiBuilder
import kdl.api.gui.GuiManager
import kdl.api.item.ItemBuilder
import kdl.api.model.BlockstateVariantBuilder
import kdl.internal.ModReference
import kdl.internal.client.ModelManager
import kdl.internal.client.TranslationManager
import kdl.internal.gui.KDLScreen
import kdl.internal.gui.KDLScreenHandler
import kdl.internal.item.KDLBlockItem
import kdl.internal.registries.InstanceManager
import kdl.internal.registries.KDLRegistries
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.model.ModelRotation
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.ModelVariant
import net.minecraft.client.render.model.json.WeightedUnbakedModel
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

class KDLBlockDSL(private val ref: ModReference) : BlockDSL {

    override fun block(definition: BlockBuilder.() -> Unit): Identifier {
        val builder = BlockBuilder()
        builder.apply(definition)

        if (builder.name == null || builder.material == null) {
            error("Block definition incomplete: name and material are required")
        }

        val id = Identifier(ref.modid, builder.name)

        if (!Registry.BLOCK.ids.contains(id)) {
            val material = KDLRegistries.material[builder.material!!] ?: Material.STONE
            val settings = AbstractBlock.Settings.of(material)

            val block = InstanceManager.newKDLBlock(id, builder, settings, builder.blockEntityConfig != null)

            Registry.register(Registry.BLOCK, id, block)
            ref.logger().info("Registering block ${builder.name}")
        }

        val block = Registry.BLOCK[id] as KDLBlock
        block.config = builder

        builder.blockStateConfig?.let { config ->

            if (config.customModel != null) {
                ModelManager.registerBlockstate(id, config.customModel!!)

            } else if (config.modelVariants != null) {

                ModelManager.registerBlockstate(id, getterFromVariants(id, config.modelVariants!!))
            }
        }

        builder.blockEntityConfig?.let { config ->
            registerBlockEntity(block, id, config)
        }


        builder.guiConfig?.let { config ->
            GuiManager.guiConfigDSLs[id] = config
            registerGui(id, GuiBuilder().apply(config))
        }

        val itemBlock = builder.blockItem ?: return id
        registerBlockItem(block, itemBlock)
        ref.logger().info("Registering blockitem ${builder.name}")

        return id
    }

    private fun registerBlockEntity(block: Block, id: Identifier, config: BlockEntityBuilder) {
        val typeId = config.type ?: id
        config.type = typeId
        if (Registry.BLOCK_ENTITY_TYPE[typeId] != null) return

        val tickable = config.modules.any { it.value.onTick != null }

        val type = BlockEntityType.Builder.create(
            Supplier { InstanceManager.newKDLBlockEntity(typeId, config, tickable) }, block
        ).build(null)

        Registry.register(Registry.BLOCK_ENTITY_TYPE, typeId, type)
    }

    private fun registerGui(id: Identifier, config: GuiBuilder) {
        val guiId = config.id ?: id
        config.id = guiId

        val clientFactory =
            ScreenHandlerRegistry.ExtendedClientHandlerFactory<KDLScreenHandler> { syncId, playerInv, buf ->
                val latestConfig = GuiManager.guiConfigs[guiId]!!
                val pos = if (buf.readByte() != 0.toByte()) buf.readBlockPos() else null

                InstanceManager.newKDLScreenHandler(latestConfig, syncId, playerInv, pos).also {

                    latestConfig.screenHandlerConfig.clientReadPacket?.invoke(it.ctx, buf)
                }
            }

        val type: ScreenHandlerType<KDLScreenHandler> = ScreenHandlerRegistry.registerExtended(guiId, clientFactory)

        val screenFactory = ScreenRegistry.Factory<KDLScreenHandler, KDLScreen> { handler, playerInv, title ->
            val latestConfig = GuiManager.guiConfigs[guiId]!!

            InstanceManager.newKDLScreen(latestConfig, handler, playerInv, title)
        }

        ScreenRegistry.register(type, screenFactory)
        GuiManager.guiConfigs[guiId] = config
    }

    private fun getterFromVariants(
        id: Identifier,
        variants: Map<String, MutableList<BlockstateVariantBuilder>>
    ): (ModelIdentifier, BlockState) -> UnbakedModel? {
        val map = mutableMapOf<String, UnbakedModel>()
        var index = 0

        variants.forEach { (name, models) ->
            val modelVariants = models.mapNotNull { variant ->

                ModelManager.registerDisplay(id, variant.display, false, index.toString())?.let { modelId ->
                    val rot = variant.rotation?.toAffineTransformation() ?: ModelRotation.X0_Y0.rotation

                    ModelVariant(modelId, rot, variant.uvLock, 1)
                }
            }

            map[name] = WeightedUnbakedModel(modelVariants)
            index++
        }

        return { modelId, _ -> map[modelId.variant] }
    }

    private fun registerBlockItem(block: KDLBlock, builder: ItemBuilder) {
        val id = Identifier(ref.modid, builder.name ?: block.config.name)

        if (!Registry.ITEM.ids.contains(id)) {
            val settings = Item.Settings().group(ItemGroup.MISC)

            val item = InstanceManager.newKDLBlockItem(id, block, builder, settings)
            Registry.register(Registry.ITEM, id, item)
        }

        val item = Registry.ITEM[id] as KDLBlockItem
        item.config = builder

        ModelManager.registerDisplay(id, builder.display, true)

        builder.defaultLocalizedName?.let {
            TranslationManager.registerDefaultTranslation(item.translationKey, it)
        }
    }
}