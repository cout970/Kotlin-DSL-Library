package kdl.internal.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class KDLSlot(
    inventory: Inventory, index: Int, x: Int, y: Int,
    var canTake: Boolean = true,
    var canPlace: Boolean = true,
    var filter: (ItemStack) -> Boolean = { true }
) : Slot(inventory, index, x, y) {

    override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {
        return canTake
    }

    override fun canInsert(stack: ItemStack): Boolean {
        return canPlace && filter(stack)
    }
}