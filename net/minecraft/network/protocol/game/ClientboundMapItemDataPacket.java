package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Collection;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.network.protocol.Packet;

public class ClientboundMapItemDataPacket implements Packet<ClientGamePacketListener> {
    private int mapId;
    private byte scale;
    private boolean trackingPosition;
    private boolean locked;
    private MapDecoration[] decorations;
    private int startX;
    private int startY;
    private int width;
    private int height;
    private byte[] mapColors;
    
    public ClientboundMapItemDataPacket() {
    }
    
    public ClientboundMapItemDataPacket(final int integer1, final byte byte2, final boolean boolean3, final boolean boolean4, final Collection<MapDecoration> collection, final byte[] arr, final int integer7, final int integer8, final int integer9, final int integer10) {
        this.mapId = integer1;
        this.scale = byte2;
        this.trackingPosition = boolean3;
        this.locked = boolean4;
        this.decorations = (MapDecoration[])collection.toArray((Object[])new MapDecoration[collection.size()]);
        this.startX = integer7;
        this.startY = integer8;
        this.width = integer9;
        this.height = integer10;
        this.mapColors = new byte[integer9 * integer10];
        for (int integer11 = 0; integer11 < integer9; ++integer11) {
            for (int integer12 = 0; integer12 < integer10; ++integer12) {
                this.mapColors[integer11 + integer12 * integer9] = arr[integer7 + integer11 + (integer8 + integer12) * 128];
            }
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.mapId = je.readVarInt();
        this.scale = je.readByte();
        this.trackingPosition = je.readBoolean();
        this.locked = je.readBoolean();
        this.decorations = new MapDecoration[je.readVarInt()];
        for (int integer3 = 0; integer3 < this.decorations.length; ++integer3) {
            final MapDecoration.Type a4 = je.<MapDecoration.Type>readEnum(MapDecoration.Type.class);
            this.decorations[integer3] = new MapDecoration(a4, je.readByte(), je.readByte(), (byte)(je.readByte() & 0xF), je.readBoolean() ? je.readComponent() : null);
        }
        this.width = je.readUnsignedByte();
        if (this.width > 0) {
            this.height = je.readUnsignedByte();
            this.startX = je.readUnsignedByte();
            this.startY = je.readUnsignedByte();
            this.mapColors = je.readByteArray();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.mapId);
        je.writeByte(this.scale);
        je.writeBoolean(this.trackingPosition);
        je.writeBoolean(this.locked);
        je.writeVarInt(this.decorations.length);
        for (final MapDecoration coe6 : this.decorations) {
            je.writeEnum(coe6.getType());
            je.writeByte(coe6.getX());
            je.writeByte(coe6.getY());
            je.writeByte(coe6.getRot() & 0xF);
            if (coe6.getName() != null) {
                je.writeBoolean(true);
                je.writeComponent(coe6.getName());
            }
            else {
                je.writeBoolean(false);
            }
        }
        je.writeByte(this.width);
        if (this.width > 0) {
            je.writeByte(this.height);
            je.writeByte(this.startX);
            je.writeByte(this.startY);
            je.writeByteArray(this.mapColors);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleMapItemData(this);
    }
    
    public int getMapId() {
        return this.mapId;
    }
    
    public void applyToMap(final MapItemSavedData coh) {
        coh.scale = this.scale;
        coh.trackingPosition = this.trackingPosition;
        coh.locked = this.locked;
        coh.decorations.clear();
        for (int integer3 = 0; integer3 < this.decorations.length; ++integer3) {
            final MapDecoration coe4 = this.decorations[integer3];
            coh.decorations.put(new StringBuilder().append("icon-").append(integer3).toString(), coe4);
        }
        for (int integer3 = 0; integer3 < this.width; ++integer3) {
            for (int integer4 = 0; integer4 < this.height; ++integer4) {
                coh.colors[this.startX + integer3 + (this.startY + integer4) * 128] = this.mapColors[integer3 + integer4 * this.width];
            }
        }
    }
}
