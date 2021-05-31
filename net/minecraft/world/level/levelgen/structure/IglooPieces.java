package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import java.util.Random;
import java.util.List;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.core.BlockPos;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class IglooPieces {
    private static final ResourceLocation STRUCTURE_LOCATION_IGLOO;
    private static final ResourceLocation STRUCTURE_LOCATION_LADDER;
    private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY;
    private static final Map<ResourceLocation, BlockPos> PIVOTS;
    private static final Map<ResourceLocation, BlockPos> OFFSETS;
    
    public static void addPieces(final StructureManager cjp, final BlockPos ew, final Rotation brg, final List<StructurePiece> list, final Random random, final NoneFeatureConfiguration cdd) {
        if (random.nextDouble() < 0.5) {
            final int integer7 = random.nextInt(8) + 4;
            list.add(new IglooPiece(cjp, IglooPieces.STRUCTURE_LOCATION_LABORATORY, ew, brg, integer7 * 3));
            for (int integer8 = 0; integer8 < integer7 - 1; ++integer8) {
                list.add(new IglooPiece(cjp, IglooPieces.STRUCTURE_LOCATION_LADDER, ew, brg, integer8 * 3));
            }
        }
        list.add(new IglooPiece(cjp, IglooPieces.STRUCTURE_LOCATION_IGLOO, ew, brg, 0));
    }
    
    static {
        STRUCTURE_LOCATION_IGLOO = new ResourceLocation("igloo/top");
        STRUCTURE_LOCATION_LADDER = new ResourceLocation("igloo/middle");
        STRUCTURE_LOCATION_LABORATORY = new ResourceLocation("igloo/bottom");
        PIVOTS = (Map)ImmutableMap.of(IglooPieces.STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), IglooPieces.STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), IglooPieces.STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
        OFFSETS = (Map)ImmutableMap.of(IglooPieces.STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, IglooPieces.STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), IglooPieces.STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));
    }
    
    public static class IglooPiece extends TemplateStructurePiece {
        private final ResourceLocation templateLocation;
        private final Rotation rotation;
        
        public IglooPiece(final StructureManager cjp, final ResourceLocation qv, final BlockPos ew, final Rotation brg, final int integer) {
            super(StructurePieceType.IGLOO, 0);
            this.templateLocation = qv;
            final BlockPos ew2 = (BlockPos)IglooPieces.OFFSETS.get(qv);
            this.templatePosition = ew.offset(ew2.getX(), ew2.getY() - integer, ew2.getZ());
            this.rotation = brg;
            this.loadTemplate(cjp);
        }
        
        public IglooPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.IGLOO, id);
            this.templateLocation = new ResourceLocation(id.getString("Template"));
            this.rotation = Rotation.valueOf(id.getString("Rot"));
            this.loadTemplate(cjp);
        }
        
        private void loadTemplate(final StructureManager cjp) {
            final StructureTemplate cjt3 = cjp.getOrCreate(this.templateLocation);
            final StructurePlaceSettings cjq4 = new StructurePlaceSettings().setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
            this.setup(cjt3, this.templatePosition, cjq4);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putString("Template", this.templateLocation.toString());
            id.putString("Rot", this.rotation.name());
        }
        
        @Override
        protected void handleDataMarker(final String string, final BlockPos ew, final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (!"chest".equals(string)) {
                return;
            }
            bhs.setBlock(ew, Blocks.AIR.defaultBlockState(), 3);
            final BlockEntity btw7 = bhs.getBlockEntity(ew.below());
            if (btw7 instanceof ChestBlockEntity) {
                ((ChestBlockEntity)btw7).setLootTable(BuiltInLootTables.IGLOO_CHEST, random.nextLong());
            }
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final StructurePlaceSettings cjq6 = new StructurePlaceSettings().setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
            final BlockPos ew7 = (BlockPos)IglooPieces.OFFSETS.get(this.templateLocation);
            final BlockPos ew8 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(cjq6, new BlockPos(3 - ew7.getX(), 0, 0 - ew7.getZ())));
            final int integer9 = bhs.getHeight(Heightmap.Types.WORLD_SURFACE_WG, ew8.getX(), ew8.getZ());
            final BlockPos ew9 = this.templatePosition;
            this.templatePosition = this.templatePosition.offset(0, integer9 - 90 - 1, 0);
            final boolean boolean11 = super.postProcess(bhs, random, cic, bhd);
            if (this.templateLocation.equals(IglooPieces.STRUCTURE_LOCATION_IGLOO)) {
                final BlockPos ew10 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(cjq6, new BlockPos(3, 0, 5)));
                final BlockState bvt13 = bhs.getBlockState(ew10.below());
                if (!bvt13.isAir() && bvt13.getBlock() != Blocks.LADDER) {
                    bhs.setBlock(ew10, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                }
            }
            this.templatePosition = ew9;
            return boolean11;
        }
    }
}
