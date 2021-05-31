package net.minecraft.world.entity.ai.village.poi;

import java.util.Objects;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Serializable;

public class PoiRecord implements Serializable {
    private final BlockPos pos;
    private final PoiType poiType;
    private int freeTickets;
    private final Runnable setDirty;
    
    private PoiRecord(final BlockPos ew, final PoiType aqs, final int integer, final Runnable runnable) {
        this.pos = ew.immutable();
        this.poiType = aqs;
        this.freeTickets = integer;
        this.setDirty = runnable;
    }
    
    public PoiRecord(final BlockPos ew, final PoiType aqs, final Runnable runnable) {
        this(ew, aqs, aqs.getMaxTickets(), runnable);
    }
    
    public <T> PoiRecord(final Dynamic<T> dynamic, final Runnable runnable) {
        this((BlockPos)dynamic.get("pos").map(BlockPos::deserialize).orElse(new BlockPos(0, 0, 0)), Registry.POINT_OF_INTEREST_TYPE.get(new ResourceLocation(dynamic.get("type").asString(""))), dynamic.get("free_tickets").asInt(0), runnable);
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("pos"), this.pos.<T>serialize(dynamicOps), dynamicOps.createString("type"), dynamicOps.createString(Registry.POINT_OF_INTEREST_TYPE.getKey(this.poiType).toString()), dynamicOps.createString("free_tickets"), dynamicOps.createInt(this.freeTickets)));
    }
    
    protected boolean acquireTicket() {
        if (this.freeTickets <= 0) {
            return false;
        }
        --this.freeTickets;
        this.setDirty.run();
        return true;
    }
    
    protected boolean releaseTicket() {
        if (this.freeTickets >= this.poiType.getMaxTickets()) {
            return false;
        }
        ++this.freeTickets;
        this.setDirty.run();
        return true;
    }
    
    public boolean hasSpace() {
        return this.freeTickets > 0;
    }
    
    public boolean isOccupied() {
        return this.freeTickets != this.poiType.getMaxTickets();
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public PoiType getPoiType() {
        return this.poiType;
    }
    
    public boolean equals(final Object object) {
        return this == object || (object != null && this.getClass() == object.getClass() && Objects.equals(this.pos, ((PoiRecord)object).pos));
    }
    
    public int hashCode() {
        return this.pos.hashCode();
    }
}
