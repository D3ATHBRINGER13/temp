package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import java.util.Spliterators;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Rotation;
import java.util.stream.IntStream;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Spliterator;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Logger;
import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Serializable;

@Immutable
public class BlockPos extends Vec3i implements Serializable {
    private static final Logger LOGGER;
    public static final BlockPos ZERO;
    private static final int PACKED_X_LENGTH;
    private static final int PACKED_Z_LENGTH;
    private static final int PACKED_Y_LENGTH;
    private static final long PACKED_X_MASK;
    private static final long PACKED_Y_MASK;
    private static final long PACKED_Z_MASK;
    private static final int Z_OFFSET;
    private static final int X_OFFSET;
    
    public BlockPos(final int integer1, final int integer2, final int integer3) {
        super(integer1, integer2, integer3);
    }
    
    public BlockPos(final double double1, final double double2, final double double3) {
        super(double1, double2, double3);
    }
    
    public BlockPos(final Entity aio) {
        this(aio.x, aio.y, aio.z);
    }
    
    public BlockPos(final Vec3 csi) {
        this(csi.x, csi.y, csi.z);
    }
    
    public BlockPos(final Position fl) {
        this(fl.x(), fl.y(), fl.z());
    }
    
    public BlockPos(final Vec3i fs) {
        this(fs.getX(), fs.getY(), fs.getZ());
    }
    
    public static <T> BlockPos deserialize(final Dynamic<T> dynamic) {
        final Spliterator.OfInt ofInt2 = dynamic.asIntStream().spliterator();
        final int[] arr3 = new int[3];
        if (ofInt2.tryAdvance(integer -> arr3[0] = integer) && ofInt2.tryAdvance(integer -> arr3[1] = integer)) {
            ofInt2.tryAdvance(integer -> arr3[2] = integer);
        }
        return new BlockPos(arr3[0], arr3[1], arr3[2]);
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createIntList(IntStream.of(new int[] { this.getX(), this.getY(), this.getZ() }));
    }
    
    public static long offset(final long long1, final Direction fb) {
        return offset(long1, fb.getStepX(), fb.getStepY(), fb.getStepZ());
    }
    
    public static long offset(final long long1, final int integer2, final int integer3, final int integer4) {
        return asLong(getX(long1) + integer2, getY(long1) + integer3, getZ(long1) + integer4);
    }
    
    public static int getX(final long long1) {
        return (int)(long1 << 64 - BlockPos.X_OFFSET - BlockPos.PACKED_X_LENGTH >> 64 - BlockPos.PACKED_X_LENGTH);
    }
    
    public static int getY(final long long1) {
        return (int)(long1 << 64 - BlockPos.PACKED_Y_LENGTH >> 64 - BlockPos.PACKED_Y_LENGTH);
    }
    
    public static int getZ(final long long1) {
        return (int)(long1 << 64 - BlockPos.Z_OFFSET - BlockPos.PACKED_Z_LENGTH >> 64 - BlockPos.PACKED_Z_LENGTH);
    }
    
    public static BlockPos of(final long long1) {
        return new BlockPos(getX(long1), getY(long1), getZ(long1));
    }
    
    public static long asLong(final int integer1, final int integer2, final int integer3) {
        long long4 = 0L;
        long4 |= ((long)integer1 & BlockPos.PACKED_X_MASK) << BlockPos.X_OFFSET;
        long4 |= ((long)integer2 & BlockPos.PACKED_Y_MASK) << 0;
        long4 |= ((long)integer3 & BlockPos.PACKED_Z_MASK) << BlockPos.Z_OFFSET;
        return long4;
    }
    
    public static long getFlatIndex(final long long1) {
        return long1 & 0xFFFFFFFFFFFFFFF0L;
    }
    
    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }
    
    public BlockPos offset(final double double1, final double double2, final double double3) {
        if (double1 == 0.0 && double2 == 0.0 && double3 == 0.0) {
            return this;
        }
        return new BlockPos(this.getX() + double1, this.getY() + double2, this.getZ() + double3);
    }
    
    public BlockPos offset(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 0 && integer2 == 0 && integer3 == 0) {
            return this;
        }
        return new BlockPos(this.getX() + integer1, this.getY() + integer2, this.getZ() + integer3);
    }
    
    public BlockPos offset(final Vec3i fs) {
        return this.offset(fs.getX(), fs.getY(), fs.getZ());
    }
    
    public BlockPos subtract(final Vec3i fs) {
        return this.offset(-fs.getX(), -fs.getY(), -fs.getZ());
    }
    
    public BlockPos above() {
        return this.above(1);
    }
    
    public BlockPos above(final int integer) {
        return this.relative(Direction.UP, integer);
    }
    
    public BlockPos below() {
        return this.below(1);
    }
    
    public BlockPos below(final int integer) {
        return this.relative(Direction.DOWN, integer);
    }
    
    public BlockPos north() {
        return this.north(1);
    }
    
    public BlockPos north(final int integer) {
        return this.relative(Direction.NORTH, integer);
    }
    
    public BlockPos south() {
        return this.south(1);
    }
    
    public BlockPos south(final int integer) {
        return this.relative(Direction.SOUTH, integer);
    }
    
    public BlockPos west() {
        return this.west(1);
    }
    
    public BlockPos west(final int integer) {
        return this.relative(Direction.WEST, integer);
    }
    
    public BlockPos east() {
        return this.east(1);
    }
    
    public BlockPos east(final int integer) {
        return this.relative(Direction.EAST, integer);
    }
    
    public BlockPos relative(final Direction fb) {
        return this.relative(fb, 1);
    }
    
    public BlockPos relative(final Direction fb, final int integer) {
        if (integer == 0) {
            return this;
        }
        return new BlockPos(this.getX() + fb.getStepX() * integer, this.getY() + fb.getStepY() * integer, this.getZ() + fb.getStepZ() * integer);
    }
    
    public BlockPos rotate(final Rotation brg) {
        switch (brg) {
            default: {
                return this;
            }
            case CLOCKWISE_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case CLOCKWISE_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos(this.getZ(), this.getY(), -this.getX());
            }
        }
    }
    
    public BlockPos d(final Vec3i fs) {
        return new BlockPos(this.getY() * fs.getZ() - this.getZ() * fs.getY(), this.getZ() * fs.getX() - this.getX() * fs.getZ(), this.getX() * fs.getY() - this.getY() * fs.getX());
    }
    
    public BlockPos immutable() {
        return this;
    }
    
    public static Iterable<BlockPos> betweenClosed(final BlockPos ew1, final BlockPos ew2) {
        return betweenClosed(Math.min(ew1.getX(), ew2.getX()), Math.min(ew1.getY(), ew2.getY()), Math.min(ew1.getZ(), ew2.getZ()), Math.max(ew1.getX(), ew2.getX()), Math.max(ew1.getY(), ew2.getY()), Math.max(ew1.getZ(), ew2.getZ()));
    }
    
    public static Stream<BlockPos> betweenClosedStream(final BlockPos ew1, final BlockPos ew2) {
        return betweenClosedStream(Math.min(ew1.getX(), ew2.getX()), Math.min(ew1.getY(), ew2.getY()), Math.min(ew1.getZ(), ew2.getZ()), Math.max(ew1.getX(), ew2.getX()), Math.max(ew1.getY(), ew2.getY()), Math.max(ew1.getZ(), ew2.getZ()));
    }
    
    public static Stream<BlockPos> betweenClosedStream(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        return (Stream<BlockPos>)StreamSupport.stream((Spliterator)new Spliterators.AbstractSpliterator<BlockPos>((long)((integer4 - integer1 + 1) * (integer5 - integer2 + 1) * (integer6 - integer3 + 1)), 64) {
            final Cursor3D cursor = new Cursor3D(integer1, integer2, integer3, integer4, integer5, integer6);
            final MutableBlockPos nextPos = new MutableBlockPos();
            
            public boolean tryAdvance(final Consumer<? super BlockPos> consumer) {
                if (this.cursor.advance()) {
                    consumer.accept(this.nextPos.set(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }
    
    public static Iterable<BlockPos> betweenClosed(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        return (Iterable<BlockPos>)(() -> new AbstractIterator<BlockPos>() {
            final Cursor3D cursor;
            final MutableBlockPos nextPos;
            final /* synthetic */ int val$minX;
            final /* synthetic */ int val$minY;
            final /* synthetic */ int val$minZ;
            final /* synthetic */ int val$maxX;
            final /* synthetic */ int val$maxY;
            final /* synthetic */ int val$maxZ;
            
            {
                this.cursor = new Cursor3D(this.val$minX, this.val$minY, this.val$minZ, this.val$maxX, this.val$maxY, this.val$maxZ);
                this.nextPos = new MutableBlockPos();
            }
            
            protected BlockPos computeNext() {
                return this.cursor.advance() ? this.nextPos.set(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()) : ((BlockPos)this.endOfData());
            }
        });
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ZERO = new BlockPos(0, 0, 0);
        PACKED_X_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
        PACKED_Z_LENGTH = BlockPos.PACKED_X_LENGTH;
        PACKED_Y_LENGTH = 64 - BlockPos.PACKED_X_LENGTH - BlockPos.PACKED_Z_LENGTH;
        PACKED_X_MASK = (1L << BlockPos.PACKED_X_LENGTH) - 1L;
        PACKED_Y_MASK = (1L << BlockPos.PACKED_Y_LENGTH) - 1L;
        PACKED_Z_MASK = (1L << BlockPos.PACKED_Z_LENGTH) - 1L;
        Z_OFFSET = BlockPos.PACKED_Y_LENGTH;
        X_OFFSET = BlockPos.PACKED_Y_LENGTH + BlockPos.PACKED_Z_LENGTH;
    }
    
    public static class MutableBlockPos extends BlockPos {
        protected int x;
        protected int y;
        protected int z;
        
        public MutableBlockPos() {
            this(0, 0, 0);
        }
        
        public MutableBlockPos(final BlockPos ew) {
            this(ew.getX(), ew.getY(), ew.getZ());
        }
        
        public MutableBlockPos(final int integer1, final int integer2, final int integer3) {
            super(0, 0, 0);
            this.x = integer1;
            this.y = integer2;
            this.z = integer3;
        }
        
        public MutableBlockPos(final double double1, final double double2, final double double3) {
            this(Mth.floor(double1), Mth.floor(double2), Mth.floor(double3));
        }
        
        @Override
        public BlockPos offset(final double double1, final double double2, final double double3) {
            return super.offset(double1, double2, double3).immutable();
        }
        
        @Override
        public BlockPos offset(final int integer1, final int integer2, final int integer3) {
            return super.offset(integer1, integer2, integer3).immutable();
        }
        
        @Override
        public BlockPos relative(final Direction fb, final int integer) {
            return super.relative(fb, integer).immutable();
        }
        
        @Override
        public BlockPos rotate(final Rotation brg) {
            return super.rotate(brg).immutable();
        }
        
        @Override
        public int getX() {
            return this.x;
        }
        
        @Override
        public int getY() {
            return this.y;
        }
        
        @Override
        public int getZ() {
            return this.z;
        }
        
        public MutableBlockPos set(final int integer1, final int integer2, final int integer3) {
            this.x = integer1;
            this.y = integer2;
            this.z = integer3;
            return this;
        }
        
        public MutableBlockPos set(final Entity aio) {
            return this.set(aio.x, aio.y, aio.z);
        }
        
        public MutableBlockPos set(final double double1, final double double2, final double double3) {
            return this.set(Mth.floor(double1), Mth.floor(double2), Mth.floor(double3));
        }
        
        public MutableBlockPos set(final Vec3i fs) {
            return this.set(fs.getX(), fs.getY(), fs.getZ());
        }
        
        public MutableBlockPos set(final long long1) {
            return this.set(BlockPos.getX(long1), BlockPos.getY(long1), BlockPos.getZ(long1));
        }
        
        public MutableBlockPos set(final AxisCycle ev, final int integer2, final int integer3, final int integer4) {
            return this.set(ev.cycle(integer2, integer3, integer4, Direction.Axis.X), ev.cycle(integer2, integer3, integer4, Direction.Axis.Y), ev.cycle(integer2, integer3, integer4, Direction.Axis.Z));
        }
        
        public MutableBlockPos move(final Direction fb) {
            return this.move(fb, 1);
        }
        
        public MutableBlockPos move(final Direction fb, final int integer) {
            return this.set(this.x + fb.getStepX() * integer, this.y + fb.getStepY() * integer, this.z + fb.getStepZ() * integer);
        }
        
        public MutableBlockPos move(final int integer1, final int integer2, final int integer3) {
            return this.set(this.x + integer1, this.y + integer2, this.z + integer3);
        }
        
        public void setX(final int integer) {
            this.x = integer;
        }
        
        public void setY(final int integer) {
            this.y = integer;
        }
        
        public void setZ(final int integer) {
            this.z = integer;
        }
        
        @Override
        public BlockPos immutable() {
            return new BlockPos(this);
        }
    }
    
    public static final class PooledMutableBlockPos extends MutableBlockPos implements AutoCloseable {
        private boolean free;
        private static final List<PooledMutableBlockPos> POOL;
        
        private PooledMutableBlockPos(final int integer1, final int integer2, final int integer3) {
            super(integer1, integer2, integer3);
        }
        
        public static PooledMutableBlockPos acquire() {
            return acquire(0, 0, 0);
        }
        
        public static PooledMutableBlockPos acquire(final Entity aio) {
            return acquire(aio.x, aio.y, aio.z);
        }
        
        public static PooledMutableBlockPos acquire(final double double1, final double double2, final double double3) {
            return acquire(Mth.floor(double1), Mth.floor(double2), Mth.floor(double3));
        }
        
        public static PooledMutableBlockPos acquire(final int integer1, final int integer2, final int integer3) {
            synchronized (PooledMutableBlockPos.POOL) {
                if (!PooledMutableBlockPos.POOL.isEmpty()) {
                    final PooledMutableBlockPos b5 = (PooledMutableBlockPos)PooledMutableBlockPos.POOL.remove(PooledMutableBlockPos.POOL.size() - 1);
                    if (b5 != null && b5.free) {
                        b5.free = false;
                        b5.set(integer1, integer2, integer3);
                        return b5;
                    }
                }
            }
            return new PooledMutableBlockPos(integer1, integer2, integer3);
        }
        
        @Override
        public PooledMutableBlockPos set(final int integer1, final int integer2, final int integer3) {
            return (PooledMutableBlockPos)super.set(integer1, integer2, integer3);
        }
        
        @Override
        public PooledMutableBlockPos set(final Entity aio) {
            return (PooledMutableBlockPos)super.set(aio);
        }
        
        @Override
        public PooledMutableBlockPos set(final double double1, final double double2, final double double3) {
            return (PooledMutableBlockPos)super.set(double1, double2, double3);
        }
        
        @Override
        public PooledMutableBlockPos set(final Vec3i fs) {
            return (PooledMutableBlockPos)super.set(fs);
        }
        
        @Override
        public PooledMutableBlockPos move(final Direction fb) {
            return (PooledMutableBlockPos)super.move(fb);
        }
        
        @Override
        public PooledMutableBlockPos move(final Direction fb, final int integer) {
            return (PooledMutableBlockPos)super.move(fb, integer);
        }
        
        @Override
        public PooledMutableBlockPos move(final int integer1, final int integer2, final int integer3) {
            return (PooledMutableBlockPos)super.move(integer1, integer2, integer3);
        }
        
        public void close() {
            synchronized (PooledMutableBlockPos.POOL) {
                if (PooledMutableBlockPos.POOL.size() < 100) {
                    PooledMutableBlockPos.POOL.add(this);
                }
                this.free = true;
            }
        }
        
        static {
            POOL = (List)Lists.newArrayList();
        }
    }
}
