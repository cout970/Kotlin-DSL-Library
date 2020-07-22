package example_mod

import kdl.api.registries.toBlock
import kdl.api.registries.toItem
import net.minecraft.block.Block
import net.minecraft.item.Item

object Storage {

    val MAGIC_DIRT = block("magic_dirt")
    val MAGIC_SAND = block("magic_sand")
    val MAGIC_SAND2 = block("magic_sand2")
    val FURNACE = block("furnace")
    val STICK = item("stick")
    val STICK2 = item("stick2")

    fun block(name: String): Block = ExampleModRef.id(name).toBlock()!!
    fun item(name: String): Item = ExampleModRef.id(name).toItem()!!
}