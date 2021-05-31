package net.minecraft.world.entity.animal;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Enemy;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class SnowGolem extends AbstractGolem implements RangedAttackMob {
    private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID;
    
    public SnowGolem(final EntityType<? extends SnowGolem> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, (Predicate<LivingEntity>)(aix -> aix instanceof Enemy)));
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
    }
    
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(SnowGolem.DATA_PUMPKIN_ID, (Byte)16);
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("Pumpkin", this.hasPumpkin());
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("Pumpkin")) {
            this.setPumpkin(id.getBoolean("Pumpkin"));
        }
    }
    
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            int integer2 = Mth.floor(this.x);
            int integer3 = Mth.floor(this.y);
            int integer4 = Mth.floor(this.z);
            if (this.isInWaterRainOrBubble()) {
                this.hurt(DamageSource.DROWN, 1.0f);
            }
            if (this.level.getBiome(new BlockPos(integer2, 0, integer4)).getTemperature(new BlockPos(integer2, integer3, integer4)) > 1.0f) {
                this.hurt(DamageSource.ON_FIRE, 1.0f);
            }
            if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return;
            }
            final BlockState bvt5 = Blocks.SNOW.defaultBlockState();
            for (int integer5 = 0; integer5 < 4; ++integer5) {
                integer2 = Mth.floor(this.x + (integer5 % 2 * 2 - 1) * 0.25f);
                integer3 = Mth.floor(this.y);
                integer4 = Mth.floor(this.z + (integer5 / 2 % 2 * 2 - 1) * 0.25f);
                final BlockPos ew7 = new BlockPos(integer2, integer3, integer4);
                if (this.level.getBlockState(ew7).isAir() && this.level.getBiome(ew7).getTemperature(ew7) < 0.8f && bvt5.canSurvive(this.level, ew7)) {
                    this.level.setBlockAndUpdate(ew7, bvt5);
                }
            }
        }
    }
    
    @Override
    public void performRangedAttack(final LivingEntity aix, final float float2) {
        final Snowball awz4 = new Snowball(this.level, this);
        final double double5 = aix.y + aix.getEyeHeight() - 1.100000023841858;
        final double double6 = aix.x - this.x;
        final double double7 = double5 - awz4.y;
        final double double8 = aix.z - this.z;
        final float float3 = Mth.sqrt(double6 * double6 + double8 * double8) * 0.2f;
        awz4.shoot(double6, double7 + float3, double8, 1.6f, 12.0f);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level.addFreshEntity(awz4);
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 1.7f;
    }
    
    protected boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.SHEARS && this.hasPumpkin() && !this.level.isClientSide) {
            this.setPumpkin(false);
            bcj4.<Player>hurtAndBreak(1, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
        }
        return super.mobInteract(awg, ahi);
    }
    
    public boolean hasPumpkin() {
        return (this.entityData.<Byte>get(SnowGolem.DATA_PUMPKIN_ID) & 0x10) != 0x0;
    }
    
    public void setPumpkin(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(SnowGolem.DATA_PUMPKIN_ID);
        if (boolean1) {
            this.entityData.<Byte>set(SnowGolem.DATA_PUMPKIN_ID, (byte)(byte3 | 0x10));
        }
        else {
            this.entityData.<Byte>set(SnowGolem.DATA_PUMPKIN_ID, (byte)(byte3 & 0xFFFFFFEF));
        }
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.SNOW_GOLEM_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNOW_GOLEM_DEATH;
    }
    
    static {
        DATA_PUMPKIN_ID = SynchedEntityData.<Byte>defineId(SnowGolem.class, EntityDataSerializers.BYTE);
    }
}
