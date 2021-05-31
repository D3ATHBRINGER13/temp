package net.minecraft.core;

import java.util.Optional;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.util.Serializable;

public final class GlobalPos implements Serializable {
    private final DimensionType dimension;
    private final BlockPos pos;
    
    private GlobalPos(final DimensionType byn, final BlockPos ew) {
        this.dimension = byn;
        this.pos = ew;
    }
    
    public static GlobalPos of(final DimensionType byn, final BlockPos ew) {
        return new GlobalPos(byn, ew);
    }
    
    public static GlobalPos of(final Dynamic<?> dynamic) {
        return (GlobalPos)dynamic.get("dimension").map(DimensionType::of).flatMap(byn -> dynamic.get("pos").map(BlockPos::deserialize).map(ew -> new GlobalPos(byn, ew))).orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
    }
    
    public DimensionType dimension() {
        return this.dimension;
    }
    
    public BlockPos pos() {
        return this.pos;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final GlobalPos fd3 = (GlobalPos)object;
        return Objects.equals(this.dimension, fd3.dimension) && Objects.equals(this.pos, fd3.pos);
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.dimension, this.pos });
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("dimension"), this.dimension.<T>serialize(dynamicOps), dynamicOps.createString("pos"), this.pos.<T>serialize(dynamicOps)));
    }
    
    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}
