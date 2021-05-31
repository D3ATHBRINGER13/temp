package net.minecraft.world.entity.global;

import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameRules;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import net.minecraft.advancements.CriteriaTriggers;
import java.util.function.Predicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class LightningBolt extends Entity {
    private int life;
    public long seed;
    private int flashes;
    private final boolean visualOnly;
    @Nullable
    private ServerPlayer cause;
    
    public LightningBolt(final Level bhr, final double double2, final double double3, final double double4, final boolean boolean5) {
        super(EntityType.LIGHTNING_BOLT, bhr);
        this.noCulling = true;
        this.moveTo(double2, double3, double4, 0.0f, 0.0f);
        this.life = 2;
        this.seed = this.random.nextLong();
        this.flashes = this.random.nextInt(3) + 1;
        this.visualOnly = boolean5;
        final Difficulty ahg10 = bhr.getDifficulty();
        if (ahg10 == Difficulty.NORMAL || ahg10 == Difficulty.HARD) {
            this.spawnFire(4);
        }
    }
    
    @Override
    public SoundSource getSoundSource() {
        return SoundSource.WEATHER;
    }
    
    public void setCause(@Nullable final ServerPlayer vl) {
        this.cause = vl;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.life == 2) {
            this.level.playSound(null, this.x, this.y, this.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0f, 0.8f + this.random.nextFloat() * 0.2f);
            this.level.playSound(null, this.x, this.y, this.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0f, 0.5f + this.random.nextFloat() * 0.2f);
        }
        --this.life;
        if (this.life < 0) {
            if (this.flashes == 0) {
                this.remove();
            }
            else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
                this.spawnFire(0);
            }
        }
        if (this.life >= 0) {
            if (this.level.isClientSide) {
                this.level.setSkyFlashTime(2);
            }
            else if (!this.visualOnly) {
                final double double2 = 3.0;
                final List<Entity> list4 = this.level.getEntities(this, new AABB(this.x - 3.0, this.y - 3.0, this.z - 3.0, this.x + 3.0, this.y + 6.0 + 3.0, this.z + 3.0), Entity::isAlive);
                for (final Entity aio6 : list4) {
                    aio6.thunderHit(this);
                }
                if (this.cause != null) {
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, list4);
                }
            }
        }
    }
    
    private void spawnFire(final int integer) {
        if (this.visualOnly || this.level.isClientSide || !this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        final BlockState bvt3 = Blocks.FIRE.defaultBlockState();
        final BlockPos ew4 = new BlockPos(this);
        if (this.level.getBlockState(ew4).isAir() && bvt3.canSurvive(this.level, ew4)) {
            this.level.setBlockAndUpdate(ew4, bvt3);
        }
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            final BlockPos ew5 = ew4.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (this.level.getBlockState(ew5).isAir() && bvt3.canSurvive(this.level, ew5)) {
                this.level.setBlockAndUpdate(ew5, bvt3);
            }
        }
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        final double double2 = 64.0 * getViewScale();
        return double1 < double2 * double2;
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddGlobalEntityPacket(this);
    }
}
