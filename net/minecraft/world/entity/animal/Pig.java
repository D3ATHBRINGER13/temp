package net.minecraft.world.entity.animal;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Pig extends Animal {
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID;
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME;
    private static final Ingredient FOOD_ITEMS;
    private boolean boosting;
    private int boostTime;
    private int boostTimeTotal;
    
    public Pig(final EntityType<? extends Pig> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, Ingredient.of(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, false, Pig.FOOD_ITEMS));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Nullable
    @Override
    public Entity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        return (Entity)this.getPassengers().get(0);
    }
    
    @Override
    public boolean canBeControlledByRider() {
        final Entity aio2 = this.getControllingPassenger();
        if (!(aio2 instanceof Player)) {
            return false;
        }
        final Player awg3 = (Player)aio2;
        return awg3.getMainHandItem().getItem() == Items.CARROT_ON_A_STICK || awg3.getOffhandItem().getItem() == Items.CARROT_ON_A_STICK;
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (Pig.DATA_BOOST_TIME.equals(qk) && this.level.isClientSide) {
            this.boosting = true;
            this.boostTime = 0;
            this.boostTimeTotal = this.entityData.<Integer>get(Pig.DATA_BOOST_TIME);
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(Pig.DATA_SADDLE_ID, false);
        this.entityData.<Integer>define(Pig.DATA_BOOST_TIME, 0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("Saddle", this.hasSaddle());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setSaddle(id.getBoolean("Saddle"));
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.PIG_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.PIG_STEP, 0.15f, 1.0f);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        if (super.mobInteract(awg, ahi)) {
            return true;
        }
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.NAME_TAG) {
            bcj4.interactEnemy(awg, this, ahi);
            return true;
        }
        if (this.hasSaddle() && !this.isVehicle()) {
            if (!this.level.isClientSide) {
                awg.startRiding(this);
            }
            return true;
        }
        if (bcj4.getItem() == Items.SADDLE) {
            bcj4.interactEnemy(awg, this, ahi);
            return true;
        }
        return false;
    }
    
    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasSaddle()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }
    
    public boolean hasSaddle() {
        return this.entityData.<Boolean>get(Pig.DATA_SADDLE_ID);
    }
    
    public void setSaddle(final boolean boolean1) {
        if (boolean1) {
            this.entityData.<Boolean>set(Pig.DATA_SADDLE_ID, true);
        }
        else {
            this.entityData.<Boolean>set(Pig.DATA_SADDLE_ID, false);
        }
    }
    
    @Override
    public void thunderHit(final LightningBolt atu) {
        final PigZombie auv3 = EntityType.ZOMBIE_PIGMAN.create(this.level);
        auv3.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        auv3.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
        auv3.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            auv3.setCustomName(this.getCustomName());
            auv3.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.level.addFreshEntity(auv3);
        this.remove();
    }
    
    @Override
    public void travel(final Vec3 csi) {
        if (!this.isAlive()) {
            return;
        }
        final Entity aio3 = this.getPassengers().isEmpty() ? null : ((Entity)this.getPassengers().get(0));
        if (!this.isVehicle() || !this.canBeControlledByRider()) {
            this.maxUpStep = 0.5f;
            this.flyingSpeed = 0.02f;
            super.travel(csi);
            return;
        }
        this.yRot = aio3.yRot;
        this.yRotO = this.yRot;
        this.xRot = aio3.xRot * 0.5f;
        this.setRot(this.yRot, this.xRot);
        this.yBodyRot = this.yRot;
        this.yHeadRot = this.yRot;
        this.maxUpStep = 1.0f;
        this.flyingSpeed = this.getSpeed() * 0.1f;
        if (this.boosting && this.boostTime++ > this.boostTimeTotal) {
            this.boosting = false;
        }
        if (this.isControlledByLocalInstance()) {
            float float4 = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 0.225f;
            if (this.boosting) {
                float4 += float4 * 1.15f * Mth.sin(this.boostTime / (float)this.boostTimeTotal * 3.1415927f);
            }
            this.setSpeed(float4);
            super.travel(new Vec3(0.0, 0.0, 1.0));
        }
        else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.animationSpeedOld = this.animationSpeed;
        final double double4 = this.x - this.xo;
        final double double5 = this.z - this.zo;
        float float5 = Mth.sqrt(double4 * double4 + double5 * double5) * 4.0f;
        if (float5 > 1.0f) {
            float5 = 1.0f;
        }
        this.animationSpeed += (float5 - this.animationSpeed) * 0.4f;
        this.animationPosition += this.animationSpeed;
    }
    
    public boolean boost() {
        if (this.boosting) {
            return false;
        }
        this.boosting = true;
        this.boostTime = 0;
        this.boostTimeTotal = this.getRandom().nextInt(841) + 140;
        this.getEntityData().<Integer>set(Pig.DATA_BOOST_TIME, this.boostTimeTotal);
        return true;
    }
    
    @Override
    public Pig getBreedOffspring(final AgableMob aim) {
        return EntityType.PIG.create(this.level);
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return Pig.FOOD_ITEMS.test(bcj);
    }
    
    static {
        DATA_SADDLE_ID = SynchedEntityData.<Boolean>defineId(Pig.class, EntityDataSerializers.BOOLEAN);
        DATA_BOOST_TIME = SynchedEntityData.<Integer>defineId(Pig.class, EntityDataSerializers.INT);
        FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
    }
}
