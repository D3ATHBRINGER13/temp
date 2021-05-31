package com.mojang.realmsclient.util;

import com.mojang.util.UUIDTypeAdapter;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import net.minecraft.realms.Realms;
import java.util.HashMap;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

public class RealmsUtil {
    private static final YggdrasilAuthenticationService authenticationService;
    private static final MinecraftSessionService sessionService;
    public static LoadingCache<String, GameProfile> gameProfileCache;
    
    public static String uuidToName(final String string) throws Exception {
        final GameProfile gameProfile2 = (GameProfile)RealmsUtil.gameProfileCache.get(string);
        return gameProfile2.getName();
    }
    
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(final String string) {
        try {
            final GameProfile gameProfile2 = (GameProfile)RealmsUtil.gameProfileCache.get(string);
            return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)RealmsUtil.sessionService.getTextures(gameProfile2, false);
        }
        catch (Exception exception2) {
            return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)new HashMap();
        }
    }
    
    public static void browseTo(final String string) {
        Realms.openUri(string);
    }
    
    public static String convertToAgePresentation(final Long long1) {
        if (long1 < 0L) {
            return "right now";
        }
        final long long2 = long1 / 1000L;
        if (long2 < 60L) {
            return new StringBuilder().append((long2 == 1L) ? "1 second" : new StringBuilder().append(long2).append(" seconds").toString()).append(" ago").toString();
        }
        if (long2 < 3600L) {
            final long long3 = long2 / 60L;
            return new StringBuilder().append((long3 == 1L) ? "1 minute" : new StringBuilder().append(long3).append(" minutes").toString()).append(" ago").toString();
        }
        if (long2 < 86400L) {
            final long long3 = long2 / 3600L;
            return new StringBuilder().append((long3 == 1L) ? "1 hour" : new StringBuilder().append(long3).append(" hours").toString()).append(" ago").toString();
        }
        final long long3 = long2 / 86400L;
        return new StringBuilder().append((long3 == 1L) ? "1 day" : new StringBuilder().append(long3).append(" days").toString()).append(" ago").toString();
    }
    
    static {
        authenticationService = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
        sessionService = RealmsUtil.authenticationService.createMinecraftSessionService();
        RealmsUtil.gameProfileCache = (LoadingCache<String, GameProfile>)CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, GameProfile>() {
            public GameProfile load(final String string) throws Exception {
                final GameProfile gameProfile3 = RealmsUtil.sessionService.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), (String)null), false);
                if (gameProfile3 == null) {
                    throw new Exception("Couldn't get profile");
                }
                return gameProfile3;
            }
        });
    }
}
