package net.minecraft.world.item.crafting;

import org.apache.logging.log4j.LogManager;
import com.google.gson.GsonBuilder;
import net.minecraft.Util;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.util.GsonHelper;
import java.util.stream.Stream;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import java.util.Iterator;
import com.google.gson.JsonParseException;
import com.google.common.collect.Maps;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonObject;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

public class RecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON;
    private static final Logger LOGGER;
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;
    private boolean hasErrors;
    
    public RecipeManager() {
        super(RecipeManager.GSON, "recipes");
        this.recipes = (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)ImmutableMap.of();
    }
    
    @Override
    protected void apply(final Map<ResourceLocation, JsonObject> map, final ResourceManager xi, final ProfilerFiller agn) {
        this.hasErrors = false;
        final Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> map2 = (Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>>)Maps.newHashMap();
        for (final Map.Entry<ResourceLocation, JsonObject> entry7 : map.entrySet()) {
            final ResourceLocation qv8 = (ResourceLocation)entry7.getKey();
            try {
                final Recipe<?> ber9 = fromJson(qv8, (JsonObject)entry7.getValue());
                ((ImmutableMap.Builder)map2.computeIfAbsent(ber9.getType(), beu -> ImmutableMap.builder())).put(qv8, ber9);
            }
            catch (JsonParseException | IllegalArgumentException ex2) {
                final RuntimeException ex;
                final RuntimeException runtimeException9 = ex;
                RecipeManager.LOGGER.error("Parsing error loading recipe {}", qv8, runtimeException9);
            }
        }
        this.recipes = (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((ImmutableMap.Builder)entry.getValue()).build()));
        RecipeManager.LOGGER.info("Loaded {} recipes", map2.size());
    }
    
    public <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(final RecipeType<T> beu, final C ahc, final Level bhr) {
        return (Optional<T>)this.<Container, T>byType(beu).values().stream().flatMap(ber -> Util.toStream((java.util.Optional<?>)beu.<Container>tryMatch(ber, bhr, ahc))).findFirst();
    }
    
    public <C extends Container, T extends Recipe<C>> List<T> getRecipesFor(final RecipeType<T> beu, final C ahc, final Level bhr) {
        return (List<T>)this.<Container, T>byType(beu).values().stream().flatMap(ber -> Util.toStream((java.util.Optional<?>)beu.<Container>tryMatch(ber, bhr, ahc))).sorted(Comparator.comparing(ber -> ber.getResultItem().getDescriptionId())).collect(Collectors.toList());
    }
    
    private <C extends Container, T extends Recipe<C>> Map<ResourceLocation, Recipe<C>> byType(final RecipeType<T> beu) {
        return (Map<ResourceLocation, Recipe<C>>)this.recipes.getOrDefault(beu, Collections.emptyMap());
    }
    
    public <C extends Container, T extends Recipe<C>> NonNullList<ItemStack> getRemainingItemsFor(final RecipeType<T> beu, final C ahc, final Level bhr) {
        final Optional<T> optional5 = this.<C, T>getRecipeFor(beu, ahc, bhr);
        if (optional5.isPresent()) {
            return ((Recipe)optional5.get()).getRemainingItems(ahc);
        }
        final NonNullList<ItemStack> fk6 = NonNullList.<ItemStack>withSize(ahc.getContainerSize(), ItemStack.EMPTY);
        for (int integer7 = 0; integer7 < fk6.size(); ++integer7) {
            fk6.set(integer7, ahc.getItem(integer7));
        }
        return fk6;
    }
    
    public Optional<? extends Recipe<?>> byKey(final ResourceLocation qv) {
        return this.recipes.values().stream().map(map -> (Recipe)map.get(qv)).filter(Objects::nonNull).findFirst();
    }
    
    public Collection<Recipe<?>> getRecipes() {
        return (Collection<Recipe<?>>)this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }
    
    public Stream<ResourceLocation> getRecipeIds() {
        return (Stream<ResourceLocation>)this.recipes.values().stream().flatMap(map -> map.keySet().stream());
    }
    
    public static Recipe<?> fromJson(final ResourceLocation qv, final JsonObject jsonObject) {
        final String string3 = GsonHelper.getAsString(jsonObject, "type");
        return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(string3)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string3 + "'"))).fromJson(qv, jsonObject);
    }
    
    public void replaceRecipes(final Iterable<Recipe<?>> iterable) {
        this.hasErrors = false;
        final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map3 = (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)Maps.newHashMap();
        iterable.forEach(ber -> {
            final Map<ResourceLocation, Recipe<?>> map2 = (Map<ResourceLocation, Recipe<?>>)map3.computeIfAbsent(ber.getType(), beu -> Maps.newHashMap());
            final Recipe<?> ber2 = map2.put(ber.getId(), ber);
            if (ber2 != null) {
                throw new IllegalStateException(new StringBuilder().append("Duplicate recipe ignored with ID ").append(ber.getId()).toString());
            }
        });
        this.recipes = (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)ImmutableMap.copyOf((Map)map3);
    }
    
    static {
        GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        LOGGER = LogManager.getLogger();
    }
}
