package net.minecraft.advancements.critereon;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import net.minecraft.tags.Tag;
import net.minecraft.core.Registry;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.EntityType;
import com.google.common.base.Joiner;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY;
    private static final Joiner COMMA_JOINER;
    
    public abstract boolean matches(final EntityType<?> ais);
    
    public abstract JsonElement serializeToJson();
    
    public static EntityTypePredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EntityTypePredicate.ANY;
        }
        final String string2 = GsonHelper.convertToString(jsonElement, "type");
        if (string2.startsWith("#")) {
            final ResourceLocation qv3 = new ResourceLocation(string2.substring(1));
            final Tag<EntityType<?>> zg4 = EntityTypeTags.getAllTags().getTagOrEmpty(qv3);
            return new TagPredicate(zg4);
        }
        final ResourceLocation qv3 = new ResourceLocation(string2);
        final EntityType<?> ais4 = Registry.ENTITY_TYPE.getOptional(qv3).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown entity type '").append(qv3).append("', valid types are: ").append(EntityTypePredicate.COMMA_JOINER.join((Iterable)Registry.ENTITY_TYPE.keySet())).toString()));
        return new TypePredicate(ais4);
    }
    
    public static EntityTypePredicate of(final EntityType<?> ais) {
        return new TypePredicate(ais);
    }
    
    public static EntityTypePredicate of(final Tag<EntityType<?>> zg) {
        return new TagPredicate(zg);
    }
    
    static {
        ANY = new EntityTypePredicate() {
            @Override
            public boolean matches(final EntityType<?> ais) {
                return true;
            }
            
            @Override
            public JsonElement serializeToJson() {
                return (JsonElement)JsonNull.INSTANCE;
            }
        };
        COMMA_JOINER = Joiner.on(", ");
    }
    
    static class TypePredicate extends EntityTypePredicate {
        private final EntityType<?> type;
        
        public TypePredicate(final EntityType<?> ais) {
            this.type = ais;
        }
        
        @Override
        public boolean matches(final EntityType<?> ais) {
            return this.type == ais;
        }
        
        @Override
        public JsonElement serializeToJson() {
            return (JsonElement)new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
        }
    }
    
    static class TagPredicate extends EntityTypePredicate {
        private final Tag<EntityType<?>> tag;
        
        public TagPredicate(final Tag<EntityType<?>> zg) {
            this.tag = zg;
        }
        
        @Override
        public boolean matches(final EntityType<?> ais) {
            return this.tag.contains(ais);
        }
        
        @Override
        public JsonElement serializeToJson() {
            return (JsonElement)new JsonPrimitive("#" + this.tag.getId().toString());
        }
    }
}
