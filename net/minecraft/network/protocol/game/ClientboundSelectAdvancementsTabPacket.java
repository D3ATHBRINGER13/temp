package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
    @Nullable
    private ResourceLocation tab;
    
    public ClientboundSelectAdvancementsTabPacket() {
    }
    
    public ClientboundSelectAdvancementsTabPacket(@Nullable final ResourceLocation qv) {
        this.tab = qv;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSelectAdvancementsTab(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        if (je.readBoolean()) {
            this.tab = je.readResourceLocation();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBoolean(this.tab != null);
        if (this.tab != null) {
            je.writeResourceLocation(this.tab);
        }
    }
    
    @Nullable
    public ResourceLocation getTab() {
        return this.tab;
    }
}
