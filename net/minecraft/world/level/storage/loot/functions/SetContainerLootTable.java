package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;

public class SetContainerLootTable extends LootItemConditionalFunction {
    private final ResourceLocation name;
    private final long seed;
    
    private SetContainerLootTable(final LootItemCondition[] arr, final ResourceLocation qv, final long long3) {
        super(arr);
        this.name = qv;
        this.seed = long3;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.isEmpty()) {
            return bcj;
        }
        final CompoundTag id4 = new CompoundTag();
        id4.putString("LootTable", this.name.toString());
        if (this.seed != 0L) {
            id4.putLong("LootTableSeed", this.seed);
        }
        bcj.getOrCreateTag().put("BlockEntityTag", (Tag)id4);
        return bcj;
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
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerLootTable> {
        protected Serializer() {
            super(new ResourceLocation("set_loot_table"), SetContainerLootTable.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetContainerLootTable cqm, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqm, jsonSerializationContext);
            jsonObject.addProperty("name", cqm.name.toString());
            if (cqm.seed != 0L) {
                jsonObject.addProperty("seed", (Number)cqm.seed);
            }
        }
        
        @Override
        public SetContainerLootTable deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final ResourceLocation qv5 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
            final long long6 = GsonHelper.getAsLong(jsonObject, "seed", 0L);
            return new SetContainerLootTable(arr, qv5, long6, null);
        }
    }
}
