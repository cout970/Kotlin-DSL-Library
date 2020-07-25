package kdl.internal.block

import kdl.api.block.*
import kdl.api.module.InventoryState
import kdl.api.util.getModule
import kdl.api.util.id
import kdl.internal.block.blockentity.KDLBlockEntity
import kdl.internal.registries.InstanceManager
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.explosion.Explosion
import java.util.*

open class KDLBlock(settings: Settings) : Block(settings), InventoryProvider {
    lateinit var config: BlockBuilder

    companion object {
        // Fuck you Mojang, stop using bugs to design your code
        lateinit var _constructor_config_: BlockBuilder
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        val list = _constructor_config_.blockStateConfig?.properties ?: return

        list.forEach { builder.add(it) }
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        val default = super.onUse(state, world, pos, player, hand, hit)
        config.onUse?.let { func ->
            return BlockOnUse(default, this, state, world, pos, player, hand, hit).apply(func).result
        }
        return default
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        config.onBreak?.let { func ->
            val event = BlockOnBreak(false, this, world, pos, state, player).apply(func)
            if (event.preventDefault) return
        }

        (world.getBlockEntity(pos) as? KDLBlockEntity)?.onBreak()
        super.onBreak(world, pos, state, player)
    }

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        config.onBroken?.let { func ->
            val event = BlockOnBroken(false, this, world, pos, state).apply(func)
            if (event.preventDefault) return
        }
        super.onBroken(world, pos, state)
    }

    override fun onDestroyedByExplosion(world: World, pos: BlockPos, explosion: Explosion) {
        config.onDestroyedByExplosion?.let { func ->
            val event = BlockOnDestroyedByExplosion(false, this, world, pos, explosion).apply(func)
            if (event.preventDefault) return
        }
        super.onDestroyedByExplosion(world, pos, explosion)
    }

    override fun onEntityLand(world: BlockView, entity: Entity) {
        config.onEntityLand?.let { func ->
            val event = BlockOnEntityLand(false, this, world, entity).apply(func)
            if (event.preventDefault) return
        }
        super.onEntityLand(world, entity)
    }

    override fun onLandedUpon(world: World, pos: BlockPos, entity: Entity, distance: Float) {
        config.onLandedUpon?.let { func ->
            val event = BlockOnLandedUpon(false, this, world, pos, entity, distance).apply(func)
            if (event.preventDefault) return
        }
        super.onLandedUpon(world, pos, entity, distance)
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        config.onPlaced?.let { func ->
            val event = BlockOnPlaced(false, this, world, pos, state, placer, itemStack).apply(func)
            if (event.preventDefault) return
        }
        super.onPlaced(world, pos, state, placer, itemStack)
    }

    override fun onSteppedOn(world: World, pos: BlockPos, entity: Entity) {
        config.onSteppedOn?.let { func ->
            val event = BlockOnSteppedOn(false, this, world, pos, entity).apply(func)
            if (event.preventDefault) return
        }
        super.onSteppedOn(world, pos, entity)
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        config.onBlockAdded?.let { func ->
            val event = BlockOnBlockAdded(false, this, state, world, pos, oldState, notify).apply(func)
            if (event.preventDefault) return
        }
        super.onBlockAdded(state, world, pos, oldState, notify)
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        config.onEntityCollision?.let { func ->
            val event = BlockOnEntityCollision(false, this, state, world, pos, entity).apply(func)
            if (event.preventDefault) return
        }
        super.onEntityCollision(state, world, pos, entity)
    }

    override fun onProjectileHit(
        world: World,
        state: BlockState,
        hit: BlockHitResult,
        projectile: ProjectileEntity
    ) {
        config.onProjectileHit?.let { func ->
            val event = BlockOnProjectileHit(false, this, world, state, hit, projectile).apply(func)
            if (event.preventDefault) return
        }
        super.onProjectileHit(world, state, hit, projectile)
    }

    override fun onStacksDropped(state: BlockState, world: World, pos: BlockPos, stack: ItemStack) {
        config.onStacksDropped?.let { func ->
            val event = BlockOnStacksDropped(false, this, state, world, pos, stack).apply(func)
            if (event.preventDefault) return
        }
        super.onStacksDropped(state, world, pos, stack)
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        config.onStateReplaced?.let { func ->
            val event = BlockOnStateReplaced(false, this, state, world, pos, newState, moved).apply(func)
            if (event.preventDefault) return
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun onSyncedBlockEvent(state: BlockState, world: World, pos: BlockPos, type: Int, data: Int): Boolean {
        val default = super.onSyncedBlockEvent(state, world, pos, type, data)
        config.onSyncedBlockEvent?.let { func ->
            return BlockOnSyncedBlockEvent(default, this, state, world, pos, type, data).apply(func).result
        }
        return default
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val default = super.getPlacementState(ctx)!!
        config.placementState?.let { func ->
            return BlockPlacementState(default, this, ctx).apply(func).result
        }
        return default
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        config.onRandomDisplayTick?.let { func ->
            val event = BlockOnRandomDisplayTick(false, this, state, world, pos, random).apply(func)
            if (event.preventDefault) return
        }
        super.randomDisplayTick(state, world, pos, random)
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory? {
        val blockEntity = (world.getBlockEntity(pos) as? KDLBlockEntity) ?: return null
        val module = blockEntity.getModule<InventoryState>(id("kdl", "inventory")) ?: return null
        val inv = module.state

        class SInventory(val inv: SimpleInventory) : Inventory by inv, SidedInventory {
            override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction): Boolean = true

            override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean = true

            override fun getAvailableSlots(side: Direction): IntArray = (0 until inv.size()).toList().toIntArray()
        }
        return SInventory(inv)
    }
}

open class KDLBlockWithEntity(settings: Settings) : KDLBlock(settings), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView?): BlockEntity? {
        val config = config.blockEntityConfig!!
        val tickable = config.modules.any { it.value.onTick != null }

        return InstanceManager.newKDLBlockEntity(config.type!!, config, tickable)
    }
}