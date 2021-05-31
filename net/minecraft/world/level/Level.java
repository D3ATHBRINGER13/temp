package net.minecraft.world.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.entity.EntityType;
import java.util.function.Predicate;
import java.io.IOException;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import java.util.function.Consumer;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import org.apache.logging.log4j.util.Supplier;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Lists;
import java.util.function.BiFunction;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import java.util.Random;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;
import net.minecraft.core.Direction;
import org.apache.logging.log4j.Logger;

public abstract class Level implements BlockAndBiomeGetter, LevelAccessor, AutoCloseable {
    protected static final Logger LOGGER;
    private static final Direction[] DIRECTIONS;
    public final List<BlockEntity> blockEntityList;
    public final List<BlockEntity> tickableBlockEntities;
    protected final List<BlockEntity> pendingBlockEntities;
    protected final List<BlockEntity> blockEntitiesToUnload;
    private final long cloudColor = 16777215L;
    private final Thread thread;
    private int skyDarken;
    protected int randValue;
    protected final int addend = 1013904223;
    protected float oRainLevel;
    protected float rainLevel;
    protected float oThunderLevel;
    protected float thunderLevel;
    private int skyFlashTime;
    public final Random random;
    public final Dimension dimension;
    protected final ChunkSource chunkSource;
    protected final LevelData levelData;
    private final ProfilerFiller profiler;
    public final boolean isClientSide;
    protected boolean updatingBlockEntities;
    private final WorldBorder worldBorder;
    
    protected Level(final LevelData com, final DimensionType byn, final BiFunction<Level, Dimension, ChunkSource> biFunction, final ProfilerFiller agn, final boolean boolean5) {
        this.blockEntityList = (List<BlockEntity>)Lists.newArrayList();
        this.tickableBlockEntities = (List<BlockEntity>)Lists.newArrayList();
        this.pendingBlockEntities = (List<BlockEntity>)Lists.newArrayList();
        this.blockEntitiesToUnload = (List<BlockEntity>)Lists.newArrayList();
        this.randValue = new Random().nextInt();
        this.random = new Random();
        this.profiler = agn;
        this.levelData = com;
        this.dimension = byn.create(this);
        this.chunkSource = (ChunkSource)biFunction.apply(this, this.dimension);
        this.isClientSide = boolean5;
        this.worldBorder = this.dimension.createWorldBorder();
        this.thread = Thread.currentThread();
    }
    
    public Biome getBiome(final BlockPos ew) {
        final ChunkSource bxl3 = this.getChunkSource();
        final LevelChunk bxt4 = bxl3.getChunk(ew.getX() >> 4, ew.getZ() >> 4, false);
        if (bxt4 != null) {
            return bxt4.getBiome(ew);
        }
        final ChunkGenerator<?> bxi5 = this.getChunkSource().getGenerator();
        if (bxi5 == null) {
            return Biomes.PLAINS;
        }
        return bxi5.getBiomeSource().getBiome(ew);
    }
    
    public boolean isClientSide() {
        return this.isClientSide;
    }
    
    @Nullable
    public MinecraftServer getServer() {
        return null;
    }
    
    public void validateSpawn() {
        this.setSpawnPos(new BlockPos(8, 64, 8));
    }
    
    public BlockState getTopBlockState(final BlockPos ew) {
        BlockPos ew2;
        for (ew2 = new BlockPos(ew.getX(), this.getSeaLevel(), ew.getZ()); !this.isEmptyBlock(ew2.above()); ew2 = ew2.above()) {}
        return this.getBlockState(ew2);
    }
    
    public static boolean isInWorldBounds(final BlockPos ew) {
        return !isOutsideBuildHeight(ew) && ew.getX() >= -30000000 && ew.getZ() >= -30000000 && ew.getX() < 30000000 && ew.getZ() < 30000000;
    }
    
    public static boolean isOutsideBuildHeight(final BlockPos ew) {
        return isOutsideBuildHeight(ew.getY());
    }
    
    public static boolean isOutsideBuildHeight(final int integer) {
        return integer < 0 || integer >= 256;
    }
    
    public LevelChunk getChunkAt(final BlockPos ew) {
        return this.getChunk(ew.getX() >> 4, ew.getZ() >> 4);
    }
    
    public LevelChunk getChunk(final int integer1, final int integer2) {
        return (LevelChunk)this.getChunk(integer1, integer2, ChunkStatus.FULL);
    }
    
    public ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        final ChunkAccess bxh6 = this.chunkSource.getChunk(integer1, integer2, bxm, boolean4);
        if (bxh6 == null && boolean4) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return bxh6;
    }
    
    public boolean setBlock(final BlockPos ew, final BlockState bvt, final int integer) {
        if (isOutsideBuildHeight(ew)) {
            return false;
        }
        if (!this.isClientSide && this.levelData.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        }
        final LevelChunk bxt5 = this.getChunkAt(ew);
        final Block bmv6 = bvt.getBlock();
        final BlockState bvt2 = bxt5.setBlockState(ew, bvt, (integer & 0x40) != 0x0);
        if (bvt2 != null) {
            final BlockState bvt3 = this.getBlockState(ew);
            if (bvt3 != bvt2 && (bvt3.getLightBlock(this, ew) != bvt2.getLightBlock(this, ew) || bvt3.getLightEmission() != bvt2.getLightEmission() || bvt3.useShapeForLightOcclusion() || bvt2.useShapeForLightOcclusion())) {
                this.profiler.push("queueCheckLight");
                this.getChunkSource().getLightEngine().checkBlock(ew);
                this.profiler.pop();
            }
            if (bvt3 == bvt) {
                if (bvt2 != bvt3) {
                    this.setBlocksDirty(ew, bvt2, bvt3);
                }
                if ((integer & 0x2) != 0x0 && (!this.isClientSide || (integer & 0x4) == 0x0) && (this.isClientSide || (bxt5.getFullStatus() != null && bxt5.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING)))) {
                    this.sendBlockUpdated(ew, bvt2, bvt, integer);
                }
                if (!this.isClientSide && (integer & 0x1) != 0x0) {
                    this.blockUpdated(ew, bvt2.getBlock());
                    if (bvt.hasAnalogOutputSignal()) {
                        this.updateNeighbourForOutputSignal(ew, bmv6);
                    }
                }
                if ((integer & 0x10) == 0x0) {
                    final int integer2 = integer & 0xFFFFFFFE;
                    bvt2.updateIndirectNeighbourShapes(this, ew, integer2);
                    bvt.updateNeighbourShapes(this, ew, integer2);
                    bvt.updateIndirectNeighbourShapes(this, ew, integer2);
                }
                this.onBlockStateChange(ew, bvt2, bvt3);
            }
            return true;
        }
        return false;
    }
    
    public void onBlockStateChange(final BlockPos ew, final BlockState bvt2, final BlockState bvt3) {
    }
    
    public boolean removeBlock(final BlockPos ew, final boolean boolean2) {
        final FluidState clk4 = this.getFluidState(ew);
        return this.setBlock(ew, clk4.createLegacyBlock(), 0x3 | (boolean2 ? 64 : 0));
    }
    
    public boolean destroyBlock(final BlockPos ew, final boolean boolean2) {
        final BlockState bvt4 = this.getBlockState(ew);
        if (bvt4.isAir()) {
            return false;
        }
        final FluidState clk5 = this.getFluidState(ew);
        this.levelEvent(2001, ew, Block.getId(bvt4));
        if (boolean2) {
            final BlockEntity btw6 = bvt4.getBlock().isEntityBlock() ? this.getBlockEntity(ew) : null;
            Block.dropResources(bvt4, this, ew, btw6);
        }
        return this.setBlock(ew, clk5.createLegacyBlock(), 3);
    }
    
    public boolean setBlockAndUpdate(final BlockPos ew, final BlockState bvt) {
        return this.setBlock(ew, bvt, 3);
    }
    
    public abstract void sendBlockUpdated(final BlockPos ew, final BlockState bvt2, final BlockState bvt3, final int integer);
    
    public void blockUpdated(final BlockPos ew, final Block bmv) {
        if (this.levelData.getGeneratorType() != LevelType.DEBUG_ALL_BLOCK_STATES) {
            this.updateNeighborsAt(ew, bmv);
        }
    }
    
    public void setBlocksDirty(final BlockPos ew, final BlockState bvt2, final BlockState bvt3) {
    }
    
    public void updateNeighborsAt(final BlockPos ew, final Block bmv) {
        this.neighborChanged(ew.west(), bmv, ew);
        this.neighborChanged(ew.east(), bmv, ew);
        this.neighborChanged(ew.below(), bmv, ew);
        this.neighborChanged(ew.above(), bmv, ew);
        this.neighborChanged(ew.north(), bmv, ew);
        this.neighborChanged(ew.south(), bmv, ew);
    }
    
    public void updateNeighborsAtExceptFromFacing(final BlockPos ew, final Block bmv, final Direction fb) {
        if (fb != Direction.WEST) {
            this.neighborChanged(ew.west(), bmv, ew);
        }
        if (fb != Direction.EAST) {
            this.neighborChanged(ew.east(), bmv, ew);
        }
        if (fb != Direction.DOWN) {
            this.neighborChanged(ew.below(), bmv, ew);
        }
        if (fb != Direction.UP) {
            this.neighborChanged(ew.above(), bmv, ew);
        }
        if (fb != Direction.NORTH) {
            this.neighborChanged(ew.north(), bmv, ew);
        }
        if (fb != Direction.SOUTH) {
            this.neighborChanged(ew.south(), bmv, ew);
        }
    }
    
    public void neighborChanged(final BlockPos ew1, final Block bmv, final BlockPos ew3) {
        if (this.isClientSide) {
            return;
        }
        final BlockState bvt5 = this.getBlockState(ew1);
        try {
            bvt5.neighborChanged(this, ew1, bmv, ew3, false);
        }
        catch (Throwable throwable6) {
            final CrashReport d7 = CrashReport.forThrowable(throwable6, "Exception while updating neighbours");
            final CrashReportCategory e8 = d7.addCategory("Block being updated");
            e8.setDetail("Source block type", (CrashReportDetail<String>)(() -> {
                try {
                    return String.format("ID #%s (%s // %s)", new Object[] { Registry.BLOCK.getKey(bmv), bmv.getDescriptionId(), bmv.getClass().getCanonicalName() });
                }
                catch (Throwable throwable2) {
                    return new StringBuilder().append("ID #").append(Registry.BLOCK.getKey(bmv)).toString();
                }
            }));
            CrashReportCategory.populateBlockDetails(e8, ew1, bvt5);
            throw new ReportedException(d7);
        }
    }
    
    public int getRawBrightness(BlockPos ew, final int integer) {
        if (ew.getX() < -30000000 || ew.getZ() < -30000000 || ew.getX() >= 30000000 || ew.getZ() >= 30000000) {
            return 15;
        }
        if (ew.getY() < 0) {
            return 0;
        }
        if (ew.getY() >= 256) {
            ew = new BlockPos(ew.getX(), 255, ew.getZ());
        }
        return this.getChunkAt(ew).getRawBrightness(ew, integer);
    }
    
    public int getHeight(final Heightmap.Types a, final int integer2, final int integer3) {
        int integer4;
        if (integer2 < -30000000 || integer3 < -30000000 || integer2 >= 30000000 || integer3 >= 30000000) {
            integer4 = this.getSeaLevel() + 1;
        }
        else if (this.hasChunk(integer2 >> 4, integer3 >> 4)) {
            integer4 = this.getChunk(integer2 >> 4, integer3 >> 4).getHeight(a, integer2 & 0xF, integer3 & 0xF) + 1;
        }
        else {
            integer4 = 0;
        }
        return integer4;
    }
    
    public int getBrightness(final LightLayer bia, final BlockPos ew) {
        return this.getChunkSource().getLightEngine().getLayerListener(bia).getLightValue(ew);
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        if (isOutsideBuildHeight(ew)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        final LevelChunk bxt3 = this.getChunk(ew.getX() >> 4, ew.getZ() >> 4);
        return bxt3.getBlockState(ew);
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        if (isOutsideBuildHeight(ew)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        final LevelChunk bxt3 = this.getChunkAt(ew);
        return bxt3.getFluidState(ew);
    }
    
    public boolean isDay() {
        return this.skyDarken < 4;
    }
    
    public void playSound(@Nullable final Player awg, final BlockPos ew, final SoundEvent yo, final SoundSource yq, final float float5, final float float6) {
        this.playSound(awg, ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, yo, yq, float5, float6);
    }
    
    public abstract void playSound(@Nullable final Player awg, final double double2, final double double3, final double double4, final SoundEvent yo, final SoundSource yq, final float float7, final float float8);
    
    public abstract void playSound(@Nullable final Player awg, final Entity aio, final SoundEvent yo, final SoundSource yq, final float float5, final float float6);
    
    public void playLocalSound(final double double1, final double double2, final double double3, final SoundEvent yo, final SoundSource yq, final float float6, final float float7, final boolean boolean8) {
    }
    
    public void addParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
    }
    
    public void addParticle(final ParticleOptions gf, final boolean boolean2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
    }
    
    public void addAlwaysVisibleParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
    }
    
    public void addAlwaysVisibleParticle(final ParticleOptions gf, final boolean boolean2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
    }
    
    public float getSkyDarken(final float float1) {
        final float float2 = this.getTimeOfDay(float1);
        float float3 = 1.0f - (Mth.cos(float2 * 6.2831855f) * 2.0f + 0.2f);
        float3 = Mth.clamp(float3, 0.0f, 1.0f);
        float3 = 1.0f - float3;
        float3 *= (float)(1.0 - this.getRainLevel(float1) * 5.0f / 16.0);
        float3 *= (float)(1.0 - this.getThunderLevel(float1) * 5.0f / 16.0);
        return float3 * 0.8f + 0.2f;
    }
    
    public Vec3 getSkyColor(final BlockPos ew, final float float2) {
        final float float3 = this.getTimeOfDay(float2);
        float float4 = Mth.cos(float3 * 6.2831855f) * 2.0f + 0.5f;
        float4 = Mth.clamp(float4, 0.0f, 1.0f);
        final Biome bio6 = this.getBiome(ew);
        final float float5 = bio6.getTemperature(ew);
        final int integer8 = bio6.getSkyColor(float5);
        float float6 = (integer8 >> 16 & 0xFF) / 255.0f;
        float float7 = (integer8 >> 8 & 0xFF) / 255.0f;
        float float8 = (integer8 & 0xFF) / 255.0f;
        float6 *= float4;
        float7 *= float4;
        float8 *= float4;
        final float float9 = this.getRainLevel(float2);
        if (float9 > 0.0f) {
            final float float10 = (float6 * 0.3f + float7 * 0.59f + float8 * 0.11f) * 0.6f;
            final float float11 = 1.0f - float9 * 0.75f;
            float6 = float6 * float11 + float10 * (1.0f - float11);
            float7 = float7 * float11 + float10 * (1.0f - float11);
            float8 = float8 * float11 + float10 * (1.0f - float11);
        }
        final float float10 = this.getThunderLevel(float2);
        if (float10 > 0.0f) {
            final float float11 = (float6 * 0.3f + float7 * 0.59f + float8 * 0.11f) * 0.2f;
            final float float12 = 1.0f - float10 * 0.75f;
            float6 = float6 * float12 + float11 * (1.0f - float12);
            float7 = float7 * float12 + float11 * (1.0f - float12);
            float8 = float8 * float12 + float11 * (1.0f - float12);
        }
        if (this.skyFlashTime > 0) {
            float float11 = this.skyFlashTime - float2;
            if (float11 > 1.0f) {
                float11 = 1.0f;
            }
            float11 *= 0.45f;
            float6 = float6 * (1.0f - float11) + 0.8f * float11;
            float7 = float7 * (1.0f - float11) + 0.8f * float11;
            float8 = float8 * (1.0f - float11) + 1.0f * float11;
        }
        return new Vec3(float6, float7, float8);
    }
    
    public float getSunAngle(final float float1) {
        final float float2 = this.getTimeOfDay(float1);
        return float2 * 6.2831855f;
    }
    
    public Vec3 getCloudColor(final float float1) {
        final float float2 = this.getTimeOfDay(float1);
        float float3 = Mth.cos(float2 * 6.2831855f) * 2.0f + 0.5f;
        float3 = Mth.clamp(float3, 0.0f, 1.0f);
        float float4 = 1.0f;
        float float5 = 1.0f;
        float float6 = 1.0f;
        final float float7 = this.getRainLevel(float1);
        if (float7 > 0.0f) {
            final float float8 = (float4 * 0.3f + float5 * 0.59f + float6 * 0.11f) * 0.6f;
            final float float9 = 1.0f - float7 * 0.95f;
            float4 = float4 * float9 + float8 * (1.0f - float9);
            float5 = float5 * float9 + float8 * (1.0f - float9);
            float6 = float6 * float9 + float8 * (1.0f - float9);
        }
        float4 *= float3 * 0.9f + 0.1f;
        float5 *= float3 * 0.9f + 0.1f;
        float6 *= float3 * 0.85f + 0.15f;
        final float float8 = this.getThunderLevel(float1);
        if (float8 > 0.0f) {
            final float float9 = (float4 * 0.3f + float5 * 0.59f + float6 * 0.11f) * 0.2f;
            final float float10 = 1.0f - float8 * 0.95f;
            float4 = float4 * float10 + float9 * (1.0f - float10);
            float5 = float5 * float10 + float9 * (1.0f - float10);
            float6 = float6 * float10 + float9 * (1.0f - float10);
        }
        return new Vec3(float4, float5, float6);
    }
    
    public Vec3 getFogColor(final float float1) {
        final float float2 = this.getTimeOfDay(float1);
        return this.dimension.getFogColor(float2, float1);
    }
    
    public float getStarBrightness(final float float1) {
        final float float2 = this.getTimeOfDay(float1);
        float float3 = 1.0f - (Mth.cos(float2 * 6.2831855f) * 2.0f + 0.25f);
        float3 = Mth.clamp(float3, 0.0f, 1.0f);
        return float3 * float3 * 0.5f;
    }
    
    public boolean addBlockEntity(final BlockEntity btw) {
        if (this.updatingBlockEntities) {
            Level.LOGGER.error("Adding block entity while ticking: {} @ {}", new Supplier[] { () -> Registry.BLOCK_ENTITY_TYPE.getKey(btw.getType()), btw::getBlockPos });
        }
        final boolean boolean3 = this.blockEntityList.add(btw);
        if (boolean3 && btw instanceof TickableBlockEntity) {
            this.tickableBlockEntities.add(btw);
        }
        if (this.isClientSide) {
            final BlockPos ew4 = btw.getBlockPos();
            final BlockState bvt5 = this.getBlockState(ew4);
            this.sendBlockUpdated(ew4, bvt5, bvt5, 2);
        }
        return boolean3;
    }
    
    public void addAllPendingBlockEntities(final Collection<BlockEntity> collection) {
        if (this.updatingBlockEntities) {
            this.pendingBlockEntities.addAll((Collection)collection);
        }
        else {
            for (final BlockEntity btw4 : collection) {
                this.addBlockEntity(btw4);
            }
        }
    }
    
    public void tickBlockEntities() {
        final ProfilerFiller agn2 = this.getProfiler();
        agn2.push("blockEntities");
        if (!this.blockEntitiesToUnload.isEmpty()) {
            this.tickableBlockEntities.removeAll((Collection)this.blockEntitiesToUnload);
            this.blockEntityList.removeAll((Collection)this.blockEntitiesToUnload);
            this.blockEntitiesToUnload.clear();
        }
        this.updatingBlockEntities = true;
        final Iterator<BlockEntity> iterator3 = (Iterator<BlockEntity>)this.tickableBlockEntities.iterator();
        while (iterator3.hasNext()) {
            final BlockEntity btw4 = (BlockEntity)iterator3.next();
            if (!btw4.isRemoved() && btw4.hasLevel()) {
                final BlockPos ew5 = btw4.getBlockPos();
                if (this.chunkSource.isTickingChunk(ew5) && this.getWorldBorder().isWithinBounds(ew5)) {
                    try {
                        agn2.push((java.util.function.Supplier<String>)(() -> String.valueOf(BlockEntityType.getKey(btw4.getType()))));
                        if (btw4.getType().isValid(this.getBlockState(ew5).getBlock())) {
                            ((TickableBlockEntity)btw4).tick();
                        }
                        else {
                            btw4.logInvalidState();
                        }
                        agn2.pop();
                    }
                    catch (Throwable throwable6) {
                        final CrashReport d7 = CrashReport.forThrowable(throwable6, "Ticking block entity");
                        final CrashReportCategory e8 = d7.addCategory("Block entity being ticked");
                        btw4.fillCrashReportCategory(e8);
                        throw new ReportedException(d7);
                    }
                }
            }
            if (btw4.isRemoved()) {
                iterator3.remove();
                this.blockEntityList.remove(btw4);
                if (!this.hasChunkAt(btw4.getBlockPos())) {
                    continue;
                }
                this.getChunkAt(btw4.getBlockPos()).removeBlockEntity(btw4.getBlockPos());
            }
        }
        this.updatingBlockEntities = false;
        agn2.popPush("pendingBlockEntities");
        if (!this.pendingBlockEntities.isEmpty()) {
            for (int integer4 = 0; integer4 < this.pendingBlockEntities.size(); ++integer4) {
                final BlockEntity btw5 = (BlockEntity)this.pendingBlockEntities.get(integer4);
                if (!btw5.isRemoved()) {
                    if (!this.blockEntityList.contains(btw5)) {
                        this.addBlockEntity(btw5);
                    }
                    if (this.hasChunkAt(btw5.getBlockPos())) {
                        final LevelChunk bxt6 = this.getChunkAt(btw5.getBlockPos());
                        final BlockState bvt7 = bxt6.getBlockState(btw5.getBlockPos());
                        bxt6.setBlockEntity(btw5.getBlockPos(), btw5);
                        this.sendBlockUpdated(btw5.getBlockPos(), bvt7, bvt7, 3);
                    }
                }
            }
            this.pendingBlockEntities.clear();
        }
        agn2.pop();
    }
    
    public void guardEntityTick(final Consumer<Entity> consumer, final Entity aio) {
        try {
            consumer.accept(aio);
        }
        catch (Throwable throwable4) {
            final CrashReport d5 = CrashReport.forThrowable(throwable4, "Ticking entity");
            final CrashReportCategory e6 = d5.addCategory("Entity being ticked");
            aio.fillCrashReportCategory(e6);
            throw new ReportedException(d5);
        }
    }
    
    public boolean containsAnyBlocks(final AABB csc) {
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
                        if (!bvt14.isAir()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsFireBlock(final AABB csc) {
        final int integer3 = Mth.floor(csc.minX);
        final int integer4 = Mth.ceil(csc.maxX);
        final int integer5 = Mth.floor(csc.minY);
        final int integer6 = Mth.ceil(csc.maxY);
        final int integer7 = Mth.floor(csc.minZ);
        final int integer8 = Mth.ceil(csc.maxZ);
        if (this.hasChunksAt(integer3, integer5, integer7, integer4, integer6, integer8)) {
            try (final BlockPos.PooledMutableBlockPos b9 = BlockPos.PooledMutableBlockPos.acquire()) {
                for (int integer9 = integer3; integer9 < integer4; ++integer9) {
                    for (int integer10 = integer5; integer10 < integer6; ++integer10) {
                        for (int integer11 = integer7; integer11 < integer8; ++integer11) {
                            final Block bmv14 = this.getBlockState(b9.set(integer9, integer10, integer11)).getBlock();
                            if (bmv14 == Blocks.FIRE || bmv14 == Blocks.LAVA) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Nullable
    public BlockState containsBlock(final AABB csc, final Block bmv) {
        final int integer4 = Mth.floor(csc.minX);
        final int integer5 = Mth.ceil(csc.maxX);
        final int integer6 = Mth.floor(csc.minY);
        final int integer7 = Mth.ceil(csc.maxY);
        final int integer8 = Mth.floor(csc.minZ);
        final int integer9 = Mth.ceil(csc.maxZ);
        if (this.hasChunksAt(integer4, integer6, integer8, integer5, integer7, integer9)) {
            try (final BlockPos.PooledMutableBlockPos b10 = BlockPos.PooledMutableBlockPos.acquire()) {
                for (int integer10 = integer4; integer10 < integer5; ++integer10) {
                    for (int integer11 = integer6; integer11 < integer7; ++integer11) {
                        for (int integer12 = integer8; integer12 < integer9; ++integer12) {
                            final BlockState bvt15 = this.getBlockState(b10.set(integer10, integer11, integer12));
                            if (bvt15.getBlock() == bmv) {
                                return bvt15;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public boolean containsMaterial(final AABB csc, final Material clo) {
        final int integer4 = Mth.floor(csc.minX);
        final int integer5 = Mth.ceil(csc.maxX);
        final int integer6 = Mth.floor(csc.minY);
        final int integer7 = Mth.ceil(csc.maxY);
        final int integer8 = Mth.floor(csc.minZ);
        final int integer9 = Mth.ceil(csc.maxZ);
        final BlockMaterialPredicate bwb10 = BlockMaterialPredicate.forMaterial(clo);
        return BlockPos.betweenClosedStream(integer4, integer6, integer8, integer5 - 1, integer7 - 1, integer9 - 1).anyMatch(ew -> bwb10.test(this.getBlockState(ew)));
    }
    
    public Explosion explode(@Nullable final Entity aio, final double double2, final double double3, final double double4, final float float5, final Explosion.BlockInteraction a) {
        return this.explode(aio, null, double2, double3, double4, float5, false, a);
    }
    
    public Explosion explode(@Nullable final Entity aio, final double double2, final double double3, final double double4, final float float5, final boolean boolean6, final Explosion.BlockInteraction a) {
        return this.explode(aio, null, double2, double3, double4, float5, boolean6, a);
    }
    
    public Explosion explode(@Nullable final Entity aio, @Nullable final DamageSource ahx, final double double3, final double double4, final double double5, final float float6, final boolean boolean7, final Explosion.BlockInteraction a) {
        final Explosion bhk13 = new Explosion(this, aio, double3, double4, double5, float6, boolean7, a);
        if (ahx != null) {
            bhk13.setDamageSource(ahx);
        }
        bhk13.explode();
        bhk13.finalizeExplosion(true);
        return bhk13;
    }
    
    public boolean extinguishFire(@Nullable final Player awg, BlockPos ew, final Direction fb) {
        ew = ew.relative(fb);
        if (this.getBlockState(ew).getBlock() == Blocks.FIRE) {
            this.levelEvent(awg, 1009, ew, 0);
            this.removeBlock(ew, false);
            return true;
        }
        return false;
    }
    
    public String gatherChunkSourceStats() {
        return this.chunkSource.gatherStats();
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        if (isOutsideBuildHeight(ew)) {
            return null;
        }
        if (!this.isClientSide && Thread.currentThread() != this.thread) {
            return null;
        }
        BlockEntity btw3 = null;
        if (this.updatingBlockEntities) {
            btw3 = this.getPendingBlockEntityAt(ew);
        }
        if (btw3 == null) {
            btw3 = this.getChunkAt(ew).getBlockEntity(ew, LevelChunk.EntityCreationType.IMMEDIATE);
        }
        if (btw3 == null) {
            btw3 = this.getPendingBlockEntityAt(ew);
        }
        return btw3;
    }
    
    @Nullable
    private BlockEntity getPendingBlockEntityAt(final BlockPos ew) {
        for (int integer3 = 0; integer3 < this.pendingBlockEntities.size(); ++integer3) {
            final BlockEntity btw4 = (BlockEntity)this.pendingBlockEntities.get(integer3);
            if (!btw4.isRemoved() && btw4.getBlockPos().equals(ew)) {
                return btw4;
            }
        }
        return null;
    }
    
    public void setBlockEntity(final BlockPos ew, @Nullable final BlockEntity btw) {
        if (isOutsideBuildHeight(ew)) {
            return;
        }
        if (btw != null && !btw.isRemoved()) {
            if (this.updatingBlockEntities) {
                btw.setPosition(ew);
                final Iterator<BlockEntity> iterator4 = (Iterator<BlockEntity>)this.pendingBlockEntities.iterator();
                while (iterator4.hasNext()) {
                    final BlockEntity btw2 = (BlockEntity)iterator4.next();
                    if (btw2.getBlockPos().equals(ew)) {
                        btw2.setRemoved();
                        iterator4.remove();
                    }
                }
                this.pendingBlockEntities.add(btw);
            }
            else {
                this.getChunkAt(ew).setBlockEntity(ew, btw);
                this.addBlockEntity(btw);
            }
        }
    }
    
    public void removeBlockEntity(final BlockPos ew) {
        final BlockEntity btw3 = this.getBlockEntity(ew);
        if (btw3 != null && this.updatingBlockEntities) {
            btw3.setRemoved();
            this.pendingBlockEntities.remove(btw3);
        }
        else {
            if (btw3 != null) {
                this.pendingBlockEntities.remove(btw3);
                this.blockEntityList.remove(btw3);
                this.tickableBlockEntities.remove(btw3);
            }
            this.getChunkAt(ew).removeBlockEntity(ew);
        }
    }
    
    public boolean isLoaded(final BlockPos ew) {
        return !isOutsideBuildHeight(ew) && this.chunkSource.hasChunk(ew.getX() >> 4, ew.getZ() >> 4);
    }
    
    public boolean loadedAndEntityCanStandOn(final BlockPos ew, final Entity aio) {
        if (isOutsideBuildHeight(ew)) {
            return false;
        }
        final ChunkAccess bxh4 = this.getChunk(ew.getX() >> 4, ew.getZ() >> 4, ChunkStatus.FULL, false);
        return bxh4 != null && bxh4.getBlockState(ew).entityCanStandOn(this, ew, aio);
    }
    
    public void updateSkyBrightness() {
        final double double2 = 1.0 - this.getRainLevel(1.0f) * 5.0f / 16.0;
        final double double3 = 1.0 - this.getThunderLevel(1.0f) * 5.0f / 16.0;
        final double double4 = 0.5 + 2.0 * Mth.clamp(Mth.cos(this.getTimeOfDay(1.0f) * 6.2831855f), -0.25, 0.25);
        this.skyDarken = (int)((1.0 - double4 * double2 * double3) * 11.0);
    }
    
    public void setSpawnSettings(final boolean boolean1, final boolean boolean2) {
        this.getChunkSource().setSpawnSettings(boolean1, boolean2);
    }
    
    protected void prepareWeather() {
        if (this.levelData.isRaining()) {
            this.rainLevel = 1.0f;
            if (this.levelData.isThundering()) {
                this.thunderLevel = 1.0f;
            }
        }
    }
    
    public void close() throws IOException {
        this.chunkSource.close();
    }
    
    public ChunkStatus statusForCollisions() {
        return ChunkStatus.FULL;
    }
    
    public List<Entity> getEntities(@Nullable final Entity aio, final AABB csc, @Nullable final Predicate<? super Entity> predicate) {
        final List<Entity> list5 = (List<Entity>)Lists.newArrayList();
        final int integer6 = Mth.floor((csc.minX - 2.0) / 16.0);
        final int integer7 = Mth.floor((csc.maxX + 2.0) / 16.0);
        final int integer8 = Mth.floor((csc.minZ - 2.0) / 16.0);
        final int integer9 = Mth.floor((csc.maxZ + 2.0) / 16.0);
        for (int integer10 = integer6; integer10 <= integer7; ++integer10) {
            for (int integer11 = integer8; integer11 <= integer9; ++integer11) {
                final LevelChunk bxt12 = this.getChunkSource().getChunk(integer10, integer11, false);
                if (bxt12 != null) {
                    bxt12.getEntities(aio, csc, list5, predicate);
                }
            }
        }
        return list5;
    }
    
    public List<Entity> getEntities(@Nullable final EntityType<?> ais, final AABB csc, final Predicate<? super Entity> predicate) {
        final int integer5 = Mth.floor((csc.minX - 2.0) / 16.0);
        final int integer6 = Mth.ceil((csc.maxX + 2.0) / 16.0);
        final int integer7 = Mth.floor((csc.minZ - 2.0) / 16.0);
        final int integer8 = Mth.ceil((csc.maxZ + 2.0) / 16.0);
        final List<Entity> list9 = (List<Entity>)Lists.newArrayList();
        for (int integer9 = integer5; integer9 < integer6; ++integer9) {
            for (int integer10 = integer7; integer10 < integer8; ++integer10) {
                final LevelChunk bxt12 = this.getChunkSource().getChunk(integer9, integer10, false);
                if (bxt12 != null) {
                    bxt12.getEntities(ais, csc, list9, predicate);
                }
            }
        }
        return list9;
    }
    
    public <T extends Entity> List<T> getEntitiesOfClass(final Class<? extends T> class1, final AABB csc, @Nullable final Predicate<? super T> predicate) {
        final int integer5 = Mth.floor((csc.minX - 2.0) / 16.0);
        final int integer6 = Mth.ceil((csc.maxX + 2.0) / 16.0);
        final int integer7 = Mth.floor((csc.minZ - 2.0) / 16.0);
        final int integer8 = Mth.ceil((csc.maxZ + 2.0) / 16.0);
        final List<T> list9 = (List<T>)Lists.newArrayList();
        final ChunkSource bxl10 = this.getChunkSource();
        for (int integer9 = integer5; integer9 < integer6; ++integer9) {
            for (int integer10 = integer7; integer10 < integer8; ++integer10) {
                final LevelChunk bxt13 = bxl10.getChunk(integer9, integer10, false);
                if (bxt13 != null) {
                    bxt13.<T>getEntitiesOfClass(class1, csc, list9, predicate);
                }
            }
        }
        return list9;
    }
    
    public <T extends Entity> List<T> getLoadedEntitiesOfClass(final Class<? extends T> class1, final AABB csc, @Nullable final Predicate<? super T> predicate) {
        final int integer5 = Mth.floor((csc.minX - 2.0) / 16.0);
        final int integer6 = Mth.ceil((csc.maxX + 2.0) / 16.0);
        final int integer7 = Mth.floor((csc.minZ - 2.0) / 16.0);
        final int integer8 = Mth.ceil((csc.maxZ + 2.0) / 16.0);
        final List<T> list9 = (List<T>)Lists.newArrayList();
        final ChunkSource bxl10 = this.getChunkSource();
        for (int integer9 = integer5; integer9 < integer6; ++integer9) {
            for (int integer10 = integer7; integer10 < integer8; ++integer10) {
                final LevelChunk bxt13 = bxl10.getChunkNow(integer9, integer10);
                if (bxt13 != null) {
                    bxt13.<T>getEntitiesOfClass(class1, csc, list9, predicate);
                }
            }
        }
        return list9;
    }
    
    @Nullable
    public abstract Entity getEntity(final int integer);
    
    public void blockEntityChanged(final BlockPos ew, final BlockEntity btw) {
        if (this.hasChunkAt(ew)) {
            this.getChunkAt(ew).markUnsaved();
        }
    }
    
    public int getSeaLevel() {
        return 63;
    }
    
    public Level getLevel() {
        return this;
    }
    
    public LevelType getGeneratorType() {
        return this.levelData.getGeneratorType();
    }
    
    public int getDirectSignalTo(final BlockPos ew) {
        int integer3 = 0;
        integer3 = Math.max(integer3, this.getDirectSignal(ew.below(), Direction.DOWN));
        if (integer3 >= 15) {
            return integer3;
        }
        integer3 = Math.max(integer3, this.getDirectSignal(ew.above(), Direction.UP));
        if (integer3 >= 15) {
            return integer3;
        }
        integer3 = Math.max(integer3, this.getDirectSignal(ew.north(), Direction.NORTH));
        if (integer3 >= 15) {
            return integer3;
        }
        integer3 = Math.max(integer3, this.getDirectSignal(ew.south(), Direction.SOUTH));
        if (integer3 >= 15) {
            return integer3;
        }
        integer3 = Math.max(integer3, this.getDirectSignal(ew.west(), Direction.WEST));
        if (integer3 >= 15) {
            return integer3;
        }
        integer3 = Math.max(integer3, this.getDirectSignal(ew.east(), Direction.EAST));
        if (integer3 >= 15) {
            return integer3;
        }
        return integer3;
    }
    
    public boolean hasSignal(final BlockPos ew, final Direction fb) {
        return this.getSignal(ew, fb) > 0;
    }
    
    public int getSignal(final BlockPos ew, final Direction fb) {
        final BlockState bvt4 = this.getBlockState(ew);
        if (bvt4.isRedstoneConductor(this, ew)) {
            return this.getDirectSignalTo(ew);
        }
        return bvt4.getSignal(this, ew, fb);
    }
    
    public boolean hasNeighborSignal(final BlockPos ew) {
        return this.getSignal(ew.below(), Direction.DOWN) > 0 || this.getSignal(ew.above(), Direction.UP) > 0 || this.getSignal(ew.north(), Direction.NORTH) > 0 || this.getSignal(ew.south(), Direction.SOUTH) > 0 || this.getSignal(ew.west(), Direction.WEST) > 0 || this.getSignal(ew.east(), Direction.EAST) > 0;
    }
    
    public int getBestNeighborSignal(final BlockPos ew) {
        int integer3 = 0;
        for (final Direction fb7 : Level.DIRECTIONS) {
            final int integer4 = this.getSignal(ew.relative(fb7), fb7);
            if (integer4 >= 15) {
                return 15;
            }
            if (integer4 > integer3) {
                integer3 = integer4;
            }
        }
        return integer3;
    }
    
    public void disconnect() {
    }
    
    public void setGameTime(final long long1) {
        this.levelData.setGameTime(long1);
    }
    
    public long getSeed() {
        return this.levelData.getSeed();
    }
    
    public long getGameTime() {
        return this.levelData.getGameTime();
    }
    
    public long getDayTime() {
        return this.levelData.getDayTime();
    }
    
    public void setDayTime(final long long1) {
        this.levelData.setDayTime(long1);
    }
    
    protected void tickTime() {
        this.setGameTime(this.levelData.getGameTime() + 1L);
        if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
        }
    }
    
    public BlockPos getSharedSpawnPos() {
        BlockPos ew2 = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
        if (!this.getWorldBorder().isWithinBounds(ew2)) {
            ew2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return ew2;
    }
    
    public void setSpawnPos(final BlockPos ew) {
        this.levelData.setSpawn(ew);
    }
    
    public boolean mayInteract(final Player awg, final BlockPos ew) {
        return true;
    }
    
    public void broadcastEntityEvent(final Entity aio, final byte byte2) {
    }
    
    public ChunkSource getChunkSource() {
        return this.chunkSource;
    }
    
    public void blockEvent(final BlockPos ew, final Block bmv, final int integer3, final int integer4) {
        this.getBlockState(ew).triggerEvent(this, ew, integer3, integer4);
    }
    
    public LevelData getLevelData() {
        return this.levelData;
    }
    
    public GameRules getGameRules() {
        return this.levelData.getGameRules();
    }
    
    public float getThunderLevel(final float float1) {
        return Mth.lerp(float1, this.oThunderLevel, this.thunderLevel) * this.getRainLevel(float1);
    }
    
    public void setThunderLevel(final float float1) {
        this.oThunderLevel = float1;
        this.thunderLevel = float1;
    }
    
    public float getRainLevel(final float float1) {
        return Mth.lerp(float1, this.oRainLevel, this.rainLevel);
    }
    
    public void setRainLevel(final float float1) {
        this.oRainLevel = float1;
        this.rainLevel = float1;
    }
    
    public boolean isThundering() {
        return this.dimension.isHasSkyLight() && !this.dimension.isHasCeiling() && this.getThunderLevel(1.0f) > 0.9;
    }
    
    public boolean isRaining() {
        return this.getRainLevel(1.0f) > 0.2;
    }
    
    public boolean isRainingAt(final BlockPos ew) {
        return this.isRaining() && this.canSeeSky(ew) && this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew).getY() <= ew.getY() && this.getBiome(ew).getPrecipitation() == Biome.Precipitation.RAIN;
    }
    
    public boolean isHumidAt(final BlockPos ew) {
        final Biome bio3 = this.getBiome(ew);
        return bio3.isHumid();
    }
    
    @Nullable
    public abstract MapItemSavedData getMapData(final String string);
    
    public abstract void setMapData(final MapItemSavedData coh);
    
    public abstract int getFreeMapId();
    
    public void globalLevelEvent(final int integer1, final BlockPos ew, final int integer3) {
    }
    
    public int getHeight() {
        return this.dimension.isHasCeiling() ? 128 : 256;
    }
    
    public double getHorizonHeight() {
        if (this.levelData.getGeneratorType() == LevelType.FLAT) {
            return 0.0;
        }
        return 63.0;
    }
    
    public CrashReportCategory fillReportDetails(final CrashReport d) {
        final CrashReportCategory e3 = d.addCategory("Affected level", 1);
        e3.setDetail("All players", (CrashReportDetail<String>)(() -> new StringBuilder().append(this.players().size()).append(" total; ").append(this.players()).toString()));
        e3.setDetail("Chunk stats", (CrashReportDetail<String>)this.chunkSource::gatherStats);
        e3.setDetail("Level dimension", (CrashReportDetail<String>)(() -> this.dimension.getType().toString()));
        try {
            this.levelData.fillCrashReportCategory(e3);
        }
        catch (Throwable throwable4) {
            e3.setDetailError("Level Data Unobtainable", throwable4);
        }
        return e3;
    }
    
    public abstract void destroyBlockProgress(final int integer1, final BlockPos ew, final int integer3);
    
    public void createFireworks(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6, @Nullable final CompoundTag id) {
    }
    
    public abstract Scoreboard getScoreboard();
    
    public void updateNeighbourForOutputSignal(final BlockPos ew, final Block bmv) {
        for (final Direction fb5 : Direction.Plane.HORIZONTAL) {
            BlockPos ew2 = ew.relative(fb5);
            if (this.hasChunkAt(ew2)) {
                BlockState bvt7 = this.getBlockState(ew2);
                if (bvt7.getBlock() == Blocks.COMPARATOR) {
                    bvt7.neighborChanged(this, ew2, bmv, ew, false);
                }
                else {
                    if (!bvt7.isRedstoneConductor(this, ew2)) {
                        continue;
                    }
                    ew2 = ew2.relative(fb5);
                    bvt7 = this.getBlockState(ew2);
                    if (bvt7.getBlock() != Blocks.COMPARATOR) {
                        continue;
                    }
                    bvt7.neighborChanged(this, ew2, bmv, ew, false);
                }
            }
        }
    }
    
    public DifficultyInstance getCurrentDifficultyAt(final BlockPos ew) {
        long long3 = 0L;
        float float5 = 0.0f;
        if (this.hasChunkAt(ew)) {
            float5 = this.getMoonBrightness();
            long3 = this.getChunkAt(ew).getInhabitedTime();
        }
        return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), long3, float5);
    }
    
    public int getSkyDarken() {
        return this.skyDarken;
    }
    
    public int getSkyFlashTime() {
        return this.skyFlashTime;
    }
    
    public void setSkyFlashTime(final int integer) {
        this.skyFlashTime = integer;
    }
    
    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }
    
    public void sendPacketToServer(final Packet<?> kc) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }
    
    @Nullable
    public BlockPos findNearestMapFeature(final String string, final BlockPos ew, final int integer, final boolean boolean4) {
        return null;
    }
    
    public Dimension getDimension() {
        return this.dimension;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    public boolean isStateAtPosition(final BlockPos ew, final Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(ew));
    }
    
    public abstract RecipeManager getRecipeManager();
    
    public abstract TagManager getTagManager();
    
    public BlockPos getBlockRandomPos(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.randValue = this.randValue * 3 + 1013904223;
        final int integer5 = this.randValue >> 2;
        return new BlockPos(integer1 + (integer5 & 0xF), integer2 + (integer5 >> 16 & integer4), integer3 + (integer5 >> 8 & 0xF));
    }
    
    public boolean noSave() {
        return false;
    }
    
    public ProfilerFiller getProfiler() {
        return this.profiler;
    }
    
    public BlockPos getHeightmapPos(final Heightmap.Types a, final BlockPos ew) {
        return new BlockPos(ew.getX(), this.getHeight(a, ew.getX(), ew.getZ()), ew.getZ());
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DIRECTIONS = Direction.values();
    }
}
