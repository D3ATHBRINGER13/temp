package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;

public class WorldTemplate extends ValueObject {
    private static final Logger LOGGER;
    public String id;
    public String name;
    public String version;
    public String author;
    public String link;
    public String image;
    public String trailer;
    public String recommendedPlayers;
    public WorldTemplateType type;
    
    public static WorldTemplate parse(final JsonObject jsonObject) {
        final WorldTemplate worldTemplate2 = new WorldTemplate();
        try {
            worldTemplate2.id = JsonUtils.getStringOr("id", jsonObject, "");
            worldTemplate2.name = JsonUtils.getStringOr("name", jsonObject, "");
            worldTemplate2.version = JsonUtils.getStringOr("version", jsonObject, "");
            worldTemplate2.author = JsonUtils.getStringOr("author", jsonObject, "");
            worldTemplate2.link = JsonUtils.getStringOr("link", jsonObject, "");
            worldTemplate2.image = JsonUtils.getStringOr("image", jsonObject, (String)null);
            worldTemplate2.trailer = JsonUtils.getStringOr("trailer", jsonObject, "");
            worldTemplate2.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", jsonObject, "");
            worldTemplate2.type = WorldTemplateType.valueOf(JsonUtils.getStringOr("type", jsonObject, WorldTemplateType.WORLD_TEMPLATE.name()));
        }
        catch (Exception exception3) {
            WorldTemplate.LOGGER.error("Could not parse WorldTemplate: " + exception3.getMessage());
        }
        return worldTemplate2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public enum WorldTemplateType {
        WORLD_TEMPLATE, 
        MINIGAME, 
        ADVENTUREMAP, 
        EXPERIENCE, 
        INSPIRATION;
    }
}
