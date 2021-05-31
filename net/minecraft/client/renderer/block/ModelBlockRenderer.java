package net.minecraft.client.renderer.block;

import java.util.function.Supplier;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import java.util.BitSet;
import net.minecraft.core.Direction;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import java.util.Random;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.client.color.block.BlockColors;

public class ModelBlockRenderer {
    private final BlockColors blockColors;
    private static final ThreadLocal<Cache> CACHE;
    
    public ModelBlockRenderer(final BlockColors cyp) {
        this.blockColors = cyp;
    }
    
    public boolean tesselateBlock(final BlockAndBiomeGetter bgz, final BakedModel dyp, final BlockState bvt, final BlockPos ew, final BufferBuilder cuw, final boolean boolean6, final Random random, final long long8) {
        final boolean boolean7 = Minecraft.useAmbientOcclusion() && bvt.getLightEmission() == 0 && dyp.useAmbientOcclusion();
        try {
            if (boolean7) {
                return this.tesselateWithAO(bgz, dyp, bvt, ew, cuw, boolean6, random, long8);
            }
            return this.tesselateWithoutAO(bgz, dyp, bvt, ew, cuw, boolean6, random, long8);
        }
        catch (Throwable throwable12) {
            final CrashReport d13 = CrashReport.forThrowable(throwable12, "Tesselating block model");
            final CrashReportCategory e14 = d13.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(e14, ew, bvt);
            e14.setDetail("Using AO", boolean7);
            throw new ReportedException(d13);
        }
    }
    
    public boolean tesselateWithAO(final BlockAndBiomeGetter bgz, final BakedModel dyp, final BlockState bvt, final BlockPos ew, final BufferBuilder cuw, final boolean boolean6, final Random random, final long long8) {
        boolean boolean7 = false;
        final float[] arr12 = new float[Direction.values().length * 2];
        final BitSet bitSet13 = new BitSet(3);
        final AmbientOcclusionFace b14 = new AmbientOcclusionFace();
        for (final Direction fb18 : Direction.values()) {
            random.setSeed(long8);
            final List<BakedQuad> list19 = dyp.getQuads(bvt, fb18, random);
            if (!list19.isEmpty()) {
                if (!boolean6 || Block.shouldRenderFace(bvt, bgz, ew, fb18)) {
                    this.renderModelFaceAO(bgz, bvt, ew, cuw, list19, arr12, bitSet13, b14);
                    boolean7 = true;
                }
            }
        }
        random.setSeed(long8);
        final List<BakedQuad> list20 = dyp.getQuads(bvt, null, random);
        if (!list20.isEmpty()) {
            this.renderModelFaceAO(bgz, bvt, ew, cuw, list20, arr12, bitSet13, b14);
            boolean7 = true;
        }
        return boolean7;
    }
    
    public boolean tesselateWithoutAO(final BlockAndBiomeGetter bgz, final BakedModel dyp, final BlockState bvt, final BlockPos ew, final BufferBuilder cuw, final boolean boolean6, final Random random, final long long8) {
        boolean boolean7 = false;
        final BitSet bitSet12 = new BitSet(3);
        for (final Direction fb16 : Direction.values()) {
            random.setSeed(long8);
            final List<BakedQuad> list17 = dyp.getQuads(bvt, fb16, random);
            if (!list17.isEmpty()) {
                if (!boolean6 || Block.shouldRenderFace(bvt, bgz, ew, fb16)) {
                    final int integer18 = bvt.getLightColor(bgz, ew.relative(fb16));
                    this.renderModelFaceFlat(bgz, bvt, ew, integer18, false, cuw, list17, bitSet12);
                    boolean7 = true;
                }
            }
        }
        random.setSeed(long8);
        final List<BakedQuad> list18 = dyp.getQuads(bvt, null, random);
        if (!list18.isEmpty()) {
            this.renderModelFaceFlat(bgz, bvt, ew, -1, true, cuw, list18, bitSet12);
            boolean7 = true;
        }
        return boolean7;
    }
    
    private void renderModelFaceAO(final BlockAndBiomeGetter bgz, final BlockState bvt, final BlockPos ew, final BufferBuilder cuw, final List<BakedQuad> list, final float[] arr, final BitSet bitSet, final AmbientOcclusionFace b) {
        final Vec3 csi10 = bvt.getOffset(bgz, ew);
        final double double11 = ew.getX() + csi10.x;
        final double double12 = ew.getY() + csi10.y;
        final double double13 = ew.getZ() + csi10.z;
        for (int integer17 = 0, integer18 = list.size(); integer17 < integer18; ++integer17) {
            final BakedQuad dnz19 = (BakedQuad)list.get(integer17);
            this.calculateShape(bgz, bvt, ew, dnz19.getVertices(), dnz19.getDirection(), arr, bitSet);
            b.calculate(bgz, bvt, ew, dnz19.getDirection(), arr, bitSet);
            cuw.putBulkData(dnz19.getVertices());
            cuw.faceTex2(b.lightmap[0], b.lightmap[1], b.lightmap[2], b.lightmap[3]);
            if (dnz19.isTinted()) {
                final int integer19 = this.blockColors.getColor(bvt, bgz, ew, dnz19.getTintIndex());
                final float float21 = (integer19 >> 16 & 0xFF) / 255.0f;
                final float float22 = (integer19 >> 8 & 0xFF) / 255.0f;
                final float float23 = (integer19 & 0xFF) / 255.0f;
                cuw.faceTint(b.brightness[0] * float21, b.brightness[0] * float22, b.brightness[0] * float23, 4);
                cuw.faceTint(b.brightness[1] * float21, b.brightness[1] * float22, b.brightness[1] * float23, 3);
                cuw.faceTint(b.brightness[2] * float21, b.brightness[2] * float22, b.brightness[2] * float23, 2);
                cuw.faceTint(b.brightness[3] * float21, b.brightness[3] * float22, b.brightness[3] * float23, 1);
            }
            else {
                cuw.faceTint(b.brightness[0], b.brightness[0], b.brightness[0], 4);
                cuw.faceTint(b.brightness[1], b.brightness[1], b.brightness[1], 3);
                cuw.faceTint(b.brightness[2], b.brightness[2], b.brightness[2], 2);
                cuw.faceTint(b.brightness[3], b.brightness[3], b.brightness[3], 1);
            }
            cuw.postProcessFacePosition(double11, double12, double13);
        }
    }
    
    private void calculateShape(final BlockAndBiomeGetter bgz, final BlockState bvt, final BlockPos ew, final int[] arr, final Direction fb, @Nullable final float[] arr, final BitSet bitSet) {
        float float9 = 32.0f;
        float float10 = 32.0f;
        float float11 = 32.0f;
        float float12 = -32.0f;
        float float13 = -32.0f;
        float float14 = -32.0f;
        for (int integer15 = 0; integer15 < 4; ++integer15) {
            final float float15 = Float.intBitsToFloat(arr[integer15 * 7]);
            final float float16 = Float.intBitsToFloat(arr[integer15 * 7 + 1]);
            final float float17 = Float.intBitsToFloat(arr[integer15 * 7 + 2]);
            float9 = Math.min(float9, float15);
            float10 = Math.min(float10, float16);
            float11 = Math.min(float11, float17);
            float12 = Math.max(float12, float15);
            float13 = Math.max(float13, float16);
            float14 = Math.max(float14, float17);
        }
        if (arr != null) {
            arr[Direction.WEST.get3DDataValue()] = float9;
            arr[Direction.EAST.get3DDataValue()] = float12;
            arr[Direction.DOWN.get3DDataValue()] = float10;
            arr[Direction.UP.get3DDataValue()] = float13;
            arr[Direction.NORTH.get3DDataValue()] = float11;
            arr[Direction.SOUTH.get3DDataValue()] = float14;
            final int integer15 = Direction.values().length;
            arr[Direction.WEST.get3DDataValue() + integer15] = 1.0f - float9;
            arr[Direction.EAST.get3DDataValue() + integer15] = 1.0f - float12;
            arr[Direction.DOWN.get3DDataValue() + integer15] = 1.0f - float10;
            arr[Direction.UP.get3DDataValue() + integer15] = 1.0f - float13;
            arr[Direction.NORTH.get3DDataValue() + integer15] = 1.0f - float11;
            arr[Direction.SOUTH.get3DDataValue() + integer15] = 1.0f - float14;
        }
        final float float18 = 1.0E-4f;
        final float float15 = 0.9999f;
        switch (fb) {
            case DOWN: {
                bitSet.set(1, float9 >= 1.0E-4f || float11 >= 1.0E-4f || float12 <= 0.9999f || float14 <= 0.9999f);
                bitSet.set(0, float10 == float13 && (float10 < 1.0E-4f || bvt.isCollisionShapeFullBlock(bgz, ew)));
                break;
            }
            case UP: {
                bitSet.set(1, float9 >= 1.0E-4f || float11 >= 1.0E-4f || float12 <= 0.9999f || float14 <= 0.9999f);
                bitSet.set(0, float10 == float13 && (float13 > 0.9999f || bvt.isCollisionShapeFullBlock(bgz, ew)));
                break;
            }
            case NORTH: {
                bitSet.set(1, float9 >= 1.0E-4f || float10 >= 1.0E-4f || float12 <= 0.9999f || float13 <= 0.9999f);
                bitSet.set(0, float11 == float14 && (float11 < 1.0E-4f || bvt.isCollisionShapeFullBlock(bgz, ew)));
                break;
            }
            case SOUTH: {
                bitSet.set(1, float9 >= 1.0E-4f || float10 >= 1.0E-4f || float12 <= 0.9999f || float13 <= 0.9999f);
                bitSet.set(0, float11 == float14 && (float14 > 0.9999f || bvt.isCollisionShapeFullBlock(bgz, ew)));
                break;
            }
            case WEST: {
                bitSet.set(1, float10 >= 1.0E-4f || float11 >= 1.0E-4f || float13 <= 0.9999f || float14 <= 0.9999f);
                bitSet.set(0, float9 == float12 && (float9 < 1.0E-4f || bvt.isCollisionShapeFullBlock(bgz, ew)));
                break;
            }
            case EAST: {
                bitSet.set(1, float10 >= 1.0E-4f || float11 >= 1.0E-4f || float13 <= 0.9999f || float14 <= 0.9999f);
                bitSet.set(0, float9 == float12 && (float12 > 0.9999f || bvt.isCollisionShapeFullBlock(bgz, ew)));
                break;
            }
        }
    }
    
    private void renderModelFaceFlat(final BlockAndBiomeGetter bgz, final BlockState bvt, final BlockPos ew, int integer, final boolean boolean5, final BufferBuilder cuw, final List<BakedQuad> list, final BitSet bitSet) {
        final Vec3 csi10 = bvt.getOffset(bgz, ew);
        final double double11 = ew.getX() + csi10.x;
        final double double12 = ew.getY() + csi10.y;
        final double double13 = ew.getZ() + csi10.z;
        for (int integer2 = 0, integer3 = list.size(); integer2 < integer3; ++integer2) {
            final BakedQuad dnz19 = (BakedQuad)list.get(integer2);
            if (boolean5) {
                this.calculateShape(bgz, bvt, ew, dnz19.getVertices(), dnz19.getDirection(), null, bitSet);
                final BlockPos ew2 = bitSet.get(0) ? ew.relative(dnz19.getDirection()) : ew;
                integer = bvt.getLightColor(bgz, ew2);
            }
            cuw.putBulkData(dnz19.getVertices());
            cuw.faceTex2(integer, integer, integer, integer);
            if (dnz19.isTinted()) {
                final int integer4 = this.blockColors.getColor(bvt, bgz, ew, dnz19.getTintIndex());
                final float float21 = (integer4 >> 16 & 0xFF) / 255.0f;
                final float float22 = (integer4 >> 8 & 0xFF) / 255.0f;
                final float float23 = (integer4 & 0xFF) / 255.0f;
                cuw.faceTint(float21, float22, float23, 4);
                cuw.faceTint(float21, float22, float23, 3);
                cuw.faceTint(float21, float22, float23, 2);
                cuw.faceTint(float21, float22, float23, 1);
            }
            cuw.postProcessFacePosition(double11, double12, double13);
        }
    }
    
    public void renderModel(final BakedModel dyp, final float float2, final float float3, final float float4, final float float5) {
        this.renderModel(null, dyp, float2, float3, float4, float5);
    }
    
    public void renderModel(@Nullable final BlockState bvt, final BakedModel dyp, final float float3, final float float4, final float float5, final float float6) {
        final Random random8 = new Random();
        final long long9 = 42L;
        for (final Direction fb14 : Direction.values()) {
            random8.setSeed(42L);
            this.renderQuadList(float3, float4, float5, float6, dyp.getQuads(bvt, fb14, random8));
        }
        random8.setSeed(42L);
        this.renderQuadList(float3, float4, float5, float6, dyp.getQuads(bvt, null, random8));
    }
    
    public void renderSingleBlock(final BakedModel dyp, final BlockState bvt, final float float3, final boolean boolean4) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        final int integer6 = this.blockColors.getColor(bvt, null, null, 0);
        final float float4 = (integer6 >> 16 & 0xFF) / 255.0f;
        final float float5 = (integer6 >> 8 & 0xFF) / 255.0f;
        final float float6 = (integer6 & 0xFF) / 255.0f;
        if (!boolean4) {
            GlStateManager.color4f(float3, float3, float3, 1.0f);
        }
        this.renderModel(bvt, dyp, float3, float4, float5, float6);
    }
    
    private void renderQuadList(final float float1, final float float2, final float float3, final float float4, final List<BakedQuad> list) {
        final Tesselator cuz7 = Tesselator.getInstance();
        final BufferBuilder cuw8 = cuz7.getBuilder();
        for (int integer9 = 0, integer10 = list.size(); integer9 < integer10; ++integer9) {
            final BakedQuad dnz11 = (BakedQuad)list.get(integer9);
            cuw8.begin(7, DefaultVertexFormat.BLOCK_NORMALS);
            cuw8.putBulkData(dnz11.getVertices());
            if (dnz11.isTinted()) {
                cuw8.fixupQuadColor(float2 * float1, float3 * float1, float4 * float1);
            }
            else {
                cuw8.fixupQuadColor(float1, float1, float1);
            }
            final Vec3i fs12 = dnz11.getDirection().getNormal();
            cuw8.postNormal((float)fs12.getX(), (float)fs12.getY(), (float)fs12.getZ());
            cuz7.end();
        }
    }
    
    public static void enableCaching() {
        ((Cache)ModelBlockRenderer.CACHE.get()).enable();
    }
    
    public static void clearCache() {
        ((Cache)ModelBlockRenderer.CACHE.get()).disable();
    }
    
    static {
        CACHE = ThreadLocal.withInitial(() -> new Cache());
    }
    
    enum AmbientVertexRemap {
        DOWN(0, 1, 2, 3), 
        UP(2, 3, 0, 1), 
        NORTH(3, 0, 1, 2), 
        SOUTH(0, 1, 2, 3), 
        WEST(3, 0, 1, 2), 
        EAST(1, 2, 3, 0);
        
        private final int vert0;
        private final int vert1;
        private final int vert2;
        private final int vert3;
        private static final AmbientVertexRemap[] BY_FACING;
        
        private AmbientVertexRemap(final int integer3, final int integer4, final int integer5, final int integer6) {
            this.vert0 = integer3;
            this.vert1 = integer4;
            this.vert2 = integer5;
            this.vert3 = integer6;
        }
        
        public static AmbientVertexRemap fromFacing(final Direction fb) {
            return AmbientVertexRemap.BY_FACING[fb.get3DDataValue()];
        }
        
        static {
            BY_FACING = Util.<AmbientVertexRemap[]>make(new AmbientVertexRemap[6], (java.util.function.Consumer<AmbientVertexRemap[]>)(arr -> {
                arr[Direction.DOWN.get3DDataValue()] = AmbientVertexRemap.DOWN;
                arr[Direction.UP.get3DDataValue()] = AmbientVertexRemap.UP;
                arr[Direction.NORTH.get3DDataValue()] = AmbientVertexRemap.NORTH;
                arr[Direction.SOUTH.get3DDataValue()] = AmbientVertexRemap.SOUTH;
                arr[Direction.WEST.get3DDataValue()] = AmbientVertexRemap.WEST;
                arr[Direction.EAST.get3DDataValue()] = AmbientVertexRemap.EAST;
            }));
        }
    }
    
    static class Cache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap colorCache;
        private final Long2FloatLinkedOpenHashMap brightnessCache;
        
        private Cache() {
            this.colorCache = Util.<Long2IntLinkedOpenHashMap>make((java.util.function.Supplier<Long2IntLinkedOpenHashMap>)(() -> {
                final Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap2 = new Long2IntLinkedOpenHashMap(100, 0.25f) {
                    protected void rehash(final int integer) {
                    }
                };
                long2IntLinkedOpenHashMap2.defaultReturnValue(Integer.MAX_VALUE);
                return long2IntLinkedOpenHashMap2;
            }));
            this.brightnessCache = Util.<Long2FloatLinkedOpenHashMap>make((java.util.function.Supplier<Long2FloatLinkedOpenHashMap>)(() -> {
                final Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap2 = new Long2FloatLinkedOpenHashMap(100, 0.25f) {
                    protected void rehash(final int integer) {
                    }
                };
                long2FloatLinkedOpenHashMap2.defaultReturnValue(Float.NaN);
                return long2FloatLinkedOpenHashMap2;
            }));
        }
        
        public void enable() {
            this.enabled = true;
        }
        
        public void disable() {
            this.enabled = false;
            this.colorCache.clear();
            this.brightnessCache.clear();
        }
        
        public int getLightColor(final BlockState bvt, final BlockAndBiomeGetter bgz, final BlockPos ew) {
            final long long5 = ew.asLong();
            if (this.enabled) {
                final int integer7 = this.colorCache.get(long5);
                if (integer7 != Integer.MAX_VALUE) {
                    return integer7;
                }
            }
            final int integer7 = bvt.getLightColor(bgz, ew);
            if (this.enabled) {
                if (this.colorCache.size() == 100) {
                    this.colorCache.removeFirstInt();
                }
                this.colorCache.put(long5, integer7);
            }
            return integer7;
        }
        
        public float getShadeBrightness(final BlockState bvt, final BlockAndBiomeGetter bgz, final BlockPos ew) {
            final long long5 = ew.asLong();
            if (this.enabled) {
                final float float7 = this.brightnessCache.get(long5);
                if (!Float.isNaN(float7)) {
                    return float7;
                }
            }
            final float float7 = bvt.getShadeBrightness(bgz, ew);
            if (this.enabled) {
                if (this.brightnessCache.size() == 100) {
                    this.brightnessCache.removeFirstFloat();
                }
                this.brightnessCache.put(long5, float7);
            }
            return float7;
        }
    }
    
    class AmbientOcclusionFace {
        private final float[] brightness;
        private final int[] lightmap;
        
        public AmbientOcclusionFace() {
            this.brightness = new float[4];
            this.lightmap = new int[4];
        }
        
        public void calculate(final BlockAndBiomeGetter bgz, final BlockState bvt, final BlockPos ew, final Direction fb, final float[] arr, final BitSet bitSet) {
            final BlockPos ew2 = bitSet.get(0) ? ew.relative(fb) : ew;
            final AdjacencyInfo a9 = AdjacencyInfo.fromFacing(fb);
            final BlockPos.MutableBlockPos a10 = new BlockPos.MutableBlockPos();
            final Cache d11 = (Cache)ModelBlockRenderer.CACHE.get();
            a10.set(ew2).move(a9.corners[0]);
            final BlockState bvt2 = bgz.getBlockState(a10);
            final int integer13 = d11.getLightColor(bvt2, bgz, a10);
            final float float14 = d11.getShadeBrightness(bvt2, bgz, a10);
            a10.set(ew2).move(a9.corners[1]);
            final BlockState bvt3 = bgz.getBlockState(a10);
            final int integer14 = d11.getLightColor(bvt3, bgz, a10);
            final float float15 = d11.getShadeBrightness(bvt3, bgz, a10);
            a10.set(ew2).move(a9.corners[2]);
            final BlockState bvt4 = bgz.getBlockState(a10);
            final int integer15 = d11.getLightColor(bvt4, bgz, a10);
            final float float16 = d11.getShadeBrightness(bvt4, bgz, a10);
            a10.set(ew2).move(a9.corners[3]);
            final BlockState bvt5 = bgz.getBlockState(a10);
            final int integer16 = d11.getLightColor(bvt5, bgz, a10);
            final float float17 = d11.getShadeBrightness(bvt5, bgz, a10);
            a10.set(ew2).move(a9.corners[0]).move(fb);
            final boolean boolean24 = bgz.getBlockState(a10).getLightBlock(bgz, a10) == 0;
            a10.set(ew2).move(a9.corners[1]).move(fb);
            final boolean boolean25 = bgz.getBlockState(a10).getLightBlock(bgz, a10) == 0;
            a10.set(ew2).move(a9.corners[2]).move(fb);
            final boolean boolean26 = bgz.getBlockState(a10).getLightBlock(bgz, a10) == 0;
            a10.set(ew2).move(a9.corners[3]).move(fb);
            final boolean boolean27 = bgz.getBlockState(a10).getLightBlock(bgz, a10) == 0;
            float float18;
            int integer17;
            if (boolean26 || boolean24) {
                a10.set(ew2).move(a9.corners[0]).move(a9.corners[2]);
                final BlockState bvt6 = bgz.getBlockState(a10);
                float18 = d11.getShadeBrightness(bvt6, bgz, a10);
                integer17 = d11.getLightColor(bvt6, bgz, a10);
            }
            else {
                float18 = float14;
                integer17 = integer13;
            }
            float float19;
            int integer18;
            if (boolean27 || boolean24) {
                a10.set(ew2).move(a9.corners[0]).move(a9.corners[3]);
                final BlockState bvt6 = bgz.getBlockState(a10);
                float19 = d11.getShadeBrightness(bvt6, bgz, a10);
                integer18 = d11.getLightColor(bvt6, bgz, a10);
            }
            else {
                float19 = float14;
                integer18 = integer13;
            }
            float float20;
            int integer19;
            if (boolean26 || boolean25) {
                a10.set(ew2).move(a9.corners[1]).move(a9.corners[2]);
                final BlockState bvt6 = bgz.getBlockState(a10);
                float20 = d11.getShadeBrightness(bvt6, bgz, a10);
                integer19 = d11.getLightColor(bvt6, bgz, a10);
            }
            else {
                float20 = float14;
                integer19 = integer13;
            }
            float float21;
            int integer20;
            if (boolean27 || boolean25) {
                a10.set(ew2).move(a9.corners[1]).move(a9.corners[3]);
                final BlockState bvt6 = bgz.getBlockState(a10);
                float21 = d11.getShadeBrightness(bvt6, bgz, a10);
                integer20 = d11.getLightColor(bvt6, bgz, a10);
            }
            else {
                float21 = float14;
                integer20 = integer13;
            }
            int integer21 = d11.getLightColor(bvt, bgz, ew);
            a10.set(ew).move(fb);
            final BlockState bvt7 = bgz.getBlockState(a10);
            if (bitSet.get(0) || !bvt7.isSolidRender(bgz, a10)) {
                integer21 = d11.getLightColor(bvt7, bgz, a10);
            }
            final float float22 = bitSet.get(0) ? d11.getShadeBrightness(bgz.getBlockState(ew2), bgz, ew2) : d11.getShadeBrightness(bgz.getBlockState(ew), bgz, ew);
            final AmbientVertexRemap c39 = AmbientVertexRemap.fromFacing(fb);
            if (!bitSet.get(1) || !a9.doNonCubicWeight) {
                final float float23 = (float17 + float14 + float19 + float22) * 0.25f;
                final float float24 = (float16 + float14 + float18 + float22) * 0.25f;
                final float float25 = (float16 + float15 + float20 + float22) * 0.25f;
                final float float26 = (float17 + float15 + float21 + float22) * 0.25f;
                this.lightmap[c39.vert0] = this.blend(integer16, integer13, integer18, integer21);
                this.lightmap[c39.vert1] = this.blend(integer15, integer13, integer17, integer21);
                this.lightmap[c39.vert2] = this.blend(integer15, integer14, integer19, integer21);
                this.lightmap[c39.vert3] = this.blend(integer16, integer14, integer20, integer21);
                this.brightness[c39.vert0] = float23;
                this.brightness[c39.vert1] = float24;
                this.brightness[c39.vert2] = float25;
                this.brightness[c39.vert3] = float26;
            }
            else {
                final float float23 = (float17 + float14 + float19 + float22) * 0.25f;
                final float float24 = (float16 + float14 + float18 + float22) * 0.25f;
                final float float25 = (float16 + float15 + float20 + float22) * 0.25f;
                final float float26 = (float17 + float15 + float21 + float22) * 0.25f;
                final float float27 = arr[a9.vert0Weights[0].shape] * arr[a9.vert0Weights[1].shape];
                final float float28 = arr[a9.vert0Weights[2].shape] * arr[a9.vert0Weights[3].shape];
                final float float29 = arr[a9.vert0Weights[4].shape] * arr[a9.vert0Weights[5].shape];
                final float float30 = arr[a9.vert0Weights[6].shape] * arr[a9.vert0Weights[7].shape];
                final float float31 = arr[a9.vert1Weights[0].shape] * arr[a9.vert1Weights[1].shape];
                final float float32 = arr[a9.vert1Weights[2].shape] * arr[a9.vert1Weights[3].shape];
                final float float33 = arr[a9.vert1Weights[4].shape] * arr[a9.vert1Weights[5].shape];
                final float float34 = arr[a9.vert1Weights[6].shape] * arr[a9.vert1Weights[7].shape];
                final float float35 = arr[a9.vert2Weights[0].shape] * arr[a9.vert2Weights[1].shape];
                final float float36 = arr[a9.vert2Weights[2].shape] * arr[a9.vert2Weights[3].shape];
                final float float37 = arr[a9.vert2Weights[4].shape] * arr[a9.vert2Weights[5].shape];
                final float float38 = arr[a9.vert2Weights[6].shape] * arr[a9.vert2Weights[7].shape];
                final float float39 = arr[a9.vert3Weights[0].shape] * arr[a9.vert3Weights[1].shape];
                final float float40 = arr[a9.vert3Weights[2].shape] * arr[a9.vert3Weights[3].shape];
                final float float41 = arr[a9.vert3Weights[4].shape] * arr[a9.vert3Weights[5].shape];
                final float float42 = arr[a9.vert3Weights[6].shape] * arr[a9.vert3Weights[7].shape];
                this.brightness[c39.vert0] = float23 * float27 + float24 * float28 + float25 * float29 + float26 * float30;
                this.brightness[c39.vert1] = float23 * float31 + float24 * float32 + float25 * float33 + float26 * float34;
                this.brightness[c39.vert2] = float23 * float35 + float24 * float36 + float25 * float37 + float26 * float38;
                this.brightness[c39.vert3] = float23 * float39 + float24 * float40 + float25 * float41 + float26 * float42;
                final int integer22 = this.blend(integer16, integer13, integer18, integer21);
                final int integer23 = this.blend(integer15, integer13, integer17, integer21);
                final int integer24 = this.blend(integer15, integer14, integer19, integer21);
                final int integer25 = this.blend(integer16, integer14, integer20, integer21);
                this.lightmap[c39.vert0] = this.blend(integer22, integer23, integer24, integer25, float27, float28, float29, float30);
                this.lightmap[c39.vert1] = this.blend(integer22, integer23, integer24, integer25, float31, float32, float33, float34);
                this.lightmap[c39.vert2] = this.blend(integer22, integer23, integer24, integer25, float35, float36, float37, float38);
                this.lightmap[c39.vert3] = this.blend(integer22, integer23, integer24, integer25, float39, float40, float41, float42);
            }
        }
        
        private int blend(int integer1, int integer2, int integer3, final int integer4) {
            if (integer1 == 0) {
                integer1 = integer4;
            }
            if (integer2 == 0) {
                integer2 = integer4;
            }
            if (integer3 == 0) {
                integer3 = integer4;
            }
            return integer1 + integer2 + integer3 + integer4 >> 2 & 0xFF00FF;
        }
        
        private int blend(final int integer1, final int integer2, final int integer3, final int integer4, final float float5, final float float6, final float float7, final float float8) {
            final int integer5 = (int)((integer1 >> 16 & 0xFF) * float5 + (integer2 >> 16 & 0xFF) * float6 + (integer3 >> 16 & 0xFF) * float7 + (integer4 >> 16 & 0xFF) * float8) & 0xFF;
            final int integer6 = (int)((integer1 & 0xFF) * float5 + (integer2 & 0xFF) * float6 + (integer3 & 0xFF) * float7 + (integer4 & 0xFF) * float8) & 0xFF;
            return integer5 << 16 | integer6;
        }
    }
    
    public enum SizeInfo {
        DOWN(Direction.DOWN, false), 
        UP(Direction.UP, false), 
        NORTH(Direction.NORTH, false), 
        SOUTH(Direction.SOUTH, false), 
        WEST(Direction.WEST, false), 
        EAST(Direction.EAST, false), 
        FLIP_DOWN(Direction.DOWN, true), 
        FLIP_UP(Direction.UP, true), 
        FLIP_NORTH(Direction.NORTH, true), 
        FLIP_SOUTH(Direction.SOUTH, true), 
        FLIP_WEST(Direction.WEST, true), 
        FLIP_EAST(Direction.EAST, true);
        
        private final int shape;
        
        private SizeInfo(final Direction fb, final boolean boolean4) {
            this.shape = fb.get3DDataValue() + (boolean4 ? Direction.values().length : 0);
        }
    }
    
    public enum AdjacencyInfo {
        DOWN(new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH }, 0.5f, true, new SizeInfo[] { SizeInfo.FLIP_WEST, SizeInfo.SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.SOUTH }, new SizeInfo[] { SizeInfo.FLIP_WEST, SizeInfo.NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.FLIP_EAST, SizeInfo.NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.FLIP_EAST, SizeInfo.SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.SOUTH }), 
        UP(new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH }, 1.0f, true, new SizeInfo[] { SizeInfo.EAST, SizeInfo.SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.SOUTH }, new SizeInfo[] { SizeInfo.EAST, SizeInfo.NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.WEST, SizeInfo.NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.WEST, SizeInfo.SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.SOUTH }), 
        NORTH(new Direction[] { Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST }, 0.8f, true, new SizeInfo[] { SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST }, new SizeInfo[] { SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST }, new SizeInfo[] { SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST }, new SizeInfo[] { SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST }), 
        SOUTH(new Direction[] { Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP }, 0.8f, true, new SizeInfo[] { SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.UP, SizeInfo.WEST }, new SizeInfo[] { SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.DOWN, SizeInfo.WEST }, new SizeInfo[] { SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.DOWN, SizeInfo.EAST }, new SizeInfo[] { SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.UP, SizeInfo.EAST }), 
        WEST(new Direction[] { Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH }, 0.6f, true, new SizeInfo[] { SizeInfo.UP, SizeInfo.SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.SOUTH }, new SizeInfo[] { SizeInfo.UP, SizeInfo.NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.DOWN, SizeInfo.NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.DOWN, SizeInfo.SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.SOUTH }), 
        EAST(new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH }, 0.6f, true, new SizeInfo[] { SizeInfo.FLIP_DOWN, SizeInfo.SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.SOUTH }, new SizeInfo[] { SizeInfo.FLIP_DOWN, SizeInfo.NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.FLIP_UP, SizeInfo.NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.NORTH }, new SizeInfo[] { SizeInfo.FLIP_UP, SizeInfo.SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.SOUTH });
        
        private final Direction[] corners;
        private final boolean doNonCubicWeight;
        private final SizeInfo[] vert0Weights;
        private final SizeInfo[] vert1Weights;
        private final SizeInfo[] vert2Weights;
        private final SizeInfo[] vert3Weights;
        private static final AdjacencyInfo[] BY_FACING;
        
        private AdjacencyInfo(final Direction[] arr, final float float4, final boolean boolean5, final SizeInfo[] arr6, final SizeInfo[] arr7, final SizeInfo[] arr8, final SizeInfo[] arr9) {
            this.corners = arr;
            this.doNonCubicWeight = boolean5;
            this.vert0Weights = arr6;
            this.vert1Weights = arr7;
            this.vert2Weights = arr8;
            this.vert3Weights = arr9;
        }
        
        public static AdjacencyInfo fromFacing(final Direction fb) {
            return AdjacencyInfo.BY_FACING[fb.get3DDataValue()];
        }
        
        static {
            BY_FACING = Util.<AdjacencyInfo[]>make(new AdjacencyInfo[6], (java.util.function.Consumer<AdjacencyInfo[]>)(arr -> {
                arr[Direction.DOWN.get3DDataValue()] = AdjacencyInfo.DOWN;
                arr[Direction.UP.get3DDataValue()] = AdjacencyInfo.UP;
                arr[Direction.NORTH.get3DDataValue()] = AdjacencyInfo.NORTH;
                arr[Direction.SOUTH.get3DDataValue()] = AdjacencyInfo.SOUTH;
                arr[Direction.WEST.get3DDataValue()] = AdjacencyInfo.WEST;
                arr[Direction.EAST.get3DDataValue()] = AdjacencyInfo.EAST;
            }));
        }
    }
}
