package net.minecraft.world.level.block;

import java.util.Collections;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.core.Direction;

public enum Rotation {
    NONE, 
    CLOCKWISE_90, 
    CLOCKWISE_180, 
    COUNTERCLOCKWISE_90;
    
    public Rotation getRotated(final Rotation brg) {
        Label_0148: {
            switch (brg) {
                case CLOCKWISE_180: {
                    switch (this) {
                        case NONE: {
                            return Rotation.CLOCKWISE_180;
                        }
                        case CLOCKWISE_90: {
                            return Rotation.COUNTERCLOCKWISE_90;
                        }
                        case CLOCKWISE_180: {
                            return Rotation.NONE;
                        }
                        case COUNTERCLOCKWISE_90: {
                            return Rotation.CLOCKWISE_90;
                        }
                        default: {
                            break Label_0148;
                        }
                    }
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    switch (this) {
                        case NONE: {
                            return Rotation.COUNTERCLOCKWISE_90;
                        }
                        case CLOCKWISE_90: {
                            return Rotation.NONE;
                        }
                        case CLOCKWISE_180: {
                            return Rotation.CLOCKWISE_90;
                        }
                        case COUNTERCLOCKWISE_90: {
                            return Rotation.CLOCKWISE_180;
                        }
                        default: {
                            break Label_0148;
                        }
                    }
                    break;
                }
                case CLOCKWISE_90: {
                    switch (this) {
                        case NONE: {
                            return Rotation.CLOCKWISE_90;
                        }
                        case CLOCKWISE_90: {
                            return Rotation.CLOCKWISE_180;
                        }
                        case CLOCKWISE_180: {
                            return Rotation.COUNTERCLOCKWISE_90;
                        }
                        case COUNTERCLOCKWISE_90: {
                            return Rotation.NONE;
                        }
                        default: {
                            break Label_0148;
                        }
                    }
                    break;
                }
            }
        }
        return this;
    }
    
    public Direction rotate(final Direction fb) {
        if (fb.getAxis() == Direction.Axis.Y) {
            return fb;
        }
        switch (this) {
            case CLOCKWISE_180: {
                return fb.getOpposite();
            }
            case COUNTERCLOCKWISE_90: {
                return fb.getCounterClockWise();
            }
            case CLOCKWISE_90: {
                return fb.getClockWise();
            }
            default: {
                return fb;
            }
        }
    }
    
    public int rotate(final int integer1, final int integer2) {
        switch (this) {
            case CLOCKWISE_180: {
                return (integer1 + integer2 / 2) % integer2;
            }
            case COUNTERCLOCKWISE_90: {
                return (integer1 + integer2 * 3 / 4) % integer2;
            }
            case CLOCKWISE_90: {
                return (integer1 + integer2 / 4) % integer2;
            }
            default: {
                return integer1;
            }
        }
    }
    
    public static Rotation getRandom(final Random random) {
        final Rotation[] arr2 = values();
        return arr2[random.nextInt(arr2.length)];
    }
    
    public static List<Rotation> getShuffled(final Random random) {
        final List<Rotation> list2 = (List<Rotation>)Lists.newArrayList((Object[])values());
        Collections.shuffle((List)list2, random);
        return list2;
    }
}
