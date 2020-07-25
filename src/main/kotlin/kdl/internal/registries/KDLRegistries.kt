package kdl.internal.registries

import com.mojang.serialization.Lifecycle
import kdl.api.util.id
import net.minecraft.block.Material
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object KDLRegistries{
    val materialRegistryKey: RegistryKey<Registry<Material>> =
        RegistryKey.ofRegistry<Material>(Identifier("kdl", "material"))

    val material: SimpleRegistry<Material> =
        SimpleRegistry<Material>(materialRegistryKey, Lifecycle.experimental())

    init {
        Registry.register(material, id("minecraft", "air"), Material.AIR)
        Registry.register(material, id("minecraft", "structure_void"), Material.STRUCTURE_VOID)
        Registry.register(material, id("minecraft", "portal"), Material.PORTAL)
        Registry.register(material, id("minecraft", "carpet"), Material.CARPET)
        Registry.register(material, id("minecraft", "plant"), Material.PLANT)
        Registry.register(material, id("minecraft", "underwater_plant"), Material.UNDERWATER_PLANT)
        Registry.register(material, id("minecraft", "replaceable_plant"), Material.REPLACEABLE_PLANT)
        Registry.register(
            material,
            id("minecraft", "replaceable_underwater_plant"),
            Material.REPLACEABLE_UNDERWATER_PLANT
        )
        Registry.register(material, id("minecraft", "water"), Material.WATER)
        Registry.register(material, id("minecraft", "bubble_column"), Material.BUBBLE_COLUMN)
        Registry.register(material, id("minecraft", "lava"), Material.LAVA)
        Registry.register(material, id("minecraft", "snow_layer"), Material.SNOW_LAYER)
        Registry.register(material, id("minecraft", "fire"), Material.FIRE)
        Registry.register(material, id("minecraft", "supported"), Material.SUPPORTED)
        Registry.register(material, id("minecraft", "cobweb"), Material.COBWEB)
        Registry.register(material, id("minecraft", "redstone_lamp"), Material.REDSTONE_LAMP)
        Registry.register(material, id("minecraft", "organic_product"), Material.ORGANIC_PRODUCT)
        Registry.register(material, id("minecraft", "soil"), Material.SOIL)
        Registry.register(material, id("minecraft", "solid_organic"), Material.SOLID_ORGANIC)
        Registry.register(material, id("minecraft", "dense_ice"), Material.DENSE_ICE)
        Registry.register(material, id("minecraft", "aggregate"), Material.AGGREGATE)
        Registry.register(material, id("minecraft", "sponge"), Material.SPONGE)
        Registry.register(material, id("minecraft", "shulker_box"), Material.SHULKER_BOX)
        Registry.register(material, id("minecraft", "wood"), Material.WOOD)
        Registry.register(material, id("minecraft", "nether_wood"), Material.NETHER_WOOD)
        Registry.register(material, id("minecraft", "bamboo_sapling"), Material.BAMBOO_SAPLING)
        Registry.register(material, id("minecraft", "bamboo"), Material.BAMBOO)
        Registry.register(material, id("minecraft", "wool"), Material.WOOL)
        Registry.register(material, id("minecraft", "tnt"), Material.TNT)
        Registry.register(material, id("minecraft", "leaves"), Material.LEAVES)
        Registry.register(material, id("minecraft", "glass"), Material.GLASS)
        Registry.register(material, id("minecraft", "ice"), Material.ICE)
        Registry.register(material, id("minecraft", "cactus"), Material.CACTUS)
        Registry.register(material, id("minecraft", "stone"), Material.STONE)
        Registry.register(material, id("minecraft", "metal"), Material.METAL)
        Registry.register(material, id("minecraft", "snow_block"), Material.SNOW_BLOCK)
        Registry.register(material, id("minecraft", "repair_station"), Material.REPAIR_STATION)
        Registry.register(material, id("minecraft", "barrier"), Material.BARRIER)
        Registry.register(material, id("minecraft", "piston"), Material.PISTON)
        Registry.register(material, id("minecraft", "unused_plant"), Material.UNUSED_PLANT)
        Registry.register(material, id("minecraft", "gourd"), Material.GOURD)
        Registry.register(material, id("minecraft", "egg"), Material.EGG)
        Registry.register(material, id("minecraft", "cake"), Material.CAKE)
    }
}