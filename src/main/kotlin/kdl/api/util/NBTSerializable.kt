package kdl.api.util

import kdl.internal.util.NBTSerializationImpl
import net.minecraft.nbt.CompoundTag

/**
 * This interface marks classes that can be serialized and deserialized to NBT using reflection.
 *
 * A constructor will all fields is required to create a new instances
 */
interface NBTSerializable

/**
 * Allows custom serializers for any class
 */
interface NBTSerializer {
    fun serialize(value: Any): CompoundTag
    fun deserialize(tag: CompoundTag): Any
}

object NBTSerialization {
    /**
     * Available serializers by class
     *
     * You can add your own serializers to the map
     */
    val customSerializers = mutableMapOf<Class<*>, NBTSerializer>()

    /**
     * Serializes a value to a CompoundTag, it supports:
     * - NBT Tags
     * - Custom Serializers with NBTSerializer
     * - Classes with NBTSerializable
     * - Classes implementing Serializable
     */
    fun serialize(value: Any): CompoundTag = NBTSerializationImpl.serialize(value)

    /**
     * Reverse operation of serialize
     */
    fun deserialize(tag: CompoundTag): Any = NBTSerializationImpl.deserialize(tag)
}