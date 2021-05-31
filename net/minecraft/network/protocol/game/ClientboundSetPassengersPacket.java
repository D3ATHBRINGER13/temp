package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetPassengersPacket implements Packet<ClientGamePacketListener> {
    private int vehicle;
    private int[] passengers;
    
    public ClientboundSetPassengersPacket() {
    }
    
    public ClientboundSetPassengersPacket(final Entity aio) {
        this.vehicle = aio.getId();
        final List<Entity> list3 = aio.getPassengers();
        this.passengers = new int[list3.size()];
        for (int integer4 = 0; integer4 < list3.size(); ++integer4) {
            this.passengers[integer4] = ((Entity)list3.get(integer4)).getId();
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.vehicle = je.readVarInt();
        this.passengers = je.readVarIntArray();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.vehicle);
        je.writeVarIntArray(this.passengers);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetEntityPassengersPacket(this);
    }
    
    public int[] getPassengers() {
        return this.passengers;
    }
    
    public int getVehicle() {
        return this.vehicle;
    }
}
