package net.minecraft.world.entity.raid;

import java.util.Objects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.core.Position;
import java.util.List;
import java.util.EnumSet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.function.Predicate;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.PatrollingMonster;

public abstract class Raider extends PatrollingMonster {
    protected static final EntityDataAccessor<Boolean> IS_CELEBRATING;
    private static final Predicate<ItemEntity> ALLOWED_ITEMS;
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean canJoinRaid;
    private int ticksOutsideRaid;
    
    protected Raider(final EntityType<? extends Raider> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ObtainRaidLeaderBannerGoal<>(this));
        this.goalSelector.addGoal(3, new PathfindToRaidGoal<>(this));
        this.goalSelector.addGoal(4, new RaiderMoveThroughVillageGoal(this, 1.0499999523162842, 1));
        this.goalSelector.addGoal(5, new RaiderCelebration(this));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(Raider.IS_CELEBRATING, false);
    }
    
    public abstract void applyRaidBuffs(final int integer, final boolean boolean2);
    
    public boolean canJoinRaid() {
        return this.canJoinRaid;
    }
    
    public void setCanJoinRaid(final boolean boolean1) {
        this.canJoinRaid = boolean1;
    }
    
    @Override
    public void aiStep() {
        if (this.level instanceof ServerLevel && this.isAlive()) {
            final Raid axk2 = this.getCurrentRaid();
            if (this.canJoinRaid()) {
                if (axk2 == null) {
                    if (this.level.getGameTime() % 20L == 0L) {
                        final Raid axk3 = ((ServerLevel)this.level).getRaidAt(new BlockPos(this));
                        if (axk3 != null && Raids.canJoinRaid(this, axk3)) {
                            axk3.joinRaid(axk3.getGroupsSpawned(), this, null, true);
                        }
                    }
                }
                else {
                    final LivingEntity aix3 = this.getTarget();
                    if (aix3 != null && (aix3.getType() == EntityType.PLAYER || aix3.getType() == EntityType.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }
        super.aiStep();
    }
    
    @Override
    protected void updateNoActionTime() {
        this.noActionTime += 2;
    }
    
    public void die(final DamageSource ahx) {
        if (this.level instanceof ServerLevel) {
            final Entity aio3 = ahx.getEntity();
            final Raid axk4 = this.getCurrentRaid();
            if (axk4 != null) {
                if (this.isPatrolLeader()) {
                    axk4.removeLeader(this.getWave());
                }
                if (aio3 != null && aio3.getType() == EntityType.PLAYER) {
                    axk4.addHeroOfTheVillage(aio3);
                }
                axk4.removeFromRaid(this, false);
            }
            if (this.isPatrolLeader() && axk4 == null && ((ServerLevel)this.level).getRaidAt(new BlockPos(this)) == null) {
                final ItemStack bcj5 = this.getItemBySlot(EquipmentSlot.HEAD);
                Player awg6 = null;
                final Entity aio4 = aio3;
                if (aio4 instanceof Player) {
                    awg6 = (Player)aio4;
                }
                else if (aio4 instanceof Wolf) {
                    final Wolf arz8 = (Wolf)aio4;
                    final LivingEntity aix9 = arz8.getOwner();
                    if (arz8.isTame() && aix9 instanceof Player) {
                        awg6 = (Player)aix9;
                    }
                }
                if (!bcj5.isEmpty() && ItemStack.matches(bcj5, Raid.getLeaderBannerInstance()) && awg6 != null) {
                    final MobEffectInstance aii8 = awg6.getEffect(MobEffects.BAD_OMEN);
                    int integer9 = 1;
                    if (aii8 != null) {
                        integer9 += aii8.getAmplifier();
                        awg6.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                    }
                    else {
                        --integer9;
                    }
                    integer9 = Mth.clamp(integer9, 0, 5);
                    final MobEffectInstance aii9 = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, integer9, false, false, true);
                    if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                        awg6.addEffect(aii9);
                    }
                }
            }
        }
        super.die(ahx);
    }
    
    @Override
    public boolean canJoinPatrol() {
        return !this.hasActiveRaid();
    }
    
    public void setCurrentRaid(@Nullable final Raid axk) {
        this.raid = axk;
    }
    
    @Nullable
    public Raid getCurrentRaid() {
        return this.raid;
    }
    
    public boolean hasActiveRaid() {
        return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
    }
    
    public void setWave(final int integer) {
        this.wave = integer;
    }
    
    public int getWave() {
        return this.wave;
    }
    
    public boolean isCelebrating() {
        return this.entityData.<Boolean>get(Raider.IS_CELEBRATING);
    }
    
    public void setCelebrating(final boolean boolean1) {
        this.entityData.<Boolean>set(Raider.IS_CELEBRATING, boolean1);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Wave", this.wave);
        id.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            id.putInt("RaidId", this.raid.getId());
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.wave = id.getInt("Wave");
        this.canJoinRaid = id.getBoolean("CanJoinRaid");
        if (id.contains("RaidId", 3)) {
            if (this.level instanceof ServerLevel) {
                this.raid = ((ServerLevel)this.level).getRaids().get(id.getInt("RaidId"));
            }
            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }
    }
    
    @Override
    protected void pickUpItem(final ItemEntity atx) {
        final ItemStack bcj3 = atx.getItem();
        final boolean boolean4 = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
        if (this.hasActiveRaid() && !boolean4 && ItemStack.matches(bcj3, Raid.getLeaderBannerInstance())) {
            final EquipmentSlot ait5 = EquipmentSlot.HEAD;
            final ItemStack bcj4 = this.getItemBySlot(ait5);
            final double double7 = this.getEquipmentDropChance(ait5);
            if (!bcj4.isEmpty() && this.random.nextFloat() - 0.1f < double7) {
                this.spawnAtLocation(bcj4);
            }
            this.setItemSlot(ait5, bcj3);
            this.take(atx, bcj3.getCount());
            atx.remove();
            this.getCurrentRaid().setLeader(this.getWave(), this);
            this.setPatrolLeader(true);
        }
        else {
            super.pickUpItem(atx);
        }
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return this.getCurrentRaid() == null && super.removeWhenFarAway(double1);
    }
    
    @Override
    public boolean requiresCustomPersistence() {
        return this.getCurrentRaid() != null;
    }
    
    public int getTicksOutsideRaid() {
        return this.ticksOutsideRaid;
    }
    
    public void setTicksOutsideRaid(final int integer) {
        this.ticksOutsideRaid = integer;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.hasActiveRaid()) {
            this.getCurrentRaid().updateBossbar();
        }
        return super.hurt(ahx, float2);
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.setCanJoinRaid(this.getType() != EntityType.WITCH || aja != MobSpawnType.NATURAL);
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    public abstract SoundEvent getCelebrateSound();
    
    static {
        IS_CELEBRATING = SynchedEntityData.<Boolean>defineId(Raider.class, EntityDataSerializers.BOOLEAN);
        ALLOWED_ITEMS = (atx -> !atx.hasPickUpDelay() && atx.isAlive() && ItemStack.matches(atx.getItem(), Raid.getLeaderBannerInstance()));
    }
    
    public class ObtainRaidLeaderBannerGoal<T extends Raider> extends Goal {
        private final T mob;
        
        public ObtainRaidLeaderBannerGoal(final T axl2) {
            this.mob = axl2;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            final Raid axk2 = this.mob.getCurrentRaid();
            if (!this.mob.hasActiveRaid() || this.mob.getCurrentRaid().isOver() || !this.mob.canBeLeader() || ItemStack.matches(this.mob.getItemBySlot(EquipmentSlot.HEAD), Raid.getLeaderBannerInstance())) {
                return false;
            }
            final Raider axl3 = axk2.getLeader(this.mob.getWave());
            if (axl3 == null || !axl3.isAlive()) {
                final List<ItemEntity> list4 = this.mob.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, this.mob.getBoundingBox().inflate(16.0, 8.0, 16.0), (java.util.function.Predicate<? super ItemEntity>)Raider.ALLOWED_ITEMS);
                if (!list4.isEmpty()) {
                    return this.mob.getNavigation().moveTo((Entity)list4.get(0), 1.149999976158142);
                }
            }
            return false;
        }
        
        @Override
        public void tick() {
            if (this.mob.getNavigation().getTargetPos().closerThan(this.mob.position(), 1.414)) {
                final List<ItemEntity> list2 = this.mob.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, this.mob.getBoundingBox().inflate(4.0, 4.0, 4.0), (java.util.function.Predicate<? super ItemEntity>)Raider.ALLOWED_ITEMS);
                if (!list2.isEmpty()) {
                    this.mob.pickUpItem((ItemEntity)list2.get(0));
                }
            }
        }
    }
    
    public class RaiderCelebration extends Goal {
        private final Raider mob;
        
        RaiderCelebration(final Raider axl2) {
            this.mob = axl2;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            final Raid axk2 = this.mob.getCurrentRaid();
            return this.mob.isAlive() && this.mob.getTarget() == null && axk2 != null && axk2.isLoss();
        }
        
        @Override
        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }
        
        @Override
        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }
        
        @Override
        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(100) == 0) {
                Raider.this.playSound(Raider.this.getCelebrateSound(), LivingEntity.this.getSoundVolume(), LivingEntity.this.getVoicePitch());
            }
            if (!this.mob.isPassenger() && this.mob.random.nextInt(50) == 0) {
                this.mob.getJumpControl().jump();
            }
            super.tick();
        }
    }
    
    public class HoldGroundAttackGoal extends Goal {
        private final Raider mob;
        private final float hostileRadiusSqr;
        public final TargetingConditions shoutTargeting;
        
        public HoldGroundAttackGoal(final AbstractIllager aua, final float float3) {
            this.shoutTargeting = new TargetingConditions().range(8.0).allowNonAttackable().allowInvulnerable().allowSameTeam().allowUnseeable().ignoreInvisibilityTesting();
            this.mob = aua;
            this.hostileRadiusSqr = float3 * float3;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = this.mob.getLastHurtByMob();
            return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && (aix2 == null || aix2.getType() != EntityType.PLAYER);
        }
        
        @Override
        public void start() {
            super.start();
            this.mob.getNavigation().stop();
            final List<Raider> list2 = this.mob.level.<Raider>getNearbyEntities((java.lang.Class<? extends Raider>)Raider.class, this.shoutTargeting, (LivingEntity)this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
            for (final Raider axl4 : list2) {
                axl4.setTarget(this.mob.getTarget());
            }
        }
        
        @Override
        public void stop() {
            super.stop();
            final LivingEntity aix2 = this.mob.getTarget();
            if (aix2 != null) {
                final List<Raider> list3 = this.mob.level.<Raider>getNearbyEntities((java.lang.Class<? extends Raider>)Raider.class, this.shoutTargeting, (LivingEntity)this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
                for (final Raider axl5 : list3) {
                    axl5.setTarget(aix2);
                    axl5.setAggressive(true);
                }
                this.mob.setAggressive(true);
            }
        }
        
        @Override
        public void tick() {
            final LivingEntity aix2 = this.mob.getTarget();
            if (aix2 == null) {
                return;
            }
            if (this.mob.distanceToSqr(aix2) > this.hostileRadiusSqr) {
                this.mob.getLookControl().setLookAt(aix2, 30.0f, 30.0f);
                if (this.mob.random.nextInt(50) == 0) {
                    this.mob.playAmbientSound();
                }
            }
            else {
                this.mob.setAggressive(true);
            }
            super.tick();
        }
    }
    
    static class RaiderMoveThroughVillageGoal extends Goal {
        private final Raider raider;
        private final double speedModifier;
        private BlockPos poiPos;
        private final List<BlockPos> visited;
        private final int distanceToPoi;
        private boolean stuck;
        
        public RaiderMoveThroughVillageGoal(final Raider axl, final double double2, final int integer) {
            this.visited = (List<BlockPos>)Lists.newArrayList();
            this.raider = axl;
            this.speedModifier = double2;
            this.distanceToPoi = integer;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            this.updateVisited();
            return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }
        
        private boolean isValidRaid() {
            return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
        }
        
        private boolean hasSuitablePoi() {
            final ServerLevel vk2 = (ServerLevel)this.raider.level;
            final BlockPos ew3 = new BlockPos(this.raider);
            final Optional<BlockPos> optional4 = vk2.getPoiManager().getRandom((Predicate<PoiType>)(aqs -> aqs == PoiType.HOME), (Predicate<BlockPos>)this::hasNotVisited, PoiManager.Occupancy.ANY, ew3, 48, this.raider.random);
            if (!optional4.isPresent()) {
                return false;
            }
            this.poiPos = ((BlockPos)optional4.get()).immutable();
            return true;
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.raider.getNavigation().isDone() && this.raider.getTarget() == null && !this.poiPos.closerThan(this.raider.position(), this.raider.getBbWidth() + this.distanceToPoi) && !this.stuck;
        }
        
        @Override
        public void stop() {
            if (this.poiPos.closerThan(this.raider.position(), this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }
        }
        
        @Override
        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo(this.poiPos.getX(), this.poiPos.getY(), this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }
        
        @Override
        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                final int integer2 = this.poiPos.getX();
                final int integer3 = this.poiPos.getY();
                final int integer4 = this.poiPos.getZ();
                Vec3 csi5 = RandomPos.getPosTowards(this.raider, 16, 7, new Vec3(integer2, integer3, integer4), 0.3141592741012573);
                if (csi5 == null) {
                    csi5 = RandomPos.getPosTowards(this.raider, 8, 7, new Vec3(integer2, integer3, integer4));
                }
                if (csi5 == null) {
                    this.stuck = true;
                    return;
                }
                this.raider.getNavigation().moveTo(csi5.x, csi5.y, csi5.z, this.speedModifier);
            }
        }
        
        private boolean hasNotVisited(final BlockPos ew) {
            for (final BlockPos ew2 : this.visited) {
                if (Objects.equals(ew, ew2)) {
                    return false;
                }
            }
            return true;
        }
        
        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }
        }
    }
}
