package kdl.api.block.blockentity

import kdl.api.KDL
import kdl.api.util.NBTSerialization
import net.minecraft.block.BlockState
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class ModuleCtx(val world: World, val pos: BlockPos, val moduleManager: ModuleManager)

interface ModuleManager {
    val modules: Map<Identifier, Module<*>>
    val blockstate: BlockState
    var removed: Boolean

    fun sendUpdateToNearPlayers()
    fun markDirty()
}

interface Module<State : Any> {
    // The internal state of the module, if the module has no state, Unit will be used instead
    val state: State

    // Readonly information about the module
    val def: ModuleDefinition<State>
}

interface ModuleDefinition<State> {
    val type: Identifier
    val onCreate: (() -> State)?
    val onInit: (ModuleCtx.(State) -> Unit)?
    val onTick: (ModuleCtx.(State) -> Unit)?
    val onBreak: (ModuleCtx.(State) -> Unit)?
}

/**
 * Allows a module to customize which part of the state to be saved,
 * if the state doesn't implement this interface, all the state will be selected
 *
 * Once a part of the state is selected to be saved a check is made for the following interfaces:
 * - [java.io.Serializable]
 * - [kdl.api.util.NBTSerializable]
 * - [kdl.api.util.GsonSerializable]
 * - [net.minecraft.nbt.Tag]
 *
 * If none is found an extra check is made for custom serializes in NBTSerialization
 *
 * If all checks fail, the state is not saved
 *
 * See also [NBTSerialization]
 */
interface PersistentState<T: Any> {

    // Returns a serializable object or NBT to save
    fun store(): T

    // Receives a deserialized object or NBT to load, should be the same value returned from store()
    fun restore(value: T)
}

/**
 * Allows a module select a part of the state to be send to the client when sendUpdateToNearPlayers() is called
 *
 * See also [NBTSerialization]
 */
interface SyncState<T: Any> {

    // Returns a serializable object or NBT to send to client
    fun toSend(): T

    // Receives a deserialized object or NBT from the server, should be the same value returned from toSend()
    fun onReceive(value: T)
}

@KDL
open class ModuleBuilder<State> : ModuleDefinition<State> {
    override val type: Identifier get() = moduleType!!
    var moduleType: Identifier? = null

    override var onCreate: (() -> State)? = null
    override var onInit: (ModuleCtx.(State) -> Unit)? = null
    override var onTick: (ModuleCtx.(State) -> Unit)? = null
    override var onBreak: (ModuleCtx.(State) -> Unit)? = null
}