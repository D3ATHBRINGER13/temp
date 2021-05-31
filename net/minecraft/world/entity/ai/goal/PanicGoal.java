package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;

public class PanicGoal extends Goal {
    protected final PathfinderMob mob;
    protected final double speedModifier;
    protected double posX;
    protected double posY;
    protected double posZ;
    
    public PanicGoal(final PathfinderMob aje, final double double2) {
        this.mob = aje;
        this.speedModifier = double2;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
            return false;
        }
        if (this.mob.isOnFire()) {
            final BlockPos ew2 = this.lookForWater(this.mob.level, this.mob, 5, 4);
            if (ew2 != null) {
                this.posX = ew2.getX();
                this.posY = ew2.getY();
                this.posZ = ew2.getZ();
                return true;
            }
        }
        return this.findRandomPosition();
    }
    
    protected boolean findRandomPosition() {
        final Vec3 csi2 = RandomPos.getPos(this.mob, 5, 4);
        if (csi2 == null) {
            return false;
        }
        this.posX = csi2.x;
        this.posY = csi2.y;
        this.posZ = csi2.z;
        return true;
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }
    
    @Nullable
    protected BlockPos lookForWater(final BlockGetter bhb, final Entity aio, final int integer3, final int integer4) {
        final BlockPos ew6 = new BlockPos(aio);
        final int integer5 = ew6.getX();
        final int integer6 = ew6.getY();
        final int integer7 = ew6.getZ();
        float float10 = (float)(integer3 * integer3 * integer4 * 2);
        BlockPos ew7 = null;
        final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
        for (int integer8 = integer5 - integer3; integer8 <= integer5 + integer3; ++integer8) {
            for (int integer9 = integer6 - integer4; integer9 <= integer6 + integer4; ++integer9) {
                for (int integer10 = integer7 - integer3; integer10 <= integer7 + integer3; ++integer10) {
                    a12.set(integer8, integer9, integer10);
                    if (bhb.getFluidState(a12).is(FluidTags.WATER)) {
                        final float float11 = (float)((integer8 - integer5) * (integer8 - integer5) + (integer9 - integer6) * (integer9 - integer6) + (integer10 - integer7) * (integer10 - integer7));
                        if (float11 < float10) {
                            float10 = float11;
                            ew7 = new BlockPos(a12);
                        }
                    }
                }
            }
        }
        return ew7;
    }
}
