package net.minecraft.world.level.storage.loot.functions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.nbt.CompoundTag;

public class SetNbtFunction extends LootItemConditionalFunction {
    private final CompoundTag tag;
    
    private SetNbtFunction(final LootItemCondition[] arr, final CompoundTag id) {
        super(arr);
        this.tag = id;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        bcj.getOrCreateTag().merge(this.tag);
        return bcj;
    }
    
    public static Builder<?> setTag(final CompoundTag id) {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)(arr -> new SetNbtFunction(arr, id)));
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetNbtFunction> {
        public Serializer() {
            super(new ResourceLocation("set_nbt"), SetNbtFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetNbtFunction cqr, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqr, jsonSerializationContext);
            jsonObject.addProperty("tag", cqr.tag.toString());
        }
        
        @Override
        public SetNbtFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            try {
                final CompoundTag id5 = TagParser.parseTag(GsonHelper.getAsString(jsonObject, "tag"));
                return new SetNbtFunction(arr, id5, null);
            }
            catch (CommandSyntaxException commandSyntaxException5) {
                throw new JsonSyntaxException(commandSyntaxException5.getMessage());
            }
        }
    }
}
