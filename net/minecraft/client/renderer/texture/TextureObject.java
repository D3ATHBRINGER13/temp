package net.minecraft.client.renderer.texture;

import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureObject {
    void pushFilter(final boolean boolean1, final boolean boolean2);
    
    void popFilter();
    
    void load(final ResourceManager xi) throws IOException;
    
    int getId();
    
    default void bind() {
        GlStateManager.bindTexture(this.getId());
    }
    
    default void reset(final TextureManager dxc, final ResourceManager xi, final ResourceLocation qv, final Executor executor) {
        dxc.register(qv, this);
    }
}
