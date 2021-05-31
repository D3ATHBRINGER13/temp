package net.minecraft.world.entity.monster;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import java.util.Random;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.LocalDate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Chicken;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.Difficulty;
import java.util.function.Predicate;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class Zombie extends Monster {
    protected static final Attribute SPAWN_REINFORCEMENTS_CHANCE;
    private static final UUID SPEED_MODIFIER_BABY_UUID;
    private static final AttributeModifier SPEED_MODIFIER_BABY;
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
    private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID;
    private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID;
    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE;
    private final BreakDoorGoal breakDoorGoal;
    private boolean canBreakDoors;
    private int inWaterTime;
    private int conversionTime;
    
    public Zombie(final EntityType<? extends Zombie> ais, final Level bhr) {
        super(ais, bhr);
        this.breakDoorGoal = new BreakDoorGoal(this, Zombie.DOOR_BREAKING_PREDICATE);
    }
    
    public Zombie(final Level bhr) {
        this(EntityType.ZOMBIE, bhr);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new ZombieAttackTurtleEggGoal(this, 1.0, 3));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }
    
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(PigZombie.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0);
        this.getAttributes().registerAttribute(Zombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.10000000149011612);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().<Boolean>define(Zombie.DATA_BABY_ID, false);
        this.getEntityData().<Integer>define(Zombie.DATA_SPECIAL_TYPE_ID, 0);
        this.getEntityData().<Boolean>define(Zombie.DATA_DROWNED_CONVERSION_ID, false);
    }
    
    public boolean isUnderWaterConverting() {
        return this.getEntityData().<Boolean>get(Zombie.DATA_DROWNED_CONVERSION_ID);
    }
    
    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }
    
    public void setCanBreakDoors(final boolean boolean1) {
        if (this.supportsBreakDoorGoal()) {
            if (this.canBreakDoors != boolean1) {
                this.canBreakDoors = boolean1;
                ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(boolean1);
                if (boolean1) {
                    this.goalSelector.addGoal(1, this.breakDoorGoal);
                }
                else {
                    this.goalSelector.removeGoal(this.breakDoorGoal);
                }
            }
        }
        else if (this.canBreakDoors) {
            this.goalSelector.removeGoal(this.breakDoorGoal);
            this.canBreakDoors = false;
        }
    }
    
    protected boolean supportsBreakDoorGoal() {
        return true;
    }
    
    public boolean isBaby() {
        return this.getEntityData().<Boolean>get(Zombie.DATA_BABY_ID);
    }
    
    @Override
    protected int getExperienceReward(final Player awg) {
        if (this.isBaby()) {
            this.xpReward *= (int)2.5f;
        }
        return super.getExperienceReward(awg);
    }
    
    public void setBaby(final boolean boolean1) {
        this.getEntityData().<Boolean>set(Zombie.DATA_BABY_ID, boolean1);
        if (this.level != null && !this.level.isClientSide) {
            final AttributeInstance ajo3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            ajo3.removeModifier(Zombie.SPEED_MODIFIER_BABY);
            if (boolean1) {
                ajo3.addModifier(Zombie.SPEED_MODIFIER_BABY);
            }
        }
    }
    
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (Zombie.DATA_BABY_ID.equals(qk)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(qk);
    }
    
    protected boolean convertsInWater() {
        return true;
    }
    
    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive()) {
            if (this.isUnderWaterConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.doUnderWaterConversion();
                }
            }
            else if (this.convertsInWater()) {
                if (this.isUnderLiquid(FluidTags.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.startUnderWaterConversion(300);
                    }
                }
                else {
                    this.inWaterTime = -1;
                }
            }
        }
        super.tick();
    }
    
    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean boolean2 = this.isSunSensitive() && this.isSunBurnTick();
            if (boolean2) {
                final ItemStack bcj3 = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!bcj3.isEmpty()) {
                    if (bcj3.isDamageableItem()) {
                        bcj3.setDamageValue(bcj3.getDamageValue() + this.random.nextInt(2));
                        if (bcj3.getDamageValue() >= bcj3.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    boolean2 = false;
                }
                if (boolean2) {
                    this.setSecondsOnFire(8);
                }
            }
        }
        super.aiStep();
    }
    
    private void startUnderWaterConversion(final int integer) {
        this.conversionTime = integer;
        this.getEntityData().<Boolean>set(Zombie.DATA_DROWNED_CONVERSION_ID, true);
    }
    
    protected void doUnderWaterConversion() {
        this.convertTo(EntityType.DROWNED);
        this.level.levelEvent(null, 1040, new BlockPos(this), 0);
    }
    
    protected void convertTo(final EntityType<? extends Zombie> ais) {
        if (this.removed) {
            return;
        }
        final Zombie avm3 = (Zombie)ais.create(this.level);
        avm3.copyPosition(this);
        avm3.setCanPickUpLoot(this.canPickUpLoot());
        avm3.setCanBreakDoors(avm3.supportsBreakDoorGoal() && this.canBreakDoors());
        avm3.handleAttributes(avm3.level.getCurrentDifficultyAt(new BlockPos(avm3)).getSpecialMultiplier());
        avm3.setBaby(this.isBaby());
        avm3.setNoAi(this.isNoAi());
        for (final EquipmentSlot ait7 : EquipmentSlot.values()) {
            final ItemStack bcj8 = this.getItemBySlot(ait7);
            if (!bcj8.isEmpty()) {
                avm3.setItemSlot(ait7, bcj8.copy());
                avm3.setDropChance(ait7, this.getEquipmentDropChance(ait7));
                bcj8.setCount(0);
            }
        }
        if (this.hasCustomName()) {
            avm3.setCustomName(this.getCustomName());
            avm3.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.level.addFreshEntity(avm3);
        this.remove();
    }
    
    protected boolean isSunSensitive() {
        return true;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (super.hurt(ahx, float2)) {
            LivingEntity aix4 = this.getTarget();
            if (aix4 == null && ahx.getEntity() instanceof LivingEntity) {
                aix4 = (LivingEntity)ahx.getEntity();
            }
            if (aix4 != null && this.level.getDifficulty() == Difficulty.HARD && this.random.nextFloat() < this.getAttribute(Zombie.SPAWN_REINFORCEMENTS_CHANCE).getValue() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                final int integer5 = Mth.floor(this.x);
                final int integer6 = Mth.floor(this.y);
                final int integer7 = Mth.floor(this.z);
                final Zombie avm8 = new Zombie(this.level);
                for (int integer8 = 0; integer8 < 50; ++integer8) {
                    final int integer9 = integer5 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    final int integer10 = integer6 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    final int integer11 = integer7 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    final BlockPos ew13 = new BlockPos(integer9, integer10 - 1, integer11);
                    if (this.level.getBlockState(ew13).entityCanStandOn(this.level, ew13, avm8) && this.level.getMaxLocalRawBrightness(new BlockPos(integer9, integer10, integer11)) < 10) {
                        avm8.setPos(integer9, integer10, integer11);
                        if (!this.level.hasNearbyAlivePlayer(integer9, integer10, integer11, 7.0) && this.level.isUnobstructed(avm8) && this.level.noCollision(avm8) && !this.level.containsAnyLiquid(avm8.getBoundingBox())) {
                            this.level.addFreshEntity(avm8);
                            avm8.setTarget(aix4);
                            avm8.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(avm8)), MobSpawnType.REINFORCEMENT, null, null);
                            this.getAttribute(Zombie.SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806, AttributeModifier.Operation.ADDITION));
                            avm8.getAttribute(Zombie.SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806, AttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        final boolean boolean3 = super.doHurtTarget(aio);
        if (boolean3) {
            final float float4 = this.level.getCurrentDifficultyAt(new BlockPos(this)).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < float4 * 0.3f) {
                aio.setSecondsOnFire(2 * (int)float4);
            }
        }
        return boolean3;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.ZOMBIE_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
    
    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }
    
    public MobType getMobType() {
        return MobType.UNDEAD;
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        super.populateDefaultEquipmentSlots(ahh);
        if (this.random.nextFloat() < ((this.level.getDifficulty() == Difficulty.HARD) ? 0.05f : 0.01f)) {
            final int integer3 = this.random.nextInt(3);
            if (integer3 == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            }
            else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.isBaby()) {
            id.putBoolean("IsBaby", true);
        }
        id.putBoolean("CanBreakDoors", this.canBreakDoors());
        id.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
        id.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.getBoolean("IsBaby")) {
            this.setBaby(true);
        }
        this.setCanBreakDoors(id.getBoolean("CanBreakDoors"));
        this.inWaterTime = id.getInt("InWaterTime");
        if (id.contains("DrownedConversionTime", 99) && id.getInt("DrownedConversionTime") > -1) {
            this.startUnderWaterConversion(id.getInt("DrownedConversionTime"));
        }
    }
    
    public void killed(final LivingEntity aix) {
        super.killed(aix);
        if ((this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) && aix instanceof Villager) {
            if (this.level.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return;
            }
            final Villager avt3 = (Villager)aix;
            final ZombieVillager avn4 = EntityType.ZOMBIE_VILLAGER.create(this.level);
            avn4.copyPosition(avt3);
            avt3.remove();
            avn4.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(avn4)), MobSpawnType.CONVERSION, new ZombieGroupData(false), null);
            avn4.setVillagerData(avt3.getVillagerData());
            avn4.setGossips((Tag)avt3.getGossips().store((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
            avn4.setTradeOffers(avt3.getOffers().createTag());
            avn4.setVillagerXp(avt3.getVillagerXp());
            avn4.setBaby(avt3.isBaby());
            avn4.setNoAi(avt3.isNoAi());
            if (avt3.hasCustomName()) {
                avn4.setCustomName(avt3.getCustomName());
                avn4.setCustomNameVisible(avt3.isCustomNameVisible());
            }
            this.level.addFreshEntity(avn4);
            this.level.levelEvent(null, 1026, new BlockPos(this), 0);
        }
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return this.isBaby() ? 0.93f : 1.74f;
    }
    
    @Override
    protected boolean canHoldItem(final ItemStack bcj) {
        return (bcj.getItem() != Items.EGG || !this.isBaby() || !this.isPassenger()) && super.canHoldItem(bcj);
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        final float float7 = ahh.getSpecialMultiplier();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55f * float7);
        if (ajj == null) {
            ajj = new ZombieGroupData(bhs.getRandom().nextFloat() < 0.05f);
        }
        if (ajj instanceof ZombieGroupData) {
            final ZombieGroupData b8 = (ZombieGroupData)ajj;
            if (b8.isBaby) {
                this.setBaby(true);
                if (bhs.getRandom().nextFloat() < 0.05) {
                    final List<Chicken> list9 = bhs.<Chicken>getEntitiesOfClass((java.lang.Class<? extends Chicken>)Chicken.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), (java.util.function.Predicate<? super Chicken>)EntitySelector.ENTITY_NOT_BEING_RIDDEN);
                    if (!list9.isEmpty()) {
                        final Chicken arc10 = (Chicken)list9.get(0);
                        arc10.setChickenJockey(true);
                        this.startRiding(arc10);
                    }
                }
                else if (bhs.getRandom().nextFloat() < 0.05) {
                    final Chicken arc11 = EntityType.CHICKEN.create(this.level);
                    arc11.moveTo(this.x, this.y, this.z, this.yRot, 0.0f);
                    arc11.finalizeSpawn(bhs, ahh, MobSpawnType.JOCKEY, null, null);
                    arc11.setChickenJockey(true);
                    bhs.addFreshEntity(arc11);
                    this.startRiding(arc11);
                }
            }
            this.setCanBreakDoors(this.supportsBreakDoorGoal() && this.random.nextFloat() < float7 * 0.1f);
            this.populateDefaultEquipmentSlots(ahh);
            this.populateDefaultEquipmentEnchantments(ahh);
        }
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            final LocalDate localDate8 = LocalDate.now();
            final int integer9 = localDate8.get((TemporalField)ChronoField.DAY_OF_MONTH);
            final int integer10 = localDate8.get((TemporalField)ChronoField.MONTH_OF_YEAR);
            if (integer10 == 10 && integer9 == 31 && this.random.nextFloat() < 0.25f) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((this.random.nextFloat() < 0.1f) ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0f;
            }
        }
        this.handleAttributes(float7);
        return ajj;
    }
    
    protected void handleAttributes(final float float1) {
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806, AttributeModifier.Operation.ADDITION));
        final double double3 = this.random.nextDouble() * 1.5 * float1;
        if (double3 > 1.0) {
            this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", double3, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (this.random.nextFloat() < float1 * 0.05f) {
            this.getAttribute(Zombie.SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, AttributeModifier.Operation.ADDITION));
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setCanBreakDoors(this.supportsBreakDoorGoal());
        }
    }
    
    public double getRidingHeight() {
        return this.isBaby() ? 0.0 : -0.45;
    }
    
    @Override
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
        super.dropCustomDeathLoot(ahx, integer, boolean3);
        final Entity aio5 = ahx.getEntity();
        if (aio5 instanceof Creeper) {
            final Creeper aue6 = (Creeper)aio5;
            if (aue6.canDropMobsSkull()) {
                aue6.increaseDroppedSkulls();
                final ItemStack bcj7 = this.getSkull();
                if (!bcj7.isEmpty()) {
                    this.spawnAtLocation(bcj7);
                }
            }
        }
    }
    
    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }
    
    static {
        SPAWN_REINFORCEMENTS_CHANCE = new RangedAttribute((Attribute)null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0).importLegacyName("Spawn Reinforcements Chance");
        SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
        SPEED_MODIFIER_BABY = new AttributeModifier(Zombie.SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
        DATA_BABY_ID = SynchedEntityData.<Boolean>defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
        DATA_SPECIAL_TYPE_ID = SynchedEntityData.<Integer>defineId(Zombie.class, EntityDataSerializers.INT);
        DATA_DROWNED_CONVERSION_ID = SynchedEntityData.<Boolean>defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
        DOOR_BREAKING_PREDICATE = (ahg -> ahg == Difficulty.HARD);
    }
    
    public class ZombieGroupData implements SpawnGroupData {
        public final boolean isBaby;
        
        private ZombieGroupData(final boolean boolean2) {
            this.isBaby = boolean2;
        }
    }
    
    class ZombieAttackTurtleEggGoal extends RemoveBlockGoal {
        ZombieAttackTurtleEggGoal(final PathfinderMob aje, final double double3, final int integer) {
            super(Blocks.TURTLE_EGG, aje, double3, integer);
        }
        
        @Override
        public void playDestroyProgressSound(final LevelAccessor bhs, final BlockPos ew) {
            bhs.playSound(null, ew, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5f, 0.9f + Zombie.this.random.nextFloat() * 0.2f);
        }
        
        @Override
        public void playBreakSound(final Level bhr, final BlockPos ew) {
            bhr.playSound(null, ew, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + bhr.random.nextFloat() * 0.2f);
        }
        
        @Override
        public double acceptedDistance() {
            return 1.14;
        }
    }
}
