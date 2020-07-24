package kdl.internal.util

import kdl.api.util.NBTSerializable
import kdl.api.util.NBTSerialization
import kdl.api.util.NBTSerializer
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import java.io.*

private const val KEY_TYPE = "t"
private const val KEY_VALUE = "v"
private const val KEY_CLASS = "c"

private const val TYPE_TAG = 0.toByte()
private const val TYPE_CUSTOM = 1.toByte()
private const val TYPE_NBT_SERIALIZABLE = 2.toByte()
private const val TYPE_SERIALIZABLE = 3.toByte()

object NBTSerializationImpl {

    fun serialize(value: Any): CompoundTag {
        val tag = CompoundTag()
        val clazz = value.javaClass

        if (clazz in NBTSerialization.customSerializers) {
            val serializer = NBTSerialization.customSerializers[clazz] as NBTSerializer<Any?>

            tag.putByte(KEY_TYPE, TYPE_CUSTOM)
            tag.putString(KEY_CLASS, clazz.canonicalName)
            tag.put(KEY_VALUE, serializer.serialize(value))
            return tag
        }

        when (value) {
            is Tag -> {
                tag.putByte(KEY_TYPE, TYPE_TAG)
                tag.put(KEY_VALUE, value)
            }
            is NBTSerializable -> {
                tag.putByte(KEY_TYPE, TYPE_CUSTOM)
                tag.putString(KEY_CLASS, clazz.canonicalName)
                val subTag = CompoundTag()

                asmFields(value) { field, subValue ->
                    if (subValue == null)
                        subTag.putByte(field, 0)
                    else
                        subTag.put(field, serialize(subValue))
                }
                tag.put(KEY_VALUE, subTag)
            }
            is Serializable -> {
                val bytes = ByteArrayOutputStream()
                ObjectOutputStream(bytes).use { it.writeObject(value) }

                tag.putByte(KEY_TYPE, TYPE_SERIALIZABLE)
                tag.putByteArray(KEY_VALUE, bytes.toByteArray())
            }
            else -> error("Unable find a way to serialize value: $value with class $clazz")
        }
        return tag
    }

    fun deserialize(tag: CompoundTag): Any {
        val type = tag.getByte(KEY_TYPE)
        val value = tag.get(KEY_VALUE)
        val clazz = tag.getString(KEY_CLASS)

        return when (type) {
            TYPE_TAG -> value!!
            TYPE_CUSTOM -> {
                val serializer = NBTSerialization.customSerializers[Class.forName(clazz)]!!
                serializer.deserialize(value as CompoundTag) as Any
            }
            TYPE_NBT_SERIALIZABLE -> {
                val arguments = value as CompoundTag
                val constructors = Class.forName(clazz).constructors
                val primary = constructors
                    .find { it.parameters.map { prop -> prop.name }.toSet() == arguments.keys }
                    ?: error("No constructor with values ${arguments.keys} found in class $clazz")

                val sortedArguments = mutableListOf<Any?>()
                primary.parameters.map { prop ->
                    sortedArguments += deserialize(arguments.getCompound(prop.name))
                }
                primary.newInstance(sortedArguments)
            }
            TYPE_SERIALIZABLE -> {
                val bytes = ByteArrayInputStream((value as ByteArrayTag).byteArray)

                ObjectInputStream(bytes).use { it.readObject() }
            }
            else -> error("Unable to determine type of serialized data in:\n$tag")
        }
    }

    private inline fun asmFields(value: Any, func: (field: String, value: Any?) -> Unit) {
        value.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            func(field.name, field[value])
        }
    }

    init {
        NBTSerialization.customSerializers[Unit::class.java] = object : NBTSerializer<Unit> {
            override fun serialize(value: Unit): CompoundTag = CompoundTag()

            override fun deserialize(tag: CompoundTag): Unit = Unit
        }

        NBTSerialization.customSerializers[ItemStack::class.java] = object : NBTSerializer<ItemStack> {
            override fun serialize(value: ItemStack): CompoundTag {
                return value.toTag(CompoundTag())
            }

            override fun deserialize(tag: CompoundTag): ItemStack {
                return ItemStack.fromTag(tag)
            }
        }

        NBTSerialization.customSerializers[SimpleInventory::class.java] = object : NBTSerializer<SimpleInventory> {
            override fun serialize(value: SimpleInventory): CompoundTag {
                val tag = CompoundTag()
                tag.putInt("Size", value.size())

                for (i in 0 until value.size()) {
                    tag.put(i.toString(), NBTSerialization.serialize(value.getStack(i)))
                }

                return tag
            }

            override fun deserialize(tag: CompoundTag): SimpleInventory {
                val size = tag.getInt("Size")
                val value = SimpleInventory(size)

                for (i in 0 until value.size()) {
                    val item = NBTSerialization.deserialize(tag.getCompound(i.toString()))
                    value.setStack(i, item as ItemStack)
                }

                return value
            }
        }
    }
}

