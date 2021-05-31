package net.minecraft.client.renderer.culling;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;

public class Frustum extends FrustumData {
    private static final Frustum FRUSTUM;
    private final FloatBuffer _proj;
    private final FloatBuffer _modl;
    private final FloatBuffer _clip;
    
    public Frustum() {
        this._proj = MemoryTracker.createFloatBuffer(16);
        this._modl = MemoryTracker.createFloatBuffer(16);
        this._clip = MemoryTracker.createFloatBuffer(16);
    }
    
    public static FrustumData getFrustum() {
        Frustum.FRUSTUM.calculateFrustum();
        return Frustum.FRUSTUM;
    }
    
    private void normalizePlane(final float[] arr) {
        final float float3 = Mth.sqrt(arr[0] * arr[0] + arr[1] * arr[1] + arr[2] * arr[2]);
        final int n = 0;
        arr[n] /= float3;
        final int n2 = 1;
        arr[n2] /= float3;
        final int n3 = 2;
        arr[n3] /= float3;
        final int n4 = 3;
        arr[n4] /= float3;
    }
    
    public void calculateFrustum() {
        this._proj.clear();
        this._modl.clear();
        this._clip.clear();
        GlStateManager.getMatrix(2983, this._proj);
        GlStateManager.getMatrix(2982, this._modl);
        final float[] arr2 = this.projectionMatrix;
        final float[] arr3 = this.modelViewMatrix;
        this._proj.flip().limit(16);
        this._proj.get(arr2);
        this._modl.flip().limit(16);
        this._modl.get(arr3);
        this.clip[0] = arr3[0] * arr2[0] + arr3[1] * arr2[4] + arr3[2] * arr2[8] + arr3[3] * arr2[12];
        this.clip[1] = arr3[0] * arr2[1] + arr3[1] * arr2[5] + arr3[2] * arr2[9] + arr3[3] * arr2[13];
        this.clip[2] = arr3[0] * arr2[2] + arr3[1] * arr2[6] + arr3[2] * arr2[10] + arr3[3] * arr2[14];
        this.clip[3] = arr3[0] * arr2[3] + arr3[1] * arr2[7] + arr3[2] * arr2[11] + arr3[3] * arr2[15];
        this.clip[4] = arr3[4] * arr2[0] + arr3[5] * arr2[4] + arr3[6] * arr2[8] + arr3[7] * arr2[12];
        this.clip[5] = arr3[4] * arr2[1] + arr3[5] * arr2[5] + arr3[6] * arr2[9] + arr3[7] * arr2[13];
        this.clip[6] = arr3[4] * arr2[2] + arr3[5] * arr2[6] + arr3[6] * arr2[10] + arr3[7] * arr2[14];
        this.clip[7] = arr3[4] * arr2[3] + arr3[5] * arr2[7] + arr3[6] * arr2[11] + arr3[7] * arr2[15];
        this.clip[8] = arr3[8] * arr2[0] + arr3[9] * arr2[4] + arr3[10] * arr2[8] + arr3[11] * arr2[12];
        this.clip[9] = arr3[8] * arr2[1] + arr3[9] * arr2[5] + arr3[10] * arr2[9] + arr3[11] * arr2[13];
        this.clip[10] = arr3[8] * arr2[2] + arr3[9] * arr2[6] + arr3[10] * arr2[10] + arr3[11] * arr2[14];
        this.clip[11] = arr3[8] * arr2[3] + arr3[9] * arr2[7] + arr3[10] * arr2[11] + arr3[11] * arr2[15];
        this.clip[12] = arr3[12] * arr2[0] + arr3[13] * arr2[4] + arr3[14] * arr2[8] + arr3[15] * arr2[12];
        this.clip[13] = arr3[12] * arr2[1] + arr3[13] * arr2[5] + arr3[14] * arr2[9] + arr3[15] * arr2[13];
        this.clip[14] = arr3[12] * arr2[2] + arr3[13] * arr2[6] + arr3[14] * arr2[10] + arr3[15] * arr2[14];
        this.clip[15] = arr3[12] * arr2[3] + arr3[13] * arr2[7] + arr3[14] * arr2[11] + arr3[15] * arr2[15];
        final float[] arr4 = this.frustumData[0];
        arr4[0] = this.clip[3] - this.clip[0];
        arr4[1] = this.clip[7] - this.clip[4];
        arr4[2] = this.clip[11] - this.clip[8];
        arr4[3] = this.clip[15] - this.clip[12];
        this.normalizePlane(arr4);
        final float[] arr5 = this.frustumData[1];
        arr5[0] = this.clip[3] + this.clip[0];
        arr5[1] = this.clip[7] + this.clip[4];
        arr5[2] = this.clip[11] + this.clip[8];
        arr5[3] = this.clip[15] + this.clip[12];
        this.normalizePlane(arr5);
        final float[] arr6 = this.frustumData[2];
        arr6[0] = this.clip[3] + this.clip[1];
        arr6[1] = this.clip[7] + this.clip[5];
        arr6[2] = this.clip[11] + this.clip[9];
        arr6[3] = this.clip[15] + this.clip[13];
        this.normalizePlane(arr6);
        final float[] arr7 = this.frustumData[3];
        arr7[0] = this.clip[3] - this.clip[1];
        arr7[1] = this.clip[7] - this.clip[5];
        arr7[2] = this.clip[11] - this.clip[9];
        arr7[3] = this.clip[15] - this.clip[13];
        this.normalizePlane(arr7);
        final float[] arr8 = this.frustumData[4];
        arr8[0] = this.clip[3] - this.clip[2];
        arr8[1] = this.clip[7] - this.clip[6];
        arr8[2] = this.clip[11] - this.clip[10];
        arr8[3] = this.clip[15] - this.clip[14];
        this.normalizePlane(arr8);
        final float[] arr9 = this.frustumData[5];
        arr9[0] = this.clip[3] + this.clip[2];
        arr9[1] = this.clip[7] + this.clip[6];
        arr9[2] = this.clip[11] + this.clip[10];
        arr9[3] = this.clip[15] + this.clip[14];
        this.normalizePlane(arr9);
    }
    
    static {
        FRUSTUM = new Frustum();
    }
}
