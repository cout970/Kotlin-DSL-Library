package example_mod

import kdl.api.block.OnUse
import kdl.api.gui.GuiBuilder
import kdl.api.gui.Pos
import kdl.api.gui.Size
import kdl.api.gui.widgets.*
import kdl.api.item.BlockCubeModel
import kdl.api.model.Transformation
import kdl.api.module.inventory
import kdl.api.util.id
import kdl.api.util.math.vec2Of
import kdl.api.util.withProperty
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.recipe.RecipeType
import net.minecraft.text.LiteralText

fun furnaceBlock() = ExampleModRef.blocks {
    block {
        name = "furnace"
        material = id("minecraft", "stone")

        val cube = BlockCubeModel(
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
            display = cube
        }

        blockState {
            properties {
                horizontalFacing()
            }

            model {
                variant("facing=north") {
                    display = cube
                    rotation = Transformation.rotation0()
                }
                variant("facing=south") {
                    display = cube
                    rotation = Transformation.rotation180Y()
                }
                variant("facing=east") {
                    display = cube
                    rotation = Transformation.rotation270Y()
                }
                variant("facing=west") {
                    display = cube
                    rotation = Transformation.rotation90Y()
                }
            }
        }

        placementState = {
            result = block.defaultState.withProperty("facing", ctx.playerFacing.opposite)
        }

        blockEntity {
            inventory { slots = 3 }
        }

        gui {
            furnaceGui()
        }

        onUse = OnUse.openGui(ExampleModRef.id("furnace"))
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
    }

    screen {
        title = LiteralText("Furnace")

        widgets {
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
                    progressGetter = { 0.75f /* Temp */ }
                }

                verticalProgressBar {
                    label = "Burning time"
                    pos = Pos.parentRelative(57, 37)
                    size = Size.fixed(14, 14)
                    texture = id("minecraft", "textures/gui/container/furnace.png")
                    textureUV = vec2Of(176, 0)
                    textureUVSize = vec2Of(14, 14)
                    progressGetter = { 0.75f /* Temp */ }
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