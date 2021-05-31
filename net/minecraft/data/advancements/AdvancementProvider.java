package net.minecraft.data.advancements;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import java.util.Set;
import java.nio.file.Path;
import com.google.common.collect.Sets;
import net.minecraft.data.HashCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import java.util.function.Consumer;
import java.util.List;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class AdvancementProvider implements DataProvider {
    private static final Logger LOGGER;
    private static final Gson GSON;
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Advancement>>> tabs;
    
    public AdvancementProvider(final DataGenerator gk) {
        this.tabs = (List<Consumer<Consumer<Advancement>>>)ImmutableList.of(new TheEndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());
        this.generator = gk;
    }
    
    public void run(final HashCache gm) throws IOException {
        final Path path3 = this.generator.getOutputFolder();
        final Set<ResourceLocation> set4 = (Set<ResourceLocation>)Sets.newHashSet();
        final Consumer<Advancement> consumer5 = (Consumer<Advancement>)(q -> {
            if (!set4.add(q.getId())) {
                throw new IllegalStateException(new StringBuilder().append("Duplicate advancement ").append(q.getId()).toString());
            }
            final Path path2 = createPath(path3, q);
            try {
                DataProvider.save(AdvancementProvider.GSON, gm, (JsonElement)q.deconstruct().serializeToJson(), path2);
            }
            catch (IOException iOException6) {
                AdvancementProvider.LOGGER.error("Couldn't save advancement {}", path2, iOException6);
            }
        });
        for (final Consumer<Consumer<Advancement>> consumer6 : this.tabs) {
            consumer6.accept(consumer5);
        }
    }
    
    private static Path createPath(final Path path, final Advancement q) {
        return path.resolve("data/" + q.getId().getNamespace() + "/advancements/" + q.getId().getPath() + ".json");
    }
    
    public String getName() {
        return "Advancements";
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}
