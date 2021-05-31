package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class SwimNodeEvaluator extends NodeEvaluator {
    private final boolean allowBreaching;
    
    public SwimNodeEvaluator(final boolean boolean1) {
        this.allowBreaching = boolean1;
    }
    
    @Override
    public Node getStart() {
        return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
    }
    
    @Override
    public Target getGoal(final double double1, final double double2, final double double3) {
        return new Target(super.getNode(Mth.floor(double1 - this.mob.getBbWidth() / 2.0f), Mth.floor(double2 + 0.5), Mth.floor(double3 - this.mob.getBbWidth() / 2.0f)));
    }
    
    @Override
    public int getNeighbors(final Node[] arr, final Node cnp) {
        int integer4 = 0;
        for (final Direction fb8 : Direction.values()) {
            final Node cnp2 = this.getWaterNode(cnp.x + fb8.getStepX(), cnp.y + fb8.getStepY(), cnp.z + fb8.getStepZ());
            if (cnp2 != null && !cnp2.closed) {
                arr[integer4++] = cnp2;
            }
        }
        return integer4;
    }
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4, final Mob aiy, final int integer6, final int integer7, final int integer8, final boolean boolean9, final boolean boolean10) {
        return this.getBlockPathType(bhb, integer2, integer3, integer4);
    }
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4) {
        final BlockPos ew6 = new BlockPos(integer2, integer3, integer4);
        final FluidState clk7 = bhb.getFluidState(ew6);
        final BlockState bvt8 = bhb.getBlockState(ew6);
        if (clk7.isEmpty() && bvt8.isPathfindable(bhb, ew6.below(), PathComputationType.WATER) && bvt8.isAir()) {
            return BlockPathTypes.BREACH;
        }
        if (!clk7.is(FluidTags.WATER) || !bvt8.isPathfindable(bhb, ew6, PathComputationType.WATER)) {
            return BlockPathTypes.BLOCKED;
        }
        return BlockPathTypes.WATER;
    }
    
    @Nullable
    private Node getWaterNode(final int integer1, final int integer2, final int integer3) {
        final BlockPathTypes cnn5 = this.isFree(integer1, integer2, integer3);
        if ((this.allowBreaching && cnn5 == BlockPathTypes.BREACH) || cnn5 == BlockPathTypes.WATER) {
            return this.getNode(integer1, integer2, integer3);
        }
        return null;
    }
    
    @Nullable
    @Override
    protected Node getNode(final int integer1, final int integer2, final int integer3) {
        Node cnp5 = null;
        final BlockPathTypes cnn6 = this.getBlockPathType(this.mob.level, integer1, integer2, integer3);
        final float float7 = this.mob.getPathfindingMalus(cnn6);
        if (float7 >= 0.0f) {
            cnp5 = super.getNode(integer1, integer2, integer3);
            cnp5.type = cnn6;
            cnp5.costMalus = Math.max(cnp5.costMalus, float7);
            if (this.level.getFluidState(new BlockPos(integer1, integer2, integer3)).isEmpty()) {
                final Node node = cnp5;
                node.costMalus += 8.0f;
            }
        }
        if (cnn6 == BlockPathTypes.OPEN) {
            return cnp5;
        }
        return cnp5;
    }
    
    private BlockPathTypes isFree(final int integer1, final int integer2, final int integer3) {
        final BlockPos.MutableBlockPos a5 = new BlockPos.MutableBlockPos();
        for (int integer4 = integer1; integer4 < integer1 + this.entityWidth; ++integer4) {
            for (int integer5 = integer2; integer5 < integer2 + this.entityHeight; ++integer5) {
                for (int integer6 = integer3; integer6 < integer3 + this.entityDepth; ++integer6) {
                    final FluidState clk9 = this.level.getFluidState(a5.set(integer4, integer5, integer6));
                    final BlockState bvt10 = this.level.getBlockState(a5.set(integer4, integer5, integer6));
                    if (clk9.isEmpty() && bvt10.isPathfindable(this.level, a5.below(), PathComputationType.WATER) && bvt10.isAir()) {
                        return BlockPathTypes.BREACH;
                    }
                    if (!clk9.is(FluidTags.WATER)) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
        }
        final BlockState bvt11 = this.level.getBlockState(a5);
        if (bvt11.isPathfindable(this.level, a5, PathComputationType.WATER)) {
            return BlockPathTypes.WATER;
        }
        return BlockPathTypes.BLOCKED;
    }
}
