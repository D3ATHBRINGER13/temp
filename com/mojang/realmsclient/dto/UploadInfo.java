package com.mojang.realmsclient.dto;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import org.apache.logging.log4j.Logger;

public class UploadInfo extends ValueObject {
    private static final Logger LOGGER;
    @Expose
    private boolean worldClosed;
    @Expose
    private String token;
    @Expose
    private String uploadEndpoint;
    private int port;
    
    public UploadInfo() {
        this.token = "";
        this.uploadEndpoint = "";
    }
    
    public static UploadInfo parse(final String string) {
        final UploadInfo uploadInfo2 = new UploadInfo();
        try {
            final JsonParser jsonParser3 = new JsonParser();
            final JsonObject jsonObject4 = jsonParser3.parse(string).getAsJsonObject();
            uploadInfo2.worldClosed = JsonUtils.getBooleanOr("worldClosed", jsonObject4, false);
            uploadInfo2.token = JsonUtils.getStringOr("token", jsonObject4, (String)null);
            uploadInfo2.uploadEndpoint = JsonUtils.getStringOr("uploadEndpoint", jsonObject4, (String)null);
            uploadInfo2.port = JsonUtils.getIntOr("port", jsonObject4, 8080);
        }
        catch (Exception exception3) {
            UploadInfo.LOGGER.error("Could not parse UploadInfo: " + exception3.getMessage());
        }
        return uploadInfo2;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getUploadEndpoint() {
        return this.uploadEndpoint;
    }
    
    public boolean isWorldClosed() {
        return this.worldClosed;
    }
    
    public void setToken(final String string) {
        this.token = string;
    }
    
    public int getPort() {
        return this.port;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
