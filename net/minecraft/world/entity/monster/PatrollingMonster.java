package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import java.util.function.Predicate;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Position;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import java.util.Random;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;

public abstract class PatrollingMonster extends Monster {
    private BlockPos patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;
    
    protected PatrollingMonster(final EntityType<? extends PatrollingMonster> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new LongDistancePatrolGoal<>(this, 0.7, 0.595));
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.patrolTarget != null) {
            id.put("PatrolTarget", (Tag)NbtUtils.writeBlockPos(this.patrolTarget));
        }
        id.putBoolean("PatrolLeader", this.patrolLeader);
        id.putBoolean("Patrolling", this.patrolling);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("PatrolTarget")) {
            this.patrolTarget = NbtUtils.readBlockPos(id.getCompound("PatrolTarget"));
        }
        this.patrolLeader = id.getBoolean("PatrolLeader");
        this.patrolling = id.getBoolean("Patrolling");
    }
    
    public double getRidingHeight() {
        return -0.45;
    }
    
    public boolean canBeLeader() {
        return true;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        if (aja != MobSpawnType.PATROL && aja != MobSpawnType.EVENT && aja != MobSpawnType.STRUCTURE && this.random.nextFloat() < 0.06f && this.canBeLeader()) {
            this.patrolLeader = true;
        }
        if (this.isPatrolLeader()) {
            this.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
            this.setDropChance(EquipmentSlot.HEAD, 2.0f);
        }
        if (aja == MobSpawnType.PATROL) {
            this.patrolling = true;
        }
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    public static boolean checkPatrollingMonsterSpawnRules(final EntityType<? extends PatrollingMonster> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getBrightness(LightLayer.BLOCK, ew) <= 8 && Monster.checkAnyLightMonsterSpawnRules(ais, bhs, aja, ew, random);
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return !this.patrolling || double1 > 16384.0;
    }
    
    public void setPatrolTarget(final BlockPos ew) {
        this.patrolTarget = ew;
        this.patrolling = true;
    }
    
    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }
    
    public boolean hasPatrolTarget() {
        return this.patrolTarget != null;
    }
    
    public void setPatrolLeader(final boolean boolean1) {
        this.patrolLeader = boolean1;
        this.patrolling = true;
    }
    
    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }
    
    public boolean canJoinPatrol() {
        return true;
    }
    
    public void findPatrolTarget() {
        this.patrolTarget = new BlockPos(this).offset(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }
    
    protected boolean isPatrolling() {
        return this.patrolling;
    }
    
    public static class LongDistancePatrolGoal<T extends PatrollingMonster> extends Goal {
        private final T mob;
        private final double speedModifier;
        private final double leaderSpeedModifier;
        
        public LongDistancePatrolGoal(final T aut, final double double2, final double double3) {
            this.mob = aut;
            this.speedModifier = double2;
            this.leaderSpeedModifier = double3;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            return this.mob.isPatrolling() && this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasPatrolTarget();
        }
        
        @Override
        public void start() {
        }
        
        @Override
        public void stop() {
        }
        
        @Override
        public void tick() {
            final boolean boolean2 = this.mob.isPatrolLeader();
            final PathNavigation app3 = this.mob.getNavigation();
            if (app3.isDone()) {
                if (!boolean2 || !this.mob.getPatrolTarget().closerThan(this.mob.position(), 10.0)) {
                    Vec3 csi4 = new Vec3(this.mob.getPatrolTarget());
                    final Vec3 csi5 = new Vec3(this.mob.x, this.mob.y, this.mob.z);
                    final Vec3 csi6 = csi5.subtract(csi4);
                    csi4 = csi6.yRot(90.0f).scale(0.4).add(csi4);
                    final Vec3 csi7 = csi4.subtract(csi5).normalize().scale(10.0).add(csi5);
                    BlockPos ew8 = new BlockPos(csi7);
                    ew8 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ew8);
                    if (!app3.moveTo(ew8.getX(), ew8.getY(), ew8.getZ(), boolean2 ? this.leaderSpeedModifier : this.speedModifier)) {
                        this.moveRandomly();
                    }
                    else if (boolean2) {
                        final List<PatrollingMonster> list9 = this.mob.level.<PatrollingMonster>getEntitiesOfClass((java.lang.Class<? extends PatrollingMonster>)PatrollingMonster.class, this.mob.getBoundingBox().inflate(16.0), (java.util.function.Predicate<? super PatrollingMonster>)(aut -> !aut.isPatrolLeader() && aut.canJoinPatrol()));
                        for (final PatrollingMonster aut11 : list9) {
                            aut11.setPatrolTarget(ew8);
                        }
                    }
                }
                else {
                    this.mob.findPatrolTarget();
                }
            }
        }
        
        private void moveRandomly() {
            final Random random2 = this.mob.getRandom();
            final BlockPos ew3 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.mob).offset(-8 + random2.nextInt(16), 0, -8 + random2.nextInt(16)));
            this.mob.getNavigation().moveTo(ew3.getX(), ew3.getY(), ew3.getZ(), this.speedModifier);
        }
    }
}
