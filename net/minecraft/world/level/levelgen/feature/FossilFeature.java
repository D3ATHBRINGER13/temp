package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public class FossilFeature extends Feature<NoneFeatureConfiguration> {
    private static final ResourceLocation SPINE_1;
    private static final ResourceLocation SPINE_2;
    private static final ResourceLocation SPINE_3;
    private static final ResourceLocation SPINE_4;
    private static final ResourceLocation SPINE_1_COAL;
    private static final ResourceLocation SPINE_2_COAL;
    private static final ResourceLocation SPINE_3_COAL;
    private static final ResourceLocation SPINE_4_COAL;
    private static final ResourceLocation SKULL_1;
    private static final ResourceLocation SKULL_2;
    private static final ResourceLocation SKULL_3;
    private static final ResourceLocation SKULL_4;
    private static final ResourceLocation SKULL_1_COAL;
    private static final ResourceLocation SKULL_2_COAL;
    private static final ResourceLocation SKULL_3_COAL;
    private static final ResourceLocation SKULL_4_COAL;
    private static final ResourceLocation[] fossils;
    private static final ResourceLocation[] fossilsCoal;
    
    public FossilFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final Random random2 = bhs.getRandom();
        final Rotation[] arr8 = Rotation.values();
        final Rotation brg9 = arr8[random2.nextInt(arr8.length)];
        final int integer10 = random2.nextInt(FossilFeature.fossils.length);
        final StructureManager cjp11 = ((ServerLevel)bhs.getLevel()).getLevelStorage().getStructureManager();
        final StructureTemplate cjt12 = cjp11.getOrCreate(FossilFeature.fossils[integer10]);
        final StructureTemplate cjt13 = cjp11.getOrCreate(FossilFeature.fossilsCoal[integer10]);
        final ChunkPos bhd14 = new ChunkPos(ew);
        final BoundingBox cic15 = new BoundingBox(bhd14.getMinBlockX(), 0, bhd14.getMinBlockZ(), bhd14.getMaxBlockX(), 256, bhd14.getMaxBlockZ());
        final StructurePlaceSettings cjq16 = new StructurePlaceSettings().setRotation(brg9).setBoundingBox(cic15).setRandom(random2).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        final BlockPos ew2 = cjt12.getSize(brg9);
        final int integer11 = random2.nextInt(16 - ew2.getX());
        final int integer12 = random2.nextInt(16 - ew2.getZ());
        int integer13 = 256;
        for (int integer14 = 0; integer14 < ew2.getX(); ++integer14) {
            for (int integer15 = 0; integer15 < ew2.getZ(); ++integer15) {
                integer13 = Math.min(integer13, bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ew.getX() + integer14 + integer11, ew.getZ() + integer15 + integer12));
            }
        }
        int integer14 = Math.max(integer13 - 15 - random2.nextInt(10), 10);
        final BlockPos ew3 = cjt12.getZeroPositionWithTransform(ew.offset(integer11, integer14, integer12), Mirror.NONE, brg9);
        final BlockRotProcessor cje23 = new BlockRotProcessor(0.9f);
        cjq16.clearProcessors().addProcessor(cje23);
        cjt12.placeInWorld(bhs, ew3, cjq16, 4);
        cjq16.popProcessor(cje23);
        final BlockRotProcessor cje24 = new BlockRotProcessor(0.1f);
        cjq16.clearProcessors().addProcessor(cje24);
        cjt13.placeInWorld(bhs, ew3, cjq16, 4);
        return true;
    }
    
    static {
        SPINE_1 = new ResourceLocation("fossil/spine_1");
        SPINE_2 = new ResourceLocation("fossil/spine_2");
        SPINE_3 = new ResourceLocation("fossil/spine_3");
        SPINE_4 = new ResourceLocation("fossil/spine_4");
        SPINE_1_COAL = new ResourceLocation("fossil/spine_1_coal");
        SPINE_2_COAL = new ResourceLocation("fossil/spine_2_coal");
        SPINE_3_COAL = new ResourceLocation("fossil/spine_3_coal");
        SPINE_4_COAL = new ResourceLocation("fossil/spine_4_coal");
        SKULL_1 = new ResourceLocation("fossil/skull_1");
        SKULL_2 = new ResourceLocation("fossil/skull_2");
        SKULL_3 = new ResourceLocation("fossil/skull_3");
        SKULL_4 = new ResourceLocation("fossil/skull_4");
        SKULL_1_COAL = new ResourceLocation("fossil/skull_1_coal");
        SKULL_2_COAL = new ResourceLocation("fossil/skull_2_coal");
        SKULL_3_COAL = new ResourceLocation("fossil/skull_3_coal");
        SKULL_4_COAL = new ResourceLocation("fossil/skull_4_coal");
        fossils = new ResourceLocation[] { FossilFeature.SPINE_1, FossilFeature.SPINE_2, FossilFeature.SPINE_3, FossilFeature.SPINE_4, FossilFeature.SKULL_1, FossilFeature.SKULL_2, FossilFeature.SKULL_3, FossilFeature.SKULL_4 };
        fossilsCoal = new ResourceLocation[] { FossilFeature.SPINE_1_COAL, FossilFeature.SPINE_2_COAL, FossilFeature.SPINE_3_COAL, FossilFeature.SPINE_4_COAL, FossilFeature.SKULL_1_COAL, FossilFeature.SKULL_2_COAL, FossilFeature.SKULL_3_COAL, FossilFeature.SKULL_4_COAL };
    }
}
