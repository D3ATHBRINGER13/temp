package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.google.gson.JsonParser;
import java.util.HashMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.util.Collections;
import java.util.Locale;
import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import java.util.ArrayList;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonObject;
import java.util.Iterator;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.Realms;
import java.util.Map;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class RealmsServer extends ValueObject {
    private static final Logger LOGGER;
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String motd;
    public State state;
    public String owner;
    public String ownerUUID;
    public List<PlayerInfo> players;
    public Map<Integer, RealmsWorldOptions> slots;
    public boolean expired;
    public boolean expiredTrial;
    public int daysLeft;
    public WorldType worldType;
    public int activeSlot;
    public String minigameName;
    public int minigameId;
    public String minigameImage;
    public RealmsServerPing serverPing;
    
    public RealmsServer() {
        this.serverPing = new RealmsServerPing();
    }
    
    public String getDescription() {
        return this.motd;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getMinigameName() {
        return this.minigameName;
    }
    
    public void setName(final String string) {
        this.name = string;
    }
    
    public void setDescription(final String string) {
        this.motd = string;
    }
    
    public void updateServerPing(final RealmsServerPlayerList realmsServerPlayerList) {
        final StringBuilder stringBuilder3 = new StringBuilder();
        int integer4 = 0;
        for (final String string6 : realmsServerPlayerList.players) {
            if (string6.equals(Realms.getUUID())) {
                continue;
            }
            String string7 = "";
            try {
                string7 = RealmsUtil.uuidToName(string6);
            }
            catch (Exception exception8) {
                RealmsServer.LOGGER.error("Could not get name for " + string6, (Throwable)exception8);
                continue;
            }
            if (stringBuilder3.length() > 0) {
                stringBuilder3.append("\n");
            }
            stringBuilder3.append(string7);
            ++integer4;
        }
        this.serverPing.nrOfPlayers = String.valueOf(integer4);
        this.serverPing.playerList = stringBuilder3.toString();
    }
    
    public static RealmsServer parse(final JsonObject jsonObject) {
        final RealmsServer realmsServer2 = new RealmsServer();
        try {
            realmsServer2.id = JsonUtils.getLongOr("id", jsonObject, -1L);
            realmsServer2.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", jsonObject, (String)null);
            realmsServer2.name = JsonUtils.getStringOr("name", jsonObject, (String)null);
            realmsServer2.motd = JsonUtils.getStringOr("motd", jsonObject, (String)null);
            realmsServer2.state = getState(JsonUtils.getStringOr("state", jsonObject, State.CLOSED.name()));
            realmsServer2.owner = JsonUtils.getStringOr("owner", jsonObject, (String)null);
            if (jsonObject.get("players") != null && jsonObject.get("players").isJsonArray()) {
                realmsServer2.players = parseInvited(jsonObject.get("players").getAsJsonArray());
                sortInvited(realmsServer2);
            }
            else {
                realmsServer2.players = (List<PlayerInfo>)new ArrayList();
            }
            realmsServer2.daysLeft = JsonUtils.getIntOr("daysLeft", jsonObject, 0);
            realmsServer2.expired = JsonUtils.getBooleanOr("expired", jsonObject, false);
            realmsServer2.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", jsonObject, false);
            realmsServer2.worldType = getWorldType(JsonUtils.getStringOr("worldType", jsonObject, WorldType.NORMAL.name()));
            realmsServer2.ownerUUID = JsonUtils.getStringOr("ownerUUID", jsonObject, "");
            if (jsonObject.get("slots") != null && jsonObject.get("slots").isJsonArray()) {
                realmsServer2.slots = parseSlots(jsonObject.get("slots").getAsJsonArray());
            }
            else {
                realmsServer2.slots = getEmptySlots();
            }
            realmsServer2.minigameName = JsonUtils.getStringOr("minigameName", jsonObject, (String)null);
            realmsServer2.activeSlot = JsonUtils.getIntOr("activeSlot", jsonObject, -1);
            realmsServer2.minigameId = JsonUtils.getIntOr("minigameId", jsonObject, -1);
            realmsServer2.minigameImage = JsonUtils.getStringOr("minigameImage", jsonObject, (String)null);
        }
        catch (Exception exception3) {
            RealmsServer.LOGGER.error("Could not parse McoServer: " + exception3.getMessage());
        }
        return realmsServer2;
    }
    
    private static void sortInvited(final RealmsServer realmsServer) {
        Collections.sort((List)realmsServer.players, (Comparator)new Comparator<PlayerInfo>() {
            public int compare(final PlayerInfo playerInfo1, final PlayerInfo playerInfo2) {
                return ComparisonChain.start().compare(Boolean.valueOf(playerInfo2.getAccepted()), Boolean.valueOf(playerInfo1.getAccepted())).compare((Comparable)playerInfo1.getName().toLowerCase(Locale.ROOT), (Comparable)playerInfo2.getName().toLowerCase(Locale.ROOT)).result();
            }
        });
    }
    
    private static List<PlayerInfo> parseInvited(final JsonArray jsonArray) {
        final ArrayList<PlayerInfo> arrayList2 = (ArrayList<PlayerInfo>)new ArrayList();
        for (final JsonElement jsonElement4 : jsonArray) {
            try {
                final JsonObject jsonObject5 = jsonElement4.getAsJsonObject();
                final PlayerInfo playerInfo6 = new PlayerInfo();
                playerInfo6.setName(JsonUtils.getStringOr("name", jsonObject5, (String)null));
                playerInfo6.setUuid(JsonUtils.getStringOr("uuid", jsonObject5, (String)null));
                playerInfo6.setOperator(JsonUtils.getBooleanOr("operator", jsonObject5, false));
                playerInfo6.setAccepted(JsonUtils.getBooleanOr("accepted", jsonObject5, false));
                playerInfo6.setOnline(JsonUtils.getBooleanOr("online", jsonObject5, false));
                arrayList2.add(playerInfo6);
            }
            catch (Exception ex) {}
        }
        return (List<PlayerInfo>)arrayList2;
    }
    
    private static Map<Integer, RealmsWorldOptions> parseSlots(final JsonArray jsonArray) {
        final Map<Integer, RealmsWorldOptions> map2 = (Map<Integer, RealmsWorldOptions>)new HashMap();
        for (final JsonElement jsonElement4 : jsonArray) {
            try {
                final JsonObject jsonObject6 = jsonElement4.getAsJsonObject();
                final JsonParser jsonParser7 = new JsonParser();
                final JsonElement jsonElement5 = jsonParser7.parse(jsonObject6.get("options").getAsString());
                RealmsWorldOptions realmsWorldOptions5;
                if (jsonElement5 == null) {
                    realmsWorldOptions5 = RealmsWorldOptions.getDefaults();
                }
                else {
                    realmsWorldOptions5 = RealmsWorldOptions.parse(jsonElement5.getAsJsonObject());
                }
                final int integer9 = JsonUtils.getIntOr("slotId", jsonObject6, -1);
                map2.put(integer9, realmsWorldOptions5);
            }
            catch (Exception ex) {}
        }
        for (int integer10 = 1; integer10 <= 3; ++integer10) {
            if (!map2.containsKey(integer10)) {
                map2.put(integer10, RealmsWorldOptions.getEmptyDefaults());
            }
        }
        return map2;
    }
    
    private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
        final HashMap<Integer, RealmsWorldOptions> hashMap1 = (HashMap<Integer, RealmsWorldOptions>)new HashMap();
        hashMap1.put(1, RealmsWorldOptions.getEmptyDefaults());
        hashMap1.put(2, RealmsWorldOptions.getEmptyDefaults());
        hashMap1.put(3, RealmsWorldOptions.getEmptyDefaults());
        return (Map<Integer, RealmsWorldOptions>)hashMap1;
    }
    
    public static RealmsServer parse(final String string) {
        RealmsServer realmsServer2 = new RealmsServer();
        try {
            final JsonParser jsonParser3 = new JsonParser();
            final JsonObject jsonObject4 = jsonParser3.parse(string).getAsJsonObject();
            realmsServer2 = parse(jsonObject4);
        }
        catch (Exception exception3) {
            RealmsServer.LOGGER.error("Could not parse McoServer: " + exception3.getMessage());
        }
        return realmsServer2;
    }
    
    private static State getState(final String string) {
        try {
            return State.valueOf(string);
        }
        catch (Exception exception2) {
            return State.CLOSED;
        }
    }
    
    private static WorldType getWorldType(final String string) {
        try {
            return WorldType.valueOf(string);
        }
        catch (Exception exception2) {
            return WorldType.NORMAL;
        }
    }
    
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id).append(this.name).append(this.motd).append(this.state).append(this.owner).append(this.expired).toHashCode();
    }
    
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        final RealmsServer realmsServer3 = (RealmsServer)object;
        return new EqualsBuilder().append(this.id, realmsServer3.id).append(this.name, realmsServer3.name).append(this.motd, realmsServer3.motd).append(this.state, realmsServer3.state).append(this.owner, realmsServer3.owner).append(this.expired, realmsServer3.expired).append(this.worldType, this.worldType).isEquals();
    }
    
    public RealmsServer clone() {
        final RealmsServer realmsServer2 = new RealmsServer();
        realmsServer2.id = this.id;
        realmsServer2.remoteSubscriptionId = this.remoteSubscriptionId;
        realmsServer2.name = this.name;
        realmsServer2.motd = this.motd;
        realmsServer2.state = this.state;
        realmsServer2.owner = this.owner;
        realmsServer2.players = this.players;
        realmsServer2.slots = this.cloneSlots(this.slots);
        realmsServer2.expired = this.expired;
        realmsServer2.expiredTrial = this.expiredTrial;
        realmsServer2.daysLeft = this.daysLeft;
        realmsServer2.serverPing = new RealmsServerPing();
        realmsServer2.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
        realmsServer2.serverPing.playerList = this.serverPing.playerList;
        realmsServer2.worldType = this.worldType;
        realmsServer2.ownerUUID = this.ownerUUID;
        realmsServer2.minigameName = this.minigameName;
        realmsServer2.activeSlot = this.activeSlot;
        realmsServer2.minigameId = this.minigameId;
        realmsServer2.minigameImage = this.minigameImage;
        return realmsServer2;
    }
    
    public Map<Integer, RealmsWorldOptions> cloneSlots(final Map<Integer, RealmsWorldOptions> map) {
        final Map<Integer, RealmsWorldOptions> map2 = (Map<Integer, RealmsWorldOptions>)new HashMap();
        for (final Map.Entry<Integer, RealmsWorldOptions> entry5 : map.entrySet()) {
            map2.put(entry5.getKey(), ((RealmsWorldOptions)entry5.getValue()).clone());
        }
        return map2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class McoServerComparator implements Comparator<RealmsServer> {
        private final String refOwner;
        
        public McoServerComparator(final String string) {
            this.refOwner = string;
        }
        
        public int compare(final RealmsServer realmsServer1, final RealmsServer realmsServer2) {
            return ComparisonChain.start().compareTrueFirst(realmsServer1.state.equals(State.UNINITIALIZED), realmsServer2.state.equals(State.UNINITIALIZED)).compareTrueFirst(realmsServer1.expiredTrial, realmsServer2.expiredTrial).compareTrueFirst(realmsServer1.owner.equals(this.refOwner), realmsServer2.owner.equals(this.refOwner)).compareFalseFirst(realmsServer1.expired, realmsServer2.expired).compareTrueFirst(realmsServer1.state.equals(State.OPEN), realmsServer2.state.equals(State.OPEN)).compare(realmsServer1.id, realmsServer2.id).result();
        }
    }
    
    public enum State {
        CLOSED, 
        OPEN, 
        UNINITIALIZED;
    }
    
    public enum WorldType {
        NORMAL, 
        MINIGAME, 
        ADVENTUREMAP, 
        EXPERIENCE, 
        INSPIRATION;
    }
}
