package net.minecraft.server;

import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.AdvancementRewards;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.Advancement;
import com.google.common.collect.Maps;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.advancements.AdvancementList;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER;
    private static final Gson GSON;
    private AdvancementList advancements;
    
    public ServerAdvancementManager() {
        super(ServerAdvancementManager.GSON, "advancements");
        this.advancements = new AdvancementList();
    }
    
    @Override
    protected void apply(final Map<ResourceLocation, JsonObject> map, final ResourceManager xi, final ProfilerFiller agn) {
        final Map<ResourceLocation, Advancement.Builder> map2 = (Map<ResourceLocation, Advancement.Builder>)Maps.newHashMap();
        map.forEach((qv, jsonObject) -> {
            try {
                final Advancement.Builder a4 = (Advancement.Builder)ServerAdvancementManager.GSON.fromJson((JsonElement)jsonObject, (Class)Advancement.Builder.class);
                map2.put(qv, a4);
            }
            catch (JsonParseException | IllegalArgumentException ex2) {
                final RuntimeException ex;
                final RuntimeException runtimeException4 = ex;
                ServerAdvancementManager.LOGGER.error("Parsing error loading custom advancement {}: {}", qv, runtimeException4.getMessage());
            }
        });
        final AdvancementList r6 = new AdvancementList();
        r6.add(map2);
        for (final Advancement q8 : r6.getRoots()) {
            if (q8.getDisplay() != null) {
                TreeNodePosition.run(q8);
            }
        }
        this.advancements = r6;
    }
    
    @Nullable
    public Advancement getAdvancement(final ResourceLocation qv) {
        return this.advancements.get(qv);
    }
    
    public Collection<Advancement> getAllAdvancements() {
        return this.advancements.getAllAdvancements();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().registerTypeHierarchyAdapter((Class)Advancement.Builder.class, ((jsonElement, type, jsonDeserializationContext) -> {
            final JsonObject jsonObject4 = GsonHelper.convertToJsonObject(jsonElement, "advancement");
            return Advancement.Builder.fromJson(jsonObject4, jsonDeserializationContext);
        })).registerTypeAdapter((Type)AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter((Class)Component.class, new Component.Serializer()).registerTypeHierarchyAdapter((Class)Style.class, new Style.Serializer()).registerTypeAdapterFactory((TypeAdapterFactory)new LowerCaseEnumTypeAdapterFactory()).create();
    }
}
