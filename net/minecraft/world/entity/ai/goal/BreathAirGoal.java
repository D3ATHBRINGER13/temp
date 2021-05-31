package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;

public class BreathAirGoal extends Goal {
    private final PathfinderMob mob;
    
    public BreathAirGoal(final PathfinderMob aje) {
        this.mob = aje;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        return this.mob.getAirSupply() < 140;
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }
    
    @Override
    public boolean isInterruptable() {
        return false;
    }
    
    @Override
    public void start() {
        this.findAirPosition();
    }
    
    private void findAirPosition() {
        final Iterable<BlockPos> iterable2 = BlockPos.betweenClosed(Mth.floor(this.mob.x - 1.0), Mth.floor(this.mob.y), Mth.floor(this.mob.z - 1.0), Mth.floor(this.mob.x + 1.0), Mth.floor(this.mob.y + 8.0), Mth.floor(this.mob.z + 1.0));
        BlockPos ew3 = null;
        for (final BlockPos ew4 : iterable2) {
            if (this.givesAir(this.mob.level, ew4)) {
                ew3 = ew4;
                break;
            }
        }
        if (ew3 == null) {
            ew3 = new BlockPos(this.mob.x, this.mob.y + 8.0, this.mob.z);
        }
        this.mob.getNavigation().moveTo(ew3.getX(), ew3.getY() + 1, ew3.getZ(), 1.0);
    }
    
    @Override
    public void tick() {
        this.findAirPosition();
        this.mob.moveRelative(0.02f, new Vec3(this.mob.xxa, this.mob.yya, this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
    }
    
    private boolean givesAir(final LevelReader bhu, final BlockPos ew) {
        final BlockState bvt4 = bhu.getBlockState(ew);
        return (bhu.getFluidState(ew).isEmpty() || bvt4.getBlock() == Blocks.BUBBLE_COLUMN) && bvt4.isPathfindable(bhu, ew, PathComputationType.LAND);
    }
}
