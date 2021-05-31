package net.minecraft.data.tags;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import java.io.BufferedWriter;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.tags.TagCollection;
import net.minecraft.data.HashCache;
import com.google.common.collect.Maps;
import net.minecraft.tags.Tag;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public abstract class TagsProvider<T> implements DataProvider {
    private static final Logger LOGGER;
    private static final Gson GSON;
    protected final DataGenerator generator;
    protected final Registry<T> registry;
    protected final Map<Tag<T>, Tag.Builder<T>> builders;
    
    protected TagsProvider(final DataGenerator gk, final Registry<T> fn) {
        this.builders = (Map<Tag<T>, Tag.Builder<T>>)Maps.newLinkedHashMap();
        this.generator = gk;
        this.registry = fn;
    }
    
    protected abstract void addTags();
    
    public void run(final HashCache gm) {
        this.builders.clear();
        this.addTags();
        final TagCollection<T> zh3 = new TagCollection<T>((java.util.function.Function<ResourceLocation, java.util.Optional<T>>)(qv -> Optional.empty()), "", false, "generated");
        final Map<ResourceLocation, Tag.Builder<T>> map4 = (Map<ResourceLocation, Tag.Builder<T>>)this.builders.entrySet().stream().collect(Collectors.toMap(entry -> ((Tag)entry.getKey()).getId(), Map.Entry::getValue));
        zh3.load(map4);
        zh3.getAllTags().forEach((qv, zg) -> {
            final JsonObject jsonObject5 = zg.serializeToJson(this.registry::getKey);
            final Path path6 = this.getPath(qv);
            try {
                final String string7 = TagsProvider.GSON.toJson((JsonElement)jsonObject5);
                final String string8 = TagsProvider.SHA1.hashUnencodedChars((CharSequence)string7).toString();
                if (!Objects.equals(gm.getHash(path6), string8) || !Files.exists(path6, new LinkOption[0])) {
                    Files.createDirectories(path6.getParent(), new FileAttribute[0]);
                    try (final BufferedWriter bufferedWriter9 = Files.newBufferedWriter(path6, new OpenOption[0])) {
                        bufferedWriter9.write(string7);
                    }
                }
                gm.putNew(path6, string8);
            }
            catch (IOException iOException7) {
                TagsProvider.LOGGER.error("Couldn't save tags to {}", path6, iOException7);
            }
        });
        this.useTags(zh3);
    }
    
    protected abstract void useTags(final TagCollection<T> zh);
    
    protected abstract Path getPath(final ResourceLocation qv);
    
    protected Tag.Builder<T> tag(final Tag<T> zg) {
        return (Tag.Builder<T>)this.builders.computeIfAbsent(zg, zg -> Tag.Builder.tag());
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}
