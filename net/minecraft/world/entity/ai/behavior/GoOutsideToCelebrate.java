package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class GoOutsideToCelebrate extends MoveToSkySeeingSpot {
    public GoOutsideToCelebrate(final float float1) {
        super(float1);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        final Raid axk4 = vk.getRaidAt(new BlockPos(aix));
        return axk4 != null && axk4.isVictory() && super.checkExtraStartConditions(vk, aix);
    }
}
