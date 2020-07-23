package kdl.api.gui

import kdl.internal.gui.KDLScreenHandler
import kdl.internal.registries.InstanceManager
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
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

    fun openScreen(guiId: Identifier, player: ServerPlayerEntity, vararg params: Any?) {
        val config = guiConfigs[guiId]

        if (config == null) {
            logger.warn("Attempt to open unregistered gui with id: $guiId")
            return
        }

        player.openHandledScreen(object : ExtendedScreenHandlerFactory {
            var instance: KDLScreenHandler? = null

            override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
                return InstanceManager.newKDLScreenHandler(config, syncId, inv, null).also { instance = it }
            }

            override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
                buf.writeByte(0)
                config.screenHandlerConfig.serverWritePacket?.invoke(instance!!.ctx, player, params, buf)
            }

            override fun getDisplayName(): Text {
                return config.screenConfig.title ?: TranslatableText("gui.${guiId.namespace}.${guiId.path}")
            }
        })
    }

    fun openBlockScreen(guiId: Identifier, player: ServerPlayerEntity, pos: BlockPos, vararg params: Any?) {
        val config = guiConfigs[guiId]

        if (config == null) {
            logger.warn("Attempt to open unregistered gui with id: $guiId")
            return
        }

        player.openHandledScreen(object : ExtendedScreenHandlerFactory {
            var instance: KDLScreenHandler? = null

            override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
                return InstanceManager.newKDLScreenHandler(config, syncId, inv, pos).also { instance = it }
            }

            override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
                buf.writeByte(1)
                buf.writeBlockPos(pos)
                config.screenHandlerConfig.serverWritePacket?.invoke(instance!!.ctx, player, params, buf)
            }

            override fun getDisplayName(): Text {
                return config.screenConfig.title ?: TranslatableText("gui.${guiId.namespace}.${guiId.path}")
            }
        })
    }
}