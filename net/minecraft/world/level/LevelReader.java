package net.minecraft.world.level;

import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.tags.FluidTags;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.BooleanOp;
import java.util.function.Consumer;
import java.util.Spliterators;
import net.minecraft.core.Cursor3D;
import net.minecraft.util.Mth;
import com.google.common.collect.Streams;
import java.util.stream.Stream;
import java.util.Set;
import java.util.Collections;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public interface LevelReader extends BlockAndBiomeGetter {
    default boolean isEmptyBlock(final BlockPos ew) {
        return this.getBlockState(ew).isAir();
    }
    
    default boolean canSeeSkyFromBelowWater(final BlockPos ew) {
        if (ew.getY() >= this.getSeaLevel()) {
            return this.canSeeSky(ew);
        }
        BlockPos ew2 = new BlockPos(ew.getX(), this.getSeaLevel(), ew.getZ());
        if (!this.canSeeSky(ew2)) {
            return false;
        }
        for (ew2 = ew2.below(); ew2.getY() > ew.getY(); ew2 = ew2.below()) {
            final BlockState bvt4 = this.getBlockState(ew2);
            if (bvt4.getLightBlock(this, ew2) > 0 && !bvt4.getMaterial().isLiquid()) {
                return false;
            }
        }
        return true;
    }
    
    int getRawBrightness(final BlockPos ew, final int integer);
    
    @Nullable
    ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4);
    
    @Deprecated
    boolean hasChunk(final int integer1, final int integer2);
    
    BlockPos getHeightmapPos(final Heightmap.Types a, final BlockPos ew);
    
    int getHeight(final Heightmap.Types a, final int integer2, final int integer3);
    
    default float getBrightness(final BlockPos ew) {
        return this.getDimension().getBrightnessRamp()[this.getMaxLocalRawBrightness(ew)];
    }
    
    int getSkyDarken();
    
    WorldBorder getWorldBorder();
    
    boolean isUnobstructed(@Nullable final Entity aio, final VoxelShape ctc);
    
    default int getDirectSignal(final BlockPos ew, final Direction fb) {
        return this.getBlockState(ew).getDirectSignal(this, ew, fb);
    }
    
    boolean isClientSide();
    
    int getSeaLevel();
    
    default ChunkAccess getChunk(final BlockPos ew) {
        return this.getChunk(ew.getX() >> 4, ew.getZ() >> 4);
    }
    
    default ChunkAccess getChunk(final int integer1, final int integer2) {
        return this.getChunk(integer1, integer2, ChunkStatus.FULL, true);
    }
    
    default ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm) {
        return this.getChunk(integer1, integer2, bxm, true);
    }
    
    default ChunkStatus statusForCollisions() {
        return ChunkStatus.EMPTY;
    }
    
    default boolean isUnobstructed(final BlockState bvt, final BlockPos ew, final CollisionContext csn) {
        final VoxelShape ctc5 = bvt.getCollisionShape(this, ew, csn);
        return ctc5.isEmpty() || this.isUnobstructed(null, ctc5.move(ew.getX(), ew.getY(), ew.getZ()));
    }
    
    default boolean isUnobstructed(final Entity aio) {
        return this.isUnobstructed(aio, Shapes.create(aio.getBoundingBox()));
    }
    
    default boolean noCollision(final AABB csc) {
        return this.noCollision(null, csc, (Set<Entity>)Collections.emptySet());
    }
    
    default boolean noCollision(final Entity aio) {
        return this.noCollision(aio, aio.getBoundingBox(), (Set<Entity>)Collections.emptySet());
    }
    
    default boolean noCollision(final Entity aio, final AABB csc) {
        return this.noCollision(aio, csc, (Set<Entity>)Collections.emptySet());
    }
    
    default boolean noCollision(@Nullable final Entity aio, final AABB csc, final Set<Entity> set) {
        return this.getCollisions(aio, csc, set).allMatch(VoxelShape::isEmpty);
    }
    
    default Stream<VoxelShape> getEntityCollisions(@Nullable final Entity aio, final AABB csc, final Set<Entity> set) {
        return (Stream<VoxelShape>)Stream.empty();
    }
    
    default Stream<VoxelShape> getCollisions(@Nullable final Entity aio, final AABB csc, final Set<Entity> set) {
        return (Stream<VoxelShape>)Streams.concat(new Stream[] { this.getBlockCollisions(aio, csc), this.getEntityCollisions(aio, csc, set) });
    }
    
    default Stream<VoxelShape> getBlockCollisions(@Nullable final Entity aio, final AABB csc) {
        final int integer4 = Mth.floor(csc.minX - 1.0E-7) - 1;
        final int integer5 = Mth.floor(csc.maxX + 1.0E-7) + 1;
        final int integer6 = Mth.floor(csc.minY - 1.0E-7) - 1;
        final int integer7 = Mth.floor(csc.maxY + 1.0E-7) + 1;
        final int integer8 = Mth.floor(csc.minZ - 1.0E-7) - 1;
        final int integer9 = Mth.floor(csc.maxZ + 1.0E-7) + 1;
        final CollisionContext csn10 = (aio == null) ? CollisionContext.empty() : CollisionContext.of(aio);
        final Cursor3D ez11 = new Cursor3D(integer4, integer6, integer8, integer5, integer7, integer9);
        final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
        final VoxelShape ctc13 = Shapes.create(csc);
        return (Stream<VoxelShape>)StreamSupport.stream((Spliterator)new Spliterators.AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
            boolean checkedBorder = aio == null;
            
            public boolean tryAdvance(final Consumer<? super VoxelShape> consumer) {
                if (!this.checkedBorder) {
                    this.checkedBorder = true;
                    final VoxelShape ctc3 = LevelReader.this.getWorldBorder().getCollisionShape();
                    final boolean boolean4 = Shapes.joinIsNotEmpty(ctc3, Shapes.create(aio.getBoundingBox().deflate(1.0E-7)), BooleanOp.AND);
                    final boolean boolean5 = Shapes.joinIsNotEmpty(ctc3, Shapes.create(aio.getBoundingBox().inflate(1.0E-7)), BooleanOp.AND);
                    if (!boolean4 && boolean5) {
                        consumer.accept(ctc3);
                        return true;
                    }
                }
                while (ez11.advance()) {
                    final int integer3 = ez11.nextX();
                    final int integer4 = ez11.nextY();
                    final int integer5 = ez11.nextZ();
                    final int integer6 = ez11.getNextType();
                    if (integer6 == 3) {
                        continue;
                    }
                    final int integer7 = integer3 >> 4;
                    final int integer8 = integer5 >> 4;
                    final ChunkAccess bxh9 = LevelReader.this.getChunk(integer7, integer8, LevelReader.this.statusForCollisions(), false);
                    if (bxh9 == null) {
                        continue;
                    }
                    a12.set(integer3, integer4, integer5);
                    final BlockState bvt10 = bxh9.getBlockState(a12);
                    if (integer6 == 1 && !bvt10.hasLargeCollisionShape()) {
                        continue;
                    }
                    if (integer6 == 2 && bvt10.getBlock() != Blocks.MOVING_PISTON) {
                        continue;
                    }
                    final VoxelShape ctc4 = bvt10.getCollisionShape(LevelReader.this, a12, csn10);
                    final VoxelShape ctc5 = ctc4.move(integer3, integer4, integer5);
                    if (Shapes.joinIsNotEmpty(ctc13, ctc5, BooleanOp.AND)) {
                        consumer.accept(ctc5);
                        return true;
                    }
                }
                return false;
            }
        }, false);
    }
    
    default boolean isWaterAt(final BlockPos ew) {
        return this.getFluidState(ew).is(FluidTags.WATER);
    }
    
    default boolean containsAnyLiquid(final AABB csc) {
        final int integer3 = Mth.floor(csc.minX);
        final int integer4 = Mth.ceil(csc.maxX);
        final int integer5 = Mth.floor(csc.minY);
        final int integer6 = Mth.ceil(csc.maxY);
        final int integer7 = Mth.floor(csc.minZ);
        final int integer8 = Mth.ceil(csc.maxZ);
        try (final BlockPos.PooledMutableBlockPos b9 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (int integer9 = integer3; integer9 < integer4; ++integer9) {
                for (int integer10 = integer5; integer10 < integer6; ++integer10) {
                    for (int integer11 = integer7; integer11 < integer8; ++integer11) {
                        final BlockState bvt14 = this.getBlockState(b9.set(integer9, integer10, integer11));
                        if (!bvt14.getFluidState().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    default int getMaxLocalRawBrightness(final BlockPos ew) {
        return this.getMaxLocalRawBrightness(ew, this.getSkyDarken());
    }
    
    default int getMaxLocalRawBrightness(final BlockPos ew, final int integer) {
        if (ew.getX() < -30000000 || ew.getZ() < -30000000 || ew.getX() >= 30000000 || ew.getZ() >= 30000000) {
            return 15;
        }
        return this.getRawBrightness(ew, integer);
    }
    
    @Deprecated
    default boolean hasChunkAt(final BlockPos ew) {
        return this.hasChunk(ew.getX() >> 4, ew.getZ() >> 4);
    }
    
    @Deprecated
    default boolean hasChunksAt(final BlockPos ew1, final BlockPos ew2) {
        return this.hasChunksAt(ew1.getX(), ew1.getY(), ew1.getZ(), ew2.getX(), ew2.getY(), ew2.getZ());
    }
    
    @Deprecated
    default boolean hasChunksAt(int integer1, final int integer2, int integer3, int integer4, final int integer5, int integer6) {
        if (integer5 < 0 || integer2 >= 256) {
            return false;
        }
        integer1 >>= 4;
        integer3 >>= 4;
        integer4 >>= 4;
        integer6 >>= 4;
        for (int integer7 = integer1; integer7 <= integer4; ++integer7) {
            for (int integer8 = integer3; integer8 <= integer6; ++integer8) {
                if (!this.hasChunk(integer7, integer8)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    Dimension getDimension();
}
