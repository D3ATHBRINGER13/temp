package net.minecraft.world.level.levelgen.feature;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class VillagePieces {
    public static void addPieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final BlockPos ew, final List<StructurePiece> list, final WorldgenRandom bzk, final VillageConfiguration cfc) {
        PlainVillagePools.bootstrap();
        SnowyVillagePools.bootstrap();
        SavannaVillagePools.bootstrap();
        DesertVillagePools.bootstrap();
        TaigaVillagePools.bootstrap();
        JigsawPlacement.addPieces(cfc.startPool, cfc.size, VillagePiece::new, bxi, cjp, ew, list, bzk);
    }
    
    public static class VillagePiece extends PoolElementStructurePiece {
        public VillagePiece(final StructureManager cjp, final StructurePoolElement cfr, final BlockPos ew, final int integer, final Rotation brg, final BoundingBox cic) {
            super(StructurePieceType.VILLAGE, cjp, cfr, ew, integer, brg, cic);
        }
        
        public VillagePiece(final StructureManager cjp, final CompoundTag id) {
            super(cjp, id, StructurePieceType.VILLAGE);
        }
    }
}
