package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.network.protocol.Packet;

public class ClientboundMoveEntityPacket implements Packet<ClientGamePacketListener> {
    protected int entityId;
    protected short xa;
    protected short ya;
    protected short za;
    protected byte yRot;
    protected byte xRot;
    protected boolean onGround;
    protected boolean hasRot;
    
    public static long entityToPacket(final double double1) {
        return Mth.lfloor(double1 * 4096.0);
    }
    
    public static Vec3 packetToEntity(final long long1, final long long2, final long long3) {
        return new Vec3((double)long1, (double)long2, (double)long3).scale(2.44140625E-4);
    }
    
    public ClientboundMoveEntityPacket() {
    }
    
    public ClientboundMoveEntityPacket(final int integer) {
        this.entityId = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleMoveEntity(this);
    }
    
    public String toString() {
        return "Entity_" + super.toString();
    }
    
    public Entity getEntity(final Level bhr) {
        return bhr.getEntity(this.entityId);
    }
    
    public short getXa() {
        return this.xa;
    }
    
    public short getYa() {
        return this.ya;
    }
    
    public short getZa() {
        return this.za;
    }
    
    public byte getyRot() {
        return this.yRot;
    }
    
    public byte getxRot() {
        return this.xRot;
    }
    
    public boolean hasRotation() {
        return this.hasRot;
    }
    
    public boolean isOnGround() {
        return this.onGround;
    }
    
    public static class PosRot extends ClientboundMoveEntityPacket {
        public PosRot() {
            this.hasRot = true;
        }
        
        public PosRot(final int integer, final short short2, final short short3, final short short4, final byte byte5, final byte byte6, final boolean boolean7) {
            super(integer);
            this.xa = short2;
            this.ya = short3;
            this.za = short4;
            this.yRot = byte5;
            this.xRot = byte6;
            this.onGround = boolean7;
            this.hasRot = true;
        }
        
        @Override
        public void read(final FriendlyByteBuf je) throws IOException {
            super.read(je);
            this.xa = je.readShort();
            this.ya = je.readShort();
            this.za = je.readShort();
            this.yRot = je.readByte();
            this.xRot = je.readByte();
            this.onGround = je.readBoolean();
        }
        
        @Override
        public void write(final FriendlyByteBuf je) throws IOException {
            super.write(je);
            je.writeShort(this.xa);
            je.writeShort(this.ya);
            je.writeShort(this.za);
            je.writeByte(this.yRot);
            je.writeByte(this.xRot);
            je.writeBoolean(this.onGround);
        }
    }
    
    public static class Pos extends ClientboundMoveEntityPacket {
        public Pos() {
        }
        
        public Pos(final int integer, final short short2, final short short3, final short short4, final boolean boolean5) {
            super(integer);
            this.xa = short2;
            this.ya = short3;
            this.za = short4;
            this.onGround = boolean5;
        }
        
        @Override
        public void read(final FriendlyByteBuf je) throws IOException {
            super.read(je);
            this.xa = je.readShort();
            this.ya = je.readShort();
            this.za = je.readShort();
            this.onGround = je.readBoolean();
        }
        
        @Override
        public void write(final FriendlyByteBuf je) throws IOException {
            super.write(je);
            je.writeShort(this.xa);
            je.writeShort(this.ya);
            je.writeShort(this.za);
            je.writeBoolean(this.onGround);
        }
    }
    
    public static class Rot extends ClientboundMoveEntityPacket {
        public Rot() {
            this.hasRot = true;
        }
        
        public Rot(final int integer, final byte byte2, final byte byte3, final boolean boolean4) {
            super(integer);
            this.yRot = byte2;
            this.xRot = byte3;
            this.hasRot = true;
            this.onGround = boolean4;
        }
        
        @Override
        public void read(final FriendlyByteBuf je) throws IOException {
            super.read(je);
            this.yRot = je.readByte();
            this.xRot = je.readByte();
            this.onGround = je.readBoolean();
        }
        
        @Override
        public void write(final FriendlyByteBuf je) throws IOException {
            super.write(je);
            je.writeByte(this.yRot);
            je.writeByte(this.xRot);
            je.writeBoolean(this.onGround);
        }
    }
}
