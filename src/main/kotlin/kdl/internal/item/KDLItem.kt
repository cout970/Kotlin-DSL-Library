package kdl.internal.item

import kdl.api.item.ItemBuilder
import kdl.internal.block.KDLBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.Item

open class KDLItem(settings: Settings) : Item(settings) {
    lateinit var config: ItemBuilder
}

open class KDLBlockItem(val block: KDLBlock, settings: Settings) : BlockItem(block, settings) {
    lateinit var config: ItemBuilder
}