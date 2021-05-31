package net.minecraft.world.level.lighting;

import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.core.Direction;

public abstract class LayerLightEngine<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> extends DynamicGraphMinFixedPoint implements LayerLightEventListener {
    private static final Direction[] DIRECTIONS;
    protected final LightChunkGetter chunkSource;
    protected final LightLayer layer;
    protected final S storage;
    private boolean runningLightUpdates;
    protected final BlockPos.MutableBlockPos pos;
    private final long[] lastChunkPos;
    private final BlockGetter[] lastChunk;
    
    public LayerLightEngine(final LightChunkGetter bxv, final LightLayer bia, final S cla) {
        super(16, 256, 8192);
        this.pos = new BlockPos.MutableBlockPos();
        this.lastChunkPos = new long[2];
        this.lastChunk = new BlockGetter[2];
        this.chunkSource = bxv;
        this.layer = bia;
        this.storage = cla;
        this.clearCache();
    }
    
    @Override
    protected void checkNode(final long long1) {
        this.storage.runAllUpdates();
        if (this.storage.storingLightForSection(SectionPos.blockToSection(long1))) {
            super.checkNode(long1);
        }
    }
    
    @Nullable
    private BlockGetter getChunk(final int integer1, final int integer2) {
        final long long4 = ChunkPos.asLong(integer1, integer2);
        for (int integer3 = 0; integer3 < 2; ++integer3) {
            if (long4 == this.lastChunkPos[integer3]) {
                return this.lastChunk[integer3];
            }
        }
        final BlockGetter bhb6 = this.chunkSource.getChunkForLighting(integer1, integer2);
        for (int integer4 = 1; integer4 > 0; --integer4) {
            this.lastChunkPos[integer4] = this.lastChunkPos[integer4 - 1];
            this.lastChunk[integer4] = this.lastChunk[integer4 - 1];
        }
        this.lastChunkPos[0] = long4;
        return this.lastChunk[0] = bhb6;
    }
    
    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill((Object[])this.lastChunk, null);
    }
    
    protected BlockState getStateAndOpacity(final long long1, @Nullable final AtomicInteger atomicInteger) {
        if (long1 == Long.MAX_VALUE) {
            if (atomicInteger != null) {
                atomicInteger.set(0);
            }
            return Blocks.AIR.defaultBlockState();
        }
        final int integer5 = SectionPos.blockToSectionCoord(BlockPos.getX(long1));
        final int integer6 = SectionPos.blockToSectionCoord(BlockPos.getZ(long1));
        final BlockGetter bhb7 = this.getChunk(integer5, integer6);
        if (bhb7 == null) {
            if (atomicInteger != null) {
                atomicInteger.set(16);
            }
            return Blocks.BEDROCK.defaultBlockState();
        }
        this.pos.set(long1);
        final BlockState bvt8 = bhb7.getBlockState(this.pos);
        final boolean boolean9 = bvt8.canOcclude() && bvt8.useShapeForLightOcclusion();
        if (atomicInteger != null) {
            atomicInteger.set(bvt8.getLightBlock(this.chunkSource.getLevel(), this.pos));
        }
        return boolean9 ? bvt8 : Blocks.AIR.defaultBlockState();
    }
    
    protected VoxelShape getShape(final BlockState bvt, final long long2, final Direction fb) {
        return bvt.canOcclude() ? bvt.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set(long2), fb) : Shapes.empty();
    }
    
    public static int getLightBlockInto(final BlockGetter bhb, final BlockState bvt2, final BlockPos ew3, final BlockState bvt4, final BlockPos ew5, final Direction fb, final int integer) {
        final boolean boolean8 = bvt2.canOcclude() && bvt2.useShapeForLightOcclusion();
        final boolean boolean9 = bvt4.canOcclude() && bvt4.useShapeForLightOcclusion();
        if (!boolean8 && !boolean9) {
            return integer;
        }
        final VoxelShape ctc10 = boolean8 ? bvt2.getOcclusionShape(bhb, ew3) : Shapes.empty();
        final VoxelShape ctc11 = boolean9 ? bvt4.getOcclusionShape(bhb, ew5) : Shapes.empty();
        if (Shapes.mergedFaceOccludes(ctc10, ctc11, fb)) {
            return 16;
        }
        return integer;
    }
    
    @Override
    protected boolean isSource(final long long1) {
        return long1 == Long.MAX_VALUE;
    }
    
    @Override
    protected int getComputedLevel(final long long1, final long long2, final int integer) {
        return 0;
    }
    
    @Override
    protected int getLevel(final long long1) {
        if (long1 == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - this.storage.getStoredLevel(long1);
    }
    
    protected int getLevel(final DataLayer bxn, final long long2) {
        return 15 - bxn.get(SectionPos.sectionRelative(BlockPos.getX(long2)), SectionPos.sectionRelative(BlockPos.getY(long2)), SectionPos.sectionRelative(BlockPos.getZ(long2)));
    }
    
    @Override
    protected void setLevel(final long long1, final int integer) {
        this.storage.setStoredLevel(long1, Math.min(15, 15 - integer));
    }
    
    @Override
    protected int computeLevelFromNeighbor(final long long1, final long long2, final int integer) {
        return 0;
    }
    
    public boolean hasLightWork() {
        return this.hasWork() || this.storage.hasWork() || this.storage.hasInconsistencies();
    }
    
    public int runUpdates(int integer, final boolean boolean2, final boolean boolean3) {
        if (!this.runningLightUpdates) {
            if (this.storage.hasWork()) {
                integer = this.storage.runUpdates(integer);
                if (integer == 0) {
                    return integer;
                }
            }
            this.storage.markNewInconsistencies(this, boolean2, boolean3);
        }
        this.runningLightUpdates = true;
        if (this.hasWork()) {
            integer = this.runUpdates(integer);
            this.clearCache();
            if (integer == 0) {
                return integer;
            }
        }
        this.runningLightUpdates = false;
        this.storage.swapSectionMap();
        return integer;
    }
    
    protected void queueSectionData(final long long1, @Nullable final DataLayer bxn) {
        this.storage.queueSectionData(long1, bxn);
    }
    
    @Nullable
    @Override
    public DataLayer getDataLayerData(final SectionPos fp) {
        return this.storage.getDataLayerData(fp.asLong());
    }
    
    @Override
    public int getLightValue(final BlockPos ew) {
        return this.storage.getLightValue(ew.asLong());
    }
    
    public String getDebugData(final long long1) {
        return new StringBuilder().append("").append(this.storage.getLevel(long1)).toString();
    }
    
    public void checkBlock(final BlockPos ew) {
        final long long3 = ew.asLong();
        this.checkNode(long3);
        for (final Direction fb8 : LayerLightEngine.DIRECTIONS) {
            this.checkNode(BlockPos.offset(long3, fb8));
        }
    }
    
    public void onBlockEmissionIncrease(final BlockPos ew, final int integer) {
    }
    
    public void updateSectionStatus(final SectionPos fp, final boolean boolean2) {
        this.storage.updateSectionStatus(fp.asLong(), boolean2);
    }
    
    public void enableLightSources(final ChunkPos bhd, final boolean boolean2) {
        final long long4 = SectionPos.getZeroNode(SectionPos.asLong(bhd.x, 0, bhd.z));
        this.storage.runAllUpdates();
        this.storage.enableLightSources(long4, boolean2);
    }
    
    public void retainData(final ChunkPos bhd, final boolean boolean2) {
        final long long4 = SectionPos.getZeroNode(SectionPos.asLong(bhd.x, 0, bhd.z));
        this.storage.retainData(long4, boolean2);
    }
    
    static {
        DIRECTIONS = Direction.values();
    }
}
