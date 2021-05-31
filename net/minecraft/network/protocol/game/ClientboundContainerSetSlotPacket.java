package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerSetSlotPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int slot;
    private ItemStack itemStack;
    
    public ClientboundContainerSetSlotPacket() {
        this.itemStack = ItemStack.EMPTY;
    }
    
    public ClientboundContainerSetSlotPacket(final int integer1, final int integer2, final ItemStack bcj) {
        this.itemStack = ItemStack.EMPTY;
        this.containerId = integer1;
        this.slot = integer2;
        this.itemStack = bcj.copy();
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleContainerSetSlot(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
        this.slot = je.readShort();
        this.itemStack = je.readItem();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeShort(this.slot);
        je.writeItem(this.itemStack);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public ItemStack getItem() {
        return this.itemStack;
    }
}
