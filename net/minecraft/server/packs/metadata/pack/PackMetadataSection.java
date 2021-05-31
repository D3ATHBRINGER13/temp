package net.minecraft.server.packs.metadata.pack;

import net.minecraft.network.chat.Component;

public class PackMetadataSection {
    public static final PackMetadataSectionSerializer SERIALIZER;
    private final Component description;
    private final int packFormat;
    
    public PackMetadataSection(final Component jo, final int integer) {
        this.description = jo;
        this.packFormat = integer;
    }
    
    public Component getDescription() {
        return this.description;
    }
    
    public int getPackFormat() {
        return this.packFormat;
    }
    
    static {
        SERIALIZER = new PackMetadataSectionSerializer();
    }
}
