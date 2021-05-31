package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.IntBuffer;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;

public class RenderTarget {
    public int width;
    public int height;
    public int viewWidth;
    public int viewHeight;
    public final boolean useDepth;
    public int frameBufferId;
    public int colorTextureId;
    public int depthBufferId;
    public final float[] clearChannels;
    public int filterMode;
    
    public RenderTarget(final int integer1, final int integer2, final boolean boolean3, final boolean boolean4) {
        this.useDepth = boolean3;
        this.frameBufferId = -1;
        this.colorTextureId = -1;
        this.depthBufferId = -1;
        (this.clearChannels = new float[4])[0] = 1.0f;
        this.clearChannels[1] = 1.0f;
        this.clearChannels[2] = 1.0f;
        this.clearChannels[3] = 0.0f;
        this.resize(integer1, integer2, boolean4);
    }
    
    public void resize(final int integer1, final int integer2, final boolean boolean3) {
        if (!GLX.isUsingFBOs()) {
            this.viewWidth = integer1;
            this.viewHeight = integer2;
            return;
        }
        GlStateManager.enableDepthTest();
        if (this.frameBufferId >= 0) {
            this.destroyBuffers();
        }
        this.createBuffers(integer1, integer2, boolean3);
        GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
    }
    
    public void destroyBuffers() {
        if (!GLX.isUsingFBOs()) {
            return;
        }
        this.unbindRead();
        this.unbindWrite();
        if (this.depthBufferId > -1) {
            GLX.glDeleteRenderbuffers(this.depthBufferId);
            this.depthBufferId = -1;
        }
        if (this.colorTextureId > -1) {
            TextureUtil.releaseTextureId(this.colorTextureId);
            this.colorTextureId = -1;
        }
        if (this.frameBufferId > -1) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
            GLX.glDeleteFramebuffers(this.frameBufferId);
            this.frameBufferId = -1;
        }
    }
    
    public void createBuffers(final int integer1, final int integer2, final boolean boolean3) {
        this.viewWidth = integer1;
        this.viewHeight = integer2;
        this.width = integer1;
        this.height = integer2;
        if (!GLX.isUsingFBOs()) {
            this.clear(boolean3);
            return;
        }
        this.frameBufferId = GLX.glGenFramebuffers();
        this.colorTextureId = TextureUtil.generateTextureId();
        if (this.useDepth) {
            this.depthBufferId = GLX.glGenRenderbuffers();
        }
        this.setFilterMode(9728);
        GlStateManager.bindTexture(this.colorTextureId);
        GlStateManager.texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, null);
        GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.frameBufferId);
        GLX.glFramebufferTexture2D(GLX.GL_FRAMEBUFFER, GLX.GL_COLOR_ATTACHMENT0, 3553, this.colorTextureId, 0);
        if (this.useDepth) {
            GLX.glBindRenderbuffer(GLX.GL_RENDERBUFFER, this.depthBufferId);
            GLX.glRenderbufferStorage(GLX.GL_RENDERBUFFER, 33190, this.width, this.height);
            GLX.glFramebufferRenderbuffer(GLX.GL_FRAMEBUFFER, GLX.GL_DEPTH_ATTACHMENT, GLX.GL_RENDERBUFFER, this.depthBufferId);
        }
        this.checkStatus();
        this.clear(boolean3);
        this.unbindRead();
    }
    
    public void setFilterMode(final int integer) {
        if (GLX.isUsingFBOs()) {
            this.filterMode = integer;
            GlStateManager.bindTexture(this.colorTextureId);
            GlStateManager.texParameter(3553, 10241, integer);
            GlStateManager.texParameter(3553, 10240, integer);
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
            GlStateManager.bindTexture(0);
        }
    }
    
    public void checkStatus() {
        final int integer2 = GLX.glCheckFramebufferStatus(GLX.GL_FRAMEBUFFER);
        if (integer2 == GLX.GL_FRAMEBUFFER_COMPLETE) {
            return;
        }
        if (integer2 == GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        }
        if (integer2 == GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        }
        if (integer2 == GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        }
        if (integer2 == GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        }
        throw new RuntimeException(new StringBuilder().append("glCheckFramebufferStatus returned unknown status:").append(integer2).toString());
    }
    
    public void bindRead() {
        if (GLX.isUsingFBOs()) {
            GlStateManager.bindTexture(this.colorTextureId);
        }
    }
    
    public void unbindRead() {
        if (GLX.isUsingFBOs()) {
            GlStateManager.bindTexture(0);
        }
    }
    
    public void bindWrite(final boolean boolean1) {
        if (GLX.isUsingFBOs()) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.frameBufferId);
            if (boolean1) {
                GlStateManager.viewport(0, 0, this.viewWidth, this.viewHeight);
            }
        }
    }
    
    public void unbindWrite() {
        if (GLX.isUsingFBOs()) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
        }
    }
    
    public void setClearColor(final float float1, final float float2, final float float3, final float float4) {
        this.clearChannels[0] = float1;
        this.clearChannels[1] = float2;
        this.clearChannels[2] = float3;
        this.clearChannels[3] = float4;
    }
    
    public void blitToScreen(final int integer1, final int integer2) {
        this.blitToScreen(integer1, integer2, true);
    }
    
    public void blitToScreen(final int integer1, final int integer2, final boolean boolean3) {
        if (!GLX.isUsingFBOs()) {
            return;
        }
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, integer1, integer2, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager.viewport(0, 0, integer1, integer2);
        GlStateManager.enableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        if (boolean3) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindRead();
        final float float5 = (float)integer1;
        final float float6 = (float)integer2;
        final float float7 = this.viewWidth / (float)this.width;
        final float float8 = this.viewHeight / (float)this.height;
        final Tesselator cuz9 = Tesselator.getInstance();
        final BufferBuilder cuw10 = cuz9.getBuilder();
        cuw10.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw10.vertex(0.0, float6, 0.0).uv(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        cuw10.vertex(float5, float6, 0.0).uv(float7, 0.0).color(255, 255, 255, 255).endVertex();
        cuw10.vertex(float5, 0.0, 0.0).uv(float7, float8).color(255, 255, 255, 255).endVertex();
        cuw10.vertex(0.0, 0.0, 0.0).uv(0.0, float8).color(255, 255, 255, 255).endVertex();
        cuz9.end();
        this.unbindRead();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
    }
    
    public void clear(final boolean boolean1) {
        this.bindWrite(true);
        GlStateManager.clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
        int integer3 = 16384;
        if (this.useDepth) {
            GlStateManager.clearDepth(1.0);
            integer3 |= 0x100;
        }
        GlStateManager.clear(integer3, boolean1);
        this.unbindWrite();
    }
}
