package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetCreativeModeSlotPacket implements Packet<ServerGamePacketListener> {
    private int slotNum;
    private ItemStack itemStack;
    
    public ServerboundSetCreativeModeSlotPacket() {
        this.itemStack = ItemStack.EMPTY;
    }
    
    public ServerboundSetCreativeModeSlotPacket(final int integer, final ItemStack bcj) {
        this.itemStack = ItemStack.EMPTY;
        this.slotNum = integer;
        this.itemStack = bcj.copy();
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSetCreativeModeSlot(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.slotNum = je.readShort();
        this.itemStack = je.readItem();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeShort(this.slotNum);
        je.writeItem(this.itemStack);
    }
    
    public int getSlotNum() {
        return this.slotNum;
    }
    
    public ItemStack getItem() {
        return this.itemStack;
    }
}
