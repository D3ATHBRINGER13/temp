package net.minecraft.client.resources;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public abstract class TextureAtlasHolder extends SimplePreparableReloadListener<TextureAtlas.Preparations> implements AutoCloseable {
    private final TextureAtlas textureAtlas;
    
    public TextureAtlasHolder(final TextureManager dxc, final ResourceLocation qv, final String string) {
        dxc.register(qv, this.textureAtlas = new TextureAtlas(string));
    }
    
    protected abstract Iterable<ResourceLocation> getResourcesToLoad();
    
    protected TextureAtlasSprite getSprite(final ResourceLocation qv) {
        return this.textureAtlas.getSprite(qv);
    }
    
    @Override
    protected TextureAtlas.Preparations prepare(final ResourceManager xi, final ProfilerFiller agn) {
        agn.startTick();
        agn.push("stitching");
        final TextureAtlas.Preparations a4 = this.textureAtlas.prepareToStitch(xi, this.getResourcesToLoad(), agn);
        agn.pop();
        agn.endTick();
        return a4;
    }
    
    @Override
    protected void apply(final TextureAtlas.Preparations a, final ResourceManager xi, final ProfilerFiller agn) {
        agn.startTick();
        agn.push("upload");
        this.textureAtlas.reload(a);
        agn.pop();
        agn.endTick();
    }
    
    public void close() {
        this.textureAtlas.clearTextureData();
    }
}
