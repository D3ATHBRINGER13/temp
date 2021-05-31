package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.network.protocol.Packet;

public class ClientboundCooldownPacket implements Packet<ClientGamePacketListener> {
    private Item item;
    private int duration;
    
    public ClientboundCooldownPacket() {
    }
    
    public ClientboundCooldownPacket(final Item bce, final int integer) {
        this.item = bce;
        this.duration = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.item = Item.byId(je.readVarInt());
        this.duration = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(Item.getId(this.item));
        je.writeVarInt(this.duration);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleItemCooldown(this);
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public int getDuration() {
        return this.duration;
    }
}
