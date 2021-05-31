package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import com.google.common.collect.Lists;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundLightUpdatePacket implements Packet<ClientGamePacketListener> {
    private int x;
    private int z;
    private int skyYMask;
    private int blockYMask;
    private int emptySkyYMask;
    private int emptyBlockYMask;
    private List<byte[]> skyUpdates;
    private List<byte[]> blockUpdates;
    
    public ClientboundLightUpdatePacket() {
    }
    
    public ClientboundLightUpdatePacket(final ChunkPos bhd, final LevelLightEngine clb) {
        this.x = bhd.x;
        this.z = bhd.z;
        this.skyUpdates = (List<byte[]>)Lists.newArrayList();
        this.blockUpdates = (List<byte[]>)Lists.newArrayList();
        for (int integer4 = 0; integer4 < 18; ++integer4) {
            final DataLayer bxn5 = clb.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(bhd, -1 + integer4));
            final DataLayer bxn6 = clb.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(bhd, -1 + integer4));
            if (bxn5 != null) {
                if (bxn5.isEmpty()) {
                    this.emptySkyYMask |= 1 << integer4;
                }
                else {
                    this.skyYMask |= 1 << integer4;
                    this.skyUpdates.add(bxn5.getData().clone());
                }
            }
            if (bxn6 != null) {
                if (bxn6.isEmpty()) {
                    this.emptyBlockYMask |= 1 << integer4;
                }
                else {
                    this.blockYMask |= 1 << integer4;
                    this.blockUpdates.add(bxn6.getData().clone());
                }
            }
        }
    }
    
    public ClientboundLightUpdatePacket(final ChunkPos bhd, final LevelLightEngine clb, final int integer3, final int integer4) {
        this.x = bhd.x;
        this.z = bhd.z;
        this.skyYMask = integer3;
        this.blockYMask = integer4;
        this.skyUpdates = (List<byte[]>)Lists.newArrayList();
        this.blockUpdates = (List<byte[]>)Lists.newArrayList();
        for (int integer5 = 0; integer5 < 18; ++integer5) {
            if ((this.skyYMask & 1 << integer5) != 0x0) {
                final DataLayer bxn7 = clb.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(bhd, -1 + integer5));
                if (bxn7 == null || bxn7.isEmpty()) {
                    this.skyYMask &= ~(1 << integer5);
                    if (bxn7 != null) {
                        this.emptySkyYMask |= 1 << integer5;
                    }
                }
                else {
                    this.skyUpdates.add(bxn7.getData().clone());
                }
            }
            if ((this.blockYMask & 1 << integer5) != 0x0) {
                final DataLayer bxn7 = clb.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(bhd, -1 + integer5));
                if (bxn7 == null || bxn7.isEmpty()) {
                    this.blockYMask &= ~(1 << integer5);
                    if (bxn7 != null) {
                        this.emptyBlockYMask |= 1 << integer5;
                    }
                }
                else {
                    this.blockUpdates.add(bxn7.getData().clone());
                }
            }
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.x = je.readVarInt();
        this.z = je.readVarInt();
        this.skyYMask = je.readVarInt();
        this.blockYMask = je.readVarInt();
        this.emptySkyYMask = je.readVarInt();
        this.emptyBlockYMask = je.readVarInt();
        this.skyUpdates = (List<byte[]>)Lists.newArrayList();
        for (int integer3 = 0; integer3 < 18; ++integer3) {
            if ((this.skyYMask & 1 << integer3) != 0x0) {
                this.skyUpdates.add(je.readByteArray(2048));
            }
        }
        this.blockUpdates = (List<byte[]>)Lists.newArrayList();
        for (int integer3 = 0; integer3 < 18; ++integer3) {
            if ((this.blockYMask & 1 << integer3) != 0x0) {
                this.blockUpdates.add(je.readByteArray(2048));
            }
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.x);
        je.writeVarInt(this.z);
        je.writeVarInt(this.skyYMask);
        je.writeVarInt(this.blockYMask);
        je.writeVarInt(this.emptySkyYMask);
        je.writeVarInt(this.emptyBlockYMask);
        for (final byte[] arr4 : this.skyUpdates) {
            je.writeByteArray(arr4);
        }
        for (final byte[] arr4 : this.blockUpdates) {
            je.writeByteArray(arr4);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleLightUpdatePacked(this);
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public int getSkyYMask() {
        return this.skyYMask;
    }
    
    public int getEmptySkyYMask() {
        return this.emptySkyYMask;
    }
    
    public List<byte[]> getSkyUpdates() {
        return this.skyUpdates;
    }
    
    public int getBlockYMask() {
        return this.blockYMask;
    }
    
    public int getEmptyBlockYMask() {
        return this.emptyBlockYMask;
    }
    
    public List<byte[]> getBlockUpdates() {
        return this.blockUpdates;
    }
}
