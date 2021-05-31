package com.mojang.realmsclient.dto;

import net.minecraft.realms.RealmsScreen;
import com.mojang.realmsclient.util.JsonUtils;
import com.google.gson.JsonObject;

public class RealmsWorldOptions extends ValueObject {
    public Boolean pvp;
    public Boolean spawnAnimals;
    public Boolean spawnMonsters;
    public Boolean spawnNPCs;
    public Integer spawnProtection;
    public Boolean commandBlocks;
    public Boolean forceGameMode;
    public Integer difficulty;
    public Integer gameMode;
    public String slotName;
    public long templateId;
    public String templateImage;
    public boolean adventureMap;
    public boolean empty;
    private static final boolean forceGameModeDefault = false;
    private static final boolean pvpDefault = true;
    private static final boolean spawnAnimalsDefault = true;
    private static final boolean spawnMonstersDefault = true;
    private static final boolean spawnNPCsDefault = true;
    private static final int spawnProtectionDefault = 0;
    private static final boolean commandBlocksDefault = false;
    private static final int difficultyDefault = 2;
    private static final int gameModeDefault = 0;
    private static final String slotNameDefault = "";
    private static final long templateIdDefault = -1L;
    private static final String templateImageDefault;
    private static final boolean adventureMapDefault = false;
    
    public RealmsWorldOptions(final Boolean boolean1, final Boolean boolean2, final Boolean boolean3, final Boolean boolean4, final Integer integer5, final Boolean boolean6, final Integer integer7, final Integer integer8, final Boolean boolean9, final String string) {
        this.pvp = boolean1;
        this.spawnAnimals = boolean2;
        this.spawnMonsters = boolean3;
        this.spawnNPCs = boolean4;
        this.spawnProtection = integer5;
        this.commandBlocks = boolean6;
        this.difficulty = integer7;
        this.gameMode = integer8;
        this.forceGameMode = boolean9;
        this.slotName = string;
    }
    
    public static RealmsWorldOptions getDefaults() {
        return new RealmsWorldOptions(Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(0), Boolean.valueOf(false), Integer.valueOf(2), Integer.valueOf(0), Boolean.valueOf(false), "");
    }
    
    public static RealmsWorldOptions getEmptyDefaults() {
        final RealmsWorldOptions realmsWorldOptions1 = new RealmsWorldOptions(Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(0), Boolean.valueOf(false), Integer.valueOf(2), Integer.valueOf(0), Boolean.valueOf(false), "");
        realmsWorldOptions1.setEmpty(true);
        return realmsWorldOptions1;
    }
    
    public void setEmpty(final boolean boolean1) {
        this.empty = boolean1;
    }
    
    public static RealmsWorldOptions parse(final JsonObject jsonObject) {
        final RealmsWorldOptions realmsWorldOptions2 = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", jsonObject, true), JsonUtils.getBooleanOr("spawnAnimals", jsonObject, true), JsonUtils.getBooleanOr("spawnMonsters", jsonObject, true), JsonUtils.getBooleanOr("spawnNPCs", jsonObject, true), JsonUtils.getIntOr("spawnProtection", jsonObject, 0), JsonUtils.getBooleanOr("commandBlocks", jsonObject, false), JsonUtils.getIntOr("difficulty", jsonObject, 2), JsonUtils.getIntOr("gameMode", jsonObject, 0), JsonUtils.getBooleanOr("forceGameMode", jsonObject, false), JsonUtils.getStringOr("slotName", jsonObject, ""));
        realmsWorldOptions2.templateId = JsonUtils.getLongOr("worldTemplateId", jsonObject, -1L);
        realmsWorldOptions2.templateImage = JsonUtils.getStringOr("worldTemplateImage", jsonObject, RealmsWorldOptions.templateImageDefault);
        realmsWorldOptions2.adventureMap = JsonUtils.getBooleanOr("adventureMap", jsonObject, false);
        return realmsWorldOptions2;
    }
    
    public String getSlotName(final int integer) {
        if (this.slotName != null && !this.slotName.isEmpty()) {
            return this.slotName;
        }
        if (this.empty) {
            return RealmsScreen.getLocalizedString("mco.configure.world.slot.empty");
        }
        return this.getDefaultSlotName(integer);
    }
    
    public String getDefaultSlotName(final int integer) {
        return RealmsScreen.getLocalizedString("mco.configure.world.slot", integer);
    }
    
    public String toJson() {
        final JsonObject jsonObject2 = new JsonObject();
        if (!this.pvp) {
            jsonObject2.addProperty("pvp", this.pvp);
        }
        if (!this.spawnAnimals) {
            jsonObject2.addProperty("spawnAnimals", this.spawnAnimals);
        }
        if (!this.spawnMonsters) {
            jsonObject2.addProperty("spawnMonsters", this.spawnMonsters);
        }
        if (!this.spawnNPCs) {
            jsonObject2.addProperty("spawnNPCs", this.spawnNPCs);
        }
        if (this.spawnProtection != 0) {
            jsonObject2.addProperty("spawnProtection", (Number)this.spawnProtection);
        }
        if (this.commandBlocks) {
            jsonObject2.addProperty("commandBlocks", this.commandBlocks);
        }
        if (this.difficulty != 2) {
            jsonObject2.addProperty("difficulty", (Number)this.difficulty);
        }
        if (this.gameMode != 0) {
            jsonObject2.addProperty("gameMode", (Number)this.gameMode);
        }
        if (this.forceGameMode) {
            jsonObject2.addProperty("forceGameMode", this.forceGameMode);
        }
        if (this.slotName != null && !this.slotName.equals("")) {
            jsonObject2.addProperty("slotName", this.slotName);
        }
        return jsonObject2.toString();
    }
    
    public RealmsWorldOptions clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
    }
    
    static {
        templateImageDefault = null;
    }
}
