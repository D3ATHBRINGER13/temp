package net.minecraft.world.level.levelgen.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PillagerOutpostPieces {
    public static void addPieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final BlockPos ew, final List<StructurePiece> list, final WorldgenRandom bzk) {
        JigsawPlacement.addPieces(new ResourceLocation("pillager_outpost/base_plates"), 7, PillagerOutpostPiece::new, bxi, cjp, ew, list, bzk);
    }
    
    static {
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/base_plates"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(Pair.of((Object)new SinglePoolElement("pillager_outpost/base_plate"), (Object)1)), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/towers"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(Pair.of((Object)new ListPoolElement((List<StructurePoolElement>)ImmutableList.of((Object)new SinglePoolElement("pillager_outpost/watchtower"), (Object)new SinglePoolElement("pillager_outpost/watchtower_overgrown", (List<StructureProcessor>)ImmutableList.of((Object)new BlockRotProcessor(0.05f))))), (Object)1)), StructureTemplatePool.Projection.RIGID));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/feature_plates"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_plate"), (Object)1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/features"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_cage1"), (Object)1), Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_cage2"), (Object)1), Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_logs"), (Object)1), Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_tent1"), (Object)1), Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_tent2"), (Object)1), Pair.of((Object)new SinglePoolElement("pillager_outpost/feature_targets"), (Object)1), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)6)), StructureTemplatePool.Projection.RIGID));
    }
    
    public static class PillagerOutpostPiece extends PoolElementStructurePiece {
        public PillagerOutpostPiece(final StructureManager cjp, final StructurePoolElement cfr, final BlockPos ew, final int integer, final Rotation brg, final BoundingBox cic) {
            super(StructurePieceType.PILLAGER_OUTPOST, cjp, cfr, ew, integer, brg, cic);
        }
        
        public PillagerOutpostPiece(final StructureManager cjp, final CompoundTag id) {
            super(cjp, id, StructurePieceType.PILLAGER_OUTPOST);
        }
    }
}
