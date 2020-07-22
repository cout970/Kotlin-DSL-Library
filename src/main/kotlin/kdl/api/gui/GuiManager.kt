package kdl.api.gui

import kdl.internal.gui.KDLScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

object GuiManager {
    private val logger = LogManager.getLogger()
    internal val guiConfigDSLs = mutableMapOf<Identifier, GuiBuilder.() -> Unit>()
    internal val guiConfigs = mutableMapOf<Identifier, GuiBuilder>()

    /**
     * Re-evaluate the gui{} builder blocks form all blocks
     */
    fun reloadGuiConfigs() {
        guiConfigDSLs.forEach { (id, func) ->
            val config = GuiBuilder().apply(func)
            val guiId = config.id ?: id
            config.id = guiId

            guiConfigs[guiId] = config
        }
    }

    fun openScreen(guiId: Identifier, player: ServerPlayerEntity) {
        val config = guiConfigs[guiId]

        if (config == null) {
            logger.warn("Attempt to open unregistered gui with id: $guiId")
            return
        }

        player.openHandledScreen(object : ExtendedScreenHandlerFactory {
            var instance: KDLScreenHandler? = null

            override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
                return KDLScreenHandler(config, syncId, inv).also { instance = it }
            }

            override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
                config.screenHandlerConfig.serverWritePacket?.invoke(instance!!, player, buf)
            }

            override fun getDisplayName(): Text = TranslatableText("gui.${guiId.namespace}.${guiId.path}")
        })
    }
}