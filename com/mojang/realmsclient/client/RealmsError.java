package com.mojang.realmsclient.client;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;

public class RealmsError {
    private static final Logger LOGGER;
    private String errorMessage;
    private int errorCode;
    
    public RealmsError(final String string) {
        try {
            final JsonParser jsonParser3 = new JsonParser();
            final JsonObject jsonObject4 = jsonParser3.parse(string).getAsJsonObject();
            this.errorMessage = JsonUtils.getStringOr("errorMsg", jsonObject4, "");
            this.errorCode = JsonUtils.getIntOr("errorCode", jsonObject4, -1);
        }
        catch (Exception exception3) {
            RealmsError.LOGGER.error("Could not parse RealmsError: " + exception3.getMessage());
            RealmsError.LOGGER.error("The error was: " + string);
        }
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
