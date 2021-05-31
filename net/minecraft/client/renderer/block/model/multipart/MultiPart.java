package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import com.google.gson.JsonDeserializer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import java.util.Collection;
import java.util.Objects;
import java.util.Iterator;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.MultiVariant;
import java.util.Set;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.client.resources.model.UnbakedModel;

public class MultiPart implements UnbakedModel {
    private final StateDefinition<Block, BlockState> definition;
    private final List<Selector> selectors;
    
    public MultiPart(final StateDefinition<Block, BlockState> bvu, final List<Selector> list) {
        this.definition = bvu;
        this.selectors = list;
    }
    
    public List<Selector> getSelectors() {
        return this.selectors;
    }
    
    public Set<MultiVariant> getMultiVariants() {
        final Set<MultiVariant> set2 = (Set<MultiVariant>)Sets.newHashSet();
        for (final Selector dou4 : this.selectors) {
            set2.add(dou4.getVariant());
        }
        return set2;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MultiPart) {
            final MultiPart dos3 = (MultiPart)object;
            return Objects.equals(this.definition, dos3.definition) && Objects.equals(this.selectors, dos3.selectors);
        }
        return false;
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.definition, this.selectors });
    }
    
    public Collection<ResourceLocation> getDependencies() {
        return (Collection<ResourceLocation>)this.getSelectors().stream().flatMap(dou -> dou.getVariant().getDependencies().stream()).collect(Collectors.toSet());
    }
    
    public Collection<ResourceLocation> getTextures(final Function<ResourceLocation, UnbakedModel> function, final Set<String> set) {
        return (Collection<ResourceLocation>)this.getSelectors().stream().flatMap(dou -> dou.getVariant().getTextures(function, set).stream()).collect(Collectors.toSet());
    }
    
    @Nullable
    public BakedModel bake(final ModelBakery dys, final Function<ResourceLocation, TextureAtlasSprite> function, final ModelState dyv) {
        final MultiPartBakedModel.Builder a5 = new MultiPartBakedModel.Builder();
        for (final Selector dou7 : this.getSelectors()) {
            final BakedModel dyp8 = dou7.getVariant().bake(dys, function, dyv);
            if (dyp8 != null) {
                a5.add(dou7.getPredicate(this.definition), dyp8);
            }
        }
        return a5.build();
    }
    
    public static class Deserializer implements JsonDeserializer<MultiPart> {
        private final BlockModelDefinition.Context context;
        
        public Deserializer(final BlockModelDefinition.Context a) {
            this.context = a;
        }
        
        public MultiPart deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new MultiPart(this.context.getDefinition(), this.getSelectors(jsonDeserializationContext, jsonElement.getAsJsonArray()));
        }
        
        private List<Selector> getSelectors(final JsonDeserializationContext jsonDeserializationContext, final JsonArray jsonArray) {
            final List<Selector> list4 = (List<Selector>)Lists.newArrayList();
            for (final JsonElement jsonElement6 : jsonArray) {
                list4.add(jsonDeserializationContext.deserialize(jsonElement6, (Type)Selector.class));
            }
            return list4;
        }
    }
}
