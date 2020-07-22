package kdl.api.gui

import kdl.api.KDL
import kdl.api.gui.widgets.WidgetBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

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

@KDL
class ScreenHandlerBuilder {
    val slots = mutableMapOf<Identifier, Unit>()
    var onInit: ((ScreenHandler, PlayerInventory) -> Unit)? = null
    var onClose: ((ScreenHandler) -> Unit)? = null
    var serverWritePacket: ((ScreenHandler, ServerPlayerEntity, PacketByteBuf) -> Unit)? = null
    var clientReadPacket: ((ScreenHandler, PlayerInventory, PacketByteBuf) -> Unit)? = null

    fun slots() {

    }
}

@KDL
class ScreenBuilder {
    var onInit: ((Screen, PlayerInventory, Text) -> Unit)? = null
    var onClose: ((Screen) -> Unit)? = null
    var widgets: (WidgetBuilder<Unit>.(Screen) -> Unit)? = null
}
