package net.minecraft.world.level.levelgen;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.LeavesBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Set;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;

public class Heightmap {
    private static final Predicate<BlockState> NOT_AIR;
    private static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING;
    private final BitStorage data;
    private final Predicate<BlockState> isOpaque;
    private final ChunkAccess chunk;
    
    public Heightmap(final ChunkAccess bxh, final Types a) {
        this.data = new BitStorage(9, 256);
        this.isOpaque = a.isOpaque();
        this.chunk = bxh;
    }
    
    public static void primeHeightmaps(final ChunkAccess bxh, final Set<Types> set) {
        final int integer3 = set.size();
        final ObjectList<Heightmap> objectList4 = (ObjectList<Heightmap>)new ObjectArrayList(integer3);
        final ObjectListIterator<Heightmap> objectListIterator5 = (ObjectListIterator<Heightmap>)objectList4.iterator();
        final int integer4 = bxh.getHighestSectionPosition() + 16;
        try (final BlockPos.PooledMutableBlockPos b7 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (int integer5 = 0; integer5 < 16; ++integer5) {
                for (int integer6 = 0; integer6 < 16; ++integer6) {
                    for (final Types a12 : set) {
                        objectList4.add(bxh.getOrCreateHeightmapUnprimed(a12));
                    }
                    for (int integer7 = integer4 - 1; integer7 >= 0; --integer7) {
                        b7.set(integer5, integer7, integer6);
                        final BlockState bvt12 = bxh.getBlockState(b7);
                        if (bvt12.getBlock() != Blocks.AIR) {
                            while (objectListIterator5.hasNext()) {
                                final Heightmap bza13 = (Heightmap)objectListIterator5.next();
                                if (bza13.isOpaque.test(bvt12)) {
                                    bza13.setHeight(integer5, integer6, integer7 + 1);
                                    objectListIterator5.remove();
                                }
                            }
                            if (objectList4.isEmpty()) {
                                break;
                            }
                            objectListIterator5.back(integer3);
                        }
                    }
                }
            }
        }
    }
    
    public boolean update(final int integer1, final int integer2, final int integer3, final BlockState bvt) {
        final int integer4 = this.getFirstAvailable(integer1, integer3);
        if (integer2 <= integer4 - 2) {
            return false;
        }
        if (this.isOpaque.test(bvt)) {
            if (integer2 >= integer4) {
                this.setHeight(integer1, integer3, integer2 + 1);
                return true;
            }
        }
        else if (integer4 - 1 == integer2) {
            final BlockPos.MutableBlockPos a7 = new BlockPos.MutableBlockPos();
            for (int integer5 = integer2 - 1; integer5 >= 0; --integer5) {
                a7.set(integer1, integer5, integer3);
                if (this.isOpaque.test(this.chunk.getBlockState(a7))) {
                    this.setHeight(integer1, integer3, integer5 + 1);
                    return true;
                }
            }
            this.setHeight(integer1, integer3, 0);
            return true;
        }
        return false;
    }
    
    public int getFirstAvailable(final int integer1, final int integer2) {
        return this.getFirstAvailable(getIndex(integer1, integer2));
    }
    
    private int getFirstAvailable(final int integer) {
        return this.data.get(integer);
    }
    
    private void setHeight(final int integer1, final int integer2, final int integer3) {
        this.data.set(getIndex(integer1, integer2), integer3);
    }
    
    public void setRawData(final long[] arr) {
        System.arraycopy(arr, 0, this.data.getRaw(), 0, arr.length);
    }
    
    public long[] getRawData() {
        return this.data.getRaw();
    }
    
    private static int getIndex(final int integer1, final int integer2) {
        return integer1 + integer2 * 16;
    }
    
    static {
        NOT_AIR = (bvt -> !bvt.isAir());
        MATERIAL_MOTION_BLOCKING = (bvt -> bvt.getMaterial().blocksMotion());
    }
    
    public enum Usage {
        WORLDGEN, 
        LIVE_WORLD, 
        CLIENT;
    }
    
    public enum Types {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Usage.WORLDGEN, (Predicate<BlockState>)Heightmap.NOT_AIR), 
        WORLD_SURFACE("WORLD_SURFACE", Usage.CLIENT, (Predicate<BlockState>)Heightmap.NOT_AIR), 
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Usage.WORLDGEN, (Predicate<BlockState>)Heightmap.MATERIAL_MOTION_BLOCKING), 
        OCEAN_FLOOR("OCEAN_FLOOR", Usage.LIVE_WORLD, (Predicate<BlockState>)Heightmap.MATERIAL_MOTION_BLOCKING), 
        MOTION_BLOCKING("MOTION_BLOCKING", Usage.CLIENT, (Predicate<BlockState>)(bvt -> bvt.getMaterial().blocksMotion() || !bvt.getFluidState().isEmpty())), 
        MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Usage.LIVE_WORLD, (Predicate<BlockState>)(bvt -> (bvt.getMaterial().blocksMotion() || !bvt.getFluidState().isEmpty()) && !(bvt.getBlock() instanceof LeavesBlock)));
        
        private final String serializationKey;
        private final Usage usage;
        private final Predicate<BlockState> isOpaque;
        private static final Map<String, Types> REVERSE_LOOKUP;
        
        private Types(final String string3, final Usage b, final Predicate<BlockState> predicate) {
            this.serializationKey = string3;
            this.usage = b;
            this.isOpaque = predicate;
        }
        
        public String getSerializationKey() {
            return this.serializationKey;
        }
        
        public boolean sendToClient() {
            return this.usage == Usage.CLIENT;
        }
        
        public boolean keepAfterWorldgen() {
            return this.usage != Usage.WORLDGEN;
        }
        
        public static Types getFromKey(final String string) {
            return (Types)Types.REVERSE_LOOKUP.get(string);
        }
        
        public Predicate<BlockState> isOpaque() {
            return this.isOpaque;
        }
        
        static {
            REVERSE_LOOKUP = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
                for (final Types a5 : values()) {
                    hashMap.put(a5.serializationKey, a5);
                }
            }));
        }
    }
}
