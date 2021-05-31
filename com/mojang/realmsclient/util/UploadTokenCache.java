package com.mojang.realmsclient.util;

import java.util.HashMap;
import java.util.Map;

public class UploadTokenCache {
    private static final Map<Long, String> tokenCache;
    
    public static String get(final long long1) {
        return (String)UploadTokenCache.tokenCache.get(long1);
    }
    
    public static void invalidate(final long long1) {
        UploadTokenCache.tokenCache.remove(long1);
    }
    
    public static void put(final long long1, final String string) {
        UploadTokenCache.tokenCache.put(long1, string);
    }
    
    static {
        tokenCache = (Map)new HashMap();
    }
}
