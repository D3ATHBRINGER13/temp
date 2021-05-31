package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetEquippedItemPacket implements Packet<ClientGamePacketListener> {
    private int entity;
    private EquipmentSlot slot;
    private ItemStack itemStack;
    
    public ClientboundSetEquippedItemPacket() {
        this.itemStack = ItemStack.EMPTY;
    }
    
    public ClientboundSetEquippedItemPacket(final int integer, final EquipmentSlot ait, final ItemStack bcj) {
        this.itemStack = ItemStack.EMPTY;
        this.entity = integer;
        this.slot = ait;
        this.itemStack = bcj.copy();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entity = je.readVarInt();
        this.slot = je.<EquipmentSlot>readEnum(EquipmentSlot.class);
        this.itemStack = je.readItem();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entity);
        je.writeEnum(this.slot);
        je.writeItem(this.itemStack);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetEquippedItem(this);
    }
    
    public ItemStack getItem() {
        return this.itemStack;
    }
    
    public int getEntity() {
        return this.entity;
    }
    
    public EquipmentSlot getSlot() {
        return this.slot;
    }
}
