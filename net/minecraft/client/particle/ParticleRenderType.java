package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.vertex.BufferBuilder;

public interface ParticleRenderType {
    public static final ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {
        public void begin(final BufferBuilder cuw, final TextureManager dxc) {
            Lighting.turnOff();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            dxc.bind(TextureAtlas.LOCATION_BLOCKS);
            cuw.begin(7, DefaultVertexFormat.PARTICLE);
        }
        
        public void end(final Tesselator cuz) {
            cuz.end();
        }
        
        public String toString() {
            return "TERRAIN_SHEET";
        }
    };
    public static final ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
        public void begin(final BufferBuilder cuw, final TextureManager dxc) {
            Lighting.turnOff();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            dxc.bind(TextureAtlas.LOCATION_PARTICLES);
            cuw.begin(7, DefaultVertexFormat.PARTICLE);
        }
        
        public void end(final Tesselator cuz) {
            cuz.end();
        }
        
        public String toString() {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    public static final ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
        public void begin(final BufferBuilder cuw, final TextureManager dxc) {
            Lighting.turnOff();
            GlStateManager.depthMask(false);
            dxc.bind(TextureAtlas.LOCATION_PARTICLES);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569f);
            cuw.begin(7, DefaultVertexFormat.PARTICLE);
        }
        
        public void end(final Tesselator cuz) {
            cuz.end();
        }
        
        public String toString() {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    public static final ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
        public void begin(final BufferBuilder cuw, final TextureManager dxc) {
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            dxc.bind(TextureAtlas.LOCATION_PARTICLES);
            Lighting.turnOff();
            cuw.begin(7, DefaultVertexFormat.PARTICLE);
        }
        
        public void end(final Tesselator cuz) {
            cuz.end();
        }
        
        public String toString() {
            return "PARTICLE_SHEET_LIT";
        }
    };
    public static final ParticleRenderType CUSTOM = new ParticleRenderType() {
        public void begin(final BufferBuilder cuw, final TextureManager dxc) {
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }
        
        public void end(final Tesselator cuz) {
        }
        
        public String toString() {
            return "CUSTOM";
        }
    };
    public static final ParticleRenderType NO_RENDER = new ParticleRenderType() {
        public void begin(final BufferBuilder cuw, final TextureManager dxc) {
        }
        
        public void end(final Tesselator cuz) {
        }
        
        public String toString() {
            return "NO_RENDER";
        }
    };
    
    void begin(final BufferBuilder cuw, final TextureManager dxc);
    
    void end(final Tesselator cuz);
}
