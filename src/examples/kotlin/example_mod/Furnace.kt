package example_mod

import kdl.KDL_ID
import kdl.api.block.BlockEntityBuilder
import kdl.api.block.OnUse
import kdl.api.block.blockentity.ModuleManager
import kdl.api.block.blockentity.SyncState
import kdl.api.gui.GuiBuilder
import kdl.api.gui.Pos
import kdl.api.gui.Size
import kdl.api.gui.widgets.*
import kdl.api.item.BlockCubeModel
import kdl.api.model.Transformation
import kdl.api.module.InventoryState
import kdl.api.module.inventory
import kdl.api.util.getModule
import kdl.api.util.getValue
import kdl.api.util.id
import kdl.api.util.math.vec2Of
import kdl.api.util.withProperty
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.Tag
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.SmeltingRecipe
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Direction
import java.io.Serializable

/**
 * Complete vanilla furnace example
 */
fun furnaceBlock() = ExampleModRef.blocks {
    block {
        name = "furnace"
        material = id("minecraft", "stone")

        // Base model of the block when is not cooking
        val disableState = BlockCubeModel(
            up = "minecraft:textures/block/furnace_top",
            down = "minecraft:textures/block/furnace_side",
            north = "minecraft:textures/block/furnace_front",
            south = "minecraft:textures/block/furnace_side",
            east = "minecraft:textures/block/furnace_side",
            west = "minecraft:textures/block/furnace_side",
            particle = "minecraft:textures/block/furnace_side"
        )

        item {
            defaultLocalizedName = "Example Furnace"
            display = disableState
        }

        blockState {
            properties {
                // Orientation on the horizontal plane
                horizontalFacing()
                // Working or not working (for fire texture), vanilla calls it "lit"
                boolean("working")
            }

            model {

                // The furnace uses 2 models with different textures, this is the model with fire on the front
                val enableState = disableState.copy(
                    north = "minecraft:textures/block/furnace_front_on"
                )

                // There are 2 properties and 8 possible states,
                // but we can use a loop to avoid half of the options
                for (working in listOf(false, true)) {

                    variant("facing=north,working=$working") {
                        display = if (working) enableState else disableState
                        rotation = Transformation.rotation0()
                    }
                    variant("facing=south,working=$working") {
                        display = if (working) enableState else disableState
                        rotation = Transformation.rotation180Y()
                    }
                    variant("facing=east,working=$working") {
                        display = if (working) enableState else disableState
                        rotation = Transformation.rotation270Y()
                    }
                    variant("facing=west,working=$working") {
                        display = if (working) enableState else disableState
                        rotation = Transformation.rotation90Y()
                    }
                }
            }
        }

        // We place the furnace with the correct orientation
        placementState = {
            result = block.defaultState
                .withProperty("facing", ctx.playerFacing.opposite)
                .withProperty("working", false)
        }

        // The BlockEntity will have an inventory and the logic of the furnace
        blockEntity {
            inventory { slots = 3 }
            furnaceModule()
        }

        gui {
            furnaceGui()
        }

        // The GUI gets opened when you right click the block
        onUse = OnUse.openGui(ExampleModRef.id("furnace"))

        // Spawn particles and sound
        onRandomDisplayTick = {
            val working = state.getValue<Boolean>("working")
            val direction = state.getValue<Direction>("facing")

            if (working) {
                val x = pos.x.toDouble() + 0.5
                val y = pos.y.toDouble()
                val z = pos.z.toDouble() + 0.5

                if (random.nextDouble() < 0.1) {
                    world.playSound(
                        x, y, z,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                        SoundCategory.BLOCKS,
                        1.0f, 1.0f, false
                    )
                }

                val axis = direction.axis
                val h = random.nextDouble() * 0.6 - 0.3
                val i = if (axis === Direction.Axis.X) direction.offsetX.toDouble() * 0.52 else h
                val j = random.nextDouble() * 6.0 / 16.0
                val k = if (axis === Direction.Axis.Z) direction.offsetZ.toDouble() * 0.52 else h

                world.addParticle(ParticleTypes.SMOKE, x + i, y + j, z + k, 0.0, 0.0, 0.0)
                world.addParticle(ParticleTypes.FLAME, x + i, y + j, z + k, 0.0, 0.0, 0.0)
            }
        }
    }
}

fun GuiBuilder.furnaceGui() {
    id = ExampleModRef.id("furnace")

    screenHandler {

        slots { ctx ->
            if (ctx.blockInventory != null) {
                slot(ctx.blockInventory, 0, 56, 17)
                slot(ctx.blockInventory, 1, 56, 53)
                slot(ctx.blockInventory, 2, 116, 35, canPlace = false)
            }

            playerInventory()
        }

        regions { ctx ->
            region(0, 1, name = "Input", filter = { _, stack ->
                // Check if the item has a smelting recipe
                ctx.world.recipeManager
                    .getFirstMatch(RecipeType.SMELTING, SimpleInventory(stack), ctx.world)
                    .isPresent
            })
            region(1, 1, name = "Fuel", filter = { _, stack ->
                // Check if the item is fuel
                AbstractFurnaceBlockEntity.canUseAsFuel(stack)
            })
            region(2, 1, name = "Output")
            region(3, 27, name = "PlayerInventoryMain")
            region(30, 9, name = "PlayerInventoryHotbar")
        }

        onTick = {
            // Send progress bar info to the client
            (blockEntity as? ModuleManager)?.sendUpdateToNearPlayers()
        }
    }

    screen {
        title = LiteralText("Furnace")

        widgets { ctx ->
            container {
                image {
                    label = "Background"
                    pos = Pos.parentRelative(0, 0)
                    size = Size.fixed(176, 166)
                    texture = id("minecraft", "textures/gui/container/furnace.png")
                    textureUVSize = vec2Of(176, 166)
                }

                horizontalProgressBar {
                    label = "Cooking progress"
                    pos = Pos.parentRelative(79, 34)
                    size = Size.fixed(24, 17)
                    texture = id("minecraft", "textures/gui/container/furnace.png")
                    textureUV = vec2Of(176, 14)
                    textureUVSize = vec2Of(24, 17)
                    progressGetter = func@{
                        val module = ctx.handlerCtx.blockEntity
                            ?.getModule<FurnaceState>(ExampleModRef.id("furnace_module"))
                            ?: return@func 0f

                        module.state.cookProgress
                    }
                }

                verticalProgressBar {
                    label = "Burning time"
                    pos = Pos.parentRelative(56, 36)
                    size = Size.fixed(14, 14)
                    texture = id("minecraft", "textures/gui/container/furnace.png")
                    textureUV = vec2Of(176, 0)
                    textureUVSize = vec2Of(14, 14)
                    progressGetter = func@{
                        val module = ctx.handlerCtx.blockEntity
                            ?.getModule<FurnaceState>(ExampleModRef.id("furnace_module"))
                            ?: return@func 0f

                        module.state.burnProgress
                    }
                }

                textLabel {
                    text = LiteralText("Furnace")
                    pos = Pos.parentRelative(67, 6)
                }

                textLabel {
                    text = LiteralText("Inventory")
                    pos = Pos.parentRelative(8, 72)
                }
            }
        }
    }
}

class FurnaceState : SyncState<Tag>, Serializable {
    var cookTime: Int = 0
    var maxCookTime: Int = 0
    var burnTime: Int = 0
    var maxBurnTime: Int = 0

    val cookProgress: Float get() = if (maxCookTime <= 0) 0f else (cookTime.toFloat() / maxCookTime).coerceIn(0f, 1f)
    val burnProgress: Float get() = if (maxBurnTime <= 0) 0f else (burnTime.toFloat() / maxBurnTime).coerceIn(0f, 1f)

    override fun toSend(): Tag = IntArrayTag(
        intArrayOf(
            cookTime,
            maxCookTime,
            burnTime,
            maxBurnTime
        )
    )

    override fun onReceive(value: Tag) {
        val array = value as IntArrayTag
        cookTime = array[0].int
        maxCookTime = array[1].int
        burnTime = array[2].int
        maxBurnTime = array[3].int
    }
}

fun BlockEntityBuilder.furnaceModule() {

    module<FurnaceState> {
        moduleType = ExampleModRef.id("furnace_module")
        onCreate = ::FurnaceState
        onTick = func@{ state ->
            // Only do processing in server-side
            if (world.isClient) return@func

            val previousBurnTime = state.burnTime

            // Consume fuel
            if (state.burnTime > 0) {
                state.burnTime--
            }

            // Get inventory
            val invModule = moduleManager.modules[id(KDL_ID, "inventory")] ?: return@func
            val inv = invModule.state as InventoryState

            // Slots contents
            val input = inv.getStack(0)
            val fuel = inv.getStack(1)
            val output = inv.getStack(2)

            if (input.isEmpty) {
                // Remove current progress if there is no input
                state.cookTime = 0
                return@func
            }

            fun isValidRecipe(recipe: SmeltingRecipe?, output: ItemStack): Boolean {
                if (recipe == null) return false
                if (recipe.output.isEmpty) return false

                if (output.isEmpty) return true
                if (!recipe.output.isItemEqualIgnoreDamage(output)) return false

                if (recipe.output.count < inv.maxCountPerStack && recipe.output.count < recipe.output.maxCount) return true

                return recipe.output.count < output.count
            }

            fun refuel() {
                val fuelValue = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuel.item, 0)

                // If there is a valid fuel, consume it
                if (fuelValue > 0) {
                    state.maxBurnTime = fuelValue
                    state.burnTime = fuelValue

                    // Consume fuel
                    fuel.decrement(1)
                    if (fuel.isEmpty) {
                        // Lava Buckets must keep the bucket
                        val remainder = fuel.item.recipeRemainder
                        val stack = remainder?.let { ItemStack(it) } ?: ItemStack.EMPTY

                        inv.setStack(1, stack)
                    }
                }
            }

            fun craft(recipe: SmeltingRecipe) {
                // Remove from input
                input.decrement(1)

                // Place on the output
                if (output.isEmpty) {
                    inv.setStack(2, recipe.output.copy())
                } else {
                    output.increment(recipe.output.count)
                }
            }

            // Vanilla checks for the current recipe all the time, this is not good but works for this example
            val recipe = world.recipeManager.getFirstMatch(RecipeType.SMELTING, inv, world)
                .orElse(null)

            // Work only if there is a valid recipe
            if (isValidRecipe(recipe, output)) {

                if (state.burnTime <= 0) refuel()

                // Set the maxCookTime, changes every ticks because the input can change anytime
                state.maxCookTime = recipe.cookTime

                // If there is fuel
                if (state.burnTime > 0) {
                    if (state.cookTime >= state.maxCookTime - 1) {
                        // Craft and reset
                        craft(recipe)
                        state.cookTime = 0
                        state.maxCookTime = 0
                    } else {
                        // Cook until the limit is reached
                        state.cookTime++
                    }
                }
            } else {
                // No recipe, no progress
                state.cookTime = 0
            }

            // Slowly remove current progress if there is no fuel
            if (state.burnTime <= 0) {
                if (state.cookTime > 0) {
                    state.cookTime = (state.cookTime - 2).coerceIn(0, state.maxCookTime)
                }
            }

            if ((previousBurnTime > 0) != (state.burnTime > 0)) {
                val workingState = moduleManager.blockstate
                    .withProperty("working", state.burnTime > 0)

                world.setBlockState(pos, workingState)
            }

            // Mark the chunk as "needs saving", otherwise the cooking progress is not saved to disk
            moduleManager.markDirty()
        }
    }
}
