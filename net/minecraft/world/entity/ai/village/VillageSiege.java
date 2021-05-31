package net.minecraft.world.entity.ai.village;

import javax.annotation.Nullable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Zombie;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

public class VillageSiege {
    private boolean hasSetupSiege;
    private State siegeState;
    private int zombiesToSpawn;
    private int nextSpawnTime;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    
    public VillageSiege() {
        this.siegeState = State.SIEGE_DONE;
    }
    
    public int tick(final ServerLevel vk, final boolean boolean2, final boolean boolean3) {
        if (vk.isDay() || !boolean2) {
            this.siegeState = State.SIEGE_DONE;
            this.hasSetupSiege = false;
            return 0;
        }
        final float float5 = vk.getTimeOfDay(0.0f);
        if (float5 == 0.5) {
            this.siegeState = ((vk.random.nextInt(10) == 0) ? State.SIEGE_TONIGHT : State.SIEGE_DONE);
        }
        if (this.siegeState == State.SIEGE_DONE) {
            return 0;
        }
        if (!this.hasSetupSiege) {
            if (!this.tryToSetupSiege(vk)) {
                return 0;
            }
            this.hasSetupSiege = true;
        }
        if (this.nextSpawnTime > 0) {
            --this.nextSpawnTime;
            return 0;
        }
        this.nextSpawnTime = 2;
        if (this.zombiesToSpawn > 0) {
            this.trySpawn(vk);
            --this.zombiesToSpawn;
        }
        else {
            this.siegeState = State.SIEGE_DONE;
        }
        return 1;
    }
    
    private boolean tryToSetupSiege(final ServerLevel vk) {
        for (final Player awg4 : vk.players()) {
            if (!awg4.isSpectator()) {
                final BlockPos ew5 = awg4.getCommandSenderBlockPosition();
                if (!vk.isVillage(ew5)) {
                    continue;
                }
                if (vk.getBiome(ew5).getBiomeCategory() == Biome.BiomeCategory.MUSHROOM) {
                    continue;
                }
                for (int integer6 = 0; integer6 < 10; ++integer6) {
                    final float float7 = vk.random.nextFloat() * 6.2831855f;
                    this.spawnX = ew5.getX() + Mth.floor(Mth.cos(float7) * 32.0f);
                    this.spawnY = ew5.getY();
                    this.spawnZ = ew5.getZ() + Mth.floor(Mth.sin(float7) * 32.0f);
                    if (this.findRandomSpawnPos(vk, new BlockPos(this.spawnX, this.spawnY, this.spawnZ)) != null) {
                        this.nextSpawnTime = 0;
                        this.zombiesToSpawn = 20;
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private void trySpawn(final ServerLevel vk) {
        final Vec3 csi3 = this.findRandomSpawnPos(vk, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
        if (csi3 == null) {
            return;
        }
        Zombie avm4;
        try {
            avm4 = new Zombie(vk);
            avm4.finalizeSpawn(vk, vk.getCurrentDifficultyAt(new BlockPos(avm4)), MobSpawnType.EVENT, null, null);
        }
        catch (Exception exception5) {
            exception5.printStackTrace();
            return;
        }
        avm4.moveTo(csi3.x, csi3.y, csi3.z, vk.random.nextFloat() * 360.0f, 0.0f);
        vk.addFreshEntity(avm4);
    }
    
    @Nullable
    private Vec3 findRandomSpawnPos(final ServerLevel vk, final BlockPos ew) {
        for (int integer4 = 0; integer4 < 10; ++integer4) {
            final int integer5 = ew.getX() + vk.random.nextInt(16) - 8;
            final int integer6 = ew.getZ() + vk.random.nextInt(16) - 8;
            final int integer7 = vk.getHeight(Heightmap.Types.WORLD_SURFACE, integer5, integer6);
            final BlockPos ew2 = new BlockPos(integer5, integer7, integer6);
            if (vk.isVillage(ew2)) {
                if (Monster.checkMonsterSpawnRules(EntityType.ZOMBIE, vk, MobSpawnType.EVENT, ew2, vk.random)) {
                    return new Vec3(ew2.getX() + 0.5, ew2.getY(), ew2.getZ() + 0.5);
                }
            }
        }
        return null;
    }
    
    enum State {
        SIEGE_CAN_ACTIVATE, 
        SIEGE_TONIGHT, 
        SIEGE_DONE;
    }
}
