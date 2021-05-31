package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetBorderPacket implements Packet<ClientGamePacketListener> {
    private Type type;
    private int newAbsoluteMaxSize;
    private double newCenterX;
    private double newCenterZ;
    private double newSize;
    private double oldSize;
    private long lerpTime;
    private int warningTime;
    private int warningBlocks;
    
    public ClientboundSetBorderPacket() {
    }
    
    public ClientboundSetBorderPacket(final WorldBorder bxf, final Type a) {
        this.type = a;
        this.newCenterX = bxf.getCenterX();
        this.newCenterZ = bxf.getCenterZ();
        this.oldSize = bxf.getSize();
        this.newSize = bxf.getLerpTarget();
        this.lerpTime = bxf.getLerpRemainingTime();
        this.newAbsoluteMaxSize = bxf.getAbsoluteMaxSize();
        this.warningBlocks = bxf.getWarningBlocks();
        this.warningTime = bxf.getWarningTime();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.type = je.<Type>readEnum(Type.class);
        switch (this.type) {
            case SET_SIZE: {
                this.newSize = je.readDouble();
                break;
            }
            case LERP_SIZE: {
                this.oldSize = je.readDouble();
                this.newSize = je.readDouble();
                this.lerpTime = je.readVarLong();
                break;
            }
            case SET_CENTER: {
                this.newCenterX = je.readDouble();
                this.newCenterZ = je.readDouble();
                break;
            }
            case SET_WARNING_BLOCKS: {
                this.warningBlocks = je.readVarInt();
                break;
            }
            case SET_WARNING_TIME: {
                this.warningTime = je.readVarInt();
                break;
            }
            case INITIALIZE: {
                this.newCenterX = je.readDouble();
                this.newCenterZ = je.readDouble();
                this.oldSize = je.readDouble();
                this.newSize = je.readDouble();
                this.lerpTime = je.readVarLong();
                this.newAbsoluteMaxSize = je.readVarInt();
                this.warningBlocks = je.readVarInt();
                this.warningTime = je.readVarInt();
                break;
            }
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.type);
        switch (this.type) {
            case SET_SIZE: {
                je.writeDouble(this.newSize);
                break;
            }
            case LERP_SIZE: {
                je.writeDouble(this.oldSize);
                je.writeDouble(this.newSize);
                je.writeVarLong(this.lerpTime);
                break;
            }
            case SET_CENTER: {
                je.writeDouble(this.newCenterX);
                je.writeDouble(this.newCenterZ);
                break;
            }
            case SET_WARNING_TIME: {
                je.writeVarInt(this.warningTime);
                break;
            }
            case SET_WARNING_BLOCKS: {
                je.writeVarInt(this.warningBlocks);
                break;
            }
            case INITIALIZE: {
                je.writeDouble(this.newCenterX);
                je.writeDouble(this.newCenterZ);
                je.writeDouble(this.oldSize);
                je.writeDouble(this.newSize);
                je.writeVarLong(this.lerpTime);
                je.writeVarInt(this.newAbsoluteMaxSize);
                je.writeVarInt(this.warningBlocks);
                je.writeVarInt(this.warningTime);
                break;
            }
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetBorder(this);
    }
    
    public void applyChanges(final WorldBorder bxf) {
        switch (this.type) {
            case SET_SIZE: {
                bxf.setSize(this.newSize);
                break;
            }
            case LERP_SIZE: {
                bxf.lerpSizeBetween(this.oldSize, this.newSize, this.lerpTime);
                break;
            }
            case SET_CENTER: {
                bxf.setCenter(this.newCenterX, this.newCenterZ);
                break;
            }
            case INITIALIZE: {
                bxf.setCenter(this.newCenterX, this.newCenterZ);
                if (this.lerpTime > 0L) {
                    bxf.lerpSizeBetween(this.oldSize, this.newSize, this.lerpTime);
                }
                else {
                    bxf.setSize(this.newSize);
                }
                bxf.setAbsoluteMaxSize(this.newAbsoluteMaxSize);
                bxf.setWarningBlocks(this.warningBlocks);
                bxf.setWarningTime(this.warningTime);
                break;
            }
            case SET_WARNING_TIME: {
                bxf.setWarningTime(this.warningTime);
                break;
            }
            case SET_WARNING_BLOCKS: {
                bxf.setWarningBlocks(this.warningBlocks);
                break;
            }
        }
    }
    
    public enum Type {
        SET_SIZE, 
        LERP_SIZE, 
        SET_CENTER, 
        INITIALIZE, 
        SET_WARNING_TIME, 
        SET_WARNING_BLOCKS;
    }
}
