package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public class FrustumCuller implements Culler {
    private final FrustumData frustum;
    private double xOff;
    private double yOff;
    private double zOff;
    
    public FrustumCuller() {
        this(Frustum.getFrustum());
    }
    
    public FrustumCuller(final FrustumData dqh) {
        this.frustum = dqh;
    }
    
    public void prepare(final double double1, final double double2, final double double3) {
        this.xOff = double1;
        this.yOff = double2;
        this.zOff = double3;
    }
    
    public boolean cubeInFrustum(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        return this.frustum.cubeInFrustum(double1 - this.xOff, double2 - this.yOff, double3 - this.zOff, double4 - this.xOff, double5 - this.yOff, double6 - this.zOff);
    }
    
    public boolean isVisible(final AABB csc) {
        return this.cubeInFrustum(csc.minX, csc.minY, csc.minZ, csc.maxX, csc.maxY, csc.maxZ);
    }
}
