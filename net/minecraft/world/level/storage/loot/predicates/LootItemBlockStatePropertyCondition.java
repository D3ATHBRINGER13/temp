package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import java.util.Iterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Map;
import net.minecraft.world.level.block.Block;

public class LootItemBlockStatePropertyCondition implements LootItemCondition {
    private final Block block;
    private final Map<Property<?>, Object> properties;
    private final Predicate<BlockState> composedPredicate;
    
    private LootItemBlockStatePropertyCondition(final Block bmv, final Map<Property<?>, Object> map) {
        this.block = bmv;
        this.properties = (Map<Property<?>, Object>)ImmutableMap.copyOf((Map)map);
        this.composedPredicate = bakePredicate(bmv, map);
    }
    
    private static Predicate<BlockState> bakePredicate(final Block bmv, final Map<Property<?>, Object> map) {
        final int integer3 = map.size();
        if (integer3 == 0) {
            return (Predicate<BlockState>)(bvt -> bvt.getBlock() == bmv);
        }
        if (integer3 == 1) {
            final Map.Entry<Property<?>, Object> entry4 = (Map.Entry<Property<?>, Object>)map.entrySet().iterator().next();
            final Property<?> bww5 = entry4.getKey();
            final Object object6 = entry4.getValue();
            return (Predicate<BlockState>)(bvt -> bvt.getBlock() == bmv && object6.equals(bvt.getValue(bww5)));
        }
        Predicate<BlockState> predicate4 = (Predicate<BlockState>)(bvt -> bvt.getBlock() == bmv);
        for (final Map.Entry<Property<?>, Object> entry5 : map.entrySet()) {
            final Property<?> bww6 = entry5.getKey();
            final Object object7 = entry5.getValue();
            predicate4 = (Predicate<BlockState>)predicate4.and(bvt -> object7.equals(bvt.getValue(bww6)));
        }
        return predicate4;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }
    
    public boolean test(final LootContext coy) {
        final BlockState bvt3 = coy.<BlockState>getParamOrNull(LootContextParams.BLOCK_STATE);
        return bvt3 != null && this.composedPredicate.test(bvt3);
    }
    
    public static Builder hasBlockStateProperties(final Block bmv) {
        return new Builder(bmv);
    }
    
    public static class Builder implements LootItemCondition.Builder {
        private final Block block;
        private final Set<Property<?>> allowedProperties;
        private final Map<Property<?>, Object> properties;
        
        public Builder(final Block bmv) {
            this.properties = (Map<Property<?>, Object>)Maps.newHashMap();
            this.block = bmv;
            (this.allowedProperties = (Set<Property<?>>)Sets.newIdentityHashSet()).addAll((Collection)bmv.getStateDefinition().getProperties());
        }
        
        public <T extends Comparable<T>> Builder withProperty(final Property<T> bww, final T comparable) {
            if (!this.allowedProperties.contains(bww)) {
                throw new IllegalArgumentException(new StringBuilder().append("Block ").append(Registry.BLOCK.getKey(this.block)).append(" does not have property '").append(bww).append("'").toString());
            }
            if (!bww.getPossibleValues().contains(comparable)) {
                throw new IllegalArgumentException(new StringBuilder().append("Block ").append(Registry.BLOCK.getKey(this.block)).append(" property '").append(bww).append("' does not have value '").append(comparable).append("'").toString());
            }
            this.properties.put(bww, comparable);
            return this;
        }
        
        public LootItemCondition build() {
            return new LootItemBlockStatePropertyCondition(this.block, this.properties, null);
        }
    }
    
    public static class Serializer extends LootItemCondition.Serializer<LootItemBlockStatePropertyCondition> {
        private static <T extends Comparable<T>> String valueToString(final Property<T> bww, final Object object) {
            return bww.getName((T)object);
        }
        
        protected Serializer() {
            super(new ResourceLocation("block_state_property"), LootItemBlockStatePropertyCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootItemBlockStatePropertyCondition crj, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("block", Registry.BLOCK.getKey(crj.block).toString());
            final JsonObject jsonObject2 = new JsonObject();
            crj.properties.forEach((bww, object) -> jsonObject2.addProperty(bww.getName(), Serializer.<Comparable>valueToString((Property<Comparable>)bww, object)));
            jsonObject.add("properties", (JsonElement)jsonObject2);
        }
        
        @Override
        public LootItemBlockStatePropertyCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final ResourceLocation qv4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            final Block bmv5 = (Block)Registry.BLOCK.getOptional(qv4).orElseThrow(() -> new IllegalArgumentException(new StringBuilder().append("Can't find block ").append(qv4).toString()));
            final StateDefinition<Block, BlockState> bvu6 = bmv5.getStateDefinition();
            final Map<Property<?>, Object> map7 = (Map<Property<?>, Object>)Maps.newHashMap();
            if (jsonObject.has("properties")) {
                final JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "properties");
                jsonObject2.entrySet().forEach(entry -> {
                    final String string5 = (String)entry.getKey();
                    final Property<?> bww6 = bvu6.getProperty(string5);
                    if (bww6 == null) {
                        throw new IllegalArgumentException(new StringBuilder().append("Block ").append(Registry.BLOCK.getKey(bmv5)).append(" does not have property '").append(string5).append("'").toString());
                    }
                    final String string6 = GsonHelper.convertToString((JsonElement)entry.getValue(), "value");
                    final Object object8 = bww6.getValue(string6).orElseThrow(() -> new IllegalArgumentException(new StringBuilder().append("Block ").append(Registry.BLOCK.getKey(bmv5)).append(" property '").append(string5).append("' does not have value '").append(string6).append("'").toString()));
                    map7.put(bww6, object8);
                });
            }
            return new LootItemBlockStatePropertyCondition(bmv5, map7, null);
        }
    }
}
