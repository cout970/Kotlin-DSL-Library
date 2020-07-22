package kdl.internal.gui

import kdl.api.gui.GuiBuilder
import kdl.internal.registries.Registries
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

class KDLScreenHandler(val config: GuiBuilder, syncId: Int, inventory: PlayerInventory) : ScreenHandler(typeOf(config), syncId) {

    init {
        config.screenHandlerConfig.onInit?.invoke(this, inventory)
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        config.screenHandlerConfig.onClose?.invoke(this)
    }

    override fun addSlot(slot: Slot?): Slot {
        return super.addSlot(slot)
    }

    override fun canUse(player: PlayerEntity?): Boolean = true
}

private fun typeOf(config: GuiBuilder): ScreenHandlerType<KDLScreenHandler> {
    @Suppress("UNCHECKED_CAST")
    return Registries.screenHandler[config.id!!] as ScreenHandlerType<KDLScreenHandler>
}