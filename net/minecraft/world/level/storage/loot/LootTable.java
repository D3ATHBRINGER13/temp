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
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import java.util.Collections;
import java.util.Collection;
import net.minecraft.util.Mth;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.Container;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.apache.logging.log4j.Logger;

public class LootTable {
    private static final Logger LOGGER;
    public static final LootTable EMPTY;
    public static final LootContextParamSet DEFAULT_PARAM_SET;
    private final LootContextParamSet paramSet;
    private final LootPool[] pools;
    private final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    
    private LootTable(final LootContextParamSet cqx, final LootPool[] arr, final LootItemFunction[] arr) {
        this.paramSet = cqx;
        this.pools = arr;
        this.functions = arr;
        this.compositeFunction = LootItemFunctions.compose(arr);
    }
    
    public static Consumer<ItemStack> createStackSplitter(final Consumer<ItemStack> consumer) {
        return (Consumer<ItemStack>)(bcj -> {
            if (bcj.getCount() < bcj.getMaxStackSize()) {
                consumer.accept(bcj);
            }
            else {
                int integer3 = bcj.getCount();
                while (integer3 > 0) {
                    final ItemStack bcj2 = bcj.copy();
                    bcj2.setCount(Math.min(bcj.getMaxStackSize(), integer3));
                    integer3 -= bcj2.getCount();
                    consumer.accept(bcj2);
                }
            }
        });
    }
    
    public void getRandomItemsRaw(final LootContext coy, final Consumer<ItemStack> consumer) {
        if (coy.addVisitedTable(this)) {
            final Consumer<ItemStack> consumer2 = LootItemFunction.decorate(this.compositeFunction, consumer, coy);
            for (final LootPool cpa8 : this.pools) {
                cpa8.addRandomItems(consumer2, coy);
            }
            coy.removeVisitedTable(this);
        }
        else {
            LootTable.LOGGER.warn("Detected infinite loop in loot tables");
        }
    }
    
    public void getRandomItems(final LootContext coy, final Consumer<ItemStack> consumer) {
        this.getRandomItemsRaw(coy, createStackSplitter(consumer));
    }
    
    public List<ItemStack> getRandomItems(final LootContext coy) {
        final List<ItemStack> list3 = (List<ItemStack>)Lists.newArrayList();
        this.getRandomItems(coy, (Consumer<ItemStack>)list3::add);
        return list3;
    }
    
    public LootContextParamSet getParamSet() {
        return this.paramSet;
    }
    
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        for (int integer6 = 0; integer6 < this.pools.length; ++integer6) {
            this.pools[integer6].validate(cpc.forChild(new StringBuilder().append(".pools[").append(integer6).append("]").toString()), function, set, cqx);
        }
        for (int integer6 = 0; integer6 < this.functions.length; ++integer6) {
            this.functions[integer6].validate(cpc.forChild(new StringBuilder().append(".functions[").append(integer6).append("]").toString()), function, set, cqx);
        }
    }
    
    public void fill(final Container ahc, final LootContext coy) {
        final List<ItemStack> list4 = this.getRandomItems(coy);
        final Random random5 = coy.getRandom();
        final List<Integer> list5 = this.getAvailableSlots(ahc, random5);
        this.shuffleAndSplitItems(list4, list5.size(), random5);
        for (final ItemStack bcj8 : list4) {
            if (list5.isEmpty()) {
                LootTable.LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (bcj8.isEmpty()) {
                ahc.setItem((int)list5.remove(list5.size() - 1), ItemStack.EMPTY);
            }
            else {
                ahc.setItem((int)list5.remove(list5.size() - 1), bcj8);
            }
        }
    }
    
    private void shuffleAndSplitItems(final List<ItemStack> list, final int integer, final Random random) {
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayList();
        final Iterator<ItemStack> iterator6 = (Iterator<ItemStack>)list.iterator();
        while (iterator6.hasNext()) {
            final ItemStack bcj7 = (ItemStack)iterator6.next();
            if (bcj7.isEmpty()) {
                iterator6.remove();
            }
            else {
                if (bcj7.getCount() <= 1) {
                    continue;
                }
                list2.add(bcj7);
                iterator6.remove();
            }
        }
        while (integer - list.size() - list2.size() > 0 && !list2.isEmpty()) {
            final ItemStack bcj8 = (ItemStack)list2.remove(Mth.nextInt(random, 0, list2.size() - 1));
            final int integer2 = Mth.nextInt(random, 1, bcj8.getCount() / 2);
            final ItemStack bcj9 = bcj8.split(integer2);
            if (bcj8.getCount() > 1 && random.nextBoolean()) {
                list2.add(bcj8);
            }
            else {
                list.add(bcj8);
            }
            if (bcj9.getCount() > 1 && random.nextBoolean()) {
                list2.add(bcj9);
            }
            else {
                list.add(bcj9);
            }
        }
        list.addAll((Collection)list2);
        Collections.shuffle((List)list, random);
    }
    
    private List<Integer> getAvailableSlots(final Container ahc, final Random random) {
        final List<Integer> list4 = (List<Integer>)Lists.newArrayList();
        for (int integer5 = 0; integer5 < ahc.getContainerSize(); ++integer5) {
            if (ahc.getItem(integer5).isEmpty()) {
                list4.add(integer5);
            }
        }
        Collections.shuffle((List)list4, random);
        return list4;
    }
    
    public static Builder lootTable() {
        return new Builder();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY = new LootTable(LootContextParamSets.EMPTY, new LootPool[0], new LootItemFunction[0]);
        DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
    }
    
    public static class Builder implements FunctionUserBuilder<Builder> {
        private final List<LootPool> pools;
        private final List<LootItemFunction> functions;
        private LootContextParamSet paramSet;
        
        public Builder() {
            this.pools = (List<LootPool>)Lists.newArrayList();
            this.functions = (List<LootItemFunction>)Lists.newArrayList();
            this.paramSet = LootTable.DEFAULT_PARAM_SET;
        }
        
        public Builder withPool(final LootPool.Builder a) {
            this.pools.add(a.build());
            return this;
        }
        
        public Builder setParamSet(final LootContextParamSet cqx) {
            this.paramSet = cqx;
            return this;
        }
        
        public Builder apply(final LootItemFunction.Builder a) {
            this.functions.add(a.build());
            return this;
        }
        
        public Builder unwrap() {
            return this;
        }
        
        public LootTable build() {
            return new LootTable(this.paramSet, (LootPool[])this.pools.toArray((Object[])new LootPool[0]), (LootItemFunction[])this.functions.toArray((Object[])new LootItemFunction[0]), null);
        }
    }
    
    public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
        public LootTable deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement, "loot table");
            final LootPool[] arr6 = GsonHelper.<LootPool[]>getAsObject(jsonObject5, "pools", new LootPool[0], jsonDeserializationContext, (java.lang.Class<? extends LootPool[]>)LootPool[].class);
            LootContextParamSet cqx7 = null;
            if (jsonObject5.has("type")) {
                final String string8 = GsonHelper.getAsString(jsonObject5, "type");
                cqx7 = LootContextParamSets.get(new ResourceLocation(string8));
            }
            final LootItemFunction[] arr7 = GsonHelper.<LootItemFunction[]>getAsObject(jsonObject5, "functions", new LootItemFunction[0], jsonDeserializationContext, (java.lang.Class<? extends LootItemFunction[]>)LootItemFunction[].class);
            return new LootTable((cqx7 != null) ? cqx7 : LootContextParamSets.ALL_PARAMS, arr6, arr7, null);
        }
        
        public JsonElement serialize(final LootTable cpb, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject5 = new JsonObject();
            if (cpb.paramSet != LootTable.DEFAULT_PARAM_SET) {
                final ResourceLocation qv6 = LootContextParamSets.getKey(cpb.paramSet);
                if (qv6 != null) {
                    jsonObject5.addProperty("type", qv6.toString());
                }
                else {
                    LootTable.LOGGER.warn(new StringBuilder().append("Failed to find id for param set ").append(cpb.paramSet).toString());
                }
            }
            if (cpb.pools.length > 0) {
                jsonObject5.add("pools", jsonSerializationContext.serialize(cpb.pools));
            }
            if (!ArrayUtils.isEmpty((Object[])cpb.functions)) {
                jsonObject5.add("functions", jsonSerializationContext.serialize(cpb.functions));
            }
            return (JsonElement)jsonObject5;
        }
    }
}
