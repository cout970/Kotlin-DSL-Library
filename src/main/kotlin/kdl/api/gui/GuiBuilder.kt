package kdl.api.gui

import kdl.api.KDL
import kdl.api.gui.widgets.WidgetBuilder
import kdl.api.util.Deferred
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.gui.screen.ingame.HandledScreen
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

    /**
     * The screen handler is a component that handles sync between client an server when a GUI is open
     *
     * There are instances both in the server and the client
     */
    fun screenHandler(func: ScreenHandlerBuilder.() -> Unit) {
        screenHandlerConfig.apply(func)
    }

    /**
     * The screen handler rendering and user input on the client side of the GUI
     *
     * There is only an instance on client
     */
    fun screen(func: ScreenBuilder.() -> Unit) {
        screenConfig.apply(func)
    }
}

/**
 * Context of a ScreenHandler
 *
 * The fields blockPos, blockInventory, blockEntity are only populated if the GUI is
 * open with [GuiManager.openBlockScreen]
 */
data class ScreenHandlerCtx(
    val screenHandler: ScreenHandler,
    val playerInventory: PlayerInventory,
    val world: World,
    val blockPos: BlockPos?,
    val blockInventory: Inventory?,
    val blockEntity: BlockEntity?
)

@KDL
class ScreenHandlerBuilder {
    // Called at creation time
    var onInit: (ScreenHandlerCtx.() -> Unit)? = null

    // Called every tick on the server
    var onTick: (ScreenHandlerCtx.() -> Unit)? = null

    // Called when the GUi is about to be closed
    var onClose: (ScreenHandlerCtx.() -> Unit)? = null

    // Called on the server before the GUI is opened
    var serverWritePacket: (ScreenHandlerCtx.(ServerPlayerEntity, Array<out Any?>, PacketByteBuf) -> Unit)? = null

    // Called on the client before the GUI is opened
    var clientReadPacket: (ScreenHandlerCtx.(PacketByteBuf) -> Unit)? = null

    // The slot configuration is executed when the GUI opens
    val slots = Deferred<SlotBuilder.(ScreenHandlerCtx) -> Unit>()

    // The region configuration is executed when the GUI opens
    val regions = Deferred<RegionBuilder.(ScreenHandlerCtx) -> Unit>()

    /**
     * The slots that hold items in the gui
     */
    fun slots(config: SlotBuilder.(ctx: ScreenHandlerCtx) -> Unit) {
        slots.onExecution(config)
    }

    /**
     * Slots regions, when the player shift clicks a slot, the items will be moved to the next region
     */
    fun regions(config: RegionBuilder.(ctx: ScreenHandlerCtx) -> Unit) {
        regions.onExecution(config)
    }
}

/**
 * Context of a Screen
 */
data class ScreenCtx(
    val screen: HandledScreen<*>,
    val handlerCtx: ScreenHandlerCtx
)

@KDL
class ScreenBuilder {
    // Title of the screen, if not defined a translation of the key "gui.<modId>.<guiId>" is used
    var title: Text? = null

    // Called when the GUI is about to be opened
    var onInit: (ScreenCtx.(PlayerInventory, Text) -> Unit)? = null

    // Called before the GUI closes
    var onClose: (ScreenCtx.() -> Unit)? = null

    // The widget configuration is executed when the GUI opens
    val widgets = Deferred<WidgetBuilder<Unit>.(ScreenCtx) -> Unit>()

    fun widgets(config: WidgetBuilder<Unit>.(ctx: ScreenCtx) -> Unit) {
        widgets.onExecution(config)
    }
}

@KDL
interface SlotBuilder {
    /**
     * Creates a slot both in the client and the server
     *
     * The position is relative to the screen background start position
     */
    fun slot(
        inv: Inventory,
        index: Int,
        posX: Int = 0,
        posY: Int = 0,
        canTake: Boolean = true,
        canPlace: Boolean = true,
        filter: (stack: ItemStack) -> Boolean = { true }
    )

    /**
     * Helper function to add slots from the player
     */
    fun playerInventory(posX: Int = 0, posY: Int = 0, main: Boolean = true, hotbar: Boolean = true)
}

@KDL
interface RegionBuilder {
    /**
     * Creates a region of slots
     *
     * The name is for debugging purposes
     */
    fun region(
        start: Int,
        size: Int,
        reverse: Boolean = false,
        name: String = "Region",
        filter: (index: Int, stack: ItemStack) -> Boolean = { _, _ -> true }
    )
}