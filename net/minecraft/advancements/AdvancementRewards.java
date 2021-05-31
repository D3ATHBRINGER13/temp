package net.minecraft.advancements;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.List;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import java.util.Arrays;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;

public class AdvancementRewards {
    public static final AdvancementRewards EMPTY;
    private final int experience;
    private final ResourceLocation[] loot;
    private final ResourceLocation[] recipes;
    private final CommandFunction.CacheableFunction function;
    
    public AdvancementRewards(final int integer, final ResourceLocation[] arr2, final ResourceLocation[] arr3, final CommandFunction.CacheableFunction a) {
        this.experience = integer;
        this.loot = arr2;
        this.recipes = arr3;
        this.function = a;
    }
    
    public void grant(final ServerPlayer vl) {
        vl.giveExperiencePoints(this.experience);
        final LootContext coy3 = new LootContext.Builder(vl.getLevel()).<Entity>withParameter(LootContextParams.THIS_ENTITY, vl).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(vl)).withRandom(vl.getRandom()).create(LootContextParamSets.ADVANCEMENT_REWARD);
        boolean boolean4 = false;
        for (final ResourceLocation qv8 : this.loot) {
            for (final ItemStack bcj10 : vl.server.getLootTables().get(qv8).getRandomItems(coy3)) {
                if (vl.addItem(bcj10)) {
                    vl.level.playSound(null, vl.x, vl.y, vl.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((vl.getRandom().nextFloat() - vl.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    boolean4 = true;
                }
                else {
                    final ItemEntity atx11 = vl.drop(bcj10, false);
                    if (atx11 == null) {
                        continue;
                    }
                    atx11.setNoPickUpDelay();
                    atx11.setOwner(vl.getUUID());
                }
            }
        }
        if (boolean4) {
            vl.inventoryMenu.broadcastChanges();
        }
        if (this.recipes.length > 0) {
            vl.awardRecipesByKey(this.recipes);
        }
        final MinecraftServer minecraftServer5 = vl.server;
        this.function.get(minecraftServer5.getFunctions()).ifPresent(ca -> minecraftServer5.getFunctions().execute(ca, vl.createCommandSourceStack().withSuppressedOutput().withPermission(2)));
    }
    
    public String toString() {
        return new StringBuilder().append("AdvancementRewards{experience=").append(this.experience).append(", loot=").append(Arrays.toString((Object[])this.loot)).append(", recipes=").append(Arrays.toString((Object[])this.recipes)).append(", function=").append(this.function).append('}').toString();
    }
    
    public JsonElement serializeToJson() {
        if (this == AdvancementRewards.EMPTY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        if (this.experience != 0) {
            jsonObject2.addProperty("experience", (Number)this.experience);
        }
        if (this.loot.length > 0) {
            final JsonArray jsonArray3 = new JsonArray();
            for (final ResourceLocation qv7 : this.loot) {
                jsonArray3.add(qv7.toString());
            }
            jsonObject2.add("loot", (JsonElement)jsonArray3);
        }
        if (this.recipes.length > 0) {
            final JsonArray jsonArray3 = new JsonArray();
            for (final ResourceLocation qv7 : this.recipes) {
                jsonArray3.add(qv7.toString());
            }
            jsonObject2.add("recipes", (JsonElement)jsonArray3);
        }
        if (this.function.getId() != null) {
            jsonObject2.addProperty("function", this.function.getId().toString());
        }
        return (JsonElement)jsonObject2;
    }
    
    static {
        EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
    }
    
    public static class Deserializer implements JsonDeserializer<AdvancementRewards> {
        public AdvancementRewards deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement, "rewards");
            final int integer6 = GsonHelper.getAsInt(jsonObject5, "experience", 0);
            final JsonArray jsonArray7 = GsonHelper.getAsJsonArray(jsonObject5, "loot", new JsonArray());
            final ResourceLocation[] arr8 = new ResourceLocation[jsonArray7.size()];
            for (int integer7 = 0; integer7 < arr8.length; ++integer7) {
                arr8[integer7] = new ResourceLocation(GsonHelper.convertToString(jsonArray7.get(integer7), new StringBuilder().append("loot[").append(integer7).append("]").toString()));
            }
            final JsonArray jsonArray8 = GsonHelper.getAsJsonArray(jsonObject5, "recipes", new JsonArray());
            final ResourceLocation[] arr9 = new ResourceLocation[jsonArray8.size()];
            for (int integer8 = 0; integer8 < arr9.length; ++integer8) {
                arr9[integer8] = new ResourceLocation(GsonHelper.convertToString(jsonArray8.get(integer8), new StringBuilder().append("recipes[").append(integer8).append("]").toString()));
            }
            CommandFunction.CacheableFunction a11;
            if (jsonObject5.has("function")) {
                a11 = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(jsonObject5, "function")));
            }
            else {
                a11 = CommandFunction.CacheableFunction.NONE;
            }
            return new AdvancementRewards(integer6, arr8, arr9, a11);
        }
    }
    
    public static class Builder {
        private int experience;
        private final List<ResourceLocation> loot;
        private final List<ResourceLocation> recipes;
        @Nullable
        private ResourceLocation function;
        
        public Builder() {
            this.loot = (List<ResourceLocation>)Lists.newArrayList();
            this.recipes = (List<ResourceLocation>)Lists.newArrayList();
        }
        
        public static Builder experience(final int integer) {
            return new Builder().addExperience(integer);
        }
        
        public Builder addExperience(final int integer) {
            this.experience += integer;
            return this;
        }
        
        public static Builder recipe(final ResourceLocation qv) {
            return new Builder().addRecipe(qv);
        }
        
        public Builder addRecipe(final ResourceLocation qv) {
            this.recipes.add(qv);
            return this;
        }
        
        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray((Object[])new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray((Object[])new ResourceLocation[0]), (this.function == null) ? CommandFunction.CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
        }
    }
}
