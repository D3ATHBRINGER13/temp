package net.minecraft.world.entity.animal;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Random;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.damagesource.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import javax.annotation.Nullable;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.function.Predicate;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Panda extends Animal {
    private static final EntityDataAccessor<Integer> UNHAPPY_COUNTER;
    private static final EntityDataAccessor<Integer> SNEEZE_COUNTER;
    private static final EntityDataAccessor<Integer> EAT_COUNTER;
    private static final EntityDataAccessor<Byte> MAIN_GENE_ID;
    private static final EntityDataAccessor<Byte> HIDDEN_GENE_ID;
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS;
    private boolean gotBamboo;
    private boolean didBite;
    public int rollCounter;
    private Vec3 rollDelta;
    private float sitAmount;
    private float sitAmountO;
    private float onBackAmount;
    private float onBackAmountO;
    private float rollAmount;
    private float rollAmountO;
    private static final Predicate<ItemEntity> PANDA_ITEMS;
    
    public Panda(final EntityType<? extends Panda> ais, final Level bhr) {
        super(ais, bhr);
        this.moveControl = new PandaMoveControl(this);
        if (!this.isBaby()) {
            this.setCanPickUpLoot(true);
        }
    }
    
    @Override
    public boolean canTakeItem(final ItemStack bcj) {
        final EquipmentSlot ait3 = Mob.getEquipmentSlotForItem(bcj);
        return this.getItemBySlot(ait3).isEmpty() && ait3 == EquipmentSlot.MAINHAND && super.canTakeItem(bcj);
    }
    
    public int getUnhappyCounter() {
        return this.entityData.<Integer>get(Panda.UNHAPPY_COUNTER);
    }
    
    public void setUnhappyCounter(final int integer) {
        this.entityData.<Integer>set(Panda.UNHAPPY_COUNTER, integer);
    }
    
    public boolean isSneezing() {
        return this.getFlag(2);
    }
    
    public boolean isSitting() {
        return this.getFlag(8);
    }
    
    public void sit(final boolean boolean1) {
        this.setFlag(8, boolean1);
    }
    
    public boolean isOnBack() {
        return this.getFlag(16);
    }
    
    public void setOnBack(final boolean boolean1) {
        this.setFlag(16, boolean1);
    }
    
    public boolean isEating() {
        return this.entityData.<Integer>get(Panda.EAT_COUNTER) > 0;
    }
    
    public void eat(final boolean boolean1) {
        this.entityData.<Integer>set(Panda.EAT_COUNTER, boolean1 ? 1 : 0);
    }
    
    private int getEatCounter() {
        return this.entityData.<Integer>get(Panda.EAT_COUNTER);
    }
    
    private void setEatCounter(final int integer) {
        this.entityData.<Integer>set(Panda.EAT_COUNTER, integer);
    }
    
    public void sneeze(final boolean boolean1) {
        this.setFlag(2, boolean1);
        if (!boolean1) {
            this.setSneezeCounter(0);
        }
    }
    
    public int getSneezeCounter() {
        return this.entityData.<Integer>get(Panda.SNEEZE_COUNTER);
    }
    
    public void setSneezeCounter(final int integer) {
        this.entityData.<Integer>set(Panda.SNEEZE_COUNTER, integer);
    }
    
    public Gene getMainGene() {
        return Gene.byId(this.entityData.<Byte>get(Panda.MAIN_GENE_ID));
    }
    
    public void setMainGene(Gene a) {
        if (a.getId() > 6) {
            a = Gene.getRandom(this.random);
        }
        this.entityData.<Byte>set(Panda.MAIN_GENE_ID, (byte)a.getId());
    }
    
    public Gene getHiddenGene() {
        return Gene.byId(this.entityData.<Byte>get(Panda.HIDDEN_GENE_ID));
    }
    
    public void setHiddenGene(Gene a) {
        if (a.getId() > 6) {
            a = Gene.getRandom(this.random);
        }
        this.entityData.<Byte>set(Panda.HIDDEN_GENE_ID, (byte)a.getId());
    }
    
    public boolean isRolling() {
        return this.getFlag(4);
    }
    
    public void roll(final boolean boolean1) {
        this.setFlag(4, boolean1);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Panda.UNHAPPY_COUNTER, 0);
        this.entityData.<Integer>define(Panda.SNEEZE_COUNTER, 0);
        this.entityData.<Byte>define(Panda.MAIN_GENE_ID, (Byte)0);
        this.entityData.<Byte>define(Panda.HIDDEN_GENE_ID, (Byte)0);
        this.entityData.<Byte>define(Panda.DATA_ID_FLAGS, (Byte)0);
        this.entityData.<Integer>define(Panda.EAT_COUNTER, 0);
    }
    
    private boolean getFlag(final int integer) {
        return (this.entityData.<Byte>get(Panda.DATA_ID_FLAGS) & integer) != 0x0;
    }
    
    private void setFlag(final int integer, final boolean boolean2) {
        final byte byte4 = this.entityData.<Byte>get(Panda.DATA_ID_FLAGS);
        if (boolean2) {
            this.entityData.<Byte>set(Panda.DATA_ID_FLAGS, (byte)(byte4 | integer));
        }
        else {
            this.entityData.<Byte>set(Panda.DATA_ID_FLAGS, (byte)(byte4 & ~integer));
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putString("MainGene", this.getMainGene().getName());
        id.putString("HiddenGene", this.getHiddenGene().getName());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setMainGene(Gene.byName(id.getString("MainGene")));
        this.setHiddenGene(Gene.byName(id.getString("HiddenGene")));
    }
    
    @Nullable
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        final Panda arl3 = EntityType.PANDA.create(this.level);
        if (aim instanceof Panda) {
            arl3.setGeneFromParents(this, (Panda)aim);
        }
        arl3.setAttributes();
        return arl3;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PandaPanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new PandaBreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new PandaAttackGoal(this, 1.2000000476837158, true));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0, Ingredient.of(Blocks.BAMBOO.asItem()), false));
        this.goalSelector.addGoal(6, new PandaAvoidGoal<>(this, Player.class, 8.0f, 2.0, 2.0));
        this.goalSelector.addGoal(6, new PandaAvoidGoal<>(this, Monster.class, 4.0f, 2.0, 2.0));
        this.goalSelector.addGoal(7, new PandaSitGoal());
        this.goalSelector.addGoal(8, new PandaLieOnBackGoal(this));
        this.goalSelector.addGoal(8, new PandaSneezeGoal(this));
        this.goalSelector.addGoal(9, new PandaLookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(12, new PandaRollGoal(this));
        this.goalSelector.addGoal(13, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new PandaHurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15000000596046448);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
    }
    
    public Gene getVariant() {
        return getVariantFromGenes(this.getMainGene(), this.getHiddenGene());
    }
    
    public boolean isLazy() {
        return this.getVariant() == Gene.LAZY;
    }
    
    public boolean isWorried() {
        return this.getVariant() == Gene.WORRIED;
    }
    
    public boolean isPlayful() {
        return this.getVariant() == Gene.PLAYFUL;
    }
    
    public boolean isWeak() {
        return this.getVariant() == Gene.WEAK;
    }
    
    @Override
    public boolean isAggressive() {
        return this.getVariant() == Gene.AGGRESSIVE;
    }
    
    @Override
    public boolean canBeLeashed(final Player awg) {
        return false;
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        this.playSound(SoundEvents.PANDA_BITE, 1.0f, 1.0f);
        if (!this.isAggressive()) {
            this.didBite = true;
        }
        return super.doHurtTarget(aio);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.isWorried()) {
            if (this.level.isThundering() && !this.isInWater()) {
                this.sit(true);
                this.eat(false);
            }
            else if (!this.isEating()) {
                this.sit(false);
            }
        }
        if (this.getTarget() == null) {
            this.gotBamboo = false;
            this.didBite = false;
        }
        if (this.getUnhappyCounter() > 0) {
            if (this.getTarget() != null) {
                this.lookAt(this.getTarget(), 90.0f, 90.0f);
            }
            if (this.getUnhappyCounter() == 29 || this.getUnhappyCounter() == 14) {
                this.playSound(SoundEvents.PANDA_CANT_BREED, 1.0f, 1.0f);
            }
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }
        if (this.isSneezing()) {
            this.setSneezeCounter(this.getSneezeCounter() + 1);
            if (this.getSneezeCounter() > 20) {
                this.sneeze(false);
                this.afterSneeze();
            }
            else if (this.getSneezeCounter() == 1) {
                this.playSound(SoundEvents.PANDA_PRE_SNEEZE, 1.0f, 1.0f);
            }
        }
        if (this.isRolling()) {
            this.handleRoll();
        }
        else {
            this.rollCounter = 0;
        }
        if (this.isSitting()) {
            this.xRot = 0.0f;
        }
        this.updateSitAmount();
        this.handleEating();
        this.updateOnBackAnimation();
        this.updateRollAmount();
    }
    
    public boolean isScared() {
        return this.isWorried() && this.level.isThundering();
    }
    
    private void handleEating() {
        if (!this.isEating() && this.isSitting() && !this.isScared() && !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.eat(true);
        }
        else if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || !this.isSitting()) {
            this.eat(false);
        }
        if (this.isEating()) {
            this.addEatingParticles();
            if (!this.level.isClientSide && this.getEatCounter() > 80 && this.random.nextInt(20) == 1) {
                if (this.getEatCounter() > 100 && this.isFoodOrCake(this.getItemBySlot(EquipmentSlot.MAINHAND))) {
                    if (!this.level.isClientSide) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    this.sit(false);
                }
                this.eat(false);
                return;
            }
            this.setEatCounter(this.getEatCounter() + 1);
        }
    }
    
    private void addEatingParticles() {
        if (this.getEatCounter() % 5 == 0) {
            this.playSound(SoundEvents.PANDA_EAT, 0.5f + 0.5f * this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            for (int integer2 = 0; integer2 < 6; ++integer2) {
                Vec3 csi3 = new Vec3((this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, (this.random.nextFloat() - 0.5) * 0.1);
                csi3 = csi3.xRot(-this.xRot * 0.017453292f);
                csi3 = csi3.yRot(-this.yRot * 0.017453292f);
                final double double4 = -this.random.nextFloat() * 0.6 - 0.3;
                Vec3 csi4 = new Vec3((this.random.nextFloat() - 0.5) * 0.8, double4, 1.0 + (this.random.nextFloat() - 0.5) * 0.4);
                csi4 = csi4.yRot(-this.yBodyRot * 0.017453292f);
                csi4 = csi4.add(this.x, this.y + this.getEyeHeight() + 1.0, this.z);
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemBySlot(EquipmentSlot.MAINHAND)), csi4.x, csi4.y, csi4.z, csi3.x, csi3.y + 0.05, csi3.z);
            }
        }
    }
    
    private void updateSitAmount() {
        this.sitAmountO = this.sitAmount;
        if (this.isSitting()) {
            this.sitAmount = Math.min(1.0f, this.sitAmount + 0.15f);
        }
        else {
            this.sitAmount = Math.max(0.0f, this.sitAmount - 0.19f);
        }
    }
    
    private void updateOnBackAnimation() {
        this.onBackAmountO = this.onBackAmount;
        if (this.isOnBack()) {
            this.onBackAmount = Math.min(1.0f, this.onBackAmount + 0.15f);
        }
        else {
            this.onBackAmount = Math.max(0.0f, this.onBackAmount - 0.19f);
        }
    }
    
    private void updateRollAmount() {
        this.rollAmountO = this.rollAmount;
        if (this.isRolling()) {
            this.rollAmount = Math.min(1.0f, this.rollAmount + 0.15f);
        }
        else {
            this.rollAmount = Math.max(0.0f, this.rollAmount - 0.19f);
        }
    }
    
    public float getSitAmount(final float float1) {
        return Mth.lerp(float1, this.sitAmountO, this.sitAmount);
    }
    
    public float getLieOnBackAmount(final float float1) {
        return Mth.lerp(float1, this.onBackAmountO, this.onBackAmount);
    }
    
    public float getRollAmount(final float float1) {
        return Mth.lerp(float1, this.rollAmountO, this.rollAmount);
    }
    
    private void handleRoll() {
        ++this.rollCounter;
        if (this.rollCounter > 32) {
            this.roll(false);
            return;
        }
        if (!this.level.isClientSide) {
            final Vec3 csi2 = this.getDeltaMovement();
            if (this.rollCounter == 1) {
                final float float3 = this.yRot * 0.017453292f;
                final float float4 = this.isBaby() ? 0.1f : 0.2f;
                this.rollDelta = new Vec3(csi2.x + -Mth.sin(float3) * float4, 0.0, csi2.z + Mth.cos(float3) * float4);
                this.setDeltaMovement(this.rollDelta.add(0.0, 0.27, 0.0));
            }
            else if (this.rollCounter == 7.0f || this.rollCounter == 15.0f || this.rollCounter == 23.0f) {
                this.setDeltaMovement(0.0, this.onGround ? 0.27 : csi2.y, 0.0);
            }
            else {
                this.setDeltaMovement(this.rollDelta.x, csi2.y, this.rollDelta.z);
            }
        }
    }
    
    private void afterSneeze() {
        final Vec3 csi2 = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SNEEZE, this.x - (this.getBbWidth() + 1.0f) * 0.5 * Mth.sin(this.yBodyRot * 0.017453292f), this.y + this.getEyeHeight() - 0.10000000149011612, this.z + (this.getBbWidth() + 1.0f) * 0.5 * Mth.cos(this.yBodyRot * 0.017453292f), csi2.x, 0.0, csi2.z);
        this.playSound(SoundEvents.PANDA_SNEEZE, 1.0f, 1.0f);
        final List<Panda> list3 = this.level.<Panda>getEntitiesOfClass((java.lang.Class<? extends Panda>)Panda.class, this.getBoundingBox().inflate(10.0));
        for (final Panda arl5 : list3) {
            if (!arl5.isBaby() && arl5.onGround && !arl5.isInWater() && arl5.canPerformAction()) {
                arl5.jumpFromGround();
            }
        }
        if (!this.level.isClientSide() && this.random.nextInt(700) == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SLIME_BALL);
        }
    }
    
    @Override
    protected void pickUpItem(final ItemEntity atx) {
        if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && Panda.PANDA_ITEMS.test(atx)) {
            final ItemStack bcj3 = atx.getItem();
            this.setItemSlot(EquipmentSlot.MAINHAND, bcj3);
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0f;
            this.take(atx, bcj3.getCount());
            atx.remove();
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        this.sit(false);
        return super.hurt(ahx, float2);
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        this.setMainGene(Gene.getRandom(this.random));
        this.setHiddenGene(Gene.getRandom(this.random));
        this.setAttributes();
        if (ajj instanceof PandaGroupData) {
            if (this.random.nextInt(5) == 0) {
                this.setAge(-24000);
            }
        }
        else {
            ajj = new PandaGroupData();
        }
        return ajj;
    }
    
    public void setGeneFromParents(final Panda arl1, @Nullable final Panda arl2) {
        if (arl2 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(arl1.getOneOfGenesRandomly());
                this.setHiddenGene(Gene.getRandom(this.random));
            }
            else {
                this.setMainGene(Gene.getRandom(this.random));
                this.setHiddenGene(arl1.getOneOfGenesRandomly());
            }
        }
        else if (this.random.nextBoolean()) {
            this.setMainGene(arl1.getOneOfGenesRandomly());
            this.setHiddenGene(arl2.getOneOfGenesRandomly());
        }
        else {
            this.setMainGene(arl2.getOneOfGenesRandomly());
            this.setHiddenGene(arl1.getOneOfGenesRandomly());
        }
        if (this.random.nextInt(32) == 0) {
            this.setMainGene(Gene.getRandom(this.random));
        }
        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(Gene.getRandom(this.random));
        }
    }
    
    private Gene getOneOfGenesRandomly() {
        if (this.random.nextBoolean()) {
            return this.getMainGene();
        }
        return this.getHiddenGene();
    }
    
    public void setAttributes() {
        if (this.isWeak()) {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        }
        if (this.isLazy()) {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.07000000029802322);
        }
    }
    
    private void tryToSit() {
        if (!this.isInWater()) {
            this.setZza(0.0f);
            this.getNavigation().stop();
            this.sit(true);
        }
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() instanceof SpawnEggItem) {
            return super.mobInteract(awg, ahi);
        }
        if (this.isScared()) {
            return false;
        }
        if (this.isOnBack()) {
            this.setOnBack(false);
            return true;
        }
        if (this.isFood(bcj4)) {
            if (this.getTarget() != null) {
                this.gotBamboo = true;
            }
            if (this.isBaby()) {
                this.usePlayerItem(awg, bcj4);
                this.ageUp((int)(-this.getAge() / 20 * 0.1f), true);
            }
            else if (!this.level.isClientSide && this.getAge() == 0 && this.canFallInLove()) {
                this.usePlayerItem(awg, bcj4);
                this.setInLove(awg);
            }
            else {
                if (this.level.isClientSide || this.isSitting() || this.isInWater()) {
                    return false;
                }
                this.tryToSit();
                this.eat(true);
                final ItemStack bcj5 = this.getItemBySlot(EquipmentSlot.MAINHAND);
                if (!bcj5.isEmpty() && !awg.abilities.instabuild) {
                    this.spawnAtLocation(bcj5);
                }
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(bcj4.getItem(), 1));
                this.usePlayerItem(awg, bcj4);
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAggressive()) {
            return SoundEvents.PANDA_AGGRESSIVE_AMBIENT;
        }
        if (this.isWorried()) {
            return SoundEvents.PANDA_WORRIED_AMBIENT;
        }
        return SoundEvents.PANDA_AMBIENT;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.PANDA_STEP, 0.15f, 1.0f);
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return bcj.getItem() == Blocks.BAMBOO.asItem();
    }
    
    private boolean isFoodOrCake(final ItemStack bcj) {
        return this.isFood(bcj) || bcj.getItem() == Blocks.CAKE.asItem();
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PANDA_DEATH;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.PANDA_HURT;
    }
    
    public boolean canPerformAction() {
        return !this.isOnBack() && !this.isScared() && !this.isEating() && !this.isRolling() && !this.isSitting();
    }
    
    static {
        UNHAPPY_COUNTER = SynchedEntityData.<Integer>defineId(Panda.class, EntityDataSerializers.INT);
        SNEEZE_COUNTER = SynchedEntityData.<Integer>defineId(Panda.class, EntityDataSerializers.INT);
        EAT_COUNTER = SynchedEntityData.<Integer>defineId(Panda.class, EntityDataSerializers.INT);
        MAIN_GENE_ID = SynchedEntityData.<Byte>defineId(Panda.class, EntityDataSerializers.BYTE);
        HIDDEN_GENE_ID = SynchedEntityData.<Byte>defineId(Panda.class, EntityDataSerializers.BYTE);
        DATA_ID_FLAGS = SynchedEntityData.<Byte>defineId(Panda.class, EntityDataSerializers.BYTE);
        PANDA_ITEMS = (atx -> {
            final Item bce2 = atx.getItem().getItem();
            return (bce2 == Blocks.BAMBOO.asItem() || bce2 == Blocks.CAKE.asItem()) && atx.isAlive() && !atx.hasPickUpDelay();
        });
    }
    
    public enum Gene {
        NORMAL(0, "normal", false), 
        LAZY(1, "lazy", false), 
        WORRIED(2, "worried", false), 
        PLAYFUL(3, "playful", false), 
        BROWN(4, "brown", true), 
        WEAK(5, "weak", true), 
        AGGRESSIVE(6, "aggressive", false);
        
        private static final Gene[] BY_ID;
        private final int id;
        private final String name;
        private final boolean isRecessive;
        
        private Gene(final int integer3, final String string4, final boolean boolean5) {
            this.id = integer3;
            this.name = string4;
            this.isRecessive = boolean5;
        }
        
        public int getId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean isRecessive() {
            return this.isRecessive;
        }
        
        private static Gene getVariantFromGenes(final Gene a1, final Gene a2) {
            if (!a1.isRecessive()) {
                return a1;
            }
            if (a1 == a2) {
                return a1;
            }
            return Gene.NORMAL;
        }
        
        public static Gene byId(int integer) {
            if (integer < 0 || integer >= Gene.BY_ID.length) {
                integer = 0;
            }
            return Gene.BY_ID[integer];
        }
        
        public static Gene byName(final String string) {
            for (final Gene a5 : values()) {
                if (a5.name.equals(string)) {
                    return a5;
                }
            }
            return Gene.NORMAL;
        }
        
        public static Gene getRandom(final Random random) {
            final int integer2 = random.nextInt(16);
            if (integer2 == 0) {
                return Gene.LAZY;
            }
            if (integer2 == 1) {
                return Gene.WORRIED;
            }
            if (integer2 == 2) {
                return Gene.PLAYFUL;
            }
            if (integer2 == 4) {
                return Gene.AGGRESSIVE;
            }
            if (integer2 < 9) {
                return Gene.WEAK;
            }
            if (integer2 < 11) {
                return Gene.BROWN;
            }
            return Gene.NORMAL;
        }
        
        static {
            BY_ID = (Gene[])Arrays.stream((Object[])values()).sorted(Comparator.comparingInt(Gene::getId)).toArray(Gene[]::new);
        }
    }
    
    static class PandaMoveControl extends MoveControl {
        private final Panda panda;
        
        public PandaMoveControl(final Panda arl) {
            super(arl);
            this.panda = arl;
        }
        
        @Override
        public void tick() {
            if (!this.panda.canPerformAction()) {
                return;
            }
            super.tick();
        }
    }
    
    static class PandaGroupData implements SpawnGroupData {
        private PandaGroupData() {
        }
    }
    
    static class PandaAttackGoal extends MeleeAttackGoal {
        private final Panda panda;
        
        public PandaAttackGoal(final Panda arl, final double double2, final boolean boolean3) {
            super(arl, double2, boolean3);
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            return this.panda.canPerformAction() && super.canUse();
        }
    }
    
    static class PandaLookAtPlayerGoal extends LookAtPlayerGoal {
        private final Panda panda;
        
        public PandaLookAtPlayerGoal(final Panda arl, final Class<? extends LivingEntity> class2, final float float3) {
            super(arl, class2, float3);
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            return this.panda.canPerformAction() && super.canUse();
        }
    }
    
    static class PandaRollGoal extends Goal {
        private final Panda panda;
        
        public PandaRollGoal(final Panda arl) {
            this.panda = arl;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK, (Enum)Flag.JUMP));
        }
        
        @Override
        public boolean canUse() {
            if ((!this.panda.isBaby() && !this.panda.isPlayful()) || !this.panda.onGround) {
                return false;
            }
            if (!this.panda.canPerformAction()) {
                return false;
            }
            final float float2 = this.panda.yRot * 0.017453292f;
            int integer3 = 0;
            int integer4 = 0;
            final float float3 = -Mth.sin(float2);
            final float float4 = Mth.cos(float2);
            if (Math.abs(float3) > 0.5) {
                integer3 += (int)(float3 / Math.abs(float3));
            }
            if (Math.abs(float4) > 0.5) {
                integer4 += (int)(float4 / Math.abs(float4));
            }
            return this.panda.level.getBlockState(new BlockPos(this.panda).offset(integer3, -1, integer4)).isAir() || (this.panda.isPlayful() && this.panda.random.nextInt(60) == 1) || this.panda.random.nextInt(500) == 1;
        }
        
        @Override
        public boolean canContinueToUse() {
            return false;
        }
        
        @Override
        public void start() {
            this.panda.roll(true);
        }
        
        @Override
        public boolean isInterruptable() {
            return false;
        }
    }
    
    static class PandaSneezeGoal extends Goal {
        private final Panda panda;
        
        public PandaSneezeGoal(final Panda arl) {
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            return this.panda.isBaby() && this.panda.canPerformAction() && ((this.panda.isWeak() && this.panda.random.nextInt(500) == 1) || this.panda.random.nextInt(6000) == 1);
        }
        
        @Override
        public boolean canContinueToUse() {
            return false;
        }
        
        @Override
        public void start() {
            this.panda.sneeze(true);
        }
    }
    
    static class PandaBreedGoal extends BreedGoal {
        private static final TargetingConditions BREED_TARGETING;
        private final Panda panda;
        private int unhappyCooldown;
        
        public PandaBreedGoal(final Panda arl, final double double2) {
            super(arl, double2);
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            if (!super.canUse() || this.panda.getUnhappyCounter() != 0) {
                return false;
            }
            if (!this.canFindBamboo()) {
                if (this.unhappyCooldown <= this.panda.tickCount) {
                    this.panda.setUnhappyCounter(32);
                    this.unhappyCooldown = this.panda.tickCount + 600;
                    if (this.panda.isEffectiveAi()) {
                        final Player awg2 = this.level.getNearestPlayer(PandaBreedGoal.BREED_TARGETING, this.panda);
                        this.panda.setTarget(awg2);
                    }
                }
                return false;
            }
            return true;
        }
        
        private boolean canFindBamboo() {
            final BlockPos ew2 = new BlockPos(this.panda);
            final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos();
            for (int integer4 = 0; integer4 < 3; ++integer4) {
                for (int integer5 = 0; integer5 < 8; ++integer5) {
                    for (int integer6 = 0; integer6 <= integer5; integer6 = ((integer6 > 0) ? (-integer6) : (1 - integer6))) {
                        for (int integer7 = (integer6 < integer5 && integer6 > -integer5) ? integer5 : 0; integer7 <= integer5; integer7 = ((integer7 > 0) ? (-integer7) : (1 - integer7))) {
                            a3.set(ew2).move(integer6, integer4, integer7);
                            if (this.level.getBlockState(a3).getBlock() == Blocks.BAMBOO) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        
        static {
            BREED_TARGETING = new TargetingConditions().range(8.0).allowSameTeam().allowInvulnerable();
        }
    }
    
    static class PandaAvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Panda panda;
        
        public PandaAvoidGoal(final Panda arl, final Class<T> class2, final float float3, final double double4, final double double5) {
            super(arl, class2, float3, double4, double5, (Predicate<LivingEntity>)EntitySelector.NO_SPECTATORS::test);
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            return this.panda.isWorried() && this.panda.canPerformAction() && super.canUse();
        }
    }
    
    class PandaSitGoal extends Goal {
        private int cooldown;
        
        public PandaSitGoal() {
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            if (this.cooldown > Panda.this.tickCount || Panda.this.isBaby() || Panda.this.isInWater() || !Panda.this.canPerformAction() || Panda.this.getUnhappyCounter() > 0) {
                return false;
            }
            final List<ItemEntity> list2 = Panda.this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, Panda.this.getBoundingBox().inflate(6.0, 6.0, 6.0), (java.util.function.Predicate<? super ItemEntity>)Panda.PANDA_ITEMS);
            return !list2.isEmpty() || !Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }
        
        @Override
        public boolean canContinueToUse() {
            return !Panda.this.isInWater() && (Panda.this.isLazy() || Panda.this.random.nextInt(600) != 1) && Panda.this.random.nextInt(2000) != 1;
        }
        
        @Override
        public void tick() {
            if (!Panda.this.isSitting() && !Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                Panda.this.tryToSit();
            }
        }
        
        @Override
        public void start() {
            final List<ItemEntity> list2 = Panda.this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, Panda.this.getBoundingBox().inflate(8.0, 8.0, 8.0), (java.util.function.Predicate<? super ItemEntity>)Panda.PANDA_ITEMS);
            if (!list2.isEmpty() && Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                Panda.this.getNavigation().moveTo((Entity)list2.get(0), 1.2000000476837158);
            }
            else if (!Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                Panda.this.tryToSit();
            }
            this.cooldown = 0;
        }
        
        @Override
        public void stop() {
            final ItemStack bcj2 = Panda.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!bcj2.isEmpty()) {
                Panda.this.spawnAtLocation(bcj2);
                Panda.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                final int integer3 = Panda.this.isLazy() ? (Panda.this.random.nextInt(50) + 10) : (Panda.this.random.nextInt(150) + 10);
                this.cooldown = Panda.this.tickCount + integer3 * 20;
            }
            Panda.this.sit(false);
        }
    }
    
    static class PandaLieOnBackGoal extends Goal {
        private final Panda panda;
        private int cooldown;
        
        public PandaLieOnBackGoal(final Panda arl) {
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            return this.cooldown < this.panda.tickCount && this.panda.isLazy() && this.panda.canPerformAction() && this.panda.random.nextInt(400) == 1;
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.panda.isInWater() && (this.panda.isLazy() || this.panda.random.nextInt(600) != 1) && this.panda.random.nextInt(2000) != 1;
        }
        
        @Override
        public void start() {
            this.panda.setOnBack(true);
            this.cooldown = 0;
        }
        
        @Override
        public void stop() {
            this.panda.setOnBack(false);
            this.cooldown = this.panda.tickCount + 200;
        }
    }
    
    static class PandaHurtByTargetGoal extends HurtByTargetGoal {
        private final Panda panda;
        
        public PandaHurtByTargetGoal(final Panda arl, final Class<?>... arr) {
            super(arl, arr);
            this.panda = arl;
        }
        
        @Override
        public boolean canContinueToUse() {
            if (this.panda.gotBamboo || this.panda.didBite) {
                this.panda.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }
        
        @Override
        protected void alertOther(final Mob aiy, final LivingEntity aix) {
            if (aiy instanceof Panda && ((Panda)aiy).isAggressive()) {
                aiy.setTarget(aix);
            }
        }
    }
    
    static class PandaPanicGoal extends PanicGoal {
        private final Panda panda;
        
        public PandaPanicGoal(final Panda arl, final double double2) {
            super(arl, double2);
            this.panda = arl;
        }
        
        @Override
        public boolean canUse() {
            if (!this.panda.isOnFire()) {
                return false;
            }
            final BlockPos ew2 = this.lookForWater(this.mob.level, this.mob, 5, 4);
            if (ew2 != null) {
                this.posX = ew2.getX();
                this.posY = ew2.getY();
                this.posZ = ew2.getZ();
                return true;
            }
            return this.findRandomPosition();
        }
        
        @Override
        public boolean canContinueToUse() {
            if (this.panda.isSitting()) {
                this.panda.getNavigation().stop();
                return false;
            }
            return super.canContinueToUse();
        }
    }
}
