package net.minecraft.util.datafix;

import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.fixes.RenamedCoralFix;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.RenamedCoralFansFix;
import net.minecraft.util.datafix.fixes.DyeItemRenameFix;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.datafix.fixes.ChunkLightRemoveFix;
import net.minecraft.util.datafix.fixes.ZombieVillagerRebuildXpFix;
import net.minecraft.util.datafix.fixes.VillagerRebuildLevelAndXpFix;
import net.minecraft.util.datafix.fixes.ReorganizePoi;
import net.minecraft.util.datafix.fixes.OptionsAddTextBackgroundFix;
import net.minecraft.util.datafix.schemas.V1931;
import net.minecraft.util.datafix.schemas.V1929;
import net.minecraft.util.datafix.fixes.EntityRavagerRenameFix;
import net.minecraft.util.datafix.schemas.V1928;
import net.minecraft.util.datafix.fixes.MapIdFix;
import net.minecraft.util.datafix.fixes.NewVillageFix;
import net.minecraft.util.datafix.schemas.V1920;
import net.minecraft.util.datafix.fixes.VillagerDataFix;
import net.minecraft.util.datafix.fixes.CatTypeFix;
import net.minecraft.util.datafix.fixes.ChunkStatusFix2;
import net.minecraft.util.datafix.schemas.V1909;
import net.minecraft.util.datafix.schemas.V1906;
import net.minecraft.util.datafix.fixes.ChunkStatusFix;
import net.minecraft.util.datafix.fixes.EntityCatSplitFix;
import net.minecraft.util.datafix.schemas.V1904;
import net.minecraft.util.datafix.fixes.ItemLoreFix;
import net.minecraft.util.datafix.schemas.V1801;
import net.minecraft.util.datafix.schemas.V1800;
import net.minecraft.util.datafix.fixes.TrappedChestBlockEntityFix;
import net.minecraft.util.datafix.fixes.ObjectiveRenderTypeFix;
import net.minecraft.util.datafix.fixes.TeamDisplayNameFix;
import net.minecraft.util.datafix.fixes.ObjectiveDisplayNameFix;
import net.minecraft.util.datafix.fixes.SwimStatsRenameFix;
import net.minecraft.util.datafix.fixes.EntityTheRenameningFix;
import net.minecraft.util.datafix.fixes.RecipesRenameningFix;
import net.minecraft.util.datafix.schemas.V1510;
import net.minecraft.util.datafix.fixes.BiomeFix;
import net.minecraft.util.datafix.fixes.LevelDataGeneratorOptionsFix;
import net.minecraft.util.datafix.fixes.RecipesFix;
import net.minecraft.util.datafix.fixes.AdvancementsFix;
import net.minecraft.util.datafix.fixes.BlockEntityKeepPacked;
import net.minecraft.util.datafix.fixes.LeavesFix;
import net.minecraft.util.datafix.fixes.ItemStackEnchantmentNamesFix;
import net.minecraft.util.datafix.fixes.ChunkStructuresTemplateRenameFix;
import net.minecraft.util.datafix.fixes.IglooMetadataRemovalFix;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.EntityCodSalmonFix;
import net.minecraft.util.datafix.schemas.V1486;
import net.minecraft.util.datafix.fixes.HeightmapRenamingFix;
import net.minecraft.util.datafix.fixes.EntityPufferfishRenameFix;
import net.minecraft.util.datafix.schemas.V1483;
import net.minecraft.util.datafix.schemas.V1481;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.ColorlessShulkerEntityFix;
import net.minecraft.util.datafix.schemas.V1470;
import net.minecraft.util.datafix.fixes.ChunkToProtochunkFix;
import net.minecraft.util.datafix.schemas.V1466;
import net.minecraft.util.datafix.fixes.EntityPaintingMotiveFix;
import net.minecraft.util.datafix.schemas.V1460;
import net.minecraft.util.datafix.fixes.BlockEntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.ItemCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.EntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.EntityItemFrameDirectionFix;
import net.minecraft.util.datafix.fixes.VillagerTradeFix;
import net.minecraft.util.datafix.fixes.SavedDataVillageCropFix;
import net.minecraft.util.datafix.schemas.V1451_7;
import net.minecraft.util.datafix.fixes.BlockEntityJukeboxFix;
import net.minecraft.util.datafix.fixes.StatsCounterFix;
import net.minecraft.util.datafix.schemas.V1451_6;
import net.minecraft.util.datafix.fixes.LevelFlatGeneratorInfoFix;
import net.minecraft.util.datafix.fixes.BlockEntityBannerColorFix;
import net.minecraft.util.datafix.fixes.EntityWolfColorFix;
import net.minecraft.util.datafix.fixes.ItemStackSpawnEggFix;
import net.minecraft.util.datafix.schemas.V1451_5;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.util.datafix.fixes.BlockNameFlatteningFix;
import net.minecraft.util.datafix.schemas.V1451_4;
import net.minecraft.util.datafix.fixes.ItemStackMapIdFix;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.schemas.V1451_3;
import net.minecraft.util.datafix.fixes.BlockEntityBlockStateFix;
import net.minecraft.util.datafix.schemas.V1451_2;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix;
import net.minecraft.util.datafix.schemas.V1451_1;
import net.minecraft.util.datafix.schemas.V1451;
import net.minecraft.util.datafix.fixes.BlockStateStructureTemplateFix;
import net.minecraft.util.datafix.fixes.OptionsKeyTranslationFix;
import net.minecraft.util.datafix.fixes.OptionsKeyLwjgl3Fix;
import net.minecraft.util.datafix.fixes.BedItemColorFix;
import net.minecraft.util.datafix.fixes.BedBlockEntityInjecter;
import net.minecraft.util.datafix.schemas.V1125;
import net.minecraft.util.datafix.fixes.WriteAndReadFix;
import net.minecraft.util.datafix.schemas.V1022;
import net.minecraft.util.datafix.fixes.OptionsLowerCaseLanguageFix;
import net.minecraft.util.datafix.fixes.BlockEntityShulkerBoxColorFix;
import net.minecraft.util.datafix.fixes.ItemShulkerBoxColorFix;
import net.minecraft.util.datafix.fixes.EntityShulkerColorFix;
import net.minecraft.util.datafix.schemas.V808;
import net.minecraft.util.datafix.fixes.ItemWaterPotionFix;
import net.minecraft.util.datafix.fixes.ItemBannerColorFix;
import net.minecraft.util.datafix.fixes.EntityIdFix;
import net.minecraft.util.datafix.schemas.V705;
import net.minecraft.util.datafix.fixes.BlockEntityIdFix;
import net.minecraft.util.datafix.schemas.V704;
import net.minecraft.util.datafix.fixes.EntityHorseSplitFix;
import net.minecraft.util.datafix.schemas.V703;
import net.minecraft.util.datafix.fixes.EntityZombieSplitFix;
import net.minecraft.util.datafix.schemas.V702;
import net.minecraft.util.datafix.fixes.EntitySkeletonSplitFix;
import net.minecraft.util.datafix.schemas.V701;
import net.minecraft.util.datafix.fixes.EntityElderGuardianSplitFix;
import net.minecraft.util.datafix.schemas.V700;
import net.minecraft.util.datafix.fixes.OptionsForceVBOFix;
import net.minecraft.util.datafix.fixes.EntityZombieVillagerTypeFix;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V501;
import net.minecraft.util.datafix.fixes.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.util.datafix.fixes.EntityArmorStandSilentFix;
import net.minecraft.util.datafix.fixes.EntityTippedArrowFix;
import net.minecraft.util.datafix.schemas.V143;
import net.minecraft.util.datafix.fixes.EntityRidingToPassengersFix;
import net.minecraft.util.datafix.schemas.V135;
import net.minecraft.util.datafix.fixes.EntityRedundantChanceTagsFix;
import net.minecraft.util.datafix.fixes.EntityPaintingItemFrameDirectionFix;
import net.minecraft.util.datafix.fixes.EntityHorseSaddleFix;
import net.minecraft.util.datafix.fixes.EntityHealthFix;
import net.minecraft.util.datafix.fixes.EntityStringUuidFix;
import net.minecraft.util.datafix.fixes.EntityMinecartIdentifiersFix;
import net.minecraft.util.datafix.schemas.V107;
import net.minecraft.util.datafix.fixes.MobSpawnerEntityIdentifiersFix;
import net.minecraft.util.datafix.schemas.V106;
import net.minecraft.util.datafix.fixes.ItemSpawnEggFix;
import net.minecraft.util.datafix.fixes.ItemPotionFix;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.schemas.V102;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import com.mojang.datafixers.DataFix;
import net.minecraft.util.datafix.fixes.EntityEquipmentToArmorAndHandFix;
import net.minecraft.util.datafix.schemas.V100;
import net.minecraft.util.datafix.schemas.V99;
import net.minecraft.Util;
import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.SharedConstants;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.BiFunction;

public class DataFixers {
    private static final BiFunction<Integer, Schema, Schema> SAME;
    private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED;
    private static final DataFixer DATA_FIXER;
    
    private static DataFixer createFixerUpper() {
        final DataFixerBuilder dataFixerBuilder1 = new DataFixerBuilder(SharedConstants.getCurrentVersion().getWorldVersion());
        addFixers(dataFixerBuilder1);
        return dataFixerBuilder1.build(Util.backgroundExecutor());
    }
    
    public static DataFixer getDataFixer() {
        return DataFixers.DATA_FIXER;
    }
    
    private static void addFixers(final DataFixerBuilder dataFixerBuilder) {
        final Schema schema2 = dataFixerBuilder.addSchema(99, V99::new);
        final Schema schema3 = dataFixerBuilder.addSchema(100, V100::new);
        dataFixerBuilder.addFixer((DataFix)new EntityEquipmentToArmorAndHandFix(schema3, true));
        final Schema schema4 = dataFixerBuilder.addSchema(101, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new BlockEntitySignTextStrictJsonFix(schema4, false));
        final Schema schema5 = dataFixerBuilder.addSchema(102, V102::new);
        dataFixerBuilder.addFixer((DataFix)new ItemIdFix(schema5, true));
        dataFixerBuilder.addFixer((DataFix)new ItemPotionFix(schema5, false));
        final Schema schema6 = dataFixerBuilder.addSchema(105, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new ItemSpawnEggFix(schema6, true));
        final Schema schema7 = dataFixerBuilder.addSchema(106, V106::new);
        dataFixerBuilder.addFixer((DataFix)new MobSpawnerEntityIdentifiersFix(schema7, true));
        final Schema schema8 = dataFixerBuilder.addSchema(107, V107::new);
        dataFixerBuilder.addFixer((DataFix)new EntityMinecartIdentifiersFix(schema8, true));
        final Schema schema9 = dataFixerBuilder.addSchema(108, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new EntityStringUuidFix(schema9, true));
        final Schema schema10 = dataFixerBuilder.addSchema(109, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new EntityHealthFix(schema10, true));
        final Schema schema11 = dataFixerBuilder.addSchema(110, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new EntityHorseSaddleFix(schema11, true));
        final Schema schema12 = dataFixerBuilder.addSchema(111, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new EntityPaintingItemFrameDirectionFix(schema12, true));
        final Schema schema13 = dataFixerBuilder.addSchema(113, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new EntityRedundantChanceTagsFix(schema13, true));
        final Schema schema14 = dataFixerBuilder.addSchema(135, V135::new);
        dataFixerBuilder.addFixer((DataFix)new EntityRidingToPassengersFix(schema14, true));
        final Schema schema15 = dataFixerBuilder.addSchema(143, V143::new);
        dataFixerBuilder.addFixer((DataFix)new EntityTippedArrowFix(schema15, true));
        final Schema schema16 = dataFixerBuilder.addSchema(147, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new EntityArmorStandSilentFix(schema16, true));
        final Schema schema17 = dataFixerBuilder.addSchema(165, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new ItemWrittenBookPagesStrictJsonFix(schema17, true));
        final Schema schema18 = dataFixerBuilder.addSchema(501, V501::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema18, "Add 1.10 entities fix", References.ENTITY));
        final Schema schema19 = dataFixerBuilder.addSchema(502, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema19, "cooked_fished item renamer", (Function<String, String>)(string -> Objects.equals(NamespacedSchema.ensureNamespaced(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string)));
        dataFixerBuilder.addFixer((DataFix)new EntityZombieVillagerTypeFix(schema19, false));
        final Schema schema20 = dataFixerBuilder.addSchema(505, (BiFunction)DataFixers.SAME);
        dataFixerBuilder.addFixer((DataFix)new OptionsForceVBOFix(schema20, false));
        final Schema schema21 = dataFixerBuilder.addSchema(700, V700::new);
        dataFixerBuilder.addFixer((DataFix)new EntityElderGuardianSplitFix(schema21, true));
        final Schema schema22 = dataFixerBuilder.addSchema(701, V701::new);
        dataFixerBuilder.addFixer((DataFix)new EntitySkeletonSplitFix(schema22, true));
        final Schema schema23 = dataFixerBuilder.addSchema(702, V702::new);
        dataFixerBuilder.addFixer((DataFix)new EntityZombieSplitFix(schema23, true));
        final Schema schema24 = dataFixerBuilder.addSchema(703, V703::new);
        dataFixerBuilder.addFixer((DataFix)new EntityHorseSplitFix(schema24, true));
        final Schema schema25 = dataFixerBuilder.addSchema(704, V704::new);
        dataFixerBuilder.addFixer((DataFix)new BlockEntityIdFix(schema25, true));
        final Schema schema26 = dataFixerBuilder.addSchema(705, V705::new);
        dataFixerBuilder.addFixer((DataFix)new EntityIdFix(schema26, true));
        final Schema schema27 = dataFixerBuilder.addSchema(804, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ItemBannerColorFix(schema27, true));
        final Schema schema28 = dataFixerBuilder.addSchema(806, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ItemWaterPotionFix(schema28, false));
        final Schema schema29 = dataFixerBuilder.addSchema(808, V808::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema29, "added shulker box", References.BLOCK_ENTITY));
        final Schema schema30 = dataFixerBuilder.addSchema(808, 1, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new EntityShulkerColorFix(schema30, false));
        final Schema schema31 = dataFixerBuilder.addSchema(813, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ItemShulkerBoxColorFix(schema31, false));
        dataFixerBuilder.addFixer((DataFix)new BlockEntityShulkerBoxColorFix(schema31, false));
        final Schema schema32 = dataFixerBuilder.addSchema(816, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new OptionsLowerCaseLanguageFix(schema32, false));
        final Schema schema33 = dataFixerBuilder.addSchema(820, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema33, "totem item renamer", (Function<String, String>)(string -> Objects.equals(string, "minecraft:totem") ? "minecraft:totem_of_undying" : string)));
        final Schema schema34 = dataFixerBuilder.addSchema(1022, V1022::new);
        dataFixerBuilder.addFixer((DataFix)new WriteAndReadFix(schema34, "added shoulder entities to players", References.PLAYER));
        final Schema schema35 = dataFixerBuilder.addSchema(1125, V1125::new);
        dataFixerBuilder.addFixer((DataFix)new BedBlockEntityInjecter(schema35, true));
        dataFixerBuilder.addFixer((DataFix)new BedItemColorFix(schema35, false));
        final Schema schema36 = dataFixerBuilder.addSchema(1344, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new OptionsKeyLwjgl3Fix(schema36, false));
        final Schema schema37 = dataFixerBuilder.addSchema(1446, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new OptionsKeyTranslationFix(schema37, false));
        final Schema schema38 = dataFixerBuilder.addSchema(1450, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new BlockStateStructureTemplateFix(schema38, false));
        final Schema schema39 = dataFixerBuilder.addSchema(1451, V1451::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema39, "AddTrappedChestFix", References.BLOCK_ENTITY));
        final Schema schema40 = dataFixerBuilder.addSchema(1451, 1, V1451_1::new);
        dataFixerBuilder.addFixer((DataFix)new ChunkPalettedStorageFix(schema40, true));
        final Schema schema41 = dataFixerBuilder.addSchema(1451, 2, V1451_2::new);
        dataFixerBuilder.addFixer((DataFix)new BlockEntityBlockStateFix(schema41, true));
        final Schema schema42 = dataFixerBuilder.addSchema(1451, 3, V1451_3::new);
        dataFixerBuilder.addFixer((DataFix)new EntityBlockStateFix(schema42, true));
        dataFixerBuilder.addFixer((DataFix)new ItemStackMapIdFix(schema42, false));
        final Schema schema43 = dataFixerBuilder.addSchema(1451, 4, V1451_4::new);
        dataFixerBuilder.addFixer((DataFix)new BlockNameFlatteningFix(schema43, true));
        dataFixerBuilder.addFixer((DataFix)new ItemStackTheFlatteningFix(schema43, false));
        final Schema schema44 = dataFixerBuilder.addSchema(1451, 5, V1451_5::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema44, "RemoveNoteBlockFlowerPotFix", References.BLOCK_ENTITY));
        dataFixerBuilder.addFixer((DataFix)new ItemStackSpawnEggFix(schema44, false));
        dataFixerBuilder.addFixer((DataFix)new EntityWolfColorFix(schema44, false));
        dataFixerBuilder.addFixer((DataFix)new BlockEntityBannerColorFix(schema44, false));
        dataFixerBuilder.addFixer((DataFix)new LevelFlatGeneratorInfoFix(schema44, false));
        final Schema schema45 = dataFixerBuilder.addSchema(1451, 6, V1451_6::new);
        dataFixerBuilder.addFixer((DataFix)new StatsCounterFix(schema45, true));
        dataFixerBuilder.addFixer((DataFix)new BlockEntityJukeboxFix(schema45, false));
        final Schema schema46 = dataFixerBuilder.addSchema(1451, 7, V1451_7::new);
        dataFixerBuilder.addFixer((DataFix)new SavedDataVillageCropFix(schema46, true));
        final Schema schema47 = dataFixerBuilder.addSchema(1451, 7, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new VillagerTradeFix(schema47, false));
        final Schema schema48 = dataFixerBuilder.addSchema(1456, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new EntityItemFrameDirectionFix(schema48, false));
        final Schema schema49 = dataFixerBuilder.addSchema(1458, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new EntityCustomNameToComponentFix(schema49, false));
        dataFixerBuilder.addFixer((DataFix)new ItemCustomNameToComponentFix(schema49, false));
        dataFixerBuilder.addFixer((DataFix)new BlockEntityCustomNameToComponentFix(schema49, false));
        final Schema schema50 = dataFixerBuilder.addSchema(1460, V1460::new);
        dataFixerBuilder.addFixer((DataFix)new EntityPaintingMotiveFix(schema50, false));
        final Schema schema51 = dataFixerBuilder.addSchema(1466, V1466::new);
        dataFixerBuilder.addFixer((DataFix)new ChunkToProtochunkFix(schema51, true));
        final Schema schema52 = dataFixerBuilder.addSchema(1470, V1470::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema52, "Add 1.13 entities fix", References.ENTITY));
        final Schema schema53 = dataFixerBuilder.addSchema(1474, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ColorlessShulkerEntityFix(schema53, false));
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema53, "Colorless shulker block fixer", (Function<String, String>)(string -> Objects.equals(NamespacedSchema.ensureNamespaced(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string)));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema53, "Colorless shulker item fixer", (Function<String, String>)(string -> Objects.equals(NamespacedSchema.ensureNamespaced(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string)));
        final Schema schema54 = dataFixerBuilder.addSchema(1475, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema54, "Flowing fixer", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(string, string))));
        final Schema schema55 = dataFixerBuilder.addSchema(1480, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema55, "Rename coral blocks", (Function<String, String>)(string -> (String)RenamedCoralFix.RENAMED_IDS.getOrDefault(string, string))));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema55, "Rename coral items", (Function<String, String>)(string -> (String)RenamedCoralFix.RENAMED_IDS.getOrDefault(string, string))));
        final Schema schema56 = dataFixerBuilder.addSchema(1481, V1481::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema56, "Add conduit", References.BLOCK_ENTITY));
        final Schema schema57 = dataFixerBuilder.addSchema(1483, V1483::new);
        dataFixerBuilder.addFixer((DataFix)new EntityPufferfishRenameFix(schema57, true));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema57, "Rename pufferfish egg item", (Function<String, String>)(string -> (String)EntityPufferfishRenameFix.RENAMED_IDS.getOrDefault(string, string))));
        final Schema schema58 = dataFixerBuilder.addSchema(1484, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema58, "Rename seagrass items", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(string, string))));
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema58, "Rename seagrass blocks", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(string, string))));
        dataFixerBuilder.addFixer((DataFix)new HeightmapRenamingFix(schema58, false));
        final Schema schema59 = dataFixerBuilder.addSchema(1486, V1486::new);
        dataFixerBuilder.addFixer((DataFix)new EntityCodSalmonFix(schema59, true));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema59, "Rename cod/salmon egg items", (Function<String, String>)(string -> (String)EntityCodSalmonFix.RENAMED_EGG_IDS.getOrDefault(string, string))));
        final Schema schema60 = dataFixerBuilder.addSchema(1487, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema60, "Rename prismarine_brick(s)_* blocks", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs").getOrDefault(string, string))));
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema60, "Rename prismarine_brick(s)_* items", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs").getOrDefault(string, string))));
        final Schema schema61 = dataFixerBuilder.addSchema(1488, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema61, "Rename kelp/kelptop", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(string, string))));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema61, "Rename kelptop", (Function<String, String>)(string -> Objects.equals(string, "minecraft:kelp_top") ? "minecraft:kelp" : string)));
        dataFixerBuilder.addFixer((DataFix)new NamedEntityFix(schema61, false, "Command block block entity custom name fix", References.BLOCK_ENTITY, "minecraft:command_block") {
            @Override
            protected Typed<?> fix(final Typed<?> typed) {
                return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixTagCustomName);
            }
        });
        dataFixerBuilder.addFixer((DataFix)new NamedEntityFix(schema61, false, "Command block minecart custom name fix", References.ENTITY, "minecraft:commandblock_minecart") {
            @Override
            protected Typed<?> fix(final Typed<?> typed) {
                return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixTagCustomName);
            }
        });
        dataFixerBuilder.addFixer((DataFix)new IglooMetadataRemovalFix(schema61, false));
        final Schema schema62 = dataFixerBuilder.addSchema(1490, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema62, "Rename melon_block", (Function<String, String>)(string -> Objects.equals(string, "minecraft:melon_block") ? "minecraft:melon" : string)));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema62, "Rename melon_block/melon/speckled_melon", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice").getOrDefault(string, string))));
        final Schema schema63 = dataFixerBuilder.addSchema(1492, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ChunkStructuresTemplateRenameFix(schema63, false));
        final Schema schema64 = dataFixerBuilder.addSchema(1494, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ItemStackEnchantmentNamesFix(schema64, false));
        final Schema schema65 = dataFixerBuilder.addSchema(1496, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new LeavesFix(schema65, false));
        final Schema schema66 = dataFixerBuilder.addSchema(1500, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new BlockEntityKeepPacked(schema66, false));
        final Schema schema67 = dataFixerBuilder.addSchema(1501, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new AdvancementsFix(schema67, false));
        final Schema schema68 = dataFixerBuilder.addSchema(1502, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new RecipesFix(schema68, false));
        final Schema schema69 = dataFixerBuilder.addSchema(1506, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new LevelDataGeneratorOptionsFix(schema69, false));
        final Schema schema70 = dataFixerBuilder.addSchema(1508, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new BiomeFix(schema70, false));
        final Schema schema71 = dataFixerBuilder.addSchema(1510, V1510::new);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema71, "Block renamening fix", (Function<String, String>)(string -> (String)EntityTheRenameningFix.RENAMED_BLOCKS.getOrDefault(string, string))));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema71, "Item renamening fix", (Function<String, String>)(string -> (String)EntityTheRenameningFix.RENAMED_ITEMS.getOrDefault(string, string))));
        dataFixerBuilder.addFixer((DataFix)new RecipesRenameningFix(schema71, false));
        dataFixerBuilder.addFixer((DataFix)new EntityTheRenameningFix(schema71, true));
        dataFixerBuilder.addFixer((DataFix)new SwimStatsRenameFix(schema71, false));
        final Schema schema72 = dataFixerBuilder.addSchema(1514, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ObjectiveDisplayNameFix(schema72, false));
        dataFixerBuilder.addFixer((DataFix)new TeamDisplayNameFix(schema72, false));
        dataFixerBuilder.addFixer((DataFix)new ObjectiveRenderTypeFix(schema72, false));
        final Schema schema73 = dataFixerBuilder.addSchema(1515, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema73, "Rename coral fan blocks", (Function<String, String>)(string -> (String)RenamedCoralFansFix.RENAMED_IDS.getOrDefault(string, string))));
        final Schema schema74 = dataFixerBuilder.addSchema(1624, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new TrappedChestBlockEntityFix(schema74, false));
        final Schema schema75 = dataFixerBuilder.addSchema(1800, V1800::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema75, "Added 1.14 mobs fix", References.ENTITY));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema75, "Rename dye items", (Function<String, String>)(string -> (String)DyeItemRenameFix.RENAMED_IDS.getOrDefault(string, string))));
        final Schema schema76 = dataFixerBuilder.addSchema(1801, V1801::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema76, "Added Illager Beast", References.ENTITY));
        final Schema schema77 = dataFixerBuilder.addSchema(1802, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer(BlockRenameFix.create(schema77, "Rename sign blocks & stone slabs", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign").getOrDefault(string, string))));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema77, "Rename sign item & stone slabs", (Function<String, String>)(string -> (String)ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign").getOrDefault(string, string))));
        final Schema schema78 = dataFixerBuilder.addSchema(1803, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ItemLoreFix(schema78, false));
        final Schema schema79 = dataFixerBuilder.addSchema(1904, V1904::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema79, "Added Cats", References.ENTITY));
        dataFixerBuilder.addFixer((DataFix)new EntityCatSplitFix(schema79, false));
        final Schema schema80 = dataFixerBuilder.addSchema(1905, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ChunkStatusFix(schema80, false));
        final Schema schema81 = dataFixerBuilder.addSchema(1906, V1906::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema81, "Add POI Blocks", References.BLOCK_ENTITY));
        final Schema schema82 = dataFixerBuilder.addSchema(1909, V1909::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema82, "Add jigsaw", References.BLOCK_ENTITY));
        final Schema schema83 = dataFixerBuilder.addSchema(1911, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ChunkStatusFix2(schema83, false));
        final Schema schema84 = dataFixerBuilder.addSchema(1917, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new CatTypeFix(schema84, false));
        final Schema schema85 = dataFixerBuilder.addSchema(1918, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new VillagerDataFix(schema85, "minecraft:villager"));
        dataFixerBuilder.addFixer((DataFix)new VillagerDataFix(schema85, "minecraft:zombie_villager"));
        final Schema schema86 = dataFixerBuilder.addSchema(1920, V1920::new);
        dataFixerBuilder.addFixer((DataFix)new NewVillageFix(schema86, false));
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema86, "Add campfire", References.BLOCK_ENTITY));
        final Schema schema87 = dataFixerBuilder.addSchema(1925, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new MapIdFix(schema87, false));
        final Schema schema88 = dataFixerBuilder.addSchema(1928, V1928::new);
        dataFixerBuilder.addFixer((DataFix)new EntityRavagerRenameFix(schema88, true));
        dataFixerBuilder.addFixer(ItemRenameFix.create(schema88, "Rename ravager egg item", (Function<String, String>)(string -> (String)EntityRavagerRenameFix.RENAMED_IDS.getOrDefault(string, string))));
        final Schema schema89 = dataFixerBuilder.addSchema(1929, V1929::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema89, "Add Wandering Trader and Trader Llama", References.ENTITY));
        final Schema schema90 = dataFixerBuilder.addSchema(1931, V1931::new);
        dataFixerBuilder.addFixer((DataFix)new AddNewChoices(schema90, "Added Fox", References.ENTITY));
        final Schema schema91 = dataFixerBuilder.addSchema(1936, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new OptionsAddTextBackgroundFix(schema91, false));
        final Schema schema92 = dataFixerBuilder.addSchema(1946, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ReorganizePoi(schema92, false));
        final Schema schema93 = dataFixerBuilder.addSchema(1948, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new OminousBannerRenameFix(schema93, false));
        final Schema schema94 = dataFixerBuilder.addSchema(1953, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new OminousBannerBlockEntityRenameFix(schema94, false));
        final Schema schema95 = dataFixerBuilder.addSchema(1955, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new VillagerRebuildLevelAndXpFix(schema95, false));
        dataFixerBuilder.addFixer((DataFix)new ZombieVillagerRebuildXpFix(schema95, false));
        final Schema schema96 = dataFixerBuilder.addSchema(1961, (BiFunction)DataFixers.SAME_NAMESPACED);
        dataFixerBuilder.addFixer((DataFix)new ChunkLightRemoveFix(schema96, false));
    }
    
    static {
        SAME = Schema::new;
        SAME_NAMESPACED = NamespacedSchema::new;
        DATA_FIXER = createFixerUpper();
    }
}
