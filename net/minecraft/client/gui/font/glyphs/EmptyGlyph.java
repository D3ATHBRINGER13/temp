package net.minecraft.client.gui.font.glyphs;

import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class EmptyGlyph extends BakedGlyph {
    public EmptyGlyph() {
        super(new ResourceLocation(""), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final TextureManager dxc, final boolean boolean2, final float float3, final float float4, final BufferBuilder cuw, final float float6, final float float7, final float float8, final float float9) {
    }
    
    @Nullable
    @Override
    public ResourceLocation getTexture() {
        return null;
    }
}
