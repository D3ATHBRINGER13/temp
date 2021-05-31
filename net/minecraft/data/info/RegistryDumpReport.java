package net.minecraft.data.info;

import com.google.gson.GsonBuilder;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.WritableRegistry;
import java.io.IOException;
import java.nio.file.Path;
import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import com.google.gson.JsonObject;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import net.minecraft.data.DataProvider;

public class RegistryDumpReport implements DataProvider {
    private static final Gson GSON;
    private final DataGenerator generator;
    
    public RegistryDumpReport(final DataGenerator gk) {
        this.generator = gk;
    }
    
    public void run(final HashCache gm) throws IOException {
        final JsonObject jsonObject3 = new JsonObject();
        Registry.REGISTRY.keySet().forEach(qv -> jsonObject3.add(qv.toString(), RegistryDumpReport.dumpRegistry(Registry.REGISTRY.get(qv))));
        final Path path4 = this.generator.getOutputFolder().resolve("reports/registries.json");
        DataProvider.save(RegistryDumpReport.GSON, gm, (JsonElement)jsonObject3, path4);
    }
    
    private static <T> JsonElement dumpRegistry(final WritableRegistry<T> ft) {
        final JsonObject jsonObject2 = new JsonObject();
        if (ft instanceof DefaultedRegistry) {
            final ResourceLocation qv3 = ((DefaultedRegistry)ft).getDefaultKey();
            jsonObject2.addProperty("default", qv3.toString());
        }
        final int integer3 = Registry.REGISTRY.getId(ft);
        jsonObject2.addProperty("protocol_id", (Number)integer3);
        final JsonObject jsonObject3 = new JsonObject();
        for (final ResourceLocation qv4 : ft.keySet()) {
            final T object7 = ft.get(qv4);
            final int integer4 = ft.getId(object7);
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("protocol_id", (Number)integer4);
            jsonObject3.add(qv4.toString(), (JsonElement)jsonObject4);
        }
        jsonObject2.add("entries", (JsonElement)jsonObject3);
        return (JsonElement)jsonObject2;
    }
    
    public String getName() {
        return "Registry Dump";
    }
    
    static {
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}
