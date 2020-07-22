package kdl.api.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.explosion.Explosion

class BlockOnUse(
    var result: ActionResult,
    val block: Block,
    val state: BlockState,
    val world: World,
    val pos: BlockPos,
    val player: PlayerEntity,
    val hand: Hand,
    val hit: BlockHitResult
)

class BlockOnBreak(
    var preventDefault: Boolean,
    val block: Block,
    val world: World,
    val pos: BlockPos,
    val state: BlockState,
    val player: PlayerEntity
)

class BlockOnBroken(
    var preventDefault: Boolean,
    val block: Block,
    val world: WorldAccess,
    val pos: BlockPos,
    val state: BlockState
)

class BlockOnDestroyedByExplosion(
    var preventDefault: Boolean,
    val block: Block,
    val world: World,
    val pos: BlockPos,
    val explosion: Explosion
)

class BlockOnEntityLand(
    var preventDefault: Boolean,
    val block: Block,
    val world: BlockView,
    val entity: Entity
)

class BlockOnLandedUpon(
    var preventDefault: Boolean,
    val block: Block,
    val world: World,
    val pos: BlockPos,
    val entity: Entity,
    val distance: Float
)

class BlockOnPlaced(
    var preventDefault: Boolean,
    val block: Block,
    val world: World,
    val pos: BlockPos,
    val state: BlockState,
    val placer: LivingEntity?,
    val itemStack: ItemStack
)

class BlockOnSteppedOn(
    var preventDefault: Boolean,
    val block: Block,
    val world: World,
    val pos: BlockPos,
    val entity: Entity
)

class BlockOnBlockAdded(
    var preventDefault: Boolean,
    val block: Block,
    val state: BlockState,
    val world: World,
    val pos: BlockPos,
    val oldState: BlockState,
    val notify: Boolean
)

class BlockOnEntityCollision(
    var preventDefault: Boolean,
    val block: Block,
    val state: BlockState,
    val world: World,
    val pos: BlockPos,
    val entity: Entity
)

class BlockOnProjectileHit(
    var preventDefault: Boolean,
    val block: Block,
    val world: World,
    val state: BlockState,
    val hit: BlockHitResult,
    val projectile: ProjectileEntity
)

class BlockOnStacksDropped(
    var preventDefault: Boolean,
    val block: Block,
    val state: BlockState,
    val world: World,
    val pos: BlockPos,
    val stack: ItemStack
)

class BlockOnStateReplaced(
    var preventDefault: Boolean,
    val block: Block,
    val state: BlockState,
    val world: World,
    val pos: BlockPos,
    val newState: BlockState,
    val moved: Boolean
)

class BlockOnSyncedBlockEvent(
    var result: Boolean,
    val block: Block,
    val state: BlockState,
    val world: World,
    val pos: BlockPos,
    val type: Int,
    val data: Int
)

class BlockPlacementState(
    var result: BlockState?,
    val block: Block,
    val ctx: ItemPlacementContext
)