package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerClickPacket implements Packet<ServerGamePacketListener> {
    private int containerId;
    private int slotNum;
    private int buttonNum;
    private short uid;
    private ItemStack itemStack;
    private ClickType clickType;
    
    public ServerboundContainerClickPacket() {
        this.itemStack = ItemStack.EMPTY;
    }
    
    public ServerboundContainerClickPacket(final int integer1, final int integer2, final int integer3, final ClickType ays, final ItemStack bcj, final short short6) {
        this.itemStack = ItemStack.EMPTY;
        this.containerId = integer1;
        this.slotNum = integer2;
        this.buttonNum = integer3;
        this.itemStack = bcj.copy();
        this.uid = short6;
        this.clickType = ays;
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleContainerClick(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
        this.slotNum = je.readShort();
        this.buttonNum = je.readByte();
        this.uid = je.readShort();
        this.clickType = je.<ClickType>readEnum(ClickType.class);
        this.itemStack = je.readItem();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeShort(this.slotNum);
        je.writeByte(this.buttonNum);
        je.writeShort(this.uid);
        je.writeEnum(this.clickType);
        je.writeItem(this.itemStack);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public int getSlotNum() {
        return this.slotNum;
    }
    
    public int getButtonNum() {
        return this.buttonNum;
    }
    
    public short getUid() {
        return this.uid;
    }
    
    public ItemStack getItem() {
        return this.itemStack;
    }
    
    public ClickType getClickType() {
        return this.clickType;
    }
}
