package net.minecraft.client.resources.language;

public class Language implements com.mojang.bridge.game.Language, Comparable<Language> {
    private final String code;
    private final String region;
    private final String name;
    private final boolean bidirectional;
    
    public Language(final String string1, final String string2, final String string3, final boolean boolean4) {
        this.code = string1;
        this.region = string2;
        this.name = string3;
        this.bidirectional = boolean4;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public boolean isBidirectional() {
        return this.bidirectional;
    }
    
    public String toString() {
        return String.format("%s (%s)", new Object[] { this.name, this.region });
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof Language && this.code.equals(((Language)object).code));
    }
    
    public int hashCode() {
        return this.code.hashCode();
    }
    
    public int compareTo(final Language dxy) {
        return this.code.compareTo(dxy.code);
    }
}
