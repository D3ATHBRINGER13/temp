package com.mojang.blaze3d.platform;

import org.lwjgl.opengl.GL;
import java.util.stream.IntStream;
import java.util.function.Consumer;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import com.mojang.math.Matrix4f;
import javax.annotation.Nullable;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL11;
import java.nio.FloatBuffer;

public class GlStateManager {
    private static final int LIGHT_COUNT = 8;
    private static final int TEXTURE_COUNT = 8;
    private static final FloatBuffer MATRIX_BUFFER;
    private static final FloatBuffer COLOR_BUFFER;
    private static final AlphaState ALPHA_TEST;
    private static final BooleanState LIGHTING;
    private static final BooleanState[] LIGHT_ENABLE;
    private static final ColorMaterialState COLOR_MATERIAL;
    private static final BlendState BLEND;
    private static final DepthState DEPTH;
    private static final FogState FOG;
    private static final CullState CULL;
    private static final PolygonOffsetState POLY_OFFSET;
    private static final ColorLogicState COLOR_LOGIC;
    private static final TexGenState TEX_GEN;
    private static final ClearState CLEAR;
    private static final StencilState STENCIL;
    private static final BooleanState NORMALIZE;
    private static int activeTexture;
    private static final TextureState[] TEXTURES;
    private static int shadeModel;
    private static final BooleanState RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color COLOR;
    private static final float DEFAULTALPHACUTOFF = 0.1f;
    
    public static void pushLightingAttributes() {
        GL11.glPushAttrib(8256);
    }
    
    public static void pushTextureAttributes() {
        GL11.glPushAttrib(270336);
    }
    
    public static void popAttributes() {
        GL11.glPopAttrib();
    }
    
    public static void disableAlphaTest() {
        GlStateManager.ALPHA_TEST.mode.disable();
    }
    
    public static void enableAlphaTest() {
        GlStateManager.ALPHA_TEST.mode.enable();
    }
    
    public static void alphaFunc(final int integer, final float float2) {
        if (integer != GlStateManager.ALPHA_TEST.func || float2 != GlStateManager.ALPHA_TEST.reference) {
            GL11.glAlphaFunc(GlStateManager.ALPHA_TEST.func = integer, GlStateManager.ALPHA_TEST.reference = float2);
        }
    }
    
    public static void enableLighting() {
        GlStateManager.LIGHTING.enable();
    }
    
    public static void disableLighting() {
        GlStateManager.LIGHTING.disable();
    }
    
    public static void enableLight(final int integer) {
        GlStateManager.LIGHT_ENABLE[integer].enable();
    }
    
    public static void disableLight(final int integer) {
        GlStateManager.LIGHT_ENABLE[integer].disable();
    }
    
    public static void enableColorMaterial() {
        GlStateManager.COLOR_MATERIAL.enable.enable();
    }
    
    public static void disableColorMaterial() {
        GlStateManager.COLOR_MATERIAL.enable.disable();
    }
    
    public static void colorMaterial(final int integer1, final int integer2) {
        if (integer1 != GlStateManager.COLOR_MATERIAL.face || integer2 != GlStateManager.COLOR_MATERIAL.mode) {
            GL11.glColorMaterial(GlStateManager.COLOR_MATERIAL.face = integer1, GlStateManager.COLOR_MATERIAL.mode = integer2);
        }
    }
    
    public static void light(final int integer1, final int integer2, final FloatBuffer floatBuffer) {
        GL11.glLightfv(integer1, integer2, floatBuffer);
    }
    
    public static void lightModel(final int integer, final FloatBuffer floatBuffer) {
        GL11.glLightModelfv(integer, floatBuffer);
    }
    
    public static void normal3f(final float float1, final float float2, final float float3) {
        GL11.glNormal3f(float1, float2, float3);
    }
    
    public static void disableDepthTest() {
        GlStateManager.DEPTH.mode.disable();
    }
    
    public static void enableDepthTest() {
        GlStateManager.DEPTH.mode.enable();
    }
    
    public static void depthFunc(final int integer) {
        if (integer != GlStateManager.DEPTH.func) {
            GL11.glDepthFunc(GlStateManager.DEPTH.func = integer);
        }
    }
    
    public static void depthMask(final boolean boolean1) {
        if (boolean1 != GlStateManager.DEPTH.mask) {
            GL11.glDepthMask(GlStateManager.DEPTH.mask = boolean1);
        }
    }
    
    public static void disableBlend() {
        GlStateManager.BLEND.mode.disable();
    }
    
    public static void enableBlend() {
        GlStateManager.BLEND.mode.enable();
    }
    
    public static void blendFunc(final SourceFactor sourceFactor, final DestFactor destFactor) {
        blendFunc(sourceFactor.value, destFactor.value);
    }
    
    public static void blendFunc(final int integer1, final int integer2) {
        if (integer1 != GlStateManager.BLEND.srcRgb || integer2 != GlStateManager.BLEND.dstRgb) {
            GL11.glBlendFunc(GlStateManager.BLEND.srcRgb = integer1, GlStateManager.BLEND.dstRgb = integer2);
        }
    }
    
    public static void blendFuncSeparate(final SourceFactor sourceFactor1, final DestFactor destFactor2, final SourceFactor sourceFactor3, final DestFactor destFactor4) {
        blendFuncSeparate(sourceFactor1.value, destFactor2.value, sourceFactor3.value, destFactor4.value);
    }
    
    public static void blendFuncSeparate(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (integer1 != GlStateManager.BLEND.srcRgb || integer2 != GlStateManager.BLEND.dstRgb || integer3 != GlStateManager.BLEND.srcAlpha || integer4 != GlStateManager.BLEND.dstAlpha) {
            GLX.glBlendFuncSeparate(GlStateManager.BLEND.srcRgb = integer1, GlStateManager.BLEND.dstRgb = integer2, GlStateManager.BLEND.srcAlpha = integer3, GlStateManager.BLEND.dstAlpha = integer4);
        }
    }
    
    public static void blendEquation(final int integer) {
        GL14.glBlendEquation(integer);
    }
    
    public static void setupSolidRenderingTextureCombine(final int integer) {
        GlStateManager.COLOR_BUFFER.put(0, (integer >> 16 & 0xFF) / 255.0f);
        GlStateManager.COLOR_BUFFER.put(1, (integer >> 8 & 0xFF) / 255.0f);
        GlStateManager.COLOR_BUFFER.put(2, (integer >> 0 & 0xFF) / 255.0f);
        GlStateManager.COLOR_BUFFER.put(3, (integer >> 24 & 0xFF) / 255.0f);
        texEnv(8960, 8705, GlStateManager.COLOR_BUFFER);
        texEnv(8960, 8704, 34160);
        texEnv(8960, 34161, 7681);
        texEnv(8960, 34176, 34166);
        texEnv(8960, 34192, 768);
        texEnv(8960, 34162, 7681);
        texEnv(8960, 34184, 5890);
        texEnv(8960, 34200, 770);
    }
    
    public static void tearDownSolidRenderingTextureCombine() {
        texEnv(8960, 8704, 8448);
        texEnv(8960, 34161, 8448);
        texEnv(8960, 34162, 8448);
        texEnv(8960, 34176, 5890);
        texEnv(8960, 34184, 5890);
        texEnv(8960, 34192, 768);
        texEnv(8960, 34200, 770);
    }
    
    public static void enableFog() {
        GlStateManager.FOG.enable.enable();
    }
    
    public static void disableFog() {
        GlStateManager.FOG.enable.disable();
    }
    
    public static void fogMode(final FogMode l) {
        fogMode(l.value);
    }
    
    private static void fogMode(final int integer) {
        if (integer != GlStateManager.FOG.mode) {
            GL11.glFogi(2917, GlStateManager.FOG.mode = integer);
        }
    }
    
    public static void fogDensity(final float float1) {
        if (float1 != GlStateManager.FOG.density) {
            GL11.glFogf(2914, GlStateManager.FOG.density = float1);
        }
    }
    
    public static void fogStart(final float float1) {
        if (float1 != GlStateManager.FOG.start) {
            GL11.glFogf(2915, GlStateManager.FOG.start = float1);
        }
    }
    
    public static void fogEnd(final float float1) {
        if (float1 != GlStateManager.FOG.end) {
            GL11.glFogf(2916, GlStateManager.FOG.end = float1);
        }
    }
    
    public static void fog(final int integer, final FloatBuffer floatBuffer) {
        GL11.glFogfv(integer, floatBuffer);
    }
    
    public static void fogi(final int integer1, final int integer2) {
        GL11.glFogi(integer1, integer2);
    }
    
    public static void enableCull() {
        GlStateManager.CULL.enable.enable();
    }
    
    public static void disableCull() {
        GlStateManager.CULL.enable.disable();
    }
    
    public static void cullFace(final CullFace i) {
        cullFace(i.value);
    }
    
    private static void cullFace(final int integer) {
        if (integer != GlStateManager.CULL.mode) {
            GL11.glCullFace(GlStateManager.CULL.mode = integer);
        }
    }
    
    public static void polygonMode(final int integer1, final int integer2) {
        GL11.glPolygonMode(integer1, integer2);
    }
    
    public static void enablePolygonOffset() {
        GlStateManager.POLY_OFFSET.fill.enable();
    }
    
    public static void disablePolygonOffset() {
        GlStateManager.POLY_OFFSET.fill.disable();
    }
    
    public static void enableLineOffset() {
        GlStateManager.POLY_OFFSET.line.enable();
    }
    
    public static void disableLineOffset() {
        GlStateManager.POLY_OFFSET.line.disable();
    }
    
    public static void polygonOffset(final float float1, final float float2) {
        if (float1 != GlStateManager.POLY_OFFSET.factor || float2 != GlStateManager.POLY_OFFSET.units) {
            GL11.glPolygonOffset(GlStateManager.POLY_OFFSET.factor = float1, GlStateManager.POLY_OFFSET.units = float2);
        }
    }
    
    public static void enableColorLogicOp() {
        GlStateManager.COLOR_LOGIC.enable.enable();
    }
    
    public static void disableColorLogicOp() {
        GlStateManager.COLOR_LOGIC.enable.disable();
    }
    
    public static void logicOp(final LogicOp n) {
        logicOp(n.value);
    }
    
    public static void logicOp(final int integer) {
        if (integer != GlStateManager.COLOR_LOGIC.op) {
            GL11.glLogicOp(GlStateManager.COLOR_LOGIC.op = integer);
        }
    }
    
    public static void enableTexGen(final TexGen s) {
        getTexGen(s).enable.enable();
    }
    
    public static void disableTexGen(final TexGen s) {
        getTexGen(s).enable.disable();
    }
    
    public static void texGenMode(final TexGen s, final int integer) {
        final TexGenCoord t3 = getTexGen(s);
        if (integer != t3.mode) {
            t3.mode = integer;
            GL11.glTexGeni(t3.coord, 9472, integer);
        }
    }
    
    public static void texGenParam(final TexGen s, final int integer, final FloatBuffer floatBuffer) {
        GL11.glTexGenfv(getTexGen(s).coord, integer, floatBuffer);
    }
    
    private static TexGenCoord getTexGen(final TexGen s) {
        switch (s) {
            case S: {
                return GlStateManager.TEX_GEN.s;
            }
            case T: {
                return GlStateManager.TEX_GEN.t;
            }
            case R: {
                return GlStateManager.TEX_GEN.r;
            }
            case Q: {
                return GlStateManager.TEX_GEN.q;
            }
            default: {
                return GlStateManager.TEX_GEN.s;
            }
        }
    }
    
    public static void activeTexture(final int integer) {
        if (GlStateManager.activeTexture != integer - GLX.GL_TEXTURE0) {
            GlStateManager.activeTexture = integer - GLX.GL_TEXTURE0;
            GLX.glActiveTexture(integer);
        }
    }
    
    public static void enableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].enable.enable();
    }
    
    public static void disableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].enable.disable();
    }
    
    public static void texEnv(final int integer1, final int integer2, final FloatBuffer floatBuffer) {
        GL11.glTexEnvfv(integer1, integer2, floatBuffer);
    }
    
    public static void texEnv(final int integer1, final int integer2, final int integer3) {
        GL11.glTexEnvi(integer1, integer2, integer3);
    }
    
    public static void texEnv(final int integer1, final int integer2, final float float3) {
        GL11.glTexEnvf(integer1, integer2, float3);
    }
    
    public static void texParameter(final int integer1, final int integer2, final float float3) {
        GL11.glTexParameterf(integer1, integer2, float3);
    }
    
    public static void texParameter(final int integer1, final int integer2, final int integer3) {
        GL11.glTexParameteri(integer1, integer2, integer3);
    }
    
    public static int getTexLevelParameter(final int integer1, final int integer2, final int integer3) {
        return GL11.glGetTexLevelParameteri(integer1, integer2, integer3);
    }
    
    public static int genTexture() {
        return GL11.glGenTextures();
    }
    
    public static void deleteTexture(final int integer) {
        GL11.glDeleteTextures(integer);
        for (final TextureState v5 : GlStateManager.TEXTURES) {
            if (v5.binding == integer) {
                v5.binding = -1;
            }
        }
    }
    
    public static void bindTexture(final int integer) {
        if (integer != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
            GL11.glBindTexture(3553, GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = integer);
        }
    }
    
    public static void texImage2D(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, @Nullable final IntBuffer intBuffer) {
        GL11.glTexImage2D(integer1, integer2, integer3, integer4, integer5, integer6, integer7, integer8, intBuffer);
    }
    
    public static void texSubImage2D(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final long long9) {
        GL11.glTexSubImage2D(integer1, integer2, integer3, integer4, integer5, integer6, integer7, integer8, long9);
    }
    
    public static void copyTexSubImage2D(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        GL11.glCopyTexSubImage2D(integer1, integer2, integer3, integer4, integer5, integer6, integer7, integer8);
    }
    
    public static void getTexImage(final int integer1, final int integer2, final int integer3, final int integer4, final long long5) {
        GL11.glGetTexImage(integer1, integer2, integer3, integer4, long5);
    }
    
    public static void enableNormalize() {
        GlStateManager.NORMALIZE.enable();
    }
    
    public static void disableNormalize() {
        GlStateManager.NORMALIZE.disable();
    }
    
    public static void shadeModel(final int integer) {
        if (integer != GlStateManager.shadeModel) {
            GL11.glShadeModel(GlStateManager.shadeModel = integer);
        }
    }
    
    public static void enableRescaleNormal() {
        GlStateManager.RESCALE_NORMAL.enable();
    }
    
    public static void disableRescaleNormal() {
        GlStateManager.RESCALE_NORMAL.disable();
    }
    
    public static void viewport(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glViewport(Viewport.INSTANCE.x = integer1, Viewport.INSTANCE.y = integer2, Viewport.INSTANCE.width = integer3, Viewport.INSTANCE.height = integer4);
    }
    
    public static void colorMask(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4) {
        if (boolean1 != GlStateManager.COLOR_MASK.red || boolean2 != GlStateManager.COLOR_MASK.green || boolean3 != GlStateManager.COLOR_MASK.blue || boolean4 != GlStateManager.COLOR_MASK.alpha) {
            GL11.glColorMask(GlStateManager.COLOR_MASK.red = boolean1, GlStateManager.COLOR_MASK.green = boolean2, GlStateManager.COLOR_MASK.blue = boolean3, GlStateManager.COLOR_MASK.alpha = boolean4);
        }
    }
    
    public static void stencilFunc(final int integer1, final int integer2, final int integer3) {
        if (integer1 != GlStateManager.STENCIL.func.func || integer1 != GlStateManager.STENCIL.func.ref || integer1 != GlStateManager.STENCIL.func.mask) {
            GL11.glStencilFunc(GlStateManager.STENCIL.func.func = integer1, GlStateManager.STENCIL.func.ref = integer2, GlStateManager.STENCIL.func.mask = integer3);
        }
    }
    
    public static void stencilMask(final int integer) {
        if (integer != GlStateManager.STENCIL.mask) {
            GL11.glStencilMask(GlStateManager.STENCIL.mask = integer);
        }
    }
    
    public static void stencilOp(final int integer1, final int integer2, final int integer3) {
        if (integer1 != GlStateManager.STENCIL.fail || integer2 != GlStateManager.STENCIL.zfail || integer3 != GlStateManager.STENCIL.zpass) {
            GL11.glStencilOp(GlStateManager.STENCIL.fail = integer1, GlStateManager.STENCIL.zfail = integer2, GlStateManager.STENCIL.zpass = integer3);
        }
    }
    
    public static void clearDepth(final double double1) {
        if (double1 != GlStateManager.CLEAR.depth) {
            GL11.glClearDepth(GlStateManager.CLEAR.depth = double1);
        }
    }
    
    public static void clearColor(final float float1, final float float2, final float float3, final float float4) {
        if (float1 != GlStateManager.CLEAR.color.r || float2 != GlStateManager.CLEAR.color.g || float3 != GlStateManager.CLEAR.color.b || float4 != GlStateManager.CLEAR.color.a) {
            GL11.glClearColor(GlStateManager.CLEAR.color.r = float1, GlStateManager.CLEAR.color.g = float2, GlStateManager.CLEAR.color.b = float3, GlStateManager.CLEAR.color.a = float4);
        }
    }
    
    public static void clearStencil(final int integer) {
        if (integer != GlStateManager.CLEAR.stencil) {
            GL11.glClearStencil(GlStateManager.CLEAR.stencil = integer);
        }
    }
    
    public static void clear(final int integer, final boolean boolean2) {
        GL11.glClear(integer);
        if (boolean2) {
            getError();
        }
    }
    
    public static void matrixMode(final int integer) {
        GL11.glMatrixMode(integer);
    }
    
    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }
    
    public static void pushMatrix() {
        GL11.glPushMatrix();
    }
    
    public static void popMatrix() {
        GL11.glPopMatrix();
    }
    
    public static void getMatrix(final int integer, final FloatBuffer floatBuffer) {
        GL11.glGetFloatv(integer, floatBuffer);
    }
    
    public static Matrix4f getMatrix4f(final int integer) {
        GL11.glGetFloatv(integer, GlStateManager.MATRIX_BUFFER);
        GlStateManager.MATRIX_BUFFER.rewind();
        final Matrix4f blaze3D. = new Matrix4f();
        blaze3D..load(GlStateManager.MATRIX_BUFFER);
        GlStateManager.MATRIX_BUFFER.rewind();
        return blaze3D.;
    }
    
    public static void ortho(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        GL11.glOrtho(double1, double2, double3, double4, double5, double6);
    }
    
    public static void rotatef(final float float1, final float float2, final float float3, final float float4) {
        GL11.glRotatef(float1, float2, float3, float4);
    }
    
    public static void rotated(final double double1, final double double2, final double double3, final double double4) {
        GL11.glRotated(double1, double2, double3, double4);
    }
    
    public static void scalef(final float float1, final float float2, final float float3) {
        GL11.glScalef(float1, float2, float3);
    }
    
    public static void scaled(final double double1, final double double2, final double double3) {
        GL11.glScaled(double1, double2, double3);
    }
    
    public static void translatef(final float float1, final float float2, final float float3) {
        GL11.glTranslatef(float1, float2, float3);
    }
    
    public static void translated(final double double1, final double double2, final double double3) {
        GL11.glTranslated(double1, double2, double3);
    }
    
    public static void multMatrix(final FloatBuffer floatBuffer) {
        GL11.glMultMatrixf(floatBuffer);
    }
    
    public static void multMatrix(final Matrix4f blaze3D. {
        blaze3D.store(GlStateManager.MATRIX_BUFFER);
        GlStateManager.MATRIX_BUFFER.rewind();
        GL11.glMultMatrixf(GlStateManager.MATRIX_BUFFER);
    }
    
    public static void color4f(final float float1, final float float2, final float float3, final float float4) {
        if (float1 != GlStateManager.COLOR.r || float2 != GlStateManager.COLOR.g || float3 != GlStateManager.COLOR.b || float4 != GlStateManager.COLOR.a) {
            GL11.glColor4f(GlStateManager.COLOR.r = float1, GlStateManager.COLOR.g = float2, GlStateManager.COLOR.b = float3, GlStateManager.COLOR.a = float4);
        }
    }
    
    public static void color3f(final float float1, final float float2, final float float3) {
        color4f(float1, float2, float3, 1.0f);
    }
    
    public static void texCoord2f(final float float1, final float float2) {
        GL11.glTexCoord2f(float1, float2);
    }
    
    public static void vertex3f(final float float1, final float float2, final float float3) {
        GL11.glVertex3f(float1, float2, float3);
    }
    
    public static void clearCurrentColor() {
        GlStateManager.COLOR.r = -1.0f;
        GlStateManager.COLOR.g = -1.0f;
        GlStateManager.COLOR.b = -1.0f;
        GlStateManager.COLOR.a = -1.0f;
    }
    
    public static void normalPointer(final int integer1, final int integer2, final int integer3) {
        GL11.glNormalPointer(integer1, integer2, (long)integer3);
    }
    
    public static void normalPointer(final int integer1, final int integer2, final ByteBuffer byteBuffer) {
        GL11.glNormalPointer(integer1, integer2, byteBuffer);
    }
    
    public static void texCoordPointer(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glTexCoordPointer(integer1, integer2, integer3, (long)integer4);
    }
    
    public static void texCoordPointer(final int integer1, final int integer2, final int integer3, final ByteBuffer byteBuffer) {
        GL11.glTexCoordPointer(integer1, integer2, integer3, byteBuffer);
    }
    
    public static void vertexPointer(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glVertexPointer(integer1, integer2, integer3, (long)integer4);
    }
    
    public static void vertexPointer(final int integer1, final int integer2, final int integer3, final ByteBuffer byteBuffer) {
        GL11.glVertexPointer(integer1, integer2, integer3, byteBuffer);
    }
    
    public static void colorPointer(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glColorPointer(integer1, integer2, integer3, (long)integer4);
    }
    
    public static void colorPointer(final int integer1, final int integer2, final int integer3, final ByteBuffer byteBuffer) {
        GL11.glColorPointer(integer1, integer2, integer3, byteBuffer);
    }
    
    public static void disableClientState(final int integer) {
        GL11.glDisableClientState(integer);
    }
    
    public static void enableClientState(final int integer) {
        GL11.glEnableClientState(integer);
    }
    
    public static void begin(final int integer) {
        GL11.glBegin(integer);
    }
    
    public static void end() {
        GL11.glEnd();
    }
    
    public static void drawArrays(final int integer1, final int integer2, final int integer3) {
        GL11.glDrawArrays(integer1, integer2, integer3);
    }
    
    public static void lineWidth(final float float1) {
        GL11.glLineWidth(float1);
    }
    
    public static void callList(final int integer) {
        GL11.glCallList(integer);
    }
    
    public static void deleteLists(final int integer1, final int integer2) {
        GL11.glDeleteLists(integer1, integer2);
    }
    
    public static void newList(final int integer1, final int integer2) {
        GL11.glNewList(integer1, integer2);
    }
    
    public static void endList() {
        GL11.glEndList();
    }
    
    public static int genLists(final int integer) {
        return GL11.glGenLists(integer);
    }
    
    public static void pixelStore(final int integer1, final int integer2) {
        GL11.glPixelStorei(integer1, integer2);
    }
    
    public static void pixelTransfer(final int integer, final float float2) {
        GL11.glPixelTransferf(integer, float2);
    }
    
    public static void readPixels(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final ByteBuffer byteBuffer) {
        GL11.glReadPixels(integer1, integer2, integer3, integer4, integer5, integer6, byteBuffer);
    }
    
    public static void readPixels(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final long long7) {
        GL11.glReadPixels(integer1, integer2, integer3, integer4, integer5, integer6, long7);
    }
    
    public static int getError() {
        return GL11.glGetError();
    }
    
    public static String getString(final int integer) {
        return GL11.glGetString(integer);
    }
    
    public static void getInteger(final int integer, final IntBuffer intBuffer) {
        GL11.glGetIntegerv(integer, intBuffer);
    }
    
    public static int getInteger(final int integer) {
        return GL11.glGetInteger(integer);
    }
    
    public static void setProfile(final Profile p) {
        p.apply();
    }
    
    public static void unsetProfile(final Profile p) {
        p.clean();
    }
    
    static {
        MATRIX_BUFFER = GLX.<FloatBuffer>make(MemoryUtil.memAllocFloat(16), (java.util.function.Consumer<FloatBuffer>)(floatBuffer -> DebugMemoryUntracker.untrack(MemoryUtil.memAddress(floatBuffer))));
        COLOR_BUFFER = GLX.<FloatBuffer>make(MemoryUtil.memAllocFloat(4), (java.util.function.Consumer<FloatBuffer>)(floatBuffer -> DebugMemoryUntracker.untrack(MemoryUtil.memAddress(floatBuffer))));
        ALPHA_TEST = new AlphaState();
        LIGHTING = new BooleanState(2896);
        LIGHT_ENABLE = (BooleanState[])IntStream.range(0, 8).mapToObj(integer -> new BooleanState(16384 + integer)).toArray(BooleanState[]::new);
        COLOR_MATERIAL = new ColorMaterialState();
        BLEND = new BlendState();
        DEPTH = new DepthState();
        FOG = new FogState();
        CULL = new CullState();
        POLY_OFFSET = new PolygonOffsetState();
        COLOR_LOGIC = new ColorLogicState();
        TEX_GEN = new TexGenState();
        CLEAR = new ClearState();
        STENCIL = new StencilState();
        NORMALIZE = new BooleanState(2977);
        TEXTURES = (TextureState[])IntStream.range(0, 8).mapToObj(integer -> new TextureState()).toArray(TextureState[]::new);
        GlStateManager.shadeModel = 7425;
        RESCALE_NORMAL = new BooleanState(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color();
    }
    
    public enum FogMode {
        LINEAR(9729), 
        EXP(2048), 
        EXP2(2049);
        
        public final int value;
        
        private FogMode(final int integer3) {
            this.value = integer3;
        }
    }
    
    public enum CullFace {
        FRONT(1028), 
        BACK(1029), 
        FRONT_AND_BACK(1032);
        
        public final int value;
        
        private CullFace(final int integer3) {
            this.value = integer3;
        }
    }
    
    public enum LogicOp {
        AND(5377), 
        AND_INVERTED(5380), 
        AND_REVERSE(5378), 
        CLEAR(5376), 
        COPY(5379), 
        COPY_INVERTED(5388), 
        EQUIV(5385), 
        INVERT(5386), 
        NAND(5390), 
        NOOP(5381), 
        NOR(5384), 
        OR(5383), 
        OR_INVERTED(5389), 
        OR_REVERSE(5387), 
        SET(5391), 
        XOR(5382);
        
        public final int value;
        
        private LogicOp(final int integer3) {
            this.value = integer3;
        }
    }
    
    public enum Viewport {
        INSTANCE;
        
        protected int x;
        protected int y;
        protected int width;
        protected int height;
    }
    
    static class TextureState {
        public final BooleanState enable;
        public int binding;
        
        private TextureState() {
            this.enable = new BooleanState(3553);
        }
    }
    
    static class AlphaState {
        public final BooleanState mode;
        public int func;
        public float reference;
        
        private AlphaState() {
            this.mode = new BooleanState(3008);
            this.func = 519;
            this.reference = -1.0f;
        }
    }
    
    static class ColorMaterialState {
        public final BooleanState enable;
        public int face;
        public int mode;
        
        private ColorMaterialState() {
            this.enable = new BooleanState(2903);
            this.face = 1032;
            this.mode = 5634;
        }
    }
    
    static class BlendState {
        public final BooleanState mode;
        public int srcRgb;
        public int dstRgb;
        public int srcAlpha;
        public int dstAlpha;
        
        private BlendState() {
            this.mode = new BooleanState(3042);
            this.srcRgb = 1;
            this.dstRgb = 0;
            this.srcAlpha = 1;
            this.dstAlpha = 0;
        }
    }
    
    static class DepthState {
        public final BooleanState mode;
        public boolean mask;
        public int func;
        
        private DepthState() {
            this.mode = new BooleanState(2929);
            this.mask = true;
            this.func = 513;
        }
    }
    
    static class FogState {
        public final BooleanState enable;
        public int mode;
        public float density;
        public float start;
        public float end;
        
        private FogState() {
            this.enable = new BooleanState(2912);
            this.mode = 2048;
            this.density = 1.0f;
            this.end = 1.0f;
        }
    }
    
    static class CullState {
        public final BooleanState enable;
        public int mode;
        
        private CullState() {
            this.enable = new BooleanState(2884);
            this.mode = 1029;
        }
    }
    
    static class PolygonOffsetState {
        public final BooleanState fill;
        public final BooleanState line;
        public float factor;
        public float units;
        
        private PolygonOffsetState() {
            this.fill = new BooleanState(32823);
            this.line = new BooleanState(10754);
        }
    }
    
    static class ColorLogicState {
        public final BooleanState enable;
        public int op;
        
        private ColorLogicState() {
            this.enable = new BooleanState(3058);
            this.op = 5379;
        }
    }
    
    static class ClearState {
        public double depth;
        public final Color color;
        public int stencil;
        
        private ClearState() {
            this.depth = 1.0;
            this.color = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
    
    static class StencilFunc {
        public int func;
        public int ref;
        public int mask;
        
        private StencilFunc() {
            this.func = 519;
            this.mask = -1;
        }
    }
    
    static class StencilState {
        public final StencilFunc func;
        public int mask;
        public int fail;
        public int zfail;
        public int zpass;
        
        private StencilState() {
            this.func = new StencilFunc();
            this.mask = -1;
            this.fail = 7680;
            this.zfail = 7680;
            this.zpass = 7680;
        }
    }
    
    static class TexGenState {
        public final TexGenCoord s;
        public final TexGenCoord t;
        public final TexGenCoord r;
        public final TexGenCoord q;
        
        private TexGenState() {
            this.s = new TexGenCoord(8192, 3168);
            this.t = new TexGenCoord(8193, 3169);
            this.r = new TexGenCoord(8194, 3170);
            this.q = new TexGenCoord(8195, 3171);
        }
    }
    
    static class TexGenCoord {
        public final BooleanState enable;
        public final int coord;
        public int mode;
        
        public TexGenCoord(final int integer1, final int integer2) {
            this.mode = -1;
            this.coord = integer1;
            this.enable = new BooleanState(integer2);
        }
    }
    
    public enum TexGen {
        S, 
        T, 
        R, 
        Q;
    }
    
    static class ColorMask {
        public boolean red;
        public boolean green;
        public boolean blue;
        public boolean alpha;
        
        private ColorMask() {
            this.red = true;
            this.green = true;
            this.blue = true;
            this.alpha = true;
        }
    }
    
    static class Color {
        public float r;
        public float g;
        public float b;
        public float a;
        
        public Color() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        public Color(final float float1, final float float2, final float float3, final float float4) {
            this.r = 1.0f;
            this.g = 1.0f;
            this.b = 1.0f;
            this.a = 1.0f;
            this.r = float1;
            this.g = float2;
            this.b = float3;
            this.a = float4;
        }
    }
    
    static class BooleanState {
        private final int state;
        private boolean enabled;
        
        public BooleanState(final int integer) {
            this.state = integer;
        }
        
        public void disable() {
            this.setEnabled(false);
        }
        
        public void enable() {
            this.setEnabled(true);
        }
        
        public void setEnabled(final boolean boolean1) {
            if (boolean1 != this.enabled) {
                this.enabled = boolean1;
                if (boolean1) {
                    GL11.glEnable(this.state);
                }
                else {
                    GL11.glDisable(this.state);
                }
            }
        }
    }
    
    public enum SourceFactor {
        CONSTANT_ALPHA(32771), 
        CONSTANT_COLOR(32769), 
        DST_ALPHA(772), 
        DST_COLOR(774), 
        ONE(1), 
        ONE_MINUS_CONSTANT_ALPHA(32772), 
        ONE_MINUS_CONSTANT_COLOR(32770), 
        ONE_MINUS_DST_ALPHA(773), 
        ONE_MINUS_DST_COLOR(775), 
        ONE_MINUS_SRC_ALPHA(771), 
        ONE_MINUS_SRC_COLOR(769), 
        SRC_ALPHA(770), 
        SRC_ALPHA_SATURATE(776), 
        SRC_COLOR(768), 
        ZERO(0);
        
        public final int value;
        
        private SourceFactor(final int integer3) {
            this.value = integer3;
        }
    }
    
    public enum DestFactor {
        CONSTANT_ALPHA(32771), 
        CONSTANT_COLOR(32769), 
        DST_ALPHA(772), 
        DST_COLOR(774), 
        ONE(1), 
        ONE_MINUS_CONSTANT_ALPHA(32772), 
        ONE_MINUS_CONSTANT_COLOR(32770), 
        ONE_MINUS_DST_ALPHA(773), 
        ONE_MINUS_DST_COLOR(775), 
        ONE_MINUS_SRC_ALPHA(771), 
        ONE_MINUS_SRC_COLOR(769), 
        SRC_ALPHA(770), 
        SRC_COLOR(768), 
        ZERO(0);
        
        public final int value;
        
        private DestFactor(final int integer3) {
            this.value = integer3;
        }
    }
    
    public enum Profile {
        DEFAULT {
            @Override
            public void apply() {
                GlStateManager.disableAlphaTest();
                GlStateManager.alphaFunc(519, 0.0f);
                GlStateManager.disableLighting();
                GlStateManager.lightModel(2899, Lighting.getBuffer(0.2f, 0.2f, 0.2f, 1.0f));
                for (int integer2 = 0; integer2 < 8; ++integer2) {
                    GlStateManager.disableLight(integer2);
                    GlStateManager.light(16384 + integer2, 4608, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    GlStateManager.light(16384 + integer2, 4611, Lighting.getBuffer(0.0f, 0.0f, 1.0f, 0.0f));
                    if (integer2 == 0) {
                        GlStateManager.light(16384 + integer2, 4609, Lighting.getBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                        GlStateManager.light(16384 + integer2, 4610, Lighting.getBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                    }
                    else {
                        GlStateManager.light(16384 + integer2, 4609, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                        GlStateManager.light(16384 + integer2, 4610, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    }
                }
                GlStateManager.disableColorMaterial();
                GlStateManager.colorMaterial(1032, 5634);
                GlStateManager.disableDepthTest();
                GlStateManager.depthFunc(513);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendFuncSeparate(SourceFactor.ONE, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendEquation(32774);
                GlStateManager.disableFog();
                GlStateManager.fogi(2917, 2048);
                GlStateManager.fogDensity(1.0f);
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(1.0f);
                GlStateManager.fog(2918, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                if (GL.getCapabilities().GL_NV_fog_distance) {
                    GlStateManager.fogi(2917, 34140);
                }
                GlStateManager.polygonOffset(0.0f, 0.0f);
                GlStateManager.disableColorLogicOp();
                GlStateManager.logicOp(5379);
                GlStateManager.disableTexGen(TexGen.S);
                GlStateManager.texGenMode(TexGen.S, 9216);
                GlStateManager.texGenParam(TexGen.S, 9474, Lighting.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexGen.S, 9217, Lighting.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexGen.T);
                GlStateManager.texGenMode(TexGen.T, 9216);
                GlStateManager.texGenParam(TexGen.T, 9474, Lighting.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexGen.T, 9217, Lighting.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexGen.R);
                GlStateManager.texGenMode(TexGen.R, 9216);
                GlStateManager.texGenParam(TexGen.R, 9474, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexGen.R, 9217, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexGen.Q);
                GlStateManager.texGenMode(TexGen.Q, 9216);
                GlStateManager.texGenParam(TexGen.Q, 9474, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexGen.Q, 9217, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.activeTexture(0);
                GlStateManager.texParameter(3553, 10240, 9729);
                GlStateManager.texParameter(3553, 10241, 9986);
                GlStateManager.texParameter(3553, 10242, 10497);
                GlStateManager.texParameter(3553, 10243, 10497);
                GlStateManager.texParameter(3553, 33085, 1000);
                GlStateManager.texParameter(3553, 33083, 1000);
                GlStateManager.texParameter(3553, 33082, -1000);
                GlStateManager.texParameter(3553, 34049, 0.0f);
                GlStateManager.texEnv(8960, 8704, 8448);
                GlStateManager.texEnv(8960, 8705, Lighting.getBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texEnv(8960, 34161, 8448);
                GlStateManager.texEnv(8960, 34162, 8448);
                GlStateManager.texEnv(8960, 34176, 5890);
                GlStateManager.texEnv(8960, 34177, 34168);
                GlStateManager.texEnv(8960, 34178, 34166);
                GlStateManager.texEnv(8960, 34184, 5890);
                GlStateManager.texEnv(8960, 34185, 34168);
                GlStateManager.texEnv(8960, 34186, 34166);
                GlStateManager.texEnv(8960, 34192, 768);
                GlStateManager.texEnv(8960, 34193, 768);
                GlStateManager.texEnv(8960, 34194, 770);
                GlStateManager.texEnv(8960, 34200, 770);
                GlStateManager.texEnv(8960, 34201, 770);
                GlStateManager.texEnv(8960, 34202, 770);
                GlStateManager.texEnv(8960, 34163, 1.0f);
                GlStateManager.texEnv(8960, 3356, 1.0f);
                GlStateManager.disableNormalize();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableRescaleNormal();
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.clearDepth(1.0);
                GlStateManager.lineWidth(1.0f);
                GlStateManager.normal3f(0.0f, 0.0f, 1.0f);
                GlStateManager.polygonMode(1028, 6914);
                GlStateManager.polygonMode(1029, 6914);
            }
            
            @Override
            public void clean() {
            }
        }, 
        PLAYER_SKIN {
            @Override
            public void apply() {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            }
            
            @Override
            public void clean() {
                GlStateManager.disableBlend();
            }
        }, 
        TRANSPARENT_MODEL {
            @Override
            public void apply() {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.15f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569f);
            }
            
            @Override
            public void clean() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.depthMask(true);
            }
        };
        
        public abstract void apply();
        
        public abstract void clean();
    }
}
