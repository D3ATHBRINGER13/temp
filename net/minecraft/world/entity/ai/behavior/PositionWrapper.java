package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

public interface PositionWrapper {
    BlockPos getPos();
    
    Vec3 getLookAtPos();
    
    boolean isVisible(final LivingEntity aix);
}
