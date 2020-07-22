package kdl.api.block.blockentity

import kdl.api.KDL
import kdl.api.util.NBTSerialization
import net.minecraft.block.BlockState
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class ModuleCtx(val world: World, val pos: BlockPos, val moduleManager: ModuleManager)

interface ModuleManager {
    val modules: Map<Identifier, ModuleState>
    val blockstate: BlockState
    var removed: Boolean

    fun sendUpdateToNearPlayers()
    fun markDirty()
}

interface ModuleState {
    /**
     * Value automatically serialized and deserialized on save/load
     *
     * You can write a custom serializer [NBTSerialization]
     */
    var persistentState: Any?
        get() = null
        set(_) {}

    /**
     * Value automatically serialized and deserialized to be send from server to client
     *
     * You must call ModuleManager.sendUpdateToNearPlayers()
     */
    var syncState: Any?
        get() = null
        set(_) {}
}

object DefaultModuleState : ModuleState

@KDL
open class ModuleBuilder<State : ModuleState> {
    var id: Identifier? = null
    var onCreate: (() -> State)? = null
    var onInit: (ModuleCtx.(State) -> Unit)? = null
    var onTick: (ModuleCtx.(State) -> Unit)? = null
    var onBreak: (ModuleCtx.(State) -> Unit)? = null
}