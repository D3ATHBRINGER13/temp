package net.minecraft.client.renderer.texture;

import net.minecraft.Util;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.concurrent.CompletableFuture;

public class PreloadedTexture extends SimpleTexture {
    private CompletableFuture<TextureImage> future;
    
    public PreloadedTexture(final ResourceManager xi, final ResourceLocation qv, final Executor executor) {
        super(qv);
        this.future = (CompletableFuture<TextureImage>)CompletableFuture.supplyAsync(() -> TextureImage.load(xi, qv), executor);
    }
    
    @Override
    protected TextureImage getTextureImage(final ResourceManager xi) {
        if (this.future != null) {
            final TextureImage a3 = (TextureImage)this.future.join();
            this.future = null;
            return a3;
        }
        return TextureImage.load(xi, this.location);
    }
    
    public CompletableFuture<Void> getFuture() {
        return (CompletableFuture<Void>)((this.future == null) ? CompletableFuture.completedFuture(null) : this.future.thenApply(a -> null));
    }
    
    public void reset(final TextureManager dxc, final ResourceManager xi, final ResourceLocation qv, final Executor executor) {
        (this.future = (CompletableFuture<TextureImage>)CompletableFuture.supplyAsync(() -> TextureImage.load(xi, this.location), Util.backgroundExecutor())).thenRunAsync(() -> dxc.register(this.location, this), executor);
    }
}
