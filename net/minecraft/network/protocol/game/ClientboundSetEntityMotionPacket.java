package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetEntityMotionPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private int xa;
    private int ya;
    private int za;
    
    public ClientboundSetEntityMotionPacket() {
    }
    
    public ClientboundSetEntityMotionPacket(final Entity aio) {
        this(aio.getId(), aio.getDeltaMovement());
    }
    
    public ClientboundSetEntityMotionPacket(final int integer, final Vec3 csi) {
        this.id = integer;
        final double double4 = 3.9;
        final double double5 = Mth.clamp(csi.x, -3.9, 3.9);
        final double double6 = Mth.clamp(csi.y, -3.9, 3.9);
        final double double7 = Mth.clamp(csi.z, -3.9, 3.9);
        this.xa = (int)(double5 * 8000.0);
        this.ya = (int)(double6 * 8000.0);
        this.za = (int)(double7 * 8000.0);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.xa = je.readShort();
        this.ya = je.readShort();
        this.za = je.readShort();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeShort(this.xa);
        je.writeShort(this.ya);
        je.writeShort(this.za);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetEntityMotion(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getXa() {
        return this.xa;
    }
    
    public int getYa() {
        return this.ya;
    }
    
    public int getZa() {
        return this.za;
    }
}
