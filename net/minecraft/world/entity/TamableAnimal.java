package net.minecraft.world.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.Team;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.SitGoal;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.Animal;

public abstract class TamableAnimal extends Animal {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;
    protected SitGoal sitGoal;
    
    protected TamableAnimal(final EntityType<? extends TamableAnimal> ais, final Level bhr) {
        super(ais, bhr);
        this.reassessTameGoals();
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(TamableAnimal.DATA_FLAGS_ID, (Byte)0);
        this.entityData.<Optional<UUID>>define(TamableAnimal.DATA_OWNERUUID_ID, (Optional<UUID>)Optional.empty());
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.getOwnerUUID() == null) {
            id.putString("OwnerUUID", "");
        }
        else {
            id.putString("OwnerUUID", this.getOwnerUUID().toString());
        }
        id.putBoolean("Sitting", this.isSitting());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        String string3;
        if (id.contains("OwnerUUID", 8)) {
            string3 = id.getString("OwnerUUID");
        }
        else {
            final String string4 = id.getString("Owner");
            string3 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string4);
        }
        if (!string3.isEmpty()) {
            try {
                this.setOwnerUUID(UUID.fromString(string3));
                this.setTame(true);
            }
            catch (Throwable throwable4) {
                this.setTame(false);
            }
        }
        if (this.sitGoal != null) {
            this.sitGoal.wantToSit(id.getBoolean("Sitting"));
        }
        this.setSitting(id.getBoolean("Sitting"));
    }
    
    @Override
    public boolean canBeLeashed(final Player awg) {
        return !this.isLeashed();
    }
    
    protected void spawnTamingParticles(final boolean boolean1) {
        ParticleOptions gf3 = ParticleTypes.HEART;
        if (!boolean1) {
            gf3 = ParticleTypes.SMOKE;
        }
        for (int integer4 = 0; integer4 < 7; ++integer4) {
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            final double double7 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(gf3, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double5, double6, double7);
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 7) {
            this.spawnTamingParticles(true);
        }
        else if (byte1 == 6) {
            this.spawnTamingParticles(false);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public boolean isTame() {
        return (this.entityData.<Byte>get(TamableAnimal.DATA_FLAGS_ID) & 0x4) != 0x0;
    }
    
    public void setTame(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(TamableAnimal.DATA_FLAGS_ID);
        if (boolean1) {
            this.entityData.<Byte>set(TamableAnimal.DATA_FLAGS_ID, (byte)(byte3 | 0x4));
        }
        else {
            this.entityData.<Byte>set(TamableAnimal.DATA_FLAGS_ID, (byte)(byte3 & 0xFFFFFFFB));
        }
        this.reassessTameGoals();
    }
    
    protected void reassessTameGoals() {
    }
    
    public boolean isSitting() {
        return (this.entityData.<Byte>get(TamableAnimal.DATA_FLAGS_ID) & 0x1) != 0x0;
    }
    
    public void setSitting(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(TamableAnimal.DATA_FLAGS_ID);
        if (boolean1) {
            this.entityData.<Byte>set(TamableAnimal.DATA_FLAGS_ID, (byte)(byte3 | 0x1));
        }
        else {
            this.entityData.<Byte>set(TamableAnimal.DATA_FLAGS_ID, (byte)(byte3 & 0xFFFFFFFE));
        }
    }
    
    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)this.entityData.<Optional<UUID>>get(TamableAnimal.DATA_OWNERUUID_ID).orElse(null);
    }
    
    public void setOwnerUUID(@Nullable final UUID uUID) {
        this.entityData.<Optional<UUID>>set(TamableAnimal.DATA_OWNERUUID_ID, (Optional<UUID>)Optional.ofNullable(uUID));
    }
    
    public void tame(final Player awg) {
        this.setTame(true);
        this.setOwnerUUID(awg.getUUID());
        if (awg instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)awg, this);
        }
    }
    
    @Nullable
    public LivingEntity getOwner() {
        try {
            final UUID uUID2 = this.getOwnerUUID();
            if (uUID2 == null) {
                return null;
            }
            return this.level.getPlayerByUUID(uUID2);
        }
        catch (IllegalArgumentException illegalArgumentException2) {
            return null;
        }
    }
    
    @Override
    public boolean canAttack(final LivingEntity aix) {
        return !this.isOwnedBy(aix) && super.canAttack(aix);
    }
    
    public boolean isOwnedBy(final LivingEntity aix) {
        return aix == this.getOwner();
    }
    
    public SitGoal getSitGoal() {
        return this.sitGoal;
    }
    
    public boolean wantsToAttack(final LivingEntity aix1, final LivingEntity aix2) {
        return true;
    }
    
    @Override
    public Team getTeam() {
        if (this.isTame()) {
            final LivingEntity aix2 = this.getOwner();
            if (aix2 != null) {
                return aix2.getTeam();
            }
        }
        return super.getTeam();
    }
    
    @Override
    public boolean isAlliedTo(final Entity aio) {
        if (this.isTame()) {
            final LivingEntity aix3 = this.getOwner();
            if (aio == aix3) {
                return true;
            }
            if (aix3 != null) {
                return aix3.isAlliedTo(aio);
            }
        }
        return super.isAlliedTo(aio);
    }
    
    @Override
    public void die(final DamageSource ahx) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
        }
        super.die(ahx);
    }
    
    static {
        DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(TamableAnimal.class, EntityDataSerializers.BYTE);
        DATA_OWNERUUID_ID = SynchedEntityData.<Optional<UUID>>defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);
    }
}
