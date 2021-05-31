package net.minecraft.client.renderer.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import com.mojang.blaze3d.platform.PngInfo;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.CrashReportDetail;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import java.util.stream.Collectors;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.CrashReportCategory;
import java.util.Iterator;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import com.mojang.blaze3d.platform.TextureUtil;
import java.util.Collection;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.Minecraft;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.Set;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class TextureAtlas extends AbstractTexture implements TickableTextureObject {
    private static final Logger LOGGER;
    public static final ResourceLocation LOCATION_BLOCKS;
    public static final ResourceLocation LOCATION_PARTICLES;
    public static final ResourceLocation LOCATION_PAINTINGS;
    public static final ResourceLocation LOCATION_MOB_EFFECTS;
    private final List<TextureAtlasSprite> animatedTextures;
    private final Set<ResourceLocation> sprites;
    private final Map<ResourceLocation, TextureAtlasSprite> texturesByName;
    private final String path;
    private final int maxSupportedTextureSize;
    private int maxMipLevel;
    private final TextureAtlasSprite missingTextureSprite;
    
    public TextureAtlas(final String string) {
        this.animatedTextures = (List<TextureAtlasSprite>)Lists.newArrayList();
        this.sprites = (Set<ResourceLocation>)Sets.newHashSet();
        this.texturesByName = (Map<ResourceLocation, TextureAtlasSprite>)Maps.newHashMap();
        this.missingTextureSprite = MissingTextureAtlasSprite.newInstance();
        this.path = string;
        this.maxSupportedTextureSize = Minecraft.maxSupportedTextureSize();
    }
    
    public void load(final ResourceManager xi) throws IOException {
    }
    
    public void reload(final Preparations a) {
        this.sprites.clear();
        this.sprites.addAll((Collection)a.sprites);
        TextureAtlas.LOGGER.info("Created: {}x{} {}-atlas", a.width, a.height, this.path);
        TextureUtil.prepareImage(this.getId(), this.maxMipLevel, a.width, a.height);
        this.clearTextureData();
        for (final TextureAtlasSprite dxb4 : a.regions) {
            this.texturesByName.put(dxb4.getName(), dxb4);
            try {
                dxb4.uploadFirstFrame();
            }
            catch (Throwable throwable5) {
                final CrashReport d6 = CrashReport.forThrowable(throwable5, "Stitching texture atlas");
                final CrashReportCategory e7 = d6.addCategory("Texture being stitched together");
                e7.setDetail("Atlas path", this.path);
                e7.setDetail("Sprite", dxb4);
                throw new ReportedException(d6);
            }
            if (dxb4.isAnimation()) {
                this.animatedTextures.add(dxb4);
            }
        }
    }
    
    public Preparations prepareToStitch(final ResourceManager xi, final Iterable<ResourceLocation> iterable, final ProfilerFiller agn) {
        final Set<ResourceLocation> set5 = (Set<ResourceLocation>)Sets.newHashSet();
        agn.push("preparing");
        iterable.forEach(qv -> {
            if (qv == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
            set5.add(qv);
        });
        final int integer6 = this.maxSupportedTextureSize;
        final Stitcher dwy7 = new Stitcher(integer6, integer6, this.maxMipLevel);
        int integer7 = Integer.MAX_VALUE;
        int integer8 = 1 << this.maxMipLevel;
        agn.popPush("extracting_frames");
        for (final TextureAtlasSprite dxb11 : this.getBasicSpriteInfos(xi, set5)) {
            integer7 = Math.min(integer7, Math.min(dxb11.getWidth(), dxb11.getHeight()));
            final int integer9 = Math.min(Integer.lowestOneBit(dxb11.getWidth()), Integer.lowestOneBit(dxb11.getHeight()));
            if (integer9 < integer8) {
                TextureAtlas.LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", dxb11.getName(), dxb11.getWidth(), dxb11.getHeight(), Mth.log2(integer8), Mth.log2(integer9));
                integer8 = integer9;
            }
            dwy7.registerSprite(dxb11);
        }
        final int integer10 = Math.min(integer7, integer8);
        final int integer11 = Mth.log2(integer10);
        if (integer11 < this.maxMipLevel) {
            TextureAtlas.LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.path, this.maxMipLevel, integer11, integer10);
            this.maxMipLevel = integer11;
        }
        agn.popPush("mipmapping");
        this.missingTextureSprite.applyMipmapping(this.maxMipLevel);
        agn.popPush("register");
        dwy7.registerSprite(this.missingTextureSprite);
        agn.popPush("stitching");
        try {
            dwy7.stitch();
        }
        catch (StitcherException dwz12) {
            final CrashReport d13 = CrashReport.forThrowable((Throwable)dwz12, "Stitching");
            final CrashReportCategory e14 = d13.addCategory("Stitcher");
            e14.setDetail("Sprites", dwz12.getAllSprites().stream().map(dxb -> String.format("%s[%dx%d]", new Object[] { dxb.getName(), dxb.getWidth(), dxb.getHeight() })).collect(Collectors.joining(",")));
            e14.setDetail("Max Texture Size", integer6);
            throw new ReportedException(d13);
        }
        agn.popPush("loading");
        final List<TextureAtlasSprite> list12 = this.getLoadedSprites(xi, dwy7);
        agn.pop();
        return new Preparations(set5, dwy7.getWidth(), dwy7.getHeight(), list12);
    }
    
    private Collection<TextureAtlasSprite> getBasicSpriteInfos(final ResourceManager xi, final Set<ResourceLocation> set) {
        final List<CompletableFuture<?>> list4 = (List<CompletableFuture<?>>)new ArrayList();
        final ConcurrentLinkedQueue<TextureAtlasSprite> concurrentLinkedQueue5 = (ConcurrentLinkedQueue<TextureAtlasSprite>)new ConcurrentLinkedQueue();
        for (final ResourceLocation qv7 : set) {
            if (this.missingTextureSprite.getName().equals(qv7)) {
                continue;
            }
            list4.add(CompletableFuture.runAsync(() -> {
                final ResourceLocation qv2 = this.getResourceLocation(qv7);
                TextureAtlasSprite dxb6;
                try (final Resource xh7 = xi.getResource(qv2)) {
                    final PngInfo cuk9 = new PngInfo(xh7.toString(), xh7.getInputStream());
                    final AnimationMetadataSection dyd10 = xh7.<AnimationMetadataSection>getMetadata((MetadataSectionSerializer<AnimationMetadataSection>)AnimationMetadataSection.SERIALIZER);
                    dxb6 = new TextureAtlasSprite(qv7, cuk9, dyd10);
                }
                catch (RuntimeException runtimeException7) {
                    TextureAtlas.LOGGER.error("Unable to parse metadata from {} : {}", (Object)qv2, (Object)runtimeException7);
                    return;
                }
                catch (IOException iOException7) {
                    TextureAtlas.LOGGER.error("Using missing texture, unable to load {} : {}", (Object)qv2, (Object)iOException7);
                    return;
                }
                concurrentLinkedQueue5.add((Object)dxb6);
            }, Util.backgroundExecutor()));
        }
        CompletableFuture.allOf((CompletableFuture[])list4.toArray((Object[])new CompletableFuture[0])).join();
        return (Collection<TextureAtlasSprite>)concurrentLinkedQueue5;
    }
    
    private List<TextureAtlasSprite> getLoadedSprites(final ResourceManager xi, final Stitcher dwy) {
        final ConcurrentLinkedQueue<TextureAtlasSprite> concurrentLinkedQueue4 = (ConcurrentLinkedQueue<TextureAtlasSprite>)new ConcurrentLinkedQueue();
        final List<CompletableFuture<?>> list5 = (List<CompletableFuture<?>>)new ArrayList();
        for (final TextureAtlasSprite dxb7 : dwy.gatherSprites()) {
            if (dxb7 == this.missingTextureSprite) {
                concurrentLinkedQueue4.add(dxb7);
            }
            else {
                list5.add(CompletableFuture.runAsync(() -> {
                    if (this.load(xi, dxb7)) {
                        concurrentLinkedQueue4.add((Object)dxb7);
                    }
                }, Util.backgroundExecutor()));
            }
        }
        CompletableFuture.allOf((CompletableFuture[])list5.toArray((Object[])new CompletableFuture[0])).join();
        return (List<TextureAtlasSprite>)new ArrayList((Collection)concurrentLinkedQueue4);
    }
    
    private boolean load(final ResourceManager xi, final TextureAtlasSprite dxb) {
        final ResourceLocation qv4 = this.getResourceLocation(dxb.getName());
        Resource xh5 = null;
        try {
            xh5 = xi.getResource(qv4);
            dxb.loadData(xh5, this.maxMipLevel + 1);
        }
        catch (RuntimeException runtimeException6) {
            TextureAtlas.LOGGER.error("Unable to parse metadata from {}", qv4, runtimeException6);
            return false;
        }
        catch (IOException iOException6) {
            TextureAtlas.LOGGER.error("Using missing texture, unable to load {}", qv4, iOException6);
            return false;
        }
        finally {
            IOUtils.closeQuietly((Closeable)xh5);
        }
        try {
            dxb.applyMipmapping(this.maxMipLevel);
        }
        catch (Throwable throwable6) {
            final CrashReport d7 = CrashReport.forThrowable(throwable6, "Applying mipmap");
            final CrashReportCategory e8 = d7.addCategory("Sprite being mipmapped");
            e8.setDetail("Sprite name", (CrashReportDetail<String>)(() -> dxb.getName().toString()));
            e8.setDetail("Sprite size", (CrashReportDetail<String>)(() -> new StringBuilder().append(dxb.getWidth()).append(" x ").append(dxb.getHeight()).toString()));
            e8.setDetail("Sprite frames", (CrashReportDetail<String>)(() -> new StringBuilder().append(dxb.getFrameCount()).append(" frames").toString()));
            e8.setDetail("Mipmap levels", this.maxMipLevel);
            throw new ReportedException(d7);
        }
        return true;
    }
    
    private ResourceLocation getResourceLocation(final ResourceLocation qv) {
        return new ResourceLocation(qv.getNamespace(), String.format("%s/%s%s", new Object[] { this.path, qv.getPath(), ".png" }));
    }
    
    public TextureAtlasSprite getTexture(final String string) {
        return this.getSprite(new ResourceLocation(string));
    }
    
    public void cycleAnimationFrames() {
        this.bind();
        for (final TextureAtlasSprite dxb3 : this.animatedTextures) {
            dxb3.cycleFrames();
        }
    }
    
    public void tick() {
        this.cycleAnimationFrames();
    }
    
    public void setMaxMipLevel(final int integer) {
        this.maxMipLevel = integer;
    }
    
    public TextureAtlasSprite getSprite(final ResourceLocation qv) {
        final TextureAtlasSprite dxb3 = (TextureAtlasSprite)this.texturesByName.get(qv);
        if (dxb3 == null) {
            return this.missingTextureSprite;
        }
        return dxb3;
    }
    
    public void clearTextureData() {
        for (final TextureAtlasSprite dxb3 : this.texturesByName.values()) {
            dxb3.wipeFrameData();
        }
        this.texturesByName.clear();
        this.animatedTextures.clear();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        LOCATION_BLOCKS = new ResourceLocation("textures/atlas/blocks.png");
        LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
        LOCATION_PAINTINGS = new ResourceLocation("textures/atlas/paintings.png");
        LOCATION_MOB_EFFECTS = new ResourceLocation("textures/atlas/mob_effects.png");
    }
    
    public static class Preparations {
        final Set<ResourceLocation> sprites;
        final int width;
        final int height;
        final List<TextureAtlasSprite> regions;
        
        public Preparations(final Set<ResourceLocation> set, final int integer2, final int integer3, final List<TextureAtlasSprite> list) {
            this.sprites = set;
            this.width = integer2;
            this.height = integer3;
            this.regions = list;
        }
    }
}
