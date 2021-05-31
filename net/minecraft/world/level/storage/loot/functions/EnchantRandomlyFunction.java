package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import java.util.function.Function;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import com.google.common.collect.Lists;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER;
    private final List<Enchantment> enchantments;
    
    private EnchantRandomlyFunction(final LootItemCondition[] arr, final Collection<Enchantment> collection) {
        super(arr);
        this.enchantments = (List<Enchantment>)ImmutableList.copyOf((Collection)collection);
    }
    
    public ItemStack run(ItemStack bcj, final LootContext coy) {
        final Random random5 = coy.getRandom();
        Enchantment bfs9;
        if (this.enchantments.isEmpty()) {
            final List<Enchantment> list6 = (List<Enchantment>)Lists.newArrayList();
            for (final Enchantment bfs8 : Registry.ENCHANTMENT) {
                if (bcj.getItem() == Items.BOOK || bfs8.canEnchant(bcj)) {
                    list6.add(bfs8);
                }
            }
            if (list6.isEmpty()) {
                EnchantRandomlyFunction.LOGGER.warn("Couldn't find a compatible enchantment for {}", bcj);
                return bcj;
            }
            bfs9 = (Enchantment)list6.get(random5.nextInt(list6.size()));
        }
        else {
            bfs9 = (Enchantment)this.enchantments.get(random5.nextInt(this.enchantments.size()));
        }
        final int integer6 = Mth.nextInt(random5, bfs9.getMinLevel(), bfs9.getMaxLevel());
        if (bcj.getItem() == Items.BOOK) {
            bcj = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(bcj, new EnchantmentInstance(bfs9, integer6));
        }
        else {
            bcj.enchant(bfs9, integer6);
        }
        return bcj;
    }
    
    public static Builder<?> randomApplicableEnchantment() {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)(arr -> new EnchantRandomlyFunction(arr, (Collection<Enchantment>)ImmutableList.of())));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantRandomlyFunction> {
        public Serializer() {
            super(new ResourceLocation("enchant_randomly"), EnchantRandomlyFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final EnchantRandomlyFunction cqa, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqa, jsonSerializationContext);
            if (!cqa.enchantments.isEmpty()) {
                final JsonArray jsonArray5 = new JsonArray();
                for (final Enchantment bfs7 : cqa.enchantments) {
                    final ResourceLocation qv8 = Registry.ENCHANTMENT.getKey(bfs7);
                    if (qv8 == null) {
                        throw new IllegalArgumentException(new StringBuilder().append("Don't know how to serialize enchantment ").append(bfs7).toString());
                    }
                    jsonArray5.add((JsonElement)new JsonPrimitive(qv8.toString()));
                }
                jsonObject.add("enchantments", (JsonElement)jsonArray5);
            }
        }
        
        @Override
        public EnchantRandomlyFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final List<Enchantment> list5 = (List<Enchantment>)Lists.newArrayList();
            if (jsonObject.has("enchantments")) {
                final JsonArray jsonArray6 = GsonHelper.getAsJsonArray(jsonObject, "enchantments");
                for (final JsonElement jsonElement8 : jsonArray6) {
                    final String string9 = GsonHelper.convertToString(jsonElement8, "enchantment");
                    final Enchantment bfs10 = (Enchantment)Registry.ENCHANTMENT.getOptional(new ResourceLocation(string9)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string9 + "'"));
                    list5.add(bfs10);
                }
            }
            return new EnchantRandomlyFunction(arr, (Collection)list5, null);
        }
    }
}
