package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import java.util.Random;

public class SwamplandHutPiece extends ScatteredFeaturePiece {
    private boolean spawnedWitch;
    private boolean spawnedCat;
    
    public SwamplandHutPiece(final Random random, final int integer2, final int integer3) {
        super(StructurePieceType.SWAMPLAND_HUT, random, integer2, 64, integer3, 7, 7, 9);
    }
    
    public SwamplandHutPiece(final StructureManager cjp, final CompoundTag id) {
        super(StructurePieceType.SWAMPLAND_HUT, id);
        this.spawnedWitch = id.getBoolean("Witch");
        this.spawnedCat = id.getBoolean("Cat");
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("Witch", this.spawnedWitch);
        id.putBoolean("Cat", this.spawnedCat);
    }
    
    @Override
    public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
        if (!this.updateAverageGroundHeight(bhs, cic, 0)) {
            return false;
        }
        this.generateBox(bhs, cic, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(bhs, cic, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox(bhs, cic, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox(bhs, cic, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox(bhs, cic, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, cic);
        this.placeBlock(bhs, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 1, 3, 4, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 5, 3, 4, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 5, 3, 5, cic);
        this.placeBlock(bhs, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, cic);
        this.placeBlock(bhs, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, cic);
        this.placeBlock(bhs, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, cic);
        this.placeBlock(bhs, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, cic);
        this.placeBlock(bhs, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, cic);
        final BlockState bvt6 = ((AbstractStateHolder<O, BlockState>)Blocks.SPRUCE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.NORTH);
        final BlockState bvt7 = ((AbstractStateHolder<O, BlockState>)Blocks.SPRUCE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.EAST);
        final BlockState bvt8 = ((AbstractStateHolder<O, BlockState>)Blocks.SPRUCE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.WEST);
        final BlockState bvt9 = ((AbstractStateHolder<O, BlockState>)Blocks.SPRUCE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.SOUTH);
        this.generateBox(bhs, cic, 0, 4, 1, 6, 4, 1, bvt6, bvt6, false);
        this.generateBox(bhs, cic, 0, 4, 2, 0, 4, 7, bvt7, bvt7, false);
        this.generateBox(bhs, cic, 6, 4, 2, 6, 4, 7, bvt8, bvt8, false);
        this.generateBox(bhs, cic, 0, 4, 8, 6, 4, 8, bvt9, bvt9, false);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)bvt6).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)bvt6).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)bvt9).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)bvt9).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, cic);
        for (int integer10 = 2; integer10 <= 7; integer10 += 5) {
            for (int integer11 = 1; integer11 <= 5; integer11 += 4) {
                this.fillColumnDown(bhs, Blocks.OAK_LOG.defaultBlockState(), integer11, -1, integer10, cic);
            }
        }
        if (!this.spawnedWitch) {
            final int integer10 = this.getWorldX(2, 5);
            final int integer11 = this.getWorldY(2);
            final int integer12 = this.getWorldZ(2, 5);
            if (cic.isInside(new BlockPos(integer10, integer11, integer12))) {
                this.spawnedWitch = true;
                final Witch avk13 = EntityType.WITCH.create(bhs.getLevel());
                avk13.setPersistenceRequired();
                avk13.moveTo(integer10 + 0.5, integer11, integer12 + 0.5, 0.0f, 0.0f);
                avk13.finalizeSpawn(bhs, bhs.getCurrentDifficultyAt(new BlockPos(integer10, integer11, integer12)), MobSpawnType.STRUCTURE, null, null);
                bhs.addFreshEntity(avk13);
            }
        }
        this.spawnCat(bhs, cic);
        return true;
    }
    
    private void spawnCat(final LevelAccessor bhs, final BoundingBox cic) {
        if (!this.spawnedCat) {
            final int integer4 = this.getWorldX(2, 5);
            final int integer5 = this.getWorldY(2);
            final int integer6 = this.getWorldZ(2, 5);
            if (cic.isInside(new BlockPos(integer4, integer5, integer6))) {
                this.spawnedCat = true;
                final Cat arb7 = EntityType.CAT.create(bhs.getLevel());
                arb7.setPersistenceRequired();
                arb7.moveTo(integer4 + 0.5, integer5, integer6 + 0.5, 0.0f, 0.0f);
                arb7.finalizeSpawn(bhs, bhs.getCurrentDifficultyAt(new BlockPos(integer4, integer5, integer6)), MobSpawnType.STRUCTURE, null, null);
                bhs.addFreshEntity(arb7);
            }
        }
    }
}
