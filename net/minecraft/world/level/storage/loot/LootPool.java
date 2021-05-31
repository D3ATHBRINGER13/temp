package net.minecraft.world.level.storage.loot;

import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.util.Mth;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import org.apache.commons.lang3.mutable.MutableInt;
import com.google.common.collect.Lists;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public class LootPool {
    private final LootPoolEntryContainer[] entries;
    private final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositeCondition;
    private final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    private final RandomIntGenerator rolls;
    private final RandomValueBounds bonusRolls;
    
    private LootPool(final LootPoolEntryContainer[] arr, final LootItemCondition[] arr, final LootItemFunction[] arr, final RandomIntGenerator cpe, final RandomValueBounds cpg) {
        this.entries = arr;
        this.conditions = arr;
        this.compositeCondition = LootItemConditions.<LootContext>andConditions((java.util.function.Predicate<LootContext>[])arr);
        this.functions = arr;
        this.compositeFunction = LootItemFunctions.compose(arr);
        this.rolls = cpe;
        this.bonusRolls = cpg;
    }
    
    private void addRandomItem(final Consumer<ItemStack> consumer, final LootContext coy) {
        final Random random4 = coy.getRandom();
        final List<LootPoolEntry> list5 = (List<LootPoolEntry>)Lists.newArrayList();
        final MutableInt mutableInt6 = new MutableInt();
        for (final LootPoolEntryContainer cpq10 : this.entries) {
            cpq10.expand(coy, (Consumer<LootPoolEntry>)(cpp -> {
                final int integer5 = cpp.getWeight(coy.getLuck());
                if (integer5 > 0) {
                    list5.add(cpp);
                    mutableInt6.add(integer5);
                }
            }));
        }
        final int integer7 = list5.size();
        if (mutableInt6.intValue() == 0 || integer7 == 0) {
            return;
        }
        if (integer7 == 1) {
            ((LootPoolEntry)list5.get(0)).createItemStack(consumer, coy);
            return;
        }
        int integer8 = random4.nextInt(mutableInt6.intValue());
        for (final LootPoolEntry cpp10 : list5) {
            integer8 -= cpp10.getWeight(coy.getLuck());
            if (integer8 < 0) {
                cpp10.createItemStack(consumer, coy);
            }
        }
    }
    
    public void addRandomItems(final Consumer<ItemStack> consumer, final LootContext coy) {
        if (!this.compositeCondition.test(coy)) {
            return;
        }
        final Consumer<ItemStack> consumer2 = LootItemFunction.decorate(this.compositeFunction, consumer, coy);
        final Random random5 = coy.getRandom();
        for (int integer6 = this.rolls.getInt(random5) + Mth.floor(this.bonusRolls.getFloat(random5) * coy.getLuck()), integer7 = 0; integer7 < integer6; ++integer7) {
            this.addRandomItem(consumer2, coy);
        }
    }
    
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        for (int integer6 = 0; integer6 < this.conditions.length; ++integer6) {
            this.conditions[integer6].validate(cpc.forChild(new StringBuilder().append(".condition[").append(integer6).append("]").toString()), function, set, cqx);
        }
        for (int integer6 = 0; integer6 < this.functions.length; ++integer6) {
            this.functions[integer6].validate(cpc.forChild(new StringBuilder().append(".functions[").append(integer6).append("]").toString()), function, set, cqx);
        }
        for (int integer6 = 0; integer6 < this.entries.length; ++integer6) {
            this.entries[integer6].validate(cpc.forChild(new StringBuilder().append(".entries[").append(integer6).append("]").toString()), function, set, cqx);
        }
    }
    
    public static Builder lootPool() {
        return new Builder();
    }
    
    public static class Builder implements FunctionUserBuilder<Builder>, ConditionUserBuilder<Builder> {
        private final List<LootPoolEntryContainer> entries;
        private final List<LootItemCondition> conditions;
        private final List<LootItemFunction> functions;
        private RandomIntGenerator rolls;
        private RandomValueBounds bonusRolls;
        
        public Builder() {
            this.entries = (List<LootPoolEntryContainer>)Lists.newArrayList();
            this.conditions = (List<LootItemCondition>)Lists.newArrayList();
            this.functions = (List<LootItemFunction>)Lists.newArrayList();
            this.rolls = new RandomValueBounds(1.0f);
            this.bonusRolls = new RandomValueBounds(0.0f, 0.0f);
        }
        
        public Builder setRolls(final RandomIntGenerator cpe) {
            this.rolls = cpe;
            return this;
        }
        
        public Builder unwrap() {
            return this;
        }
        
        public Builder add(final LootPoolEntryContainer.Builder<?> a) {
            this.entries.add(a.build());
            return this;
        }
        
        public Builder when(final LootItemCondition.Builder a) {
            this.conditions.add(a.build());
            return this;
        }
        
        public Builder apply(final LootItemFunction.Builder a) {
            this.functions.add(a.build());
            return this;
        }
        
        public LootPool build() {
            if (this.rolls == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool((LootPoolEntryContainer[])this.entries.toArray((Object[])new LootPoolEntryContainer[0]), (LootItemCondition[])this.conditions.toArray((Object[])new LootItemCondition[0]), (LootItemFunction[])this.functions.toArray((Object[])new LootItemFunction[0]), this.rolls, this.bonusRolls, null);
        }
    }
    
    public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
        public LootPool deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement, "loot pool");
            final LootPoolEntryContainer[] arr6 = GsonHelper.<LootPoolEntryContainer[]>getAsObject(jsonObject5, "entries", jsonDeserializationContext, (java.lang.Class<? extends LootPoolEntryContainer[]>)LootPoolEntryContainer[].class);
            final LootItemCondition[] arr7 = GsonHelper.<LootItemCondition[]>getAsObject(jsonObject5, "conditions", new LootItemCondition[0], jsonDeserializationContext, (java.lang.Class<? extends LootItemCondition[]>)LootItemCondition[].class);
            final LootItemFunction[] arr8 = GsonHelper.<LootItemFunction[]>getAsObject(jsonObject5, "functions", new LootItemFunction[0], jsonDeserializationContext, (java.lang.Class<? extends LootItemFunction[]>)LootItemFunction[].class);
            final RandomIntGenerator cpe9 = RandomIntGenerators.deserialize(jsonObject5.get("rolls"), jsonDeserializationContext);
            final RandomValueBounds cpg10 = GsonHelper.<RandomValueBounds>getAsObject(jsonObject5, "bonus_rolls", new RandomValueBounds(0.0f, 0.0f), jsonDeserializationContext, (java.lang.Class<? extends RandomValueBounds>)RandomValueBounds.class);
            return new LootPool(arr6, arr7, arr8, cpe9, cpg10, null);
        }
        
        public JsonElement serialize(final LootPool cpa, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject5 = new JsonObject();
            jsonObject5.add("rolls", RandomIntGenerators.serialize(cpa.rolls, jsonSerializationContext));
            jsonObject5.add("entries", jsonSerializationContext.serialize(cpa.entries));
            if (cpa.bonusRolls.getMin() != 0.0f && cpa.bonusRolls.getMax() != 0.0f) {
                jsonObject5.add("bonus_rolls", jsonSerializationContext.serialize(cpa.bonusRolls));
            }
            if (!ArrayUtils.isEmpty((Object[])cpa.conditions)) {
                jsonObject5.add("conditions", jsonSerializationContext.serialize(cpa.conditions));
            }
            if (!ArrayUtils.isEmpty((Object[])cpa.functions)) {
                jsonObject5.add("functions", jsonSerializationContext.serialize(cpa.functions));
            }
            return (JsonElement)jsonObject5;
        }
    }
}
