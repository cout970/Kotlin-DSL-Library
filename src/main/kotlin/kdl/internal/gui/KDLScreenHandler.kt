package kdl.internal.gui

import kdl.api.gui.GuiBuilder
import kdl.api.gui.RegionBuilder
import kdl.api.gui.ScreenHandlerCtx
import kdl.api.gui.SlotBuilder
import kdl.internal.registries.Registries
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos

class KDLScreenHandler(
    config: GuiBuilder,
    syncId: Int,
    val playerInventory: PlayerInventory,
    var blockPos: BlockPos? = null
) : ScreenHandler(typeOf(config), syncId), SlotBuilder, RegionBuilder {

    val ctx: ScreenHandlerCtx
        get() = ScreenHandlerCtx(this, playerInventory, playerInventory.player.world, blockPos)

    val config = config.screenHandlerConfig
    val regions = mutableListOf<Region>()

    init {
        this.config.onInit?.invoke(ctx)
        this.config.slots.execute { it(ctx) }
        this.config.regions.execute { it(ctx) }
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        config.onClose?.invoke(ctx)
    }

    public override fun addSlot(slot: Slot?): Slot {
        return super.addSlot(slot)
    }

    override fun canUse(player: PlayerEntity?): Boolean = true

    override fun slot(inv: Inventory, index: Int, posX: Int, posY: Int) {
        addSlot(KDLSlot(inv, index, posX, posY))
    }

    override fun slotArea(inv: Inventory, startIndex: Int, cols: Int, rows: Int, posX: Int, posY: Int) {
        repeat(3) { i ->
            repeat(9) { j ->
                addSlot(KDLSlot(inv, j + i * 9 + startIndex, posX + j * 18, posY + i * 18))
            }
        }
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
    return Registries.screenHandler[config.id!!] as ScreenHandlerType<KDLScreenHandler>
}

