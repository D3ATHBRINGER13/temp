package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import org.apache.logging.log4j.Logger;

public class SetItemDamageFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER;
    private final RandomValueBounds damage;
    
    private SetItemDamageFunction(final LootItemCondition[] arr, final RandomValueBounds cpg) {
        super(arr);
        this.damage = cpg;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.isDamageableItem()) {
            final float float4 = 1.0f - this.damage.getFloat(coy.getRandom());
            bcj.setDamageValue(Mth.floor(float4 * bcj.getMaxDamage()));
        }
        else {
            SetItemDamageFunction.LOGGER.warn("Couldn't set damage of loot item {}", bcj);
        }
        return bcj;
    }
    
    public static Builder<?> setDamage(final RandomValueBounds cpg) {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)(arr -> new SetItemDamageFunction(arr, cpg)));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemDamageFunction> {
        protected Serializer() {
            super(new ResourceLocation("set_damage"), SetItemDamageFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetItemDamageFunction cqo, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqo, jsonSerializationContext);
            jsonObject.add("damage", jsonSerializationContext.serialize(cqo.damage));
        }
        
        @Override
        public SetItemDamageFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            return new SetItemDamageFunction(arr, GsonHelper.<RandomValueBounds>getAsObject(jsonObject, "damage", jsonDeserializationContext, (java.lang.Class<? extends RandomValueBounds>)RandomValueBounds.class), null);
        }
    }
}
