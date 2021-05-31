package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class LocateHidingPlaceDuringRaid extends LocateHidingPlace {
    public LocateHidingPlaceDuringRaid(final int integer, final float float2) {
        super(integer, float2, 1);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        final Raid axk4 = vk.getRaidAt(new BlockPos(aix));
        return super.checkExtraStartConditions(vk, aix) && axk4 != null && axk4.isActive() && !axk4.isVictory() && !axk4.isLoss();
    }
}
