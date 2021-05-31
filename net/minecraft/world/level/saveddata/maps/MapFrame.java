package net.minecraft.world.level.saveddata.maps;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

public class MapFrame {
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;
    
    public MapFrame(final BlockPos ew, final int integer2, final int integer3) {
        this.pos = ew;
        this.rotation = integer2;
        this.entityId = integer3;
    }
    
    public static MapFrame load(final CompoundTag id) {
        final BlockPos ew2 = NbtUtils.readBlockPos(id.getCompound("Pos"));
        final int integer3 = id.getInt("Rotation");
        final int integer4 = id.getInt("EntityId");
        return new MapFrame(ew2, integer3, integer4);
    }
    
    public CompoundTag save() {
        final CompoundTag id2 = new CompoundTag();
        id2.put("Pos", (Tag)NbtUtils.writeBlockPos(this.pos));
        id2.putInt("Rotation", this.rotation);
        id2.putInt("EntityId", this.entityId);
        return id2;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public int getRotation() {
        return this.rotation;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public String getId() {
        return frameId(this.pos);
    }
    
    public static String frameId(final BlockPos ew) {
        return new StringBuilder().append("frame-").append(ew.getX()).append(",").append(ew.getY()).append(",").append(ew.getZ()).toString();
    }
}
