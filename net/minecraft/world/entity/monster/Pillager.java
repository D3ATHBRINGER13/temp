package net.minecraft.world.entity.monster;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.entity.item.ItemEntity;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import com.google.common.collect.Maps;
import net.minecraft.world.level.ItemLike;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Pillager extends AbstractIllager implements CrossbowAttackMob, RangedAttackMob {
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW;
    private final SimpleContainer inventory;
    
    public Pillager(final EntityType<? extends Pillager> ais, final Level bhr) {
        super(ais, bhr);
        this.inventory = new SimpleContainer(5);
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new HoldGroundAttackGoal(this, 10.0f));
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0, 8.0f));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] { Raider.class }).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(Pillager.IS_CHARGING_CROSSBOW, false);
    }
    
    public boolean isChargingCrossbow() {
        return this.entityData.<Boolean>get(Pillager.IS_CHARGING_CROSSBOW);
    }
    
    @Override
    public void setChargingCrossbow(final boolean boolean1) {
        this.entityData.<Boolean>set(Pillager.IS_CHARGING_CROSSBOW, boolean1);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        final ListTag ik3 = new ListTag();
        for (int integer4 = 0; integer4 < this.inventory.getContainerSize(); ++integer4) {
            final ItemStack bcj5 = this.inventory.getItem(integer4);
            if (!bcj5.isEmpty()) {
                ik3.add(bcj5.save(new CompoundTag()));
            }
        }
        id.put("Inventory", (Tag)ik3);
    }
    
    @Override
    public IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return IllagerArmPose.CROSSBOW_CHARGE;
        }
        if (this.isHolding(Items.CROSSBOW)) {
            return IllagerArmPose.CROSSBOW_HOLD;
        }
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        }
        return IllagerArmPose.CROSSED;
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        final ListTag ik3 = id.getList("Inventory", 10);
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            final ItemStack bcj5 = ItemStack.of(ik3.getCompound(integer4));
            if (!bcj5.isEmpty()) {
                this.inventory.addItem(bcj5);
            }
        }
        this.setCanPickUpLoot(true);
    }
    
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        final Block bmv4 = bhu.getBlockState(ew.below()).getBlock();
        if (bmv4 == Blocks.GRASS_BLOCK || bmv4 == Blocks.SAND) {
            return 10.0f;
        }
        return 0.5f - bhu.getBrightness(ew);
    }
    
    public int getMaxSpawnClusterSize() {
        return 1;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.populateDefaultEquipmentSlots(ahh);
        this.populateDefaultEquipmentEnchantments(ahh);
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        final ItemStack bcj3 = new ItemStack(Items.CROSSBOW);
        if (this.random.nextInt(300) == 0) {
            final Map<Enchantment, Integer> map4 = (Map<Enchantment, Integer>)Maps.newHashMap();
            map4.put(Enchantments.PIERCING, 1);
            EnchantmentHelper.setEnchantments(map4, bcj3);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, bcj3);
    }
    
    public boolean isAlliedTo(final Entity aio) {
        return super.isAlliedTo(aio) || (aio instanceof LivingEntity && ((LivingEntity)aio).getMobType() == MobType.ILLAGER && this.getTeam() == null && aio.getTeam() == null);
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.PILLAGER_HURT;
    }
    
    @Override
    public void performRangedAttack(final LivingEntity aix, final float float2) {
        final InteractionHand ahi4 = ProjectileUtil.getWeaponHoldingHand(this, Items.CROSSBOW);
        final ItemStack bcj5 = this.getItemInHand(ahi4);
        if (this.isHolding(Items.CROSSBOW)) {
            CrossbowItem.performShooting(this.level, this, ahi4, bcj5, 1.6f, (float)(14 - this.level.getDifficulty().getId() * 4));
        }
        this.noActionTime = 0;
    }
    
    @Override
    public void shootProjectile(final LivingEntity aix, final ItemStack bcj, final Projectile awv, final float float4) {
        final Entity aio6 = (Entity)awv;
        final double double7 = aix.x - this.x;
        final double double8 = aix.z - this.z;
        final double double9 = Mth.sqrt(double7 * double7 + double8 * double8);
        final double double10 = aix.getBoundingBox().minY + aix.getBbHeight() / 3.0f - aio6.y + double9 * 0.20000000298023224;
        final Vector3f b15 = this.getProjectileShotVector(new Vec3(double7, double10, double8), float4);
        awv.shoot(b15.x(), b15.y(), b15.z(), 1.6f, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
    }
    
    private Vector3f getProjectileShotVector(final Vec3 csi, final float float2) {
        final Vec3 csi2 = csi.normalize();
        Vec3 csi3 = csi2.cross(new Vec3(0.0, 1.0, 0.0));
        if (csi3.lengthSqr() <= 1.0E-7) {
            csi3 = csi2.cross(this.getUpVector(1.0f));
        }
        final Quaternion a6 = new Quaternion(new Vector3f(csi3), 90.0f, true);
        final Vector3f b7 = new Vector3f(csi2);
        b7.transform(a6);
        final Quaternion a7 = new Quaternion(b7, float2, true);
        final Vector3f b8 = new Vector3f(csi2);
        b8.transform(a7);
        return b8;
    }
    
    public SimpleContainer getInventory() {
        return this.inventory;
    }
    
    @Override
    protected void pickUpItem(final ItemEntity atx) {
        final ItemStack bcj3 = atx.getItem();
        if (bcj3.getItem() instanceof BannerItem) {
            super.pickUpItem(atx);
        }
        else {
            final Item bce4 = bcj3.getItem();
            if (this.wantsItem(bce4)) {
                final ItemStack bcj4 = this.inventory.addItem(bcj3);
                if (bcj4.isEmpty()) {
                    atx.remove();
                }
                else {
                    bcj3.setCount(bcj4.getCount());
                }
            }
        }
    }
    
    private boolean wantsItem(final Item bce) {
        return this.hasActiveRaid() && bce == Items.WHITE_BANNER;
    }
    
    public boolean setSlot(final int integer, final ItemStack bcj) {
        if (super.setSlot(integer, bcj)) {
            return true;
        }
        final int integer2 = integer - 300;
        if (integer2 >= 0 && integer2 < this.inventory.getContainerSize()) {
            this.inventory.setItem(integer2, bcj);
            return true;
        }
        return false;
    }
    
    @Override
    public void applyRaidBuffs(final int integer, final boolean boolean2) {
        final Raid axk4 = this.getCurrentRaid();
        final boolean boolean3 = this.random.nextFloat() <= axk4.getEnchantOdds();
        if (boolean3) {
            final ItemStack bcj6 = new ItemStack(Items.CROSSBOW);
            final Map<Enchantment, Integer> map7 = (Map<Enchantment, Integer>)Maps.newHashMap();
            if (integer > axk4.getNumGroups(Difficulty.NORMAL)) {
                map7.put(Enchantments.QUICK_CHARGE, 2);
            }
            else if (integer > axk4.getNumGroups(Difficulty.EASY)) {
                map7.put(Enchantments.QUICK_CHARGE, 1);
            }
            map7.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.setEnchantments(map7, bcj6);
            this.setItemSlot(EquipmentSlot.MAINHAND, bcj6);
        }
    }
    
    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() && this.getInventory().isEmpty();
    }
    
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return super.removeWhenFarAway(double1) && this.getInventory().isEmpty();
    }
    
    static {
        IS_CHARGING_CROSSBOW = SynchedEntityData.<Boolean>defineId(Pillager.class, EntityDataSerializers.BOOLEAN);
    }
}
