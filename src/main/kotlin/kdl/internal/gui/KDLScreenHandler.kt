package kdl.internal.gui

import kdl.KDL_ID
import kdl.api.gui.GuiBuilder
import kdl.api.gui.RegionBuilder
import kdl.api.gui.ScreenHandlerCtx
import kdl.api.gui.SlotBuilder
import kdl.api.module.InventoryState
import kdl.api.util.getModule
import kdl.api.util.id
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

class KDLScreenHandler(
    config: GuiBuilder,
    syncId: Int,
    val playerInventory: PlayerInventory,
    var blockPos: BlockPos? = null
) : ScreenHandler(typeOf(config), syncId), SlotBuilder, RegionBuilder {

    val blockEntity: BlockEntity? = blockPos?.let {
        playerInventory.player.world
            .getBlockEntity(blockPos)
    }

    val blockInventory: Inventory? = blockPos?.let {
        playerInventory.player.world
            .getBlockEntity(blockPos)
            ?.getModule<InventoryState>(id(KDL_ID, "inventory"))
            ?.state
    }

    val ctx: ScreenHandlerCtx
        get() = ScreenHandlerCtx(
            this,
            playerInventory,
            playerInventory.player.world,
            blockPos,
            blockInventory,
            blockEntity
        )

    val config = config.screenHandlerConfig
    val regions = mutableListOf<Region>()

    init {
        this.config.onInit?.invoke(ctx)
        this.config.slots.execute { it(ctx) }
        this.config.regions.execute { it(ctx) }
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        config.onClose?.invoke(ctx)
    }

    public override fun addSlot(slot: Slot?): Slot {
        return super.addSlot(slot)
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        config.onTick?.invoke(ctx)
        return true
    }

    override fun slot(
        inv: Inventory,
        index: Int,
        posX: Int,
        posY: Int,
        canTake: Boolean,
        canPlace: Boolean,
        filter: (ItemStack) -> Boolean
    ) {
        addSlot(KDLSlot(inv, index, posX, posY, canTake, canPlace, filter))
    }

    override fun playerInventory(posX: Int, posY: Int, main: Boolean, hotbar: Boolean) {
        if (main) {
            repeat(3) { i ->
                repeat(9) { j ->
                    addSlot(KDLSlot(playerInventory, j + i * 9 + 9, posX + 8 + j * 18, posY + 84 + i * 18))
                }
            }
        }
        if (hotbar) {
            repeat(9) { i ->
                addSlot(KDLSlot(playerInventory, i, posX + 8 + i * 18, posY + 142))
            }
        }
    }

    override fun region(start: Int, size: Int, reverse: Boolean, name: String, filter: (Int, ItemStack) -> Boolean) {
        regions += Region(start, size, reverse, name, filter)
    }

    override fun transferSlot(player: PlayerEntity?, index: Int): ItemStack? {
        val slot = slots[index]
        if (slot == null || !slot.hasStack()) return ItemStack.EMPTY

        val slotStack: ItemStack = slot.stack
        val copy = slotStack.copy()
        var inserted = false

        for (reg in regions) {
            val start = reg.start
            val end = reg.start + reg.size

            if (index !in start until end && reg.filter(index, slotStack)) {
                if (insertItem(slotStack, start, end, reg.reverse)) {
                    inserted = true
                    slot.onStackChanged(slotStack, copy)
                    break
                }
            }
        }

        if (!inserted) return ItemStack.EMPTY

        if (slotStack.isEmpty) {
            slot.stack = ItemStack.EMPTY
        } else {
            slot.markDirty()
        }
        if (slotStack.count == copy.count) {
            return ItemStack.EMPTY
        }
        slot.onTakeItem(player, slotStack)
        return copy
    }

    data class Region(
        val start: Int,
        val size: Int,
        val reverse: Boolean,
        val name: String,
        val filter: (Int, ItemStack) -> Boolean
    )
}

private fun typeOf(config: GuiBuilder): ScreenHandlerType<KDLScreenHandler> {
    @Suppress("UNCHECKED_CAST")
    return Registry.SCREEN_HANDLER[config.id!!] as ScreenHandlerType<KDLScreenHandler>
}

