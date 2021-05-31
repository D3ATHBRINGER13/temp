package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagManager;
import net.minecraft.network.protocol.Packet;

public class ClientboundUpdateTagsPacket implements Packet<ClientGamePacketListener> {
    private TagManager tags;
    
    public ClientboundUpdateTagsPacket() {
    }
    
    public ClientboundUpdateTagsPacket(final TagManager zi) {
        this.tags = zi;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.tags = TagManager.deserializeFromNetwork(je);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        this.tags.serializeToNetwork(je);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleUpdateTags(this);
    }
    
    public TagManager getTags() {
        return this.tags;
    }
}
