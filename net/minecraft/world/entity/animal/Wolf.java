package net.minecraft.world.entity.animal;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Random;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Creeper;
import java.util.UUID;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import javax.annotation.Nullable;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.TamableAnimal;

public class Wolf extends TamableAnimal {
    private static final EntityDataAccessor<Float> DATA_HEALTH_ID;
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID;
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
    public static final Predicate<LivingEntity> PREY_SELECTOR;
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    
    public Wolf(final EntityType<? extends Wolf> ais, final Level bhr) {
        super(ais, bhr);
        this.setTame(false);
    }
    
    @Override
    protected void registerGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, this.sitGoal);
        this.goalSelector.addGoal(3, new WolfAvoidEntityGoal<>(this, Llama.class, 24.0f, 1.5, 1.5));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4f));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Animal.class, false, Wolf.PREY_SELECTOR));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
        if (this.isTame()) {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        }
        else {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    @Override
    public void setTarget(@Nullable final LivingEntity aix) {
        super.setTarget(aix);
        if (aix == null) {
            this.setAngry(false);
        }
        else if (!this.isTame()) {
            this.setAngry(true);
        }
    }
    
    @Override
    protected void customServerAiStep() {
        this.entityData.<Float>set(Wolf.DATA_HEALTH_ID, this.getHealth());
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Float>define(Wolf.DATA_HEALTH_ID, this.getHealth());
        this.entityData.<Boolean>define(Wolf.DATA_INTERESTED_ID, false);
        this.entityData.<Integer>define(Wolf.DATA_COLLAR_COLOR, DyeColor.RED.getId());
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15f, 1.0f);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("Angry", this.isAngry());
        id.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setAngry(id.getBoolean("Angry"));
        if (id.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(id.getInt("CollarColor")));
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAngry()) {
            return SoundEvents.WOLF_GROWL;
        }
        if (this.random.nextInt(3) != 0) {
            return SoundEvents.WOLF_AMBIENT;
        }
        if (this.isTame() && this.entityData.<Float>get(Wolf.DATA_HEALTH_ID) < 10.0f) {
            return SoundEvents.WOLF_WHINE;
        }
        return SoundEvents.WOLF_PANT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.WOLF_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
            this.isShaking = true;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
            this.level.broadcastEntityEvent(this, (byte)8);
        }
        if (!this.level.isClientSide && this.getTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        this.interestedAngleO = this.interestedAngle;
        if (this.isInterested()) {
            this.interestedAngle += (1.0f - this.interestedAngle) * 0.4f;
        }
        else {
            this.interestedAngle += (0.0f - this.interestedAngle) * 0.4f;
        }
        if (this.isInWaterRainOrBubble()) {
            this.isWet = true;
            this.isShaking = false;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
        }
        else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.shakeAnim == 0.0f) {
                this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            }
            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05f;
            if (this.shakeAnimO >= 2.0f) {
                this.isWet = false;
                this.isShaking = false;
                this.shakeAnimO = 0.0f;
                this.shakeAnim = 0.0f;
            }
            if (this.shakeAnim > 0.4f) {
                final float float2 = (float)this.getBoundingBox().minY;
                final int integer3 = (int)(Mth.sin((this.shakeAnim - 0.4f) * 3.1415927f) * 7.0f);
                final Vec3 csi4 = this.getDeltaMovement();
                for (int integer4 = 0; integer4 < integer3; ++integer4) {
                    final float float3 = (this.random.nextFloat() * 2.0f - 1.0f) * this.getBbWidth() * 0.5f;
                    final float float4 = (this.random.nextFloat() * 2.0f - 1.0f) * this.getBbWidth() * 0.5f;
                    this.level.addParticle(ParticleTypes.SPLASH, this.x + float3, float2 + 0.8f, this.z + float4, csi4.x, csi4.y, csi4.z);
                }
            }
        }
    }
    
    @Override
    public void die(final DamageSource ahx) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0f;
        this.shakeAnim = 0.0f;
        super.die(ahx);
    }
    
    public boolean isWet() {
        return this.isWet;
    }
    
    public float getWetShade(final float float1) {
        return 0.75f + Mth.lerp(float1, this.shakeAnimO, this.shakeAnim) / 2.0f * 0.25f;
    }
    
    public float getBodyRollAngle(final float float1, final float float2) {
        float float3 = (Mth.lerp(float1, this.shakeAnimO, this.shakeAnim) + float2) / 1.8f;
        if (float3 < 0.0f) {
            float3 = 0.0f;
        }
        else if (float3 > 1.0f) {
            float3 = 1.0f;
        }
        return Mth.sin(float3 * 3.1415927f) * Mth.sin(float3 * 3.1415927f * 11.0f) * 0.15f * 3.1415927f;
    }
    
    public float getHeadRollAngle(final float float1) {
        return Mth.lerp(float1, this.interestedAngleO, this.interestedAngle) * 0.15f * 3.1415927f;
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.8f;
    }
    
    @Override
    public int getMaxHeadXRot() {
        if (this.isSitting()) {
            return 20;
        }
        return super.getMaxHeadXRot();
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        final Entity aio4 = ahx.getEntity();
        if (this.sitGoal != null) {
            this.sitGoal.wantToSit(false);
        }
        if (aio4 != null && !(aio4 instanceof Player) && !(aio4 instanceof AbstractArrow)) {
            float2 = (float2 + 1.0f) / 2.0f;
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        final boolean boolean3 = aio.hurt(DamageSource.mobAttack(this), (float)(int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
        if (boolean3) {
            this.doEnchantDamageEffects(this, aio);
        }
        return boolean3;
    }
    
    @Override
    public void setTame(final boolean boolean1) {
        super.setTame(boolean1);
        if (boolean1) {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        }
        else {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        final Item bce5 = bcj4.getItem();
        if (this.isTame()) {
            if (!bcj4.isEmpty()) {
                if (bce5.isEdible()) {
                    if (bce5.getFoodProperties().isMeat() && this.entityData.<Float>get(Wolf.DATA_HEALTH_ID) < 20.0f) {
                        if (!awg.abilities.instabuild) {
                            bcj4.shrink(1);
                        }
                        this.heal((float)bce5.getFoodProperties().getNutrition());
                        return true;
                    }
                }
                else if (bce5 instanceof DyeItem) {
                    final DyeColor bbg6 = ((DyeItem)bce5).getDyeColor();
                    if (bbg6 != this.getCollarColor()) {
                        this.setCollarColor(bbg6);
                        if (!awg.abilities.instabuild) {
                            bcj4.shrink(1);
                        }
                        return true;
                    }
                }
            }
            if (this.isOwnedBy(awg) && !this.level.isClientSide && !this.isFood(bcj4)) {
                this.sitGoal.wantToSit(!this.isSitting());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
            }
        }
        else if (bce5 == Items.BONE && !this.isAngry()) {
            if (!awg.abilities.instabuild) {
                bcj4.shrink(1);
            }
            if (!this.level.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(awg);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.sitGoal.wantToSit(true);
                    this.setHealth(20.0f);
                    this.spawnTamingParticles(true);
                    this.level.broadcastEntityEvent(this, (byte)7);
                }
                else {
                    this.spawnTamingParticles(false);
                    this.level.broadcastEntityEvent(this, (byte)6);
                }
            }
            return true;
        }
        return super.mobInteract(awg, ahi);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804f;
        }
        if (this.isTame()) {
            return (0.55f - (this.getMaxHealth() - this.entityData.<Float>get(Wolf.DATA_HEALTH_ID)) * 0.02f) * 3.1415927f;
        }
        return 0.62831855f;
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        final Item bce3 = bcj.getItem();
        return bce3.isEdible() && bce3.getFoodProperties().isMeat();
    }
    
    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }
    
    public boolean isAngry() {
        return (this.entityData.<Byte>get(Wolf.DATA_FLAGS_ID) & 0x2) != 0x0;
    }
    
    public void setAngry(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(Wolf.DATA_FLAGS_ID);
        if (boolean1) {
            this.entityData.<Byte>set(Wolf.DATA_FLAGS_ID, (byte)(byte3 | 0x2));
        }
        else {
            this.entityData.<Byte>set(Wolf.DATA_FLAGS_ID, (byte)(byte3 & 0xFFFFFFFD));
        }
    }
    
    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.<Integer>get(Wolf.DATA_COLLAR_COLOR));
    }
    
    public void setCollarColor(final DyeColor bbg) {
        this.entityData.<Integer>set(Wolf.DATA_COLLAR_COLOR, bbg.getId());
    }
    
    @Override
    public Wolf getBreedOffspring(final AgableMob aim) {
        final Wolf arz3 = EntityType.WOLF.create(this.level);
        final UUID uUID4 = this.getOwnerUUID();
        if (uUID4 != null) {
            arz3.setOwnerUUID(uUID4);
            arz3.setTame(true);
        }
        return arz3;
    }
    
    public void setIsInterested(final boolean boolean1) {
        this.entityData.<Boolean>set(Wolf.DATA_INTERESTED_ID, boolean1);
    }
    
    @Override
    public boolean canMate(final Animal ara) {
        if (ara == this) {
            return false;
        }
        if (!this.isTame()) {
            return false;
        }
        if (!(ara instanceof Wolf)) {
            return false;
        }
        final Wolf arz3 = (Wolf)ara;
        return arz3.isTame() && !arz3.isSitting() && this.isInLove() && arz3.isInLove();
    }
    
    public boolean isInterested() {
        return this.entityData.<Boolean>get(Wolf.DATA_INTERESTED_ID);
    }
    
    @Override
    public boolean wantsToAttack(final LivingEntity aix1, final LivingEntity aix2) {
        if (aix1 instanceof Creeper || aix1 instanceof Ghast) {
            return false;
        }
        if (aix1 instanceof Wolf) {
            final Wolf arz4 = (Wolf)aix1;
            if (arz4.isTame() && arz4.getOwner() == aix2) {
                return false;
            }
        }
        return (!(aix1 instanceof Player) || !(aix2 instanceof Player) || ((Player)aix2).canHarmPlayer((Player)aix1)) && (!(aix1 instanceof AbstractHorse) || !((AbstractHorse)aix1).isTamed()) && (!(aix1 instanceof Cat) || !((Cat)aix1).isTame());
    }
    
    @Override
    public boolean canBeLeashed(final Player awg) {
        return !this.isAngry() && super.canBeLeashed(awg);
    }
    
    static {
        DATA_HEALTH_ID = SynchedEntityData.<Float>defineId(Wolf.class, EntityDataSerializers.FLOAT);
        DATA_INTERESTED_ID = SynchedEntityData.<Boolean>defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
        DATA_COLLAR_COLOR = SynchedEntityData.<Integer>defineId(Wolf.class, EntityDataSerializers.INT);
        PREY_SELECTOR = (aix -> {
            final EntityType<?> ais2 = aix.getType();
            return ais2 == EntityType.SHEEP || ais2 == EntityType.RABBIT || ais2 == EntityType.FOX;
        });
    }
    
    class WolfAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Wolf wolf;
        
        public WolfAvoidEntityGoal(final Wolf arz2, final Class<T> class3, final float float4, final double double5, final double double6) {
            super(arz2, class3, float4, double5, double6);
            this.wolf = arz2;
        }
        
        @Override
        public boolean canUse() {
            return super.canUse() && this.toAvoid instanceof Llama && !this.wolf.isTame() && this.avoidLlama((Llama)this.toAvoid);
        }
        
        private boolean avoidLlama(final Llama ase) {
            return ase.getStrength() >= Wolf.this.random.nextInt(5);
        }
        
        @Override
        public void start() {
            Wolf.this.setTarget(null);
            super.start();
        }
        
        @Override
        public void tick() {
            Wolf.this.setTarget(null);
            super.tick();
        }
    }
}
