package com.mojang.realmsclient.dto;

import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonObject;

public class ServerActivity extends ValueObject {
    public String profileUuid;
    public long joinTime;
    public long leaveTime;
    
    public static ServerActivity parse(final JsonObject jsonObject) {
        final ServerActivity serverActivity2 = new ServerActivity();
        try {
            serverActivity2.profileUuid = JsonUtils.getStringOr("profileUuid", jsonObject, (String)null);
            serverActivity2.joinTime = JsonUtils.getLongOr("joinTime", jsonObject, Long.MIN_VALUE);
            serverActivity2.leaveTime = JsonUtils.getLongOr("leaveTime", jsonObject, Long.MIN_VALUE);
        }
        catch (Exception ex) {}
        return serverActivity2;
    }
}
