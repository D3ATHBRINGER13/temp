package net.minecraft.client.gui.font.glyphs;

import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class BakedGlyph {
    private final ResourceLocation texture;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float left;
    private final float right;
    private final float up;
    private final float down;
    
    public BakedGlyph(final ResourceLocation qv, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8, final float float9) {
        this.texture = qv;
        this.u0 = float2;
        this.u1 = float3;
        this.v0 = float4;
        this.v1 = float5;
        this.left = float6;
        this.right = float7;
        this.up = float8;
        this.down = float9;
    }
    
    public void render(final TextureManager dxc, final boolean boolean2, final float float3, final float float4, final BufferBuilder cuw, final float float6, final float float7, final float float8, final float float9) {
        final int integer11 = 3;
        final float float10 = float3 + this.left;
        final float float11 = float3 + this.right;
        final float float12 = this.up - 3.0f;
        final float float13 = this.down - 3.0f;
        final float float14 = float4 + float12;
        final float float15 = float4 + float13;
        final float float16 = boolean2 ? (1.0f - 0.25f * float12) : 0.0f;
        final float float17 = boolean2 ? (1.0f - 0.25f * float13) : 0.0f;
        cuw.vertex(float10 + float16, float14, 0.0).uv(this.u0, this.v0).color(float6, float7, float8, float9).endVertex();
        cuw.vertex(float10 + float17, float15, 0.0).uv(this.u0, this.v1).color(float6, float7, float8, float9).endVertex();
        cuw.vertex(float11 + float17, float15, 0.0).uv(this.u1, this.v1).color(float6, float7, float8, float9).endVertex();
        cuw.vertex(float11 + float16, float14, 0.0).uv(this.u1, this.v0).color(float6, float7, float8, float9).endVertex();
    }
    
    @Nullable
    public ResourceLocation getTexture() {
        return this.texture;
    }
}
