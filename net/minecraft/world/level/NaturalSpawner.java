package net.minecraft.world.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.levelgen.Heightmap;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.util.WeighedRandom;
import java.util.Random;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import java.util.Objects;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.entity.MobCategory;
import org.apache.logging.log4j.Logger;

public final class NaturalSpawner {
    private static final Logger LOGGER;
    
    public static void spawnCategoryForChunk(final MobCategory aiz, final Level bhr, final LevelChunk bxt, final BlockPos ew) {
        final ChunkGenerator<?> bxi5 = bhr.getChunkSource().getGenerator();
        int integer6 = 0;
        final BlockPos ew2 = getRandomPosWithin(bhr, bxt);
        final int integer7 = ew2.getX();
        final int integer8 = ew2.getY();
        final int integer9 = ew2.getZ();
        if (integer8 < 1) {
            return;
        }
        final BlockState bvt11 = bxt.getBlockState(ew2);
        if (bvt11.isRedstoneConductor(bxt, ew2)) {
            return;
        }
        final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
        for (int integer10 = 0; integer10 < 3; ++integer10) {
            int integer11 = integer7;
            int integer12 = integer9;
            final int integer13 = 6;
            Biome.SpawnerData e17 = null;
            SpawnGroupData ajj18 = null;
            int integer14 = Mth.ceil(Math.random() * 4.0);
            int integer15 = 0;
            for (int integer16 = 0; integer16 < integer14; ++integer16) {
                integer11 += bhr.random.nextInt(6) - bhr.random.nextInt(6);
                integer12 += bhr.random.nextInt(6) - bhr.random.nextInt(6);
                a12.set(integer11, integer8, integer12);
                final float float22 = integer11 + 0.5f;
                final float float23 = integer12 + 0.5f;
                final Player awg24 = bhr.getNearestPlayerIgnoreY(float22, float23, -1.0);
                if (awg24 != null) {
                    final double double25 = awg24.distanceToSqr(float22, integer8, float23);
                    if (double25 > 576.0) {
                        if (!ew.closerThan(new Vec3(float22, integer8, float23), 24.0)) {
                            final ChunkPos bhd27 = new ChunkPos(a12);
                            if (Objects.equals(bhd27, bxt.getPos()) || bhr.getChunkSource().isEntityTickingChunk(bhd27)) {
                                if (e17 == null) {
                                    e17 = getRandomSpawnMobAt(bxi5, aiz, bhr.random, a12);
                                    if (e17 == null) {
                                        break;
                                    }
                                    integer14 = e17.minCount + bhr.random.nextInt(1 + e17.maxCount - e17.minCount);
                                }
                                if (e17.type.getCategory() != MobCategory.MISC) {
                                    if (e17.type.canSpawnFarFromPlayer() || double25 <= 16384.0) {
                                        final EntityType<?> ais28 = e17.type;
                                        if (ais28.canSummon()) {
                                            if (canSpawnMobAt(bxi5, aiz, e17, a12)) {
                                                final SpawnPlacements.Type c29 = SpawnPlacements.getPlacementType(ais28);
                                                if (isSpawnPositionOk(c29, bhr, a12, ais28)) {
                                                    if (SpawnPlacements.checkSpawnRules(ais28, bhr, MobSpawnType.NATURAL, a12, bhr.random)) {
                                                        if (bhr.noCollision(ais28.getAABB(float22, integer8, float23))) {
                                                            Mob aiy30;
                                                            try {
                                                                final Entity aio31 = (Entity)ais28.create(bhr);
                                                                if (!(aio31 instanceof Mob)) {
                                                                    throw new IllegalStateException(new StringBuilder().append("Trying to spawn a non-mob: ").append(Registry.ENTITY_TYPE.getKey(ais28)).toString());
                                                                }
                                                                aiy30 = (Mob)aio31;
                                                            }
                                                            catch (Exception exception31) {
                                                                NaturalSpawner.LOGGER.warn("Failed to create mob", (Throwable)exception31);
                                                                return;
                                                            }
                                                            aiy30.moveTo(float22, integer8, float23, bhr.random.nextFloat() * 360.0f, 0.0f);
                                                            if (double25 <= 16384.0 || !aiy30.removeWhenFarAway(double25)) {
                                                                if (aiy30.checkSpawnRules(bhr, MobSpawnType.NATURAL)) {
                                                                    if (aiy30.checkSpawnObstruction(bhr)) {
                                                                        ajj18 = aiy30.finalizeSpawn(bhr, bhr.getCurrentDifficultyAt(new BlockPos(aiy30)), MobSpawnType.NATURAL, ajj18, null);
                                                                        ++integer6;
                                                                        ++integer15;
                                                                        bhr.addFreshEntity(aiy30);
                                                                        if (integer6 >= aiy30.getMaxSpawnClusterSize()) {
                                                                            return;
                                                                        }
                                                                        if (aiy30.isMaxGroupSizeReached(integer15)) {
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Nullable
    private static Biome.SpawnerData getRandomSpawnMobAt(final ChunkGenerator<?> bxi, final MobCategory aiz, final Random random, final BlockPos ew) {
        final List<Biome.SpawnerData> list5 = bxi.getMobsAt(aiz, ew);
        if (list5.isEmpty()) {
            return null;
        }
        return WeighedRandom.<Biome.SpawnerData>getRandomItem(random, list5);
    }
    
    private static boolean canSpawnMobAt(final ChunkGenerator<?> bxi, final MobCategory aiz, final Biome.SpawnerData e, final BlockPos ew) {
        final List<Biome.SpawnerData> list5 = bxi.getMobsAt(aiz, ew);
        return !list5.isEmpty() && list5.contains(e);
    }
    
    private static BlockPos getRandomPosWithin(final Level bhr, final LevelChunk bxt) {
        final ChunkPos bhd3 = bxt.getPos();
        final int integer4 = bhd3.getMinBlockX() + bhr.random.nextInt(16);
        final int integer5 = bhd3.getMinBlockZ() + bhr.random.nextInt(16);
        final int integer6 = bxt.getHeight(Heightmap.Types.WORLD_SURFACE, integer4, integer5) + 1;
        final int integer7 = bhr.random.nextInt(integer6 + 1);
        return new BlockPos(integer4, integer7, integer5);
    }
    
    public static boolean isValidEmptySpawnBlock(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        return !bvt.isCollisionShapeFullBlock(bhb, ew) && !bvt.isSignalSource() && clk.isEmpty() && !bvt.is(BlockTags.RAILS);
    }
    
    public static boolean isSpawnPositionOk(final SpawnPlacements.Type c, final LevelReader bhu, final BlockPos ew, @Nullable final EntityType<?> ais) {
        if (c == SpawnPlacements.Type.NO_RESTRICTIONS) {
            return true;
        }
        if (ais == null || !bhu.getWorldBorder().isWithinBounds(ew)) {
            return false;
        }
        final BlockState bvt5 = bhu.getBlockState(ew);
        final FluidState clk6 = bhu.getFluidState(ew);
        final BlockPos ew2 = ew.above();
        final BlockPos ew3 = ew.below();
        switch (c) {
            case IN_WATER: {
                return clk6.is(FluidTags.WATER) && bhu.getFluidState(ew3).is(FluidTags.WATER) && !bhu.getBlockState(ew2).isRedstoneConductor(bhu, ew2);
            }
            default: {
                final BlockState bvt6 = bhu.getBlockState(ew3);
                return bvt6.isValidSpawn(bhu, ew3, ais) && isValidEmptySpawnBlock(bhu, ew, bvt5, clk6) && isValidEmptySpawnBlock(bhu, ew2, bhu.getBlockState(ew2), bhu.getFluidState(ew2));
            }
        }
    }
    
    public static void spawnMobsForChunkGeneration(final LevelAccessor bhs, final Biome bio, final int integer3, final int integer4, final Random random) {
        final List<Biome.SpawnerData> list6 = bio.getMobs(MobCategory.CREATURE);
        if (list6.isEmpty()) {
            return;
        }
        final int integer5 = integer3 << 4;
        final int integer6 = integer4 << 4;
        while (random.nextFloat() < bio.getCreatureProbability()) {
            final Biome.SpawnerData e9 = WeighedRandom.<Biome.SpawnerData>getRandomItem(random, list6);
            final int integer7 = e9.minCount + random.nextInt(1 + e9.maxCount - e9.minCount);
            SpawnGroupData ajj11 = null;
            int integer8 = integer5 + random.nextInt(16);
            int integer9 = integer6 + random.nextInt(16);
            final int integer10 = integer8;
            final int integer11 = integer9;
            for (int integer12 = 0; integer12 < integer7; ++integer12) {
                boolean boolean17 = false;
                for (int integer13 = 0; !boolean17 && integer13 < 4; ++integer13) {
                    final BlockPos ew19 = getTopNonCollidingPos(bhs, e9.type, integer8, integer9);
                    if (e9.type.canSummon() && isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, bhs, ew19, e9.type)) {
                        final float float20 = e9.type.getWidth();
                        final double double21 = Mth.clamp(integer8, integer5 + (double)float20, integer5 + 16.0 - float20);
                        final double double22 = Mth.clamp(integer9, integer6 + (double)float20, integer6 + 16.0 - float20);
                        if (!bhs.noCollision(e9.type.getAABB(double21, ew19.getY(), double22))) {
                            continue;
                        }
                        if (!SpawnPlacements.<Entity>checkSpawnRules(e9.type, bhs, MobSpawnType.CHUNK_GENERATION, new BlockPos(double21, ew19.getY(), double22), bhs.getRandom())) {
                            continue;
                        }
                        Entity aio25;
                        try {
                            aio25 = (Entity)e9.type.create(bhs.getLevel());
                        }
                        catch (Exception exception26) {
                            NaturalSpawner.LOGGER.warn("Failed to create mob", (Throwable)exception26);
                            continue;
                        }
                        aio25.moveTo(double21, ew19.getY(), double22, random.nextFloat() * 360.0f, 0.0f);
                        if (aio25 instanceof Mob) {
                            final Mob aiy26 = (Mob)aio25;
                            if (aiy26.checkSpawnRules(bhs, MobSpawnType.CHUNK_GENERATION) && aiy26.checkSpawnObstruction(bhs)) {
                                ajj11 = aiy26.finalizeSpawn(bhs, bhs.getCurrentDifficultyAt(new BlockPos(aiy26)), MobSpawnType.CHUNK_GENERATION, ajj11, null);
                                bhs.addFreshEntity(aiy26);
                                boolean17 = true;
                            }
                        }
                    }
                    for (integer8 += random.nextInt(5) - random.nextInt(5), integer9 += random.nextInt(5) - random.nextInt(5); integer8 < integer5 || integer8 >= integer5 + 16 || integer9 < integer6 || integer9 >= integer6 + 16; integer8 = integer10 + random.nextInt(5) - random.nextInt(5), integer9 = integer11 + random.nextInt(5) - random.nextInt(5)) {}
                }
            }
        }
    }
    
    private static BlockPos getTopNonCollidingPos(final LevelReader bhu, @Nullable final EntityType<?> ais, final int integer3, final int integer4) {
        final BlockPos ew5 = new BlockPos(integer3, bhu.getHeight(SpawnPlacements.getHeightmapType(ais), integer3, integer4), integer4);
        final BlockPos ew6 = ew5.below();
        if (bhu.getBlockState(ew6).isPathfindable(bhu, ew6, PathComputationType.LAND)) {
            return ew6;
        }
        return ew5;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
