package kdl.internal.block.blockentity

import kdl.api.block.BlockEntityBuilder
import kdl.api.block.blockentity.DefaultModuleState
import kdl.api.block.blockentity.ModuleBuilder
import kdl.api.block.blockentity.ModuleCtx
import kdl.api.block.blockentity.ModuleState
import kdl.api.util.NBTSerialization
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

open class KDLBlockEntity(val config: BlockEntityBuilder) : BlockEntity(type(config)) {
    /**
     * Be careful extending this class, moduleManager may not be fully configured in the subclass constructor
     */
    @Suppress("LeakingThis")
    val moduleManager = KDLModuleManager(this)

    val ctx: ModuleCtx
        get() = ModuleCtx(world!!, pos, moduleManager)

    init {
        config.modules.forEach { (id, dsl) ->
            val module = dsl.onCreate?.invoke() ?: DefaultModuleState
            moduleManager.modules[id] = module
        }
    }

    fun init() {
        if (!hasWorld()) return
        modules { _, builder, state ->
            builder.onInit?.let { func -> func(ctx, state) }
        }
    }

    override fun setLocation(world: World?, pos: BlockPos?) {
        super.setLocation(world, pos)
        init()
    }

    fun onBreak() {
        modules { _, builder, state ->
            builder.onBreak?.let { func -> func(ctx, state) }
        }
    }

    override fun getSquaredRenderDistance(): Double {
        return config.renderDistance * config.renderDistance
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        modules { id, _, state ->
            val persistentState = state.persistentState ?: return@modules
            val value = NBTSerialization.serialize(persistentState)
            tag.put(id.toString(), value)
        }
        return super.toTag(tag)
    }

    override fun fromTag(blockstate: BlockState, tag: CompoundTag) {
        super.fromTag(blockstate, tag)
        modules { id, _, state ->
            if (tag.contains(id.toString())) {
                state.persistentState = NBTSerialization.deserialize(tag.getCompound(id.toString()))
            }
        }
    }

    inline fun modules(func: (id: Identifier, builder: ModuleBuilder<ModuleState>, state: ModuleState) -> Unit) {
        config.modules.forEach { (id, dsl) ->
            val state = moduleManager.modules[id] ?: return@forEach
            @Suppress("UNCHECKED_CAST")
            func(id, dsl as ModuleBuilder<ModuleState>, state)
        }
    }
}

open class KDLTickableBlockEntity(config: BlockEntityBuilder) : KDLBlockEntity(config), Tickable {

    override fun tick() {
        modules { _, builder, state ->
            builder.onTick?.let { func -> func(ctx, state) }
        }
    }
}

private fun type(config: BlockEntityBuilder): BlockEntityType<*> {
    val id = config.type!!
    return Registry.BLOCK_ENTITY_TYPE[id] ?: error("Block entity ${config.type} is not registered")
}