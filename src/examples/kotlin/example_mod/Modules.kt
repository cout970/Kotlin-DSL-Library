package example_mod

import kdl.api.block.BlockEntityBuilder
import kdl.api.block.blockentity.ModuleState
import kdl.api.util.findProperty
import net.minecraft.util.StringIdentifiable
import java.io.Serializable


enum class States : StringIdentifiable {
    A, B, C;

    override fun asString(): String = name.toLowerCase()
}

fun BlockEntityBuilder.customModule() {
    data class Counter(var count: Int = 0) : Serializable, ModuleState

    module<Counter> {
        moduleType = ExampleModRef.id("custom_module")
        onCreate = { Counter() }
        onTick = { counter ->
            val enumProperty = moduleManager.blockstate.findProperty<States>("option")!!

            if (counter.count % 20 == 0) {
                world.setBlockState(pos, moduleManager.blockstate.cycle(enumProperty))
            }
            counter.count++
        }
    }
}