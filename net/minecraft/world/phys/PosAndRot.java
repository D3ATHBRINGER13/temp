package net.minecraft.world.phys;

import java.util.Objects;

public class PosAndRot {
    private final Vec3 pos;
    private final float xRot;
    private final float yRot;
    
    public PosAndRot(final Vec3 csi, final float float2, final float float3) {
        this.pos = csi;
        this.xRot = float2;
        this.yRot = float3;
    }
    
    public Vec3 pos() {
        return this.pos;
    }
    
    public float xRot() {
        return this.xRot;
    }
    
    public float yRot() {
        return this.yRot;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final PosAndRot csg3 = (PosAndRot)object;
        return Float.compare(csg3.xRot, this.xRot) == 0 && Float.compare(csg3.yRot, this.yRot) == 0 && Objects.equals(this.pos, csg3.pos);
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.pos, this.xRot, this.yRot });
    }
    
    public String toString() {
        return new StringBuilder().append("PosAndRot[").append(this.pos).append(" (").append(this.xRot).append(", ").append(this.yRot).append(")]").toString();
    }
}
