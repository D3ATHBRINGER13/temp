package net.minecraft.client.renderer.block.model;

import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import com.google.gson.Gson;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Maps;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import java.util.Map;

public class BlockModelDefinition {
    private final Map<String, MultiVariant> variants;
    private MultiPart multiPart;
    
    public static BlockModelDefinition fromStream(final Context a, final Reader reader) {
        return GsonHelper.<BlockModelDefinition>fromJson(a.gson, reader, BlockModelDefinition.class);
    }
    
    public BlockModelDefinition(final Map<String, MultiVariant> map, final MultiPart dos) {
        this.variants = (Map<String, MultiVariant>)Maps.newLinkedHashMap();
        this.multiPart = dos;
        this.variants.putAll((Map)map);
    }
    
    public BlockModelDefinition(final List<BlockModelDefinition> list) {
        this.variants = (Map<String, MultiVariant>)Maps.newLinkedHashMap();
        BlockModelDefinition dof3 = null;
        for (final BlockModelDefinition dof4 : list) {
            if (dof4.isMultiPart()) {
                this.variants.clear();
                dof3 = dof4;
            }
            this.variants.putAll((Map)dof4.variants);
        }
        if (dof3 != null) {
            this.multiPart = dof3.multiPart;
        }
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BlockModelDefinition) {
            final BlockModelDefinition dof3 = (BlockModelDefinition)object;
            if (this.variants.equals(dof3.variants)) {
                return this.isMultiPart() ? this.multiPart.equals(dof3.multiPart) : (!dof3.isMultiPart());
            }
        }
        return false;
    }
    
    public int hashCode() {
        return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
    }
    
    public Map<String, MultiVariant> getVariants() {
        return this.variants;
    }
    
    public boolean isMultiPart() {
        return this.multiPart != null;
    }
    
    public MultiPart getMultiPart() {
        return this.multiPart;
    }
    
    public static final class Context {
        protected final Gson gson;
        private StateDefinition<Block, BlockState> definition;
        
        public Context() {
            this.gson = new GsonBuilder().registerTypeAdapter((Type)BlockModelDefinition.class, new Deserializer()).registerTypeAdapter((Type)Variant.class, new Variant.Deserializer()).registerTypeAdapter((Type)MultiVariant.class, new MultiVariant.Deserializer()).registerTypeAdapter((Type)MultiPart.class, new MultiPart.Deserializer(this)).registerTypeAdapter((Type)Selector.class, new Selector.Deserializer()).create();
        }
        
        public StateDefinition<Block, BlockState> getDefinition() {
            return this.definition;
        }
        
        public void setDefinition(final StateDefinition<Block, BlockState> bvu) {
            this.definition = bvu;
        }
    }
    
    public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
        public BlockModelDefinition deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
            final Map<String, MultiVariant> map6 = this.getVariants(jsonDeserializationContext, jsonObject5);
            final MultiPart dos7 = this.getMultiPart(jsonDeserializationContext, jsonObject5);
            if (map6.isEmpty() && (dos7 == null || dos7.getMultiVariants().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new BlockModelDefinition(map6, dos7);
        }
        
        protected Map<String, MultiVariant> getVariants(final JsonDeserializationContext jsonDeserializationContext, final JsonObject jsonObject) {
            final Map<String, MultiVariant> map4 = (Map<String, MultiVariant>)Maps.newHashMap();
            if (jsonObject.has("variants")) {
                final JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "variants");
                for (final Map.Entry<String, JsonElement> entry7 : jsonObject2.entrySet()) {
                    map4.put(entry7.getKey(), jsonDeserializationContext.deserialize((JsonElement)entry7.getValue(), (Type)MultiVariant.class));
                }
            }
            return map4;
        }
        
        @Nullable
        protected MultiPart getMultiPart(final JsonDeserializationContext jsonDeserializationContext, final JsonObject jsonObject) {
            if (!jsonObject.has("multipart")) {
                return null;
            }
            final JsonArray jsonArray4 = GsonHelper.getAsJsonArray(jsonObject, "multipart");
            return (MultiPart)jsonDeserializationContext.deserialize((JsonElement)jsonArray4, (Type)MultiPart.class);
        }
    }
}
