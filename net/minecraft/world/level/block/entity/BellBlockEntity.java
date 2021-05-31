package net.minecraft.world.level.block.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.level.Level;
import net.minecraft.tags.EntityTypeTags;
import java.util.Iterator;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.core.Position;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import net.minecraft.core.Direction;

public class BellBlockEntity extends BlockEntity implements TickableBlockEntity {
    private long lastRingTimestamp;
    public int ticks;
    public boolean shaking;
    public Direction clickDirection;
    private List<LivingEntity> nearbyEntities;
    private boolean resonating;
    private int resonationTicks;
    
    public BellBlockEntity() {
        super(BlockEntityType.BELL);
    }
    
    @Override
    public boolean triggerEvent(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.updateEntities();
            this.resonationTicks = 0;
            this.clickDirection = Direction.from3DDataValue(integer2);
            this.ticks = 0;
            return this.shaking = true;
        }
        return super.triggerEvent(integer1, integer2);
    }
    
    @Override
    public void tick() {
        if (this.shaking) {
            ++this.ticks;
        }
        if (this.ticks >= 50) {
            this.shaking = false;
            this.ticks = 0;
        }
        if (this.ticks >= 5 && this.resonationTicks == 0 && this.areRaidersNearby()) {
            this.resonating = true;
            this.playResonateSound();
        }
        if (this.resonating) {
            if (this.resonationTicks < 40) {
                ++this.resonationTicks;
            }
            else {
                this.makeRaidersGlow(this.level);
                this.showBellParticles(this.level);
                this.resonating = false;
            }
        }
    }
    
    private void playResonateSound() {
        this.level.playSound(null, this.getBlockPos(), SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
    
    public void onHit(final Direction fb) {
        final BlockPos ew3 = this.getBlockPos();
        this.clickDirection = fb;
        if (this.shaking) {
            this.ticks = 0;
        }
        else {
            this.shaking = true;
        }
        this.level.blockEvent(ew3, this.getBlockState().getBlock(), 1, fb.get3DDataValue());
    }
    
    private void updateEntities() {
        final BlockPos ew2 = this.getBlockPos();
        if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
            this.lastRingTimestamp = this.level.getGameTime();
            final AABB csc3 = new AABB(ew2).inflate(48.0);
            this.nearbyEntities = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, csc3);
        }
        if (!this.level.isClientSide) {
            for (final LivingEntity aix4 : this.nearbyEntities) {
                if (aix4.isAlive()) {
                    if (aix4.removed) {
                        continue;
                    }
                    if (!ew2.closerThan(aix4.position(), 32.0)) {
                        continue;
                    }
                    aix4.getBrain().<Long>setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
                }
            }
        }
    }
    
    private boolean areRaidersNearby() {
        final BlockPos ew2 = this.getBlockPos();
        for (final LivingEntity aix4 : this.nearbyEntities) {
            if (aix4.isAlive()) {
                if (aix4.removed) {
                    continue;
                }
                if (ew2.closerThan(aix4.position(), 32.0) && aix4.getType().is(EntityTypeTags.RAIDERS)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private void makeRaidersGlow(final Level bhr) {
        if (bhr.isClientSide) {
            return;
        }
        this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(this::glow);
    }
    
    private void showBellParticles(final Level bhr) {
        if (!bhr.isClientSide) {
            return;
        }
        final BlockPos ew3 = this.getBlockPos();
        final AtomicInteger atomicInteger4 = new AtomicInteger(16700985);
        final int integer5 = (int)this.nearbyEntities.stream().filter(aix -> ew3.closerThan(aix.position(), 48.0)).count();
        this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(aix -> {
            final float float6 = 1.0f;
            final float float7 = Mth.sqrt((aix.x - ew3.getX()) * (aix.x - ew3.getX()) + (aix.z - ew3.getZ()) * (aix.z - ew3.getZ()));
            final double double8 = ew3.getX() + 0.5f + 1.0f / float7 * (aix.x - ew3.getX());
            final double double9 = ew3.getZ() + 0.5f + 1.0f / float7 * (aix.z - ew3.getZ());
            for (int integer2 = Mth.clamp((integer5 - 21) / -2, 3, 15), integer3 = 0; integer3 < integer2; ++integer3) {
                atomicInteger4.addAndGet(5);
                final double double10 = (atomicInteger4.get() >> 16 & 0xFF) / 255.0;
                final double double11 = (atomicInteger4.get() >> 8 & 0xFF) / 255.0;
                final double double12 = (atomicInteger4.get() & 0xFF) / 255.0;
                bhr.addParticle(ParticleTypes.ENTITY_EFFECT, double8, ew3.getY() + 0.5f, double9, double10, double11, double12);
            }
        });
    }
    
    private boolean isRaiderWithinRange(final LivingEntity aix) {
        return aix.isAlive() && !aix.removed && this.getBlockPos().closerThan(aix.position(), 48.0) && aix.getType().is(EntityTypeTags.RAIDERS);
    }
    
    private void glow(final LivingEntity aix) {
        aix.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
    }
}
