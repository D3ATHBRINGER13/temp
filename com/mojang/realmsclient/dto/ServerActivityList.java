package com.mojang.realmsclient.dto;

import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class ServerActivityList extends ValueObject {
    public long periodInMillis;
    public List<ServerActivity> serverActivities;
    
    public ServerActivityList() {
        this.serverActivities = (List<ServerActivity>)new ArrayList();
    }
    
    public static ServerActivityList parse(final String string) {
        final ServerActivityList serverActivityList2 = new ServerActivityList();
        final JsonParser jsonParser3 = new JsonParser();
        try {
            final JsonElement jsonElement4 = jsonParser3.parse(string);
            final JsonObject jsonObject5 = jsonElement4.getAsJsonObject();
            serverActivityList2.periodInMillis = JsonUtils.getLongOr("periodInMillis", jsonObject5, -1L);
            final JsonElement jsonElement5 = jsonObject5.get("playerActivityDto");
            if (jsonElement5 != null && jsonElement5.isJsonArray()) {
                final JsonArray jsonArray7 = jsonElement5.getAsJsonArray();
                for (final JsonElement jsonElement6 : jsonArray7) {
                    final ServerActivity serverActivity10 = ServerActivity.parse(jsonElement6.getAsJsonObject());
                    serverActivityList2.serverActivities.add(serverActivity10);
                }
            }
        }
        catch (Exception ex) {}
        return serverActivityList2;
    }
}
