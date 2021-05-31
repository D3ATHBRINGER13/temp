package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerLists extends ValueObject {
    private static final Logger LOGGER;
    public List<RealmsServerPlayerList> servers;
    
    public static RealmsServerPlayerLists parse(final String string) {
        final RealmsServerPlayerLists realmsServerPlayerLists2 = new RealmsServerPlayerLists();
        realmsServerPlayerLists2.servers = (List<RealmsServerPlayerList>)new ArrayList();
        try {
            final JsonParser jsonParser3 = new JsonParser();
            final JsonObject jsonObject4 = jsonParser3.parse(string).getAsJsonObject();
            if (jsonObject4.get("lists").isJsonArray()) {
                final JsonArray jsonArray5 = jsonObject4.get("lists").getAsJsonArray();
                final Iterator<JsonElement> iterator6 = (Iterator<JsonElement>)jsonArray5.iterator();
                while (iterator6.hasNext()) {
                    realmsServerPlayerLists2.servers.add(RealmsServerPlayerList.parse(((JsonElement)iterator6.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception exception3) {
            RealmsServerPlayerLists.LOGGER.error("Could not parse RealmsServerPlayerLists: " + exception3.getMessage());
        }
        return realmsServerPlayerLists2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
