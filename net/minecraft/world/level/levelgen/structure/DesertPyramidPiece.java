package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Iterator;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
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

public class DesertPyramidPiece extends ScatteredFeaturePiece {
    private final boolean[] hasPlacedChest;
    
    public DesertPyramidPiece(final Random random, final int integer2, final int integer3) {
        super(StructurePieceType.DESERT_PYRAMID_PIECE, random, integer2, 64, integer3, 21, 15, 21);
        this.hasPlacedChest = new boolean[4];
    }
    
    public DesertPyramidPiece(final StructureManager cjp, final CompoundTag id) {
        super(StructurePieceType.DESERT_PYRAMID_PIECE, id);
        (this.hasPlacedChest = new boolean[4])[0] = id.getBoolean("hasPlacedChest0");
        this.hasPlacedChest[1] = id.getBoolean("hasPlacedChest1");
        this.hasPlacedChest[2] = id.getBoolean("hasPlacedChest2");
        this.hasPlacedChest[3] = id.getBoolean("hasPlacedChest3");
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
        id.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
        id.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
        id.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
    }
    
    @Override
    public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
        this.generateBox(bhs, cic, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        for (int integer6 = 1; integer6 <= 9; ++integer6) {
            this.generateBox(bhs, cic, integer6, integer6, integer6, this.width - 1 - integer6, integer6, this.depth - 1 - integer6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(bhs, cic, integer6 + 1, integer6, integer6 + 1, this.width - 2 - integer6, integer6, this.depth - 2 - integer6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        }
        for (int integer6 = 0; integer6 < this.width; ++integer6) {
            for (int integer7 = 0; integer7 < this.depth; ++integer7) {
                final int integer8 = -5;
                this.fillColumnDown(bhs, Blocks.SANDSTONE.defaultBlockState(), integer6, -5, integer7, cic);
            }
        }
        final BlockState bvt6 = ((AbstractStateHolder<O, BlockState>)Blocks.SANDSTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.NORTH);
        final BlockState bvt7 = ((AbstractStateHolder<O, BlockState>)Blocks.SANDSTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.SOUTH);
        final BlockState bvt8 = ((AbstractStateHolder<O, BlockState>)Blocks.SANDSTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.EAST);
        final BlockState bvt9 = ((AbstractStateHolder<O, BlockState>)Blocks.SANDSTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.WEST);
        this.generateBox(bhs, cic, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.placeBlock(bhs, bvt6, 2, 10, 0, cic);
        this.placeBlock(bhs, bvt7, 2, 10, 4, cic);
        this.placeBlock(bhs, bvt8, 0, 10, 2, cic);
        this.placeBlock(bhs, bvt9, 4, 10, 2, cic);
        this.generateBox(bhs, cic, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.placeBlock(bhs, bvt6, this.width - 3, 10, 0, cic);
        this.placeBlock(bhs, bvt7, this.width - 3, 10, 4, cic);
        this.placeBlock(bhs, bvt8, this.width - 5, 10, 2, cic);
        this.placeBlock(bhs, bvt9, this.width - 1, 10, 2, cic);
        this.generateBox(bhs, cic, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, cic);
        this.generateBox(bhs, cic, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 5, 5, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 5, 6, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 6, 6, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, cic);
        this.generateBox(bhs, cic, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock(bhs, bvt6, 2, 4, 5, cic);
        this.placeBlock(bhs, bvt6, 2, 3, 4, cic);
        this.placeBlock(bhs, bvt6, this.width - 3, 4, 5, cic);
        this.placeBlock(bhs, bvt6, this.width - 3, 3, 4, cic);
        this.generateBox(bhs, cic, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, cic);
        this.placeBlock(bhs, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, cic);
        this.placeBlock(bhs, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, cic);
        this.placeBlock(bhs, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, cic);
        this.placeBlock(bhs, bvt9, 2, 1, 2, cic);
        this.placeBlock(bhs, bvt8, this.width - 3, 1, 2, cic);
        this.generateBox(bhs, cic, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox(bhs, cic, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        for (int integer9 = 5; integer9 <= 17; integer9 += 2) {
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, integer9, cic);
            this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, integer9, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, integer9, cic);
            this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, integer9, cic);
        }
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, cic);
        this.placeBlock(bhs, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, cic);
        for (int integer9 = 0; integer9 <= this.width - 1; integer9 += this.width - 1) {
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 2, 1, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 2, 2, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 2, 3, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 3, 1, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 3, 2, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 3, 3, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 4, 1, cic);
            this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), integer9, 4, 2, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 4, 3, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 5, 1, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 5, 2, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 5, 3, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 6, 1, cic);
            this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), integer9, 6, 2, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 6, 3, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 7, 1, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 7, 2, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 7, 3, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 8, 1, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 8, 2, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 8, 3, cic);
        }
        for (int integer9 = 2; integer9 <= this.width - 3; integer9 += this.width - 3 - 2) {
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 - 1, 2, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 2, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 + 1, 2, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 - 1, 3, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 3, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 + 1, 3, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9 - 1, 4, 0, cic);
            this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), integer9, 4, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9 + 1, 4, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 - 1, 5, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 5, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 + 1, 5, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9 - 1, 6, 0, cic);
            this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), integer9, 6, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9 + 1, 6, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9 - 1, 7, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9, 7, 0, cic);
            this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), integer9 + 1, 7, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 - 1, 8, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9, 8, 0, cic);
            this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), integer9 + 1, 8, 0, cic);
        }
        this.generateBox(bhs, cic, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 8, 6, 0, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 12, 6, 0, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, cic);
        this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, cic);
        this.placeBlock(bhs, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, cic);
        this.generateBox(bhs, cic, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox(bhs, cic, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, cic);
        this.generateBox(bhs, cic, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 8, -11, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 8, -10, 10, cic);
        this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 12, -11, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 12, -10, 10, cic);
        this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 10, -11, 8, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 10, -10, 8, cic);
        this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 10, -11, 12, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 10, -10, 12, cic);
        this.placeBlock(bhs, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, cic);
        this.placeBlock(bhs, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, cic);
        for (final Direction fb11 : Direction.Plane.HORIZONTAL) {
            if (!this.hasPlacedChest[fb11.get2DDataValue()]) {
                final int integer10 = fb11.getStepX() * 2;
                final int integer11 = fb11.getStepZ() * 2;
                this.hasPlacedChest[fb11.get2DDataValue()] = this.createChest(bhs, cic, random, 10 + integer10, -11, 10 + integer11, BuiltInLootTables.DESERT_PYRAMID);
            }
        }
        return true;
    }
}
