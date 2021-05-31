package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import java.util.List;

public class NetherFortressFeature extends StructureFeature<NoneFeatureConfiguration> {
    private static final List<Biome.SpawnerData> FORTRESS_ENEMIES;
    
    public NetherFortressFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        final int integer5 = integer3 >> 4;
        final int integer6 = integer4 >> 4;
        random.setSeed((long)(integer5 ^ integer6 << 4) ^ bxi.getSeed());
        random.nextInt();
        if (random.nextInt(3) != 0) {
            return false;
        }
        if (integer3 != (integer5 << 4) + 4 + random.nextInt(8)) {
            return false;
        }
        if (integer4 != (integer6 << 4) + 4 + random.nextInt(8)) {
            return false;
        }
        final Biome bio8 = bxi.getBiomeSource().getBiome(new BlockPos((integer3 << 4) + 9, 0, (integer4 << 4) + 9));
        return bxi.isBiomeValidStartForStructure(bio8, Feature.NETHER_BRIDGE);
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return NetherBridgeStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Fortress";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    @Override
    public List<Biome.SpawnerData> getSpecialEnemies() {
        return NetherFortressFeature.FORTRESS_ENEMIES;
    }
    
    static {
        FORTRESS_ENEMIES = (List)Lists.newArrayList((Object[])new Biome.SpawnerData[] { new Biome.SpawnerData(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnerData(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnerData(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4) });
    }
    
    public static class NetherBridgeStart extends StructureStart {
        public NetherBridgeStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final NetherBridgePieces.StartPiece q7 = new NetherBridgePieces.StartPiece(this.random, (integer3 << 4) + 2, (integer4 << 4) + 2);
            this.pieces.add(q7);
            q7.addChildren(q7, this.pieces, this.random);
            final List<StructurePiece> list8 = q7.pendingChildren;
            while (!list8.isEmpty()) {
                final int integer5 = this.random.nextInt(list8.size());
                final StructurePiece civ10 = (StructurePiece)list8.remove(integer5);
                civ10.addChildren(q7, this.pieces, this.random);
            }
            this.calculateBoundingBox();
            this.moveInsideHeights(this.random, 48, 70);
        }
    }
}
