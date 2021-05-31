package com.mojang.realmsclient.dto;

public class RealmsDescriptionDto extends ValueObject {
    public String name;
    public String description;
    
    public RealmsDescriptionDto(final String string1, final String string2) {
        this.name = string1;
        this.description = string2;
    }
}
