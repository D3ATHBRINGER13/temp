package net.minecraft.world.entity.monster;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Creeper extends Monster {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR;
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED;
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED;
    private int oldSwell;
    private int swell;
    private int maxSwell;
    private int explosionRadius;
    private int droppedSkulls;
    
    public Creeper(final EntityType<? extends Creeper> ais, final Level bhr) {
        super(ais, bhr);
        this.maxSwell = 30;
        this.explosionRadius = 3;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        return 3 + (int)(this.getHealth() - 1.0f);
    }
    
    public void causeFallDamage(final float float1, final float float2) {
        super.causeFallDamage(float1, float2);
        this.swell += (int)(float1 * 1.5f);
        if (this.swell > this.maxSwell - 5) {
            this.swell = this.maxSwell - 5;
        }
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Creeper.DATA_SWELL_DIR, -1);
        this.entityData.<Boolean>define(Creeper.DATA_IS_POWERED, false);
        this.entityData.<Boolean>define(Creeper.DATA_IS_IGNITED, false);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.entityData.<Boolean>get(Creeper.DATA_IS_POWERED)) {
            id.putBoolean("powered", true);
        }
        id.putShort("Fuse", (short)this.maxSwell);
        id.putByte("ExplosionRadius", (byte)this.explosionRadius);
        id.putBoolean("ignited", this.isIgnited());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.entityData.<Boolean>set(Creeper.DATA_IS_POWERED, id.getBoolean("powered"));
        if (id.contains("Fuse", 99)) {
            this.maxSwell = id.getShort("Fuse");
        }
        if (id.contains("ExplosionRadius", 99)) {
            this.explosionRadius = id.getByte("ExplosionRadius");
        }
        if (id.getBoolean("ignited")) {
            this.ignite();
        }
    }
    
    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }
            final int integer2 = this.getSwellDir();
            if (integer2 > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f);
            }
            this.swell += integer2;
            if (this.swell < 0) {
                this.swell = 0;
            }
            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.CREEPER_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }
    
    @Override
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
        super.dropCustomDeathLoot(ahx, integer, boolean3);
        final Entity aio5 = ahx.getEntity();
        if (aio5 != this && aio5 instanceof Creeper) {
            final Creeper aue6 = (Creeper)aio5;
            if (aue6.canDropMobsSkull()) {
                aue6.increaseDroppedSkulls();
                this.spawnAtLocation(Items.CREEPER_HEAD);
            }
        }
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        return true;
    }
    
    public boolean isPowered() {
        return this.entityData.<Boolean>get(Creeper.DATA_IS_POWERED);
    }
    
    public float getSwelling(final float float1) {
        return Mth.lerp(float1, (float)this.oldSwell, (float)this.swell) / (this.maxSwell - 2);
    }
    
    public int getSwellDir() {
        return this.entityData.<Integer>get(Creeper.DATA_SWELL_DIR);
    }
    
    public void setSwellDir(final int integer) {
        this.entityData.<Integer>set(Creeper.DATA_SWELL_DIR, integer);
    }
    
    public void thunderHit(final LightningBolt atu) {
        super.thunderHit(atu);
        this.entityData.<Boolean>set(Creeper.DATA_IS_POWERED, true);
    }
    
    @Override
    protected boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.FLINT_AND_STEEL) {
            this.level.playSound(awg, this.x, this.y, this.z, SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            awg.swing(ahi);
            if (!this.level.isClientSide) {
                this.ignite();
                bcj4.<Player>hurtAndBreak(1, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
                return true;
            }
        }
        return super.mobInteract(awg, ahi);
    }
    
    private void explodeCreeper() {
        if (!this.level.isClientSide) {
            final Explosion.BlockInteraction a2 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            final float float3 = this.isPowered() ? 2.0f : 1.0f;
            this.dead = true;
            this.level.explode(this, this.x, this.y, this.z, this.explosionRadius * float3, a2);
            this.remove();
            this.spawnLingeringCloud();
        }
    }
    
    private void spawnLingeringCloud() {
        final Collection<MobEffectInstance> collection2 = this.getActiveEffects();
        if (!collection2.isEmpty()) {
            final AreaEffectCloud ain3 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
            ain3.setRadius(2.5f);
            ain3.setRadiusOnUse(-0.5f);
            ain3.setWaitTime(10);
            ain3.setDuration(ain3.getDuration() / 2);
            ain3.setRadiusPerTick(-ain3.getRadius() / ain3.getDuration());
            for (final MobEffectInstance aii5 : collection2) {
                ain3.addEffect(new MobEffectInstance(aii5));
            }
            this.level.addFreshEntity(ain3);
        }
    }
    
    public boolean isIgnited() {
        return this.entityData.<Boolean>get(Creeper.DATA_IS_IGNITED);
    }
    
    public void ignite() {
        this.entityData.<Boolean>set(Creeper.DATA_IS_IGNITED, true);
    }
    
    public boolean canDropMobsSkull() {
        return this.isPowered() && this.droppedSkulls < 1;
    }
    
    public void increaseDroppedSkulls() {
        ++this.droppedSkulls;
    }
    
    static {
        DATA_SWELL_DIR = SynchedEntityData.<Integer>defineId(Creeper.class, EntityDataSerializers.INT);
        DATA_IS_POWERED = SynchedEntityData.<Boolean>defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
        DATA_IS_IGNITED = SynchedEntityData.<Boolean>defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
    }
}
