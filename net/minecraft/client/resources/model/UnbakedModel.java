package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import java.util.Collection;

public interface UnbakedModel {
    Collection<ResourceLocation> getDependencies();
    
    Collection<ResourceLocation> getTextures(final Function<ResourceLocation, UnbakedModel> function, final Set<String> set);
    
    @Nullable
    BakedModel bake(final ModelBakery dys, final Function<ResourceLocation, TextureAtlasSprite> function, final ModelState dyv);
}
