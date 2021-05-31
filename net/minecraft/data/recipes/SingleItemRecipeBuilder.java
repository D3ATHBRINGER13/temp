package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.function.Consumer;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Item;

public class SingleItemRecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final Advancement.Builder advancement;
    private String group;
    private final RecipeSerializer<?> type;
    
    public SingleItemRecipeBuilder(final RecipeSerializer<?> bet, final Ingredient beo, final ItemLike bhq, final int integer) {
        this.advancement = Advancement.Builder.advancement();
        this.type = bet;
        this.result = bhq.asItem();
        this.ingredient = beo;
        this.count = integer;
    }
    
    public static SingleItemRecipeBuilder stonecutting(final Ingredient beo, final ItemLike bhq) {
        return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, beo, bhq, 1);
    }
    
    public static SingleItemRecipeBuilder stonecutting(final Ingredient beo, final ItemLike bhq, final int integer) {
        return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, beo, bhq, integer);
    }
    
    public SingleItemRecipeBuilder unlocks(final String string, final CriterionTriggerInstance y) {
        this.advancement.addCriterion(string, y);
        return this;
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final String string) {
        final ResourceLocation qv4 = Registry.ITEM.getKey(this.result);
        if (new ResourceLocation(string).equals(qv4)) {
            throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
        }
        this.save(consumer, new ResourceLocation(string));
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final ResourceLocation qv) {
        this.ensureValid(qv);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)new RecipeUnlockedTrigger.TriggerInstance(qv)).rewards(AdvancementRewards.Builder.recipe(qv)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(qv, this.type, (this.group == null) ? "" : this.group, this.ingredient, this.result, this.count, this.advancement, new ResourceLocation(qv.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + qv.getPath())));
    }
    
    private void ensureValid(final ResourceLocation qv) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException(new StringBuilder().append("No way of obtaining recipe ").append(qv).toString());
        }
    }
    
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final int count;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<?> type;
        
        public Result(final ResourceLocation qv1, final RecipeSerializer<?> bet, final String string, final Ingredient beo, final Item bce, final int integer, final Advancement.Builder a, final ResourceLocation qv8) {
            this.id = qv1;
            this.type = bet;
            this.group = string;
            this.ingredient = beo;
            this.result = bce;
            this.count = integer;
            this.advancement = a;
            this.advancementId = qv8;
        }
        
        public void serializeRecipeData(final JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("ingredient", this.ingredient.toJson());
            jsonObject.addProperty("result", Registry.ITEM.getKey(this.result).toString());
            jsonObject.addProperty("count", (Number)this.count);
        }
        
        public ResourceLocation getId() {
            return this.id;
        }
        
        public RecipeSerializer<?> getType() {
            return this.type;
        }
        
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }
        
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
