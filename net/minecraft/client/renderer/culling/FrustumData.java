package net.minecraft.client.renderer.culling;

public class FrustumData {
    public final float[][] frustumData;
    public final float[] projectionMatrix;
    public final float[] modelViewMatrix;
    public final float[] clip;
    
    public FrustumData() {
        this.frustumData = new float[6][4];
        this.projectionMatrix = new float[16];
        this.modelViewMatrix = new float[16];
        this.clip = new float[16];
    }
    
    private double discriminant(final float[] arr, final double double2, final double double3, final double double4) {
        return arr[0] * double2 + arr[1] * double3 + arr[2] * double4 + arr[3];
    }
    
    public boolean cubeInFrustum(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        for (int integer14 = 0; integer14 < 6; ++integer14) {
            final float[] arr15 = this.frustumData[integer14];
            if (this.discriminant(arr15, double1, double2, double3) <= 0.0) {
                if (this.discriminant(arr15, double4, double2, double3) <= 0.0) {
                    if (this.discriminant(arr15, double1, double5, double3) <= 0.0) {
                        if (this.discriminant(arr15, double4, double5, double3) <= 0.0) {
                            if (this.discriminant(arr15, double1, double2, double6) <= 0.0) {
                                if (this.discriminant(arr15, double4, double2, double6) <= 0.0) {
                                    if (this.discriminant(arr15, double1, double5, double6) <= 0.0) {
                                        if (this.discriminant(arr15, double4, double5, double6) <= 0.0) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
