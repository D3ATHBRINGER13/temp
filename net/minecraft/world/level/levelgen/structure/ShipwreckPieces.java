package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import java.util.Random;
import java.util.List;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class ShipwreckPieces {
    private static final BlockPos PIVOT;
    private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED;
    private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN;
    
    public static void addPieces(final StructureManager cjp, final BlockPos ew, final Rotation brg, final List<StructurePiece> list, final Random random, final ShipwreckConfiguration cee) {
        final ResourceLocation qv7 = cee.isBeached ? ShipwreckPieces.STRUCTURE_LOCATION_BEACHED[random.nextInt(ShipwreckPieces.STRUCTURE_LOCATION_BEACHED.length)] : ShipwreckPieces.STRUCTURE_LOCATION_OCEAN[random.nextInt(ShipwreckPieces.STRUCTURE_LOCATION_OCEAN.length)];
        list.add(new ShipwreckPiece(cjp, qv7, ew, brg, cee.isBeached));
    }
    
    static {
        PIVOT = new BlockPos(4, 0, 15);
        STRUCTURE_LOCATION_BEACHED = new ResourceLocation[] { new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded") };
        STRUCTURE_LOCATION_OCEAN = new ResourceLocation[] { new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded") };
    }
    
    public static class ShipwreckPiece extends TemplateStructurePiece {
        private final Rotation rotation;
        private final ResourceLocation templateLocation;
        private final boolean isBeached;
        
        public ShipwreckPiece(final StructureManager cjp, final ResourceLocation qv, final BlockPos ew, final Rotation brg, final boolean boolean5) {
            super(StructurePieceType.SHIPWRECK_PIECE, 0);
            this.templatePosition = ew;
            this.rotation = brg;
            this.templateLocation = qv;
            this.isBeached = boolean5;
            this.loadTemplate(cjp);
        }
        
        public ShipwreckPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.SHIPWRECK_PIECE, id);
            this.templateLocation = new ResourceLocation(id.getString("Template"));
            this.isBeached = id.getBoolean("isBeached");
            this.rotation = Rotation.valueOf(id.getString("Rot"));
            this.loadTemplate(cjp);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putString("Template", this.templateLocation.toString());
            id.putBoolean("isBeached", this.isBeached);
            id.putString("Rot", this.rotation.name());
        }
        
        private void loadTemplate(final StructureManager cjp) {
            final StructureTemplate cjt3 = cjp.getOrCreate(this.templateLocation);
            final StructurePlaceSettings cjq4 = new StructurePlaceSettings().setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(ShipwreckPieces.PIVOT).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            this.setup(cjt3, this.templatePosition, cjq4);
        }
        
        @Override
        protected void handleDataMarker(final String string, final BlockPos ew, final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if ("map_chest".equals(string)) {
                RandomizableContainerBlockEntity.setLootTable(bhs, random, ew.below(), BuiltInLootTables.SHIPWRECK_MAP);
            }
            else if ("treasure_chest".equals(string)) {
                RandomizableContainerBlockEntity.setLootTable(bhs, random, ew.below(), BuiltInLootTables.SHIPWRECK_TREASURE);
            }
            else if ("supply_chest".equals(string)) {
                RandomizableContainerBlockEntity.setLootTable(bhs, random, ew.below(), BuiltInLootTables.SHIPWRECK_SUPPLY);
            }
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            int integer6 = 256;
            int integer7 = 0;
            final BlockPos ew8 = this.templatePosition.offset(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1);
            for (final BlockPos ew9 : BlockPos.betweenClosed(this.templatePosition, ew8)) {
                final int integer8 = bhs.getHeight(this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG, ew9.getX(), ew9.getZ());
                integer7 += integer8;
                integer6 = Math.min(integer6, integer8);
            }
            integer7 /= this.template.getSize().getX() * this.template.getSize().getZ();
            final int integer9 = this.isBeached ? (integer6 - this.template.getSize().getY() / 2 - random.nextInt(3)) : integer7;
            this.templatePosition = new BlockPos(this.templatePosition.getX(), integer9, this.templatePosition.getZ());
            return super.postProcess(bhs, random, cic, bhd);
        }
    }
}
