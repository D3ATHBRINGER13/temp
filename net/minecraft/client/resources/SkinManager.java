package net.minecraft.client.resources;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.common.collect.Multimap;
import com.mojang.authlib.minecraft.InsecureTextureException;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.HttpTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.HttpTextureProcessor;
import net.minecraft.client.renderer.MobSkinTextureProcessor;
import com.google.common.hash.Hashing;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import net.minecraft.client.renderer.texture.TextureManager;
import java.util.concurrent.ExecutorService;

public class SkinManager {
    private static final ExecutorService EXECUTOR_SERVICE;
    private final TextureManager textureManager;
    private final File skinsDirectory;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> insecureSkinCache;
    
    public SkinManager(final TextureManager dxc, final File file, final MinecraftSessionService minecraftSessionService) {
        this.textureManager = dxc;
        this.skinsDirectory = file;
        this.sessionService = minecraftSessionService;
        this.insecureSkinCache = (LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>)CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(final GameProfile gameProfile) throws Exception {
                try {
                    return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)Minecraft.getInstance().getMinecraftSessionService().getTextures(gameProfile, false);
                }
                catch (Throwable throwable3) {
                    return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)Maps.newHashMap();
                }
            }
        });
    }
    
    public ResourceLocation registerTexture(final MinecraftProfileTexture minecraftProfileTexture, final MinecraftProfileTexture.Type type) {
        return this.registerTexture(minecraftProfileTexture, type, null);
    }
    
    public ResourceLocation registerTexture(final MinecraftProfileTexture minecraftProfileTexture, final MinecraftProfileTexture.Type type, @Nullable final SkinTextureCallback a) {
        final String string5 = Hashing.sha1().hashUnencodedChars((CharSequence)minecraftProfileTexture.getHash()).toString();
        final ResourceLocation qv6 = new ResourceLocation("skins/" + string5);
        final TextureObject dxd7 = this.textureManager.getTexture(qv6);
        if (dxd7 != null) {
            if (a != null) {
                a.onSkinTextureAvailable(type, qv6, minecraftProfileTexture);
            }
        }
        else {
            final File file8 = new File(this.skinsDirectory, (string5.length() > 2) ? string5.substring(0, 2) : "xx");
            final File file9 = new File(file8, string5);
            final HttpTextureProcessor dnd10 = (type == MinecraftProfileTexture.Type.SKIN) ? new MobSkinTextureProcessor() : null;
            final HttpTexture dws11 = new HttpTexture(file9, minecraftProfileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkin(), new HttpTextureProcessor() {
                public NativeImage process(final NativeImage cuj) {
                    if (dnd10 != null) {
                        return dnd10.process(cuj);
                    }
                    return cuj;
                }
                
                public void onTextureDownloaded() {
                    if (dnd10 != null) {
                        dnd10.onTextureDownloaded();
                    }
                    if (a != null) {
                        a.onSkinTextureAvailable(type, qv6, minecraftProfileTexture);
                    }
                }
            });
            this.textureManager.register(qv6, dws11);
        }
        return qv6;
    }
    
    public void registerSkins(final GameProfile gameProfile, final SkinTextureCallback a, final boolean boolean3) {
        SkinManager.EXECUTOR_SERVICE.submit(() -> {
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map5 = (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)Maps.newHashMap();
            try {
                map5.putAll(this.sessionService.getTextures(gameProfile, boolean3));
            }
            catch (InsecureTextureException ex) {}
            if (map5.isEmpty()) {
                gameProfile.getProperties().clear();
                if (gameProfile.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
                    gameProfile.getProperties().putAll((Multimap)Minecraft.getInstance().getProfileProperties());
                    map5.putAll(this.sessionService.getTextures(gameProfile, false));
                }
                else {
                    this.sessionService.fillProfileProperties(gameProfile, boolean3);
                    try {
                        map5.putAll(this.sessionService.getTextures(gameProfile, boolean3));
                    }
                    catch (InsecureTextureException ex2) {}
                }
            }
            Minecraft.getInstance().execute(() -> {
                if (map5.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    this.registerTexture((MinecraftProfileTexture)map5.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, a);
                }
                if (map5.containsKey(MinecraftProfileTexture.Type.CAPE)) {
                    this.registerTexture((MinecraftProfileTexture)map5.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, a);
                }
            });
        });
    }
    
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getInsecureSkinInformation(final GameProfile gameProfile) {
        return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)this.insecureSkinCache.getUnchecked(gameProfile);
    }
    
    static {
        EXECUTOR_SERVICE = (ExecutorService)new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, (BlockingQueue)new LinkedBlockingQueue());
    }
    
    public interface SkinTextureCallback {
        void onSkinTextureAvailable(final MinecraftProfileTexture.Type type, final ResourceLocation qv, final MinecraftProfileTexture minecraftProfileTexture);
    }
}
