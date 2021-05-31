package net.minecraft.commands.arguments.coordinates;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.CommandSourceStack;

public interface Coordinates {
    Vec3 getPosition(final CommandSourceStack cd);
    
    Vec2 getRotation(final CommandSourceStack cd);
    
    default BlockPos getBlockPos(final CommandSourceStack cd) {
        return new BlockPos(this.getPosition(cd));
    }
    
    boolean isXRelative();
    
    boolean isYRelative();
    
    boolean isZRelative();
}
