package net.minecraft.client.gui.font;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.profiling.InactiveProfiler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.stream.Stream;
import com.google.gson.JsonArray;
import java.io.InputStream;
import java.util.Iterator;
import com.google.gson.Gson;
import com.google.common.collect.Lists;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilderType;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.packs.resources.Resource;
import java.util.function.Supplier;
import java.util.function.Predicate;
import com.google.gson.GsonBuilder;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.List;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.font.GlyphProvider;
import java.util.Set;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class FontManager implements AutoCloseable {
    private static final Logger LOGGER;
    private final Map<ResourceLocation, Font> fonts;
    private final Set<GlyphProvider> providers;
    private final TextureManager textureManager;
    private boolean forceUnicode;
    private final PreparableReloadListener reloadListener;
    
    public FontManager(final TextureManager dxc, final boolean boolean2) {
        this.fonts = (Map<ResourceLocation, Font>)Maps.newHashMap();
        this.providers = (Set<GlyphProvider>)Sets.newHashSet();
        this.reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>() {
            @Override
            protected Map<ResourceLocation, List<GlyphProvider>> prepare(final ResourceManager xi, final ProfilerFiller agn) {
                agn.startTick();
                final Gson gson4 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                final Map<ResourceLocation, List<GlyphProvider>> map5 = (Map<ResourceLocation, List<GlyphProvider>>)Maps.newHashMap();
                for (final ResourceLocation qv7 : xi.listResources("font", (Predicate<String>)(string -> string.endsWith(".json")))) {
                    final String string8 = qv7.getPath();
                    final ResourceLocation qv8 = new ResourceLocation(qv7.getNamespace(), string8.substring("font/".length(), string8.length() - ".json".length()));
                    final List<GlyphProvider> list10 = (List<GlyphProvider>)map5.computeIfAbsent(qv8, qv -> Lists.newArrayList((Object[])new GlyphProvider[] { new AllMissingGlyphProvider() }));
                    agn.push((Supplier<String>)qv8::toString);
                    try {
                        for (final Resource xh12 : xi.getResources(qv7)) {
                            agn.push((Supplier<String>)xh12::getSourceName);
                            try (final InputStream inputStream13 = xh12.getInputStream();
                                 final Reader reader15 = (Reader)new BufferedReader((Reader)new InputStreamReader(inputStream13, StandardCharsets.UTF_8))) {
                                agn.push("reading");
                                final JsonArray jsonArray17 = GsonHelper.getAsJsonArray((JsonObject)GsonHelper.<JsonObject>fromJson(gson4, reader15, JsonObject.class), "providers");
                                agn.popPush("parsing");
                                for (int integer18 = jsonArray17.size() - 1; integer18 >= 0; --integer18) {
                                    final JsonObject jsonObject19 = GsonHelper.convertToJsonObject(jsonArray17.get(integer18), new StringBuilder().append("providers[").append(integer18).append("]").toString());
                                    try {
                                        final String string9 = GsonHelper.getAsString(jsonObject19, "type");
                                        final GlyphProviderBuilderType dbc21 = GlyphProviderBuilderType.byName(string9);
                                        if (!FontManager.this.forceUnicode || dbc21 == GlyphProviderBuilderType.LEGACY_UNICODE || !qv8.equals(Minecraft.DEFAULT_FONT)) {
                                            agn.push(string9);
                                            list10.add(dbc21.create(jsonObject19).create(xi));
                                            agn.pop();
                                        }
                                    }
                                    catch (RuntimeException runtimeException20) {
                                        FontManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", qv8, xh12.getSourceName(), runtimeException20.getMessage());
                                    }
                                }
                                agn.pop();
                            }
                            catch (RuntimeException runtimeException21) {
                                FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", qv8, xh12.getSourceName(), runtimeException21.getMessage());
                            }
                            agn.pop();
                        }
                    }
                    catch (IOException iOException11) {
                        FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", qv8, iOException11.getMessage());
                    }
                    agn.push("caching");
                    for (char character11 = '\0'; character11 < '\uffff'; ++character11) {
                        if (character11 != ' ') {
                            for (final GlyphProvider ctw13 : Lists.reverse((List)list10)) {
                                if (ctw13.getGlyph(character11) != null) {
                                    break;
                                }
                            }
                        }
                    }
                    agn.pop();
                    agn.pop();
                }
                agn.endTick();
                return map5;
            }
            
            @Override
            protected void apply(final Map<ResourceLocation, List<GlyphProvider>> map, final ResourceManager xi, final ProfilerFiller agn) {
                agn.startTick();
                agn.push("reloading");
                Stream.concat(FontManager.this.fonts.keySet().stream(), map.keySet().stream()).distinct().forEach(qv -> {
                    final List<GlyphProvider> list4 = (List<GlyphProvider>)map.getOrDefault(qv, Collections.emptyList());
                    Collections.reverse((List)list4);
                    ((Font)FontManager.this.fonts.computeIfAbsent(qv, qv -> new Font(FontManager.this.textureManager, new FontSet(FontManager.this.textureManager, qv)))).reload(list4);
                });
                map.values().forEach(FontManager.this.providers::addAll);
                agn.pop();
                agn.endTick();
            }
        };
        this.textureManager = dxc;
        this.forceUnicode = boolean2;
    }
    
    @Nullable
    public Font get(final ResourceLocation qv) {
        return (Font)this.fonts.computeIfAbsent(qv, qv -> {
            final Font cyu3 = new Font(this.textureManager, new FontSet(this.textureManager, qv));
            cyu3.reload((List<GlyphProvider>)Lists.newArrayList((Object[])new GlyphProvider[] { new AllMissingGlyphProvider() }));
            return cyu3;
        });
    }
    
    public void setForceUnicode(final boolean boolean1, final Executor executor2, final Executor executor3) {
        if (boolean1 == this.forceUnicode) {
            return;
        }
        this.forceUnicode = boolean1;
        final ResourceManager xi5 = Minecraft.getInstance().getResourceManager();
        final PreparableReloadListener.PreparationBarrier a6 = new PreparableReloadListener.PreparationBarrier() {
            public <T> CompletableFuture<T> wait(final T object) {
                return (CompletableFuture<T>)CompletableFuture.completedFuture(object);
            }
        };
        this.reloadListener.reload(a6, xi5, InactiveProfiler.INACTIVE, InactiveProfiler.INACTIVE, executor2, executor3);
    }
    
    public PreparableReloadListener getReloadListener() {
        return this.reloadListener;
    }
    
    public void close() {
        this.fonts.values().forEach(Font::close);
        this.providers.forEach(GlyphProvider::close);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
