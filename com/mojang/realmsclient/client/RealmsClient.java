package com.mojang.realmsclient.client;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.net.URISyntaxException;
import java.net.URI;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNews;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.io.UnsupportedEncodingException;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsServer;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.dto.RealmsServerList;
import java.net.Proxy;
import net.minecraft.realms.Realms;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class RealmsClient {
    public static Environment currentEnvironment;
    private static boolean initialized;
    private static final Logger LOGGER;
    private final String sessionId;
    private final String username;
    private static final Gson gson;
    
    public static RealmsClient createRealmsClient() {
        final String string1 = Realms.userName();
        final String string2 = Realms.sessionId();
        if (string1 == null || string2 == null) {
            return null;
        }
        if (!RealmsClient.initialized) {
            RealmsClient.initialized = true;
            String string3 = System.getenv("realms.environment");
            if (string3 == null) {
                string3 = System.getProperty("realms.environment");
            }
            if (string3 != null) {
                if ("LOCAL".equals(string3)) {
                    switchToLocal();
                }
                else if ("STAGE".equals(string3)) {
                    switchToStage();
                }
            }
        }
        return new RealmsClient(string2, string1, Realms.getProxy());
    }
    
    public static void switchToStage() {
        RealmsClient.currentEnvironment = Environment.STAGE;
    }
    
    public static void switchToProd() {
        RealmsClient.currentEnvironment = Environment.PRODUCTION;
    }
    
    public static void switchToLocal() {
        RealmsClient.currentEnvironment = Environment.LOCAL;
    }
    
    public RealmsClient(final String string1, final String string2, final Proxy proxy) {
        this.sessionId = string1;
        this.username = string2;
        RealmsClientConfig.setProxy(proxy);
    }
    
    public RealmsServerList listWorlds() throws RealmsServiceException, IOException {
        final String string2 = this.url("worlds");
        final String string3 = this.execute(Request.get(string2));
        return RealmsServerList.parse(string3);
    }
    
    public RealmsServer getOwnWorld(final long long1) throws RealmsServiceException, IOException {
        final String string4 = this.url("worlds" + "/$ID".replace("$ID", (CharSequence)String.valueOf(long1)));
        final String string5 = this.execute(Request.get(string4));
        return RealmsServer.parse(string5);
    }
    
    public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
        final String string2 = this.url("activities/liveplayerlist");
        final String string3 = this.execute(Request.get(string2));
        return RealmsServerPlayerLists.parse(string3);
    }
    
    public RealmsServerAddress join(final long long1) throws RealmsServiceException, IOException {
        final String string4 = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", (CharSequence)new StringBuilder().append("").append(long1).toString()));
        final String string5 = this.execute(Request.get(string4, 5000, 30000));
        return RealmsServerAddress.parse(string5);
    }
    
    public void initializeWorld(final long long1, final String string2, final String string3) throws RealmsServiceException, IOException {
        final RealmsDescriptionDto realmsDescriptionDto6 = new RealmsDescriptionDto(string2, string3);
        final String string4 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string5 = RealmsClient.gson.toJson(realmsDescriptionDto6);
        this.execute(Request.post(string4, string5, 5000, 10000));
    }
    
    public Boolean mcoEnabled() throws RealmsServiceException, IOException {
        final String string2 = this.url("mco/available");
        final String string3 = this.execute(Request.get(string2));
        return Boolean.valueOf(string3);
    }
    
    public Boolean stageAvailable() throws RealmsServiceException, IOException {
        final String string2 = this.url("mco/stageAvailable");
        final String string3 = this.execute(Request.get(string2));
        return Boolean.valueOf(string3);
    }
    
    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException, IOException {
        final String string2 = this.url("mco/client/compatible");
        final String string3 = this.execute(Request.get(string2));
        CompatibleVersionResponse a4;
        try {
            a4 = CompatibleVersionResponse.valueOf(string3);
        }
        catch (IllegalArgumentException illegalArgumentException5) {
            throw new RealmsServiceException(500, "Could not check compatible version, got response: " + string3, -1, "");
        }
        return a4;
    }
    
    public void uninvite(final long long1, final String string) throws RealmsServiceException {
        final String string2 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)).replace("$UUID", (CharSequence)string));
        this.execute(Request.delete(string2));
    }
    
    public void uninviteMyselfFrom(final long long1) throws RealmsServiceException {
        final String string4 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        this.execute(Request.delete(string4));
    }
    
    public RealmsServer invite(final long long1, final String string) throws RealmsServiceException, IOException {
        final PlayerInfo playerInfo5 = new PlayerInfo();
        playerInfo5.setName(string);
        final String string2 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string3 = this.execute(Request.post(string2, RealmsClient.gson.toJson(playerInfo5)));
        return RealmsServer.parse(string3);
    }
    
    public BackupList backupsFor(final long long1) throws RealmsServiceException {
        final String string4 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string5 = this.execute(Request.get(string4));
        return BackupList.parse(string5);
    }
    
    public void update(final long long1, final String string2, final String string3) throws RealmsServiceException, UnsupportedEncodingException {
        final RealmsDescriptionDto realmsDescriptionDto6 = new RealmsDescriptionDto(string2, string3);
        final String string4 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        this.execute(Request.post(string4, RealmsClient.gson.toJson(realmsDescriptionDto6)));
    }
    
    public void updateSlot(final long long1, final int integer, final RealmsWorldOptions realmsWorldOptions) throws RealmsServiceException, UnsupportedEncodingException {
        final String string6 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)).replace("$SLOT_ID", (CharSequence)String.valueOf(integer)));
        final String string7 = realmsWorldOptions.toJson();
        this.execute(Request.post(string6, string7));
    }
    
    public boolean switchSlot(final long long1, final int integer) throws RealmsServiceException {
        final String string5 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)).replace("$SLOT_ID", (CharSequence)String.valueOf(integer)));
        final String string6 = this.execute(Request.put(string5, ""));
        return Boolean.valueOf(string6);
    }
    
    public void restoreWorld(final long long1, final String string) throws RealmsServiceException {
        final String string2 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)), "backupId=" + string);
        this.execute(Request.put(string2, "", 40000, 600000));
    }
    
    public WorldTemplatePaginatedList fetchWorldTemplates(final int integer1, final int integer2, final RealmsServer.WorldType c) throws RealmsServiceException {
        final String string5 = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", (CharSequence)c.toString()), String.format("page=%d&pageSize=%d", new Object[] { integer1, integer2 }));
        final String string6 = this.execute(Request.get(string5));
        return WorldTemplatePaginatedList.parse(string6);
    }
    
    public Boolean putIntoMinigameMode(final long long1, final String string) throws RealmsServiceException {
        final String string2 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", (CharSequence)string).replace("$WORLD_ID", (CharSequence)String.valueOf(long1));
        final String string3 = this.url("worlds" + string2);
        return Boolean.valueOf(this.execute(Request.put(string3, "")));
    }
    
    public Ops op(final long long1, final String string) throws RealmsServiceException {
        final String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)).replace("$PROFILE_UUID", (CharSequence)string);
        final String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.post(string3, "")));
    }
    
    public Ops deop(final long long1, final String string) throws RealmsServiceException {
        final String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)).replace("$PROFILE_UUID", (CharSequence)string);
        final String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.delete(string3)));
    }
    
    public Boolean open(final long long1) throws RealmsServiceException, IOException {
        final String string4 = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string5 = this.execute(Request.put(string4, ""));
        return Boolean.valueOf(string5);
    }
    
    public Boolean close(final long long1) throws RealmsServiceException, IOException {
        final String string4 = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string5 = this.execute(Request.put(string4, ""));
        return Boolean.valueOf(string5);
    }
    
    public Boolean resetWorldWithSeed(final long long1, final String string, final Integer integer, final boolean boolean4) throws RealmsServiceException, IOException {
        final RealmsWorldResetDto realmsWorldResetDto7 = new RealmsWorldResetDto(string, -1L, integer, boolean4);
        final String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string3 = this.execute(Request.post(string2, RealmsClient.gson.toJson(realmsWorldResetDto7), 30000, 80000));
        return Boolean.valueOf(string3);
    }
    
    public Boolean resetWorldWithTemplate(final long long1, final String string) throws RealmsServiceException, IOException {
        final RealmsWorldResetDto realmsWorldResetDto5 = new RealmsWorldResetDto(null, Long.valueOf(string), -1, false);
        final String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string3 = this.execute(Request.post(string2, RealmsClient.gson.toJson(realmsWorldResetDto5), 30000, 80000));
        return Boolean.valueOf(string3);
    }
    
    public Subscription subscriptionFor(final long long1) throws RealmsServiceException, IOException {
        final String string4 = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final String string5 = this.execute(Request.get(string4));
        return Subscription.parse(string5);
    }
    
    public int pendingInvitesCount() throws RealmsServiceException {
        final String string2 = this.url("invites/count/pending");
        final String string3 = this.execute(Request.get(string2));
        return Integer.parseInt(string3);
    }
    
    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        final String string2 = this.url("invites/pending");
        final String string3 = this.execute(Request.get(string2));
        return PendingInvitesList.parse(string3);
    }
    
    public void acceptInvitation(final String string) throws RealmsServiceException {
        final String string2 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", (CharSequence)string));
        this.execute(Request.put(string2, ""));
    }
    
    public WorldDownload download(final long long1, final int integer) throws RealmsServiceException {
        final String string5 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)).replace("$SLOT_ID", (CharSequence)String.valueOf(integer)));
        final String string6 = this.execute(Request.get(string5));
        return WorldDownload.parse(string6);
    }
    
    public UploadInfo upload(final long long1, final String string) throws RealmsServiceException {
        final String string2 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        final UploadInfo uploadInfo6 = new UploadInfo();
        if (string != null) {
            uploadInfo6.setToken(string);
        }
        final GsonBuilder gsonBuilder7 = new GsonBuilder();
        gsonBuilder7.excludeFieldsWithoutExposeAnnotation();
        final Gson gson8 = gsonBuilder7.create();
        final String string3 = gson8.toJson(uploadInfo6);
        return UploadInfo.parse(this.execute(Request.put(string2, string3)));
    }
    
    public void rejectInvitation(final String string) throws RealmsServiceException {
        final String string2 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", (CharSequence)string));
        this.execute(Request.put(string2, ""));
    }
    
    public void agreeToTos() throws RealmsServiceException {
        final String string2 = this.url("mco/tos/agreed");
        this.execute(Request.post(string2, ""));
    }
    
    public RealmsNews getNews() throws RealmsServiceException, IOException {
        final String string2 = this.url("mco/v1/news");
        final String string3 = this.execute(Request.get(string2, 5000, 10000));
        return RealmsNews.parse(string3);
    }
    
    public void sendPingResults(final PingResult pingResult) throws RealmsServiceException {
        final String string3 = this.url("regions/ping/stat");
        this.execute(Request.post(string3, RealmsClient.gson.toJson(pingResult)));
    }
    
    public Boolean trialAvailable() throws RealmsServiceException, IOException {
        final String string2 = this.url("trial");
        final String string3 = this.execute(Request.get(string2));
        return Boolean.valueOf(string3);
    }
    
    public RealmsServer createTrial(final String string1, final String string2) throws RealmsServiceException, IOException {
        final RealmsDescriptionDto realmsDescriptionDto4 = new RealmsDescriptionDto(string1, string2);
        final String string3 = RealmsClient.gson.toJson(realmsDescriptionDto4);
        final String string4 = this.url("trial");
        final String string5 = this.execute(Request.post(string4, string3, 5000, 10000));
        return RealmsServer.parse(string5);
    }
    
    public void deleteWorld(final long long1) throws RealmsServiceException, IOException {
        final String string4 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", (CharSequence)String.valueOf(long1)));
        this.execute(Request.delete(string4));
    }
    
    private String url(final String string) {
        return this.url(string, null);
    }
    
    private String url(final String string1, final String string2) {
        try {
            final URI uRI4 = new URI(RealmsClient.currentEnvironment.protocol, RealmsClient.currentEnvironment.baseUrl, "/" + string1, string2, (String)null);
            return uRI4.toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException4) {
            uRISyntaxException4.printStackTrace();
            return null;
        }
    }
    
    private String execute(final Request<?> cvp) throws RealmsServiceException {
        cvp.cookie("sid", this.sessionId);
        cvp.cookie("user", this.username);
        cvp.cookie("version", Realms.getMinecraftVersionString());
        try {
            final int integer3 = cvp.responseCode();
            if (integer3 == 503) {
                final int integer4 = cvp.getRetryAfterHeader();
                throw new RetryCallException(integer4);
            }
            final String string4 = cvp.text();
            if (integer3 >= 200 && integer3 < 300) {
                return string4;
            }
            if (integer3 == 401) {
                final String string5 = cvp.getHeader("WWW-Authenticate");
                RealmsClient.LOGGER.info("Could not authorize you against Realms server: " + string5);
                throw new RealmsServiceException(integer3, string5, -1, string5);
            }
            if (string4 == null || string4.length() == 0) {
                RealmsClient.LOGGER.error(new StringBuilder().append("Realms error code: ").append(integer3).append(" message: ").append(string4).toString());
                throw new RealmsServiceException(integer3, string4, integer3, "");
            }
            final RealmsError cvo5 = new RealmsError(string4);
            RealmsClient.LOGGER.error(new StringBuilder().append("Realms http code: ").append(integer3).append(" -  error code: ").append(cvo5.getErrorCode()).append(" -  message: ").append(cvo5.getErrorMessage()).append(" - raw body: ").append(string4).toString());
            throw new RealmsServiceException(integer3, string4, cvo5);
        }
        catch (RealmsHttpException cvt3) {
            throw new RealmsServiceException(500, "Could not connect to Realms: " + cvt3.getMessage(), -1, "");
        }
    }
    
    static {
        RealmsClient.currentEnvironment = Environment.PRODUCTION;
        LOGGER = LogManager.getLogger();
        gson = new Gson();
    }
    
    public enum Environment {
        PRODUCTION("pc.realms.minecraft.net", "https"), 
        STAGE("pc-stage.realms.minecraft.net", "https"), 
        LOCAL("localhost:8080", "http");
        
        public String baseUrl;
        public String protocol;
        
        private Environment(final String string3, final String string4) {
            this.baseUrl = string3;
            this.protocol = string4;
        }
    }
    
    public enum CompatibleVersionResponse {
        COMPATIBLE, 
        OUTDATED, 
        OTHER;
    }
}
