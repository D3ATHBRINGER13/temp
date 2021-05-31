package net.minecraft.world.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.EntityDataAccessor;

public abstract class AgableMob extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;
    
    protected AgableMob(final EntityType<? extends AgableMob> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Nullable
    public abstract AgableMob getBreedOffspring(final AgableMob aim);
    
    protected void onOffspringSpawnedFromEgg(final Player awg, final AgableMob aim) {
    }
    
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        final Item bce5 = bcj4.getItem();
        if (bce5 instanceof SpawnEggItem && ((SpawnEggItem)bce5).spawnsEntity(bcj4.getTag(), this.getType())) {
            if (!this.level.isClientSide) {
                final AgableMob aim6 = this.getBreedOffspring(this);
                if (aim6 != null) {
                    aim6.setAge(-24000);
                    aim6.moveTo(this.x, this.y, this.z, 0.0f, 0.0f);
                    this.level.addFreshEntity(aim6);
                    if (bcj4.hasCustomHoverName()) {
                        aim6.setCustomName(bcj4.getHoverName());
                    }
                    this.onOffspringSpawnedFromEgg(awg, aim6);
                    if (!awg.abilities.instabuild) {
                        bcj4.shrink(1);
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(AgableMob.DATA_BABY_ID, false);
    }
    
    public int getAge() {
        if (this.level.isClientSide) {
            return this.entityData.<Boolean>get(AgableMob.DATA_BABY_ID) ? -1 : 1;
        }
        return this.age;
    }
    
    public void ageUp(final int integer, final boolean boolean2) {
        final int integer3;
        int integer2 = integer3 = this.getAge();
        integer2 += integer * 20;
        if (integer2 > 0) {
            integer2 = 0;
        }
        final int integer4 = integer2 - integer3;
        this.setAge(integer2);
        if (boolean2) {
            this.forcedAge += integer4;
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }
        if (this.getAge() == 0) {
            this.setAge(this.forcedAge);
        }
    }
    
    public void ageUp(final int integer) {
        this.ageUp(integer, false);
    }
    
    public void setAge(final int integer) {
        final int integer2 = this.age;
        this.age = integer;
        if ((integer2 < 0 && integer >= 0) || (integer2 >= 0 && integer < 0)) {
            this.entityData.<Boolean>set(AgableMob.DATA_BABY_ID, integer < 0);
            this.ageBoundaryReached();
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Age", this.getAge());
        id.putInt("ForcedAge", this.forcedAge);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setAge(id.getInt("Age"));
        this.forcedAge = id.getInt("ForcedAge");
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (AgableMob.DATA_BABY_ID.equals(qk)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), 0.0, 0.0, 0.0);
                }
                --this.forcedAgeTimer;
            }
        }
        else if (this.isAlive()) {
            int integer2 = this.getAge();
            if (integer2 < 0) {
                ++integer2;
                this.setAge(integer2);
            }
            else if (integer2 > 0) {
                --integer2;
                this.setAge(integer2);
            }
        }
    }
    
    protected void ageBoundaryReached() {
    }
    
    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }
    
    static {
        DATA_BABY_ID = SynchedEntityData.<Boolean>defineId(AgableMob.class, EntityDataSerializers.BOOLEAN);
    }
}
