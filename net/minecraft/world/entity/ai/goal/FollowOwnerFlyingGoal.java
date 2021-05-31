package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;

public class FollowOwnerFlyingGoal extends FollowOwnerGoal {
    public FollowOwnerFlyingGoal(final TamableAnimal ajl, final double double2, final float float3, final float float4) {
        super(ajl, double2, float3, float4);
    }
    
    @Override
    protected boolean isTeleportFriendlyBlock(final BlockPos ew) {
        final BlockState bvt3 = this.level.getBlockState(ew);
        return (bvt3.entityCanStandOn(this.level, ew, this.tamable) || bvt3.is(BlockTags.LEAVES)) && this.level.isEmptyBlock(ew.above()) && this.level.isEmptyBlock(ew.above(2));
    }
}
