package kdl.api.registries

import com.mojang.serialization.Codec
import kdl.api.API
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

/**
 * List of registries to be accessed by scripts
 */
interface IRegistries {
    val soundEvent: IRegistry<SoundEvent>
    val fluid: IRegistry<Fluid>
    val statusEffect: IRegistry<StatusEffect>
    val block: IRegistry<Block>
    val enchantment: IRegistry<Enchantment>
    val entityType: IRegistry<EntityType<*>>
    val item: IRegistry<Item>
    val potion: IRegistry<Potion>
    val carver: IRegistry<Carver<*>>
    val surfaceBuilder: IRegistry<SurfaceBuilder<*>>
    val feature: IRegistry<Feature<*>>
    val decorator: IRegistry<Decorator<*>>
    val biome: IRegistry<Biome>
    val blockStateProviderType: IRegistry<BlockStateProviderType<*>>
    val blockPlacerType: IRegistry<BlockPlacerType<*>>
    val foliagePlacerType: IRegistry<FoliagePlacerType<*>>
    val trunkPlacerType: IRegistry<TrunkPlacerType<*>>
    val treeDecoratorType: IRegistry<TreeDecoratorType<*>>
    val featureSizeType: IRegistry<FeatureSizeType<*>>
    val particleType: IRegistry<ParticleType<*>>
    val biomeSource: IRegistry<Codec<out BiomeSource>>
    val chunkGenerator: IRegistry<Codec<out ChunkGenerator>>
    val blockEntityType: IRegistry<BlockEntityType<*>>
    val paintingMotive: IRegistry<PaintingMotive>
    val customStat: IRegistry<Identifier>
    val chunkStatus: IRegistry<ChunkStatus>
    val structureFeature: IRegistry<StructureFeature<*>>
    val structurePiece: IRegistry<StructurePieceType>
    val ruleTest: IRegistry<RuleTestType<*>>
    val posRuleTest: IRegistry<PosRuleTestType<*>>
    val structureProcessor: IRegistry<StructureProcessorType<*>>
    val structurePoolElement: IRegistry<StructurePoolElementType<*>>
    val screenHandler: IRegistry<ScreenHandlerType<*>>
    val recipeType: IRegistry<RecipeType<*>>
    val recipeSerializer: IRegistry<RecipeSerializer<*>>
    val attribute: IRegistry<EntityAttribute>
    val statType: IRegistry<StatType<*>>
    val villagerType: IRegistry<VillagerType>
    val villagerProfession: IRegistry<VillagerProfession>
    val pointOfInterestType: IRegistry<PointOfInterestType>
    val memoryModuleType: IRegistry<MemoryModuleType<*>>
    val sensorType: IRegistry<SensorType<*>>
    val schedule: IRegistry<Schedule>
    val activity: IRegistry<Activity>
    val lootPoolEntryType: IRegistry<LootPoolEntryType>
    val lootFunctionType: IRegistry<LootFunctionType>
    val lootConditionType: IRegistry<LootConditionType>

    val material: IRegistry<Material>
}


interface IRegistry<T> {

    /**
     * Get a value from the registry by its id
     */
    operator fun get(id: Identifier): T?
}

/**
 * Easy registry accessors
 */

fun Identifier.toSoundEvent() = API.registries().soundEvent[this]
fun Identifier.toFluid() = API.registries().fluid[this]
fun Identifier.toStatusEffect() = API.registries().statusEffect[this]
fun Identifier.toBlock() = API.registries().block[this]
fun Identifier.toEnchantment() = API.registries().enchantment[this]
fun Identifier.toEntityType() = API.registries().entityType[this]
fun Identifier.toItem() = API.registries().item[this]
fun Identifier.toPotion() = API.registries().potion[this]
fun Identifier.toCarver() = API.registries().carver[this]
fun Identifier.toSurfaceBuilder() = API.registries().surfaceBuilder[this]
fun Identifier.toFeature() = API.registries().feature[this]
fun Identifier.toDecorator() = API.registries().decorator[this]
fun Identifier.toBiome() = API.registries().biome[this]
fun Identifier.toBlockStateProviderType() = API.registries().blockStateProviderType[this]
fun Identifier.toBlockPlacerType() = API.registries().blockPlacerType[this]
fun Identifier.toFoliagePlacerType() = API.registries().foliagePlacerType[this]
fun Identifier.toTrunkPlacerType() = API.registries().trunkPlacerType[this]
fun Identifier.toTreeDecoratorType() = API.registries().treeDecoratorType[this]
fun Identifier.toFeatureSizeType() = API.registries().featureSizeType[this]
fun Identifier.toParticleType() = API.registries().particleType[this]
fun Identifier.toBiomeSource() = API.registries().biomeSource[this]
fun Identifier.toChunkGenerator() = API.registries().chunkGenerator[this]
fun Identifier.toBlockEntityType() = API.registries().blockEntityType[this]
fun Identifier.toPaintingMotive() = API.registries().paintingMotive[this]
fun Identifier.toCustomStat() = API.registries().customStat[this]
fun Identifier.toChunkStatus() = API.registries().chunkStatus[this]
fun Identifier.toStructureFeature() = API.registries().structureFeature[this]
fun Identifier.toStructurePiece() = API.registries().structurePiece[this]
fun Identifier.toRuleTest() = API.registries().ruleTest[this]
fun Identifier.toPosRuleTest() = API.registries().posRuleTest[this]
fun Identifier.toStructureProcessor() = API.registries().structureProcessor[this]
fun Identifier.toStructurePoolElement() = API.registries().structurePoolElement[this]
fun Identifier.toScreenHandler() = API.registries().screenHandler[this]
fun Identifier.toRecipeType() = API.registries().recipeType[this]
fun Identifier.toRecipeSerializer() = API.registries().recipeSerializer[this]
fun Identifier.toAttribute() = API.registries().attribute[this]
fun Identifier.toStatType() = API.registries().statType[this]
fun Identifier.toVillagerType() = API.registries().villagerType[this]
fun Identifier.toVillagerProfession() = API.registries().villagerProfession[this]
fun Identifier.toPointOfInterestType() = API.registries().pointOfInterestType[this]
fun Identifier.toMemoryModuleType() = API.registries().memoryModuleType[this]
fun Identifier.toSensorType() = API.registries().sensorType[this]
fun Identifier.toSchedule() = API.registries().schedule[this]
fun Identifier.toActivity() = API.registries().activity[this]
fun Identifier.toLootPoolEntryType() = API.registries().lootPoolEntryType[this]
fun Identifier.toLootFunctionType() = API.registries().lootFunctionType[this]
fun Identifier.toLootConditionType() = API.registries().lootConditionType[this]