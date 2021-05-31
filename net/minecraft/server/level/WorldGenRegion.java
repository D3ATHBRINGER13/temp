package net.minecraft.server.level;

import org.apache.logging.log4j.LogManager;
import java.util.Collections;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import java.util.function.Function;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.dimension.Dimension;
import java.util.Random;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.LevelAccessor;

public class WorldGenRegion implements LevelAccessor {
    private static final Logger LOGGER;
    private final List<ChunkAccess> cache;
    private final int x;
    private final int z;
    private final int size;
    private final ServerLevel level;
    private final long seed;
    private final int seaLevel;
    private final LevelData levelData;
    private final Random random;
    private final Dimension dimension;
    private final ChunkGeneratorSettings settings;
    private final TickList<Block> blockTicks;
    private final TickList<Fluid> liquidTicks;
    
    public WorldGenRegion(final ServerLevel vk, final List<ChunkAccess> list) {
        this.blockTicks = new WorldGenTickList<Block>((java.util.function.Function<BlockPos, TickList<Block>>)(ew -> this.getChunk(ew).getBlockTicks()));
        this.liquidTicks = new WorldGenTickList<Fluid>((java.util.function.Function<BlockPos, TickList<Fluid>>)(ew -> this.getChunk(ew).getLiquidTicks()));
        final int integer4 = Mth.floor(Math.sqrt((double)list.size()));
        if (integer4 * integer4 != list.size()) {
            throw new IllegalStateException("Cache size is not a square.");
        }
        final ChunkPos bhd5 = ((ChunkAccess)list.get(list.size() / 2)).getPos();
        this.cache = list;
        this.x = bhd5.x;
        this.z = bhd5.z;
        this.size = integer4;
        this.level = vk;
        this.seed = vk.getSeed();
        this.settings = (ChunkGeneratorSettings)vk.getChunkSource().getGenerator().getSettings();
        this.seaLevel = vk.getSeaLevel();
        this.levelData = vk.getLevelData();
        this.random = vk.getRandom();
        this.dimension = vk.getDimension();
    }
    
    public int getCenterX() {
        return this.x;
    }
    
    public int getCenterZ() {
        return this.z;
    }
    
    public ChunkAccess getChunk(final int integer1, final int integer2) {
        return this.getChunk(integer1, integer2, ChunkStatus.EMPTY);
    }
    
    @Nullable
    public ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        ChunkAccess bxh6;
        if (this.hasChunk(integer1, integer2)) {
            final ChunkPos bhd7 = ((ChunkAccess)this.cache.get(0)).getPos();
            final int integer3 = integer1 - bhd7.x;
            final int integer4 = integer2 - bhd7.z;
            bxh6 = (ChunkAccess)this.cache.get(integer3 + integer4 * this.size);
            if (bxh6.getStatus().isOrAfter(bxm)) {
                return bxh6;
            }
        }
        else {
            bxh6 = null;
        }
        if (!boolean4) {
            return null;
        }
        final ChunkAccess bxh7 = (ChunkAccess)this.cache.get(0);
        final ChunkAccess bxh8 = (ChunkAccess)this.cache.get(this.cache.size() - 1);
        WorldGenRegion.LOGGER.error("Requested chunk : {} {}", integer1, integer2);
        WorldGenRegion.LOGGER.error("Region bounds : {} {} | {} {}", bxh7.getPos().x, bxh7.getPos().z, bxh8.getPos().x, bxh8.getPos().z);
        if (bxh6 != null) {
            throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", new Object[] { bxm, bxh6.getStatus(), integer1, integer2 }));
        }
        throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", new Object[] { integer1, integer2 }));
    }
    
    public boolean hasChunk(final int integer1, final int integer2) {
        final ChunkAccess bxh4 = (ChunkAccess)this.cache.get(0);
        final ChunkAccess bxh5 = (ChunkAccess)this.cache.get(this.cache.size() - 1);
        return integer1 >= bxh4.getPos().x && integer1 <= bxh5.getPos().x && integer2 >= bxh4.getPos().z && integer2 <= bxh5.getPos().z;
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        return this.getChunk(ew.getX() >> 4, ew.getZ() >> 4).getBlockState(ew);
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        return this.getChunk(ew).getFluidState(ew);
    }
    
    @Nullable
    public Player getNearestPlayer(final double double1, final double double2, final double double3, final double double4, final Predicate<Entity> predicate) {
        return null;
    }
    
    public int getSkyDarken() {
        return 0;
    }
    
    public Biome getBiome(final BlockPos ew) {
        final Biome bio3 = this.getChunk(ew).getBiomes()[(ew.getX() & 0xF) | (ew.getZ() & 0xF) << 4];
        if (bio3 == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", new Object[] { ew }));
        }
        return bio3;
    }
    
    public int getBrightness(final LightLayer bia, final BlockPos ew) {
        return this.getChunkSource().getLightEngine().getLayerListener(bia).getLightValue(ew);
    }
    
    public int getRawBrightness(final BlockPos ew, final int integer) {
        return this.getChunk(ew).getRawBrightness(ew, integer, this.getDimension().isHasSkyLight());
    }
    
    public boolean destroyBlock(final BlockPos ew, final boolean boolean2) {
        final BlockState bvt4 = this.getBlockState(ew);
        if (bvt4.isAir()) {
            return false;
        }
        if (boolean2) {
            final BlockEntity btw5 = bvt4.getBlock().isEntityBlock() ? this.getBlockEntity(ew) : null;
            Block.dropResources(bvt4, this.level, ew, btw5);
        }
        return this.setBlock(ew, Blocks.AIR.defaultBlockState(), 3);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        final ChunkAccess bxh3 = this.getChunk(ew);
        BlockEntity btw4 = bxh3.getBlockEntity(ew);
        if (btw4 != null) {
            return btw4;
        }
        final CompoundTag id5 = bxh3.getBlockEntityNbt(ew);
        if (id5 != null) {
            if ("DUMMY".equals(id5.getString("id"))) {
                final Block bmv6 = this.getBlockState(ew).getBlock();
                if (!(bmv6 instanceof EntityBlock)) {
                    return null;
                }
                btw4 = ((EntityBlock)bmv6).newBlockEntity(this.level);
            }
            else {
                btw4 = BlockEntity.loadStatic(id5);
            }
            if (btw4 != null) {
                bxh3.setBlockEntity(ew, btw4);
                return btw4;
            }
        }
        if (bxh3.getBlockState(ew).getBlock() instanceof EntityBlock) {
            WorldGenRegion.LOGGER.warn("Tried to access a block entity before it was created. {}", ew);
        }
        return null;
    }
    
    public boolean setBlock(final BlockPos ew, final BlockState bvt, final int integer) {
        final ChunkAccess bxh5 = this.getChunk(ew);
        final BlockState bvt2 = bxh5.setBlockState(ew, bvt, false);
        if (bvt2 != null) {
            this.level.onBlockStateChange(ew, bvt2, bvt);
        }
        final Block bmv7 = bvt.getBlock();
        if (bmv7.isEntityBlock()) {
            if (bxh5.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                bxh5.setBlockEntity(ew, ((EntityBlock)bmv7).newBlockEntity(this));
            }
            else {
                final CompoundTag id8 = new CompoundTag();
                id8.putInt("x", ew.getX());
                id8.putInt("y", ew.getY());
                id8.putInt("z", ew.getZ());
                id8.putString("id", "DUMMY");
                bxh5.setBlockEntityNbt(id8);
            }
        }
        else if (bvt2 != null && bvt2.getBlock().isEntityBlock()) {
            bxh5.removeBlockEntity(ew);
        }
        if (bvt.hasPostProcess(this, ew)) {
            this.markPosForPostprocessing(ew);
        }
        return true;
    }
    
    private void markPosForPostprocessing(final BlockPos ew) {
        this.getChunk(ew).markPosForPostprocessing(ew);
    }
    
    public boolean addFreshEntity(final Entity aio) {
        final int integer3 = Mth.floor(aio.x / 16.0);
        final int integer4 = Mth.floor(aio.z / 16.0);
        this.getChunk(integer3, integer4).addEntity(aio);
        return true;
    }
    
    public boolean removeBlock(final BlockPos ew, final boolean boolean2) {
        return this.setBlock(ew, Blocks.AIR.defaultBlockState(), 3);
    }
    
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }
    
    public boolean isUnobstructed(@Nullable final Entity aio, final VoxelShape ctc) {
        return true;
    }
    
    public boolean isClientSide() {
        return false;
    }
    
    @Deprecated
    public ServerLevel getLevel() {
        return this.level;
    }
    
    public LevelData getLevelData() {
        return this.levelData;
    }
    
    public DifficultyInstance getCurrentDifficultyAt(final BlockPos ew) {
        if (!this.hasChunk(ew.getX() >> 4, ew.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new DifficultyInstance(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.getMoonBrightness());
    }
    
    public ChunkSource getChunkSource() {
        return this.level.getChunkSource();
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public TickList<Block> getBlockTicks() {
        return this.blockTicks;
    }
    
    public TickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }
    
    public int getSeaLevel() {
        return this.seaLevel;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    public void blockUpdated(final BlockPos ew, final Block bmv) {
    }
    
    public int getHeight(final Heightmap.Types a, final int integer2, final int integer3) {
        return this.getChunk(integer2 >> 4, integer3 >> 4).getHeight(a, integer2 & 0xF, integer3 & 0xF) + 1;
    }
    
    public void playSound(@Nullable final Player awg, final BlockPos ew, final SoundEvent yo, final SoundSource yq, final float float5, final float float6) {
    }
    
    public void addParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
    }
    
    public void levelEvent(@Nullable final Player awg, final int integer2, final BlockPos ew, final int integer4) {
    }
    
    public BlockPos getSharedSpawnPos() {
        return this.level.getSharedSpawnPos();
    }
    
    public Dimension getDimension() {
        return this.dimension;
    }
    
    public boolean isStateAtPosition(final BlockPos ew, final Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(ew));
    }
    
    public <T extends Entity> List<T> getEntitiesOfClass(final Class<? extends T> class1, final AABB csc, @Nullable final Predicate<? super T> predicate) {
        return (List<T>)Collections.emptyList();
    }
    
    public List<Entity> getEntities(@Nullable final Entity aio, final AABB csc, @Nullable final Predicate<? super Entity> predicate) {
        return (List<Entity>)Collections.emptyList();
    }
    
    public List<Player> players() {
        return (List<Player>)Collections.emptyList();
    }
    
    public BlockPos getHeightmapPos(final Heightmap.Types a, final BlockPos ew) {
        return new BlockPos(ew.getX(), this.getHeight(a, ew.getX(), ew.getZ()), ew.getZ());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
