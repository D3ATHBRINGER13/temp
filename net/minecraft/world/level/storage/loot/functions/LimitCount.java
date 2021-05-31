package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.IntLimiter;

public class LimitCount extends LootItemConditionalFunction {
    private final IntLimiter limiter;
    
    private LimitCount(final LootItemCondition[] arr, final IntLimiter cox) {
        super(arr);
        this.limiter = cox;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final int integer4 = this.limiter.applyAsInt(bcj.getCount());
        bcj.setCount(integer4);
        return bcj;
    }
    
    public static Builder<?> limitCount(final IntLimiter cox) {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)(arr -> new LimitCount(arr, cox)));
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<LimitCount> {
        protected Serializer() {
            super(new ResourceLocation("limit_count"), LimitCount.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LimitCount cqf, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqf, jsonSerializationContext);
            jsonObject.add("limit", jsonSerializationContext.serialize(cqf.limiter));
        }
        
        @Override
        public LimitCount deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final IntLimiter cox5 = GsonHelper.<IntLimiter>getAsObject(jsonObject, "limit", jsonDeserializationContext, (java.lang.Class<? extends IntLimiter>)IntLimiter.class);
            return new LimitCount(arr, cox5, null);
        }
    }
}
