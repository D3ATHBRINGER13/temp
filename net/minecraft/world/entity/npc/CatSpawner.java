package net.minecraft.world.entity.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.server.level.ServerLevel;

public class CatSpawner {
    private int nextTick;
    
    public int tick(final ServerLevel vk, final boolean boolean2, final boolean boolean3) {
        if (!boolean3 || !vk.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return 0;
        }
        --this.nextTick;
        if (this.nextTick > 0) {
            return 0;
        }
        this.nextTick = 1200;
        final Player awg5 = vk.getRandomPlayer();
        if (awg5 == null) {
            return 0;
        }
        final Random random6 = vk.random;
        final int integer7 = (8 + random6.nextInt(24)) * (random6.nextBoolean() ? -1 : 1);
        final int integer8 = (8 + random6.nextInt(24)) * (random6.nextBoolean() ? -1 : 1);
        final BlockPos ew9 = new BlockPos(awg5).offset(integer7, 0, integer8);
        if (!vk.hasChunksAt(ew9.getX() - 10, ew9.getY() - 10, ew9.getZ() - 10, ew9.getX() + 10, ew9.getY() + 10, ew9.getZ() + 10)) {
            return 0;
        }
        if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, vk, ew9, EntityType.CAT)) {
            if (vk.closeToVillage(ew9, 2)) {
                return this.spawnInVillage(vk, ew9);
            }
            if (Feature.SWAMP_HUT.isInsideFeature(vk, ew9)) {
                return this.spawnInHut(vk, ew9);
            }
        }
        return 0;
    }
    
    private int spawnInVillage(final ServerLevel vk, final BlockPos ew) {
        final int integer4 = 48;
        if (vk.getPoiManager().getCountInRange(PoiType.HOME.getPredicate(), ew, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L) {
            final List<Cat> list5 = vk.<Cat>getEntitiesOfClass((java.lang.Class<? extends Cat>)Cat.class, new AABB(ew).inflate(48.0, 8.0, 48.0));
            if (list5.size() < 5) {
                return this.spawnCat(ew, vk);
            }
        }
        return 0;
    }
    
    private int spawnInHut(final Level bhr, final BlockPos ew) {
        final int integer4 = 16;
        final List<Cat> list5 = bhr.<Cat>getEntitiesOfClass((java.lang.Class<? extends Cat>)Cat.class, new AABB(ew).inflate(16.0, 8.0, 16.0));
        if (list5.size() < 1) {
            return this.spawnCat(ew, bhr);
        }
        return 0;
    }
    
    private int spawnCat(final BlockPos ew, final Level bhr) {
        final Cat arb4 = EntityType.CAT.create(bhr);
        if (arb4 == null) {
            return 0;
        }
        arb4.finalizeSpawn(bhr, bhr.getCurrentDifficultyAt(ew), MobSpawnType.NATURAL, null, null);
        arb4.moveTo(ew, 0.0f, 0.0f);
        bhr.addFreshEntity(arb4);
        return 1;
    }
}
