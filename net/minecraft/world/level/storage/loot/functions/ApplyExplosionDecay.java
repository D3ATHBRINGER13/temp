package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import java.util.Random;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay extends LootItemConditionalFunction {
    private ApplyExplosionDecay(final LootItemCondition[] arr) {
        super(arr);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final Float float4 = coy.<Float>getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if (float4 != null) {
            final Random random5 = coy.getRandom();
            final float float5 = 1.0f / float4;
            final int integer7 = bcj.getCount();
            int integer8 = 0;
            for (int integer9 = 0; integer9 < integer7; ++integer9) {
                if (random5.nextFloat() <= float5) {
                    ++integer8;
                }
            }
            bcj.setCount(integer8);
        }
        return bcj;
    }
    
    public static Builder<?> explosionDecay() {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)ApplyExplosionDecay::new);
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyExplosionDecay> {
        protected Serializer() {
            super(new ResourceLocation("explosion_decay"), ApplyExplosionDecay.class);
        }
        
        @Override
        public ApplyExplosionDecay deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            return new ApplyExplosionDecay(arr, null);
        }
    }
}
