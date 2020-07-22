package kdl.internal.registries

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import kdl.api.registries.IRegistries
import kdl.api.registries.IRegistry
import kdl.api.util.id
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.Schedule
import net.minecraft.entity.ai.brain.sensor.SensorType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.decoration.painting.PaintingMotive
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.loot.condition.LootConditionType
import net.minecraft.loot.entry.LootPoolEntryType
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.particle.ParticleType
import net.minecraft.potion.Potion
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.StatType
import net.minecraft.structure.StructurePieceType
import net.minecraft.structure.pool.StructurePoolElementType
import net.minecraft.structure.processor.StructureProcessorType
import net.minecraft.structure.rule.PosRuleTestType
import net.minecraft.structure.rule.RuleTestType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry
import net.minecraft.village.VillagerProfession
import net.minecraft.village.VillagerType
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.gen.carver.Carver
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.TreeDecoratorType
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.StructureFeature
import net.minecraft.world.gen.feature.size.FeatureSizeType
import net.minecraft.world.gen.foliage.FoliagePlacerType
import net.minecraft.world.gen.placer.BlockPlacerType
import net.minecraft.world.gen.stateprovider.BlockStateProviderType
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder
import net.minecraft.world.gen.trunk.TrunkPlacerType
import net.minecraft.world.poi.PointOfInterestType

internal object Registries : IRegistries {
    override val soundEvent: IRegistry<SoundEvent> =
        RegistryWrapper(Registry.SOUND_EVENT)

    override val fluid: IRegistry<Fluid> =
        RegistryWrapper(Registry.FLUID)

    override val statusEffect: IRegistry<StatusEffect> =
        RegistryWrapper(Registry.STATUS_EFFECT)

    override val block: IRegistry<Block> =
        RegistryWrapper(Registry.BLOCK)

    override val enchantment: IRegistry<Enchantment> =
        RegistryWrapper(Registry.ENCHANTMENT)

    override val entityType: IRegistry<EntityType<*>> =
        RegistryWrapper(Registry.ENTITY_TYPE)

    override val item: IRegistry<Item> =
        RegistryWrapper(Registry.ITEM)

    override val potion: IRegistry<Potion> =
        RegistryWrapper(Registry.POTION)

    override val carver: IRegistry<Carver<*>> =
        RegistryWrapper(Registry.CARVER)

    override val surfaceBuilder: IRegistry<SurfaceBuilder<*>> =
        RegistryWrapper(Registry.SURFACE_BUILDER)

    override val feature: IRegistry<Feature<*>> =
        RegistryWrapper(Registry.FEATURE)

    override val decorator: IRegistry<Decorator<*>> =
        RegistryWrapper(Registry.DECORATOR)

    override val biome: IRegistry<Biome> =
        RegistryWrapper(Registry.BIOME)

    override val blockStateProviderType: IRegistry<BlockStateProviderType<*>> =
        RegistryWrapper(Registry.BLOCK_STATE_PROVIDER_TYPE)

    override val blockPlacerType: IRegistry<BlockPlacerType<*>> =
        RegistryWrapper(Registry.BLOCK_PLACER_TYPE)

    override val foliagePlacerType: IRegistry<FoliagePlacerType<*>> =
        RegistryWrapper(Registry.FOLIAGE_PLACER_TYPE)

    override val trunkPlacerType: IRegistry<TrunkPlacerType<*>> =
        RegistryWrapper(Registry.TRUNK_PLACER_TYPE)

    override val treeDecoratorType: IRegistry<TreeDecoratorType<*>> =
        RegistryWrapper(Registry.TREE_DECORATOR_TYPE)

    override val featureSizeType: IRegistry<FeatureSizeType<*>> =
        RegistryWrapper(Registry.FEATURE_SIZE_TYPE)

    override val particleType: IRegistry<ParticleType<*>> =
        RegistryWrapper(Registry.PARTICLE_TYPE)

    override val biomeSource: IRegistry<Codec<out BiomeSource>> =
        RegistryWrapper(Registry.BIOME_SOURCE)

    override val chunkGenerator: IRegistry<Codec<out ChunkGenerator>> =
        RegistryWrapper(Registry.CHUNK_GENERATOR)

    override val blockEntityType: IRegistry<BlockEntityType<*>> =
        RegistryWrapper(Registry.BLOCK_ENTITY_TYPE)

    override val paintingMotive: IRegistry<PaintingMotive> =
        RegistryWrapper(Registry.PAINTING_MOTIVE)

    override val customStat: IRegistry<Identifier> =
        RegistryWrapper(Registry.CUSTOM_STAT)

    override val chunkStatus: IRegistry<ChunkStatus> =
        RegistryWrapper(Registry.CHUNK_STATUS)

    override val structureFeature: IRegistry<StructureFeature<*>> =
        RegistryWrapper(Registry.STRUCTURE_FEATURE)

    override val structurePiece: IRegistry<StructurePieceType> =
        RegistryWrapper(Registry.STRUCTURE_PIECE)

    override val ruleTest: IRegistry<RuleTestType<*>> =
        RegistryWrapper(Registry.RULE_TEST)

    override val posRuleTest: IRegistry<PosRuleTestType<*>> =
        RegistryWrapper(Registry.POS_RULE_TEST)

    override val structureProcessor: IRegistry<StructureProcessorType<*>> =
        RegistryWrapper(Registry.STRUCTURE_PROCESSOR)

    override val structurePoolElement: IRegistry<StructurePoolElementType<*>> =
        RegistryWrapper(Registry.STRUCTURE_POOL_ELEMENT)

    override val screenHandler: IRegistry<ScreenHandlerType<*>> =
        RegistryWrapper(Registry.SCREEN_HANDLER)

    override val recipeType: IRegistry<RecipeType<*>> =
        RegistryWrapper(Registry.RECIPE_TYPE)

    override val recipeSerializer: IRegistry<RecipeSerializer<*>> =
        RegistryWrapper(Registry.RECIPE_SERIALIZER)

    override val attribute: IRegistry<EntityAttribute> =
        RegistryWrapper(Registry.ATTRIBUTE)

    override val statType: IRegistry<StatType<*>> =
        RegistryWrapper(Registry.STAT_TYPE)

    override val villagerType: IRegistry<VillagerType> =
        RegistryWrapper(Registry.VILLAGER_TYPE)

    override val villagerProfession: IRegistry<VillagerProfession> =
        RegistryWrapper(Registry.VILLAGER_PROFESSION)

    override val pointOfInterestType: IRegistry<PointOfInterestType> =
        RegistryWrapper(Registry.POINT_OF_INTEREST_TYPE)

    override val memoryModuleType: IRegistry<MemoryModuleType<*>> =
        RegistryWrapper(Registry.MEMORY_MODULE_TYPE)

    override val sensorType: IRegistry<SensorType<*>> =
        RegistryWrapper(Registry.SENSOR_TYPE)

    override val schedule: IRegistry<Schedule> =
        RegistryWrapper(Registry.SCHEDULE)

    override val activity: IRegistry<Activity> =
        RegistryWrapper(Registry.ACTIVITY)

    override val lootPoolEntryType: IRegistry<LootPoolEntryType> =
        RegistryWrapper(Registry.LOOT_POOL_ENTRY_TYPE)

    override val lootFunctionType: IRegistry<LootFunctionType> =
        RegistryWrapper(Registry.LOOT_FUNCTION_TYPE)

    override val lootConditionType: IRegistry<LootConditionType> =
        RegistryWrapper(Registry.LOOT_CONDITION_TYPE)

    val materialRegistryKey: RegistryKey<Registry<Material>> =
        RegistryKey.ofRegistry<Material>(Identifier("kdl", "material"))

    val materialRegistry: SimpleRegistry<Material> =
        SimpleRegistry<Material>(materialRegistryKey, Lifecycle.experimental())

    override val material: IRegistry<Material> =
        RegistryWrapper(materialRegistry)

    init {
        Registry.register(materialRegistry, id("minecraft", "air"), Material.AIR)
        Registry.register(materialRegistry, id("minecraft", "structure_void"), Material.STRUCTURE_VOID)
        Registry.register(materialRegistry, id("minecraft", "portal"), Material.PORTAL)
        Registry.register(materialRegistry, id("minecraft", "carpet"), Material.CARPET)
        Registry.register(materialRegistry, id("minecraft", "plant"), Material.PLANT)
        Registry.register(materialRegistry, id("minecraft", "underwater_plant"), Material.UNDERWATER_PLANT)
        Registry.register(materialRegistry, id("minecraft", "replaceable_plant"), Material.REPLACEABLE_PLANT)
        Registry.register(
            materialRegistry, id("minecraft", "replaceable_underwater_plant"), Material.REPLACEABLE_UNDERWATER_PLANT
        )
        Registry.register(materialRegistry, id("minecraft", "water"), Material.WATER)
        Registry.register(materialRegistry, id("minecraft", "bubble_column"), Material.BUBBLE_COLUMN)
        Registry.register(materialRegistry, id("minecraft", "lava"), Material.LAVA)
        Registry.register(materialRegistry, id("minecraft", "snow_layer"), Material.SNOW_LAYER)
        Registry.register(materialRegistry, id("minecraft", "fire"), Material.FIRE)
        Registry.register(materialRegistry, id("minecraft", "supported"), Material.SUPPORTED)
        Registry.register(materialRegistry, id("minecraft", "cobweb"), Material.COBWEB)
        Registry.register(materialRegistry, id("minecraft", "redstone_lamp"), Material.REDSTONE_LAMP)
        Registry.register(materialRegistry, id("minecraft", "organic_product"), Material.ORGANIC_PRODUCT)
        Registry.register(materialRegistry, id("minecraft", "soil"), Material.SOIL)
        Registry.register(materialRegistry, id("minecraft", "solid_organic"), Material.SOLID_ORGANIC)
        Registry.register(materialRegistry, id("minecraft", "dense_ice"), Material.DENSE_ICE)
        Registry.register(materialRegistry, id("minecraft", "aggregate"), Material.AGGREGATE)
        Registry.register(materialRegistry, id("minecraft", "sponge"), Material.SPONGE)
        Registry.register(materialRegistry, id("minecraft", "shulker_box"), Material.SHULKER_BOX)
        Registry.register(materialRegistry, id("minecraft", "wood"), Material.WOOD)
        Registry.register(materialRegistry, id("minecraft", "nether_wood"), Material.NETHER_WOOD)
        Registry.register(materialRegistry, id("minecraft", "bamboo_sapling"), Material.BAMBOO_SAPLING)
        Registry.register(materialRegistry, id("minecraft", "bamboo"), Material.BAMBOO)
        Registry.register(materialRegistry, id("minecraft", "wool"), Material.WOOL)
        Registry.register(materialRegistry, id("minecraft", "tnt"), Material.TNT)
        Registry.register(materialRegistry, id("minecraft", "leaves"), Material.LEAVES)
        Registry.register(materialRegistry, id("minecraft", "glass"), Material.GLASS)
        Registry.register(materialRegistry, id("minecraft", "ice"), Material.ICE)
        Registry.register(materialRegistry, id("minecraft", "cactus"), Material.CACTUS)
        Registry.register(materialRegistry, id("minecraft", "stone"), Material.STONE)
        Registry.register(materialRegistry, id("minecraft", "metal"), Material.METAL)
        Registry.register(materialRegistry, id("minecraft", "snow_block"), Material.SNOW_BLOCK)
        Registry.register(materialRegistry, id("minecraft", "repair_station"), Material.REPAIR_STATION)
        Registry.register(materialRegistry, id("minecraft", "barrier"), Material.BARRIER)
        Registry.register(materialRegistry, id("minecraft", "piston"), Material.PISTON)
        Registry.register(materialRegistry, id("minecraft", "unused_plant"), Material.UNUSED_PLANT)
        Registry.register(materialRegistry, id("minecraft", "gourd"), Material.GOURD)
        Registry.register(materialRegistry, id("minecraft", "egg"), Material.EGG)
        Registry.register(materialRegistry, id("minecraft", "cake"), Material.CAKE)
    }
}

internal class RegistryWrapper<T>(private val reg: Registry<T>) : IRegistry<T> {
    override fun get(id: Identifier): T? = reg.get(id)
}