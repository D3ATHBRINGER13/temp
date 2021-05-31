package net.minecraft.client.resources.model;

import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import com.mojang.math.Quaternion;
import java.util.Map;

public enum BlockModelRotation implements ModelState {
    X0_Y0(0, 0), 
    X0_Y90(0, 90), 
    X0_Y180(0, 180), 
    X0_Y270(0, 270), 
    X90_Y0(90, 0), 
    X90_Y90(90, 90), 
    X90_Y180(90, 180), 
    X90_Y270(90, 270), 
    X180_Y0(180, 0), 
    X180_Y90(180, 90), 
    X180_Y180(180, 180), 
    X180_Y270(180, 270), 
    X270_Y0(270, 0), 
    X270_Y90(270, 90), 
    X270_Y180(270, 180), 
    X270_Y270(270, 270);
    
    private static final Map<Integer, BlockModelRotation> BY_INDEX;
    private final int index;
    private final Quaternion rotation;
    private final int xSteps;
    private final int ySteps;
    
    private static int getIndex(final int integer1, final int integer2) {
        return integer1 * 360 + integer2;
    }
    
    private BlockModelRotation(final int integer3, final int integer4) {
        this.index = getIndex(integer3, integer4);
        final Quaternion a6 = new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), (float)(-integer4), true);
        a6.mul(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), (float)(-integer3), true));
        this.rotation = a6;
        this.xSteps = Mth.abs(integer3 / 90);
        this.ySteps = Mth.abs(integer4 / 90);
    }
    
    public BlockModelRotation getRotation() {
        return this;
    }
    
    public Quaternion getRotationQuaternion() {
        return this.rotation;
    }
    
    public Direction rotate(final Direction fb) {
        Direction fb2 = fb;
        for (int integer4 = 0; integer4 < this.xSteps; ++integer4) {
            fb2 = fb2.getClockWise(Direction.Axis.X);
        }
        if (fb2.getAxis() != Direction.Axis.Y) {
            for (int integer4 = 0; integer4 < this.ySteps; ++integer4) {
                fb2 = fb2.getClockWise(Direction.Axis.Y);
            }
        }
        return fb2;
    }
    
    public int rotateVertexIndex(final Direction fb, final int integer) {
        int integer2 = integer;
        if (fb.getAxis() == Direction.Axis.X) {
            integer2 = (integer2 + this.xSteps) % 4;
        }
        Direction fb2 = fb;
        for (int integer3 = 0; integer3 < this.xSteps; ++integer3) {
            fb2 = fb2.getClockWise(Direction.Axis.X);
        }
        if (fb2.getAxis() == Direction.Axis.Y) {
            integer2 = (integer2 + this.ySteps) % 4;
        }
        return integer2;
    }
    
    public static BlockModelRotation by(final int integer1, final int integer2) {
        return (BlockModelRotation)BlockModelRotation.BY_INDEX.get(getIndex(Mth.positiveModulo(integer1, 360), Mth.positiveModulo(integer2, 360)));
    }
    
    static {
        BY_INDEX = (Map)Arrays.stream((Object[])values()).sorted(Comparator.comparingInt(dyq -> dyq.index)).collect(Collectors.toMap(dyq -> dyq.index, dyq -> dyq));
    }
}
