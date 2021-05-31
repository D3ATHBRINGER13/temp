package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.block.Block;
import java.util.Iterator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.PathfinderMob;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomStrollGoal {
    public WaterAvoidingRandomFlyingGoal(final PathfinderMob aje, final double double2) {
        super(aje, double2);
    }
    
    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 csi2 = null;
        if (this.mob.isInWater()) {
            csi2 = RandomPos.getLandPos(this.mob, 15, 15);
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            csi2 = this.getTreePos();
        }
        return (csi2 == null) ? super.getPosition() : csi2;
    }
    
    @Nullable
    private Vec3 getTreePos() {
        final BlockPos ew2 = new BlockPos(this.mob);
        final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos a4 = new BlockPos.MutableBlockPos();
        final Iterable<BlockPos> iterable5 = BlockPos.betweenClosed(Mth.floor(this.mob.x - 3.0), Mth.floor(this.mob.y - 6.0), Mth.floor(this.mob.z - 3.0), Mth.floor(this.mob.x + 3.0), Mth.floor(this.mob.y + 6.0), Mth.floor(this.mob.z + 3.0));
        for (final BlockPos ew3 : iterable5) {
            if (ew2.equals(ew3)) {
                continue;
            }
            final Block bmv8 = this.mob.level.getBlockState(a4.set(ew3).move(Direction.DOWN)).getBlock();
            final boolean boolean9 = bmv8 instanceof LeavesBlock || bmv8.is(BlockTags.LOGS);
            if (boolean9 && this.mob.level.isEmptyBlock(ew3) && this.mob.level.isEmptyBlock(a3.set(ew3).move(Direction.UP))) {
                return new Vec3(ew3.getX(), ew3.getY(), ew3.getZ());
            }
        }
        return null;
    }
}
