package net.minecraft.client.renderer.block.model;

import net.minecraft.core.Vec3i;
import com.mojang.math.Vector4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.math.Vector3f;

public class FaceBakery {
    private static final float RESCALE_22_5;
    private static final float RESCALE_45;
    private static final Rotation[] BY_INDEX;
    private static final Rotation ROT_0;
    private static final Rotation ROT_90;
    private static final Rotation ROT_180;
    private static final Rotation ROT_270;
    
    public BakedQuad bakeQuad(final Vector3f b1, final Vector3f b2, final BlockElementFace dob, final TextureAtlasSprite dxb, final Direction fb, final ModelState dyv, @Nullable final BlockElementRotation doc, final boolean boolean8) {
        BlockFaceUV dod10 = dob.uv;
        if (dyv.isUvLocked()) {
            dod10 = this.recomputeUVs(dob.uv, fb, dyv.getRotation());
        }
        final float[] arr11 = new float[dod10.uvs.length];
        System.arraycopy(dod10.uvs, 0, arr11, 0, arr11.length);
        final float float12 = dxb.getWidth() / (dxb.getU1() - dxb.getU0());
        final float float13 = dxb.getHeight() / (dxb.getV1() - dxb.getV0());
        final float float14 = 4.0f / Math.max(float13, float12);
        final float float15 = (dod10.uvs[0] + dod10.uvs[0] + dod10.uvs[2] + dod10.uvs[2]) / 4.0f;
        final float float16 = (dod10.uvs[1] + dod10.uvs[1] + dod10.uvs[3] + dod10.uvs[3]) / 4.0f;
        dod10.uvs[0] = Mth.lerp(float14, dod10.uvs[0], float15);
        dod10.uvs[2] = Mth.lerp(float14, dod10.uvs[2], float15);
        dod10.uvs[1] = Mth.lerp(float14, dod10.uvs[1], float16);
        dod10.uvs[3] = Mth.lerp(float14, dod10.uvs[3], float16);
        final int[] arr12 = this.makeVertices(dod10, dxb, fb, this.setupShape(b1, b2), dyv.getRotation(), doc, boolean8);
        final Direction fb2 = calculateFacing(arr12);
        System.arraycopy(arr11, 0, dod10.uvs, 0, arr11.length);
        if (doc == null) {
            this.recalculateWinding(arr12, fb2);
        }
        return new BakedQuad(arr12, dob.tintIndex, fb2, dxb);
    }
    
    private BlockFaceUV recomputeUVs(final BlockFaceUV dod, final Direction fb, final BlockModelRotation dyq) {
        return FaceBakery.BY_INDEX[getIndex(dyq, fb)].recompute(dod);
    }
    
    private int[] makeVertices(final BlockFaceUV dod, final TextureAtlasSprite dxb, final Direction fb, final float[] arr, final BlockModelRotation dyq, @Nullable final BlockElementRotation doc, final boolean boolean7) {
        final int[] arr2 = new int[28];
        for (int integer10 = 0; integer10 < 4; ++integer10) {
            this.bakeVertex(arr2, integer10, fb, dod, arr, dxb, dyq, doc, boolean7);
        }
        return arr2;
    }
    
    private int getShadeValue(final Direction fb) {
        final float float3 = this.getShade(fb);
        final int integer4 = Mth.clamp((int)(float3 * 255.0f), 0, 255);
        return 0xFF000000 | integer4 << 16 | integer4 << 8 | integer4;
    }
    
    private float getShade(final Direction fb) {
        switch (fb) {
            case DOWN: {
                return 0.5f;
            }
            case UP: {
                return 1.0f;
            }
            case NORTH:
            case SOUTH: {
                return 0.8f;
            }
            case WEST:
            case EAST: {
                return 0.6f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    private float[] setupShape(final Vector3f b1, final Vector3f b2) {
        final float[] arr4 = new float[Direction.values().length];
        arr4[FaceInfo.Constants.MIN_X] = b1.x() / 16.0f;
        arr4[FaceInfo.Constants.MIN_Y] = b1.y() / 16.0f;
        arr4[FaceInfo.Constants.MIN_Z] = b1.z() / 16.0f;
        arr4[FaceInfo.Constants.MAX_X] = b2.x() / 16.0f;
        arr4[FaceInfo.Constants.MAX_Y] = b2.y() / 16.0f;
        arr4[FaceInfo.Constants.MAX_Z] = b2.z() / 16.0f;
        return arr4;
    }
    
    private void bakeVertex(final int[] arr, final int integer, final Direction fb, final BlockFaceUV dod, final float[] arr, final TextureAtlasSprite dxb, final BlockModelRotation dyq, @Nullable final BlockElementRotation doc, final boolean boolean9) {
        final Direction fb2 = dyq.rotate(fb);
        final int integer2 = boolean9 ? this.getShadeValue(fb2) : -1;
        final FaceInfo.VertexInfo b13 = FaceInfo.fromFacing(fb).getVertexInfo(integer);
        final Vector3f b14 = new Vector3f(arr[b13.xFace], arr[b13.yFace], arr[b13.zFace]);
        this.applyElementRotation(b14, doc);
        final int integer3 = this.applyModelRotation(b14, fb, integer, dyq);
        this.fillVertex(arr, integer3, integer, b14, integer2, dxb, dod);
    }
    
    private void fillVertex(final int[] arr, final int integer2, final int integer3, final Vector3f b, final int integer5, final TextureAtlasSprite dxb, final BlockFaceUV dod) {
        final int integer6 = integer2 * 7;
        arr[integer6] = Float.floatToRawIntBits(b.x());
        arr[integer6 + 1] = Float.floatToRawIntBits(b.y());
        arr[integer6 + 2] = Float.floatToRawIntBits(b.z());
        arr[integer6 + 3] = integer5;
        arr[integer6 + 4] = Float.floatToRawIntBits(dxb.getU(dod.getU(integer3)));
        arr[integer6 + 4 + 1] = Float.floatToRawIntBits(dxb.getV(dod.getV(integer3)));
    }
    
    private void applyElementRotation(final Vector3f b, @Nullable final BlockElementRotation doc) {
        if (doc == null) {
            return;
        }
        Vector3f b2 = null;
        Vector3f b3 = null;
        switch (doc.axis) {
            case X: {
                b2 = new Vector3f(1.0f, 0.0f, 0.0f);
                b3 = new Vector3f(0.0f, 1.0f, 1.0f);
                break;
            }
            case Y: {
                b2 = new Vector3f(0.0f, 1.0f, 0.0f);
                b3 = new Vector3f(1.0f, 0.0f, 1.0f);
                break;
            }
            case Z: {
                b2 = new Vector3f(0.0f, 0.0f, 1.0f);
                b3 = new Vector3f(1.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                throw new IllegalArgumentException("There are only 3 axes");
            }
        }
        final Quaternion a6 = new Quaternion(b2, doc.angle, true);
        if (doc.rescale) {
            if (Math.abs(doc.angle) == 22.5f) {
                b3.mul(FaceBakery.RESCALE_22_5);
            }
            else {
                b3.mul(FaceBakery.RESCALE_45);
            }
            b3.add(1.0f, 1.0f, 1.0f);
        }
        else {
            b3.set(1.0f, 1.0f, 1.0f);
        }
        this.rotateVertexBy(b, new Vector3f(doc.origin), a6, b3);
    }
    
    public int applyModelRotation(final Vector3f b, final Direction fb, final int integer, final BlockModelRotation dyq) {
        if (dyq == BlockModelRotation.X0_Y0) {
            return integer;
        }
        this.rotateVertexBy(b, new Vector3f(0.5f, 0.5f, 0.5f), dyq.getRotationQuaternion(), new Vector3f(1.0f, 1.0f, 1.0f));
        return dyq.rotateVertexIndex(fb, integer);
    }
    
    private void rotateVertexBy(final Vector3f b1, final Vector3f b2, final Quaternion a, final Vector3f b4) {
        final Vector4f library. = new Vector4f(b1.x() - b2.x(), b1.y() - b2.y(), b1.z() - b2.z(), 1.0f);
        library..transform(a);
        library..mul(b4);
        b1.set(library6.x() + b2.x(), library..y() + b2.y(), library..z() + b2.z());
    }
    
    public static Direction calculateFacing(final int[] arr) {
        final Vector3f b2 = new Vector3f(Float.intBitsToFloat(arr[0]), Float.intBitsToFloat(arr[1]), Float.intBitsToFloat(arr[2]));
        final Vector3f b3 = new Vector3f(Float.intBitsToFloat(arr[7]), Float.intBitsToFloat(arr[8]), Float.intBitsToFloat(arr[9]));
        final Vector3f b4 = new Vector3f(Float.intBitsToFloat(arr[14]), Float.intBitsToFloat(arr[15]), Float.intBitsToFloat(arr[16]));
        final Vector3f b5 = new Vector3f(b2);
        b5.sub(b3);
        final Vector3f b6 = new Vector3f(b4);
        b6.sub(b3);
        final Vector3f b7 = new Vector3f(b6);
        b7.cross(b5);
        b7.normalize();
        Direction fb8 = null;
        float float9 = 0.0f;
        for (final Direction fb9 : Direction.values()) {
            final Vec3i fs14 = fb9.getNormal();
            final Vector3f b8 = new Vector3f((float)fs14.getX(), (float)fs14.getY(), (float)fs14.getZ());
            final float float10 = b7.dot(b8);
            if (float10 >= 0.0f && float10 > float9) {
                float9 = float10;
                fb8 = fb9;
            }
        }
        if (fb8 == null) {
            return Direction.UP;
        }
        return fb8;
    }
    
    private void recalculateWinding(final int[] arr, final Direction fb) {
        final int[] arr2 = new int[arr.length];
        System.arraycopy(arr, 0, arr2, 0, arr.length);
        final float[] arr3 = new float[Direction.values().length];
        arr3[FaceInfo.Constants.MIN_X] = 999.0f;
        arr3[FaceInfo.Constants.MIN_Y] = 999.0f;
        arr3[FaceInfo.Constants.MIN_Z] = 999.0f;
        arr3[FaceInfo.Constants.MAX_X] = -999.0f;
        arr3[FaceInfo.Constants.MAX_Y] = -999.0f;
        arr3[FaceInfo.Constants.MAX_Z] = -999.0f;
        for (int integer6 = 0; integer6 < 4; ++integer6) {
            final int integer7 = 7 * integer6;
            final float float8 = Float.intBitsToFloat(arr2[integer7]);
            final float float9 = Float.intBitsToFloat(arr2[integer7 + 1]);
            final float float10 = Float.intBitsToFloat(arr2[integer7 + 2]);
            if (float8 < arr3[FaceInfo.Constants.MIN_X]) {
                arr3[FaceInfo.Constants.MIN_X] = float8;
            }
            if (float9 < arr3[FaceInfo.Constants.MIN_Y]) {
                arr3[FaceInfo.Constants.MIN_Y] = float9;
            }
            if (float10 < arr3[FaceInfo.Constants.MIN_Z]) {
                arr3[FaceInfo.Constants.MIN_Z] = float10;
            }
            if (float8 > arr3[FaceInfo.Constants.MAX_X]) {
                arr3[FaceInfo.Constants.MAX_X] = float8;
            }
            if (float9 > arr3[FaceInfo.Constants.MAX_Y]) {
                arr3[FaceInfo.Constants.MAX_Y] = float9;
            }
            if (float10 > arr3[FaceInfo.Constants.MAX_Z]) {
                arr3[FaceInfo.Constants.MAX_Z] = float10;
            }
        }
        final FaceInfo dna6 = FaceInfo.fromFacing(fb);
        for (int integer7 = 0; integer7 < 4; ++integer7) {
            final int integer8 = 7 * integer7;
            final FaceInfo.VertexInfo b9 = dna6.getVertexInfo(integer7);
            final float float10 = arr3[b9.xFace];
            final float float11 = arr3[b9.yFace];
            final float float12 = arr3[b9.zFace];
            arr[integer8] = Float.floatToRawIntBits(float10);
            arr[integer8 + 1] = Float.floatToRawIntBits(float11);
            arr[integer8 + 2] = Float.floatToRawIntBits(float12);
            for (int integer9 = 0; integer9 < 4; ++integer9) {
                final int integer10 = 7 * integer9;
                final float float13 = Float.intBitsToFloat(arr2[integer10]);
                final float float14 = Float.intBitsToFloat(arr2[integer10 + 1]);
                final float float15 = Float.intBitsToFloat(arr2[integer10 + 2]);
                if (Mth.equal(float10, float13) && Mth.equal(float11, float14) && Mth.equal(float12, float15)) {
                    arr[integer8 + 4] = arr2[integer10 + 4];
                    arr[integer8 + 4 + 1] = arr2[integer10 + 4 + 1];
                }
            }
        }
    }
    
    private static void register(final BlockModelRotation dyq, final Direction fb, final Rotation a) {
        FaceBakery.BY_INDEX[getIndex(dyq, fb)] = a;
    }
    
    private static int getIndex(final BlockModelRotation dyq, final Direction fb) {
        return BlockModelRotation.values().length * fb.ordinal() + dyq.ordinal();
    }
    
    static {
        RESCALE_22_5 = 1.0f / (float)Math.cos(0.39269909262657166) - 1.0f;
        RESCALE_45 = 1.0f / (float)Math.cos(0.7853981852531433) - 1.0f;
        BY_INDEX = new Rotation[BlockModelRotation.values().length * Direction.values().length];
        ROT_0 = new Rotation() {
            @Override
            BlockFaceUV apply(final float float1, final float float2, final float float3, final float float4) {
                return new BlockFaceUV(new float[] { float1, float2, float3, float4 }, 0);
            }
        };
        ROT_90 = new Rotation() {
            @Override
            BlockFaceUV apply(final float float1, final float float2, final float float3, final float float4) {
                return new BlockFaceUV(new float[] { float4, 16.0f - float1, float2, 16.0f - float3 }, 270);
            }
        };
        ROT_180 = new Rotation() {
            @Override
            BlockFaceUV apply(final float float1, final float float2, final float float3, final float float4) {
                return new BlockFaceUV(new float[] { 16.0f - float1, 16.0f - float2, 16.0f - float3, 16.0f - float4 }, 0);
            }
        };
        ROT_270 = new Rotation() {
            @Override
            BlockFaceUV apply(final float float1, final float float2, final float float3, final float float4) {
                return new BlockFaceUV(new float[] { 16.0f - float2, float3, 16.0f - float4, float1 }, 90);
            }
        };
        register(BlockModelRotation.X0_Y0, Direction.DOWN, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y0, Direction.EAST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y0, Direction.NORTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y0, Direction.SOUTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y0, Direction.UP, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y0, Direction.WEST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y90, Direction.EAST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y90, Direction.NORTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y90, Direction.SOUTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y90, Direction.WEST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y180, Direction.EAST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y180, Direction.NORTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y180, Direction.SOUTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y180, Direction.WEST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y270, Direction.EAST, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y270, Direction.NORTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y270, Direction.SOUTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y270, Direction.WEST, FaceBakery.ROT_0);
        register(BlockModelRotation.X90_Y0, Direction.DOWN, FaceBakery.ROT_0);
        register(BlockModelRotation.X90_Y0, Direction.SOUTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X90_Y90, Direction.DOWN, FaceBakery.ROT_0);
        register(BlockModelRotation.X90_Y180, Direction.DOWN, FaceBakery.ROT_0);
        register(BlockModelRotation.X90_Y180, Direction.NORTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X90_Y270, Direction.DOWN, FaceBakery.ROT_0);
        register(BlockModelRotation.X180_Y0, Direction.DOWN, FaceBakery.ROT_0);
        register(BlockModelRotation.X180_Y0, Direction.UP, FaceBakery.ROT_0);
        register(BlockModelRotation.X270_Y0, Direction.SOUTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X270_Y0, Direction.UP, FaceBakery.ROT_0);
        register(BlockModelRotation.X270_Y90, Direction.UP, FaceBakery.ROT_0);
        register(BlockModelRotation.X270_Y180, Direction.NORTH, FaceBakery.ROT_0);
        register(BlockModelRotation.X270_Y180, Direction.UP, FaceBakery.ROT_0);
        register(BlockModelRotation.X270_Y270, Direction.UP, FaceBakery.ROT_0);
        register(BlockModelRotation.X0_Y270, Direction.UP, FaceBakery.ROT_90);
        register(BlockModelRotation.X0_Y90, Direction.DOWN, FaceBakery.ROT_90);
        register(BlockModelRotation.X90_Y0, Direction.WEST, FaceBakery.ROT_90);
        register(BlockModelRotation.X90_Y90, Direction.WEST, FaceBakery.ROT_90);
        register(BlockModelRotation.X90_Y180, Direction.WEST, FaceBakery.ROT_90);
        register(BlockModelRotation.X90_Y270, Direction.NORTH, FaceBakery.ROT_90);
        register(BlockModelRotation.X90_Y270, Direction.SOUTH, FaceBakery.ROT_90);
        register(BlockModelRotation.X90_Y270, Direction.WEST, FaceBakery.ROT_90);
        register(BlockModelRotation.X180_Y90, Direction.UP, FaceBakery.ROT_90);
        register(BlockModelRotation.X180_Y270, Direction.DOWN, FaceBakery.ROT_90);
        register(BlockModelRotation.X270_Y0, Direction.EAST, FaceBakery.ROT_90);
        register(BlockModelRotation.X270_Y90, Direction.EAST, FaceBakery.ROT_90);
        register(BlockModelRotation.X270_Y90, Direction.NORTH, FaceBakery.ROT_90);
        register(BlockModelRotation.X270_Y90, Direction.SOUTH, FaceBakery.ROT_90);
        register(BlockModelRotation.X270_Y180, Direction.EAST, FaceBakery.ROT_90);
        register(BlockModelRotation.X270_Y270, Direction.EAST, FaceBakery.ROT_90);
        register(BlockModelRotation.X0_Y180, Direction.DOWN, FaceBakery.ROT_180);
        register(BlockModelRotation.X0_Y180, Direction.UP, FaceBakery.ROT_180);
        register(BlockModelRotation.X90_Y0, Direction.NORTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X90_Y0, Direction.UP, FaceBakery.ROT_180);
        register(BlockModelRotation.X90_Y90, Direction.UP, FaceBakery.ROT_180);
        register(BlockModelRotation.X90_Y180, Direction.SOUTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X90_Y180, Direction.UP, FaceBakery.ROT_180);
        register(BlockModelRotation.X90_Y270, Direction.UP, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y0, Direction.EAST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y0, Direction.NORTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y0, Direction.SOUTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y0, Direction.WEST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y90, Direction.EAST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y90, Direction.NORTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y90, Direction.SOUTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y90, Direction.WEST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y180, Direction.DOWN, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y180, Direction.EAST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y180, Direction.NORTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y180, Direction.SOUTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y180, Direction.UP, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y180, Direction.WEST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y270, Direction.EAST, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y270, Direction.NORTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y270, Direction.SOUTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X180_Y270, Direction.WEST, FaceBakery.ROT_180);
        register(BlockModelRotation.X270_Y0, Direction.DOWN, FaceBakery.ROT_180);
        register(BlockModelRotation.X270_Y0, Direction.NORTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X270_Y90, Direction.DOWN, FaceBakery.ROT_180);
        register(BlockModelRotation.X270_Y180, Direction.DOWN, FaceBakery.ROT_180);
        register(BlockModelRotation.X270_Y180, Direction.SOUTH, FaceBakery.ROT_180);
        register(BlockModelRotation.X270_Y270, Direction.DOWN, FaceBakery.ROT_180);
        register(BlockModelRotation.X0_Y90, Direction.UP, FaceBakery.ROT_270);
        register(BlockModelRotation.X0_Y270, Direction.DOWN, FaceBakery.ROT_270);
        register(BlockModelRotation.X90_Y0, Direction.EAST, FaceBakery.ROT_270);
        register(BlockModelRotation.X90_Y90, Direction.EAST, FaceBakery.ROT_270);
        register(BlockModelRotation.X90_Y90, Direction.NORTH, FaceBakery.ROT_270);
        register(BlockModelRotation.X90_Y90, Direction.SOUTH, FaceBakery.ROT_270);
        register(BlockModelRotation.X90_Y180, Direction.EAST, FaceBakery.ROT_270);
        register(BlockModelRotation.X90_Y270, Direction.EAST, FaceBakery.ROT_270);
        register(BlockModelRotation.X270_Y0, Direction.WEST, FaceBakery.ROT_270);
        register(BlockModelRotation.X180_Y90, Direction.DOWN, FaceBakery.ROT_270);
        register(BlockModelRotation.X180_Y270, Direction.UP, FaceBakery.ROT_270);
        register(BlockModelRotation.X270_Y90, Direction.WEST, FaceBakery.ROT_270);
        register(BlockModelRotation.X270_Y180, Direction.WEST, FaceBakery.ROT_270);
        register(BlockModelRotation.X270_Y270, Direction.NORTH, FaceBakery.ROT_270);
        register(BlockModelRotation.X270_Y270, Direction.SOUTH, FaceBakery.ROT_270);
        register(BlockModelRotation.X270_Y270, Direction.WEST, FaceBakery.ROT_270);
    }
    
    abstract static class Rotation {
        private Rotation() {
        }
        
        public BlockFaceUV recompute(final BlockFaceUV dod) {
            final float float3 = dod.getU(dod.getReverseIndex(0));
            final float float4 = dod.getV(dod.getReverseIndex(0));
            final float float5 = dod.getU(dod.getReverseIndex(2));
            final float float6 = dod.getV(dod.getReverseIndex(2));
            return this.apply(float3, float4, float5, float6);
        }
        
        abstract BlockFaceUV apply(final float float1, final float float2, final float float3, final float float4);
    }
}
