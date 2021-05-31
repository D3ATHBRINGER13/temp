package net.minecraft.world.entity.monster;

import net.minecraft.world.effect.MobEffects;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.LevelType;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Mob;

public class Slime extends Mob implements Enemy {
    private static final EntityDataAccessor<Integer> ID_SIZE;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;
    
    public Slime(final EntityType<? extends Slime> ais, final Level bhr) {
        super(ais, bhr);
        this.moveControl = new SlimeMoveControl(this);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)(aix -> Math.abs(aix.y - this.y) <= 4.0)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Slime.ID_SIZE, 1);
    }
    
    protected void setSize(final int integer, final boolean boolean2) {
        this.entityData.<Integer>set(Slime.ID_SIZE, integer);
        this.setPos(this.x, this.y, this.z);
        this.refreshDimensions();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(integer * integer);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * integer);
        if (boolean2) {
            this.setHealth(this.getMaxHealth());
        }
        this.xpReward = integer;
    }
    
    public int getSize() {
        return this.entityData.<Integer>get(Slime.ID_SIZE);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Size", this.getSize() - 1);
        id.putBoolean("wasOnGround", this.wasOnGround);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        int integer3 = id.getInt("Size");
        if (integer3 < 0) {
            integer3 = 0;
        }
        this.setSize(integer3 + 1, false);
        this.wasOnGround = id.getBoolean("wasOnGround");
    }
    
    public boolean isTiny() {
        return this.getSize() <= 1;
    }
    
    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }
    
    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
            this.removed = true;
        }
        this.squish += (this.targetSquish - this.squish) * 0.5f;
        this.oSquish = this.squish;
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            for (int integer2 = this.getSize(), integer3 = 0; integer3 < integer2 * 8; ++integer3) {
                final float float4 = this.random.nextFloat() * 6.2831855f;
                final float float5 = this.random.nextFloat() * 0.5f + 0.5f;
                final float float6 = Mth.sin(float4) * integer2 * 0.5f * float5;
                final float float7 = Mth.cos(float4) * integer2 * 0.5f * float5;
                this.level.addParticle(this.getParticleType(), this.x + float6, this.getBoundingBox().minY, this.z + float7, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            this.targetSquish = -0.5f;
        }
        else if (!this.onGround && this.wasOnGround) {
            this.targetSquish = 1.0f;
        }
        this.wasOnGround = this.onGround;
        this.decreaseSquish();
    }
    
    protected void decreaseSquish() {
        this.targetSquish *= 0.6f;
    }
    
    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (Slime.ID_SIZE.equals(qk)) {
            this.refreshDimensions();
            this.yRot = this.yHeadRot;
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
        super.onSyncedDataUpdated(qk);
    }
    
    public EntityType<? extends Slime> getType() {
        return super.getType();
    }
    
    public void remove() {
        final int integer2 = this.getSize();
        if (!this.level.isClientSide && integer2 > 1 && this.getHealth() <= 0.0f) {
            for (int integer3 = 2 + this.random.nextInt(3), integer4 = 0; integer4 < integer3; ++integer4) {
                final float float5 = (integer4 % 2 - 0.5f) * integer2 / 4.0f;
                final float float6 = (integer4 / 2 - 0.5f) * integer2 / 4.0f;
                final Slime ave7 = (Slime)this.getType().create(this.level);
                if (this.hasCustomName()) {
                    ave7.setCustomName(this.getCustomName());
                }
                if (this.isPersistenceRequired()) {
                    ave7.setPersistenceRequired();
                }
                ave7.setSize(integer2 / 2, true);
                ave7.moveTo(this.x + float5, this.y + 0.5, this.z + float6, this.random.nextFloat() * 360.0f, 0.0f);
                this.level.addFreshEntity(ave7);
            }
        }
        super.remove();
    }
    
    @Override
    public void push(final Entity aio) {
        super.push(aio);
        if (aio instanceof IronGolem && this.isDealsDamage()) {
            this.dealDamage((LivingEntity)aio);
        }
    }
    
    public void playerTouch(final Player awg) {
        if (this.isDealsDamage()) {
            this.dealDamage(awg);
        }
    }
    
    protected void dealDamage(final LivingEntity aix) {
        if (this.isAlive()) {
            final int integer3 = this.getSize();
            if (this.distanceToSqr(aix) < 0.6 * integer3 * (0.6 * integer3) && this.canSee(aix) && aix.hurt(DamageSource.mobAttack(this), (float)this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                this.doEnchantDamageEffects(this, aix);
            }
        }
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.625f * aip.height;
    }
    
    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }
    
    protected int getAttackDamage() {
        return this.getSize();
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        if (this.isTiny()) {
            return SoundEvents.SLIME_HURT_SMALL;
        }
        return SoundEvents.SLIME_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isTiny()) {
            return SoundEvents.SLIME_DEATH_SMALL;
        }
        return SoundEvents.SLIME_DEATH;
    }
    
    protected SoundEvent getSquishSound() {
        if (this.isTiny()) {
            return SoundEvents.SLIME_SQUISH_SMALL;
        }
        return SoundEvents.SLIME_SQUISH;
    }
    
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return (this.getSize() == 1) ? this.getType().getDefaultLootTable() : BuiltInLootTables.EMPTY;
    }
    
    public static boolean checkSlimeSpawnRules(final EntityType<Slime> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        if (bhs.getLevelData().getGeneratorType() == LevelType.FLAT && random.nextInt(4) != 1) {
            return false;
        }
        if (bhs.getDifficulty() != Difficulty.PEACEFUL) {
            final Biome bio6 = bhs.getBiome(ew);
            if (bio6 == Biomes.SWAMP && ew.getY() > 50 && ew.getY() < 70 && random.nextFloat() < 0.5f && random.nextFloat() < bhs.getMoonBrightness() && bhs.getMaxLocalRawBrightness(ew) <= random.nextInt(8)) {
                return Mob.checkMobSpawnRules(ais, bhs, aja, ew, random);
            }
            final ChunkPos bhd7 = new ChunkPos(ew);
            final boolean boolean8 = WorldgenRandom.seedSlimeChunk(bhd7.x, bhd7.z, bhs.getSeed(), 987234911L).nextInt(10) == 0;
            if (random.nextInt(10) == 0 && boolean8 && ew.getY() < 40) {
                return Mob.checkMobSpawnRules(ais, bhs, aja, ew, random);
            }
        }
        return false;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f * this.getSize();
    }
    
    @Override
    public int getMaxHeadXRot() {
        return 0;
    }
    
    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }
    
    @Override
    protected void jumpFromGround() {
        final Vec3 csi2 = this.getDeltaMovement();
        this.setDeltaMovement(csi2.x, 0.41999998688697815, csi2.z);
        this.hasImpulse = true;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        int integer7 = this.random.nextInt(3);
        if (integer7 < 2 && this.random.nextFloat() < 0.5f * ahh.getSpecialMultiplier()) {
            ++integer7;
        }
        final int integer8 = 1 << integer7;
        this.setSize(integer8, true);
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }
    
    @Override
    public EntityDimensions getDimensions(final Pose ajh) {
        return super.getDimensions(ajh).scale(0.255f * this.getSize());
    }
    
    static {
        ID_SIZE = SynchedEntityData.<Integer>defineId(Slime.class, EntityDataSerializers.INT);
    }
    
    static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final Slime slime;
        private boolean isAggressive;
        
        public SlimeMoveControl(final Slime ave) {
            super(ave);
            this.slime = ave;
            this.yRot = 180.0f * ave.yRot / 3.1415927f;
        }
        
        public void setDirection(final float float1, final boolean boolean2) {
            this.yRot = float1;
            this.isAggressive = boolean2;
        }
        
        public void setWantedMovement(final double double1) {
            this.speedModifier = double1;
            this.operation = Operation.MOVE_TO;
        }
        
        @Override
        public void tick() {
            this.mob.yRot = this.rotlerp(this.mob.yRot, this.yRot, 90.0f);
            this.mob.yHeadRot = this.mob.yRot;
            this.mob.yBodyRot = this.mob.yRot;
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0f);
                return;
            }
            this.operation = Operation.WAIT;
            if (this.mob.onGround) {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = this.slime.getJumpDelay();
                    if (this.isAggressive) {
                        this.jumpDelay /= 3;
                    }
                    this.slime.getJumpControl().jump();
                    if (this.slime.doPlayJumpSound()) {
                        this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRandom().nextFloat() - this.slime.getRandom().nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    }
                }
                else {
                    this.slime.xxa = 0.0f;
                    this.slime.zza = 0.0f;
                    this.mob.setSpeed(0.0f);
                }
            }
            else {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
            }
        }
    }
    
    static class SlimeAttackGoal extends Goal {
        private final Slime slime;
        private int growTiredTimer;
        
        public SlimeAttackGoal(final Slime ave) {
            this.slime = ave;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = this.slime.getTarget();
            return aix2 != null && aix2.isAlive() && (!(aix2 instanceof Player) || !((Player)aix2).abilities.invulnerable) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }
        
        @Override
        public void start() {
            this.growTiredTimer = 300;
            super.start();
        }
        
        @Override
        public boolean canContinueToUse() {
            final LivingEntity aix2 = this.slime.getTarget();
            return aix2 != null && aix2.isAlive() && (!(aix2 instanceof Player) || !((Player)aix2).abilities.invulnerable) && --this.growTiredTimer > 0;
        }
        
        @Override
        public void tick() {
            this.slime.lookAt(this.slime.getTarget(), 10.0f, 10.0f);
            ((SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.slime.yRot, this.slime.isDealsDamage());
        }
    }
    
    static class SlimeRandomDirectionGoal extends Goal {
        private final Slime slime;
        private float chosenDegrees;
        private int nextRandomizeTime;
        
        public SlimeRandomDirectionGoal(final Slime ave) {
            this.slime = ave;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }
        
        @Override
        public void tick() {
            final int nextRandomizeTime = this.nextRandomizeTime - 1;
            this.nextRandomizeTime = nextRandomizeTime;
            if (nextRandomizeTime <= 0) {
                this.nextRandomizeTime = 40 + this.slime.getRandom().nextInt(60);
                this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }
    
    static class SlimeFloatGoal extends Goal {
        private final Slime slime;
        
        public SlimeFloatGoal(final Slime ave) {
            this.slime = ave;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP, (Enum)Flag.MOVE));
            ave.getNavigation().setCanFloat(true);
        }
        
        @Override
        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }
        
        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8f) {
                this.slime.getJumpControl().jump();
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.2);
        }
    }
    
    static class SlimeKeepOnJumpingGoal extends Goal {
        private final Slime slime;
        
        public SlimeKeepOnJumpingGoal(final Slime ave) {
            this.slime = ave;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP, (Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            return !this.slime.isPassenger();
        }
        
        @Override
        public void tick() {
            ((SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.0);
        }
    }
}
