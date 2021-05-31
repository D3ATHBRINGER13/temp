package net.minecraft.client.resources.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.client.renderer.block.model.BreakingQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;

public class SimpleBakedModel implements BakedModel {
    protected final List<BakedQuad> unculledFaces;
    protected final Map<Direction, List<BakedQuad>> culledFaces;
    protected final boolean hasAmbientOcclusion;
    protected final boolean isGui3d;
    protected final TextureAtlasSprite particleIcon;
    protected final ItemTransforms transforms;
    protected final ItemOverrides overrides;
    
    public SimpleBakedModel(final List<BakedQuad> list, final Map<Direction, List<BakedQuad>> map, final boolean boolean3, final boolean boolean4, final TextureAtlasSprite dxb, final ItemTransforms dom, final ItemOverrides dok) {
        this.unculledFaces = list;
        this.culledFaces = map;
        this.hasAmbientOcclusion = boolean3;
        this.isGui3d = boolean4;
        this.particleIcon = dxb;
        this.transforms = dom;
        this.overrides = dok;
    }
    
    public List<BakedQuad> getQuads(@Nullable final BlockState bvt, @Nullable final Direction fb, final Random random) {
        return (List<BakedQuad>)((fb == null) ? this.unculledFaces : ((List)this.culledFaces.get(fb)));
    }
    
    public boolean useAmbientOcclusion() {
        return this.hasAmbientOcclusion;
    }
    
    public boolean isGui3d() {
        return this.isGui3d;
    }
    
    public boolean isCustomRenderer() {
        return false;
    }
    
    public TextureAtlasSprite getParticleIcon() {
        return this.particleIcon;
    }
    
    public ItemTransforms getTransforms() {
        return this.transforms;
    }
    
    public ItemOverrides getOverrides() {
        return this.overrides;
    }
    
    public static class Builder {
        private final List<BakedQuad> unculledFaces;
        private final Map<Direction, List<BakedQuad>> culledFaces;
        private final ItemOverrides overrides;
        private final boolean hasAmbientOcclusion;
        private TextureAtlasSprite particleIcon;
        private final boolean isGui3d;
        private final ItemTransforms transforms;
        
        public Builder(final BlockModel doe, final ItemOverrides dok) {
            this(doe.hasAmbientOcclusion(), doe.isGui3d(), doe.getTransforms(), dok);
        }
        
        public Builder(final BlockState bvt, final BakedModel dyp, final TextureAtlasSprite dxb, final Random random, final long long5) {
            this(dyp.useAmbientOcclusion(), dyp.isGui3d(), dyp.getTransforms(), dyp.getOverrides());
            this.particleIcon = dyp.getParticleIcon();
            for (final Direction fb11 : Direction.values()) {
                random.setSeed(long5);
                for (final BakedQuad dnz13 : dyp.getQuads(bvt, fb11, random)) {
                    this.addCulledFace(fb11, new BreakingQuad(dnz13, dxb));
                }
            }
            random.setSeed(long5);
            for (final BakedQuad dnz14 : dyp.getQuads(bvt, null, random)) {
                this.addUnculledFace(new BreakingQuad(dnz14, dxb));
            }
        }
        
        private Builder(final boolean boolean1, final boolean boolean2, final ItemTransforms dom, final ItemOverrides dok) {
            this.unculledFaces = (List<BakedQuad>)Lists.newArrayList();
            this.culledFaces = (Map<Direction, List<BakedQuad>>)Maps.newEnumMap((Class)Direction.class);
            for (final Direction fb9 : Direction.values()) {
                this.culledFaces.put(fb9, Lists.newArrayList());
            }
            this.overrides = dok;
            this.hasAmbientOcclusion = boolean1;
            this.isGui3d = boolean2;
            this.transforms = dom;
        }
        
        public Builder addCulledFace(final Direction fb, final BakedQuad dnz) {
            ((List)this.culledFaces.get(fb)).add(dnz);
            return this;
        }
        
        public Builder addUnculledFace(final BakedQuad dnz) {
            this.unculledFaces.add(dnz);
            return this;
        }
        
        public Builder particle(final TextureAtlasSprite dxb) {
            this.particleIcon = dxb;
            return this;
        }
        
        public BakedModel build() {
            if (this.particleIcon == null) {
                throw new RuntimeException("Missing particle!");
            }
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
        }
    }
}
