package kdl.api.model

import kdl.api.KDL
import kdl.api.item.DisplayModel

@KDL
class BlockstateModelBuilder {
    val variants = mutableMapOf<String, MutableList<BlockstateVariantBuilder>>()

    fun variant(name: String, func: BlockstateVariantBuilder.() -> Unit) {
        val list = variants.getOrPut(name) { mutableListOf() }

        list += BlockstateVariantBuilder().apply(func)
    }
}

@KDL
class BlockstateVariantBuilder {
    var display: DisplayModel? = null
    var rotation: Transformation? = null
    var uvLock: Boolean = false
}