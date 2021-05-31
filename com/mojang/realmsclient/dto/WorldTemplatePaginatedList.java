package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class WorldTemplatePaginatedList extends ValueObject {
    private static final Logger LOGGER;
    public List<WorldTemplate> templates;
    public int page;
    public int size;
    public int total;
    
    public WorldTemplatePaginatedList() {
    }
    
    public WorldTemplatePaginatedList(final int integer) {
        this.templates = (List<WorldTemplate>)Collections.emptyList();
        this.page = 0;
        this.size = integer;
        this.total = -1;
    }
    
    public boolean isLastPage() {
        return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
    }
    
    public static WorldTemplatePaginatedList parse(final String string) {
        final WorldTemplatePaginatedList worldTemplatePaginatedList2 = new WorldTemplatePaginatedList();
        worldTemplatePaginatedList2.templates = (List<WorldTemplate>)new ArrayList();
        try {
            final JsonParser jsonParser3 = new JsonParser();
            final JsonObject jsonObject4 = jsonParser3.parse(string).getAsJsonObject();
            if (jsonObject4.get("templates").isJsonArray()) {
                final Iterator<JsonElement> iterator5 = (Iterator<JsonElement>)jsonObject4.get("templates").getAsJsonArray().iterator();
                while (iterator5.hasNext()) {
                    worldTemplatePaginatedList2.templates.add(WorldTemplate.parse(((JsonElement)iterator5.next()).getAsJsonObject()));
                }
            }
            worldTemplatePaginatedList2.page = JsonUtils.getIntOr("page", jsonObject4, 0);
            worldTemplatePaginatedList2.size = JsonUtils.getIntOr("size", jsonObject4, 0);
            worldTemplatePaginatedList2.total = JsonUtils.getIntOr("total", jsonObject4, 0);
        }
        catch (Exception exception3) {
            WorldTemplatePaginatedList.LOGGER.error("Could not parse WorldTemplatePaginatedList: " + exception3.getMessage());
        }
        return worldTemplatePaginatedList2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
