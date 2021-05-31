package net.minecraft.world.level.chunk.storage;

import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.OldDataLayer;
import net.minecraft.nbt.CompoundTag;

public class OldChunkStorage {
    public static OldLevelChunk load(final CompoundTag id) {
        final int integer2 = id.getInt("xPos");
        final int integer3 = id.getInt("zPos");
        final OldLevelChunk a4 = new OldLevelChunk(integer2, integer3);
        a4.blocks = id.getByteArray("Blocks");
        a4.data = new OldDataLayer(id.getByteArray("Data"), 7);
        a4.skyLight = new OldDataLayer(id.getByteArray("SkyLight"), 7);
        a4.blockLight = new OldDataLayer(id.getByteArray("BlockLight"), 7);
        a4.heightmap = id.getByteArray("HeightMap");
        a4.terrainPopulated = id.getBoolean("TerrainPopulated");
        a4.entities = id.getList("Entities", 10);
        a4.blockEntities = id.getList("TileEntities", 10);
        a4.blockTicks = id.getList("TileTicks", 10);
        try {
            a4.lastUpdated = id.getLong("LastUpdate");
        }
        catch (ClassCastException classCastException5) {
            a4.lastUpdated = id.getInt("LastUpdate");
        }
        return a4;
    }
    
    public static void convertToAnvilFormat(final OldLevelChunk a, final CompoundTag id, final BiomeSource biq) {
        id.putInt("xPos", a.x);
        id.putInt("zPos", a.z);
        id.putLong("LastUpdate", a.lastUpdated);
        final int[] arr4 = new int[a.heightmap.length];
        for (int integer5 = 0; integer5 < a.heightmap.length; ++integer5) {
            arr4[integer5] = a.heightmap[integer5];
        }
        id.putIntArray("HeightMap", arr4);
        id.putBoolean("TerrainPopulated", a.terrainPopulated);
        final ListTag ik5 = new ListTag();
        for (int integer6 = 0; integer6 < 8; ++integer6) {
            boolean boolean7 = true;
            for (int integer7 = 0; integer7 < 16 && boolean7; ++integer7) {
                for (int integer8 = 0; integer8 < 16 && boolean7; ++integer8) {
                    for (int integer9 = 0; integer9 < 16; ++integer9) {
                        final int integer10 = integer7 << 11 | integer9 << 7 | integer8 + (integer6 << 4);
                        final int integer11 = a.blocks[integer10];
                        if (integer11 != 0) {
                            boolean7 = false;
                            break;
                        }
                    }
                }
            }
            if (!boolean7) {
                final byte[] arr5 = new byte[4096];
                final DataLayer bxn9 = new DataLayer();
                final DataLayer bxn10 = new DataLayer();
                final DataLayer bxn11 = new DataLayer();
                for (int integer11 = 0; integer11 < 16; ++integer11) {
                    for (int integer12 = 0; integer12 < 16; ++integer12) {
                        for (int integer13 = 0; integer13 < 16; ++integer13) {
                            final int integer14 = integer11 << 11 | integer13 << 7 | integer12 + (integer6 << 4);
                            final int integer15 = a.blocks[integer14];
                            arr5[integer12 << 8 | integer13 << 4 | integer11] = (byte)(integer15 & 0xFF);
                            bxn9.set(integer11, integer12, integer13, a.data.get(integer11, integer12 + (integer6 << 4), integer13));
                            bxn10.set(integer11, integer12, integer13, a.skyLight.get(integer11, integer12 + (integer6 << 4), integer13));
                            bxn11.set(integer11, integer12, integer13, a.blockLight.get(integer11, integer12 + (integer6 << 4), integer13));
                        }
                    }
                }
                final CompoundTag id2 = new CompoundTag();
                id2.putByte("Y", (byte)(integer6 & 0xFF));
                id2.putByteArray("Blocks", arr5);
                id2.putByteArray("Data", bxn9.getData());
                id2.putByteArray("SkyLight", bxn10.getData());
                id2.putByteArray("BlockLight", bxn11.getData());
                ik5.add(id2);
            }
        }
        id.put("Sections", (Tag)ik5);
        final byte[] arr6 = new byte[256];
        final BlockPos.MutableBlockPos a2 = new BlockPos.MutableBlockPos();
        for (int integer7 = 0; integer7 < 16; ++integer7) {
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                a2.set(a.x << 4 | integer7, 0, a.z << 4 | integer8);
                arr6[integer8 << 4 | integer7] = (byte)(Registry.BIOME.getId(biq.getBiome(a2)) & 0xFF);
            }
        }
        id.putByteArray("Biomes", arr6);
        id.put("Entities", (Tag)a.entities);
        id.put("TileEntities", (Tag)a.blockEntities);
        if (a.blockTicks != null) {
            id.put("TileTicks", (Tag)a.blockTicks);
        }
        id.putBoolean("convertedFromAlphaFormat", true);
    }
    
    public static class OldLevelChunk {
        public long lastUpdated;
        public boolean terrainPopulated;
        public byte[] heightmap;
        public OldDataLayer blockLight;
        public OldDataLayer skyLight;
        public OldDataLayer data;
        public byte[] blocks;
        public ListTag entities;
        public ListTag blockEntities;
        public ListTag blockTicks;
        public final int x;
        public final int z;
        
        public OldLevelChunk(final int integer1, final int integer2) {
            this.x = integer1;
            this.z = integer2;
        }
    }
}
