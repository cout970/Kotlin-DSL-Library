package kdl.api.module

import kdl.api.block.BlockEntityBuilder
import kdl.api.block.blockentity.ModuleState
import kdl.api.util.dropItem
import net.minecraft.inventory.SimpleInventory
import net.minecraft.util.Identifier

class InventoryModuleDSL {
    var slots: Int = 1
}

data class InventoryState(val inventory: SimpleInventory) : ModuleState

fun BlockEntityBuilder.inventory(dsl: InventoryModuleDSL.() -> Unit) {
    val config = InventoryModuleDSL().apply(dsl)

    module<InventoryState> {
        id = Identifier("kdl", "inventory")
        onCreate = { InventoryState(SimpleInventory(config.slots)) }
        onBreak = { state ->
            state.inventory.clearToList().forEach { world.dropItem(it, pos) }
        }
    }
}