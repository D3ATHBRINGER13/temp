package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonObject;
import java.util.List;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerList extends ValueObject {
    private static final Logger LOGGER;
    private static final JsonParser jsonParser;
    public long serverId;
    public List<String> players;
    
    public static RealmsServerPlayerList parse(final JsonObject jsonObject) {
        final RealmsServerPlayerList realmsServerPlayerList2 = new RealmsServerPlayerList();
        try {
            realmsServerPlayerList2.serverId = JsonUtils.getLongOr("serverId", jsonObject, -1L);
            final String string3 = JsonUtils.getStringOr("playerList", jsonObject, (String)null);
            if (string3 != null) {
                final JsonElement jsonElement4 = RealmsServerPlayerList.jsonParser.parse(string3);
                if (jsonElement4.isJsonArray()) {
                    realmsServerPlayerList2.players = parsePlayers(jsonElement4.getAsJsonArray());
                }
                else {
                    realmsServerPlayerList2.players = (List<String>)new ArrayList();
                }
            }
            else {
                realmsServerPlayerList2.players = (List<String>)new ArrayList();
            }
        }
        catch (Exception exception3) {
            RealmsServerPlayerList.LOGGER.error("Could not parse RealmsServerPlayerList: " + exception3.getMessage());
        }
        return realmsServerPlayerList2;
    }
    
    private static List<String> parsePlayers(final JsonArray jsonArray) {
        final ArrayList<String> arrayList2 = (ArrayList<String>)new ArrayList();
        for (final JsonElement jsonElement4 : jsonArray) {
            try {
                arrayList2.add(jsonElement4.getAsString());
            }
            catch (Exception ex) {}
        }
        return (List<String>)arrayList2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        jsonParser = new JsonParser();
    }
}
