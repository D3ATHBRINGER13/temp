package net.minecraft.tags;

import javax.annotation.Nullable;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import java.util.Optional;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Random;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.function.Function;
import java.util.Iterator;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class Tag<T> {
    private final ResourceLocation id;
    private final Set<T> values;
    private final Collection<Entry<T>> source;
    
    public Tag(final ResourceLocation qv) {
        this.id = qv;
        this.values = (Set<T>)Collections.emptySet();
        this.source = (Collection<Entry<T>>)Collections.emptyList();
    }
    
    public Tag(final ResourceLocation qv, final Collection<Entry<T>> collection, final boolean boolean3) {
        this.id = qv;
        this.values = (Set<T>)(boolean3 ? Sets.newLinkedHashSet() : Sets.newHashSet());
        this.source = collection;
        for (final Entry<T> b6 : collection) {
            b6.build((java.util.Collection<T>)this.values);
        }
    }
    
    public JsonObject serializeToJson(final Function<T, ResourceLocation> function) {
        final JsonObject jsonObject3 = new JsonObject();
        final JsonArray jsonArray4 = new JsonArray();
        for (final Entry<T> b6 : this.source) {
            b6.serializeTo(jsonArray4, function);
        }
        jsonObject3.addProperty("replace", Boolean.valueOf(false));
        jsonObject3.add("values", (JsonElement)jsonArray4);
        return jsonObject3;
    }
    
    public boolean contains(final T object) {
        return this.values.contains(object);
    }
    
    public Collection<T> getValues() {
        return (Collection<T>)this.values;
    }
    
    public Collection<Entry<T>> getSource() {
        return this.source;
    }
    
    public T getRandomElement(final Random random) {
        final List<T> list3 = (List<T>)Lists.newArrayList((Iterable)this.getValues());
        return (T)list3.get(random.nextInt(list3.size()));
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public static class Builder<T> {
        private final Set<Entry<T>> values;
        private boolean ordered;
        
        public Builder() {
            this.values = (Set<Entry<T>>)Sets.newLinkedHashSet();
        }
        
        public static <T> Builder<T> tag() {
            return new Builder<T>();
        }
        
        public Builder<T> add(final Entry<T> b) {
            this.values.add(b);
            return this;
        }
        
        public Builder<T> add(final T object) {
            this.values.add(new ValuesEntry((java.util.Collection<Object>)Collections.singleton((Object)object)));
            return this;
        }
        
        @SafeVarargs
        public final Builder<T> add(final T... arr) {
            this.values.add(new ValuesEntry((java.util.Collection<Object>)Lists.newArrayList((Object[])arr)));
            return this;
        }
        
        public Builder<T> addTag(final Tag<T> zg) {
            this.values.add(new TagEntry((Tag<Object>)zg));
            return this;
        }
        
        public Builder<T> keepOrder(final boolean boolean1) {
            this.ordered = boolean1;
            return this;
        }
        
        public boolean canBuild(final Function<ResourceLocation, Tag<T>> function) {
            for (final Entry<T> b4 : this.values) {
                if (!b4.canBuild(function)) {
                    return false;
                }
            }
            return true;
        }
        
        public Tag<T> build(final ResourceLocation qv) {
            return new Tag<T>(qv, (java.util.Collection<Entry<T>>)this.values, this.ordered);
        }
        
        public Builder<T> addFromJson(final Function<ResourceLocation, Optional<T>> function, final JsonObject jsonObject) {
            final JsonArray jsonArray4 = GsonHelper.getAsJsonArray(jsonObject, "values");
            final List<Entry<T>> list5 = (List<Entry<T>>)Lists.newArrayList();
            for (final JsonElement jsonElement7 : jsonArray4) {
                final String string8 = GsonHelper.convertToString(jsonElement7, "value");
                if (string8.startsWith("#")) {
                    list5.add(new TagEntry(new ResourceLocation(string8.substring(1))));
                }
                else {
                    final ResourceLocation qv9 = new ResourceLocation(string8);
                    list5.add(new ValuesEntry((java.util.Collection<Object>)Collections.singleton(((Optional)function.apply((Object)qv9)).orElseThrow(() -> new JsonParseException(new StringBuilder().append("Unknown value '").append((Object)qv9).append("'").toString())))));
                }
            }
            if (GsonHelper.getAsBoolean(jsonObject, "replace", false)) {
                this.values.clear();
            }
            this.values.addAll((Collection)list5);
            return this;
        }
    }
    
    public interface Entry<T> {
        default boolean canBuild(final Function<ResourceLocation, Tag<T>> function) {
            return true;
        }
        
        void build(final Collection<T> collection);
        
        void serializeTo(final JsonArray jsonArray, final Function<T, ResourceLocation> function);
    }
    
    public interface Entry<T> {
        default boolean canBuild(final Function<ResourceLocation, Tag<T>> function) {
            return true;
        }
        
        void build(final Collection<T> collection);
        
        void serializeTo(final JsonArray jsonArray, final Function<T, ResourceLocation> function);
    }
    
    public static class ValuesEntry<T> implements Entry<T> {
        private final Collection<T> values;
        
        public ValuesEntry(final Collection<T> collection) {
            this.values = collection;
        }
        
        public void build(final Collection<T> collection) {
            collection.addAll((Collection)this.values);
        }
        
        public void serializeTo(final JsonArray jsonArray, final Function<T, ResourceLocation> function) {
            for (final T object5 : this.values) {
                final ResourceLocation qv6 = (ResourceLocation)function.apply(object5);
                if (qv6 == null) {
                    throw new IllegalStateException("Unable to serialize an anonymous value to json!");
                }
                jsonArray.add(qv6.toString());
            }
        }
        
        public Collection<T> getValues() {
            return this.values;
        }
    }
    
    public static class TagEntry<T> implements Entry<T> {
        @Nullable
        private final ResourceLocation id;
        @Nullable
        private Tag<T> tag;
        
        public TagEntry(final ResourceLocation qv) {
            this.id = qv;
        }
        
        public TagEntry(final Tag<T> zg) {
            this.id = zg.getId();
            this.tag = zg;
        }
        
        public boolean canBuild(final Function<ResourceLocation, Tag<T>> function) {
            if (this.tag == null) {
                this.tag = (Tag<T>)function.apply(this.id);
            }
            return this.tag != null;
        }
        
        public void build(final Collection<T> collection) {
            if (this.tag == null) {
                throw new IllegalStateException("Cannot build unresolved tag entry");
            }
            collection.addAll((Collection)this.tag.getValues());
        }
        
        public ResourceLocation getId() {
            if (this.tag != null) {
                return this.tag.getId();
            }
            if (this.id != null) {
                return this.id;
            }
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
        }
        
        public void serializeTo(final JsonArray jsonArray, final Function<T, ResourceLocation> function) {
            jsonArray.add(new StringBuilder().append("#").append(this.getId()).toString());
        }
    }
    
    public static class TagEntry<T> implements Entry<T> {
        @Nullable
        private final ResourceLocation id;
        @Nullable
        private Tag<T> tag;
        
        public TagEntry(final ResourceLocation qv) {
            this.id = qv;
        }
        
        public TagEntry(final Tag<T> zg) {
            this.id = zg.getId();
            this.tag = zg;
        }
        
        public boolean canBuild(final Function<ResourceLocation, Tag<T>> function) {
            if (this.tag == null) {
                this.tag = (Tag<T>)function.apply(this.id);
            }
            return this.tag != null;
        }
        
        public void build(final Collection<T> collection) {
            if (this.tag == null) {
                throw new IllegalStateException("Cannot build unresolved tag entry");
            }
            collection.addAll((Collection)this.tag.getValues());
        }
        
        public ResourceLocation getId() {
            if (this.tag != null) {
                return this.tag.getId();
            }
            if (this.id != null) {
                return this.id;
            }
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
        }
        
        public void serializeTo(final JsonArray jsonArray, final Function<T, ResourceLocation> function) {
            jsonArray.add(new StringBuilder().append("#").append(this.getId()).toString());
        }
    }
}
