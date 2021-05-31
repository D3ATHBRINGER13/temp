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

public class RealmsServerList extends ValueObject {
    private static final Logger LOGGER;
    public List<RealmsServer> servers;
    
    public static RealmsServerList parse(final String string) {
        final RealmsServerList realmsServerList2 = new RealmsServerList();
        realmsServerList2.servers = (List<RealmsServer>)new ArrayList();
        try {
            final JsonParser jsonParser3 = new JsonParser();
            final JsonObject jsonObject4 = jsonParser3.parse(string).getAsJsonObject();
            if (jsonObject4.get("servers").isJsonArray()) {
                final JsonArray jsonArray5 = jsonObject4.get("servers").getAsJsonArray();
                final Iterator<JsonElement> iterator6 = (Iterator<JsonElement>)jsonArray5.iterator();
                while (iterator6.hasNext()) {
                    realmsServerList2.servers.add(RealmsServer.parse(((JsonElement)iterator6.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception exception3) {
            RealmsServerList.LOGGER.error("Could not parse McoServerList: " + exception3.getMessage());
        }
        return realmsServerList2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
