package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.Logger;

public class SmeltItemFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER;
    
    private SmeltItemFunction(final LootItemCondition[] arr) {
        super(arr);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.isEmpty()) {
            return bcj;
        }
        final Optional<SmeltingRecipe> optional4 = coy.getLevel().getRecipeManager().<SimpleContainer, SmeltingRecipe>getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[] { bcj }), coy.getLevel());
        if (optional4.isPresent()) {
            final ItemStack bcj2 = ((SmeltingRecipe)optional4.get()).getResultItem();
            if (!bcj2.isEmpty()) {
                final ItemStack bcj3 = bcj2.copy();
                bcj3.setCount(bcj.getCount());
                return bcj3;
            }
        }
        SmeltItemFunction.LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", bcj);
        return bcj;
    }
    
    public static Builder<?> smelted() {
        return LootItemConditionalFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)SmeltItemFunction::new);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SmeltItemFunction> {
        protected Serializer() {
            super(new ResourceLocation("furnace_smelt"), SmeltItemFunction.class);
        }
        
        @Override
        public SmeltItemFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            return new SmeltItemFunction(arr, null);
        }
    }
}
