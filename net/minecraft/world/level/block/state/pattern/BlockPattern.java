package net.minecraft.world.level.block.state.pattern;

import net.minecraft.world.phys.Vec3;
import com.google.common.base.MoreObjects;
import net.minecraft.core.Vec3i;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import javax.annotation.Nullable;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import java.util.function.Predicate;

public class BlockPattern {
    private final Predicate<BlockInWorld>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;
    
    public BlockPattern(final Predicate<BlockInWorld>[][][] arr) {
        this.pattern = arr;
        this.depth = arr.length;
        if (this.depth > 0) {
            this.height = arr[0].length;
            if (this.height > 0) {
                this.width = arr[0][0].length;
            }
            else {
                this.width = 0;
            }
        }
        else {
            this.height = 0;
            this.width = 0;
        }
    }
    
    public int getDepth() {
        return this.depth;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    @Nullable
    private BlockPatternMatch matches(final BlockPos ew, final Direction fb2, final Direction fb3, final LoadingCache<BlockPos, BlockInWorld> loadingCache) {
        for (int integer6 = 0; integer6 < this.width; ++integer6) {
            for (int integer7 = 0; integer7 < this.height; ++integer7) {
                for (int integer8 = 0; integer8 < this.depth; ++integer8) {
                    if (!this.pattern[integer8][integer7][integer6].test(loadingCache.getUnchecked(translateAndRotate(ew, fb2, fb3, integer6, integer7, integer8)))) {
                        return null;
                    }
                }
            }
        }
        return new BlockPatternMatch(ew, fb2, fb3, loadingCache, this.width, this.height, this.depth);
    }
    
    @Nullable
    public BlockPatternMatch find(final LevelReader bhu, final BlockPos ew) {
        final LoadingCache<BlockPos, BlockInWorld> loadingCache4 = createLevelCache(bhu, false);
        final int integer5 = Math.max(Math.max(this.width, this.height), this.depth);
        for (final BlockPos ew2 : BlockPos.betweenClosed(ew, ew.offset(integer5 - 1, integer5 - 1, integer5 - 1))) {
            for (final Direction fb11 : Direction.values()) {
                for (final Direction fb12 : Direction.values()) {
                    if (fb12 != fb11) {
                        if (fb12 != fb11.getOpposite()) {
                            final BlockPatternMatch b16 = this.matches(ew2, fb11, fb12, loadingCache4);
                            if (b16 != null) {
                                return b16;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static LoadingCache<BlockPos, BlockInWorld> createLevelCache(final LevelReader bhu, final boolean boolean2) {
        return (LoadingCache<BlockPos, BlockInWorld>)CacheBuilder.newBuilder().build((CacheLoader)new BlockCacheLoader(bhu, boolean2));
    }
    
    protected static BlockPos translateAndRotate(final BlockPos ew, final Direction fb2, final Direction fb3, final int integer4, final int integer5, final int integer6) {
        if (fb2 == fb3 || fb2 == fb3.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        final Vec3i fs7 = new Vec3i(fb2.getStepX(), fb2.getStepY(), fb2.getStepZ());
        final Vec3i fs8 = new Vec3i(fb3.getStepX(), fb3.getStepY(), fb3.getStepZ());
        final Vec3i fs9 = fs7.cross(fs8);
        return ew.offset(fs8.getX() * -integer5 + fs9.getX() * integer4 + fs7.getX() * integer6, fs8.getY() * -integer5 + fs9.getY() * integer4 + fs7.getY() * integer6, fs8.getZ() * -integer5 + fs9.getZ() * integer4 + fs7.getZ() * integer6);
    }
    
    static class BlockCacheLoader extends CacheLoader<BlockPos, BlockInWorld> {
        private final LevelReader level;
        private final boolean loadChunks;
        
        public BlockCacheLoader(final LevelReader bhu, final boolean boolean2) {
            this.level = bhu;
            this.loadChunks = boolean2;
        }
        
        public BlockInWorld load(final BlockPos ew) throws Exception {
            return new BlockInWorld(this.level, ew, this.loadChunks);
        }
    }
    
    public static class BlockPatternMatch {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, BlockInWorld> cache;
        private final int width;
        private final int height;
        private final int depth;
        
        public BlockPatternMatch(final BlockPos ew, final Direction fb2, final Direction fb3, final LoadingCache<BlockPos, BlockInWorld> loadingCache, final int integer5, final int integer6, final int integer7) {
            this.frontTopLeft = ew;
            this.forwards = fb2;
            this.up = fb3;
            this.cache = loadingCache;
            this.width = integer5;
            this.height = integer6;
            this.depth = integer7;
        }
        
        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }
        
        public Direction getForwards() {
            return this.forwards;
        }
        
        public Direction getUp() {
            return this.up;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public BlockInWorld getBlock(final int integer1, final int integer2, final int integer3) {
            return (BlockInWorld)this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), integer1, integer2, integer3));
        }
        
        public String toString() {
            return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
        }
        
        public PortalInfo getPortalOutput(final Direction fb, final BlockPos ew, final double double3, final Vec3 csi, final double double5) {
            final Direction fb2 = this.getForwards();
            final Direction fb3 = fb2.getClockWise();
            final double double6 = this.getFrontTopLeft().getY() + 1 - double3 * this.getHeight();
            double double7;
            double double8;
            if (fb3 == Direction.NORTH) {
                double7 = ew.getX() + 0.5;
                double8 = this.getFrontTopLeft().getZ() + 1 - (1.0 - double5) * this.getWidth();
            }
            else if (fb3 == Direction.SOUTH) {
                double7 = ew.getX() + 0.5;
                double8 = this.getFrontTopLeft().getZ() + (1.0 - double5) * this.getWidth();
            }
            else if (fb3 == Direction.WEST) {
                double7 = this.getFrontTopLeft().getX() + 1 - (1.0 - double5) * this.getWidth();
                double8 = ew.getZ() + 0.5;
            }
            else {
                double7 = this.getFrontTopLeft().getX() + (1.0 - double5) * this.getWidth();
                double8 = ew.getZ() + 0.5;
            }
            double double9;
            double double10;
            if (fb2.getOpposite() == fb) {
                double9 = csi.x;
                double10 = csi.z;
            }
            else if (fb2.getOpposite() == fb.getOpposite()) {
                double9 = -csi.x;
                double10 = -csi.z;
            }
            else if (fb2.getOpposite() == fb.getClockWise()) {
                double9 = -csi.z;
                double10 = csi.x;
            }
            else {
                double9 = csi.z;
                double10 = -csi.x;
            }
            final int integer21 = (fb2.get2DDataValue() - fb.getOpposite().get2DDataValue()) * 90;
            return new PortalInfo(new Vec3(double7, double6, double8), new Vec3(double9, csi.y, double10), integer21);
        }
    }
    
    public static class PortalInfo {
        public final Vec3 pos;
        public final Vec3 speed;
        public final int angle;
        
        public PortalInfo(final Vec3 csi1, final Vec3 csi2, final int integer) {
            this.pos = csi1;
            this.speed = csi2;
            this.angle = integer;
        }
    }
}
