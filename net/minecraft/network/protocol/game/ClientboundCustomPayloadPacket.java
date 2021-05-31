package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ClientboundCustomPayloadPacket implements Packet<ClientGamePacketListener> {
    public static final ResourceLocation BRAND;
    public static final ResourceLocation DEBUG_PATHFINDING_PACKET;
    public static final ResourceLocation DEBUG_NEIGHBORSUPDATE_PACKET;
    public static final ResourceLocation DEBUG_CAVES_PACKET;
    public static final ResourceLocation DEBUG_STRUCTURES_PACKET;
    public static final ResourceLocation DEBUG_WORLDGENATTEMPT_PACKET;
    public static final ResourceLocation DEBUG_POI_TICKET_COUNT_PACKET;
    public static final ResourceLocation DEBUG_POI_ADDED_PACKET;
    public static final ResourceLocation DEBUG_POI_REMOVED_PACKET;
    public static final ResourceLocation DEBUG_VILLAGE_SECTIONS;
    public static final ResourceLocation DEBUG_GOAL_SELECTOR;
    public static final ResourceLocation DEBUG_BRAIN;
    public static final ResourceLocation DEBUG_RAIDS;
    private ResourceLocation identifier;
    private FriendlyByteBuf data;
    
    public ClientboundCustomPayloadPacket() {
    }
    
    public ClientboundCustomPayloadPacket(final ResourceLocation qv, final FriendlyByteBuf je) {
        this.identifier = qv;
        this.data = je;
        if (je.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.identifier = je.readResourceLocation();
        final int integer3 = je.readableBytes();
        if (integer3 < 0 || integer3 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new FriendlyByteBuf(je.readBytes(integer3));
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeResourceLocation(this.identifier);
        je.writeBytes(this.data.copy());
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleCustomPayload(this);
    }
    
    public ResourceLocation getIdentifier() {
        return this.identifier;
    }
    
    public FriendlyByteBuf getData() {
        return new FriendlyByteBuf(this.data.copy());
    }
    
    static {
        BRAND = new ResourceLocation("brand");
        DEBUG_PATHFINDING_PACKET = new ResourceLocation("debug/path");
        DEBUG_NEIGHBORSUPDATE_PACKET = new ResourceLocation("debug/neighbors_update");
        DEBUG_CAVES_PACKET = new ResourceLocation("debug/caves");
        DEBUG_STRUCTURES_PACKET = new ResourceLocation("debug/structures");
        DEBUG_WORLDGENATTEMPT_PACKET = new ResourceLocation("debug/worldgen_attempt");
        DEBUG_POI_TICKET_COUNT_PACKET = new ResourceLocation("debug/poi_ticket_count");
        DEBUG_POI_ADDED_PACKET = new ResourceLocation("debug/poi_added");
        DEBUG_POI_REMOVED_PACKET = new ResourceLocation("debug/poi_removed");
        DEBUG_VILLAGE_SECTIONS = new ResourceLocation("debug/village_sections");
        DEBUG_GOAL_SELECTOR = new ResourceLocation("debug/goal_selector");
        DEBUG_BRAIN = new ResourceLocation("debug/brain");
        DEBUG_RAIDS = new ResourceLocation("debug/raids");
    }
}
