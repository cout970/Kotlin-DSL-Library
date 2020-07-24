package kdl.api.module

import kdl.KDL_ID
import kdl.api.block.BlockEntityBuilder
import kdl.api.block.blockentity.PersistentState
import kdl.api.util.NBTSerialization
import kdl.api.util.dropItem
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier

class InventoryModuleDSL {
    var slots: Int = 1
}

fun BlockEntityBuilder.inventory(dsl: InventoryModuleDSL.() -> Unit) {
    val config = InventoryModuleDSL().apply(dsl)

    module<InventoryState> {
        moduleType = Identifier(KDL_ID, "inventory")
        onCreate = { InventoryState(config.slots) }
        onBreak = { state ->
            state.clearToList().forEach { world.dropItem(it, pos) }
        }
    }
}

class InventoryState(size: Int) : SimpleInventory(size), PersistentState<CompoundTag> {

    override fun store(): CompoundTag {
        val tag = CompoundTag()

        for (i in 0 until size()) {
            tag.put(i.toString(), NBTSerialization.serialize(getStack(i)))
        }

        return tag
    }

    override fun restore(value: CompoundTag) {
        for (i in 0 until size()) {
            val item = NBTSerialization.deserialize(value.getCompound(i.toString()))
            setStack(i, item as ItemStack)
        }
    }
}