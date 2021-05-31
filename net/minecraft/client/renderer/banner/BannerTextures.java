package net.minecraft.client.renderer.banner;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.Minecraft;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class BannerTextures {
    public static final TextureCache BANNER_CACHE;
    public static final TextureCache SHIELD_CACHE;
    public static final ResourceLocation NO_PATTERN_SHIELD;
    public static final ResourceLocation DEFAULT_PATTERN_BANNER;
    
    static {
        BANNER_CACHE = new TextureCache("banner_", new ResourceLocation("textures/entity/banner_base.png"), "textures/entity/banner/");
        SHIELD_CACHE = new TextureCache("shield_", new ResourceLocation("textures/entity/shield_base.png"), "textures/entity/shield/");
        NO_PATTERN_SHIELD = new ResourceLocation("textures/entity/shield_base_nopattern.png");
        DEFAULT_PATTERN_BANNER = new ResourceLocation("textures/entity/banner/base.png");
    }
    
    public static class TextureCache {
        private final Map<String, TimestampedBannerTexture> cache;
        private final ResourceLocation baseResource;
        private final String resourceNameBase;
        private final String hashPrefix;
        
        public TextureCache(final String string1, final ResourceLocation qv, final String string3) {
            this.cache = (Map<String, TimestampedBannerTexture>)Maps.newLinkedHashMap();
            this.hashPrefix = string1;
            this.baseResource = qv;
            this.resourceNameBase = string3;
        }
        
        @Nullable
        public ResourceLocation getTextureLocation(String string, final List<BannerPattern> list2, final List<DyeColor> list3) {
            if (string.isEmpty()) {
                return null;
            }
            if (list2.isEmpty() || list3.isEmpty()) {
                return MissingTextureAtlasSprite.getLocation();
            }
            string = this.hashPrefix + string;
            TimestampedBannerTexture b5 = (TimestampedBannerTexture)this.cache.get(string);
            if (b5 == null) {
                if (this.cache.size() >= 256 && !this.freeCacheSlot()) {
                    return BannerTextures.DEFAULT_PATTERN_BANNER;
                }
                final List<String> list4 = (List<String>)Lists.newArrayList();
                for (final BannerPattern btp8 : list2) {
                    list4.add((this.resourceNameBase + btp8.getFilename() + ".png"));
                }
                b5 = new TimestampedBannerTexture();
                b5.textureLocation = new ResourceLocation(string);
                Minecraft.getInstance().getTextureManager().register(b5.textureLocation, new LayeredColorMaskTexture(this.baseResource, list4, list3));
                this.cache.put(string, b5);
            }
            b5.lastUseMilliseconds = Util.getMillis();
            return b5.textureLocation;
        }
        
        private boolean freeCacheSlot() {
            final long long2 = Util.getMillis();
            final Iterator<String> iterator4 = (Iterator<String>)this.cache.keySet().iterator();
            while (iterator4.hasNext()) {
                final String string5 = (String)iterator4.next();
                final TimestampedBannerTexture b6 = (TimestampedBannerTexture)this.cache.get(string5);
                if (long2 - b6.lastUseMilliseconds > 5000L) {
                    Minecraft.getInstance().getTextureManager().release(b6.textureLocation);
                    iterator4.remove();
                    return true;
                }
            }
            return this.cache.size() < 256;
        }
    }
    
    static class TimestampedBannerTexture {
        public long lastUseMilliseconds;
        public ResourceLocation textureLocation;
        
        private TimestampedBannerTexture() {
        }
    }
}
