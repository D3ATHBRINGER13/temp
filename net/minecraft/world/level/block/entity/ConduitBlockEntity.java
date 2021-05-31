package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.sounds.SoundEvent;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import java.util.function.Predicate;
import java.util.Iterator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.world.level.block.Block;

public class ConduitBlockEntity extends BlockEntity implements TickableBlockEntity {
    private static final Block[] VALID_BLOCKS;
    public int tickCount;
    private float activeRotation;
    private boolean isActive;
    private boolean isHunting;
    private final List<BlockPos> effectBlocks;
    @Nullable
    private LivingEntity destroyTarget;
    @Nullable
    private UUID destroyTargetUUID;
    private long nextAmbientSoundActivation;
    
    public ConduitBlockEntity() {
        this(BlockEntityType.CONDUIT);
    }
    
    public ConduitBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
        this.effectBlocks = (List<BlockPos>)Lists.newArrayList();
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        if (id.contains("target_uuid")) {
            this.destroyTargetUUID = NbtUtils.loadUUIDTag(id.getCompound("target_uuid"));
        }
        else {
            this.destroyTargetUUID = null;
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (this.destroyTarget != null) {
            id.put("target_uuid", (Tag)NbtUtils.createUUIDTag(this.destroyTarget.getUUID()));
        }
        return id;
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 5, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    @Override
    public void tick() {
        ++this.tickCount;
        final long long2 = this.level.getGameTime();
        if (long2 % 40L == 0L) {
            this.setActive(this.updateShape());
            if (!this.level.isClientSide && this.isActive()) {
                this.applyEffects();
                this.updateDestroyTarget();
            }
        }
        if (long2 % 80L == 0L && this.isActive()) {
            this.playSound(SoundEvents.CONDUIT_AMBIENT);
        }
        if (long2 > this.nextAmbientSoundActivation && this.isActive()) {
            this.nextAmbientSoundActivation = long2 + 60L + this.level.getRandom().nextInt(40);
            this.playSound(SoundEvents.CONDUIT_AMBIENT_SHORT);
        }
        if (this.level.isClientSide) {
            this.updateClientTarget();
            this.animationTick();
            if (this.isActive()) {
                ++this.activeRotation;
            }
        }
    }
    
    private boolean updateShape() {
        this.effectBlocks.clear();
        for (int integer2 = -1; integer2 <= 1; ++integer2) {
            for (int integer3 = -1; integer3 <= 1; ++integer3) {
                for (int integer4 = -1; integer4 <= 1; ++integer4) {
                    final BlockPos ew5 = this.worldPosition.offset(integer2, integer3, integer4);
                    if (!this.level.isWaterAt(ew5)) {
                        return false;
                    }
                }
            }
        }
        for (int integer2 = -2; integer2 <= 2; ++integer2) {
            for (int integer3 = -2; integer3 <= 2; ++integer3) {
                for (int integer4 = -2; integer4 <= 2; ++integer4) {
                    final int integer5 = Math.abs(integer2);
                    final int integer6 = Math.abs(integer3);
                    final int integer7 = Math.abs(integer4);
                    if (integer5 > 1 || integer6 > 1 || integer7 > 1) {
                        if ((integer2 == 0 && (integer6 == 2 || integer7 == 2)) || (integer3 == 0 && (integer5 == 2 || integer7 == 2)) || (integer4 == 0 && (integer5 == 2 || integer6 == 2))) {
                            final BlockPos ew6 = this.worldPosition.offset(integer2, integer3, integer4);
                            final BlockState bvt9 = this.level.getBlockState(ew6);
                            for (final Block bmv13 : ConduitBlockEntity.VALID_BLOCKS) {
                                if (bvt9.getBlock() == bmv13) {
                                    this.effectBlocks.add(ew6);
                                }
                            }
                        }
                    }
                }
            }
        }
        this.setHunting(this.effectBlocks.size() >= 42);
        return this.effectBlocks.size() >= 16;
    }
    
    private void applyEffects() {
        final int integer2 = this.effectBlocks.size();
        final int integer3 = integer2 / 7 * 16;
        final int integer4 = this.worldPosition.getX();
        final int integer5 = this.worldPosition.getY();
        final int integer6 = this.worldPosition.getZ();
        final AABB csc7 = new AABB(integer4, integer5, integer6, integer4 + 1, integer5 + 1, integer6 + 1).inflate(integer3).expandTowards(0.0, this.level.getMaxBuildHeight(), 0.0);
        final List<Player> list8 = this.level.<Player>getEntitiesOfClass((java.lang.Class<? extends Player>)Player.class, csc7);
        if (list8.isEmpty()) {
            return;
        }
        for (final Player awg10 : list8) {
            if (this.worldPosition.closerThan(new BlockPos(awg10), integer3) && awg10.isInWaterOrRain()) {
                awg10.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
            }
        }
    }
    
    private void updateDestroyTarget() {
        final LivingEntity aix2 = this.destroyTarget;
        final int integer3 = this.effectBlocks.size();
        if (integer3 < 42) {
            this.destroyTarget = null;
        }
        else if (this.destroyTarget == null && this.destroyTargetUUID != null) {
            this.destroyTarget = this.findDestroyTarget();
            this.destroyTargetUUID = null;
        }
        else if (this.destroyTarget == null) {
            final List<LivingEntity> list4 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, this.getDestroyRangeAABB(), (java.util.function.Predicate<? super LivingEntity>)(aix -> aix instanceof Enemy && aix.isInWaterOrRain()));
            if (!list4.isEmpty()) {
                this.destroyTarget = (LivingEntity)list4.get(this.level.random.nextInt(list4.size()));
            }
        }
        else if (!this.destroyTarget.isAlive() || !this.worldPosition.closerThan(new BlockPos(this.destroyTarget), 8.0)) {
            this.destroyTarget = null;
        }
        if (this.destroyTarget != null) {
            this.level.playSound(null, this.destroyTarget.x, this.destroyTarget.y, this.destroyTarget.z, SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.destroyTarget.hurt(DamageSource.MAGIC, 4.0f);
        }
        if (aix2 != this.destroyTarget) {
            final BlockState bvt4 = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, bvt4, bvt4, 2);
        }
    }
    
    private void updateClientTarget() {
        if (this.destroyTargetUUID == null) {
            this.destroyTarget = null;
        }
        else if (this.destroyTarget == null || !this.destroyTarget.getUUID().equals(this.destroyTargetUUID)) {
            this.destroyTarget = this.findDestroyTarget();
            if (this.destroyTarget == null) {
                this.destroyTargetUUID = null;
            }
        }
    }
    
    private AABB getDestroyRangeAABB() {
        final int integer2 = this.worldPosition.getX();
        final int integer3 = this.worldPosition.getY();
        final int integer4 = this.worldPosition.getZ();
        return new AABB(integer2, integer3, integer4, integer2 + 1, integer3 + 1, integer4 + 1).inflate(8.0);
    }
    
    @Nullable
    private LivingEntity findDestroyTarget() {
        final List<LivingEntity> list2 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, this.getDestroyRangeAABB(), (java.util.function.Predicate<? super LivingEntity>)(aix -> aix.getUUID().equals(this.destroyTargetUUID)));
        if (list2.size() == 1) {
            return (LivingEntity)list2.get(0);
        }
        return null;
    }
    
    private void animationTick() {
        final Random random2 = this.level.random;
        float float3 = Mth.sin((this.tickCount + 35) * 0.1f) / 2.0f + 0.5f;
        float3 = (float3 * float3 + float3) * 0.3f;
        final Vec3 csi4 = new Vec3(this.worldPosition.getX() + 0.5f, this.worldPosition.getY() + 1.5f + float3, this.worldPosition.getZ() + 0.5f);
        for (final BlockPos ew6 : this.effectBlocks) {
            if (random2.nextInt(50) != 0) {
                continue;
            }
            final float float4 = -0.5f + random2.nextFloat();
            final float float5 = -2.0f + random2.nextFloat();
            final float float6 = -0.5f + random2.nextFloat();
            final BlockPos ew7 = ew6.subtract(this.worldPosition);
            final Vec3 csi5 = new Vec3(float4, float5, float6).add(ew7.getX(), ew7.getY(), ew7.getZ());
            this.level.addParticle(ParticleTypes.NAUTILUS, csi4.x, csi4.y, csi4.z, csi5.x, csi5.y, csi5.z);
        }
        if (this.destroyTarget != null) {
            final Vec3 csi6 = new Vec3(this.destroyTarget.x, this.destroyTarget.y + this.destroyTarget.getEyeHeight(), this.destroyTarget.z);
            final float float7 = (-0.5f + random2.nextFloat()) * (3.0f + this.destroyTarget.getBbWidth());
            final float float4 = -1.0f + random2.nextFloat() * this.destroyTarget.getBbHeight();
            final float float5 = (-0.5f + random2.nextFloat()) * (3.0f + this.destroyTarget.getBbWidth());
            final Vec3 csi7 = new Vec3(float7, float4, float5);
            this.level.addParticle(ParticleTypes.NAUTILUS, csi6.x, csi6.y, csi6.z, csi7.x, csi7.y, csi7.z);
        }
    }
    
    public boolean isActive() {
        return this.isActive;
    }
    
    public boolean isHunting() {
        return this.isHunting;
    }
    
    private void setActive(final boolean boolean1) {
        if (boolean1 != this.isActive) {
            this.playSound(boolean1 ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE);
        }
        this.isActive = boolean1;
    }
    
    private void setHunting(final boolean boolean1) {
        this.isHunting = boolean1;
    }
    
    public float getActiveRotation(final float float1) {
        return (this.activeRotation + float1) * -0.0375f;
    }
    
    public void playSound(final SoundEvent yo) {
        this.level.playSound(null, this.worldPosition, yo, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
    
    static {
        VALID_BLOCKS = new Block[] { Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE };
    }
}
