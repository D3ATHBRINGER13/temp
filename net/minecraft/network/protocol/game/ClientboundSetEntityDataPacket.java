package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetEntityDataPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private List<SynchedEntityData.DataItem<?>> packedItems;
    
    public ClientboundSetEntityDataPacket() {
    }
    
    public ClientboundSetEntityDataPacket(final int integer, final SynchedEntityData qn, final boolean boolean3) {
        this.id = integer;
        if (boolean3) {
            this.packedItems = qn.getAll();
            qn.clearDirty();
        }
        else {
            this.packedItems = qn.packDirty();
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.packedItems = SynchedEntityData.unpack(je);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        SynchedEntityData.pack(this.packedItems, je);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetEntityData(this);
    }
    
    public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
        return this.packedItems;
    }
    
    public int getId() {
        return this.id;
    }
}
