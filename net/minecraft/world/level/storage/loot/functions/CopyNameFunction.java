package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.function.Function;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
    private final NameSource source;
    
    private CopyNameFunction(final LootItemCondition[] arr, final NameSource a) {
        super(arr);
        this.source = a;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(this.source.param);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final Object object4 = coy.getParamOrNull(this.source.param);
        if (object4 instanceof Nameable) {
            final Nameable ahn5 = (Nameable)object4;
            if (ahn5.hasCustomName()) {
                bcj.setHoverName(ahn5.getDisplayName());
            }
        }
        return bcj;
    }
    
    public static Builder<?> copyName(final NameSource a) {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)(arr -> new CopyNameFunction(arr, a)));
    }
    
    public enum NameSource {
        THIS("this", LootContextParams.THIS_ENTITY), 
        KILLER("killer", LootContextParams.KILLER_ENTITY), 
        KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER), 
        BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);
        
        public final String name;
        public final LootContextParam<?> param;
        
        private NameSource(final String string3, final LootContextParam<?> cqw) {
            this.name = string3;
            this.param = cqw;
        }
        
        public static NameSource getByName(final String string) {
            for (final NameSource a5 : values()) {
                if (a5.name.equals(string)) {
                    return a5;
                }
            }
            throw new IllegalArgumentException("Invalid name source " + string);
        }
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyNameFunction> {
        public Serializer() {
            super(new ResourceLocation("copy_name"), CopyNameFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final CopyNameFunction cpy, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cpy, jsonSerializationContext);
            jsonObject.addProperty("source", cpy.source.name);
        }
        
        @Override
        public CopyNameFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final NameSource a5 = NameSource.getByName(GsonHelper.getAsString(jsonObject, "source"));
            return new CopyNameFunction(arr, a5, null);
        }
    }
}
