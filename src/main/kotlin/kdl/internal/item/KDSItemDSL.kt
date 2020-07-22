package kdl.internal.item

import kdl.api.item.ItemBuilder
import kdl.api.item.ItemDSL
import kdl.internal.ModReference
import kdl.internal.client.ModelManager
import kdl.internal.client.TranslationManager
import kdl.internal.registries.InstanceManager
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class KDLItemDSL(private val ref: ModReference) : ItemDSL {

    override fun item(definition: ItemBuilder.() -> Unit): Identifier {
        val builder = ItemBuilder()
        builder.apply(definition)

        if (builder.name == null) {
            error("Item definition incomplete: name and material are required")
        }

        val id = Identifier(ref.modid, builder.name)

        if (!Registry.ITEM.ids.contains(id)) {
            val settings = Item.Settings().group(ItemGroup.MISC)
            val item = InstanceManager.newKDLItem(id, builder, settings)

            Registry.register(Registry.ITEM, id, item)
            ref.logger().info("Registering item ${builder.name}")
        }

        val item = Registry.ITEM[id] as KDLItem
        item.config = builder

        ModelManager.registerDisplay(id, builder.display, true)

        builder.defaultLocalizedName?.let {
            TranslationManager.registerDefaultTranslation(item.translationKey, it)
        }

        return id
    }
}