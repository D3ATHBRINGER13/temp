package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.material.FluidState;
import java.util.Iterator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import com.google.common.collect.Lists;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import java.util.List;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;

public class OceanRuinPieces {
    private static final ResourceLocation[] WARM_RUINS;
    private static final ResourceLocation[] RUINS_BRICK;
    private static final ResourceLocation[] RUINS_CRACKED;
    private static final ResourceLocation[] RUINS_MOSSY;
    private static final ResourceLocation[] BIG_RUINS_BRICK;
    private static final ResourceLocation[] BIG_RUINS_MOSSY;
    private static final ResourceLocation[] BIG_RUINS_CRACKED;
    private static final ResourceLocation[] BIG_WARM_RUINS;
    
    private static ResourceLocation getSmallWarmRuin(final Random random) {
        return OceanRuinPieces.WARM_RUINS[random.nextInt(OceanRuinPieces.WARM_RUINS.length)];
    }
    
    private static ResourceLocation getBigWarmRuin(final Random random) {
        return OceanRuinPieces.BIG_WARM_RUINS[random.nextInt(OceanRuinPieces.BIG_WARM_RUINS.length)];
    }
    
    public static void addPieces(final StructureManager cjp, final BlockPos ew, final Rotation brg, final List<StructurePiece> list, final Random random, final OceanRuinConfiguration cdf) {
        final boolean boolean7 = random.nextFloat() <= cdf.largeProbability;
        final float float8 = boolean7 ? 0.9f : 0.8f;
        addPiece(cjp, ew, brg, list, random, cdf, boolean7, float8);
        if (boolean7 && random.nextFloat() <= cdf.clusterProbability) {
            addClusterRuins(cjp, random, brg, ew, cdf, list);
        }
    }
    
    private static void addClusterRuins(final StructureManager cjp, final Random random, final Rotation brg, final BlockPos ew, final OceanRuinConfiguration cdf, final List<StructurePiece> list) {
        final int integer7 = ew.getX();
        final int integer8 = ew.getZ();
        final BlockPos ew2 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, brg, BlockPos.ZERO).offset(integer7, 0, integer8);
        final BoundingBox cic10 = BoundingBox.createProper(integer7, 0, integer8, ew2.getX(), 0, ew2.getZ());
        final BlockPos ew3 = new BlockPos(Math.min(integer7, ew2.getX()), 0, Math.min(integer8, ew2.getZ()));
        final List<BlockPos> list2 = allPositions(random, ew3.getX(), ew3.getZ());
        for (int integer9 = Mth.nextInt(random, 4, 8), integer10 = 0; integer10 < integer9; ++integer10) {
            if (!list2.isEmpty()) {
                final int integer11 = random.nextInt(list2.size());
                final BlockPos ew4 = (BlockPos)list2.remove(integer11);
                final int integer12 = ew4.getX();
                final int integer13 = ew4.getZ();
                final Rotation brg2 = Rotation.values()[random.nextInt(Rotation.values().length)];
                final BlockPos ew5 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, brg2, BlockPos.ZERO).offset(integer12, 0, integer13);
                final BoundingBox cic11 = BoundingBox.createProper(integer12, 0, integer13, ew5.getX(), 0, ew5.getZ());
                if (!cic11.intersects(cic10)) {
                    addPiece(cjp, ew4, brg2, list, random, cdf, false, 0.8f);
                }
            }
        }
    }
    
    private static List<BlockPos> allPositions(final Random random, final int integer2, final int integer3) {
        final List<BlockPos> list4 = (List<BlockPos>)Lists.newArrayList();
        list4.add(new BlockPos(integer2 - 16 + Mth.nextInt(random, 1, 8), 90, integer3 + 16 + Mth.nextInt(random, 1, 7)));
        list4.add(new BlockPos(integer2 - 16 + Mth.nextInt(random, 1, 8), 90, integer3 + Mth.nextInt(random, 1, 7)));
        list4.add(new BlockPos(integer2 - 16 + Mth.nextInt(random, 1, 8), 90, integer3 - 16 + Mth.nextInt(random, 4, 8)));
        list4.add(new BlockPos(integer2 + Mth.nextInt(random, 1, 7), 90, integer3 + 16 + Mth.nextInt(random, 1, 7)));
        list4.add(new BlockPos(integer2 + Mth.nextInt(random, 1, 7), 90, integer3 - 16 + Mth.nextInt(random, 4, 6)));
        list4.add(new BlockPos(integer2 + 16 + Mth.nextInt(random, 1, 7), 90, integer3 + 16 + Mth.nextInt(random, 3, 8)));
        list4.add(new BlockPos(integer2 + 16 + Mth.nextInt(random, 1, 7), 90, integer3 + Mth.nextInt(random, 1, 7)));
        list4.add(new BlockPos(integer2 + 16 + Mth.nextInt(random, 1, 7), 90, integer3 - 16 + Mth.nextInt(random, 4, 8)));
        return list4;
    }
    
    private static void addPiece(final StructureManager cjp, final BlockPos ew, final Rotation brg, final List<StructurePiece> list, final Random random, final OceanRuinConfiguration cdf, final boolean boolean7, final float float8) {
        if (cdf.biomeTemp == OceanRuinFeature.Type.WARM) {
            final ResourceLocation qv9 = boolean7 ? getBigWarmRuin(random) : getSmallWarmRuin(random);
            list.add(new OceanRuinPiece(cjp, qv9, ew, brg, float8, cdf.biomeTemp, boolean7));
        }
        else if (cdf.biomeTemp == OceanRuinFeature.Type.COLD) {
            final ResourceLocation[] arr9 = boolean7 ? OceanRuinPieces.BIG_RUINS_BRICK : OceanRuinPieces.RUINS_BRICK;
            final ResourceLocation[] arr10 = boolean7 ? OceanRuinPieces.BIG_RUINS_CRACKED : OceanRuinPieces.RUINS_CRACKED;
            final ResourceLocation[] arr11 = boolean7 ? OceanRuinPieces.BIG_RUINS_MOSSY : OceanRuinPieces.RUINS_MOSSY;
            final int integer12 = random.nextInt(arr9.length);
            list.add(new OceanRuinPiece(cjp, arr9[integer12], ew, brg, float8, cdf.biomeTemp, boolean7));
            list.add(new OceanRuinPiece(cjp, arr10[integer12], ew, brg, 0.7f, cdf.biomeTemp, boolean7));
            list.add(new OceanRuinPiece(cjp, arr11[integer12], ew, brg, 0.5f, cdf.biomeTemp, boolean7));
        }
    }
    
    static {
        WARM_RUINS = new ResourceLocation[] { new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8") };
        RUINS_BRICK = new ResourceLocation[] { new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8") };
        RUINS_CRACKED = new ResourceLocation[] { new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8") };
        RUINS_MOSSY = new ResourceLocation[] { new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8") };
        BIG_RUINS_BRICK = new ResourceLocation[] { new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8") };
        BIG_RUINS_MOSSY = new ResourceLocation[] { new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8") };
        BIG_RUINS_CRACKED = new ResourceLocation[] { new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8") };
        BIG_WARM_RUINS = new ResourceLocation[] { new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7") };
    }
    
    public static class OceanRuinPiece extends TemplateStructurePiece {
        private final OceanRuinFeature.Type biomeType;
        private final float integrity;
        private final ResourceLocation templateLocation;
        private final Rotation rotation;
        private final boolean isLarge;
        
        public OceanRuinPiece(final StructureManager cjp, final ResourceLocation qv, final BlockPos ew, final Rotation brg, final float float5, final OceanRuinFeature.Type b, final boolean boolean7) {
            super(StructurePieceType.OCEAN_RUIN, 0);
            this.templateLocation = qv;
            this.templatePosition = ew;
            this.rotation = brg;
            this.integrity = float5;
            this.biomeType = b;
            this.isLarge = boolean7;
            this.loadTemplate(cjp);
        }
        
        public OceanRuinPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_RUIN, id);
            this.templateLocation = new ResourceLocation(id.getString("Template"));
            this.rotation = Rotation.valueOf(id.getString("Rot"));
            this.integrity = id.getFloat("Integrity");
            this.biomeType = OceanRuinFeature.Type.valueOf(id.getString("BiomeType"));
            this.isLarge = id.getBoolean("IsLarge");
            this.loadTemplate(cjp);
        }
        
        private void loadTemplate(final StructureManager cjp) {
            final StructureTemplate cjt3 = cjp.getOrCreate(this.templateLocation);
            final StructurePlaceSettings cjq4 = new StructurePlaceSettings().setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            this.setup(cjt3, this.templatePosition, cjq4);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putString("Template", this.templateLocation.toString());
            id.putString("Rot", this.rotation.name());
            id.putFloat("Integrity", this.integrity);
            id.putString("BiomeType", this.biomeType.toString());
            id.putBoolean("IsLarge", this.isLarge);
        }
        
        @Override
        protected void handleDataMarker(final String string, final BlockPos ew, final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if ("chest".equals(string)) {
                bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)Blocks.CHEST.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)ChestBlock.WATERLOGGED, bhs.getFluidState(ew).is(FluidTags.WATER)), 2);
                final BlockEntity btw7 = bhs.getBlockEntity(ew);
                if (btw7 instanceof ChestBlockEntity) {
                    ((ChestBlockEntity)btw7).setLootTable(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL, random.nextLong());
                }
            }
            else if ("drowned".equals(string)) {
                final Drowned aug7 = EntityType.DROWNED.create(bhs.getLevel());
                aug7.setPersistenceRequired();
                aug7.moveTo(ew, 0.0f, 0.0f);
                aug7.finalizeSpawn(bhs, bhs.getCurrentDifficultyAt(ew), MobSpawnType.STRUCTURE, null, null);
                bhs.addFreshEntity(aug7);
                if (ew.getY() > bhs.getSeaLevel()) {
                    bhs.setBlock(ew, Blocks.AIR.defaultBlockState(), 2);
                }
                else {
                    bhs.setBlock(ew, Blocks.WATER.defaultBlockState(), 2);
                }
            }
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            final int integer6 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
            this.templatePosition = new BlockPos(this.templatePosition.getX(), integer6, this.templatePosition.getZ());
            final BlockPos ew7 = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).offset(this.templatePosition);
            this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, bhs, ew7), this.templatePosition.getZ());
            return super.postProcess(bhs, random, cic, bhd);
        }
        
        private int getHeight(final BlockPos ew1, final BlockGetter bhb, final BlockPos ew3) {
            int integer5 = ew1.getY();
            int integer6 = 512;
            final int integer7 = integer5 - 1;
            int integer8 = 0;
            for (final BlockPos ew4 : BlockPos.betweenClosed(ew1, ew3)) {
                final int integer9 = ew4.getX();
                final int integer10 = ew4.getZ();
                int integer11 = ew1.getY() - 1;
                final BlockPos.MutableBlockPos a14 = new BlockPos.MutableBlockPos(integer9, integer11, integer10);
                BlockState bvt15 = bhb.getBlockState(a14);
                for (FluidState clk16 = bhb.getFluidState(a14); (bvt15.isAir() || clk16.is(FluidTags.WATER) || bvt15.getBlock().is(BlockTags.ICE)) && integer11 > 1; bvt15 = bhb.getBlockState(a14), clk16 = bhb.getFluidState(a14)) {
                    --integer11;
                    a14.set(integer9, integer11, integer10);
                }
                integer6 = Math.min(integer6, integer11);
                if (integer11 < integer7 - 2) {
                    ++integer8;
                }
            }
            final int integer12 = Math.abs(ew1.getX() - ew3.getX());
            if (integer7 - integer6 > 2 && integer8 > integer12 - 2) {
                integer5 = integer6 + 1;
            }
            return integer5;
        }
    }
}
