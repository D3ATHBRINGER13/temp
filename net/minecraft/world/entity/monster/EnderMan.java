package net.minecraft.world.entity.monster;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.world.level.LevelReader;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;

public class EnderMan extends Monster {
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID;
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING;
    private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE;
    private static final EntityDataAccessor<Boolean> DATA_CREEPY;
    private static final Predicate<LivingEntity> ENDERMITE_SELECTOR;
    private int lastCreepySound;
    private int targetChangeTime;
    
    public EnderMan(final EntityType<? extends EnderMan> ais, final Level bhr) {
        super(ais, bhr);
        this.maxUpStep = 1.0f;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EndermanFreezeWhenLookedAt(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0f));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new EndermanLeaveBlockGoal(this));
        this.goalSelector.addGoal(11, new EndermanTakeBlockGoal(this));
        this.targetSelector.addGoal(1, new EndermanLookForPlayerGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, 10, true, false, EnderMan.ENDERMITE_SELECTOR));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0);
    }
    
    @Override
    public void setTarget(@Nullable final LivingEntity aix) {
        super.setTarget(aix);
        final AttributeInstance ajo3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (aix == null) {
            this.targetChangeTime = 0;
            this.entityData.<Boolean>set(EnderMan.DATA_CREEPY, false);
            ajo3.removeModifier(EnderMan.SPEED_MODIFIER_ATTACKING);
        }
        else {
            this.targetChangeTime = this.tickCount;
            this.entityData.<Boolean>set(EnderMan.DATA_CREEPY, true);
            if (!ajo3.hasModifier(EnderMan.SPEED_MODIFIER_ATTACKING)) {
                ajo3.addModifier(EnderMan.SPEED_MODIFIER_ATTACKING);
            }
        }
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Optional<BlockState>>define(EnderMan.DATA_CARRY_STATE, (Optional<BlockState>)Optional.empty());
        this.entityData.<Boolean>define(EnderMan.DATA_CREEPY, false);
    }
    
    public void playCreepySound() {
        if (this.tickCount >= this.lastCreepySound + 400) {
            this.lastCreepySound = this.tickCount;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.x, this.y + this.getEyeHeight(), this.z, SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5f, 1.0f, false);
            }
        }
    }
    
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (EnderMan.DATA_CREEPY.equals(qk) && this.isCreepy() && this.level.isClientSide) {
            this.playCreepySound();
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        final BlockState bvt3 = this.getCarriedBlock();
        if (bvt3 != null) {
            id.put("carriedBlockState", (Tag)NbtUtils.writeBlockState(bvt3));
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        BlockState bvt3 = null;
        if (id.contains("carriedBlockState", 10)) {
            bvt3 = NbtUtils.readBlockState(id.getCompound("carriedBlockState"));
            if (bvt3.isAir()) {
                bvt3 = null;
            }
        }
        this.setCarriedBlock(bvt3);
    }
    
    private boolean isLookingAtMe(final Player awg) {
        final ItemStack bcj3 = awg.inventory.armor.get(3);
        if (bcj3.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            return false;
        }
        final Vec3 csi4 = awg.getViewVector(1.0f).normalize();
        Vec3 csi5 = new Vec3(this.x - awg.x, this.getBoundingBox().minY + this.getEyeHeight() - (awg.y + awg.getEyeHeight()), this.z - awg.z);
        final double double6 = csi5.length();
        csi5 = csi5.normalize();
        final double double7 = csi4.dot(csi5);
        return double7 > 1.0 - 0.025 / double6 && awg.canSee(this);
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 2.55f;
    }
    
    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            for (int integer2 = 0; integer2 < 2; ++integer2) {
                this.level.addParticle(ParticleTypes.PORTAL, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth(), this.y + this.random.nextDouble() * this.getBbHeight() - 0.25, this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth(), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        this.jumping = false;
        super.aiStep();
    }
    
    @Override
    protected void customServerAiStep() {
        if (this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0f);
        }
        if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600) {
            final float float2 = this.getBrightness();
            if (float2 > 0.5f && this.level.canSeeSky(new BlockPos(this)) && this.random.nextFloat() * 30.0f < (float2 - 0.4f) * 2.0f) {
                this.setTarget(null);
                this.teleport();
            }
        }
        super.customServerAiStep();
    }
    
    protected boolean teleport() {
        final double double2 = this.x + (this.random.nextDouble() - 0.5) * 64.0;
        final double double3 = this.y + (this.random.nextInt(64) - 32);
        final double double4 = this.z + (this.random.nextDouble() - 0.5) * 64.0;
        return this.teleport(double2, double3, double4);
    }
    
    private boolean teleportTowards(final Entity aio) {
        Vec3 csi3 = new Vec3(this.x - aio.x, this.getBoundingBox().minY + this.getBbHeight() / 2.0f - aio.y + aio.getEyeHeight(), this.z - aio.z);
        csi3 = csi3.normalize();
        final double double4 = 16.0;
        final double double5 = this.x + (this.random.nextDouble() - 0.5) * 8.0 - csi3.x * 16.0;
        final double double6 = this.y + (this.random.nextInt(16) - 8) - csi3.y * 16.0;
        final double double7 = this.z + (this.random.nextDouble() - 0.5) * 8.0 - csi3.z * 16.0;
        return this.teleport(double5, double6, double7);
    }
    
    private boolean teleport(final double double1, final double double2, final double double3) {
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos(double1, double2, double3);
        while (a8.getY() > 0 && !this.level.getBlockState(a8).getMaterial().blocksMotion()) {
            a8.move(Direction.DOWN);
        }
        if (!this.level.getBlockState(a8).getMaterial().blocksMotion()) {
            return false;
        }
        final boolean boolean9 = this.randomTeleport(double1, double2, double3, true);
        if (boolean9) {
            this.level.playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0f, 1.0f);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        return boolean9;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.ENDERMAN_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }
    
    @Override
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
        super.dropCustomDeathLoot(ahx, integer, boolean3);
        final BlockState bvt5 = this.getCarriedBlock();
        if (bvt5 != null) {
            this.spawnAtLocation(bvt5.getBlock());
        }
    }
    
    public void setCarriedBlock(@Nullable final BlockState bvt) {
        this.entityData.<Optional<BlockState>>set(EnderMan.DATA_CARRY_STATE, (Optional<BlockState>)Optional.ofNullable(bvt));
    }
    
    @Nullable
    public BlockState getCarriedBlock() {
        return (BlockState)this.entityData.<Optional<BlockState>>get(EnderMan.DATA_CARRY_STATE).orElse(null);
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (ahx instanceof IndirectEntityDamageSource || ahx == DamageSource.FIREWORKS) {
            for (int integer4 = 0; integer4 < 64; ++integer4) {
                if (this.teleport()) {
                    return true;
                }
            }
            return false;
        }
        final boolean boolean4 = super.hurt(ahx, float2);
        if (ahx.isBypassArmor() && this.random.nextInt(10) != 0) {
            this.teleport();
        }
        return boolean4;
    }
    
    public boolean isCreepy() {
        return this.entityData.<Boolean>get(EnderMan.DATA_CREEPY);
    }
    
    static {
        SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
        SPEED_MODIFIER_ATTACKING = new AttributeModifier(EnderMan.SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.15000000596046448, AttributeModifier.Operation.ADDITION).setSerialize(false);
        DATA_CARRY_STATE = SynchedEntityData.<Optional<BlockState>>defineId(EnderMan.class, EntityDataSerializers.BLOCK_STATE);
        DATA_CREEPY = SynchedEntityData.<Boolean>defineId(EnderMan.class, EntityDataSerializers.BOOLEAN);
        ENDERMITE_SELECTOR = (aix -> aix instanceof Endermite && ((Endermite)aix).isPlayerSpawned());
    }
    
    static class EndermanLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final EnderMan enderman;
        private Player pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions;
        
        public EndermanLookForPlayerGoal(final EnderMan aui) {
            super(aui, Player.class, false);
            this.continueAggroTargetConditions = new TargetingConditions().allowUnseeable();
            this.enderman = aui;
            this.startAggroTargetConditions = new TargetingConditions().range(this.getFollowDistance()).selector((Predicate<LivingEntity>)(aix -> aui.isLookingAtMe((Player)aix)));
        }
        
        @Override
        public boolean canUse() {
            this.pendingTarget = this.enderman.level.getNearestPlayer(this.startAggroTargetConditions, this.enderman);
            return this.pendingTarget != null;
        }
        
        @Override
        public void start() {
            this.aggroTime = 5;
            this.teleportTime = 0;
        }
        
        @Override
        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }
        
        @Override
        public boolean canContinueToUse() {
            if (this.pendingTarget == null) {
                return (this.target != null && this.continueAggroTargetConditions.test(this.enderman, this.target)) || super.canContinueToUse();
            }
            if (!this.enderman.isLookingAtMe(this.pendingTarget)) {
                return false;
            }
            this.enderman.lookAt(this.pendingTarget, 10.0f, 10.0f);
            return true;
        }
        
        @Override
        public void tick() {
            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
            }
            else {
                if (this.target != null && !this.enderman.isPassenger()) {
                    if (this.enderman.isLookingAtMe((Player)this.target)) {
                        if (this.target.distanceToSqr(this.enderman) < 16.0) {
                            this.enderman.teleport();
                        }
                        this.teleportTime = 0;
                    }
                    else if (this.target.distanceToSqr(this.enderman) > 256.0 && this.teleportTime++ >= 30 && this.enderman.teleportTowards(this.target)) {
                        this.teleportTime = 0;
                    }
                }
                super.tick();
            }
        }
    }
    
    static class EndermanFreezeWhenLookedAt extends Goal {
        private final EnderMan enderman;
        
        public EndermanFreezeWhenLookedAt(final EnderMan aui) {
            this.enderman = aui;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP, (Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = this.enderman.getTarget();
            if (!(aix2 instanceof Player)) {
                return false;
            }
            final double double3 = aix2.distanceToSqr(this.enderman);
            return double3 <= 256.0 && this.enderman.isLookingAtMe((Player)aix2);
        }
        
        @Override
        public void start() {
            this.enderman.getNavigation().stop();
        }
    }
    
    static class EndermanLeaveBlockGoal extends Goal {
        private final EnderMan enderman;
        
        public EndermanLeaveBlockGoal(final EnderMan aui) {
            this.enderman = aui;
        }
        
        @Override
        public boolean canUse() {
            return this.enderman.getCarriedBlock() != null && this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.enderman.getRandom().nextInt(2000) == 0;
        }
        
        @Override
        public void tick() {
            final Random random2 = this.enderman.getRandom();
            final LevelAccessor bhs3 = this.enderman.level;
            final int integer4 = Mth.floor(this.enderman.x - 1.0 + random2.nextDouble() * 2.0);
            final int integer5 = Mth.floor(this.enderman.y + random2.nextDouble() * 2.0);
            final int integer6 = Mth.floor(this.enderman.z - 1.0 + random2.nextDouble() * 2.0);
            final BlockPos ew7 = new BlockPos(integer4, integer5, integer6);
            final BlockState bvt8 = bhs3.getBlockState(ew7);
            final BlockPos ew8 = ew7.below();
            final BlockState bvt9 = bhs3.getBlockState(ew8);
            final BlockState bvt10 = this.enderman.getCarriedBlock();
            if (bvt10 != null && this.canPlaceBlock(bhs3, ew7, bvt10, bvt8, bvt9, ew8)) {
                bhs3.setBlock(ew7, bvt10, 3);
                this.enderman.setCarriedBlock(null);
            }
        }
        
        private boolean canPlaceBlock(final LevelReader bhu, final BlockPos ew2, final BlockState bvt3, final BlockState bvt4, final BlockState bvt5, final BlockPos ew6) {
            return bvt4.isAir() && !bvt5.isAir() && bvt5.isCollisionShapeFullBlock(bhu, ew6) && bvt3.canSurvive(bhu, ew2);
        }
    }
    
    static class EndermanTakeBlockGoal extends Goal {
        private final EnderMan enderman;
        
        public EndermanTakeBlockGoal(final EnderMan aui) {
            this.enderman = aui;
        }
        
        @Override
        public boolean canUse() {
            return this.enderman.getCarriedBlock() == null && this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.enderman.getRandom().nextInt(20) == 0;
        }
        
        @Override
        public void tick() {
            final Random random2 = this.enderman.getRandom();
            final Level bhr3 = this.enderman.level;
            final int integer4 = Mth.floor(this.enderman.x - 2.0 + random2.nextDouble() * 4.0);
            final int integer5 = Mth.floor(this.enderman.y + random2.nextDouble() * 3.0);
            final int integer6 = Mth.floor(this.enderman.z - 2.0 + random2.nextDouble() * 4.0);
            final BlockPos ew7 = new BlockPos(integer4, integer5, integer6);
            final BlockState bvt8 = bhr3.getBlockState(ew7);
            final Block bmv9 = bvt8.getBlock();
            final Vec3 csi10 = new Vec3(Mth.floor(this.enderman.x) + 0.5, integer5 + 0.5, Mth.floor(this.enderman.z) + 0.5);
            final Vec3 csi11 = new Vec3(integer4 + 0.5, integer5 + 0.5, integer6 + 0.5);
            final BlockHitResult csd12 = bhr3.clip(new ClipContext(csi10, csi11, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.enderman));
            final boolean boolean13 = csd12.getType() != HitResult.Type.MISS && csd12.getBlockPos().equals(ew7);
            if (bmv9.is(BlockTags.ENDERMAN_HOLDABLE) && boolean13) {
                this.enderman.setCarriedBlock(bvt8);
                bhr3.removeBlock(ew7, false);
            }
        }
    }
}
