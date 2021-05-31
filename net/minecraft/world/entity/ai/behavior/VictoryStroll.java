package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.server.level.ServerLevel;

public class VictoryStroll extends VillageBoundRandomStroll {
    public VictoryStroll(final float float1) {
        super(float1);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        final Raid axk4 = vk.getRaidAt(new BlockPos(aje));
        return axk4 != null && axk4.isVictory() && super.checkExtraStartConditions(vk, aje);
    }
}
