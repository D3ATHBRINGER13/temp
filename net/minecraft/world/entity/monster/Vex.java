package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.control.MoveControl;
import java.util.Random;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Vex extends Monster {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    private Mob owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;
    
    public Vex(final EntityType<? extends Vex> ais, final Level bhr) {
        super(ais, bhr);
        this.moveControl = new VexMoveControl(this);
        this.xpReward = 3;
    }
    
    public void move(final MoverType ajc, final Vec3 csi) {
        super.move(ajc, csi);
        this.checkInsideBlocks();
    }
    
    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(DamageSource.STARVE, 1.0f);
        }
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new VexChargeAttackGoal());
        this.goalSelector.addGoal(8, new VexRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] { Raider.class }).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new VexCopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(Vex.DATA_FLAGS_ID, (Byte)0);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("BoundX")) {
            this.boundOrigin = new BlockPos(id.getInt("BoundX"), id.getInt("BoundY"), id.getInt("BoundZ"));
        }
        if (id.contains("LifeTicks")) {
            this.setLimitedLife(id.getInt("LifeTicks"));
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.boundOrigin != null) {
            id.putInt("BoundX", this.boundOrigin.getX());
            id.putInt("BoundY", this.boundOrigin.getY());
            id.putInt("BoundZ", this.boundOrigin.getZ());
        }
        if (this.hasLimitedLife) {
            id.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }
    
    public Mob getOwner() {
        return this.owner;
    }
    
    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }
    
    public void setBoundOrigin(@Nullable final BlockPos ew) {
        this.boundOrigin = ew;
    }
    
    private boolean getVexFlag(final int integer) {
        final int integer2 = this.entityData.<Byte>get(Vex.DATA_FLAGS_ID);
        return (integer2 & integer) != 0x0;
    }
    
    private void setVexFlag(final int integer, final boolean boolean2) {
        int integer2 = this.entityData.<Byte>get(Vex.DATA_FLAGS_ID);
        if (boolean2) {
            integer2 |= integer;
        }
        else {
            integer2 &= ~integer;
        }
        this.entityData.<Byte>set(Vex.DATA_FLAGS_ID, (byte)(integer2 & 0xFF));
    }
    
    public boolean isCharging() {
        return this.getVexFlag(1);
    }
    
    public void setIsCharging(final boolean boolean1) {
        this.setVexFlag(1, boolean1);
    }
    
    public void setOwner(final Mob aiy) {
        this.owner = aiy;
    }
    
    public void setLimitedLife(final int integer) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = integer;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.VEX_HURT;
    }
    
    public int getLightColor() {
        return 15728880;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.populateDefaultEquipmentSlots(ahh);
        this.populateDefaultEquipmentEnchantments(ahh);
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }
    
    static {
        DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(Vex.class, EntityDataSerializers.BYTE);
    }
    
    class VexMoveControl extends MoveControl {
        public VexMoveControl(final Vex avi2) {
            super(avi2);
        }
        
        @Override
        public void tick() {
            if (this.operation != Operation.MOVE_TO) {
                return;
            }
            final Vec3 csi2 = new Vec3(this.wantedX - Vex.this.x, this.wantedY - Vex.this.y, this.wantedZ - Vex.this.z);
            final double double3 = csi2.length();
            if (double3 < Vex.this.getBoundingBox().getSize()) {
                this.operation = Operation.WAIT;
                Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().scale(0.5));
            }
            else {
                Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().add(csi2.scale(this.speedModifier * 0.05 / double3)));
                if (Vex.this.getTarget() == null) {
                    final Vec3 csi3 = Vex.this.getDeltaMovement();
                    Vex.this.yRot = -(float)Mth.atan2(csi3.x, csi3.z) * 57.295776f;
                    Vex.this.yBodyRot = Vex.this.yRot;
                }
                else {
                    final double double4 = Vex.this.getTarget().x - Vex.this.x;
                    final double double5 = Vex.this.getTarget().z - Vex.this.z;
                    Vex.this.yRot = -(float)Mth.atan2(double4, double5) * 57.295776f;
                    Vex.this.yBodyRot = Vex.this.yRot;
                }
            }
        }
    }
    
    class VexChargeAttackGoal extends Goal {
        public VexChargeAttackGoal() {
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            return Vex.this.getTarget() != null && !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(7) == 0 && Vex.this.distanceToSqr(Vex.this.getTarget()) > 4.0;
        }
        
        @Override
        public boolean canContinueToUse() {
            return Vex.this.getMoveControl().hasWanted() && Vex.this.isCharging() && Vex.this.getTarget() != null && Vex.this.getTarget().isAlive();
        }
        
        @Override
        public void start() {
            final LivingEntity aix2 = Vex.this.getTarget();
            final Vec3 csi3 = aix2.getEyePosition(1.0f);
            Vex.this.moveControl.setWantedPosition(csi3.x, csi3.y, csi3.z, 1.0);
            Vex.this.setIsCharging(true);
            Vex.this.playSound(SoundEvents.VEX_CHARGE, 1.0f, 1.0f);
        }
        
        @Override
        public void stop() {
            Vex.this.setIsCharging(false);
        }
        
        @Override
        public void tick() {
            final LivingEntity aix2 = Vex.this.getTarget();
            if (Vex.this.getBoundingBox().intersects(aix2.getBoundingBox())) {
                Vex.this.doHurtTarget(aix2);
                Vex.this.setIsCharging(false);
            }
            else {
                final double double3 = Vex.this.distanceToSqr(aix2);
                if (double3 < 9.0) {
                    final Vec3 csi5 = aix2.getEyePosition(1.0f);
                    Vex.this.moveControl.setWantedPosition(csi5.x, csi5.y, csi5.z, 1.0);
                }
            }
        }
    }
    
    class VexRandomMoveGoal extends Goal {
        public VexRandomMoveGoal() {
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            return !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(7) == 0;
        }
        
        @Override
        public boolean canContinueToUse() {
            return false;
        }
        
        @Override
        public void tick() {
            BlockPos ew2 = Vex.this.getBoundOrigin();
            if (ew2 == null) {
                ew2 = new BlockPos(Vex.this);
            }
            int integer3 = 0;
            while (integer3 < 3) {
                final BlockPos ew3 = ew2.offset(Vex.this.random.nextInt(15) - 7, Vex.this.random.nextInt(11) - 5, Vex.this.random.nextInt(15) - 7);
                if (Vex.this.level.isEmptyBlock(ew3)) {
                    Vex.this.moveControl.setWantedPosition(ew3.getX() + 0.5, ew3.getY() + 0.5, ew3.getZ() + 0.5, 0.25);
                    if (Vex.this.getTarget() == null) {
                        Vex.this.getLookControl().setLookAt(ew3.getX() + 0.5, ew3.getY() + 0.5, ew3.getZ() + 0.5, 180.0f, 20.0f);
                        break;
                    }
                    break;
                }
                else {
                    ++integer3;
                }
            }
        }
    }
    
    class VexCopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting;
        
        public VexCopyOwnerTargetGoal(final PathfinderMob aje) {
            super(aje, false);
            this.copyOwnerTargeting = new TargetingConditions().allowUnseeable().ignoreInvisibilityTesting();
        }
        
        @Override
        public boolean canUse() {
            return Vex.this.owner != null && Vex.this.owner.getTarget() != null && this.canAttack(Vex.this.owner.getTarget(), this.copyOwnerTargeting);
        }
        
        @Override
        public void start() {
            Vex.this.setTarget(Vex.this.owner.getTarget());
            super.start();
        }
    }
}
