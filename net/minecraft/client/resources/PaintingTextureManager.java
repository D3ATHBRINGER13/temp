package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.decoration.Motive;
import com.google.common.collect.Iterables;
import java.util.Collections;
import net.minecraft.core.Registry;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PaintingTextureManager extends TextureAtlasHolder {
    private static final ResourceLocation BACK_SPRITE_LOCATION;
    
    public PaintingTextureManager(final TextureManager dxc) {
        super(dxc, TextureAtlas.LOCATION_PAINTINGS, "textures/painting");
    }
    
    @Override
    protected Iterable<ResourceLocation> getResourcesToLoad() {
        return (Iterable<ResourceLocation>)Iterables.concat((Iterable)Registry.MOTIVE.keySet(), (Iterable)Collections.singleton(PaintingTextureManager.BACK_SPRITE_LOCATION));
    }
    
    public TextureAtlasSprite get(final Motive atp) {
        return this.getSprite(Registry.MOTIVE.getKey(atp));
    }
    
    public TextureAtlasSprite getBackSprite() {
        return this.getSprite(PaintingTextureManager.BACK_SPRITE_LOCATION);
    }
    
    static {
        BACK_SPRITE_LOCATION = new ResourceLocation("back");
    }
}
