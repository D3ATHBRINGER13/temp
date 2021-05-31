package net.minecraft.client.resources.model;

import java.util.Collections;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;
import java.util.Random;
import net.minecraft.core.Direction;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;

public class BuiltInModel implements BakedModel {
    private final ItemTransforms itemTransforms;
    private final ItemOverrides overrides;
    private final TextureAtlasSprite particleTexture;
    
    public BuiltInModel(final ItemTransforms dom, final ItemOverrides dok, final TextureAtlasSprite dxb) {
        this.itemTransforms = dom;
        this.overrides = dok;
        this.particleTexture = dxb;
    }
    
    public List<BakedQuad> getQuads(@Nullable final BlockState bvt, @Nullable final Direction fb, final Random random) {
        return (List<BakedQuad>)Collections.emptyList();
    }
    
    public boolean useAmbientOcclusion() {
        return false;
    }
    
    public boolean isGui3d() {
        return true;
    }
    
    public boolean isCustomRenderer() {
        return true;
    }
    
    public TextureAtlasSprite getParticleIcon() {
        return this.particleTexture;
    }
    
    public ItemTransforms getTransforms() {
        return this.itemTransforms;
    }
    
    public ItemOverrides getOverrides() {
        return this.overrides;
    }
}
