package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.Tuple;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public class EndCityPieces {
    private static final StructurePlaceSettings OVERWRITE;
    private static final StructurePlaceSettings INSERT;
    private static final SectionGenerator HOUSE_TOWER_GENERATOR;
    private static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES;
    private static final SectionGenerator TOWER_GENERATOR;
    private static final SectionGenerator TOWER_BRIDGE_GENERATOR;
    private static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES;
    private static final SectionGenerator FAT_TOWER_GENERATOR;
    
    private static EndCityPiece addPiece(final StructureManager cjp, final EndCityPiece a, final BlockPos ew, final String string, final Rotation brg, final boolean boolean6) {
        final EndCityPiece a2 = new EndCityPiece(cjp, string, a.templatePosition, brg, boolean6);
        final BlockPos ew2 = a.template.calculateConnectedPosition(a.placeSettings, ew, a2.placeSettings, BlockPos.ZERO);
        a2.move(ew2.getX(), ew2.getY(), ew2.getZ());
        return a2;
    }
    
    public static void startHouseTower(final StructureManager cjp, final BlockPos ew, final Rotation brg, final List<StructurePiece> list, final Random random) {
        EndCityPieces.FAT_TOWER_GENERATOR.init();
        EndCityPieces.HOUSE_TOWER_GENERATOR.init();
        EndCityPieces.TOWER_BRIDGE_GENERATOR.init();
        EndCityPieces.TOWER_GENERATOR.init();
        EndCityPiece a6 = addHelper(list, new EndCityPiece(cjp, "base_floor", ew, brg, true));
        a6 = addHelper(list, addPiece(cjp, a6, new BlockPos(-1, 0, -1), "second_floor_1", brg, false));
        a6 = addHelper(list, addPiece(cjp, a6, new BlockPos(-1, 4, -1), "third_floor_1", brg, false));
        a6 = addHelper(list, addPiece(cjp, a6, new BlockPos(-1, 8, -1), "third_roof", brg, true));
        recursiveChildren(cjp, EndCityPieces.TOWER_GENERATOR, 1, a6, null, list, random);
    }
    
    private static EndCityPiece addHelper(final List<StructurePiece> list, final EndCityPiece a) {
        list.add(a);
        return a;
    }
    
    private static boolean recursiveChildren(final StructureManager cjp, final SectionGenerator b, final int integer, final EndCityPiece a, final BlockPos ew, final List<StructurePiece> list, final Random random) {
        if (integer > 8) {
            return false;
        }
        final List<StructurePiece> list2 = (List<StructurePiece>)Lists.newArrayList();
        if (b.generate(cjp, integer, a, ew, list2, random)) {
            boolean boolean9 = false;
            final int integer2 = random.nextInt();
            for (final StructurePiece civ12 : list2) {
                civ12.genDepth = integer2;
                final StructurePiece civ13 = StructurePiece.findCollisionPiece(list, civ12.getBoundingBox());
                if (civ13 != null && civ13.genDepth != a.genDepth) {
                    boolean9 = true;
                    break;
                }
            }
            if (!boolean9) {
                list.addAll((Collection)list2);
                return true;
            }
        }
        return false;
    }
    
    static {
        OVERWRITE = new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        INSERT = new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        HOUSE_TOWER_GENERATOR = new SectionGenerator() {
            public void init() {
            }
            
            public boolean generate(final StructureManager cjp, final int integer, final EndCityPiece a, final BlockPos ew, final List<StructurePiece> list, final Random random) {
                if (integer > 8) {
                    return false;
                }
                final Rotation brg8 = a.placeSettings.getRotation();
                EndCityPiece a2 = addHelper(list, addPiece(cjp, a, ew, "base_floor", brg8, true));
                final int integer2 = random.nextInt(3);
                if (integer2 == 0) {
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 4, -1), "base_roof", brg8, true));
                }
                else if (integer2 == 1) {
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 0, -1), "second_floor_2", brg8, false));
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 8, -1), "second_roof", brg8, false));
                    recursiveChildren(cjp, EndCityPieces.TOWER_GENERATOR, integer + 1, a2, null, list, random);
                }
                else if (integer2 == 2) {
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 0, -1), "second_floor_2", brg8, false));
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 4, -1), "third_floor_2", brg8, false));
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 8, -1), "third_roof", brg8, true));
                    recursiveChildren(cjp, EndCityPieces.TOWER_GENERATOR, integer + 1, a2, null, list, random);
                }
                return true;
            }
        };
        TOWER_BRIDGES = (List)Lists.newArrayList((Object[])new Tuple[] { new Tuple((A)Rotation.NONE, (B)new BlockPos(1, -1, 0)), new Tuple((A)Rotation.CLOCKWISE_90, (B)new BlockPos(6, -1, 1)), new Tuple((A)Rotation.COUNTERCLOCKWISE_90, (B)new BlockPos(0, -1, 5)), new Tuple((A)Rotation.CLOCKWISE_180, (B)new BlockPos(5, -1, 6)) });
        TOWER_GENERATOR = new SectionGenerator() {
            public void init() {
            }
            
            public boolean generate(final StructureManager cjp, final int integer, final EndCityPiece a, final BlockPos ew, final List<StructurePiece> list, final Random random) {
                final Rotation brg8 = a.placeSettings.getRotation();
                EndCityPiece a2 = a;
                a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", brg8, true));
                a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, 7, 0), "tower_piece", brg8, true));
                EndCityPiece a3 = (random.nextInt(3) == 0) ? a2 : null;
                for (int integer2 = 1 + random.nextInt(3), integer3 = 0; integer3 < integer2; ++integer3) {
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, 4, 0), "tower_piece", brg8, true));
                    if (integer3 < integer2 - 1 && random.nextBoolean()) {
                        a3 = a2;
                    }
                }
                if (a3 != null) {
                    for (final Tuple<Rotation, BlockPos> aaf13 : EndCityPieces.TOWER_BRIDGES) {
                        if (random.nextBoolean()) {
                            final EndCityPiece a4 = addHelper(list, addPiece(cjp, a3, (BlockPos)aaf13.getB(), "bridge_end", brg8.getRotated(aaf13.getA()), true));
                            recursiveChildren(cjp, EndCityPieces.TOWER_BRIDGE_GENERATOR, integer + 1, a4, null, list, random);
                        }
                    }
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 4, -1), "tower_top", brg8, true));
                }
                else {
                    if (integer != 7) {
                        return recursiveChildren(cjp, EndCityPieces.FAT_TOWER_GENERATOR, integer + 1, a2, null, list, random);
                    }
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-1, 4, -1), "tower_top", brg8, true));
                }
                return true;
            }
        };
        TOWER_BRIDGE_GENERATOR = new SectionGenerator() {
            public boolean shipCreated;
            
            public void init() {
                this.shipCreated = false;
            }
            
            public boolean generate(final StructureManager cjp, final int integer, final EndCityPiece a, final BlockPos ew, final List<StructurePiece> list, final Random random) {
                final Rotation brg8 = a.placeSettings.getRotation();
                final int integer2 = random.nextInt(4) + 1;
                EndCityPiece a2 = addHelper(list, addPiece(cjp, a, new BlockPos(0, 0, -4), "bridge_piece", brg8, true));
                a2.genDepth = -1;
                int integer3 = 0;
                for (int integer4 = 0; integer4 < integer2; ++integer4) {
                    if (random.nextBoolean()) {
                        a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, integer3, -4), "bridge_piece", brg8, true));
                        integer3 = 0;
                    }
                    else {
                        if (random.nextBoolean()) {
                            a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, integer3, -4), "bridge_steep_stairs", brg8, true));
                        }
                        else {
                            a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, integer3, -8), "bridge_gentle_stairs", brg8, true));
                        }
                        integer3 = 4;
                    }
                }
                if (this.shipCreated || random.nextInt(10 - integer) != 0) {
                    if (!recursiveChildren(cjp, EndCityPieces.HOUSE_TOWER_GENERATOR, integer + 1, a2, new BlockPos(-3, integer3 + 1, -11), list, random)) {
                        return false;
                    }
                }
                else {
                    addHelper(list, addPiece(cjp, a2, new BlockPos(-8 + random.nextInt(8), integer3, -70 + random.nextInt(10)), "ship", brg8, true));
                    this.shipCreated = true;
                }
                a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(4, integer3, 0), "bridge_end", brg8.getRotated(Rotation.CLOCKWISE_180), true));
                a2.genDepth = -1;
                return true;
            }
        };
        FAT_TOWER_BRIDGES = (List)Lists.newArrayList((Object[])new Tuple[] { new Tuple((A)Rotation.NONE, (B)new BlockPos(4, -1, 0)), new Tuple((A)Rotation.CLOCKWISE_90, (B)new BlockPos(12, -1, 4)), new Tuple((A)Rotation.COUNTERCLOCKWISE_90, (B)new BlockPos(0, -1, 8)), new Tuple((A)Rotation.CLOCKWISE_180, (B)new BlockPos(8, -1, 12)) });
        FAT_TOWER_GENERATOR = new SectionGenerator() {
            public void init() {
            }
            
            public boolean generate(final StructureManager cjp, final int integer, final EndCityPiece a, final BlockPos ew, final List<StructurePiece> list, final Random random) {
                final Rotation brg9 = a.placeSettings.getRotation();
                EndCityPiece a2 = addHelper(list, addPiece(cjp, a, new BlockPos(-3, 4, -3), "fat_tower_base", brg9, true));
                a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, 4, 0), "fat_tower_middle", brg9, true));
                for (int integer2 = 0; integer2 < 2 && random.nextInt(3) != 0; ++integer2) {
                    a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(0, 8, 0), "fat_tower_middle", brg9, true));
                    for (final Tuple<Rotation, BlockPos> aaf12 : EndCityPieces.FAT_TOWER_BRIDGES) {
                        if (random.nextBoolean()) {
                            final EndCityPiece a3 = addHelper(list, addPiece(cjp, a2, (BlockPos)aaf12.getB(), "bridge_end", brg9.getRotated(aaf12.getA()), true));
                            recursiveChildren(cjp, EndCityPieces.TOWER_BRIDGE_GENERATOR, integer + 1, a3, null, list, random);
                        }
                    }
                }
                a2 = addHelper(list, addPiece(cjp, a2, new BlockPos(-2, 8, -2), "fat_tower_top", brg9, true));
                return true;
            }
        };
    }
    
    public static class EndCityPiece extends TemplateStructurePiece {
        private final String templateName;
        private final Rotation rotation;
        private final boolean overwrite;
        
        public EndCityPiece(final StructureManager cjp, final String string, final BlockPos ew, final Rotation brg, final boolean boolean5) {
            super(StructurePieceType.END_CITY_PIECE, 0);
            this.templateName = string;
            this.templatePosition = ew;
            this.rotation = brg;
            this.overwrite = boolean5;
            this.loadTemplate(cjp);
        }
        
        public EndCityPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.END_CITY_PIECE, id);
            this.templateName = id.getString("Template");
            this.rotation = Rotation.valueOf(id.getString("Rot"));
            this.overwrite = id.getBoolean("OW");
            this.loadTemplate(cjp);
        }
        
        private void loadTemplate(final StructureManager cjp) {
            final StructureTemplate cjt3 = cjp.getOrCreate(new ResourceLocation("end_city/" + this.templateName));
            final StructurePlaceSettings cjq4 = (this.overwrite ? EndCityPieces.OVERWRITE : EndCityPieces.INSERT).copy().setRotation(this.rotation);
            this.setup(cjt3, this.templatePosition, cjq4);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putString("Template", this.templateName);
            id.putString("Rot", this.rotation.name());
            id.putBoolean("OW", this.overwrite);
        }
        
        @Override
        protected void handleDataMarker(final String string, final BlockPos ew, final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (string.startsWith("Chest")) {
                final BlockPos ew2 = ew.below();
                if (cic.isInside(ew2)) {
                    RandomizableContainerBlockEntity.setLootTable(bhs, random, ew2, BuiltInLootTables.END_CITY_TREASURE);
                }
            }
            else if (string.startsWith("Sentry")) {
                final Shulker avb7 = EntityType.SHULKER.create(bhs.getLevel());
                avb7.setPos(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5);
                avb7.setAttachPosition(ew);
                bhs.addFreshEntity(avb7);
            }
            else if (string.startsWith("Elytra")) {
                final ItemFrame atn7 = new ItemFrame(bhs.getLevel(), ew, this.rotation.rotate(Direction.SOUTH));
                atn7.setItem(new ItemStack(Items.ELYTRA), false);
                bhs.addFreshEntity(atn7);
            }
        }
    }
    
    interface SectionGenerator {
        void init();
        
        boolean generate(final StructureManager cjp, final int integer, final EndCityPiece a, final BlockPos ew, final List<StructurePiece> list, final Random random);
    }
}
