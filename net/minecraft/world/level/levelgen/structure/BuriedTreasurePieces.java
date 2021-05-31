package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.core.BlockPos;

public class BuriedTreasurePieces {
    public static class BuriedTreasurePiece extends StructurePiece {
        public BuriedTreasurePiece(final BlockPos ew) {
            super(StructurePieceType.BURIED_TREASURE_PIECE, 0);
            this.boundingBox = new BoundingBox(ew.getX(), ew.getY(), ew.getZ(), ew.getX(), ew.getY(), ew.getZ());
        }
        
        public BuriedTreasurePiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.BURIED_TREASURE_PIECE, id);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final int integer6 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
            final BlockPos.MutableBlockPos a7 = new BlockPos.MutableBlockPos(this.boundingBox.x0, integer6, this.boundingBox.z0);
            while (a7.getY() > 0) {
                final BlockState bvt8 = bhs.getBlockState(a7);
                final BlockState bvt9 = bhs.getBlockState(a7.below());
                if (bvt9 == Blocks.SANDSTONE.defaultBlockState() || bvt9 == Blocks.STONE.defaultBlockState() || bvt9 == Blocks.ANDESITE.defaultBlockState() || bvt9 == Blocks.GRANITE.defaultBlockState() || bvt9 == Blocks.DIORITE.defaultBlockState()) {
                    final BlockState bvt10 = (bvt8.isAir() || this.isLiquid(bvt8)) ? Blocks.SAND.defaultBlockState() : bvt8;
                    for (final Direction fb14 : Direction.values()) {
                        final BlockPos ew15 = a7.relative(fb14);
                        final BlockState bvt11 = bhs.getBlockState(ew15);
                        if (bvt11.isAir() || this.isLiquid(bvt11)) {
                            final BlockPos ew16 = ew15.below();
                            final BlockState bvt12 = bhs.getBlockState(ew16);
                            if ((bvt12.isAir() || this.isLiquid(bvt12)) && fb14 != Direction.UP) {
                                bhs.setBlock(ew15, bvt9, 3);
                            }
                            else {
                                bhs.setBlock(ew15, bvt10, 3);
                            }
                        }
                    }
                    this.boundingBox = new BoundingBox(a7.getX(), a7.getY(), a7.getZ(), a7.getX(), a7.getY(), a7.getZ());
                    return this.createChest(bhs, cic, random, a7, BuiltInLootTables.BURIED_TREASURE, null);
                }
                a7.move(0, -1, 0);
            }
            return false;
        }
        
        private boolean isLiquid(final BlockState bvt) {
            return bvt == Blocks.WATER.defaultBlockState() || bvt == Blocks.LAVA.defaultBlockState();
        }
    }
}
