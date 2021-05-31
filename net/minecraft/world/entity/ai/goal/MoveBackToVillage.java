package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;

public class MoveBackToVillage extends RandomStrollGoal {
    public MoveBackToVillage(final PathfinderMob aje, final double double2) {
        super(aje, double2, 10);
    }
    
    @Override
    public boolean canUse() {
        final ServerLevel vk2 = (ServerLevel)this.mob.level;
        final BlockPos ew3 = new BlockPos(this.mob);
        return !vk2.isVillage(ew3) && super.canUse();
    }
    
    @Nullable
    @Override
    protected Vec3 getPosition() {
        final ServerLevel vk2 = (ServerLevel)this.mob.level;
        final BlockPos ew3 = new BlockPos(this.mob);
        final SectionPos fp4 = SectionPos.of(ew3);
        final SectionPos fp5 = BehaviorUtils.findSectionClosestToVillage(vk2, fp4, 2);
        if (fp5 != fp4) {
            final BlockPos ew4 = fp5.center();
            return RandomPos.getPosTowards(this.mob, 10, 7, new Vec3(ew4.getX(), ew4.getY(), ew4.getZ()));
        }
        return null;
    }
}
