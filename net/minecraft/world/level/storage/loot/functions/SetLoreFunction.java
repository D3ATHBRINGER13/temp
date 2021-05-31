package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Streams;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.network.chat.Component;
import java.util.List;

public class SetLoreFunction extends LootItemConditionalFunction {
    private final boolean replace;
    private final List<Component> lore;
    @Nullable
    private final LootContext.EntityTarget resolutionContext;
    
    public SetLoreFunction(final LootItemCondition[] arr, final boolean boolean2, final List<Component> list, @Nullable final LootContext.EntityTarget c) {
        super(arr);
        this.replace = boolean2;
        this.lore = (List<Component>)ImmutableList.copyOf((Collection)list);
        this.resolutionContext = c;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)((this.resolutionContext != null) ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of());
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final ListTag ik4 = this.getLoreTag(bcj, !this.lore.isEmpty());
        if (ik4 != null) {
            if (this.replace) {
                ik4.clear();
            }
            final UnaryOperator<Component> unaryOperator5 = SetNameFunction.createResolver(coy, this.resolutionContext);
            this.lore.stream().map((Function)unaryOperator5).map(Component.Serializer::toJson).map(StringTag::new).forEach(ik4::add);
        }
        return bcj;
    }
    
    @Nullable
    private ListTag getLoreTag(final ItemStack bcj, final boolean boolean2) {
        CompoundTag id4;
        if (bcj.hasTag()) {
            id4 = bcj.getTag();
        }
        else {
            if (!boolean2) {
                return null;
            }
            id4 = new CompoundTag();
            bcj.setTag(id4);
        }
        CompoundTag id5;
        if (id4.contains("display", 10)) {
            id5 = id4.getCompound("display");
        }
        else {
            if (!boolean2) {
                return null;
            }
            id5 = new CompoundTag();
            id4.put("display", (Tag)id5);
        }
        if (id5.contains("Lore", 9)) {
            return id5.getList("Lore", 8);
        }
        if (boolean2) {
            final ListTag ik6 = new ListTag();
            id5.put("Lore", (Tag)ik6);
            return ik6;
        }
        return null;
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetLoreFunction> {
        public Serializer() {
            super(new ResourceLocation("set_lore"), SetLoreFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetLoreFunction cqp, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqp, jsonSerializationContext);
            jsonObject.addProperty("replace", Boolean.valueOf(cqp.replace));
            final JsonArray jsonArray5 = new JsonArray();
            for (final Component jo7 : cqp.lore) {
                jsonArray5.add(Component.Serializer.toJsonTree(jo7));
            }
            jsonObject.add("lore", (JsonElement)jsonArray5);
            if (cqp.resolutionContext != null) {
                jsonObject.add("entity", jsonSerializationContext.serialize(cqp.resolutionContext));
            }
        }
        
        @Override
        public SetLoreFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final boolean boolean5 = GsonHelper.getAsBoolean(jsonObject, "replace", false);
            final List<Component> list6 = (List<Component>)Streams.stream((Iterable)GsonHelper.getAsJsonArray(jsonObject, "lore")).map(Component.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            final LootContext.EntityTarget c7 = GsonHelper.<LootContext.EntityTarget>getAsObject(jsonObject, "entity", (LootContext.EntityTarget)null, jsonDeserializationContext, (java.lang.Class<? extends LootContext.EntityTarget>)LootContext.EntityTarget.class);
            return new SetLoreFunction(arr, boolean5, list6, c7);
        }
    }
}
