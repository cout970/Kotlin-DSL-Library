package example_mod

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

object Storage {

    val MAGIC_DIRT = block("magic_dirt")
    val MAGIC_SAND = block("magic_sand")
    val MAGIC_SAND2 = block("magic_sand2")
    val FURNACE = block("furnace")
    val STICK = item("stick")
    val STICK2 = item("stick2")

    fun block(name: String): Block = Registry.BLOCK.getOrEmpty(ExampleModRef.id(name)).get()
    fun item(name: String): Item = Registry.ITEM.getOrEmpty(ExampleModRef.id(name)).get()
}