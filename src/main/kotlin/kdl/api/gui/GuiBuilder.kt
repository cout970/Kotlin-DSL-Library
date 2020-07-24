package kdl.api.gui

import kdl.api.KDL
import kdl.api.gui.widgets.WidgetBuilder
import kdl.api.util.Deferred
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@KDL
class GuiBuilder {
    var id: Identifier? = null
    var screenHandlerConfig: ScreenHandlerBuilder = ScreenHandlerBuilder()
    var screenConfig: ScreenBuilder = ScreenBuilder()

    fun screenHandler(func: ScreenHandlerBuilder.() -> Unit) {
        screenHandlerConfig.apply(func)
    }

    fun screen(func: ScreenBuilder.() -> Unit) {
        screenConfig.apply(func)
    }
}

data class ScreenHandlerCtx(
    val screenHandler: ScreenHandler,
    val playerInventory: PlayerInventory,
    val world: World,
    val blockPos: BlockPos?,
    val blockInventory: Inventory?
)

@KDL
class ScreenHandlerBuilder {
    var onInit: ((ScreenHandlerCtx) -> Unit)? = null
    var onClose: ((ScreenHandlerCtx) -> Unit)? = null
    var serverWritePacket: ((ScreenHandlerCtx, ServerPlayerEntity, params: Array<out Any?>, PacketByteBuf) -> Unit)? =
        null
    var clientReadPacket: ((ScreenHandlerCtx, PacketByteBuf) -> Unit)? = null
    val slots = Deferred<SlotBuilder.(ScreenHandlerCtx) -> Unit>()
    val regions = Deferred<RegionBuilder.(ScreenHandlerCtx) -> Unit>()

    /**
     * The slots that hold items in the gui
     */
    fun slots(config: SlotBuilder.(ScreenHandlerCtx) -> Unit) {
        slots.onExecution(config)
    }

    /**
     * Slots regions, when the player shift clicks a slot, the items will be moved to the next region
     */
    fun regions(config: RegionBuilder.(ScreenHandlerCtx) -> Unit) {
        regions.onExecution(config)
    }
}

@KDL
class ScreenBuilder {
    var title: Text? = null
    var onInit: ((Screen, PlayerInventory, Text) -> Unit)? = null
    var onClose: ((Screen) -> Unit)? = null
    val widgets = Deferred<WidgetBuilder<Unit>.(Screen) -> Unit>()

    fun widgets(config: WidgetBuilder<Unit>.(Screen) -> Unit) {
        widgets.onExecution(config)
    }
}

@KDL
interface SlotBuilder {
    fun slot(
        inv: Inventory,
        index: Int,
        posX: Int = 0,
        posY: Int = 0,
        canTake: Boolean = true,
        canPlace: Boolean = true,
        filter: (ItemStack) -> Boolean = { true }
    )

    fun slotArea(inv: Inventory, startIndex: Int, cols: Int = 1, rows: Int = 1, posX: Int = 0, posY: Int = 0)

    fun playerInventory(posX: Int = 0, posY: Int = 0, main: Boolean = true, hotbar: Boolean = true)
}

@KDL
interface RegionBuilder {
    fun region(
        start: Int,
        size: Int,
        reverse: Boolean = false,
        name: String = "Region",
        filter: (Int, ItemStack) -> Boolean = { _, _ -> true }
    )
}