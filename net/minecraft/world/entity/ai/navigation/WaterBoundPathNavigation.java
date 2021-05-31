package net.minecraft.world.entity.ai.navigation;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;

public class WaterBoundPathNavigation extends PathNavigation {
    private boolean allowBreaching;
    
    public WaterBoundPathNavigation(final Mob aiy, final Level bhr) {
        super(aiy, bhr);
    }
    
    @Override
    protected PathFinder createPathFinder(final int integer) {
        this.allowBreaching = (this.mob instanceof Dolphin);
        this.nodeEvaluator = new SwimNodeEvaluator(this.allowBreaching);
        return new PathFinder(this.nodeEvaluator, integer);
    }
    
    @Override
    protected boolean canUpdatePath() {
        return this.allowBreaching || this.isInLiquid();
    }
    
    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.x, this.mob.y + this.mob.getBbHeight() * 0.5, this.mob.z);
    }
    
    @Override
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
            final Vec3 csi2 = this.path.getPos(this.mob, this.path.getIndex());
            if (Mth.floor(this.mob.x) == Mth.floor(csi2.x) && Mth.floor(this.mob.y) == Mth.floor(csi2.y) && Mth.floor(this.mob.z) == Mth.floor(csi2.z)) {
                this.path.setIndex(this.path.getIndex() + 1);
            }
        }
        DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
        if (this.isDone()) {
            return;
        }
        final Vec3 csi2 = this.path.currentPos(this.mob);
        this.mob.getMoveControl().setWantedPosition(csi2.x, csi2.y, csi2.z, this.speedModifier);
    }
    
    @Override
    protected void updatePath() {
        if (this.path == null) {
            return;
        }
        final Vec3 csi2 = this.getTempMobPos();
        final float float3 = this.mob.getBbWidth();
        float float4 = (float3 > 0.75f) ? (float3 / 2.0f) : (0.75f - float3 / 2.0f);
        final Vec3 csi3 = this.mob.getDeltaMovement();
        if (Math.abs(csi3.x) > 0.2 || Math.abs(csi3.z) > 0.2) {
            float4 *= (float)(csi3.length() * 6.0);
        }
        final int integer6 = 6;
        Vec3 csi4 = this.path.currentPos();
        if (Math.abs(this.mob.x - (csi4.x + 0.5)) < float4 && Math.abs(this.mob.z - (csi4.z + 0.5)) < float4 && Math.abs(this.mob.y - csi4.y) < float4 * 2.0f) {
            this.path.next();
        }
        for (int integer7 = Math.min(this.path.getIndex() + 6, this.path.getSize() - 1); integer7 > this.path.getIndex(); --integer7) {
            csi4 = this.path.getPos(this.mob, integer7);
            if (csi4.distanceToSqr(csi2) <= 36.0) {
                if (this.canMoveDirectly(csi2, csi4, 0, 0, 0)) {
                    this.path.setIndex(integer7);
                    break;
                }
            }
        }
        this.doStuckDetection(csi2);
    }
    
    @Override
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
                this.timeoutLimit = ((this.mob.getSpeed() > 0.0f) ? (double4 / this.mob.getSpeed() * 100.0) : 0.0);
            }
            if (this.timeoutLimit > 0.0 && this.timeoutTimer > this.timeoutLimit * 2.0) {
                this.timeoutCachedNode = Vec3.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0;
                this.stop();
            }
            this.lastTimeoutCheck = Util.getMillis();
        }
    }
    
    @Override
    protected boolean canMoveDirectly(final Vec3 csi1, final Vec3 csi2, final int integer3, final int integer4, final int integer5) {
        final Vec3 csi3 = new Vec3(csi2.x, csi2.y + this.mob.getBbHeight() * 0.5, csi2.z);
        return this.level.clip(new ClipContext(csi1, csi3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
    }
    
    @Override
    public boolean isStableDestination(final BlockPos ew) {
        return !this.level.getBlockState(ew).isSolidRender(this.level, ew);
    }
    
    @Override
    public void setCanFloat(final boolean boolean1) {
    }
}
