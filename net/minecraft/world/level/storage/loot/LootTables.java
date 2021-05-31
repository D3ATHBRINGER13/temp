package net.minecraft.world.level.storage.loot;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonElement;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import java.util.function.Function;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonObject;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

public class LootTables extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER;
    private static final Gson GSON;
    private Map<ResourceLocation, LootTable> tables;
    
    public LootTables() {
        super(LootTables.GSON, "loot_tables");
        this.tables = (Map<ResourceLocation, LootTable>)ImmutableMap.of();
    }
    
    public LootTable get(final ResourceLocation qv) {
        return (LootTable)this.tables.getOrDefault(qv, LootTable.EMPTY);
    }
    
    @Override
    protected void apply(final Map<ResourceLocation, JsonObject> map, final ResourceManager xi, final ProfilerFiller agn) {
        final ImmutableMap.Builder<ResourceLocation, LootTable> builder5 = (ImmutableMap.Builder<ResourceLocation, LootTable>)ImmutableMap.builder();
        final JsonObject jsonObject6 = (JsonObject)map.remove(BuiltInLootTables.EMPTY);
        if (jsonObject6 != null) {
            LootTables.LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", BuiltInLootTables.EMPTY);
        }
        map.forEach((qv, jsonObject) -> {
            try {
                final LootTable cpb4 = (LootTable)LootTables.GSON.fromJson((JsonElement)jsonObject, (Class)LootTable.class);
                builder5.put(qv, cpb4);
            }
            catch (Exception exception4) {
                LootTables.LOGGER.error("Couldn't parse loot table {}", qv, exception4);
            }
        });
        builder5.put(BuiltInLootTables.EMPTY, LootTable.EMPTY);
        final ImmutableMap<ResourceLocation, LootTable> immutableMap7 = (ImmutableMap<ResourceLocation, LootTable>)builder5.build();
        final LootTableProblemCollector cpc8 = new LootTableProblemCollector();
        immutableMap7.forEach((qv, cpb) -> validate(cpc8, qv, cpb, (Function<ResourceLocation, LootTable>)immutableMap7::get));
        cpc8.getProblems().forEach((string1, string2) -> LootTables.LOGGER.warn("Found validation problem in " + string1 + ": " + string2));
        this.tables = (Map<ResourceLocation, LootTable>)immutableMap7;
    }
    
    public static void validate(final LootTableProblemCollector cpc, final ResourceLocation qv, final LootTable cpb, final Function<ResourceLocation, LootTable> function) {
        final Set<ResourceLocation> set5 = (Set<ResourceLocation>)ImmutableSet.of(qv);
        cpb.validate(cpc.forChild("{" + qv.toString() + "}"), function, set5, cpb.getParamSet());
    }
    
    public static JsonElement serialize(final LootTable cpb) {
        return LootTables.GSON.toJsonTree(cpb);
    }
    
    public Set<ResourceLocation> getIds() {
        return (Set<ResourceLocation>)this.tables.keySet();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().registerTypeAdapter((Type)RandomValueBounds.class, new RandomValueBounds.Serializer()).registerTypeAdapter((Type)BinomialDistributionGenerator.class, new BinomialDistributionGenerator.Serializer()).registerTypeAdapter((Type)ConstantIntValue.class, new ConstantIntValue.Serializer()).registerTypeAdapter((Type)IntLimiter.class, new IntLimiter.Serializer()).registerTypeAdapter((Type)LootPool.class, new LootPool.Serializer()).registerTypeAdapter((Type)LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter((Class)LootPoolEntryContainer.class, new LootPoolEntries.Serializer()).registerTypeHierarchyAdapter((Class)LootItemFunction.class, new LootItemFunctions.Serializer()).registerTypeHierarchyAdapter((Class)LootItemCondition.class, new LootItemConditions.Serializer()).registerTypeHierarchyAdapter((Class)LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    }
}
