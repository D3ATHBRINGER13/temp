package net.minecraft.world.entity.animal;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.item.Items;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.HashMap;
import net.minecraft.sounds.SoundSource;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LogBlock;
import net.minecraft.tags.BlockTags;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerFlyingGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Mob;
import java.util.function.Predicate;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Parrot extends ShoulderRidingEntity implements FlyingAnimal {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID;
    private static final Predicate<Mob> NOT_PARROT_PREDICATE;
    private static final Item POISONOUS_FOOD;
    private static final Set<Item> TAME_FOOD;
    private static final Map<EntityType<?>, SoundEvent> MOB_SOUND_MAP;
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping;
    private boolean partyParrot;
    private BlockPos jukebox;
    
    public Parrot(final EntityType<? extends Parrot> ais, final Level bhr) {
        super(ais, bhr);
        this.flapping = 1.0f;
        this.moveControl = new FlyingMoveControl(this);
    }
    
    @Nullable
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.setVariant(this.random.nextInt(5));
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    protected void registerGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(2, this.sitGoal);
        this.goalSelector.addGoal(2, new FollowOwnerFlyingGoal(this, 1.0, 5.0f, 1.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0f, 7.0f));
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4000000059604645);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
    }
    
    protected PathNavigation createNavigation(final Level bhr) {
        final FlyingPathNavigation apn3 = new FlyingPathNavigation(this, bhr);
        apn3.setCanOpenDoors(false);
        apn3.setCanFloat(true);
        apn3.setCanPassDoors(true);
        return apn3;
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.6f;
    }
    
    public void aiStep() {
        imitateNearbyMobs(this.level, this);
        if (this.jukebox == null || !this.jukebox.closerThan(this.position(), 3.46) || this.level.getBlockState(this.jukebox).getBlock() != Blocks.JUKEBOX) {
            this.partyParrot = false;
            this.jukebox = null;
        }
        super.aiStep();
        this.calculateFlapping();
    }
    
    public void setRecordPlayingNearby(final BlockPos ew, final boolean boolean2) {
        this.jukebox = ew;
        this.partyParrot = boolean2;
    }
    
    public boolean isPartyParrot() {
        return this.partyParrot;
    }
    
    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (float)(((this.onGround || this.isPassenger()) ? -1 : 4) * 0.3);
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0f, 1.0f);
        if (!this.onGround && this.flapping < 1.0f) {
            this.flapping = 1.0f;
        }
        this.flapping *= (float)0.9;
        final Vec3 csi2 = this.getDeltaMovement();
        if (!this.onGround && csi2.y < 0.0) {
            this.setDeltaMovement(csi2.multiply(1.0, 0.6, 1.0));
        }
        this.flap += this.flapping * 2.0f;
    }
    
    private static boolean imitateNearbyMobs(final Level bhr, final Entity aio) {
        if (!aio.isAlive() || aio.isSilent() || bhr.random.nextInt(50) != 0) {
            return false;
        }
        final List<Mob> list3 = bhr.<Mob>getEntitiesOfClass((java.lang.Class<? extends Mob>)Mob.class, aio.getBoundingBox().inflate(20.0), (java.util.function.Predicate<? super Mob>)Parrot.NOT_PARROT_PREDICATE);
        if (!list3.isEmpty()) {
            final Mob aiy4 = (Mob)list3.get(bhr.random.nextInt(list3.size()));
            if (!aiy4.isSilent()) {
                final SoundEvent yo5 = getImitatedSound(aiy4.getType());
                bhr.playSound(null, aio.x, aio.y, aio.z, yo5, aio.getSoundSource(), 0.7f, getPitch(bhr.random));
                return true;
            }
        }
        return false;
    }
    
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (!this.isTame() && Parrot.TAME_FOOD.contains(bcj4.getItem())) {
            if (!awg.abilities.instabuild) {
                bcj4.shrink(1);
            }
            if (!this.isSilent()) {
                this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            if (!this.level.isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame(awg);
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
        if (bcj4.getItem() == Parrot.POISONOUS_FOOD) {
            if (!awg.abilities.instabuild) {
                bcj4.shrink(1);
            }
            this.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
            if (awg.isCreative() || !this.isInvulnerable()) {
                this.hurt(DamageSource.playerAttack(awg), Float.MAX_VALUE);
            }
            return true;
        }
        if (!this.level.isClientSide && !this.isFlying() && this.isTame() && this.isOwnedBy(awg)) {
            this.sitGoal.wantToSit(!this.isSitting());
        }
        return super.mobInteract(awg, ahi);
    }
    
    public boolean isFood(final ItemStack bcj) {
        return false;
    }
    
    public static boolean checkParrotSpawnRules(final EntityType<Parrot> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        final Block bmv6 = bhs.getBlockState(ew.below()).getBlock();
        return (bmv6.is(BlockTags.LEAVES) || bmv6 == Blocks.GRASS_BLOCK || bmv6 instanceof LogBlock || bmv6 == Blocks.AIR) && bhs.getRawBrightness(ew, 0) > 8;
    }
    
    public void causeFallDamage(final float float1, final float float2) {
    }
    
    protected void checkFallDamage(final double double1, final boolean boolean2, final BlockState bvt, final BlockPos ew) {
    }
    
    public boolean canMate(final Animal ara) {
        return false;
    }
    
    @Nullable
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return null;
    }
    
    public static void playAmbientSound(final Level bhr, final Entity aio) {
        if (!aio.isSilent() && !imitateNearbyMobs(bhr, aio) && bhr.random.nextInt(200) == 0) {
            bhr.playSound(null, aio.x, aio.y, aio.z, getAmbient(bhr.random), aio.getSoundSource(), 1.0f, getPitch(bhr.random));
        }
    }
    
    public boolean doHurtTarget(final Entity aio) {
        return aio.hurt(DamageSource.mobAttack(this), 3.0f);
    }
    
    @Nullable
    public SoundEvent getAmbientSound() {
        return getAmbient(this.random);
    }
    
    private static SoundEvent getAmbient(final Random random) {
        if (random.nextInt(1000) == 0) {
            final List<EntityType<?>> list2 = (List<EntityType<?>>)Lists.newArrayList((Iterable)Parrot.MOB_SOUND_MAP.keySet());
            return getImitatedSound(list2.get(random.nextInt(list2.size())));
        }
        return SoundEvents.PARROT_AMBIENT;
    }
    
    public static SoundEvent getImitatedSound(final EntityType<?> ais) {
        return (SoundEvent)Parrot.MOB_SOUND_MAP.getOrDefault(ais, SoundEvents.PARROT_AMBIENT);
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.PARROT_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.PARROT_STEP, 0.15f, 1.0f);
    }
    
    protected float playFlySound(final float float1) {
        this.playSound(SoundEvents.PARROT_FLY, 0.15f, 1.0f);
        return float1 + this.flapSpeed / 2.0f;
    }
    
    protected boolean makeFlySound() {
        return true;
    }
    
    protected float getVoicePitch() {
        return getPitch(this.random);
    }
    
    private static float getPitch(final Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }
    
    public boolean isPushable() {
        return true;
    }
    
    protected void doPush(final Entity aio) {
        if (aio instanceof Player) {
            return;
        }
        super.doPush(aio);
    }
    
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (this.sitGoal != null) {
            this.sitGoal.wantToSit(false);
        }
        return super.hurt(ahx, float2);
    }
    
    public int getVariant() {
        return Mth.clamp(this.entityData.<Integer>get(Parrot.DATA_VARIANT_ID), 0, 4);
    }
    
    public void setVariant(final int integer) {
        this.entityData.<Integer>set(Parrot.DATA_VARIANT_ID, integer);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Parrot.DATA_VARIANT_ID, 0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Variant", this.getVariant());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setVariant(id.getInt("Variant"));
    }
    
    public boolean isFlying() {
        return !this.onGround;
    }
    
    static {
        DATA_VARIANT_ID = SynchedEntityData.<Integer>defineId(Parrot.class, EntityDataSerializers.INT);
        NOT_PARROT_PREDICATE = (Predicate)new Predicate<Mob>() {
            public boolean test(@Nullable final Mob aiy) {
                return aiy != null && Parrot.MOB_SOUND_MAP.containsKey(aiy.getType());
            }
        };
        POISONOUS_FOOD = Items.COOKIE;
        TAME_FOOD = (Set)Sets.newHashSet((Object[])new Item[] { Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS });
        MOB_SOUND_MAP = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put(EntityType.BLAZE, SoundEvents.PARROT_IMITATE_BLAZE);
            hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
            hashMap.put(EntityType.CREEPER, SoundEvents.PARROT_IMITATE_CREEPER);
            hashMap.put(EntityType.DROWNED, SoundEvents.PARROT_IMITATE_DROWNED);
            hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.PARROT_IMITATE_ELDER_GUARDIAN);
            hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.PARROT_IMITATE_ENDER_DRAGON);
            hashMap.put(EntityType.ENDERMAN, SoundEvents.PARROT_IMITATE_ENDERMAN);
            hashMap.put(EntityType.ENDERMITE, SoundEvents.PARROT_IMITATE_ENDERMITE);
            hashMap.put(EntityType.EVOKER, SoundEvents.PARROT_IMITATE_EVOKER);
            hashMap.put(EntityType.GHAST, SoundEvents.PARROT_IMITATE_GHAST);
            hashMap.put(EntityType.GUARDIAN, SoundEvents.PARROT_IMITATE_GUARDIAN);
            hashMap.put(EntityType.HUSK, SoundEvents.PARROT_IMITATE_HUSK);
            hashMap.put(EntityType.ILLUSIONER, SoundEvents.PARROT_IMITATE_ILLUSIONER);
            hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.PARROT_IMITATE_MAGMA_CUBE);
            hashMap.put(EntityType.ZOMBIE_PIGMAN, SoundEvents.PARROT_IMITATE_ZOMBIE_PIGMAN);
            hashMap.put(EntityType.PANDA, SoundEvents.PARROT_IMITATE_PANDA);
            hashMap.put(EntityType.PHANTOM, SoundEvents.PARROT_IMITATE_PHANTOM);
            hashMap.put(EntityType.PILLAGER, SoundEvents.PARROT_IMITATE_PILLAGER);
            hashMap.put(EntityType.POLAR_BEAR, SoundEvents.PARROT_IMITATE_POLAR_BEAR);
            hashMap.put(EntityType.RAVAGER, SoundEvents.PARROT_IMITATE_RAVAGER);
            hashMap.put(EntityType.SHULKER, SoundEvents.PARROT_IMITATE_SHULKER);
            hashMap.put(EntityType.SILVERFISH, SoundEvents.PARROT_IMITATE_SILVERFISH);
            hashMap.put(EntityType.SKELETON, SoundEvents.PARROT_IMITATE_SKELETON);
            hashMap.put(EntityType.SLIME, SoundEvents.PARROT_IMITATE_SLIME);
            hashMap.put(EntityType.SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
            hashMap.put(EntityType.STRAY, SoundEvents.PARROT_IMITATE_STRAY);
            hashMap.put(EntityType.VEX, SoundEvents.PARROT_IMITATE_VEX);
            hashMap.put(EntityType.VINDICATOR, SoundEvents.PARROT_IMITATE_VINDICATOR);
            hashMap.put(EntityType.WITCH, SoundEvents.PARROT_IMITATE_WITCH);
            hashMap.put(EntityType.WITHER, SoundEvents.PARROT_IMITATE_WITHER);
            hashMap.put(EntityType.WITHER_SKELETON, SoundEvents.PARROT_IMITATE_WITHER_SKELETON);
            hashMap.put(EntityType.WOLF, SoundEvents.PARROT_IMITATE_WOLF);
            hashMap.put(EntityType.ZOMBIE, SoundEvents.PARROT_IMITATE_ZOMBIE);
            hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.PARROT_IMITATE_ZOMBIE_VILLAGER);
        }));
    }
}
