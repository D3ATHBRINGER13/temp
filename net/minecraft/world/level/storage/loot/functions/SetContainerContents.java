package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import java.util.List;

public class SetContainerContents extends LootItemConditionalFunction {
    private final List<LootPoolEntryContainer> entries;
    
    private SetContainerContents(final LootItemCondition[] arr, final List<LootPoolEntryContainer> list) {
        super(arr);
        this.entries = (List<LootPoolEntryContainer>)ImmutableList.copyOf((Collection)list);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.isEmpty()) {
            return bcj;
        }
        final NonNullList<ItemStack> fk4 = NonNullList.<ItemStack>create();
        this.entries.forEach(cpq -> cpq.expand(coy, (Consumer<LootPoolEntry>)(cpp -> cpp.createItemStack(LootTable.createStackSplitter((Consumer<ItemStack>)fk4::add), coy))));
        final CompoundTag id5 = new CompoundTag();
        ContainerHelper.saveAllItems(id5, fk4);
        final CompoundTag id6 = bcj.getOrCreateTag();
        id6.put("BlockEntityTag", (Tag)id5.merge(id6.getCompound("BlockEntityTag")));
        return bcj;
    }
    
    @Override
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        super.validate(cpc, function, set, cqx);
        for (int integer6 = 0; integer6 < this.entries.size(); ++integer6) {
            ((LootPoolEntryContainer)this.entries.get(integer6)).validate(cpc.forChild(new StringBuilder().append(".entry[").append(integer6).append("]").toString()), function, set, cqx);
        }
    }
    
    public static Builder setContents() {
        return new Builder();
    }
    
    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries;
        
        public Builder() {
            this.entries = (List<LootPoolEntryContainer>)Lists.newArrayList();
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
        
        public Builder withEntry(final LootPoolEntryContainer.Builder<?> a) {
            this.entries.add(a.build());
            return this;
        }
        
        public LootItemFunction build() {
            return new SetContainerContents(this.getConditions(), this.entries, null);
        }
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerContents> {
        protected Serializer() {
            super(new ResourceLocation("set_contents"), SetContainerContents.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetContainerContents cql, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cql, jsonSerializationContext);
            jsonObject.add("entries", jsonSerializationContext.serialize(cql.entries));
        }
        
        @Override
        public SetContainerContents deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final LootPoolEntryContainer[] arr2 = GsonHelper.<LootPoolEntryContainer[]>getAsObject(jsonObject, "entries", jsonDeserializationContext, (java.lang.Class<? extends LootPoolEntryContainer[]>)LootPoolEntryContainer[].class);
            return new SetContainerContents(arr, Arrays.asList((Object[])arr2), null);
        }
    }
}
