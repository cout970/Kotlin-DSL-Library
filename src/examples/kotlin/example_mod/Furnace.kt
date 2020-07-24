package example_mod

import kdl.KDL_ID
import kdl.api.block.OnUse
import kdl.api.gui.GuiBuilder
import kdl.api.gui.Pos
import kdl.api.gui.Size
import kdl.api.gui.widgets.container
import kdl.api.gui.widgets.horizontalProgressBar
import kdl.api.gui.widgets.image
import kdl.api.gui.widgets.verticalProgressBar
import kdl.api.item.BlockCubeModel
import kdl.api.model.Transformation
import kdl.api.module.InventoryState
import kdl.api.module.inventory
import kdl.api.util.getModule
import kdl.api.util.id
import kdl.api.util.math.vec2Of
import kdl.api.util.withProperty
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
            val inv = ctx.world.getBlockEntity(ctx.blockPos!!)
                ?.getModule<InventoryState>(id(KDL_ID, "inventory"))

            if (inv != null) {
                slot(inv.inventory, 0, 56, 17)
                slot(inv.inventory, 1, 56, 53)
                slot(inv.inventory, 2, 116, 35)
            }

            playerInventory()
        }

        regions {
            region(0, 1, name = "Input")
            region(1, 1, name = "Fuel")
            region(2, 1, name = "Output")
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
                    pos = Pos.centerRelative(-9, -49)
                    size = Size.fixed(24, 17)
                    texture = id("minecraft", "textures/gui/container/furnace.png")
                    textureUV = vec2Of(176, 14)
                    textureUVSize = vec2Of(24, 17)
                    progressGetter = { 0.75f /* Temp */ }
                }

                verticalProgressBar {
                    label = "Burning time"
                    pos = Pos.centerRelative(-31, -46)
                    size = Size.fixed(14, 14)
                    texture = id("minecraft", "textures/gui/container/furnace.png")
                    textureUV = vec2Of(176, 0)
                    textureUVSize = vec2Of(14, 14)
                    progressGetter = { 0.75f /* Temp */ }
                }
            }
        }
    }
}