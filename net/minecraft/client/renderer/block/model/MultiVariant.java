package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.resources.model.UnbakedModel;

public class MultiVariant implements UnbakedModel {
    private final List<Variant> variants;
    
    public MultiVariant(final List<Variant> list) {
        this.variants = list;
    }
    
    public List<Variant> getVariants() {
        return this.variants;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MultiVariant) {
            final MultiVariant don3 = (MultiVariant)object;
            return this.variants.equals(don3.variants);
        }
        return false;
    }
    
    public int hashCode() {
        return this.variants.hashCode();
    }
    
    public Collection<ResourceLocation> getDependencies() {
        return (Collection<ResourceLocation>)this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
    }
    
    public Collection<ResourceLocation> getTextures(final Function<ResourceLocation, UnbakedModel> function, final Set<String> set) {
        return (Collection<ResourceLocation>)this.getVariants().stream().map(Variant::getModelLocation).distinct().flatMap(qv -> ((UnbakedModel)function.apply(qv)).getTextures(function, set).stream()).collect(Collectors.toSet());
    }
    
    @Nullable
    public BakedModel bake(final ModelBakery dys, final Function<ResourceLocation, TextureAtlasSprite> function, final ModelState dyv) {
        if (this.getVariants().isEmpty()) {
            return null;
        }
        final WeightedBakedModel.Builder a5 = new WeightedBakedModel.Builder();
        for (final Variant doo7 : this.getVariants()) {
            final BakedModel dyp8 = dys.bake(doo7.getModelLocation(), doo7);
            a5.add(dyp8, doo7.getWeight());
        }
        return a5.build();
    }
    
    public static class Deserializer implements JsonDeserializer<MultiVariant> {
        public MultiVariant deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final List<Variant> list5 = (List<Variant>)Lists.newArrayList();
            if (jsonElement.isJsonArray()) {
                final JsonArray jsonArray6 = jsonElement.getAsJsonArray();
                if (jsonArray6.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (final JsonElement jsonElement2 : jsonArray6) {
                    list5.add(jsonDeserializationContext.deserialize(jsonElement2, (Type)Variant.class));
                }
            }
            else {
                list5.add(jsonDeserializationContext.deserialize(jsonElement, (Type)Variant.class));
            }
            return new MultiVariant(list5);
        }
    }
}
