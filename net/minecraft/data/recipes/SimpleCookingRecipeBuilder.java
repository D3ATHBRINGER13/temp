package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.function.Consumer;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Item;

public class SimpleCookingRecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement;
    private String group;
    private final SimpleCookingSerializer<?> serializer;
    
    private SimpleCookingRecipeBuilder(final ItemLike bhq, final Ingredient beo, final float float3, final int integer, final SimpleCookingSerializer<?> bfa) {
        this.advancement = Advancement.Builder.advancement();
        this.result = bhq.asItem();
        this.ingredient = beo;
        this.experience = float3;
        this.cookingTime = integer;
        this.serializer = bfa;
    }
    
    public static SimpleCookingRecipeBuilder cooking(final Ingredient beo, final ItemLike bhq, final float float3, final int integer, final SimpleCookingSerializer<?> bfa) {
        return new SimpleCookingRecipeBuilder(bhq, beo, float3, integer, bfa);
    }
    
    public static SimpleCookingRecipeBuilder blasting(final Ingredient beo, final ItemLike bhq, final float float3, final int integer) {
        return cooking(beo, bhq, float3, integer, RecipeSerializer.BLASTING_RECIPE);
    }
    
    public static SimpleCookingRecipeBuilder smelting(final Ingredient beo, final ItemLike bhq, final float float3, final int integer) {
        return cooking(beo, bhq, float3, integer, RecipeSerializer.SMELTING_RECIPE);
    }
    
    public SimpleCookingRecipeBuilder unlocks(final String string, final CriterionTriggerInstance y) {
        this.advancement.addCriterion(string, y);
        return this;
    }
    
    public void save(final Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.ITEM.getKey(this.result));
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final String string) {
        final ResourceLocation qv4 = Registry.ITEM.getKey(this.result);
        final ResourceLocation qv5 = new ResourceLocation(string);
        if (qv5.equals(qv4)) {
            throw new IllegalStateException(new StringBuilder().append("Recipe ").append(qv5).append(" should remove its 'save' argument").toString());
        }
        this.save(consumer, qv5);
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final ResourceLocation qv) {
        this.ensureValid(qv);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)new RecipeUnlockedTrigger.TriggerInstance(qv)).rewards(AdvancementRewards.Builder.recipe(qv)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(qv, (this.group == null) ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, new ResourceLocation(qv.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + qv.getPath()), this.serializer));
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
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;
        
        public Result(final ResourceLocation qv1, final String string, final Ingredient beo, final Item bce, final float float5, final int integer, final Advancement.Builder a, final ResourceLocation qv8, final RecipeSerializer<? extends AbstractCookingRecipe> bet) {
            this.id = qv1;
            this.group = string;
            this.ingredient = beo;
            this.result = bce;
            this.experience = float5;
            this.cookingTime = integer;
            this.advancement = a;
            this.advancementId = qv8;
            this.serializer = bet;
        }
        
        public void serializeRecipeData(final JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("ingredient", this.ingredient.toJson());
            jsonObject.addProperty("result", Registry.ITEM.getKey(this.result).toString());
            jsonObject.addProperty("experience", (Number)this.experience);
            jsonObject.addProperty("cookingtime", (Number)this.cookingTime);
        }
        
        public RecipeSerializer<?> getType() {
            return this.serializer;
        }
        
        public ResourceLocation getId() {
            return this.id;
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
