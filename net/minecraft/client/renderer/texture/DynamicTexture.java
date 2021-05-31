package net.minecraft.client.renderer.texture;

import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.NativeImage;

public class DynamicTexture extends AbstractTexture implements AutoCloseable {
    private NativeImage pixels;
    
    public DynamicTexture(final NativeImage cuj) {
        this.pixels = cuj;
        TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
        this.upload();
    }
    
    public DynamicTexture(final int integer1, final int integer2, final boolean boolean3) {
        this.pixels = new NativeImage(integer1, integer2, boolean3);
        TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
    }
    
    public void load(final ResourceManager xi) throws IOException {
    }
    
    public void upload() {
        this.bind();
        this.pixels.upload(0, 0, 0, false);
    }
    
    @Nullable
    public NativeImage getPixels() {
        return this.pixels;
    }
    
    public void setPixels(final NativeImage cuj) throws Exception {
        this.pixels.close();
        this.pixels = cuj;
    }
    
    public void close() {
        this.pixels.close();
        this.releaseId();
        this.pixels = null;
    }
}
