package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.core.Registry;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.item.enchantment.Enchantment;

public class BonusLevelTableCondition implements LootItemCondition {
    private final Enchantment enchantment;
    private final float[] values;
    
    private BonusLevelTableCondition(final Enchantment bfs, final float[] arr) {
        this.enchantment = bfs;
        this.values = arr;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.TOOL);
    }
    
    public boolean test(final LootContext coy) {
        final ItemStack bcj3 = coy.<ItemStack>getParamOrNull(LootContextParams.TOOL);
        final int integer4 = (bcj3 != null) ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, bcj3) : 0;
        final float float5 = this.values[Math.min(integer4, this.values.length - 1)];
        return coy.getRandom().nextFloat() < float5;
    }
    
    public static Builder bonusLevelFlatChance(final Enchantment bfs, final float... arr) {
        return () -> new BonusLevelTableCondition(bfs, arr);
    }
    
    public static class Serializer extends LootItemCondition.Serializer<BonusLevelTableCondition> {
        public Serializer() {
            super(new ResourceLocation("table_bonus"), BonusLevelTableCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final BonusLevelTableCondition crc, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(crc.enchantment).toString());
            jsonObject.add("chances", jsonSerializationContext.serialize(crc.values));
        }
        
        @Override
        public BonusLevelTableCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final ResourceLocation qv4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "enchantment"));
            final Enchantment bfs5 = (Enchantment)Registry.ENCHANTMENT.getOptional(qv4).orElseThrow(() -> new JsonParseException(new StringBuilder().append("Invalid enchantment id: ").append(qv4).toString()));
            final float[] arr6 = GsonHelper.<float[]>getAsObject(jsonObject, "chances", jsonDeserializationContext, (java.lang.Class<? extends float[]>)float[].class);
            return new BonusLevelTableCondition(bfs5, arr6, null);
        }
    }
}
