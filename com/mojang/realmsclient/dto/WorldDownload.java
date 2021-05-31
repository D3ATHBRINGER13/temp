package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;

public class WorldDownload extends ValueObject {
    private static final Logger LOGGER;
    public String downloadLink;
    public String resourcePackUrl;
    public String resourcePackHash;
    
    public static WorldDownload parse(final String string) {
        final JsonParser jsonParser2 = new JsonParser();
        final JsonObject jsonObject3 = jsonParser2.parse(string).getAsJsonObject();
        final WorldDownload worldDownload4 = new WorldDownload();
        try {
            worldDownload4.downloadLink = JsonUtils.getStringOr("downloadLink", jsonObject3, "");
            worldDownload4.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", jsonObject3, "");
            worldDownload4.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", jsonObject3, "");
        }
        catch (Exception exception5) {
            WorldDownload.LOGGER.error("Could not parse WorldDownload: " + exception5.getMessage());
        }
        return worldDownload4;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
