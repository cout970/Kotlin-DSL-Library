import example_mod.ExampleModRef
import kdl.api.item.ItemSpriteModel

fun items() = ExampleModRef.items {
    item {
        name = "stick"
        defaultLocalizedName = "Stick"
        display = ItemSpriteModel("items/stick")
    }
    item {
        name = "stick2"
        defaultLocalizedName = "Stick 2"
        display = ItemSpriteModel("items/stick")
    }
}