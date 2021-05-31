package com.mojang.realmsclient.dto;

public class RealmsWorldResetDto extends ValueObject {
    private final String seed;
    private final long worldTemplateId;
    private final int levelType;
    private final boolean generateStructures;
    
    public RealmsWorldResetDto(final String string, final long long2, final int integer, final boolean boolean4) {
        this.seed = string;
        this.worldTemplateId = long2;
        this.levelType = integer;
        this.generateStructures = boolean4;
    }
}
