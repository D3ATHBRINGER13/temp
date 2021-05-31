package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.Entity;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.BlockPos;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.server.level.ServerLevel;
import java.util.Random;

public class WanderingTraderSpawner {
    private final Random random;
    private final ServerLevel level;
    private int tickDelay;
    private int spawnDelay;
    private int spawnChance;
    
    public WanderingTraderSpawner(final ServerLevel vk) {
        this.random = new Random();
        this.level = vk;
        this.tickDelay = 1200;
        final LevelData com3 = vk.getLevelData();
        this.spawnDelay = com3.getWanderingTraderSpawnDelay();
        this.spawnChance = com3.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            com3.setWanderingTraderSpawnDelay(this.spawnDelay = 24000);
            com3.setWanderingTraderSpawnChance(this.spawnChance = 25);
        }
    }
    
    public void tick() {
        final int tickDelay = this.tickDelay - 1;
        this.tickDelay = tickDelay;
        if (tickDelay > 0) {
            return;
        }
        this.tickDelay = 1200;
        final LevelData com2 = this.level.getLevelData();
        com2.setWanderingTraderSpawnDelay(this.spawnDelay -= 1200);
        if (this.spawnDelay > 0) {
            return;
        }
        this.spawnDelay = 24000;
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        final int integer3 = this.spawnChance;
        com2.setWanderingTraderSpawnChance(this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75));
        if (this.random.nextInt(100) > integer3) {
            return;
        }
        if (this.spawn()) {
            this.spawnChance = 25;
        }
    }
    
    private boolean spawn() {
        final Player awg2 = this.level.getRandomPlayer();
        if (awg2 == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        final BlockPos ew3 = awg2.getCommandSenderBlockPosition();
        final int integer4 = 48;
        final PoiManager aqp5 = this.level.getPoiManager();
        final Optional<BlockPos> optional6 = aqp5.find(PoiType.MEETING.getPredicate(), (Predicate<BlockPos>)(ew -> true), ew3, 48, PoiManager.Occupancy.ANY);
        final BlockPos ew4 = (BlockPos)optional6.orElse(ew3);
        final BlockPos ew5 = this.findSpawnPositionNear(ew4, 48);
        if (ew5 != null) {
            if (this.level.getBiome(ew5) == Biomes.THE_VOID) {
                return false;
            }
            final WanderingTrader avz9 = EntityType.WANDERING_TRADER.spawn(this.level, null, null, null, ew5, MobSpawnType.EVENT, false, false);
            if (avz9 != null) {
                for (int integer5 = 0; integer5 < 2; ++integer5) {
                    this.tryToSpawnLlamaFor(avz9, 4);
                }
                this.level.getLevelData().setWanderingTraderId(avz9.getUUID());
                avz9.setDespawnDelay(48000);
                avz9.setWanderTarget(ew4);
                avz9.restrictTo(ew4, 16);
                return true;
            }
        }
        return false;
    }
    
    private void tryToSpawnLlamaFor(final WanderingTrader avz, final int integer) {
        final BlockPos ew4 = this.findSpawnPositionNear(new BlockPos(avz), integer);
        if (ew4 == null) {
            return;
        }
        final TraderLlama asi5 = EntityType.TRADER_LLAMA.spawn(this.level, null, null, null, ew4, MobSpawnType.EVENT, false, false);
        if (asi5 == null) {
            return;
        }
        asi5.setLeashedTo(avz, true);
    }
    
    @Nullable
    private BlockPos findSpawnPositionNear(final BlockPos ew, final int integer) {
        BlockPos ew2 = null;
        for (int integer2 = 0; integer2 < 10; ++integer2) {
            final int integer3 = ew.getX() + this.random.nextInt(integer * 2) - integer;
            final int integer4 = ew.getZ() + this.random.nextInt(integer * 2) - integer;
            final int integer5 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, integer3, integer4);
            final BlockPos ew3 = new BlockPos(integer3, integer5, integer4);
            if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, ew3, EntityType.WANDERING_TRADER)) {
                ew2 = ew3;
                break;
            }
        }
        return ew2;
    }
}
