package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerSetContentPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private List<ItemStack> items;
    
    public ClientboundContainerSetContentPacket() {
    }
    
    public ClientboundContainerSetContentPacket(final int integer, final NonNullList<ItemStack> fk) {
        this.containerId = integer;
        this.items = NonNullList.withSize(fk.size(), ItemStack.EMPTY);
        for (int integer2 = 0; integer2 < this.items.size(); ++integer2) {
            this.items.set(integer2, fk.get(integer2).copy());
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readUnsignedByte();
        final int integer3 = je.readShort();
        this.items = NonNullList.withSize(integer3, ItemStack.EMPTY);
        for (int integer4 = 0; integer4 < integer3; ++integer4) {
            this.items.set(integer4, je.readItem());
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeShort(this.items.size());
        for (final ItemStack bcj4 : this.items) {
            je.writeItem(bcj4);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleContainerContent(this);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public List<ItemStack> getItems() {
        return this.items;
    }
}
