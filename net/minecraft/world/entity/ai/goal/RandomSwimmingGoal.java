package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.PathfinderMob;

public class RandomSwimmingGoal extends RandomStrollGoal {
    public RandomSwimmingGoal(final PathfinderMob aje, final double double2, final int integer) {
        super(aje, double2, integer);
    }
    
    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 csi2 = RandomPos.getPos(this.mob, 10, 7);
        for (int integer3 = 0; csi2 != null && !this.mob.level.getBlockState(new BlockPos(csi2)).isPathfindable(this.mob.level, new BlockPos(csi2), PathComputationType.WATER) && integer3++ < 10; csi2 = RandomPos.getPos(this.mob, 10, 7)) {}
        return csi2;
    }
}
