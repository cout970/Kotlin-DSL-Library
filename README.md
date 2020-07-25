# Kotlin DSL Library (KDL)
A minecraft modding library to do the heavy lifting for you.

Based on [KDS](https://github.com/cout970/KDS) but without scripts

Check a complete rewrite of the vanilla furnace with this library [here](src/examples/kotlin/example_mod/Furnace.kt)

### Quick Example
This is an [example](src/examples/kotlin/example_mod/Blocks.kt) of a simple block with a cube model, a texture, a blockitem and a localized name. 
```kotlin
block {
        name = "magic_dirt"
        material = id("minecraft", "dirt")

        item {
            defaultLocalizedName = "Magic Dirt"
            display = BlockCubeModel("blocks/magic_dirt")
        }

        blockState {
            model {
                variant("") {
                    display = BlockCubeModel("blocks/magic_dirt")
                }
            }
        }
    }
```

### Need help?
You can find me at [Discord](https://discord.gg/VbQs3ve) and ask anything.