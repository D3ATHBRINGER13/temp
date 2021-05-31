package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;

public class TryFindWaterGoal extends Goal {
    private final PathfinderMob mob;
    
    public TryFindWaterGoal(final PathfinderMob aje) {
        this.mob = aje;
    }
    
    @Override
    public boolean canUse() {
        return this.mob.onGround && !this.mob.level.getFluidState(new BlockPos(this.mob)).is(FluidTags.WATER);
    }
    
    @Override
    public void start() {
        BlockPos ew2 = null;
        final Iterable<BlockPos> iterable3 = BlockPos.betweenClosed(Mth.floor(this.mob.x - 2.0), Mth.floor(this.mob.y - 2.0), Mth.floor(this.mob.z - 2.0), Mth.floor(this.mob.x + 2.0), Mth.floor(this.mob.y), Mth.floor(this.mob.z + 2.0));
        for (final BlockPos ew3 : iterable3) {
            if (this.mob.level.getFluidState(ew3).is(FluidTags.WATER)) {
                ew2 = ew3;
                break;
            }
        }
        if (ew2 != null) {
            this.mob.getMoveControl().setWantedPosition(ew2.getX(), ew2.getY(), ew2.getZ(), 1.0);
        }
    }
}
