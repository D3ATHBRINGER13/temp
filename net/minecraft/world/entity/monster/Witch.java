package net.minecraft.world.entity.monster;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.alchemy.Potion;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;
import net.minecraft.world.entity.raid.Raider;

public class Witch extends Raider implements RangedAttackMob {
    private static final UUID SPEED_MODIFIER_DRINKING_UUID;
    private static final AttributeModifier SPEED_MODIFIER_DRINKING;
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM;
    private int usingTime;
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;
    
    public Witch(final EntityType<? extends Witch> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<Raider>(this, Raider.class, true, (Predicate<LivingEntity>)(aix -> aix != null && this.hasActiveRaid() && aix.getType() != EntityType.WITCH));
        this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<Player>(this, Player.class, 10, true, false, null);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 60, 10.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] { Raider.class }));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().<Boolean>define(Witch.DATA_USING_ITEM, false);
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.WITCH_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }
    
    public void setUsingItem(final boolean boolean1) {
        this.getEntityData().<Boolean>set(Witch.DATA_USING_ITEM, boolean1);
    }
    
    public boolean isDrinkingPotion() {
        return this.getEntityData().<Boolean>get(Witch.DATA_USING_ITEM);
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
            this.healRaidersGoal.decrementCooldown();
            if (this.healRaidersGoal.getCooldown() <= 0) {
                this.attackPlayersGoal.setCanAttack(true);
            }
            else {
                this.attackPlayersGoal.setCanAttack(false);
            }
            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    final ItemStack bcj2 = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (bcj2.getItem() == Items.POTION) {
                        final List<MobEffectInstance> list3 = PotionUtils.getMobEffects(bcj2);
                        if (list3 != null) {
                            for (final MobEffectInstance aii5 : list3) {
                                this.addEffect(new MobEffectInstance(aii5));
                            }
                        }
                    }
                    this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(Witch.SPEED_MODIFIER_DRINKING);
                }
            }
            else {
                Potion bdy2 = null;
                if (this.random.nextFloat() < 0.15f && this.isUnderLiquid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    bdy2 = Potions.WATER_BREATHING;
                }
                else if (this.random.nextFloat() < 0.15f && (this.isOnFire() || (this.getLastDamageSource() != null && this.getLastDamageSource().isFire())) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    bdy2 = Potions.FIRE_RESISTANCE;
                }
                else if (this.random.nextFloat() < 0.05f && this.getHealth() < this.getMaxHealth()) {
                    bdy2 = Potions.HEALING;
                }
                else if (this.random.nextFloat() < 0.5f && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0) {
                    bdy2 = Potions.SWIFTNESS;
                }
                if (bdy2 != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), bdy2));
                    this.usingTime = this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
                    this.level.playSound(null, this.x, this.y, this.z, SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
                    final AttributeInstance ajo3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    ajo3.removeModifier(Witch.SPEED_MODIFIER_DRINKING);
                    ajo3.addModifier(Witch.SPEED_MODIFIER_DRINKING);
                }
            }
            if (this.random.nextFloat() < 7.5E-4f) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
        }
        super.aiStep();
    }
    
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.WITCH_CELEBRATE;
    }
    
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 15) {
            for (int integer3 = 0; integer3 < this.random.nextInt(35) + 10; ++integer3) {
                this.level.addParticle(ParticleTypes.WITCH, this.x + this.random.nextGaussian() * 0.12999999523162842, this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.12999999523162842, this.z + this.random.nextGaussian() * 0.12999999523162842, 0.0, 0.0, 0.0);
            }
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    protected float getDamageAfterMagicAbsorb(final DamageSource ahx, float float2) {
        float2 = super.getDamageAfterMagicAbsorb(ahx, float2);
        if (ahx.getEntity() == this) {
            float2 = 0.0f;
        }
        if (ahx.isMagic()) {
            float2 *= (float)0.15;
        }
        return float2;
    }
    
    @Override
    public void performRangedAttack(final LivingEntity aix, final float float2) {
        if (this.isDrinkingPotion()) {
            return;
        }
        final Vec3 csi4 = aix.getDeltaMovement();
        final double double5 = aix.x + csi4.x - this.x;
        final double double6 = aix.y + aix.getEyeHeight() - 1.100000023841858 - this.y;
        final double double7 = aix.z + csi4.z - this.z;
        final float float3 = Mth.sqrt(double5 * double5 + double7 * double7);
        Potion bdy12 = Potions.HARMING;
        if (aix instanceof Raider) {
            if (aix.getHealth() <= 4.0f) {
                bdy12 = Potions.HEALING;
            }
            else {
                bdy12 = Potions.REGENERATION;
            }
            this.setTarget(null);
        }
        else if (float3 >= 8.0f && !aix.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            bdy12 = Potions.SLOWNESS;
        }
        else if (aix.getHealth() >= 8.0f && !aix.hasEffect(MobEffects.POISON)) {
            bdy12 = Potions.POISON;
        }
        else if (float3 <= 3.0f && !aix.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25f) {
            bdy12 = Potions.WEAKNESS;
        }
        final ThrownPotion axg13 = new ThrownPotion(this.level, this);
        axg13.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), bdy12));
        final ThrownPotion thrownPotion = axg13;
        thrownPotion.xRot += 20.0f;
        axg13.shoot(double5, double6 + float3 * 0.2f, double7, 0.75f, 8.0f);
        this.level.playSound(null, this.x, this.y, this.z, SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        this.level.addFreshEntity(axg13);
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 1.62f;
    }
    
    @Override
    public void applyRaidBuffs(final int integer, final boolean boolean2) {
    }
    
    @Override
    public boolean canBeLeader() {
        return false;
    }
    
    static {
        SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
        SPEED_MODIFIER_DRINKING = new AttributeModifier(Witch.SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25, AttributeModifier.Operation.ADDITION).setSerialize(false);
        DATA_USING_ITEM = SynchedEntityData.<Boolean>defineId(Witch.class, EntityDataSerializers.BOOLEAN);
    }
}
