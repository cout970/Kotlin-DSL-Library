package kdl.api.util

import kdl.api.block.blockentity.ModuleState
import kdl.internal.block.blockentity.KDLBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldView

fun <T : Comparable<T>> Block.findProperty(name: String): Property<T>? {
    return defaultState?.properties?.find { it.getName() == name } as? Property<T>?
}

fun <T : Comparable<T>> BlockState.findProperty(name: String): Property<T>? {
    return properties.find { it.getName() == name } as? Property<T>?
}


fun <T : Comparable<T>> BlockState.withProperty(name: String, value: T): BlockState {
    return this.with(findProperty(name), value)
}

val WorldView.isServer: Boolean get() = !isClient

fun World.dropItem(item: ItemStack, pos: BlockPos): ItemEntity {
    return ItemEntity(this, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, item).also { spawnEntity(it) }
}

fun World.dropItem(item: ItemStack, pos: Vec3d): ItemEntity {
    return ItemEntity(this, pos.x, pos.y, pos.z, item).also { spawnEntity(it) }
}

fun <T : ModuleState> BlockEntity.getModule(id: Identifier): T? {
    val tile = this as? KDLBlockEntity ?: return null
    return tile.moduleManager.modules[id] as T
}