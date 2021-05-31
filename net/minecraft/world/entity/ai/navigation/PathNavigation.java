package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.Position;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.Util;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.entity.Entity;
import com.google.common.collect.ImmutableSet;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import javax.annotation.Nullable;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;

public abstract class PathNavigation {
    protected final Mob mob;
    protected final Level level;
    @Nullable
    protected Path path;
    protected double speedModifier;
    private final AttributeInstance dist;
    protected int tick;
    protected int lastStuckCheck;
    protected Vec3 lastStuckCheckPos;
    protected Vec3 timeoutCachedNode;
    protected long timeoutTimer;
    protected long lastTimeoutCheck;
    protected double timeoutLimit;
    protected float maxDistanceToWaypoint;
    protected boolean hasDelayedRecomputation;
    protected long timeLastRecompute;
    protected NodeEvaluator nodeEvaluator;
    private BlockPos targetPos;
    private int reachRange;
    private PathFinder pathFinder;
    
    public PathNavigation(final Mob aiy, final Level bhr) {
        this.lastStuckCheckPos = Vec3.ZERO;
        this.timeoutCachedNode = Vec3.ZERO;
        this.maxDistanceToWaypoint = 0.5f;
        this.mob = aiy;
        this.level = bhr;
        this.dist = aiy.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        this.pathFinder = this.createPathFinder(Mth.floor(this.dist.getValue() * 16.0));
    }
    
    public BlockPos getTargetPos() {
        return this.targetPos;
    }
    
    protected abstract PathFinder createPathFinder(final int integer);
    
    public void setSpeedModifier(final double double1) {
        this.speedModifier = double1;
    }
    
    public float getMaxDist() {
        return (float)this.dist.getValue();
    }
    
    public boolean hasDelayedRecomputation() {
        return this.hasDelayedRecomputation;
    }
    
    public void recomputePath() {
        if (this.level.getGameTime() - this.timeLastRecompute > 20L) {
            if (this.targetPos != null) {
                this.path = null;
                this.path = this.createPath(this.targetPos, this.reachRange);
                this.timeLastRecompute = this.level.getGameTime();
                this.hasDelayedRecomputation = false;
            }
        }
        else {
            this.hasDelayedRecomputation = true;
        }
    }
    
    @Nullable
    public final Path createPath(final double double1, final double double2, final double double3, final int integer) {
        return this.createPath(new BlockPos(double1, double2, double3), integer);
    }
    
    @Nullable
    public Path createPath(final Stream<BlockPos> stream, final int integer) {
        return this.createPath((Set<BlockPos>)stream.collect(Collectors.toSet()), 8, false, integer);
    }
    
    @Nullable
    public Path createPath(final BlockPos ew, final int integer) {
        return this.createPath((Set<BlockPos>)ImmutableSet.of(ew), 8, false, integer);
    }
    
    @Nullable
    public Path createPath(final Entity aio, final int integer) {
        return this.createPath((Set<BlockPos>)ImmutableSet.of(new BlockPos(aio)), 16, true, integer);
    }
    
    @Nullable
    protected Path createPath(final Set<BlockPos> set, final int integer2, final boolean boolean3, final int integer4) {
        if (set.isEmpty()) {
            return null;
        }
        if (this.mob.y < 0.0) {
            return null;
        }
        if (!this.canUpdatePath()) {
            return null;
        }
        if (this.path != null && !this.path.isDone() && set.contains(this.targetPos)) {
            return this.path;
        }
        this.level.getProfiler().push("pathfind");
        final float float6 = this.getMaxDist();
        final BlockPos ew7 = boolean3 ? new BlockPos(this.mob).above() : new BlockPos(this.mob);
        final int integer5 = (int)(float6 + integer2);
        final LevelReader bhu9 = new PathNavigationRegion(this.level, ew7.offset(-integer5, -integer5, -integer5), ew7.offset(integer5, integer5, integer5));
        final Path cnr10 = this.pathFinder.findPath(bhu9, this.mob, set, float6, integer4);
        this.level.getProfiler().pop();
        if (cnr10 != null && cnr10.getTarget() != null) {
            this.targetPos = cnr10.getTarget();
            this.reachRange = integer4;
        }
        return cnr10;
    }
    
    public boolean moveTo(final double double1, final double double2, final double double3, final double double4) {
        return this.moveTo(this.createPath(double1, double2, double3, 1), double4);
    }
    
    public boolean moveTo(final Entity aio, final double double2) {
        final Path cnr5 = this.createPath(aio, 1);
        return cnr5 != null && this.moveTo(cnr5, double2);
    }
    
    public boolean moveTo(@Nullable final Path cnr, final double double2) {
        if (cnr == null) {
            this.path = null;
            return false;
        }
        if (!cnr.sameAs(this.path)) {
            this.path = cnr;
        }
        this.trimPath();
        if (this.path.getSize() <= 0) {
            return false;
        }
        this.speedModifier = double2;
        final Vec3 csi5 = this.getTempMobPos();
        this.lastStuckCheck = this.tick;
        this.lastStuckCheckPos = csi5;
        return true;
    }
    
    @Nullable
    public Path getPath() {
        return this.path;
    }
    
    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }
        if (this.isDone()) {
            return;
        }
        if (this.canUpdatePath()) {
            this.updatePath();
        }
        else if (this.path != null && this.path.getIndex() < this.path.getSize()) {
            final Vec3 csi2 = this.getTempMobPos();
            final Vec3 csi3 = this.path.getPos(this.mob, this.path.getIndex());
            if (csi2.y > csi3.y && !this.mob.onGround && Mth.floor(csi2.x) == Mth.floor(csi3.x) && Mth.floor(csi2.z) == Mth.floor(csi3.z)) {
                this.path.setIndex(this.path.getIndex() + 1);
            }
        }
        DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
        if (this.isDone()) {
            return;
        }
        final Vec3 csi2 = this.path.currentPos(this.mob);
        final BlockPos ew3 = new BlockPos(csi2);
        this.mob.getMoveControl().setWantedPosition(csi2.x, this.level.getBlockState(ew3.below()).isAir() ? csi2.y : WalkNodeEvaluator.getFloorLevel(this.level, ew3), csi2.z, this.speedModifier);
    }
    
    protected void updatePath() {
        final Vec3 csi2 = this.getTempMobPos();
        this.maxDistanceToWaypoint = ((this.mob.getBbWidth() > 0.75f) ? (this.mob.getBbWidth() / 2.0f) : (0.75f - this.mob.getBbWidth() / 2.0f));
        final Vec3 csi3 = this.path.currentPos();
        if (Math.abs(this.mob.x - (csi3.x + 0.5)) < this.maxDistanceToWaypoint && Math.abs(this.mob.z - (csi3.z + 0.5)) < this.maxDistanceToWaypoint && Math.abs(this.mob.y - csi3.y) < 1.0) {
            this.path.setIndex(this.path.getIndex() + 1);
        }
        this.doStuckDetection(csi2);
    }
    
    protected void doStuckDetection(final Vec3 csi) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (csi.distanceToSqr(this.lastStuckCheckPos) < 2.25) {
                this.stop();
            }
            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = csi;
        }
        if (this.path != null && !this.path.isDone()) {
            final Vec3 csi2 = this.path.currentPos();
            if (csi2.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
            }
            else {
                this.timeoutCachedNode = csi2;
                final double double4 = csi.distanceTo(this.timeoutCachedNode);
                this.timeoutLimit = ((this.mob.getSpeed() > 0.0f) ? (double4 / this.mob.getSpeed() * 1000.0) : 0.0);
            }
            if (this.timeoutLimit > 0.0 && this.timeoutTimer > this.timeoutLimit * 3.0) {
                this.timeoutCachedNode = Vec3.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0;
                this.stop();
            }
            this.lastTimeoutCheck = Util.getMillis();
        }
    }
    
    public boolean isDone() {
        return this.path == null || this.path.isDone();
    }
    
    public void stop() {
        this.path = null;
    }
    
    protected abstract Vec3 getTempMobPos();
    
    protected abstract boolean canUpdatePath();
    
    protected boolean isInLiquid() {
        return this.mob.isInWaterOrBubble() || this.mob.isInLava();
    }
    
    protected void trimPath() {
        if (this.path == null) {
            return;
        }
        for (int integer2 = 0; integer2 < this.path.getSize(); ++integer2) {
            final Node cnp3 = this.path.get(integer2);
            final Node cnp4 = (integer2 + 1 < this.path.getSize()) ? this.path.get(integer2 + 1) : null;
            final BlockState bvt5 = this.level.getBlockState(new BlockPos(cnp3.x, cnp3.y, cnp3.z));
            final Block bmv6 = bvt5.getBlock();
            if (bmv6 == Blocks.CAULDRON) {
                this.path.set(integer2, cnp3.cloneMove(cnp3.x, cnp3.y + 1, cnp3.z));
                if (cnp4 != null && cnp3.y >= cnp4.y) {
                    this.path.set(integer2 + 1, cnp4.cloneMove(cnp4.x, cnp3.y + 1, cnp4.z));
                }
            }
        }
    }
    
    protected abstract boolean canMoveDirectly(final Vec3 csi1, final Vec3 csi2, final int integer3, final int integer4, final int integer5);
    
    public boolean isStableDestination(final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        return this.level.getBlockState(ew2).isSolidRender(this.level, ew2);
    }
    
    public NodeEvaluator getNodeEvaluator() {
        return this.nodeEvaluator;
    }
    
    public void setCanFloat(final boolean boolean1) {
        this.nodeEvaluator.setCanFloat(boolean1);
    }
    
    public boolean canFloat() {
        return this.nodeEvaluator.canFloat();
    }
    
    public void recomputePath(final BlockPos ew) {
        if (this.path == null || this.path.isDone() || this.path.getSize() == 0) {
            return;
        }
        final Node cnp3 = this.path.last();
        final Vec3 csi4 = new Vec3((cnp3.x + this.mob.x) / 2.0, (cnp3.y + this.mob.y) / 2.0, (cnp3.z + this.mob.z) / 2.0);
        if (ew.closerThan(csi4, this.path.getSize() - this.path.getIndex())) {
            this.recomputePath();
        }
    }
}
