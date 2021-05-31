package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.feature.structures.FeaturePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.tags.BlockTags;

public class DesertVillagePools {
    public static void bootstrap() {
    }
    
    static {
        final ImmutableList<StructureProcessor> immutableList1 = (ImmutableList<StructureProcessor>)ImmutableList.of(new RuleProcessor((List<ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SMOOTH_SANDSTONE, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.CUT_SANDSTONE, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.TERRACOTTA, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SMOOTH_SANDSTONE_STAIRS, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SMOOTH_SANDSTONE_SLAB, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState()))));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/town_centers"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/town_centers/desert_meeting_point_1"), (Object)98), new Pair((Object)new SinglePoolElement("village/desert/town_centers/desert_meeting_point_2"), (Object)98), new Pair((Object)new SinglePoolElement("village/desert/town_centers/desert_meeting_point_3"), (Object)49), new Pair((Object)new SinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_1", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_2", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_3", (List<StructureProcessor>)immutableList1), (Object)1)), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/streets"), new ResourceLocation("village/desert/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/streets/corner_01"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/corner_02"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/straight_01"), (Object)4), new Pair((Object)new SinglePoolElement("village/desert/streets/straight_02"), (Object)4), new Pair((Object)new SinglePoolElement("village/desert/streets/straight_03"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/crossroad_01"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/crossroad_02"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/crossroad_03"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/square_01"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/square_02"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/streets/turn_01"), (Object)3)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/zombie/streets"), new ResourceLocation("village/desert/zombie/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/corner_01"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/corner_02"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/straight_01"), (Object)4), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/straight_02"), (Object)4), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/straight_03"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/crossroad_01"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/crossroad_02"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/crossroad_03"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/square_01"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/square_02"), (Object)3), new Pair((Object)new SinglePoolElement("village/desert/zombie/streets/turn_01"), (Object)3)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        final ImmutableList<StructureProcessor> immutableList2 = (ImmutableList<StructureProcessor>)ImmutableList.of(new RuleProcessor((List<ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState()))));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/houses"), new ResourceLocation("village/desert/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_1"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_2"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_3"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_4"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_5"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_6"), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_7"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_small_house_8"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_medium_house_1"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_medium_house_2"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_butcher_shop_1"), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_tool_smith_1"), (Object)2), (Object[])new Pair[] { new Pair(new SinglePoolElement("village/desert/houses/desert_fletcher_house_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_shepherd_house_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_armorer_1"), 1), new Pair(new SinglePoolElement("village/desert/houses/desert_fisher_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_tannery_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_cartographer_house_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_library_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_mason_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_weaponsmith_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_temple_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_temple_2"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_large_farm_1", (List<StructureProcessor>)immutableList2), 11), new Pair(new SinglePoolElement("village/desert/houses/desert_farm_1", (List<StructureProcessor>)immutableList2), 4), new Pair(new SinglePoolElement("village/desert/houses/desert_farm_2", (List<StructureProcessor>)immutableList2), 4), new Pair(new SinglePoolElement("village/desert/houses/desert_animal_pen_1"), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_animal_pen_2"), 2), Pair.of(EmptyPoolElement.INSTANCE, 5) }), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/zombie/houses"), new ResourceLocation("village/desert/zombie/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_1", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_2", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_3", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_4", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_5", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_6", (List<StructureProcessor>)immutableList1), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_7", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_small_house_8", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_medium_house_1", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/zombie/houses/desert_medium_house_2", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_butcher_shop_1", (List<StructureProcessor>)immutableList1), (Object)2), new Pair((Object)new SinglePoolElement("village/desert/houses/desert_tool_smith_1", (List<StructureProcessor>)immutableList1), (Object)2), (Object[])new Pair[] { new Pair(new SinglePoolElement("village/desert/houses/desert_fletcher_house_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_shepherd_house_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_armorer_1", (List<StructureProcessor>)immutableList1), 1), new Pair(new SinglePoolElement("village/desert/houses/desert_fisher_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_tannery_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_cartographer_house_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_library_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_mason_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_weaponsmith_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_temple_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_temple_2", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_large_farm_1", (List<StructureProcessor>)immutableList1), 7), new Pair(new SinglePoolElement("village/desert/houses/desert_farm_1", (List<StructureProcessor>)immutableList1), 4), new Pair(new SinglePoolElement("village/desert/houses/desert_farm_2", (List<StructureProcessor>)immutableList1), 4), new Pair(new SinglePoolElement("village/desert/houses/desert_animal_pen_1", (List<StructureProcessor>)immutableList1), 2), new Pair(new SinglePoolElement("village/desert/houses/desert_animal_pen_2", (List<StructureProcessor>)immutableList1), 2), Pair.of(EmptyPoolElement.INSTANCE, 5) }), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/terminators"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/terminators/terminator_01"), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/terminators/terminator_02"), (Object)1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/zombie/terminators"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/terminators/terminator_01"), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/zombie/terminators/terminator_02"), (Object)1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/decor"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/desert_lamp_1"), (Object)10), new Pair((Object)new FeaturePoolElement(new ConfiguredFeature<>(Feature.CACTUS, FeatureConfiguration.NONE)), (Object)4), new Pair((Object)new FeaturePoolElement(new ConfiguredFeature<>(Feature.HAY_PILE, FeatureConfiguration.NONE)), (Object)4), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)10)), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/zombie/decor"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/desert_lamp_1", (List<StructureProcessor>)immutableList1), (Object)10), new Pair((Object)new FeaturePoolElement(new ConfiguredFeature<>(Feature.CACTUS, FeatureConfiguration.NONE)), (Object)4), new Pair((Object)new FeaturePoolElement(new ConfiguredFeature<>(Feature.HAY_PILE, FeatureConfiguration.NONE)), (Object)4), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)10)), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/villagers"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/villagers/nitwit"), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/villagers/baby"), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/villagers/unemployed"), (Object)10)), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("village/desert/zombie/villagers"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(new Pair((Object)new SinglePoolElement("village/desert/zombie/villagers/nitwit"), (Object)1), new Pair((Object)new SinglePoolElement("village/desert/zombie/villagers/unemployed"), (Object)10)), StructureTemplatePool.Projection.RIGID));
    }
}
