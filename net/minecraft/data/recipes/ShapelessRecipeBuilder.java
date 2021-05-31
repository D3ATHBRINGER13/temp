package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.RecipeSerializer;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.function.Consumer;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.tags.Tag;
import com.google.common.collect.Lists;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.List;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeBuilder {
    private static final Logger LOGGER;
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients;
    private final Advancement.Builder advancement;
    private String group;
    
    public ShapelessRecipeBuilder(final ItemLike bhq, final int integer) {
        this.ingredients = (List<Ingredient>)Lists.newArrayList();
        this.advancement = Advancement.Builder.advancement();
        this.result = bhq.asItem();
        this.count = integer;
    }
    
    public static ShapelessRecipeBuilder shapeless(final ItemLike bhq) {
        return new ShapelessRecipeBuilder(bhq, 1);
    }
    
    public static ShapelessRecipeBuilder shapeless(final ItemLike bhq, final int integer) {
        return new ShapelessRecipeBuilder(bhq, integer);
    }
    
    public ShapelessRecipeBuilder requires(final Tag<Item> zg) {
        return this.requires(Ingredient.of(zg));
    }
    
    public ShapelessRecipeBuilder requires(final ItemLike bhq) {
        return this.requires(bhq, 1);
    }
    
    public ShapelessRecipeBuilder requires(final ItemLike bhq, final int integer) {
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            this.requires(Ingredient.of(bhq));
        }
        return this;
    }
    
    public ShapelessRecipeBuilder requires(final Ingredient beo) {
        return this.requires(beo, 1);
    }
    
    public ShapelessRecipeBuilder requires(final Ingredient beo, final int integer) {
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            this.ingredients.add(beo);
        }
        return this;
    }
    
    public ShapelessRecipeBuilder unlocks(final String string, final CriterionTriggerInstance y) {
        this.advancement.addCriterion(string, y);
        return this;
    }
    
    public ShapelessRecipeBuilder group(final String string) {
        this.group = string;
        return this;
    }
    
    public void save(final Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.ITEM.getKey(this.result));
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final String string) {
        final ResourceLocation qv4 = Registry.ITEM.getKey(this.result);
        if (new ResourceLocation(string).equals(qv4)) {
            throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
        }
        this.save(consumer, new ResourceLocation(string));
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final ResourceLocation qv) {
        this.ensureValid(qv);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)new RecipeUnlockedTrigger.TriggerInstance(qv)).rewards(AdvancementRewards.Builder.recipe(qv)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(qv, this.result, this.count, (this.group == null) ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(qv.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + qv.getPath())));
    }
    
    private void ensureValid(final ResourceLocation qv) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException(new StringBuilder().append("No way of obtaining recipe ").append(qv).toString());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        
        public Result(final ResourceLocation qv1, final Item bce, final int integer, final String string, final List<Ingredient> list, final Advancement.Builder a, final ResourceLocation qv7) {
            this.id = qv1;
            this.result = bce;
            this.count = integer;
            this.group = string;
            this.ingredients = list;
            this.advancement = a;
            this.advancementId = qv7;
        }
        
        public void serializeRecipeData(final JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            final JsonArray jsonArray3 = new JsonArray();
            for (final Ingredient beo5 : this.ingredients) {
                jsonArray3.add(beo5.toJson());
            }
            jsonObject.add("ingredients", (JsonElement)jsonArray3);
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonObject2.addProperty("count", (Number)this.count);
            }
            jsonObject.add("result", (JsonElement)jsonObject2);
        }
        
        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPELESS_RECIPE;
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
