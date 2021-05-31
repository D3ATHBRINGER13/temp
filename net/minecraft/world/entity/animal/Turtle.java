package net.minecraft.world.entity.animal;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.TurtleNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import java.util.EnumSet;
import com.google.common.collect.Sets;
import net.minecraft.world.item.Item;
import java.util.Set;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.core.Position;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Turtle extends Animal {
    private static final EntityDataAccessor<BlockPos> HOME_POS;
    private static final EntityDataAccessor<Boolean> HAS_EGG;
    private static final EntityDataAccessor<Boolean> LAYING_EGG;
    private static final EntityDataAccessor<BlockPos> TRAVEL_POS;
    private static final EntityDataAccessor<Boolean> GOING_HOME;
    private static final EntityDataAccessor<Boolean> TRAVELLING;
    private int layEggCounter;
    public static final Predicate<LivingEntity> BABY_ON_LAND_SELECTOR;
    
    public Turtle(final EntityType<? extends Turtle> ais, final Level bhr) {
        super(ais, bhr);
        this.moveControl = new TurtleMoveControl(this);
        this.maxUpStep = 1.0f;
    }
    
    public void setHomePos(final BlockPos ew) {
        this.entityData.<BlockPos>set(Turtle.HOME_POS, ew);
    }
    
    private BlockPos getHomePos() {
        return this.entityData.<BlockPos>get(Turtle.HOME_POS);
    }
    
    private void setTravelPos(final BlockPos ew) {
        this.entityData.<BlockPos>set(Turtle.TRAVEL_POS, ew);
    }
    
    private BlockPos getTravelPos() {
        return this.entityData.<BlockPos>get(Turtle.TRAVEL_POS);
    }
    
    public boolean hasEgg() {
        return this.entityData.<Boolean>get(Turtle.HAS_EGG);
    }
    
    private void setHasEgg(final boolean boolean1) {
        this.entityData.<Boolean>set(Turtle.HAS_EGG, boolean1);
    }
    
    public boolean isLayingEgg() {
        return this.entityData.<Boolean>get(Turtle.LAYING_EGG);
    }
    
    private void setLayingEgg(final boolean boolean1) {
        this.layEggCounter = (boolean1 ? 1 : 0);
        this.entityData.<Boolean>set(Turtle.LAYING_EGG, boolean1);
    }
    
    private boolean isGoingHome() {
        return this.entityData.<Boolean>get(Turtle.GOING_HOME);
    }
    
    private void setGoingHome(final boolean boolean1) {
        this.entityData.<Boolean>set(Turtle.GOING_HOME, boolean1);
    }
    
    private boolean isTravelling() {
        return this.entityData.<Boolean>get(Turtle.TRAVELLING);
    }
    
    private void setTravelling(final boolean boolean1) {
        this.entityData.<Boolean>set(Turtle.TRAVELLING, boolean1);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<BlockPos>define(Turtle.HOME_POS, BlockPos.ZERO);
        this.entityData.<Boolean>define(Turtle.HAS_EGG, false);
        this.entityData.<BlockPos>define(Turtle.TRAVEL_POS, BlockPos.ZERO);
        this.entityData.<Boolean>define(Turtle.GOING_HOME, false);
        this.entityData.<Boolean>define(Turtle.TRAVELLING, false);
        this.entityData.<Boolean>define(Turtle.LAYING_EGG, false);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("HomePosX", this.getHomePos().getX());
        id.putInt("HomePosY", this.getHomePos().getY());
        id.putInt("HomePosZ", this.getHomePos().getZ());
        id.putBoolean("HasEgg", this.hasEgg());
        id.putInt("TravelPosX", this.getTravelPos().getX());
        id.putInt("TravelPosY", this.getTravelPos().getY());
        id.putInt("TravelPosZ", this.getTravelPos().getZ());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        final int integer3 = id.getInt("HomePosX");
        final int integer4 = id.getInt("HomePosY");
        final int integer5 = id.getInt("HomePosZ");
        this.setHomePos(new BlockPos(integer3, integer4, integer5));
        super.readAdditionalSaveData(id);
        this.setHasEgg(id.getBoolean("HasEgg"));
        final int integer6 = id.getInt("TravelPosX");
        final int integer7 = id.getInt("TravelPosY");
        final int integer8 = id.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(integer6, integer7, integer8));
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.setHomePos(new BlockPos(this));
        this.setTravelPos(BlockPos.ZERO);
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    public static boolean checkTurtleSpawnRules(final EntityType<Turtle> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return ew.getY() < bhs.getSeaLevel() + 4 && bhs.getBlockState(ew.below()).getBlock() == Blocks.SAND && bhs.getRawBrightness(ew, 0) > 8;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TurtlePanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new TurtleBreedGoal(this, 1.0));
        this.goalSelector.addGoal(1, new TurtleLayEggGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TurtleTemptGoal(this, 1.1, Blocks.SEAGRASS.asItem()));
        this.goalSelector.addGoal(3, new TurtleGoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TurtleGoHomeGoal(this, 1.0));
        this.goalSelector.addGoal(7, new TurtleTravelGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(9, new TurtleRandomStrollGoal(this, 1.0, 100));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!this.isInWater() && this.onGround && !this.isBaby()) {
            return SoundEvents.TURTLE_AMBIENT_LAND;
        }
        return super.getAmbientSound();
    }
    
    @Override
    protected void playSwimSound(final float float1) {
        super.playSwimSound(float1 * 1.5f);
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.TURTLE_SWIM;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        if (this.isBaby()) {
            return SoundEvents.TURTLE_HURT_BABY;
        }
        return SoundEvents.TURTLE_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isBaby()) {
            return SoundEvents.TURTLE_DEATH_BABY;
        }
        return SoundEvents.TURTLE_DEATH;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        final SoundEvent yo4 = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
        this.playSound(yo4, 0.15f, 1.0f);
    }
    
    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }
    
    @Override
    protected float nextStep() {
        return this.moveDist + 0.15f;
    }
    
    @Override
    public float getScale() {
        return this.isBaby() ? 0.3f : 1.0f;
    }
    
    @Override
    protected PathNavigation createNavigation(final Level bhr) {
        return new TurtlePathNavigation(this, bhr);
    }
    
    @Nullable
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return EntityType.TURTLE.create(this.level);
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return bcj.getItem() == Blocks.SEAGRASS.asItem();
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        if (!this.isGoingHome() && bhu.getFluidState(ew).is(FluidTags.WATER)) {
            return 10.0f;
        }
        if (bhu.getBlockState(ew.below()).getBlock() == Blocks.SAND) {
            return 10.0f;
        }
        return bhu.getBrightness(ew) - 0.5f;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
            final BlockPos ew2 = new BlockPos(this);
            if (this.level.getBlockState(ew2.below()).getBlock() == Blocks.SAND) {
                this.level.levelEvent(2001, ew2, Block.getId(Blocks.SAND.defaultBlockState()));
            }
        }
    }
    
    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SCUTE, 1);
        }
    }
    
    @Override
    public void travel(final Vec3 csi) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1f, csi);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (this.getTarget() == null && (!this.isGoingHome() || !this.getHomePos().closerThan(this.position(), 20.0))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        }
        else {
            super.travel(csi);
        }
    }
    
    @Override
    public boolean canBeLeashed(final Player awg) {
        return false;
    }
    
    @Override
    public void thunderHit(final LightningBolt atu) {
        this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }
    
    static {
        HOME_POS = SynchedEntityData.<BlockPos>defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
        HAS_EGG = SynchedEntityData.<Boolean>defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
        LAYING_EGG = SynchedEntityData.<Boolean>defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
        TRAVEL_POS = SynchedEntityData.<BlockPos>defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
        GOING_HOME = SynchedEntityData.<Boolean>defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
        TRAVELLING = SynchedEntityData.<Boolean>defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
        BABY_ON_LAND_SELECTOR = (aix -> aix.isBaby() && !aix.isInWater());
    }
    
    static class TurtlePanicGoal extends PanicGoal {
        TurtlePanicGoal(final Turtle arx, final double double2) {
            super(arx, double2);
        }
        
        @Override
        public boolean canUse() {
            if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
                return false;
            }
            final BlockPos ew2 = this.lookForWater(this.mob.level, this.mob, 7, 4);
            if (ew2 != null) {
                this.posX = ew2.getX();
                this.posY = ew2.getY();
                this.posZ = ew2.getZ();
                return true;
            }
            return this.findRandomPosition();
        }
    }
    
    static class TurtleTravelGoal extends Goal {
        private final Turtle turtle;
        private final double speedModifier;
        private boolean stuck;
        
        TurtleTravelGoal(final Turtle arx, final double double2) {
            this.turtle = arx;
            this.speedModifier = double2;
        }
        
        @Override
        public boolean canUse() {
            return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }
        
        @Override
        public void start() {
            final int integer2 = 512;
            final int integer3 = 4;
            final Random random4 = this.turtle.random;
            final int integer4 = random4.nextInt(1025) - 512;
            int integer5 = random4.nextInt(9) - 4;
            final int integer6 = random4.nextInt(1025) - 512;
            if (integer5 + this.turtle.y > this.turtle.level.getSeaLevel() - 1) {
                integer5 = 0;
            }
            final BlockPos ew8 = new BlockPos(integer4 + this.turtle.x, integer5 + this.turtle.y, integer6 + this.turtle.z);
            this.turtle.setTravelPos(ew8);
            this.turtle.setTravelling(true);
            this.stuck = false;
        }
        
        @Override
        public void tick() {
            if (this.turtle.getNavigation().isDone()) {
                final BlockPos ew2 = this.turtle.getTravelPos();
                Vec3 csi3 = RandomPos.getPosTowards(this.turtle, 16, 3, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()), 0.3141592741012573);
                if (csi3 == null) {
                    csi3 = RandomPos.getPosTowards(this.turtle, 8, 7, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()));
                }
                if (csi3 != null) {
                    final int integer4 = Mth.floor(csi3.x);
                    final int integer5 = Mth.floor(csi3.z);
                    final int integer6 = 34;
                    if (!this.turtle.level.hasChunksAt(integer4 - 34, 0, integer5 - 34, integer4 + 34, 0, integer5 + 34)) {
                        csi3 = null;
                    }
                }
                if (csi3 == null) {
                    this.stuck = true;
                    return;
                }
                this.turtle.getNavigation().moveTo(csi3.x, csi3.y, csi3.z, this.speedModifier);
            }
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }
        
        @Override
        public void stop() {
            this.turtle.setTravelling(false);
            super.stop();
        }
    }
    
    static class TurtleGoHomeGoal extends Goal {
        private final Turtle turtle;
        private final double speedModifier;
        private boolean stuck;
        private int closeToHomeTryTicks;
        
        TurtleGoHomeGoal(final Turtle arx, final double double2) {
            this.turtle = arx;
            this.speedModifier = double2;
        }
        
        @Override
        public boolean canUse() {
            return !this.turtle.isBaby() && (this.turtle.hasEgg() || (this.turtle.getRandom().nextInt(700) == 0 && !this.turtle.getHomePos().closerThan(this.turtle.position(), 64.0)));
        }
        
        @Override
        public void start() {
            this.turtle.setGoingHome(true);
            this.stuck = false;
            this.closeToHomeTryTicks = 0;
        }
        
        @Override
        public void stop() {
            this.turtle.setGoingHome(false);
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getHomePos().closerThan(this.turtle.position(), 7.0) && !this.stuck && this.closeToHomeTryTicks <= 600;
        }
        
        @Override
        public void tick() {
            final BlockPos ew2 = this.turtle.getHomePos();
            final boolean boolean3 = ew2.closerThan(this.turtle.position(), 16.0);
            if (boolean3) {
                ++this.closeToHomeTryTicks;
            }
            if (this.turtle.getNavigation().isDone()) {
                Vec3 csi4 = RandomPos.getPosTowards(this.turtle, 16, 3, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()), 0.3141592741012573);
                if (csi4 == null) {
                    csi4 = RandomPos.getPosTowards(this.turtle, 8, 7, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()));
                }
                if (csi4 != null && !boolean3 && this.turtle.level.getBlockState(new BlockPos(csi4)).getBlock() != Blocks.WATER) {
                    csi4 = RandomPos.getPosTowards(this.turtle, 16, 5, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()));
                }
                if (csi4 == null) {
                    this.stuck = true;
                    return;
                }
                this.turtle.getNavigation().moveTo(csi4.x, csi4.y, csi4.z, this.speedModifier);
            }
        }
    }
    
    static class TurtleTemptGoal extends Goal {
        private static final TargetingConditions TEMPT_TARGETING;
        private final Turtle turtle;
        private final double speedModifier;
        private Player player;
        private int calmDown;
        private final Set<Item> items;
        
        TurtleTemptGoal(final Turtle arx, final double double2, final Item bce) {
            this.turtle = arx;
            this.speedModifier = double2;
            this.items = (Set<Item>)Sets.newHashSet((Object[])new Item[] { bce });
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            if (this.calmDown > 0) {
                --this.calmDown;
                return false;
            }
            this.player = this.turtle.level.getNearestPlayer(TurtleTemptGoal.TEMPT_TARGETING, this.turtle);
            return this.player != null && (this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem()));
        }
        
        private boolean shouldFollowItem(final ItemStack bcj) {
            return this.items.contains(bcj.getItem());
        }
        
        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }
        
        @Override
        public void stop() {
            this.player = null;
            this.turtle.getNavigation().stop();
            this.calmDown = 100;
        }
        
        @Override
        public void tick() {
            this.turtle.getLookControl().setLookAt(this.player, (float)(this.turtle.getMaxHeadYRot() + 20), (float)this.turtle.getMaxHeadXRot());
            if (this.turtle.distanceToSqr(this.player) < 6.25) {
                this.turtle.getNavigation().stop();
            }
            else {
                this.turtle.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }
        
        static {
            TEMPT_TARGETING = new TargetingConditions().range(10.0).allowSameTeam().allowInvulnerable();
        }
    }
    
    static class TurtleBreedGoal extends BreedGoal {
        private final Turtle turtle;
        
        TurtleBreedGoal(final Turtle arx, final double double2) {
            super(arx, double2);
            this.turtle = arx;
        }
        
        @Override
        public boolean canUse() {
            return super.canUse() && !this.turtle.hasEgg();
        }
        
        @Override
        protected void breed() {
            ServerPlayer vl2 = this.animal.getLoveCause();
            if (vl2 == null && this.partner.getLoveCause() != null) {
                vl2 = this.partner.getLoveCause();
            }
            if (vl2 != null) {
                vl2.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(vl2, this.animal, this.partner, null);
            }
            this.turtle.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            final Random random3 = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.x, this.animal.y, this.animal.z, random3.nextInt(7) + 1));
            }
        }
    }
    
    static class TurtleLayEggGoal extends MoveToBlockGoal {
        private final Turtle turtle;
        
        TurtleLayEggGoal(final Turtle arx, final double double2) {
            super(arx, double2, 16);
            this.turtle = arx;
        }
        
        @Override
        public boolean canUse() {
            return this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0) && super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0);
        }
        
        @Override
        public void tick() {
            super.tick();
            final BlockPos ew2 = new BlockPos(this.turtle);
            if (!this.turtle.isInWater() && this.isReachedTarget()) {
                if (this.turtle.layEggCounter < 1) {
                    this.turtle.setLayingEgg(true);
                }
                else if (this.turtle.layEggCounter > 200) {
                    final Level bhr3 = this.turtle.level;
                    bhr3.playSound(null, ew2, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3f, 0.9f + bhr3.random.nextFloat() * 0.2f);
                    bhr3.setBlock(this.blockPos.above(), ((AbstractStateHolder<O, BlockState>)Blocks.TURTLE_EGG.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setLayingEgg(false);
                    this.turtle.setInLoveTime(600);
                }
                if (this.turtle.isLayingEgg()) {
                    this.turtle.layEggCounter++;
                }
            }
        }
        
        @Override
        protected boolean isValidTarget(final LevelReader bhu, final BlockPos ew) {
            if (!bhu.isEmptyBlock(ew.above())) {
                return false;
            }
            final Block bmv4 = bhu.getBlockState(ew).getBlock();
            return bmv4 == Blocks.SAND;
        }
    }
    
    static class TurtleRandomStrollGoal extends RandomStrollGoal {
        private final Turtle turtle;
        
        private TurtleRandomStrollGoal(final Turtle arx, final double double2, final int integer) {
            super(arx, double2, integer);
            this.turtle = arx;
        }
        
        @Override
        public boolean canUse() {
            return !this.mob.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() && super.canUse();
        }
    }
    
    static class TurtleGoToWaterGoal extends MoveToBlockGoal {
        private final Turtle turtle;
        
        private TurtleGoToWaterGoal(final Turtle arx, final double double2) {
            super(arx, arx.isBaby() ? 2.0 : double2, 24);
            this.turtle = arx;
            this.verticalSearchStart = -1;
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
        }
        
        @Override
        public boolean canUse() {
            if (this.turtle.isBaby() && !this.turtle.isInWater()) {
                return super.canUse();
            }
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() && super.canUse();
        }
        
        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 160 == 0;
        }
        
        @Override
        protected boolean isValidTarget(final LevelReader bhu, final BlockPos ew) {
            final Block bmv4 = bhu.getBlockState(ew).getBlock();
            return bmv4 == Blocks.WATER;
        }
    }
    
    static class TurtleMoveControl extends MoveControl {
        private final Turtle turtle;
        
        TurtleMoveControl(final Turtle arx) {
            super(arx);
            this.turtle = arx;
        }
        
        private void updateSpeed() {
            if (this.turtle.isInWater()) {
                this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHomePos().closerThan(this.turtle.position(), 16.0)) {
                    this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0f, 0.08f));
                }
                if (this.turtle.isBaby()) {
                    this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0f, 0.06f));
                }
            }
            else if (this.turtle.onGround) {
                this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0f, 0.06f));
            }
        }
        
        @Override
        public void tick() {
            this.updateSpeed();
            if (this.operation != Operation.MOVE_TO || this.turtle.getNavigation().isDone()) {
                this.turtle.setSpeed(0.0f);
                return;
            }
            final double double2 = this.wantedX - this.turtle.x;
            double double3 = this.wantedY - this.turtle.y;
            final double double4 = this.wantedZ - this.turtle.z;
            final double double5 = Mth.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
            double3 /= double5;
            final float float10 = (float)(Mth.atan2(double4, double2) * 57.2957763671875) - 90.0f;
            this.turtle.yRot = this.rotlerp(this.turtle.yRot, float10, 90.0f);
            this.turtle.yBodyRot = this.turtle.yRot;
            final float float11 = (float)(this.speedModifier * this.turtle.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.turtle.setSpeed(Mth.lerp(0.125f, this.turtle.getSpeed(), float11));
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, this.turtle.getSpeed() * double3 * 0.1, 0.0));
        }
    }
    
    static class TurtlePathNavigation extends WaterBoundPathNavigation {
        TurtlePathNavigation(final Turtle arx, final Level bhr) {
            super(arx, bhr);
        }
        
        @Override
        protected boolean canUpdatePath() {
            return true;
        }
        
        @Override
        protected PathFinder createPathFinder(final int integer) {
            return new PathFinder(new TurtleNodeEvaluator(), integer);
        }
        
        @Override
        public boolean isStableDestination(final BlockPos ew) {
            if (this.mob instanceof Turtle) {
                final Turtle arx3 = (Turtle)this.mob;
                if (arx3.isTravelling()) {
                    return this.level.getBlockState(ew).getBlock() == Blocks.WATER;
                }
            }
            return !this.level.getBlockState(ew.below()).isAir();
        }
    }
}
