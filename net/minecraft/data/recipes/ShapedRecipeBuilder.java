package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.function.Consumer;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.tags.Tag;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.Map;
import java.util.List;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeBuilder {
    private static final Logger LOGGER;
    private final Item result;
    private final int count;
    private final List<String> rows;
    private final Map<Character, Ingredient> key;
    private final Advancement.Builder advancement;
    private String group;
    
    public ShapedRecipeBuilder(final ItemLike bhq, final int integer) {
        this.rows = (List<String>)Lists.newArrayList();
        this.key = (Map<Character, Ingredient>)Maps.newLinkedHashMap();
        this.advancement = Advancement.Builder.advancement();
        this.result = bhq.asItem();
        this.count = integer;
    }
    
    public static ShapedRecipeBuilder shaped(final ItemLike bhq) {
        return shaped(bhq, 1);
    }
    
    public static ShapedRecipeBuilder shaped(final ItemLike bhq, final int integer) {
        return new ShapedRecipeBuilder(bhq, integer);
    }
    
    public ShapedRecipeBuilder define(final Character character, final Tag<Item> zg) {
        return this.define(character, Ingredient.of(zg));
    }
    
    public ShapedRecipeBuilder define(final Character character, final ItemLike bhq) {
        return this.define(character, Ingredient.of(bhq));
    }
    
    public ShapedRecipeBuilder define(final Character character, final Ingredient beo) {
        if (this.key.containsKey(character)) {
            throw new IllegalArgumentException(new StringBuilder().append("Symbol '").append(character).append("' is already defined!").toString());
        }
        if (character == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.key.put(character, beo);
        return this;
    }
    
    public ShapedRecipeBuilder pattern(final String string) {
        if (!this.rows.isEmpty() && string.length() != ((String)this.rows.get(0)).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.rows.add(string);
        return this;
    }
    
    public ShapedRecipeBuilder unlocks(final String string, final CriterionTriggerInstance y) {
        this.advancement.addCriterion(string, y);
        return this;
    }
    
    public ShapedRecipeBuilder group(final String string) {
        this.group = string;
        return this;
    }
    
    public void save(final Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.ITEM.getKey(this.result));
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final String string) {
        final ResourceLocation qv4 = Registry.ITEM.getKey(this.result);
        if (new ResourceLocation(string).equals(qv4)) {
            throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
        }
        this.save(consumer, new ResourceLocation(string));
    }
    
    public void save(final Consumer<FinishedRecipe> consumer, final ResourceLocation qv) {
        this.ensureValid(qv);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)new RecipeUnlockedTrigger.TriggerInstance(qv)).rewards(AdvancementRewards.Builder.recipe(qv)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(qv, this.result, this.count, (this.group == null) ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(qv.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + qv.getPath())));
    }
    
    private void ensureValid(final ResourceLocation qv) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException(new StringBuilder().append("No pattern is defined for shaped recipe ").append(qv).append("!").toString());
        }
        final Set<Character> set3 = (Set<Character>)Sets.newHashSet((Iterable)this.key.keySet());
        set3.remove(' ');
        for (final String string5 : this.rows) {
            for (int integer6 = 0; integer6 < string5.length(); ++integer6) {
                final char character7 = string5.charAt(integer6);
                if (!this.key.containsKey(character7) && character7 != ' ') {
                    throw new IllegalStateException(new StringBuilder().append("Pattern in recipe ").append(qv).append(" uses undefined symbol '").append(character7).append("'").toString());
                }
                set3.remove(character7);
            }
        }
        if (!set3.isEmpty()) {
            throw new IllegalStateException(new StringBuilder().append("Ingredients are defined but not used in pattern for recipe ").append(qv).toString());
        }
        if (this.rows.size() == 1 && ((String)this.rows.get(0)).length() == 1) {
            throw new IllegalStateException(new StringBuilder().append("Shaped recipe ").append(qv).append(" only takes in a single item - should it be a shapeless recipe instead?").toString());
        }
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException(new StringBuilder().append("No way of obtaining recipe ").append(qv).toString());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        
        public Result(final ResourceLocation qv2, final Item bce, final int integer, final String string, final List<String> list, final Map<Character, Ingredient> map, final Advancement.Builder a, final ResourceLocation qv9) {
            this.id = qv2;
            this.result = bce;
            this.count = integer;
            this.group = string;
            this.pattern = list;
            this.key = map;
            this.advancement = a;
            this.advancementId = qv9;
        }
        
        public void serializeRecipeData(final JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            final JsonArray jsonArray3 = new JsonArray();
            for (final String string5 : this.pattern) {
                jsonArray3.add(string5);
            }
            jsonObject.add("pattern", (JsonElement)jsonArray3);
            final JsonObject jsonObject2 = new JsonObject();
            for (final Map.Entry<Character, Ingredient> entry6 : this.key.entrySet()) {
                jsonObject2.add(String.valueOf(entry6.getKey()), ((Ingredient)entry6.getValue()).toJson());
            }
            jsonObject.add("key", (JsonElement)jsonObject2);
            final JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonObject3.addProperty("count", (Number)this.count);
            }
            jsonObject.add("result", (JsonElement)jsonObject3);
        }
        
        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPED_RECIPE;
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
