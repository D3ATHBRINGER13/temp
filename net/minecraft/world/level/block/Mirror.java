package net.minecraft.world.level.block;

import net.minecraft.core.Direction;

public enum Mirror {
    NONE, 
    LEFT_RIGHT, 
    FRONT_BACK;
    
    public int mirror(final int integer1, final int integer2) {
        final int integer3 = integer2 / 2;
        final int integer4 = (integer1 > integer3) ? (integer1 - integer2) : integer1;
        switch (this) {
            case FRONT_BACK: {
                return (integer2 - integer4) % integer2;
            }
            case LEFT_RIGHT: {
                return (integer3 - integer4 + integer2) % integer2;
            }
            default: {
                return integer1;
            }
        }
    }
    
    public Rotation getRotation(final Direction fb) {
        final Direction.Axis a3 = fb.getAxis();
        return ((this == Mirror.LEFT_RIGHT && a3 == Direction.Axis.Z) || (this == Mirror.FRONT_BACK && a3 == Direction.Axis.X)) ? Rotation.CLOCKWISE_180 : Rotation.NONE;
    }
    
    public Direction mirror(final Direction fb) {
        if (this == Mirror.FRONT_BACK && fb.getAxis() == Direction.Axis.X) {
            return fb.getOpposite();
        }
        if (this == Mirror.LEFT_RIGHT && fb.getAxis() == Direction.Axis.Z) {
            return fb.getOpposite();
        }
        return fb;
    }
}
