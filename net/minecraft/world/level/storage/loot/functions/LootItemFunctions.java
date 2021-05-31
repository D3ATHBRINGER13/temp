package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Maps;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class LootItemFunctions {
    private static final Map<ResourceLocation, LootItemFunction.Serializer<?>> FUNCTIONS_BY_NAME;
    private static final Map<Class<? extends LootItemFunction>, LootItemFunction.Serializer<?>> FUNCTIONS_BY_CLASS;
    public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY;
    
    public static <T extends LootItemFunction> void register(final LootItemFunction.Serializer<? extends T> b) {
        final ResourceLocation qv2 = b.getName();
        final Class<T> class3 = (Class<T>)b.getFunctionClass();
        if (LootItemFunctions.FUNCTIONS_BY_NAME.containsKey(qv2)) {
            throw new IllegalArgumentException(new StringBuilder().append("Can't re-register item function name ").append(qv2).toString());
        }
        if (LootItemFunctions.FUNCTIONS_BY_CLASS.containsKey(class3)) {
            throw new IllegalArgumentException("Can't re-register item function class " + class3.getName());
        }
        LootItemFunctions.FUNCTIONS_BY_NAME.put(qv2, b);
        LootItemFunctions.FUNCTIONS_BY_CLASS.put(class3, b);
    }
    
    public static LootItemFunction.Serializer<?> getSerializer(final ResourceLocation qv) {
        final LootItemFunction.Serializer<?> b2 = LootItemFunctions.FUNCTIONS_BY_NAME.get(qv);
        if (b2 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Unknown loot item function '").append(qv).append("'").toString());
        }
        return b2;
    }
    
    public static <T extends LootItemFunction> LootItemFunction.Serializer<T> getSerializer(final T cqh) {
        final LootItemFunction.Serializer<T> b2 = (LootItemFunction.Serializer<T>)LootItemFunctions.FUNCTIONS_BY_CLASS.get(cqh.getClass());
        if (b2 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Unknown loot item function ").append(cqh).toString());
        }
        return b2;
    }
    
    public static BiFunction<ItemStack, LootContext, ItemStack> compose(final BiFunction<ItemStack, LootContext, ItemStack>[] arr) {
        switch (arr.length) {
            case 0: {
                return LootItemFunctions.IDENTITY;
            }
            case 1: {
                return arr[0];
            }
            case 2: {
                final BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = arr[0];
                final BiFunction<ItemStack, LootContext, ItemStack> biFunction3 = arr[1];
                return (BiFunction<ItemStack, LootContext, ItemStack>)((bcj, coy) -> (ItemStack)biFunction3.apply(biFunction2.apply(bcj, coy), coy));
            }
            default: {
                return (BiFunction<ItemStack, LootContext, ItemStack>)((bcj, coy) -> {
                    for (final BiFunction<ItemStack, LootContext, ItemStack> biFunction7 : arr) {
                        bcj = (ItemStack)biFunction7.apply(bcj, coy);
                    }
                    return bcj;
                });
            }
        }
    }
    
    static {
        FUNCTIONS_BY_NAME = (Map)Maps.newHashMap();
        FUNCTIONS_BY_CLASS = (Map)Maps.newHashMap();
        IDENTITY = ((bcj, coy) -> bcj);
        LootItemFunctions.<LootItemFunction>register(new SetItemCountFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new EnchantWithLevelsFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new EnchantRandomlyFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetNbtFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SmeltItemFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new LootingEnchantFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetItemDamageFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetAttributesFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetNameFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new ExplorationMapFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetStewEffectFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new CopyNameFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetContainerContents.Serializer());
        LootItemFunctions.<LootItemFunction>register(new LimitCount.Serializer());
        LootItemFunctions.<LootItemFunction>register(new ApplyBonusCount.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetContainerLootTable.Serializer());
        LootItemFunctions.<LootItemFunction>register(new ApplyExplosionDecay.Serializer());
        LootItemFunctions.<LootItemFunction>register(new SetLoreFunction.Serializer());
        LootItemFunctions.<LootItemFunction>register(new FillPlayerHead.Serializer());
        LootItemFunctions.<LootItemFunction>register(new CopyNbtFunction.Serializer());
    }
    
    public static class Serializer implements JsonDeserializer<LootItemFunction>, JsonSerializer<LootItemFunction> {
        public LootItemFunction deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement, "function");
            final ResourceLocation qv6 = new ResourceLocation(GsonHelper.getAsString(jsonObject5, "function"));
            LootItemFunction.Serializer<?> b7;
            try {
                b7 = LootItemFunctions.getSerializer(qv6);
            }
            catch (IllegalArgumentException illegalArgumentException8) {
                throw new JsonSyntaxException(new StringBuilder().append("Unknown function '").append(qv6).append("'").toString());
            }
            return (LootItemFunction)b7.deserialize(jsonObject5, jsonDeserializationContext);
        }
        
        public JsonElement serialize(final LootItemFunction cqh, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final LootItemFunction.Serializer<LootItemFunction> b5 = LootItemFunctions.<LootItemFunction>getSerializer(cqh);
            final JsonObject jsonObject6 = new JsonObject();
            jsonObject6.addProperty("function", b5.getName().toString());
            b5.serialize(jsonObject6, cqh, jsonSerializationContext);
            return (JsonElement)jsonObject6;
        }
    }
}
