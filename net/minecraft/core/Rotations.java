package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class Rotations {
    protected final float x;
    protected final float y;
    protected final float z;
    
    public Rotations(final float float1, final float float2, final float float3) {
        this.x = ((Float.isInfinite(float1) || Float.isNaN(float1)) ? 0.0f : (float1 % 360.0f));
        this.y = ((Float.isInfinite(float2) || Float.isNaN(float2)) ? 0.0f : (float2 % 360.0f));
        this.z = ((Float.isInfinite(float3) || Float.isNaN(float3)) ? 0.0f : (float3 % 360.0f));
    }
    
    public Rotations(final ListTag ik) {
        this(ik.getFloat(0), ik.getFloat(1), ik.getFloat(2));
    }
    
    public ListTag save() {
        final ListTag ik2 = new ListTag();
        ik2.add(new FloatTag(this.x));
        ik2.add(new FloatTag(this.y));
        ik2.add(new FloatTag(this.z));
        return ik2;
    }
    
    public boolean equals(final Object object) {
        if (!(object instanceof Rotations)) {
            return false;
        }
        final Rotations fo3 = (Rotations)object;
        return this.x == fo3.x && this.y == fo3.y && this.z == fo3.z;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }
}
