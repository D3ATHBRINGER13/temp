package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
    protected final LootPoolEntryContainer[] children;
    private final ComposableEntryContainer composedChildren;
    
    protected CompositeEntryBase(final LootPoolEntryContainer[] arr, final LootItemCondition[] arr) {
        super(arr);
        this.children = arr;
        this.composedChildren = this.compose(arr);
    }
    
    @Override
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        super.validate(cpc, function, set, cqx);
        if (this.children.length == 0) {
            cpc.reportProblem("Empty children list");
        }
        for (int integer6 = 0; integer6 < this.children.length; ++integer6) {
            this.children[integer6].validate(cpc.forChild(new StringBuilder().append(".entry[").append(integer6).append("]").toString()), function, set, cqx);
        }
    }
    
    protected abstract ComposableEntryContainer compose(final ComposableEntryContainer[] arr);
    
    public final boolean expand(final LootContext coy, final Consumer<LootPoolEntry> consumer) {
        return this.canRun(coy) && this.composedChildren.expand(coy, consumer);
    }
    
    public static <T extends CompositeEntryBase> Serializer<T> createSerializer(final ResourceLocation qv, final Class<T> class2, final CompositeEntryConstructor<T> a) {
        return new Serializer<T>(qv, class2) {
            @Override
            protected T deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootPoolEntryContainer[] arr, final LootItemCondition[] arr) {
                return a.create(arr, arr);
            }
        };
    }
    
    public abstract static class Serializer<T extends CompositeEntryBase> extends LootPoolEntryContainer.Serializer<T> {
        public Serializer(final ResourceLocation qv, final Class<T> class2) {
            super(qv, class2);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final T cpj, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("children", jsonSerializationContext.serialize(cpj.children));
        }
        
        @Override
        public final T deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final LootPoolEntryContainer[] arr2 = GsonHelper.<LootPoolEntryContainer[]>getAsObject(jsonObject, "children", jsonDeserializationContext, (java.lang.Class<? extends LootPoolEntryContainer[]>)LootPoolEntryContainer[].class);
            return this.deserialize(jsonObject, jsonDeserializationContext, arr2, arr);
        }
        
        protected abstract T deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootPoolEntryContainer[] arr, final LootItemCondition[] arr);
    }
    
    @FunctionalInterface
    public interface CompositeEntryConstructor<T extends CompositeEntryBase> {
        T create(final LootPoolEntryContainer[] arr, final LootItemCondition[] arr);
    }
}
