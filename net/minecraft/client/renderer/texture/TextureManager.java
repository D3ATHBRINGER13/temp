package net.minecraft.client.renderer.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.profiling.ProfilerFiller;
import com.mojang.blaze3d.platform.TextureUtil;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import java.io.IOException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class TextureManager implements Tickable, PreparableReloadListener {
    private static final Logger LOGGER;
    public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE;
    private final Map<ResourceLocation, TextureObject> byPath;
    private final List<Tickable> tickableTextures;
    private final Map<String, Integer> prefixRegister;
    private final ResourceManager resourceManager;
    
    public TextureManager(final ResourceManager xi) {
        this.byPath = (Map<ResourceLocation, TextureObject>)Maps.newHashMap();
        this.tickableTextures = (List<Tickable>)Lists.newArrayList();
        this.prefixRegister = (Map<String, Integer>)Maps.newHashMap();
        this.resourceManager = xi;
    }
    
    public void bind(final ResourceLocation qv) {
        TextureObject dxd3 = (TextureObject)this.byPath.get(qv);
        if (dxd3 == null) {
            dxd3 = new SimpleTexture(qv);
            this.register(qv, dxd3);
        }
        dxd3.bind();
    }
    
    public boolean register(final ResourceLocation qv, final TickableTextureObject dxf) {
        if (this.register(qv, (TextureObject)dxf)) {
            this.tickableTextures.add(dxf);
            return true;
        }
        return false;
    }
    
    public boolean register(final ResourceLocation qv, TextureObject dxd) {
        boolean boolean4 = true;
        try {
            dxd.load(this.resourceManager);
        }
        catch (IOException iOException5) {
            if (qv != TextureManager.INTENTIONAL_MISSING_TEXTURE) {
                TextureManager.LOGGER.warn("Failed to load texture: {}", qv, iOException5);
            }
            dxd = MissingTextureAtlasSprite.getTexture();
            this.byPath.put(qv, dxd);
            boolean4 = false;
        }
        catch (Throwable throwable5) {
            final CrashReport d6 = CrashReport.forThrowable(throwable5, "Registering texture");
            final CrashReportCategory e7 = d6.addCategory("Resource location being registered");
            final TextureObject dxd2 = dxd;
            e7.setDetail("Resource location", qv);
            e7.setDetail("Texture object class", (CrashReportDetail<String>)(() -> dxd2.getClass().getName()));
            throw new ReportedException(d6);
        }
        this.byPath.put(qv, dxd);
        return boolean4;
    }
    
    public TextureObject getTexture(final ResourceLocation qv) {
        return (TextureObject)this.byPath.get(qv);
    }
    
    public ResourceLocation register(final String string, final DynamicTexture dwr) {
        Integer integer4 = (Integer)this.prefixRegister.get(string);
        if (integer4 == null) {
            integer4 = 1;
        }
        else {
            ++integer4;
        }
        this.prefixRegister.put(string, integer4);
        final ResourceLocation qv5 = new ResourceLocation(String.format("dynamic/%s_%d", new Object[] { string, integer4 }));
        this.register(qv5, dwr);
        return qv5;
    }
    
    public CompletableFuture<Void> preload(final ResourceLocation qv, final Executor executor) {
        if (!this.byPath.containsKey(qv)) {
            final PreloadedTexture dww4 = new PreloadedTexture(this.resourceManager, qv, executor);
            this.byPath.put(qv, dww4);
            return (CompletableFuture<Void>)dww4.getFuture().thenRunAsync(() -> this.register(qv, dww4), (Executor)Minecraft.getInstance());
        }
        return (CompletableFuture<Void>)CompletableFuture.completedFuture(null);
    }
    
    public void tick() {
        for (final Tickable dxe3 : this.tickableTextures) {
            dxe3.tick();
        }
    }
    
    public void release(final ResourceLocation qv) {
        final TextureObject dxd3 = this.getTexture(qv);
        if (dxd3 != null) {
            TextureUtil.releaseTextureId(dxd3.getId());
        }
    }
    
    public CompletableFuture<Void> reload(final PreparationBarrier a, final ResourceManager xi, final ProfilerFiller agn3, final ProfilerFiller agn4, final Executor executor5, final Executor executor6) {
        return (CompletableFuture<Void>)CompletableFuture.allOf(new CompletableFuture[] { TitleScreen.preloadResources(this, executor5), this.preload(AbstractWidget.WIDGETS_LOCATION, executor5) }).thenCompose(a::wait).thenAcceptAsync(void3 -> {
            MissingTextureAtlasSprite.getTexture();
            final Iterator<Map.Entry<ResourceLocation, TextureObject>> iterator5 = (Iterator<Map.Entry<ResourceLocation, TextureObject>>)this.byPath.entrySet().iterator();
            while (iterator5.hasNext()) {
                final Map.Entry<ResourceLocation, TextureObject> entry6 = (Map.Entry<ResourceLocation, TextureObject>)iterator5.next();
                final ResourceLocation qv7 = (ResourceLocation)entry6.getKey();
                final TextureObject dxd8 = (TextureObject)entry6.getValue();
                if (dxd8 == MissingTextureAtlasSprite.getTexture() && !qv7.equals(MissingTextureAtlasSprite.getLocation())) {
                    iterator5.remove();
                }
                else {
                    dxd8.reset(this, xi, qv7, executor6);
                }
            }
        }, executor6);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
    }
}
