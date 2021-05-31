package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class MobEffectTextureManager extends TextureAtlasHolder {
    public MobEffectTextureManager(final TextureManager dxc) {
        super(dxc, TextureAtlas.LOCATION_MOB_EFFECTS, "textures/mob_effect");
    }
    
    @Override
    protected Iterable<ResourceLocation> getResourcesToLoad() {
        return (Iterable<ResourceLocation>)Registry.MOB_EFFECT.keySet();
    }
    
    public TextureAtlasSprite get(final MobEffect aig) {
        return this.getSprite(Registry.MOB_EFFECT.getKey(aig));
    }
}
