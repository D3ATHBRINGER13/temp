package net.minecraft.client.renderer.chunk;

import net.minecraft.client.Camera;
import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import java.util.Collection;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.client.Minecraft;
import java.util.Random;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GLX;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.level.BlockLayer;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;

public class RenderChunk {
    private volatile Level level;
    private final LevelRenderer renderer;
    public static int updateCounter;
    public CompiledChunk compiled;
    private final ReentrantLock taskLock;
    private final ReentrantLock compileLock;
    private ChunkCompileTask pendingTask;
    private final Set<BlockEntity> globalBlockEntities;
    private final VertexBuffer[] buffers;
    public AABB bb;
    private int lastFrame;
    private boolean dirty;
    private final BlockPos.MutableBlockPos origin;
    private final BlockPos.MutableBlockPos[] relativeOrigins;
    private boolean playerChanged;
    
    public RenderChunk(final Level bhr, final LevelRenderer dng) {
        this.compiled = CompiledChunk.UNCOMPILED;
        this.taskLock = new ReentrantLock();
        this.compileLock = new ReentrantLock();
        this.globalBlockEntities = (Set<BlockEntity>)Sets.newHashSet();
        this.buffers = new VertexBuffer[BlockLayer.values().length];
        this.lastFrame = -1;
        this.dirty = true;
        this.origin = new BlockPos.MutableBlockPos(-1, -1, -1);
        this.relativeOrigins = Util.<BlockPos.MutableBlockPos[]>make(new BlockPos.MutableBlockPos[6], (java.util.function.Consumer<BlockPos.MutableBlockPos[]>)(arr -> {
            for (int integer2 = 0; integer2 < arr.length; ++integer2) {
                arr[integer2] = new BlockPos.MutableBlockPos();
            }
        }));
        this.level = bhr;
        this.renderer = dng;
        if (GLX.useVbo()) {
            for (int integer4 = 0; integer4 < BlockLayer.values().length; ++integer4) {
                this.buffers[integer4] = new VertexBuffer(DefaultVertexFormat.BLOCK);
            }
        }
    }
    
    private static boolean doesChunkExistAt(final BlockPos ew, final Level bhr) {
        return !bhr.getChunk(ew.getX() >> 4, ew.getZ() >> 4).isEmpty();
    }
    
    public boolean hasAllNeighbors() {
        final int integer2 = 24;
        if (this.getDistToPlayerSqr() > 576.0) {
            final Level bhr3 = this.getLevel();
            return doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()], bhr3) && doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()], bhr3) && doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()], bhr3) && doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()], bhr3);
        }
        return true;
    }
    
    public boolean setFrame(final int integer) {
        if (this.lastFrame == integer) {
            return false;
        }
        this.lastFrame = integer;
        return true;
    }
    
    public VertexBuffer getBuffer(final int integer) {
        return this.buffers[integer];
    }
    
    public void setOrigin(final int integer1, final int integer2, final int integer3) {
        if (integer1 == this.origin.getX() && integer2 == this.origin.getY() && integer3 == this.origin.getZ()) {
            return;
        }
        this.reset();
        this.origin.set(integer1, integer2, integer3);
        this.bb = new AABB(integer1, integer2, integer3, integer1 + 16, integer2 + 16, integer3 + 16);
        for (final Direction fb8 : Direction.values()) {
            this.relativeOrigins[fb8.ordinal()].set(this.origin).move(fb8, 16);
        }
    }
    
    public void rebuildTransparent(final float float1, final float float2, final float float3, final ChunkCompileTask dpt) {
        final CompiledChunk dpw6 = dpt.getCompiledChunk();
        if (dpw6.getTransparencyState() == null || dpw6.isEmpty(BlockLayer.TRANSLUCENT)) {
            return;
        }
        this.beginLayer(dpt.getBuilders().builder(BlockLayer.TRANSLUCENT), this.origin);
        dpt.getBuilders().builder(BlockLayer.TRANSLUCENT).restoreState(dpw6.getTransparencyState());
        this.preEndLayer(BlockLayer.TRANSLUCENT, float1, float2, float3, dpt.getBuilders().builder(BlockLayer.TRANSLUCENT), dpw6);
    }
    
    public void compile(final float float1, final float float2, final float float3, final ChunkCompileTask dpt) {
        final CompiledChunk dpw6 = new CompiledChunk();
        final int integer7 = 1;
        final BlockPos ew8 = this.origin.immutable();
        final BlockPos ew9 = ew8.offset(15, 15, 15);
        final Level bhr10 = this.level;
        if (bhr10 == null) {
            return;
        }
        dpt.getStatusLock().lock();
        try {
            if (dpt.getStatus() != ChunkCompileTask.Status.COMPILING) {
                return;
            }
            dpt.setCompiledChunk(dpw6);
        }
        finally {
            dpt.getStatusLock().unlock();
        }
        final VisGraph dqb11 = new VisGraph();
        final Set<BlockEntity> set12 = (Set<BlockEntity>)Sets.newHashSet();
        final RenderChunkRegion dqa13 = dpt.takeRegion();
        if (dqa13 != null) {
            ++RenderChunk.updateCounter;
            final boolean[] arr14 = new boolean[BlockLayer.values().length];
            ModelBlockRenderer.enableCaching();
            final Random random15 = new Random();
            final BlockRenderDispatcher dnw16 = Minecraft.getInstance().getBlockRenderer();
            for (final BlockPos ew10 : BlockPos.betweenClosed(ew8, ew9)) {
                final BlockState bvt19 = dqa13.getBlockState(ew10);
                final Block bmv20 = bvt19.getBlock();
                if (bvt19.isSolidRender(dqa13, ew10)) {
                    dqb11.setOpaque(ew10);
                }
                if (bmv20.isEntityBlock()) {
                    final BlockEntity btw21 = dqa13.getBlockEntity(ew10, LevelChunk.EntityCreationType.CHECK);
                    if (btw21 != null) {
                        final BlockEntityRenderer<BlockEntity> dpe22 = BlockEntityRenderDispatcher.instance.<BlockEntity>getRenderer(btw21);
                        if (dpe22 != null) {
                            dpw6.addRenderableBlockEntity(btw21);
                            if (dpe22.shouldRenderOffScreen(btw21)) {
                                set12.add(btw21);
                            }
                        }
                    }
                }
                final FluidState clk21 = dqa13.getFluidState(ew10);
                if (!clk21.isEmpty()) {
                    final BlockLayer bhc22 = clk21.getRenderLayer();
                    final int integer8 = bhc22.ordinal();
                    final BufferBuilder cuw24 = dpt.getBuilders().builder(integer8);
                    if (!dpw6.hasLayer(bhc22)) {
                        dpw6.layerIsPresent(bhc22);
                        this.beginLayer(cuw24, ew8);
                    }
                    final boolean[] array = arr14;
                    final int n = integer8;
                    array[n] |= dnw16.renderLiquid(ew10, dqa13, cuw24, clk21);
                }
                if (bvt19.getRenderShape() != RenderShape.INVISIBLE) {
                    final BlockLayer bhc22 = bmv20.getRenderLayer();
                    final int integer8 = bhc22.ordinal();
                    final BufferBuilder cuw24 = dpt.getBuilders().builder(integer8);
                    if (!dpw6.hasLayer(bhc22)) {
                        dpw6.layerIsPresent(bhc22);
                        this.beginLayer(cuw24, ew8);
                    }
                    final boolean[] array2 = arr14;
                    final int n2 = integer8;
                    array2[n2] |= dnw16.renderBatched(bvt19, ew10, dqa13, cuw24, random15);
                }
            }
            for (final BlockLayer bhc23 : BlockLayer.values()) {
                if (arr14[bhc23.ordinal()]) {
                    dpw6.setChanged(bhc23);
                }
                if (dpw6.hasLayer(bhc23)) {
                    this.preEndLayer(bhc23, float1, float2, float3, dpt.getBuilders().builder(bhc23), dpw6);
                }
            }
            ModelBlockRenderer.clearCache();
        }
        dpw6.setVisibilitySet(dqb11.resolve());
        this.taskLock.lock();
        try {
            final Set<BlockEntity> set13 = (Set<BlockEntity>)Sets.newHashSet((Iterable)set12);
            final Set<BlockEntity> set14 = (Set<BlockEntity>)Sets.newHashSet((Iterable)this.globalBlockEntities);
            set13.removeAll((Collection)this.globalBlockEntities);
            set14.removeAll((Collection)set12);
            this.globalBlockEntities.clear();
            this.globalBlockEntities.addAll((Collection)set12);
            this.renderer.updateGlobalBlockEntities((Collection<BlockEntity>)set14, (Collection<BlockEntity>)set13);
        }
        finally {
            this.taskLock.unlock();
        }
    }
    
    protected void cancelCompile() {
        this.taskLock.lock();
        try {
            if (this.pendingTask != null && this.pendingTask.getStatus() != ChunkCompileTask.Status.DONE) {
                this.pendingTask.cancel();
                this.pendingTask = null;
            }
        }
        finally {
            this.taskLock.unlock();
        }
    }
    
    public ReentrantLock getTaskLock() {
        return this.taskLock;
    }
    
    public ChunkCompileTask createCompileTask() {
        this.taskLock.lock();
        try {
            this.cancelCompile();
            final BlockPos ew2 = this.origin.immutable();
            final int integer3 = 1;
            final RenderChunkRegion dqa4 = RenderChunkRegion.createIfNotEmpty(this.level, ew2.offset(-1, -1, -1), ew2.offset(16, 16, 16), 1);
            return this.pendingTask = new ChunkCompileTask(this, ChunkCompileTask.Type.REBUILD_CHUNK, this.getDistToPlayerSqr(), dqa4);
        }
        finally {
            this.taskLock.unlock();
        }
    }
    
    @Nullable
    public ChunkCompileTask createTransparencySortTask() {
        this.taskLock.lock();
        try {
            if (this.pendingTask != null && this.pendingTask.getStatus() == ChunkCompileTask.Status.PENDING) {
                return null;
            }
            if (this.pendingTask != null && this.pendingTask.getStatus() != ChunkCompileTask.Status.DONE) {
                this.pendingTask.cancel();
                this.pendingTask = null;
            }
            (this.pendingTask = new ChunkCompileTask(this, ChunkCompileTask.Type.RESORT_TRANSPARENCY, this.getDistToPlayerSqr(), null)).setCompiledChunk(this.compiled);
            return this.pendingTask;
        }
        finally {
            this.taskLock.unlock();
        }
    }
    
    protected double getDistToPlayerSqr() {
        final Camera cxq2 = Minecraft.getInstance().gameRenderer.getMainCamera();
        final double double3 = this.bb.minX + 8.0 - cxq2.getPosition().x;
        final double double4 = this.bb.minY + 8.0 - cxq2.getPosition().y;
        final double double5 = this.bb.minZ + 8.0 - cxq2.getPosition().z;
        return double3 * double3 + double4 * double4 + double5 * double5;
    }
    
    private void beginLayer(final BufferBuilder cuw, final BlockPos ew) {
        cuw.begin(7, DefaultVertexFormat.BLOCK);
        cuw.offset(-ew.getX(), -ew.getY(), -ew.getZ());
    }
    
    private void preEndLayer(final BlockLayer bhc, final float float2, final float float3, final float float4, final BufferBuilder cuw, final CompiledChunk dpw) {
        if (bhc == BlockLayer.TRANSLUCENT && !dpw.isEmpty(bhc)) {
            cuw.sortQuads(float2, float3, float4);
            dpw.setTransparencyState(cuw.getState());
        }
        cuw.end();
    }
    
    public CompiledChunk getCompiledChunk() {
        return this.compiled;
    }
    
    public void setCompiledChunk(final CompiledChunk dpw) {
        this.compileLock.lock();
        try {
            this.compiled = dpw;
        }
        finally {
            this.compileLock.unlock();
        }
    }
    
    public void reset() {
        this.cancelCompile();
        this.compiled = CompiledChunk.UNCOMPILED;
        this.dirty = true;
    }
    
    public void releaseBuffers() {
        this.reset();
        this.level = null;
        for (int integer2 = 0; integer2 < BlockLayer.values().length; ++integer2) {
            if (this.buffers[integer2] != null) {
                this.buffers[integer2].delete();
            }
        }
    }
    
    public BlockPos getOrigin() {
        return this.origin;
    }
    
    public void setDirty(boolean boolean1) {
        if (this.dirty) {
            boolean1 |= this.playerChanged;
        }
        this.dirty = true;
        this.playerChanged = boolean1;
    }
    
    public void setNotDirty() {
        this.dirty = false;
        this.playerChanged = false;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public boolean isDirtyFromPlayer() {
        return this.dirty && this.playerChanged;
    }
    
    public BlockPos getRelativeOrigin(final Direction fb) {
        return this.relativeOrigins[fb.ordinal()];
    }
    
    public Level getLevel() {
        return this.level;
    }
}
