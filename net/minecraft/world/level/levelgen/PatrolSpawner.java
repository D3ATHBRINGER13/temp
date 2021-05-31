package net.minecraft.world.level.levelgen;

import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

public class PatrolSpawner {
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
        this.nextTick += 12000 + random5.nextInt(1200);
        final long long6 = vk.getDayTime() / 24000L;
        if (long6 < 5L || !vk.isDay()) {
            return 0;
        }
        if (random5.nextInt(5) != 0) {
            return 0;
        }
        final int integer8 = vk.players().size();
        if (integer8 < 1) {
            return 0;
        }
        final Player awg9 = (Player)vk.players().get(random5.nextInt(integer8));
        if (awg9.isSpectator()) {
            return 0;
        }
        if (vk.isVillage(awg9.getCommandSenderBlockPosition())) {
            return 0;
        }
        final int integer9 = (24 + random5.nextInt(24)) * (random5.nextBoolean() ? -1 : 1);
        final int integer10 = (24 + random5.nextInt(24)) * (random5.nextBoolean() ? -1 : 1);
        final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
        a12.set(awg9.x, awg9.y, awg9.z).move(integer9, 0, integer10);
        if (!vk.hasChunksAt(a12.getX() - 10, a12.getY() - 10, a12.getZ() - 10, a12.getX() + 10, a12.getY() + 10, a12.getZ() + 10)) {
            return 0;
        }
        final Biome bio13 = vk.getBiome(a12);
        final Biome.BiomeCategory b14 = bio13.getBiomeCategory();
        if (b14 == Biome.BiomeCategory.MUSHROOM) {
            return 0;
        }
        int integer11 = 0;
        for (int integer12 = (int)Math.ceil((double)vk.getCurrentDifficultyAt(a12).getEffectiveDifficulty()) + 1, integer13 = 0; integer13 < integer12; ++integer13) {
            ++integer11;
            a12.setY(vk.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, a12).getY());
            if (integer13 == 0) {
                if (!this.spawnPatrolMember(vk, a12, random5, true)) {
                    break;
                }
            }
            else {
                this.spawnPatrolMember(vk, a12, random5, false);
            }
            a12.setX(a12.getX() + random5.nextInt(5) - random5.nextInt(5));
            a12.setZ(a12.getZ() + random5.nextInt(5) - random5.nextInt(5));
        }
        return integer11;
    }
    
    private boolean spawnPatrolMember(final Level bhr, final BlockPos ew, final Random random, final boolean boolean4) {
        if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, bhr, MobSpawnType.PATROL, ew, random)) {
            return false;
        }
        final PatrollingMonster aut6 = EntityType.PILLAGER.create(bhr);
        if (aut6 != null) {
            if (boolean4) {
                aut6.setPatrolLeader(true);
                aut6.findPatrolTarget();
            }
            aut6.setPos(ew.getX(), ew.getY(), ew.getZ());
            aut6.finalizeSpawn(bhr, bhr.getCurrentDifficultyAt(ew), MobSpawnType.PATROL, null, null);
            bhr.addFreshEntity(aut6);
            return true;
        }
        return false;
    }
}
