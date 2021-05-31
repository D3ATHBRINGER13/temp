package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public class SetItemCountFunction extends LootItemConditionalFunction {
    private final RandomIntGenerator value;
    
    private SetItemCountFunction(final LootItemCondition[] arr, final RandomIntGenerator cpe) {
        super(arr);
        this.value = cpe;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        bcj.setCount(this.value.getInt(coy.getRandom()));
        return bcj;
    }
    
    public static Builder<?> setCount(final RandomIntGenerator cpe) {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)(arr -> new SetItemCountFunction(arr, cpe)));
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemCountFunction> {
        protected Serializer() {
            super(new ResourceLocation("set_count"), SetItemCountFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetItemCountFunction cqn, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqn, jsonSerializationContext);
            jsonObject.add("count", RandomIntGenerators.serialize(cqn.value, jsonSerializationContext));
        }
        
        @Override
        public SetItemCountFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final RandomIntGenerator cpe5 = RandomIntGenerators.deserialize(jsonObject.get("count"), jsonDeserializationContext);
            return new SetItemCountFunction(arr, cpe5, null);
        }
    }
}
