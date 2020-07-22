package kdl.internal.client

import com.google.gson.Gson
import kdl.api.item.BlockCubeModel
import kdl.api.item.CustomDisplayModel
import kdl.api.item.DisplayModel
import kdl.api.item.ItemSpriteModel
import kdl.api.model.JsonModel
import net.minecraft.block.BlockState
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier

object ModelManager {
    private val GSON = Gson()
    private val customModels = mutableMapOf<Identifier, JsonUnbakedModel>()
    private val customBlockstateModels = mutableMapOf<Identifier, (ModelIdentifier, BlockState) -> UnbakedModel?>()
    private val customTexturePaths = mutableMapOf<Identifier, Identifier>()

    fun getCustomModel(id: Identifier): JsonUnbakedModel? {
        return customModels[id]
    }

    fun getBlockstateConfig(id: Identifier): ((ModelIdentifier, BlockState) -> UnbakedModel?)? {
        return customBlockstateModels[id]
    }

    fun getCustomTexturePath(id: Identifier): Identifier? = customTexturePaths[id]

    fun removeItemModel(id: Identifier) = customModels.remove(id)

    fun registerTexturePath(namespace: String, path: String) {
        val id = Identifier(namespace, path)
        customTexturePaths[id] = Identifier(namespace, "$path.png")
    }

    fun registerItemSprite(id: Identifier, path: String, item: Boolean): Identifier {

        val jsonModel = JsonModel()
        jsonModel.parent = "minecraft:item/handheld"
        jsonModel.textures["layer0"] = "${id.namespace}:${path}"

        val model = JsonUnbakedModel.deserialize(GSON.toJson(jsonModel))
        model.id = id.toString()

        registerTexturePath(id.namespace, path)

        val modelPath = if (item)
            Identifier(id.namespace, "item/${id.path}")
        else
            Identifier(id.namespace, "blocks/${id.path}")

        customModels[modelPath] = model
        return modelPath
    }

    fun idOf(mod: Identifier, path: String): Identifier {
        if (path.contains(':')) {
            val id = Identifier.tryParse(path)
            if (id != null) return id
        }
        return Identifier(mod.namespace, path)
    }

    fun registerBlockCube(id: Identifier, blockCube: BlockCubeModel, item: Boolean): Identifier {

        val particle = idOf(id, blockCube.particle)
        val down = idOf(id, blockCube.down)
        val up = idOf(id, blockCube.up)
        val north = idOf(id, blockCube.north)
        val east = idOf(id, blockCube.east)
        val south = idOf(id, blockCube.south)
        val west = idOf(id, blockCube.west)

        val jsonModel = JsonModel()
        jsonModel.parent = "minecraft:block/cube"
        jsonModel.textures["particle"] = particle.toString()
        jsonModel.textures["down"] = down.toString()
        jsonModel.textures["up"] = up.toString()
        jsonModel.textures["north"] = north.toString()
        jsonModel.textures["east"] = east.toString()
        jsonModel.textures["south"] = south.toString()
        jsonModel.textures["west"] = west.toString()

        val model = JsonUnbakedModel.deserialize(GSON.toJson(jsonModel))
        model.id = id.toString()

        registerTexturePath(particle.namespace, particle.path)
        registerTexturePath(down.namespace, down.path)
        registerTexturePath(up.namespace, up.path)
        registerTexturePath(north.namespace, north.path)
        registerTexturePath(east.namespace, east.path)
        registerTexturePath(south.namespace, south.path)
        registerTexturePath(west.namespace, west.path)

        val modelPath = if (item)
            Identifier(id.namespace, "item/${id.path}")
        else
            Identifier(id.namespace, "blocks/${id.path}")

        customModels[modelPath] = model
        return modelPath
    }

    fun registerCustomModel(id: Identifier, jsonModel: JsonModel, item: Boolean): Identifier {

        val model = JsonUnbakedModel.deserialize(GSON.toJson(jsonModel))
        model.id = id.toString()

        jsonModel.textures.values.forEach { path ->
            registerTexturePath(id.namespace, Identifier(path).path)
        }

        val modelPath = if (item)
            Identifier(id.namespace, "item/${id.path}")
        else
            Identifier(id.namespace, "blocks/${id.path}")

        customModels[modelPath] = model
        return modelPath
    }

    fun registerDisplay(id: Identifier, display: DisplayModel?, item: Boolean): Identifier? {
        return when (display) {
            is ItemSpriteModel -> registerItemSprite(id, display.path, item)
            is BlockCubeModel -> registerBlockCube(id, display, item)
            is CustomDisplayModel -> registerCustomModel(id, display.model, item)
            null -> {
                removeItemModel(id)
                null
            }
        }
    }

    fun registerBlockstate(id: Identifier, getter: (ModelIdentifier, BlockState) -> UnbakedModel?) {
        val blockstateJson = Identifier(id.namespace, "blockstates/${id.path}.json")
        customBlockstateModels[blockstateJson] = getter
    }
}