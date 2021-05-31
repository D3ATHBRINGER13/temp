package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.util.Mth;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetStructureBlockPacket implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private StructureBlockEntity.UpdateType updateType;
    private StructureMode mode;
    private String name;
    private BlockPos offset;
    private BlockPos size;
    private Mirror mirror;
    private Rotation rotation;
    private String data;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private float integrity;
    private long seed;
    
    public ServerboundSetStructureBlockPacket() {
    }
    
    public ServerboundSetStructureBlockPacket(final BlockPos ew1, final StructureBlockEntity.UpdateType a, final StructureMode bxb, final String string4, final BlockPos ew5, final BlockPos ew6, final Mirror bqg, final Rotation brg, final String string9, final boolean boolean10, final boolean boolean11, final boolean boolean12, final float float13, final long long14) {
        this.pos = ew1;
        this.updateType = a;
        this.mode = bxb;
        this.name = string4;
        this.offset = ew5;
        this.size = ew6;
        this.mirror = bqg;
        this.rotation = brg;
        this.data = string9;
        this.ignoreEntities = boolean10;
        this.showAir = boolean11;
        this.showBoundingBox = boolean12;
        this.integrity = float13;
        this.seed = long14;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
        this.updateType = je.<StructureBlockEntity.UpdateType>readEnum(StructureBlockEntity.UpdateType.class);
        this.mode = je.<StructureMode>readEnum(StructureMode.class);
        this.name = je.readUtf(32767);
        this.offset = new BlockPos(Mth.clamp(je.readByte(), -32, 32), Mth.clamp(je.readByte(), -32, 32), Mth.clamp(je.readByte(), -32, 32));
        this.size = new BlockPos(Mth.clamp(je.readByte(), 0, 32), Mth.clamp(je.readByte(), 0, 32), Mth.clamp(je.readByte(), 0, 32));
        this.mirror = je.<Mirror>readEnum(Mirror.class);
        this.rotation = je.<Rotation>readEnum(Rotation.class);
        this.data = je.readUtf(12);
        this.integrity = Mth.clamp(je.readFloat(), 0.0f, 1.0f);
        this.seed = je.readVarLong();
        final int integer3 = je.readByte();
        this.ignoreEntities = ((integer3 & 0x1) != 0x0);
        this.showAir = ((integer3 & 0x2) != 0x0);
        this.showBoundingBox = ((integer3 & 0x4) != 0x0);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
        je.writeEnum(this.updateType);
        je.writeEnum(this.mode);
        je.writeUtf(this.name);
        je.writeByte(this.offset.getX());
        je.writeByte(this.offset.getY());
        je.writeByte(this.offset.getZ());
        je.writeByte(this.size.getX());
        je.writeByte(this.size.getY());
        je.writeByte(this.size.getZ());
        je.writeEnum(this.mirror);
        je.writeEnum(this.rotation);
        je.writeUtf(this.data);
        je.writeFloat(this.integrity);
        je.writeVarLong(this.seed);
        int integer3 = 0;
        if (this.ignoreEntities) {
            integer3 |= 0x1;
        }
        if (this.showAir) {
            integer3 |= 0x2;
        }
        if (this.showBoundingBox) {
            integer3 |= 0x4;
        }
        je.writeByte(integer3);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSetStructureBlock(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public StructureBlockEntity.UpdateType getUpdateType() {
        return this.updateType;
    }
    
    public StructureMode getMode() {
        return this.mode;
    }
    
    public String getName() {
        return this.name;
    }
    
    public BlockPos getOffset() {
        return this.offset;
    }
    
    public BlockPos getSize() {
        return this.size;
    }
    
    public Mirror getMirror() {
        return this.mirror;
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public String getData() {
        return this.data;
    }
    
    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }
    
    public boolean isShowAir() {
        return this.showAir;
    }
    
    public boolean isShowBoundingBox() {
        return this.showBoundingBox;
    }
    
    public float getIntegrity() {
        return this.integrity;
    }
    
    public long getSeed() {
        return this.seed;
    }
}
