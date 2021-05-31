package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonObject;
import java.util.Date;
import org.apache.logging.log4j.Logger;

public class PendingInvite extends ValueObject {
    private static final Logger LOGGER;
    public String invitationId;
    public String worldName;
    public String worldOwnerName;
    public String worldOwnerUuid;
    public Date date;
    
    public static PendingInvite parse(final JsonObject jsonObject) {
        final PendingInvite pendingInvite2 = new PendingInvite();
        try {
            pendingInvite2.invitationId = JsonUtils.getStringOr("invitationId", jsonObject, "");
            pendingInvite2.worldName = JsonUtils.getStringOr("worldName", jsonObject, "");
            pendingInvite2.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", jsonObject, "");
            pendingInvite2.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", jsonObject, "");
            pendingInvite2.date = JsonUtils.getDateOr("date", jsonObject);
        }
        catch (Exception exception3) {
            PendingInvite.LOGGER.error("Could not parse PendingInvite: " + exception3.getMessage());
        }
        return pendingInvite2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
