package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundOpenScreenPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int type;
    private Component title;
    
    public ClientboundOpenScreenPacket() {
    }
    
    public ClientboundOpenScreenPacket(final int integer, final MenuType<?> azl, final Component jo) {
        this.containerId = integer;
        this.type = Registry.MENU.getId(azl);
        this.title = jo;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readVarInt();
        this.type = je.readVarInt();
        this.title = je.readComponent();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.containerId);
        je.writeVarInt(this.type);
        je.writeComponent(this.title);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleOpenScreen(this);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    @Nullable
    public MenuType<?> getType() {
        return Registry.MENU.byId(this.type);
    }
    
    public Component getTitle() {
        return this.title;
    }
}
