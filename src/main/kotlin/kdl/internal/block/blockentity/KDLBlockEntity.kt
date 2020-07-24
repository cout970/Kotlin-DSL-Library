package kdl.internal.block.blockentity

import kdl.api.block.BlockEntityBuilder
import kdl.api.block.blockentity.*
import kdl.api.util.NBTSerialization
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

open class KDLBlockEntity(val config: BlockEntityBuilder) : BlockEntity(type(config)), ModuleManager {

    override val modules = mutableMapOf<Identifier, KDLModule>()

    override val blockstate: BlockState get() = cachedState

    override var removed: Boolean
        get() = isRemoved
        set(value) {
            if (value) markRemoved() else cancelRemoval()
        }

    val ctx: ModuleCtx
        get() = ModuleCtx(
            world = world ?: error("BlockEntity world is not set"),
            pos = pos,
            moduleManager = this
        )

    init {
        config.modules.forEach { (id, dsl) ->
            val state = dsl.onCreate?.invoke() ?: Unit
            @Suppress("UNCHECKED_CAST")
            modules[id] = KDLModule(state, dsl as ModuleDefinition<Any>)
        }
    }

    override fun setLocation(world: World?, pos: BlockPos?) {
        super.setLocation(world, pos)
        init()
    }

    fun init() {
        if (!hasWorld()) return
        modules.forEach { (_, mod) ->
            mod.def.onInit?.invoke(ctx, mod.state)
        }
    }

    fun onBreak() {
        modules.forEach { (_, mod) ->
            mod.def.onBreak?.invoke(ctx, mod.state)
        }
    }

    override fun getSquaredRenderDistance(): Double {
        return config.renderDistance * config.renderDistance
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        modules.forEach { (id, mod) ->
            var state: Any = mod.state

            if (state is PersistentState<*>) {
                state = state.store()
            }

            val value = NBTSerialization.serialize(state)
            tag.put(id.toString(), value)
        }
        return super.toTag(tag)
    }

    override fun fromTag(blockstate: BlockState, tag: CompoundTag) {
        super.fromTag(blockstate, tag)
        modules.forEach { (id, mod) ->
            if (!tag.contains(id.toString())) return@forEach

            val nbtValue = tag.getCompound(id.toString())
            val value = NBTSerialization.deserialize(nbtValue)

            if (mod.state is PersistentState<*>) {
                @Suppress("UNCHECKED_CAST")
                (mod.state as PersistentState<Any>).restore(value)
            } else {
                mod.state = value
            }
        }
    }

    override fun sendUpdateToNearPlayers() {
        val world = world ?: return
        val pos = pos ?: return
        if (world.isClient) return

        toUpdatePacket()?.let { packet ->
            world.players
                .map { it as ServerPlayerEntity }
                .filter { it.squaredDistanceTo(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) < (64 * 64) }
                .forEach { it.networkHandler.sendPacket(packet) }
        }
    }

    override fun toUpdatePacket(): BlockEntityUpdateS2CPacket? {
        val tag = toInitialChunkDataTag()

        modules.forEach { (id, mod) ->
            if (mod.state is SyncState<*>) {
                val value = (mod.state as SyncState<Any>).toSend()

                if (value != Unit) {
                    tag.put(id.toString(), NBTSerialization.serialize(value))
                }
            }
        }
        return BlockEntityUpdateS2CPacket(pos, 0, tag)
    }

    fun receiveUpdatePacket(packet: BlockEntityUpdateS2CPacket) {
        val tag = packet.compoundTag ?: return

        modules.forEach { (id, mod) ->
            if (mod.state is SyncState<*>) {
                val nbtValue = tag.getCompound(id.toString())
                val value = NBTSerialization.deserialize(nbtValue)
                (mod.state as SyncState<Any>).onReceive(value)
            }
        }
    }

    override fun markDirty() {
        super.markDirty()
    }
}

data class KDLModule(
    override var state: Any,
    override val def: ModuleDefinition<Any>
) : Module<Any>

open class KDLTickableBlockEntity(config: BlockEntityBuilder) : KDLBlockEntity(config), Tickable {

    override fun tick() {
        modules.forEach { (_, mod) ->
            mod.def.onTick?.invoke(ctx, mod.state)
        }
    }
}

private fun type(config: BlockEntityBuilder): BlockEntityType<*> {
    val id = config.type!!
    return Registry.BLOCK_ENTITY_TYPE[id] ?: error("Block entity ${config.type} is not registered")
}