package kdl.api.block

import kdl.api.gui.GuiManager
import kdl.api.util.isServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier


object OnUse {

    fun openGui(guiId: Identifier): BlockOnUse.() -> Unit = {
        if (world.isServer) {
            GuiManager.openBlockScreen(guiId, player as ServerPlayerEntity, pos)
            result = ActionResult.CONSUME
        }
        result = ActionResult.SUCCESS
    }
}