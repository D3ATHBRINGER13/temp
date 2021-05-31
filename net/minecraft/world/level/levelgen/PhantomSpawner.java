package net.minecraft.world.level.levelgen;

import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.DifficultyInstance;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.util.Mth;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

public class PhantomSpawner {
    private int nextTick;
    
    public int tick(final ServerLevel vk, final boolean boolean2, final boolean boolean3) {
        if (!boolean2) {
            return 0;
        }
        final Random random5 = vk.random;
        --this.nextTick;
        if (this.nextTick > 0) {
            return 0;
        }
        this.nextTick += (60 + random5.nextInt(60)) * 20;
        if (vk.getSkyDarken() < 5 && vk.dimension.isHasSkyLight()) {
            return 0;
        }
        int integer6 = 0;
        for (final Player awg8 : vk.players()) {
            if (awg8.isSpectator()) {
                continue;
            }
            final BlockPos ew9 = new BlockPos(awg8);
            if (vk.dimension.isHasSkyLight()) {
                if (ew9.getY() < vk.getSeaLevel()) {
                    continue;
                }
                if (!vk.canSeeSky(ew9)) {
                    continue;
                }
            }
            final DifficultyInstance ahh10 = vk.getCurrentDifficultyAt(ew9);
            if (!ahh10.isHarderThan(random5.nextFloat() * 3.0f)) {
                continue;
            }
            final ServerStatsCounter yu11 = ((ServerPlayer)awg8).getStats();
            final int integer7 = Mth.clamp(yu11.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            final int integer8 = 24000;
            if (random5.nextInt(integer7) < 72000) {
                continue;
            }
            final BlockPos ew10 = ew9.above(20 + random5.nextInt(15)).east(-10 + random5.nextInt(21)).south(-10 + random5.nextInt(21));
            final BlockState bvt15 = vk.getBlockState(ew10);
            final FluidState clk16 = vk.getFluidState(ew10);
            if (!NaturalSpawner.isValidEmptySpawnBlock(vk, ew10, bvt15, clk16)) {
                continue;
            }
            SpawnGroupData ajj17 = null;
            final int integer9 = 1 + random5.nextInt(ahh10.getDifficulty().getId() + 1);
            for (int integer10 = 0; integer10 < integer9; ++integer10) {
                final Phantom auu20 = EntityType.PHANTOM.create(vk);
                auu20.moveTo(ew10, 0.0f, 0.0f);
                ajj17 = auu20.finalizeSpawn(vk, ahh10, MobSpawnType.NATURAL, ajj17, null);
                vk.addFreshEntity(auu20);
            }
            integer6 += integer9;
        }
        return integer6;
    }
}
