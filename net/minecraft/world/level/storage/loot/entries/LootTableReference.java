package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;

public class LootTableReference extends LootPoolSingletonContainer {
    private final ResourceLocation name;
    
    private LootTableReference(final ResourceLocation qv, final int integer2, final int integer3, final LootItemCondition[] arr, final LootItemFunction[] arr) {
        super(integer2, integer3, arr, arr);
        this.name = qv;
    }
    
    public void createItemStack(final Consumer<ItemStack> consumer, final LootContext coy) {
        final LootTable cpb4 = coy.getLootTables().get(this.name);
        cpb4.getRandomItemsRaw(coy, consumer);
    }
    
    @Override
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        if (set.contains(this.name)) {
            cpc.reportProblem(new StringBuilder().append("Table ").append(this.name).append(" is recursively called").toString());
            return;
        }
        super.validate(cpc, function, set, cqx);
        final LootTable cpb6 = (LootTable)function.apply(this.name);
        if (cpb6 == null) {
            cpc.reportProblem(new StringBuilder().append("Unknown loot table called ").append(this.name).toString());
        }
        else {
            final Set<ResourceLocation> set2 = (Set<ResourceLocation>)ImmutableSet.builder().addAll((Iterable)set).add(this.name).build();
            cpb6.validate(cpc.forChild(new StringBuilder().append("->{").append(this.name).append("}").toString()), function, set2, cqx);
        }
    }
    
    public static Builder<?> lootTableReference(final ResourceLocation qv) {
        return LootPoolSingletonContainer.simpleBuilder((integer2, integer3, arr, arr) -> new LootTableReference(qv, integer2, integer3, arr, arr));
    }
    
    public static class Serializer extends LootPoolSingletonContainer.Serializer<LootTableReference> {
        public Serializer() {
            super(new ResourceLocation("loot_table"), LootTableReference.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootTableReference cps, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cps, jsonSerializationContext);
            jsonObject.addProperty("name", cps.name.toString());
        }
        
        @Override
        protected LootTableReference deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final int integer3, final int integer4, final LootItemCondition[] arr, final LootItemFunction[] arr) {
            final ResourceLocation qv8 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
            return new LootTableReference(qv8, integer3, integer4, arr, arr, null);
        }
    }
}
