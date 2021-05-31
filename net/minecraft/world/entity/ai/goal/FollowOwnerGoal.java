package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class FollowOwnerGoal extends Goal {
    protected final TamableAnimal tamable;
    private LivingEntity owner;
    protected final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    
    public FollowOwnerGoal(final TamableAnimal ajl, final double double2, final float float3, final float float4) {
        this.tamable = ajl;
        this.level = ajl.level;
        this.speedModifier = double2;
        this.navigation = ajl.getNavigation();
        this.startDistance = float3;
        this.stopDistance = float4;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        if (!(ajl.getNavigation() instanceof GroundPathNavigation) && !(ajl.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }
    
    @Override
    public boolean canUse() {
        final LivingEntity aix2 = this.tamable.getOwner();
        if (aix2 == null) {
            return false;
        }
        if (aix2 instanceof Player && ((Player)aix2).isSpectator()) {
            return false;
        }
        if (this.tamable.isSitting()) {
            return false;
        }
        if (this.tamable.distanceToSqr(aix2) < this.startDistance * this.startDistance) {
            return false;
        }
        this.owner = aix2;
        return true;
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.navigation.isDone() && this.tamable.distanceToSqr(this.owner) > this.stopDistance * this.stopDistance && !this.tamable.isSitting();
    }
    
    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }
    
    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }
    
    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0f, (float)this.tamable.getMaxHeadXRot());
        if (this.tamable.isSitting()) {
            return;
        }
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        if (this.navigation.moveTo(this.owner, this.speedModifier)) {
            return;
        }
        if (this.tamable.isLeashed() || this.tamable.isPassenger()) {
            return;
        }
        if (this.tamable.distanceToSqr(this.owner) < 144.0) {
            return;
        }
        final int integer2 = Mth.floor(this.owner.x) - 2;
        final int integer3 = Mth.floor(this.owner.z) - 2;
        final int integer4 = Mth.floor(this.owner.getBoundingBox().minY);
        for (int integer5 = 0; integer5 <= 4; ++integer5) {
            for (int integer6 = 0; integer6 <= 4; ++integer6) {
                if (integer5 < 1 || integer6 < 1 || integer5 > 3 || integer6 > 3) {
                    if (this.isTeleportFriendlyBlock(new BlockPos(integer2 + integer5, integer4 - 1, integer3 + integer6))) {
                        this.tamable.moveTo(integer2 + integer5 + 0.5f, integer4, integer3 + integer6 + 0.5f, this.tamable.yRot, this.tamable.xRot);
                        this.navigation.stop();
                        return;
                    }
                }
            }
        }
    }
    
    protected boolean isTeleportFriendlyBlock(final BlockPos ew) {
        final BlockState bvt3 = this.level.getBlockState(ew);
        return bvt3.isValidSpawn(this.level, ew, this.tamable.getType()) && this.level.isEmptyBlock(ew.above()) && this.level.isEmptyBlock(ew.above(2));
    }
}
