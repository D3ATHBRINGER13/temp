package net.minecraft.data.loot;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.function.Function;
import java.io.IOException;
import net.minecraft.world.level.storage.loot.LootTables;
import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.Map;
import java.nio.file.Path;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import com.google.common.collect.Maps;
import net.minecraft.data.HashCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class LootTableProvider implements DataProvider {
    private static final Logger LOGGER;
    private static final Gson GSON;
    private final DataGenerator generator;
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders;
    
    public LootTableProvider(final DataGenerator gk) {
        this.subProviders = (List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>>)ImmutableList.of(Pair.of((Object)FishingLoot::new, (Object)LootContextParamSets.FISHING), Pair.of((Object)ChestLoot::new, (Object)LootContextParamSets.CHEST), Pair.of((Object)EntityLoot::new, (Object)LootContextParamSets.ENTITY), Pair.of((Object)BlockLoot::new, (Object)LootContextParamSets.BLOCK), Pair.of((Object)GiftLoot::new, (Object)LootContextParamSets.GIFT));
        this.generator = gk;
    }
    
    public void run(final HashCache gm) {
        final Path path3 = this.generator.getOutputFolder();
        final Map<ResourceLocation, LootTable> map4 = (Map<ResourceLocation, LootTable>)Maps.newHashMap();
        this.subProviders.forEach(pair -> ((Consumer)((Supplier)pair.getFirst()).get()).accept(((qv, a) -> {
            if (map4.put((Object)qv, (Object)a.setParamSet((LootContextParamSet)pair.getSecond()).build()) != null) {
                throw new IllegalStateException(new StringBuilder().append("Duplicate loot table ").append((Object)qv).toString());
            }
        })));
        final LootTableProblemCollector cpc5 = new LootTableProblemCollector();
        final Set<ResourceLocation> set6 = (Set<ResourceLocation>)Sets.difference((Set)BuiltInLootTables.all(), map4.keySet());
        for (final ResourceLocation qv8 : set6) {
            cpc5.reportProblem(new StringBuilder().append("Missing built-in table: ").append(qv8).toString());
        }
        map4.forEach((qv, cpb) -> LootTables.validate(cpc5, qv, cpb, (Function<ResourceLocation, LootTable>)map4::get));
        final Multimap<String, String> multimap7 = cpc5.getProblems();
        if (!multimap7.isEmpty()) {
            multimap7.forEach((string1, string2) -> LootTableProvider.LOGGER.warn("Found validation problem in " + string1 + ": " + string2));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        map4.forEach((qv, cpb) -> {
            final Path path2 = createPath(path3, qv);
            try {
                DataProvider.save(LootTableProvider.GSON, gm, LootTables.serialize(cpb), path2);
            }
            catch (IOException iOException6) {
                LootTableProvider.LOGGER.error("Couldn't save loot table {}", path2, iOException6);
            }
        });
    }
    
    private static Path createPath(final Path path, final ResourceLocation qv) {
        return path.resolve("data/" + qv.getNamespace() + "/loot_tables/" + qv.getPath() + ".json");
    }
    
    public String getName() {
        return "LootTables";
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
}
