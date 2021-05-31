package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.ChunkPos;
import java.util.Collections;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.Random;
import java.util.List;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.block.Block;
import java.util.Set;
import net.minecraft.world.level.block.state.BlockState;

public class OceanMonumentPieces {
    public abstract static class OceanMonumentPiece extends StructurePiece {
        protected static final BlockState BASE_GRAY;
        protected static final BlockState BASE_LIGHT;
        protected static final BlockState BASE_BLACK;
        protected static final BlockState DOT_DECO_DATA;
        protected static final BlockState LAMP_BLOCK;
        protected static final BlockState FILL_BLOCK;
        protected static final Set<Block> FILL_KEEP;
        protected static final int GRIDROOM_SOURCE_INDEX;
        protected static final int GRIDROOM_TOP_CONNECT_INDEX;
        protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX;
        protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX;
        protected RoomDefinition roomDefinition;
        
        protected static final int getRoomIndex(final int integer1, final int integer2, final int integer3) {
            return integer2 * 25 + integer3 * 5 + integer1;
        }
        
        public OceanMonumentPiece(final StructurePieceType cev, final int integer) {
            super(cev, integer);
        }
        
        public OceanMonumentPiece(final StructurePieceType cev, final Direction fb, final BoundingBox cic) {
            super(cev, 1);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        protected OceanMonumentPiece(final StructurePieceType cev, final int integer2, final Direction fb, final RoomDefinition v, final int integer5, final int integer6, final int integer7) {
            super(cev, integer2);
            this.setOrientation(fb);
            this.roomDefinition = v;
            final int integer8 = v.index;
            final int integer9 = integer8 % 5;
            final int integer10 = integer8 / 5 % 5;
            final int integer11 = integer8 / 25;
            if (fb == Direction.NORTH || fb == Direction.SOUTH) {
                this.boundingBox = new BoundingBox(0, 0, 0, integer5 * 8 - 1, integer6 * 4 - 1, integer7 * 8 - 1);
            }
            else {
                this.boundingBox = new BoundingBox(0, 0, 0, integer7 * 8 - 1, integer6 * 4 - 1, integer5 * 8 - 1);
            }
            switch (fb) {
                case NORTH: {
                    this.boundingBox.move(integer9 * 8, integer11 * 4, -(integer10 + integer7) * 8 + 1);
                    break;
                }
                case SOUTH: {
                    this.boundingBox.move(integer9 * 8, integer11 * 4, integer10 * 8);
                    break;
                }
                case WEST: {
                    this.boundingBox.move(-(integer10 + integer7) * 8 + 1, integer11 * 4, integer9 * 8);
                    break;
                }
                default: {
                    this.boundingBox.move(integer10 * 8, integer11 * 4, integer9 * 8);
                    break;
                }
            }
        }
        
        public OceanMonumentPiece(final StructurePieceType cev, final CompoundTag id) {
            super(cev, id);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
        }
        
        protected void generateWaterBox(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
            for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
                for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                    for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                        final BlockState bvt13 = this.getBlock(bhs, integer10, integer9, integer11, cic);
                        if (!OceanMonumentPiece.FILL_KEEP.contains(bvt13.getBlock())) {
                            if (this.getWorldY(integer9) >= bhs.getSeaLevel() && bvt13 != OceanMonumentPiece.FILL_BLOCK) {
                                this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), integer10, integer9, integer11, cic);
                            }
                            else {
                                this.placeBlock(bhs, OceanMonumentPiece.FILL_BLOCK, integer10, integer9, integer11, cic);
                            }
                        }
                    }
                }
            }
        }
        
        protected void generateDefaultFloor(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final boolean boolean5) {
            if (boolean5) {
                this.generateBox(bhs, cic, integer3 + 0, 0, integer4 + 0, integer3 + 2, 0, integer4 + 8 - 1, OceanMonumentPiece.BASE_GRAY, OceanMonumentPiece.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer3 + 5, 0, integer4 + 0, integer3 + 8 - 1, 0, integer4 + 8 - 1, OceanMonumentPiece.BASE_GRAY, OceanMonumentPiece.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer3 + 3, 0, integer4 + 0, integer3 + 4, 0, integer4 + 2, OceanMonumentPiece.BASE_GRAY, OceanMonumentPiece.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer3 + 3, 0, integer4 + 5, integer3 + 4, 0, integer4 + 8 - 1, OceanMonumentPiece.BASE_GRAY, OceanMonumentPiece.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer3 + 3, 0, integer4 + 2, integer3 + 4, 0, integer4 + 2, OceanMonumentPiece.BASE_LIGHT, OceanMonumentPiece.BASE_LIGHT, false);
                this.generateBox(bhs, cic, integer3 + 3, 0, integer4 + 5, integer3 + 4, 0, integer4 + 5, OceanMonumentPiece.BASE_LIGHT, OceanMonumentPiece.BASE_LIGHT, false);
                this.generateBox(bhs, cic, integer3 + 2, 0, integer4 + 3, integer3 + 2, 0, integer4 + 4, OceanMonumentPiece.BASE_LIGHT, OceanMonumentPiece.BASE_LIGHT, false);
                this.generateBox(bhs, cic, integer3 + 5, 0, integer4 + 3, integer3 + 5, 0, integer4 + 4, OceanMonumentPiece.BASE_LIGHT, OceanMonumentPiece.BASE_LIGHT, false);
            }
            else {
                this.generateBox(bhs, cic, integer3 + 0, 0, integer4 + 0, integer3 + 8 - 1, 0, integer4 + 8 - 1, OceanMonumentPiece.BASE_GRAY, OceanMonumentPiece.BASE_GRAY, false);
            }
        }
        
        protected void generateBoxOnFillOnly(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final BlockState bvt) {
            for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
                for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                    for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                        if (this.getBlock(bhs, integer10, integer9, integer11, cic) == OceanMonumentPiece.FILL_BLOCK) {
                            this.placeBlock(bhs, bvt, integer10, integer9, integer11, cic);
                        }
                    }
                }
            }
        }
        
        protected boolean chunkIntersects(final BoundingBox cic, final int integer2, final int integer3, final int integer4, final int integer5) {
            final int integer6 = this.getWorldX(integer2, integer3);
            final int integer7 = this.getWorldZ(integer2, integer3);
            final int integer8 = this.getWorldX(integer4, integer5);
            final int integer9 = this.getWorldZ(integer4, integer5);
            return cic.intersects(Math.min(integer6, integer8), Math.min(integer7, integer9), Math.max(integer6, integer8), Math.max(integer7, integer9));
        }
        
        protected boolean spawnElder(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5) {
            final int integer6 = this.getWorldX(integer3, integer5);
            final int integer7 = this.getWorldY(integer4);
            final int integer8 = this.getWorldZ(integer3, integer5);
            if (cic.isInside(new BlockPos(integer6, integer7, integer8))) {
                final ElderGuardian auh10 = EntityType.ELDER_GUARDIAN.create(bhs.getLevel());
                auh10.heal(auh10.getMaxHealth());
                auh10.moveTo(integer6 + 0.5, integer7, integer8 + 0.5, 0.0f, 0.0f);
                auh10.finalizeSpawn(bhs, bhs.getCurrentDifficultyAt(new BlockPos(auh10)), MobSpawnType.STRUCTURE, null, null);
                bhs.addFreshEntity(auh10);
                return true;
            }
            return false;
        }
        
        static {
            BASE_GRAY = Blocks.PRISMARINE.defaultBlockState();
            BASE_LIGHT = Blocks.PRISMARINE_BRICKS.defaultBlockState();
            BASE_BLACK = Blocks.DARK_PRISMARINE.defaultBlockState();
            DOT_DECO_DATA = OceanMonumentPiece.BASE_LIGHT;
            LAMP_BLOCK = Blocks.SEA_LANTERN.defaultBlockState();
            FILL_BLOCK = Blocks.WATER.defaultBlockState();
            FILL_KEEP = (Set)ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(OceanMonumentPiece.FILL_BLOCK.getBlock()).build();
            GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
            GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
            GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
            GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
        }
    }
    
    public static class MonumentBuilding extends OceanMonumentPiece {
        private RoomDefinition sourceRoom;
        private RoomDefinition coreRoom;
        private final List<OceanMonumentPiece> childPieces;
        
        public MonumentBuilding(final Random random, final int integer2, final int integer3, final Direction fb) {
            super(StructurePieceType.OCEAN_MONUMENT_BUILDING, 0);
            this.childPieces = (List<OceanMonumentPiece>)Lists.newArrayList();
            this.setOrientation(fb);
            final Direction fb2 = this.getOrientation();
            if (fb2.getAxis() == Direction.Axis.Z) {
                this.boundingBox = new BoundingBox(integer2, 39, integer3, integer2 + 58 - 1, 61, integer3 + 58 - 1);
            }
            else {
                this.boundingBox = new BoundingBox(integer2, 39, integer3, integer2 + 58 - 1, 61, integer3 + 58 - 1);
            }
            final List<RoomDefinition> list7 = this.generateRoomGraph(random);
            this.sourceRoom.claimed = true;
            this.childPieces.add(new OceanMonumentEntryRoom(fb2, this.sourceRoom));
            this.childPieces.add(new OceanMonumentCoreRoom(fb2, this.coreRoom));
            final List<MonumentRoomFitter> list8 = (List<MonumentRoomFitter>)Lists.newArrayList();
            list8.add(new FitDoubleXYRoom());
            list8.add(new FitDoubleYZRoom());
            list8.add(new FitDoubleZRoom());
            list8.add(new FitDoubleXRoom());
            list8.add(new FitDoubleYRoom());
            list8.add(new FitSimpleTopRoom());
            list8.add(new FitSimpleRoom());
            for (final RoomDefinition v10 : list7) {
                if (!v10.claimed && !v10.isSpecial()) {
                    for (final MonumentRoomFitter i12 : list8) {
                        if (i12.fits(v10)) {
                            this.childPieces.add(i12.create(fb2, v10, random));
                            break;
                        }
                    }
                }
            }
            final int integer4 = this.boundingBox.y0;
            final int integer5 = this.getWorldX(9, 22);
            final int integer6 = this.getWorldZ(9, 22);
            for (final OceanMonumentPiece r13 : this.childPieces) {
                r13.getBoundingBox().move(integer5, integer4, integer6);
            }
            final BoundingBox cic12 = BoundingBox.createProper(this.getWorldX(1, 1), this.getWorldY(1), this.getWorldZ(1, 1), this.getWorldX(23, 21), this.getWorldY(8), this.getWorldZ(23, 21));
            final BoundingBox cic13 = BoundingBox.createProper(this.getWorldX(34, 1), this.getWorldY(1), this.getWorldZ(34, 1), this.getWorldX(56, 21), this.getWorldY(8), this.getWorldZ(56, 21));
            final BoundingBox cic14 = BoundingBox.createProper(this.getWorldX(22, 22), this.getWorldY(13), this.getWorldZ(22, 22), this.getWorldX(35, 35), this.getWorldY(17), this.getWorldZ(35, 35));
            int integer7 = random.nextInt();
            this.childPieces.add(new OceanMonumentWingRoom(fb2, cic12, integer7++));
            this.childPieces.add(new OceanMonumentWingRoom(fb2, cic13, integer7++));
            this.childPieces.add(new OceanMonumentPenthouse(fb2, cic14));
        }
        
        public MonumentBuilding(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_BUILDING, id);
            this.childPieces = (List<OceanMonumentPiece>)Lists.newArrayList();
        }
        
        private List<RoomDefinition> generateRoomGraph(final Random random) {
            final RoomDefinition[] arr3 = new RoomDefinition[75];
            for (int integer4 = 0; integer4 < 5; ++integer4) {
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    final int integer6 = 0;
                    final int integer7 = OceanMonumentPiece.getRoomIndex(integer4, 0, integer5);
                    arr3[integer7] = new RoomDefinition(integer7);
                }
            }
            for (int integer4 = 0; integer4 < 5; ++integer4) {
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    final int integer6 = 1;
                    final int integer7 = OceanMonumentPiece.getRoomIndex(integer4, 1, integer5);
                    arr3[integer7] = new RoomDefinition(integer7);
                }
            }
            for (int integer4 = 1; integer4 < 4; ++integer4) {
                for (int integer5 = 0; integer5 < 2; ++integer5) {
                    final int integer6 = 2;
                    final int integer7 = OceanMonumentPiece.getRoomIndex(integer4, 2, integer5);
                    arr3[integer7] = new RoomDefinition(integer7);
                }
            }
            this.sourceRoom = arr3[MonumentBuilding.GRIDROOM_SOURCE_INDEX];
            for (int integer4 = 0; integer4 < 5; ++integer4) {
                for (int integer5 = 0; integer5 < 5; ++integer5) {
                    for (int integer6 = 0; integer6 < 3; ++integer6) {
                        final int integer7 = OceanMonumentPiece.getRoomIndex(integer4, integer6, integer5);
                        if (arr3[integer7] != null) {
                            for (final Direction fb11 : Direction.values()) {
                                final int integer8 = integer4 + fb11.getStepX();
                                final int integer9 = integer6 + fb11.getStepY();
                                final int integer10 = integer5 + fb11.getStepZ();
                                if (integer8 >= 0 && integer8 < 5 && integer10 >= 0 && integer10 < 5 && integer9 >= 0 && integer9 < 3) {
                                    final int integer11 = OceanMonumentPiece.getRoomIndex(integer8, integer9, integer10);
                                    if (arr3[integer11] != null) {
                                        if (integer10 == integer5) {
                                            arr3[integer7].setConnection(fb11, arr3[integer11]);
                                        }
                                        else {
                                            arr3[integer7].setConnection(fb11.getOpposite(), arr3[integer11]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final RoomDefinition v4 = new RoomDefinition(1003);
            final RoomDefinition v5 = new RoomDefinition(1001);
            final RoomDefinition v6 = new RoomDefinition(1002);
            arr3[MonumentBuilding.GRIDROOM_TOP_CONNECT_INDEX].setConnection(Direction.UP, v4);
            arr3[MonumentBuilding.GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, v5);
            arr3[MonumentBuilding.GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, v6);
            v4.claimed = true;
            v5.claimed = true;
            v6.claimed = true;
            this.sourceRoom.isSource = true;
            (this.coreRoom = arr3[OceanMonumentPiece.getRoomIndex(random.nextInt(4), 0, 2)]).claimed = true;
            this.coreRoom.connections[Direction.EAST.get3DDataValue()].claimed = true;
            this.coreRoom.connections[Direction.NORTH.get3DDataValue()].claimed = true;
            this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].claimed = true;
            this.coreRoom.connections[Direction.UP.get3DDataValue()].claimed = true;
            this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
            this.coreRoom.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
            this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
            final List<RoomDefinition> list7 = (List<RoomDefinition>)Lists.newArrayList();
            for (final RoomDefinition v7 : arr3) {
                if (v7 != null) {
                    v7.updateOpenings();
                    list7.add(v7);
                }
            }
            v4.updateOpenings();
            Collections.shuffle((List)list7, random);
            int integer12 = 1;
            for (final RoomDefinition v8 : list7) {
                int integer13 = 0;
                int integer8 = 0;
                while (integer13 < 2 && integer8 < 5) {
                    ++integer8;
                    final int integer9 = random.nextInt(6);
                    if (v8.hasOpening[integer9]) {
                        final int integer10 = Direction.from3DDataValue(integer9).getOpposite().get3DDataValue();
                        v8.hasOpening[integer9] = false;
                        v8.connections[integer9].hasOpening[integer10] = false;
                        if (v8.findSource(integer12++) && v8.connections[integer9].findSource(integer12++)) {
                            ++integer13;
                        }
                        else {
                            v8.hasOpening[integer9] = true;
                            v8.connections[integer9].hasOpening[integer10] = true;
                        }
                    }
                }
            }
            list7.add(v4);
            list7.add(v5);
            list7.add(v6);
            return list7;
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final int integer6 = Math.max(bhs.getSeaLevel(), 64) - this.boundingBox.y0;
            this.generateWaterBox(bhs, cic, 0, 0, 0, 58, integer6, 58);
            this.generateWing(false, 0, bhs, random, cic);
            this.generateWing(true, 33, bhs, random, cic);
            this.generateEntranceArchs(bhs, random, cic);
            this.generateEntranceWall(bhs, random, cic);
            this.generateRoofPiece(bhs, random, cic);
            this.generateLowerWall(bhs, random, cic);
            this.generateMiddleWall(bhs, random, cic);
            this.generateUpperWall(bhs, random, cic);
            for (int integer7 = 0; integer7 < 7; ++integer7) {
                int integer8 = 0;
                while (integer8 < 7) {
                    if (integer8 == 0 && integer7 == 3) {
                        integer8 = 6;
                    }
                    final int integer9 = integer7 * 9;
                    final int integer10 = integer8 * 9;
                    for (int integer11 = 0; integer11 < 4; ++integer11) {
                        for (int integer12 = 0; integer12 < 4; ++integer12) {
                            this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, integer9 + integer11, 0, integer10 + integer12, cic);
                            this.fillColumnDown(bhs, MonumentBuilding.BASE_LIGHT, integer9 + integer11, -1, integer10 + integer12, cic);
                        }
                    }
                    if (integer7 == 0 || integer7 == 6) {
                        ++integer8;
                    }
                    else {
                        integer8 += 6;
                    }
                }
            }
            for (int integer7 = 0; integer7 < 5; ++integer7) {
                this.generateWaterBox(bhs, cic, -1 - integer7, 0 + integer7 * 2, -1 - integer7, -1 - integer7, 23, 58 + integer7);
                this.generateWaterBox(bhs, cic, 58 + integer7, 0 + integer7 * 2, -1 - integer7, 58 + integer7, 23, 58 + integer7);
                this.generateWaterBox(bhs, cic, 0 - integer7, 0 + integer7 * 2, -1 - integer7, 57 + integer7, 23, -1 - integer7);
                this.generateWaterBox(bhs, cic, 0 - integer7, 0 + integer7 * 2, 58 + integer7, 57 + integer7, 23, 58 + integer7);
            }
            for (final OceanMonumentPiece r8 : this.childPieces) {
                if (r8.getBoundingBox().intersects(cic)) {
                    r8.postProcess(bhs, random, cic, bhd);
                }
            }
            return true;
        }
        
        private void generateWing(final boolean boolean1, final int integer, final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            final int integer2 = 24;
            if (this.chunkIntersects(cic, integer, 0, integer + 23, 20)) {
                this.generateBox(bhs, cic, integer + 0, 0, 0, integer + 24, 0, 20, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, integer + 0, 1, 0, integer + 24, 10, 20);
                for (int integer3 = 0; integer3 < 4; ++integer3) {
                    this.generateBox(bhs, cic, integer + integer3, integer3 + 1, integer3, integer + integer3, integer3 + 1, 20, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer + integer3 + 7, integer3 + 5, integer3 + 7, integer + integer3 + 7, integer3 + 5, 20, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer + 17 - integer3, integer3 + 5, integer3 + 7, integer + 17 - integer3, integer3 + 5, 20, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer + 24 - integer3, integer3 + 1, integer3, integer + 24 - integer3, integer3 + 1, 20, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer + integer3 + 1, integer3 + 1, integer3, integer + 23 - integer3, integer3 + 1, integer3, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer + integer3 + 8, integer3 + 5, integer3 + 7, integer + 16 - integer3, integer3 + 5, integer3 + 7, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                this.generateBox(bhs, cic, integer + 4, 4, 4, integer + 6, 4, 20, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer + 7, 4, 4, integer + 17, 4, 6, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer + 18, 4, 4, integer + 20, 4, 20, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer + 11, 8, 11, integer + 13, 8, 20, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer + 12, 9, 12, cic);
                this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer + 12, 9, 15, cic);
                this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer + 12, 9, 18, cic);
                int integer3 = integer + (boolean1 ? 19 : 5);
                final int integer4 = integer + (boolean1 ? 5 : 19);
                for (int integer5 = 20; integer5 >= 5; integer5 -= 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer3, 5, integer5, cic);
                }
                for (int integer5 = 19; integer5 >= 7; integer5 -= 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer4, 5, integer5, cic);
                }
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    final int integer6 = boolean1 ? (integer + 24 - (17 - integer5 * 3)) : (integer + 17 - integer5 * 3);
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer6, 5, 5, cic);
                }
                this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer4, 5, 5, cic);
                this.generateBox(bhs, cic, integer + 11, 1, 12, integer + 13, 7, 12, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, integer + 12, 1, 11, integer + 12, 7, 13, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
            }
        }
        
        private void generateEntranceArchs(final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (this.chunkIntersects(cic, 22, 5, 35, 17)) {
                this.generateWaterBox(bhs, cic, 25, 0, 0, 32, 8, 20);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, 24, 2, 5 + integer5 * 4, 24, 4, 5 + integer5 * 4, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 22, 4, 5 + integer5 * 4, 23, 4, 5 + integer5 * 4, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 25, 5, 5 + integer5 * 4, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 26, 6, 5 + integer5 * 4, cic);
                    this.placeBlock(bhs, MonumentBuilding.LAMP_BLOCK, 26, 5, 5 + integer5 * 4, cic);
                    this.generateBox(bhs, cic, 33, 2, 5 + integer5 * 4, 33, 4, 5 + integer5 * 4, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 34, 4, 5 + integer5 * 4, 35, 4, 5 + integer5 * 4, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 32, 5, 5 + integer5 * 4, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 31, 6, 5 + integer5 * 4, cic);
                    this.placeBlock(bhs, MonumentBuilding.LAMP_BLOCK, 31, 5, 5 + integer5 * 4, cic);
                    this.generateBox(bhs, cic, 27, 6, 5 + integer5 * 4, 30, 6, 5 + integer5 * 4, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                }
            }
        }
        
        private void generateEntranceWall(final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (this.chunkIntersects(cic, 15, 20, 42, 21)) {
                this.generateBox(bhs, cic, 15, 0, 21, 42, 0, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 26, 1, 21, 31, 3, 21);
                this.generateBox(bhs, cic, 21, 12, 21, 36, 12, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 17, 11, 21, 40, 11, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 16, 10, 21, 41, 10, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 15, 7, 21, 42, 9, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 16, 6, 21, 41, 6, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 17, 5, 21, 40, 5, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 21, 4, 21, 36, 4, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 22, 3, 21, 26, 3, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 31, 3, 21, 35, 3, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 23, 2, 21, 25, 2, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 32, 2, 21, 34, 2, 21, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 28, 4, 20, 29, 4, 21, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 27, 3, 21, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 30, 3, 21, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 26, 2, 21, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 31, 2, 21, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 25, 1, 21, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 32, 1, 21, cic);
                for (int integer5 = 0; integer5 < 7; ++integer5) {
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 28 - integer5, 6 + integer5, 21, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 29 + integer5, 6 + integer5, 21, cic);
                }
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 28 - integer5, 9 + integer5, 21, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 29 + integer5, 9 + integer5, 21, cic);
                }
                this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 28, 12, 21, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 29, 12, 21, cic);
                for (int integer5 = 0; integer5 < 3; ++integer5) {
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 22 - integer5 * 2, 8, 21, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 22 - integer5 * 2, 9, 21, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 35 + integer5 * 2, 8, 21, cic);
                    this.placeBlock(bhs, MonumentBuilding.BASE_BLACK, 35 + integer5 * 2, 9, 21, cic);
                }
                this.generateWaterBox(bhs, cic, 15, 13, 21, 42, 15, 21);
                this.generateWaterBox(bhs, cic, 15, 1, 21, 15, 6, 21);
                this.generateWaterBox(bhs, cic, 16, 1, 21, 16, 5, 21);
                this.generateWaterBox(bhs, cic, 17, 1, 21, 20, 4, 21);
                this.generateWaterBox(bhs, cic, 21, 1, 21, 21, 3, 21);
                this.generateWaterBox(bhs, cic, 22, 1, 21, 22, 2, 21);
                this.generateWaterBox(bhs, cic, 23, 1, 21, 24, 1, 21);
                this.generateWaterBox(bhs, cic, 42, 1, 21, 42, 6, 21);
                this.generateWaterBox(bhs, cic, 41, 1, 21, 41, 5, 21);
                this.generateWaterBox(bhs, cic, 37, 1, 21, 40, 4, 21);
                this.generateWaterBox(bhs, cic, 36, 1, 21, 36, 3, 21);
                this.generateWaterBox(bhs, cic, 33, 1, 21, 34, 1, 21);
                this.generateWaterBox(bhs, cic, 35, 1, 21, 35, 2, 21);
            }
        }
        
        private void generateRoofPiece(final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (this.chunkIntersects(cic, 21, 21, 36, 36)) {
                this.generateBox(bhs, cic, 21, 0, 22, 36, 0, 36, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 21, 1, 22, 36, 23, 36);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, 21 + integer5, 13 + integer5, 21 + integer5, 36 - integer5, 13 + integer5, 21 + integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 21 + integer5, 13 + integer5, 36 - integer5, 36 - integer5, 13 + integer5, 36 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 21 + integer5, 13 + integer5, 22 + integer5, 21 + integer5, 13 + integer5, 35 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 36 - integer5, 13 + integer5, 22 + integer5, 36 - integer5, 13 + integer5, 35 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                this.generateBox(bhs, cic, 25, 16, 25, 32, 16, 32, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 25, 17, 25, 25, 19, 25, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 32, 17, 25, 32, 19, 25, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 25, 17, 32, 25, 19, 32, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 32, 17, 32, 32, 19, 32, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 26, 20, 26, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 27, 21, 27, cic);
                this.placeBlock(bhs, MonumentBuilding.LAMP_BLOCK, 27, 20, 27, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 26, 20, 31, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 27, 21, 30, cic);
                this.placeBlock(bhs, MonumentBuilding.LAMP_BLOCK, 27, 20, 30, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 31, 20, 31, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 30, 21, 30, cic);
                this.placeBlock(bhs, MonumentBuilding.LAMP_BLOCK, 30, 20, 30, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 31, 20, 26, cic);
                this.placeBlock(bhs, MonumentBuilding.BASE_LIGHT, 30, 21, 27, cic);
                this.placeBlock(bhs, MonumentBuilding.LAMP_BLOCK, 30, 20, 27, cic);
                this.generateBox(bhs, cic, 28, 21, 27, 29, 21, 27, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 27, 21, 28, 27, 21, 29, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 28, 21, 30, 29, 21, 30, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 30, 21, 28, 30, 21, 29, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
            }
        }
        
        private void generateLowerWall(final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (this.chunkIntersects(cic, 0, 21, 6, 58)) {
                this.generateBox(bhs, cic, 0, 0, 21, 6, 0, 57, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 0, 1, 21, 6, 7, 57);
                this.generateBox(bhs, cic, 4, 4, 21, 6, 4, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, integer5, integer5 + 1, 21, integer5, integer5 + 1, 57 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 23; integer5 < 53; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 5, 5, integer5, cic);
                }
                this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 5, 5, 52, cic);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, integer5, integer5 + 1, 21, integer5, integer5 + 1, 57 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                this.generateBox(bhs, cic, 4, 1, 52, 6, 3, 52, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 5, 1, 51, 5, 3, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
            }
            if (this.chunkIntersects(cic, 51, 21, 58, 58)) {
                this.generateBox(bhs, cic, 51, 0, 21, 57, 0, 57, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 51, 1, 21, 57, 7, 57);
                this.generateBox(bhs, cic, 51, 4, 21, 53, 4, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, 57 - integer5, integer5 + 1, 21, 57 - integer5, integer5 + 1, 57 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 23; integer5 < 53; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 52, 5, integer5, cic);
                }
                this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 52, 5, 52, cic);
                this.generateBox(bhs, cic, 51, 1, 52, 53, 3, 52, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 52, 1, 51, 52, 3, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
            }
            if (this.chunkIntersects(cic, 0, 51, 57, 57)) {
                this.generateBox(bhs, cic, 7, 0, 51, 50, 0, 57, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 7, 1, 51, 50, 10, 57);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, integer5 + 1, integer5 + 1, 57 - integer5, 56 - integer5, integer5 + 1, 57 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
            }
        }
        
        private void generateMiddleWall(final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (this.chunkIntersects(cic, 7, 21, 13, 50)) {
                this.generateBox(bhs, cic, 7, 0, 21, 13, 0, 50, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 7, 1, 21, 13, 10, 50);
                this.generateBox(bhs, cic, 11, 8, 21, 13, 8, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, integer5 + 7, integer5 + 5, 21, integer5 + 7, integer5 + 5, 54, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 21; integer5 <= 45; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 12, 9, integer5, cic);
                }
            }
            if (this.chunkIntersects(cic, 44, 21, 50, 54)) {
                this.generateBox(bhs, cic, 44, 0, 21, 50, 0, 50, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 44, 1, 21, 50, 10, 50);
                this.generateBox(bhs, cic, 44, 8, 21, 46, 8, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, 50 - integer5, integer5 + 5, 21, 50 - integer5, integer5 + 5, 54, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 21; integer5 <= 45; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 45, 9, integer5, cic);
                }
            }
            if (this.chunkIntersects(cic, 8, 44, 49, 54)) {
                this.generateBox(bhs, cic, 14, 0, 44, 43, 0, 50, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 14, 1, 44, 43, 10, 50);
                for (int integer5 = 12; integer5 <= 45; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 9, 45, cic);
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 9, 52, cic);
                    if (integer5 == 12 || integer5 == 18 || integer5 == 24 || integer5 == 33 || integer5 == 39 || integer5 == 45) {
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 9, 47, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 9, 50, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 10, 45, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 10, 46, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 10, 51, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 10, 52, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 11, 47, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 11, 50, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 12, 48, cic);
                        this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 12, 49, cic);
                    }
                }
                for (int integer5 = 0; integer5 < 3; ++integer5) {
                    this.generateBox(bhs, cic, 8 + integer5, 5 + integer5, 54, 49 - integer5, 5 + integer5, 54, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                }
                this.generateBox(bhs, cic, 11, 8, 54, 46, 8, 54, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 14, 8, 44, 43, 8, 53, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
            }
        }
        
        private void generateUpperWall(final LevelAccessor bhs, final Random random, final BoundingBox cic) {
            if (this.chunkIntersects(cic, 14, 21, 20, 43)) {
                this.generateBox(bhs, cic, 14, 0, 21, 20, 0, 43, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 14, 1, 22, 20, 14, 43);
                this.generateBox(bhs, cic, 18, 12, 22, 20, 12, 39, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 18, 12, 21, 20, 12, 21, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, integer5 + 14, integer5 + 9, 21, integer5 + 14, integer5 + 9, 43 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 23; integer5 <= 39; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 19, 13, integer5, cic);
                }
            }
            if (this.chunkIntersects(cic, 37, 21, 43, 43)) {
                this.generateBox(bhs, cic, 37, 0, 21, 43, 0, 43, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 37, 1, 22, 43, 14, 43);
                this.generateBox(bhs, cic, 37, 12, 22, 39, 12, 39, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateBox(bhs, cic, 37, 12, 21, 39, 12, 21, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, 43 - integer5, integer5 + 9, 21, 43 - integer5, integer5 + 9, 43 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 23; integer5 <= 39; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, 38, 13, integer5, cic);
                }
            }
            if (this.chunkIntersects(cic, 15, 37, 42, 43)) {
                this.generateBox(bhs, cic, 21, 0, 37, 36, 0, 43, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                this.generateWaterBox(bhs, cic, 21, 1, 37, 36, 14, 43);
                this.generateBox(bhs, cic, 21, 12, 37, 36, 12, 39, MonumentBuilding.BASE_GRAY, MonumentBuilding.BASE_GRAY, false);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.generateBox(bhs, cic, 15 + integer5, integer5 + 9, 43 - integer5, 42 - integer5, integer5 + 9, 43 - integer5, MonumentBuilding.BASE_LIGHT, MonumentBuilding.BASE_LIGHT, false);
                }
                for (int integer5 = 21; integer5 <= 36; integer5 += 3) {
                    this.placeBlock(bhs, MonumentBuilding.DOT_DECO_DATA, integer5, 13, 38, cic);
                }
            }
        }
    }
    
    public static class OceanMonumentEntryRoom extends OceanMonumentPiece {
        public OceanMonumentEntryRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, fb, v, 1, 1, 1);
        }
        
        public OceanMonumentEntryRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 3, 0, 2, 3, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 3, 0, 7, 3, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 2, 0, 1, 2, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 2, 0, 7, 2, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 1, 0, 0, 1, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 7, 1, 0, 7, 1, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 1, 7, 7, 3, 7, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 0, 2, 3, 0, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 1, 0, 6, 3, 0, OceanMonumentEntryRoom.BASE_LIGHT, OceanMonumentEntryRoom.BASE_LIGHT, false);
            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 7, 4, 2, 7);
            }
            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 3, 1, 2, 4);
            }
            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 6, 1, 3, 7, 2, 4);
            }
            return true;
        }
    }
    
    public static class OceanMonumentSimpleRoom extends OceanMonumentPiece {
        private int mainDesign;
        
        public OceanMonumentSimpleRoom(final Direction fb, final RoomDefinition v, final Random random) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, fb, v, 1, 1, 1);
            this.mainDesign = random.nextInt(3);
        }
        
        public OceanMonumentSimpleRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 4, 1, 6, 4, 6, OceanMonumentSimpleRoom.BASE_GRAY);
            }
            final boolean boolean6 = this.mainDesign != 0 && random.nextBoolean() && !this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()] && !this.roomDefinition.hasOpening[Direction.UP.get3DDataValue()] && this.roomDefinition.countOpenings() > 1;
            if (this.mainDesign == 0) {
                this.generateBox(bhs, cic, 0, 1, 0, 2, 1, 2, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 3, 0, 2, 3, 2, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 2, 0, 0, 2, 2, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.generateBox(bhs, cic, 1, 2, 0, 2, 2, 0, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 1, 2, 1, cic);
                this.generateBox(bhs, cic, 5, 1, 0, 7, 1, 2, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 5, 3, 0, 7, 3, 2, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 2, 0, 7, 2, 2, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.generateBox(bhs, cic, 5, 2, 0, 6, 2, 0, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 6, 2, 1, cic);
                this.generateBox(bhs, cic, 0, 1, 5, 2, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 3, 5, 2, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 2, 5, 0, 2, 7, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.generateBox(bhs, cic, 1, 2, 7, 2, 2, 7, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 1, 2, 6, cic);
                this.generateBox(bhs, cic, 5, 1, 5, 7, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 5, 3, 5, 7, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 2, 5, 7, 2, 7, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.generateBox(bhs, cic, 5, 2, 7, 6, 2, 7, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 6, 2, 6, cic);
                if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 3, 3, 0, 4, 3, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, 3, 3, 0, 4, 3, 1, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 3, 2, 0, 4, 2, 0, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 3, 1, 0, 4, 1, 1, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 3, 3, 7, 4, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, 3, 3, 6, 4, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 3, 2, 7, 4, 2, 7, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 3, 1, 6, 4, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 0, 3, 3, 0, 3, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, 0, 3, 3, 1, 3, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 0, 2, 3, 0, 2, 4, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 0, 1, 3, 1, 1, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 7, 3, 3, 7, 3, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, 6, 3, 3, 7, 3, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 7, 2, 3, 7, 2, 4, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 6, 1, 3, 7, 1, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
            }
            else if (this.mainDesign == 1) {
                this.generateBox(bhs, cic, 2, 1, 2, 2, 3, 2, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 2, 1, 5, 2, 3, 5, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 5, 1, 5, 5, 3, 5, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 5, 1, 2, 5, 3, 2, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 2, 2, 2, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 2, 2, 5, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 5, 2, 5, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.LAMP_BLOCK, 5, 2, 2, cic);
                this.generateBox(bhs, cic, 0, 1, 0, 1, 3, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 1, 1, 0, 3, 1, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 1, 7, 1, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 1, 6, 0, 3, 6, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 1, 7, 7, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 1, 6, 7, 3, 6, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 1, 0, 7, 3, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 1, 1, 7, 3, 1, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 1, 2, 0, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 0, 2, 1, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 1, 2, 7, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 0, 2, 6, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 6, 2, 7, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 7, 2, 6, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 6, 2, 0, cic);
                this.placeBlock(bhs, OceanMonumentSimpleRoom.BASE_GRAY, 7, 2, 1, cic);
                if (!this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 1, 3, 0, 6, 3, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 1, 2, 0, 6, 2, 0, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 1, 1, 0, 6, 1, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                if (!this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 1, 3, 7, 6, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 1, 2, 7, 6, 2, 7, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 1, 1, 7, 6, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                if (!this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 0, 3, 1, 0, 3, 6, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 0, 2, 1, 0, 2, 6, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 0, 1, 1, 0, 1, 6, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
                if (!this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 7, 3, 1, 7, 3, 6, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 7, 2, 1, 7, 2, 6, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                    this.generateBox(bhs, cic, 7, 1, 1, 7, 1, 6, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                }
            }
            else if (this.mainDesign == 2) {
                this.generateBox(bhs, cic, 0, 1, 0, 0, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 1, 0, 7, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 1, 0, 6, 1, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 1, 7, 6, 1, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 2, 0, 0, 2, 7, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 7, 2, 0, 7, 2, 7, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 1, 2, 0, 6, 2, 0, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 1, 2, 7, 6, 2, 7, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 0, 3, 0, 0, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 3, 0, 7, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 3, 0, 6, 3, 0, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 3, 7, 6, 3, 7, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 0, 1, 3, 0, 2, 4, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 7, 1, 3, 7, 2, 4, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 3, 1, 0, 4, 2, 0, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 3, 1, 7, 4, 2, 7, OceanMonumentSimpleRoom.BASE_BLACK, OceanMonumentSimpleRoom.BASE_BLACK, false);
                if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                    this.generateWaterBox(bhs, cic, 3, 1, 0, 4, 2, 0);
                }
                if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
                    this.generateWaterBox(bhs, cic, 3, 1, 7, 4, 2, 7);
                }
                if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
                    this.generateWaterBox(bhs, cic, 0, 1, 3, 0, 2, 4);
                }
                if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
                    this.generateWaterBox(bhs, cic, 7, 1, 3, 7, 2, 4);
                }
            }
            if (boolean6) {
                this.generateBox(bhs, cic, 3, 1, 3, 4, 1, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 3, 2, 3, 4, 2, 4, OceanMonumentSimpleRoom.BASE_GRAY, OceanMonumentSimpleRoom.BASE_GRAY, false);
                this.generateBox(bhs, cic, 3, 3, 3, 4, 3, 4, OceanMonumentSimpleRoom.BASE_LIGHT, OceanMonumentSimpleRoom.BASE_LIGHT, false);
            }
            return true;
        }
    }
    
    public static class OceanMonumentSimpleTopRoom extends OceanMonumentPiece {
        public OceanMonumentSimpleTopRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, fb, v, 1, 1, 1);
        }
        
        public OceanMonumentSimpleTopRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 4, 1, 6, 4, 6, OceanMonumentSimpleTopRoom.BASE_GRAY);
            }
            for (int integer6 = 1; integer6 <= 6; ++integer6) {
                for (int integer7 = 1; integer7 <= 6; ++integer7) {
                    if (random.nextInt(3) != 0) {
                        final int integer8 = 2 + ((random.nextInt(4) != 0) ? 1 : 0);
                        final BlockState bvt9 = Blocks.WET_SPONGE.defaultBlockState();
                        this.generateBox(bhs, cic, integer6, integer8, integer7, integer6, 3, integer7, bvt9, bvt9, false);
                    }
                }
            }
            this.generateBox(bhs, cic, 0, 1, 0, 0, 1, 7, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 7, 1, 0, 7, 1, 7, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 0, 6, 1, 0, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 7, 6, 1, 7, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 2, 7, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 7, 2, 0, 7, 2, 7, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 1, 2, 0, 6, 2, 0, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 1, 2, 7, 6, 2, 7, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 0, 3, 0, 0, 3, 7, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 7, 3, 0, 7, 3, 7, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 0, 6, 3, 0, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 7, 6, 3, 7, OceanMonumentSimpleTopRoom.BASE_LIGHT, OceanMonumentSimpleTopRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 1, 3, 0, 2, 4, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 7, 1, 3, 7, 2, 4, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 3, 1, 0, 4, 2, 0, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 3, 1, 7, 4, 2, 7, OceanMonumentSimpleTopRoom.BASE_BLACK, OceanMonumentSimpleTopRoom.BASE_BLACK, false);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 0, 4, 2, 0);
            }
            return true;
        }
    }
    
    public static class OceanMonumentDoubleYRoom extends OceanMonumentPiece {
        public OceanMonumentDoubleYRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, fb, v, 1, 2, 1);
        }
        
        public OceanMonumentDoubleYRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            final RoomDefinition v6 = this.roomDefinition.connections[Direction.UP.get3DDataValue()];
            if (v6.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 8, 1, 6, 8, 6, OceanMonumentDoubleYRoom.BASE_GRAY);
            }
            this.generateBox(bhs, cic, 0, 4, 0, 0, 4, 7, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 7, 4, 0, 7, 4, 7, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 4, 0, 6, 4, 0, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 4, 7, 6, 4, 7, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 4, 1, 2, 4, 2, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 4, 2, 1, 4, 2, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 4, 1, 5, 4, 2, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 4, 2, 6, 4, 2, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 4, 5, 2, 4, 6, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 4, 5, 1, 4, 5, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 4, 5, 5, 4, 6, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 4, 5, 6, 4, 5, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
            RoomDefinition v7 = this.roomDefinition;
            for (int integer8 = 1; integer8 <= 5; integer8 += 4) {
                int integer9 = 0;
                if (v7.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 2, integer8, integer9, 2, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 5, integer8, integer9, 5, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 3, integer8 + 2, integer9, 4, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, 0, integer8, integer9, 7, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 0, integer8 + 1, integer9, 7, integer8 + 1, integer9, OceanMonumentDoubleYRoom.BASE_GRAY, OceanMonumentDoubleYRoom.BASE_GRAY, false);
                }
                integer9 = 7;
                if (v7.hasOpening[Direction.NORTH.get3DDataValue()]) {
                    this.generateBox(bhs, cic, 2, integer8, integer9, 2, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 5, integer8, integer9, 5, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 3, integer8 + 2, integer9, 4, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, 0, integer8, integer9, 7, integer8 + 2, integer9, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, 0, integer8 + 1, integer9, 7, integer8 + 1, integer9, OceanMonumentDoubleYRoom.BASE_GRAY, OceanMonumentDoubleYRoom.BASE_GRAY, false);
                }
                int integer10 = 0;
                if (v7.hasOpening[Direction.WEST.get3DDataValue()]) {
                    this.generateBox(bhs, cic, integer10, integer8, 2, integer10, integer8 + 2, 2, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer10, integer8, 5, integer10, integer8 + 2, 5, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer10, integer8 + 2, 3, integer10, integer8 + 2, 4, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, integer10, integer8, 0, integer10, integer8 + 2, 7, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer10, integer8 + 1, 0, integer10, integer8 + 1, 7, OceanMonumentDoubleYRoom.BASE_GRAY, OceanMonumentDoubleYRoom.BASE_GRAY, false);
                }
                integer10 = 7;
                if (v7.hasOpening[Direction.EAST.get3DDataValue()]) {
                    this.generateBox(bhs, cic, integer10, integer8, 2, integer10, integer8 + 2, 2, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer10, integer8, 5, integer10, integer8 + 2, 5, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer10, integer8 + 2, 3, integer10, integer8 + 2, 4, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                }
                else {
                    this.generateBox(bhs, cic, integer10, integer8, 0, integer10, integer8 + 2, 7, OceanMonumentDoubleYRoom.BASE_LIGHT, OceanMonumentDoubleYRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer10, integer8 + 1, 0, integer10, integer8 + 1, 7, OceanMonumentDoubleYRoom.BASE_GRAY, OceanMonumentDoubleYRoom.BASE_GRAY, false);
                }
                v7 = v6;
            }
            return true;
        }
    }
    
    public static class OceanMonumentDoubleXRoom extends OceanMonumentPiece {
        public OceanMonumentDoubleXRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, fb, v, 2, 1, 1);
        }
        
        public OceanMonumentDoubleXRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final RoomDefinition v6 = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
            final RoomDefinition v7 = this.roomDefinition;
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 8, 0, v6.hasOpening[Direction.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(bhs, cic, 0, 0, v7.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            if (v7.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 4, 1, 7, 4, 6, OceanMonumentDoubleXRoom.BASE_GRAY);
            }
            if (v6.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 8, 4, 1, 14, 4, 6, OceanMonumentDoubleXRoom.BASE_GRAY);
            }
            this.generateBox(bhs, cic, 0, 3, 0, 0, 3, 7, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 15, 3, 0, 15, 3, 7, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 0, 15, 3, 0, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 7, 14, 3, 7, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 2, 7, OceanMonumentDoubleXRoom.BASE_GRAY, OceanMonumentDoubleXRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 15, 2, 0, 15, 2, 7, OceanMonumentDoubleXRoom.BASE_GRAY, OceanMonumentDoubleXRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 1, 2, 0, 15, 2, 0, OceanMonumentDoubleXRoom.BASE_GRAY, OceanMonumentDoubleXRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 1, 2, 7, 14, 2, 7, OceanMonumentDoubleXRoom.BASE_GRAY, OceanMonumentDoubleXRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 0, 1, 0, 0, 1, 7, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 15, 1, 0, 15, 1, 7, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 0, 15, 1, 0, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 7, 14, 1, 7, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 1, 0, 10, 1, 4, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 2, 0, 9, 2, 3, OceanMonumentDoubleXRoom.BASE_GRAY, OceanMonumentDoubleXRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 5, 3, 0, 10, 3, 4, OceanMonumentDoubleXRoom.BASE_LIGHT, OceanMonumentDoubleXRoom.BASE_LIGHT, false);
            this.placeBlock(bhs, OceanMonumentDoubleXRoom.LAMP_BLOCK, 6, 2, 3, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXRoom.LAMP_BLOCK, 9, 2, 3, cic);
            if (v7.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 0, 4, 2, 0);
            }
            if (v7.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 7, 4, 2, 7);
            }
            if (v7.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 3, 0, 2, 4);
            }
            if (v6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 11, 1, 0, 12, 2, 0);
            }
            if (v6.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 11, 1, 7, 12, 2, 7);
            }
            if (v6.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 15, 1, 3, 15, 2, 4);
            }
            return true;
        }
    }
    
    public static class OceanMonumentDoubleZRoom extends OceanMonumentPiece {
        public OceanMonumentDoubleZRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, fb, v, 1, 1, 2);
        }
        
        public OceanMonumentDoubleZRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final RoomDefinition v6 = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
            final RoomDefinition v7 = this.roomDefinition;
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 0, 8, v6.hasOpening[Direction.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(bhs, cic, 0, 0, v7.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            if (v7.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 4, 1, 6, 4, 7, OceanMonumentDoubleZRoom.BASE_GRAY);
            }
            if (v6.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 4, 8, 6, 4, 14, OceanMonumentDoubleZRoom.BASE_GRAY);
            }
            this.generateBox(bhs, cic, 0, 3, 0, 0, 3, 15, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 7, 3, 0, 7, 3, 15, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 0, 7, 3, 0, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 15, 6, 3, 15, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 2, 15, OceanMonumentDoubleZRoom.BASE_GRAY, OceanMonumentDoubleZRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 7, 2, 0, 7, 2, 15, OceanMonumentDoubleZRoom.BASE_GRAY, OceanMonumentDoubleZRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 1, 2, 0, 7, 2, 0, OceanMonumentDoubleZRoom.BASE_GRAY, OceanMonumentDoubleZRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 1, 2, 15, 6, 2, 15, OceanMonumentDoubleZRoom.BASE_GRAY, OceanMonumentDoubleZRoom.BASE_GRAY, false);
            this.generateBox(bhs, cic, 0, 1, 0, 0, 1, 15, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 7, 1, 0, 7, 1, 15, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 0, 7, 1, 0, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 15, 6, 1, 15, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 1, 1, 1, 2, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 1, 1, 6, 1, 2, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 1, 1, 3, 2, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 3, 1, 6, 3, 2, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 1, 13, 1, 1, 14, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 1, 13, 6, 1, 14, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 3, 13, 1, 3, 14, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 3, 13, 6, 3, 14, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 1, 6, 2, 3, 6, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 1, 6, 5, 3, 6, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 1, 9, 2, 3, 9, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 1, 9, 5, 3, 9, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 3, 2, 6, 4, 2, 6, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 3, 2, 9, 4, 2, 9, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 2, 7, 2, 2, 8, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 2, 7, 5, 2, 8, OceanMonumentDoubleZRoom.BASE_LIGHT, OceanMonumentDoubleZRoom.BASE_LIGHT, false);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.LAMP_BLOCK, 2, 2, 5, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.LAMP_BLOCK, 5, 2, 5, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.LAMP_BLOCK, 2, 2, 10, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.LAMP_BLOCK, 5, 2, 10, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.BASE_LIGHT, 2, 3, 5, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.BASE_LIGHT, 5, 3, 5, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.BASE_LIGHT, 2, 3, 10, cic);
            this.placeBlock(bhs, OceanMonumentDoubleZRoom.BASE_LIGHT, 5, 3, 10, cic);
            if (v7.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 0, 4, 2, 0);
            }
            if (v7.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 7, 1, 3, 7, 2, 4);
            }
            if (v7.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 3, 0, 2, 4);
            }
            if (v6.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 15, 4, 2, 15);
            }
            if (v6.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 11, 0, 2, 12);
            }
            if (v6.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 7, 1, 11, 7, 2, 12);
            }
            return true;
        }
    }
    
    public static class OceanMonumentDoubleXYRoom extends OceanMonumentPiece {
        public OceanMonumentDoubleXYRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, fb, v, 2, 2, 1);
        }
        
        public OceanMonumentDoubleXYRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final RoomDefinition v6 = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
            final RoomDefinition v7 = this.roomDefinition;
            final RoomDefinition v8 = v7.connections[Direction.UP.get3DDataValue()];
            final RoomDefinition v9 = v6.connections[Direction.UP.get3DDataValue()];
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 8, 0, v6.hasOpening[Direction.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(bhs, cic, 0, 0, v7.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            if (v8.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 8, 1, 7, 8, 6, OceanMonumentDoubleXYRoom.BASE_GRAY);
            }
            if (v9.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 8, 8, 1, 14, 8, 6, OceanMonumentDoubleXYRoom.BASE_GRAY);
            }
            for (int integer10 = 1; integer10 <= 7; ++integer10) {
                BlockState bvt11 = OceanMonumentDoubleXYRoom.BASE_LIGHT;
                if (integer10 == 2 || integer10 == 6) {
                    bvt11 = OceanMonumentDoubleXYRoom.BASE_GRAY;
                }
                this.generateBox(bhs, cic, 0, integer10, 0, 0, integer10, 7, bvt11, bvt11, false);
                this.generateBox(bhs, cic, 15, integer10, 0, 15, integer10, 7, bvt11, bvt11, false);
                this.generateBox(bhs, cic, 1, integer10, 0, 15, integer10, 0, bvt11, bvt11, false);
                this.generateBox(bhs, cic, 1, integer10, 7, 14, integer10, 7, bvt11, bvt11, false);
            }
            this.generateBox(bhs, cic, 2, 1, 3, 2, 7, 4, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 3, 1, 2, 4, 7, 2, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 3, 1, 5, 4, 7, 5, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 13, 1, 3, 13, 7, 4, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 11, 1, 2, 12, 7, 2, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 11, 1, 5, 12, 7, 5, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 1, 3, 5, 3, 4, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 1, 3, 10, 3, 4, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 7, 2, 10, 7, 5, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 5, 2, 5, 7, 2, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 5, 2, 10, 7, 2, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 5, 5, 5, 7, 5, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 5, 5, 10, 7, 5, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.BASE_LIGHT, 6, 6, 2, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.BASE_LIGHT, 9, 6, 2, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.BASE_LIGHT, 6, 6, 5, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.BASE_LIGHT, 9, 6, 5, cic);
            this.generateBox(bhs, cic, 5, 4, 3, 6, 4, 4, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 9, 4, 3, 10, 4, 4, OceanMonumentDoubleXYRoom.BASE_LIGHT, OceanMonumentDoubleXYRoom.BASE_LIGHT, false);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.LAMP_BLOCK, 5, 4, 2, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.LAMP_BLOCK, 5, 4, 5, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.LAMP_BLOCK, 10, 4, 2, cic);
            this.placeBlock(bhs, OceanMonumentDoubleXYRoom.LAMP_BLOCK, 10, 4, 5, cic);
            if (v7.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 0, 4, 2, 0);
            }
            if (v7.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 7, 4, 2, 7);
            }
            if (v7.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 3, 0, 2, 4);
            }
            if (v6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 11, 1, 0, 12, 2, 0);
            }
            if (v6.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 11, 1, 7, 12, 2, 7);
            }
            if (v6.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 15, 1, 3, 15, 2, 4);
            }
            if (v8.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 5, 0, 4, 6, 0);
            }
            if (v8.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 5, 7, 4, 6, 7);
            }
            if (v8.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 5, 3, 0, 6, 4);
            }
            if (v9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 11, 5, 0, 12, 6, 0);
            }
            if (v9.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 11, 5, 7, 12, 6, 7);
            }
            if (v9.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 15, 5, 3, 15, 6, 4);
            }
            return true;
        }
    }
    
    public static class OceanMonumentDoubleYZRoom extends OceanMonumentPiece {
        public OceanMonumentDoubleYZRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, fb, v, 1, 2, 2);
        }
        
        public OceanMonumentDoubleYZRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final RoomDefinition v6 = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
            final RoomDefinition v7 = this.roomDefinition;
            final RoomDefinition v8 = v6.connections[Direction.UP.get3DDataValue()];
            final RoomDefinition v9 = v7.connections[Direction.UP.get3DDataValue()];
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(bhs, cic, 0, 8, v6.hasOpening[Direction.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(bhs, cic, 0, 0, v7.hasOpening[Direction.DOWN.get3DDataValue()]);
            }
            if (v9.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 8, 1, 6, 8, 7, OceanMonumentDoubleYZRoom.BASE_GRAY);
            }
            if (v8.connections[Direction.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(bhs, cic, 1, 8, 8, 6, 8, 14, OceanMonumentDoubleYZRoom.BASE_GRAY);
            }
            for (int integer10 = 1; integer10 <= 7; ++integer10) {
                BlockState bvt11 = OceanMonumentDoubleYZRoom.BASE_LIGHT;
                if (integer10 == 2 || integer10 == 6) {
                    bvt11 = OceanMonumentDoubleYZRoom.BASE_GRAY;
                }
                this.generateBox(bhs, cic, 0, integer10, 0, 0, integer10, 15, bvt11, bvt11, false);
                this.generateBox(bhs, cic, 7, integer10, 0, 7, integer10, 15, bvt11, bvt11, false);
                this.generateBox(bhs, cic, 1, integer10, 0, 6, integer10, 0, bvt11, bvt11, false);
                this.generateBox(bhs, cic, 1, integer10, 15, 6, integer10, 15, bvt11, bvt11, false);
            }
            for (int integer10 = 1; integer10 <= 7; ++integer10) {
                BlockState bvt11 = OceanMonumentDoubleYZRoom.BASE_BLACK;
                if (integer10 == 2 || integer10 == 6) {
                    bvt11 = OceanMonumentDoubleYZRoom.LAMP_BLOCK;
                }
                this.generateBox(bhs, cic, 3, integer10, 7, 4, integer10, 8, bvt11, bvt11, false);
            }
            if (v7.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 0, 4, 2, 0);
            }
            if (v7.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 7, 1, 3, 7, 2, 4);
            }
            if (v7.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 3, 0, 2, 4);
            }
            if (v6.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 1, 15, 4, 2, 15);
            }
            if (v6.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 1, 11, 0, 2, 12);
            }
            if (v6.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 7, 1, 11, 7, 2, 12);
            }
            if (v9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 5, 0, 4, 6, 0);
            }
            if (v9.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 7, 5, 3, 7, 6, 4);
                this.generateBox(bhs, cic, 5, 4, 2, 6, 4, 5, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 1, 2, 6, 3, 2, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 1, 5, 6, 3, 5, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
            }
            if (v9.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 5, 3, 0, 6, 4);
                this.generateBox(bhs, cic, 1, 4, 2, 2, 4, 5, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 1, 2, 1, 3, 2, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 1, 5, 1, 3, 5, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
            }
            if (v8.hasOpening[Direction.NORTH.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 3, 5, 15, 4, 6, 15);
            }
            if (v8.hasOpening[Direction.WEST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 0, 5, 11, 0, 6, 12);
                this.generateBox(bhs, cic, 1, 4, 10, 2, 4, 13, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 1, 10, 1, 3, 10, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 1, 1, 13, 1, 3, 13, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
            }
            if (v8.hasOpening[Direction.EAST.get3DDataValue()]) {
                this.generateWaterBox(bhs, cic, 7, 5, 11, 7, 6, 12);
                this.generateBox(bhs, cic, 5, 4, 10, 6, 4, 13, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 1, 10, 6, 3, 10, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 1, 13, 6, 3, 13, OceanMonumentDoubleYZRoom.BASE_LIGHT, OceanMonumentDoubleYZRoom.BASE_LIGHT, false);
            }
            return true;
        }
    }
    
    public static class OceanMonumentCoreRoom extends OceanMonumentPiece {
        public OceanMonumentCoreRoom(final Direction fb, final RoomDefinition v) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, fb, v, 2, 2, 2);
        }
        
        public OceanMonumentCoreRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBoxOnFillOnly(bhs, cic, 1, 8, 0, 14, 8, 14, OceanMonumentCoreRoom.BASE_GRAY);
            int integer6 = 7;
            BlockState bvt7 = OceanMonumentCoreRoom.BASE_LIGHT;
            this.generateBox(bhs, cic, 0, 7, 0, 0, 7, 15, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 15, 7, 0, 15, 7, 15, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 1, 7, 0, 15, 7, 0, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 1, 7, 15, 14, 7, 15, bvt7, bvt7, false);
            for (integer6 = 1; integer6 <= 6; ++integer6) {
                bvt7 = OceanMonumentCoreRoom.BASE_LIGHT;
                if (integer6 == 2 || integer6 == 6) {
                    bvt7 = OceanMonumentCoreRoom.BASE_GRAY;
                }
                for (int integer7 = 0; integer7 <= 15; integer7 += 15) {
                    this.generateBox(bhs, cic, integer7, integer6, 0, integer7, integer6, 1, bvt7, bvt7, false);
                    this.generateBox(bhs, cic, integer7, integer6, 6, integer7, integer6, 9, bvt7, bvt7, false);
                    this.generateBox(bhs, cic, integer7, integer6, 14, integer7, integer6, 15, bvt7, bvt7, false);
                }
                this.generateBox(bhs, cic, 1, integer6, 0, 1, integer6, 0, bvt7, bvt7, false);
                this.generateBox(bhs, cic, 6, integer6, 0, 9, integer6, 0, bvt7, bvt7, false);
                this.generateBox(bhs, cic, 14, integer6, 0, 14, integer6, 0, bvt7, bvt7, false);
                this.generateBox(bhs, cic, 1, integer6, 15, 14, integer6, 15, bvt7, bvt7, false);
            }
            this.generateBox(bhs, cic, 6, 3, 6, 9, 6, 9, OceanMonumentCoreRoom.BASE_BLACK, OceanMonumentCoreRoom.BASE_BLACK, false);
            this.generateBox(bhs, cic, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.GOLD_BLOCK.defaultBlockState(), false);
            for (integer6 = 3; integer6 <= 6; integer6 += 3) {
                for (int integer8 = 6; integer8 <= 9; integer8 += 3) {
                    this.placeBlock(bhs, OceanMonumentCoreRoom.LAMP_BLOCK, integer8, integer6, 6, cic);
                    this.placeBlock(bhs, OceanMonumentCoreRoom.LAMP_BLOCK, integer8, integer6, 9, cic);
                }
            }
            this.generateBox(bhs, cic, 5, 1, 6, 5, 2, 6, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 1, 9, 5, 2, 9, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 1, 6, 10, 2, 6, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 1, 9, 10, 2, 9, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 1, 5, 6, 2, 5, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 9, 1, 5, 9, 2, 5, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, 1, 10, 6, 2, 10, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 9, 1, 10, 9, 2, 10, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 2, 5, 5, 6, 5, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 2, 10, 5, 6, 10, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 2, 5, 10, 6, 5, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 2, 10, 10, 6, 10, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 7, 1, 5, 7, 6, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 7, 1, 10, 7, 6, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 5, 7, 9, 5, 7, 14, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 10, 7, 9, 10, 7, 14, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 7, 5, 6, 7, 5, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 7, 10, 6, 7, 10, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 9, 7, 5, 14, 7, 5, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 9, 7, 10, 14, 7, 10, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 1, 2, 2, 1, 3, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 3, 1, 2, 3, 1, 2, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 13, 1, 2, 13, 1, 3, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 12, 1, 2, 12, 1, 2, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 2, 1, 12, 2, 1, 13, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 3, 1, 13, 3, 1, 13, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 13, 1, 12, 13, 1, 13, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 12, 1, 13, 12, 1, 13, OceanMonumentCoreRoom.BASE_LIGHT, OceanMonumentCoreRoom.BASE_LIGHT, false);
            return true;
        }
    }
    
    public static class OceanMonumentWingRoom extends OceanMonumentPiece {
        private int mainDesign;
        
        public OceanMonumentWingRoom(final Direction fb, final BoundingBox cic, final int integer) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, fb, cic);
            this.mainDesign = (integer & 0x1);
        }
        
        public OceanMonumentWingRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.mainDesign == 0) {
                for (int integer6 = 0; integer6 < 4; ++integer6) {
                    this.generateBox(bhs, cic, 10 - integer6, 3 - integer6, 20 - integer6, 12 + integer6, 3 - integer6, 20, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                }
                this.generateBox(bhs, cic, 7, 0, 6, 15, 0, 16, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 6, 0, 6, 6, 3, 20, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 16, 0, 6, 16, 3, 20, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 1, 7, 7, 1, 20, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 15, 1, 7, 15, 1, 20, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 7, 1, 6, 9, 3, 6, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 13, 1, 6, 15, 3, 6, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 8, 1, 7, 9, 1, 7, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 13, 1, 7, 14, 1, 7, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 9, 0, 5, 13, 0, 5, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 10, 0, 7, 12, 0, 7, OceanMonumentWingRoom.BASE_BLACK, OceanMonumentWingRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 8, 0, 10, 8, 0, 12, OceanMonumentWingRoom.BASE_BLACK, OceanMonumentWingRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 14, 0, 10, 14, 0, 12, OceanMonumentWingRoom.BASE_BLACK, OceanMonumentWingRoom.BASE_BLACK, false);
                for (int integer6 = 18; integer6 >= 7; integer6 -= 3) {
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 6, 3, integer6, cic);
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 16, 3, integer6, cic);
                }
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 10, 0, 10, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 12, 0, 10, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 10, 0, 12, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 12, 0, 12, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 8, 3, 6, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 14, 3, 6, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 4, 2, 4, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 4, 1, 4, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 4, 0, 4, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 18, 2, 4, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 18, 1, 4, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 18, 0, 4, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 4, 2, 18, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 4, 1, 18, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 4, 0, 18, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 18, 2, 18, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, 18, 1, 18, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 18, 0, 18, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 9, 7, 20, cic);
                this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, 13, 7, 20, cic);
                this.generateBox(bhs, cic, 6, 0, 21, 7, 4, 21, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 15, 0, 21, 16, 4, 21, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.spawnElder(bhs, cic, 11, 2, 16);
            }
            else if (this.mainDesign == 1) {
                this.generateBox(bhs, cic, 9, 3, 18, 13, 3, 20, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 9, 0, 18, 9, 2, 18, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                this.generateBox(bhs, cic, 13, 0, 18, 13, 2, 18, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                int integer6 = 9;
                final int integer7 = 20;
                final int integer8 = 5;
                for (int integer9 = 0; integer9 < 2; ++integer9) {
                    this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, integer6, 6, 20, cic);
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, integer6, 5, 20, cic);
                    this.placeBlock(bhs, OceanMonumentWingRoom.BASE_LIGHT, integer6, 4, 20, cic);
                    integer6 = 13;
                }
                this.generateBox(bhs, cic, 7, 3, 7, 15, 3, 14, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                integer6 = 10;
                for (int integer9 = 0; integer9 < 2; ++integer9) {
                    this.generateBox(bhs, cic, integer6, 0, 10, integer6, 6, 10, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer6, 0, 12, integer6, 6, 12, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, integer6, 0, 10, cic);
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, integer6, 0, 12, cic);
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, integer6, 4, 10, cic);
                    this.placeBlock(bhs, OceanMonumentWingRoom.LAMP_BLOCK, integer6, 4, 12, cic);
                    integer6 = 12;
                }
                integer6 = 8;
                for (int integer9 = 0; integer9 < 2; ++integer9) {
                    this.generateBox(bhs, cic, integer6, 0, 7, integer6, 2, 7, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                    this.generateBox(bhs, cic, integer6, 0, 14, integer6, 2, 14, OceanMonumentWingRoom.BASE_LIGHT, OceanMonumentWingRoom.BASE_LIGHT, false);
                    integer6 = 14;
                }
                this.generateBox(bhs, cic, 8, 3, 8, 8, 3, 13, OceanMonumentWingRoom.BASE_BLACK, OceanMonumentWingRoom.BASE_BLACK, false);
                this.generateBox(bhs, cic, 14, 3, 8, 14, 3, 13, OceanMonumentWingRoom.BASE_BLACK, OceanMonumentWingRoom.BASE_BLACK, false);
                this.spawnElder(bhs, cic, 11, 5, 13);
            }
            return true;
        }
    }
    
    public static class OceanMonumentPenthouse extends OceanMonumentPiece {
        public OceanMonumentPenthouse(final Direction fb, final BoundingBox cic) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, fb, cic);
        }
        
        public OceanMonumentPenthouse(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, id);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 2, -1, 2, 11, -1, 11, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 0, -1, 0, 1, -1, 11, OceanMonumentPenthouse.BASE_GRAY, OceanMonumentPenthouse.BASE_GRAY, false);
            this.generateBox(bhs, cic, 12, -1, 0, 13, -1, 11, OceanMonumentPenthouse.BASE_GRAY, OceanMonumentPenthouse.BASE_GRAY, false);
            this.generateBox(bhs, cic, 2, -1, 0, 11, -1, 1, OceanMonumentPenthouse.BASE_GRAY, OceanMonumentPenthouse.BASE_GRAY, false);
            this.generateBox(bhs, cic, 2, -1, 12, 11, -1, 13, OceanMonumentPenthouse.BASE_GRAY, OceanMonumentPenthouse.BASE_GRAY, false);
            this.generateBox(bhs, cic, 0, 0, 0, 0, 0, 13, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 13, 0, 0, 13, 0, 13, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 0, 0, 12, 0, 0, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 1, 0, 13, 12, 0, 13, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            for (int integer6 = 2; integer6 <= 11; integer6 += 3) {
                this.placeBlock(bhs, OceanMonumentPenthouse.LAMP_BLOCK, 0, 0, integer6, cic);
                this.placeBlock(bhs, OceanMonumentPenthouse.LAMP_BLOCK, 13, 0, integer6, cic);
                this.placeBlock(bhs, OceanMonumentPenthouse.LAMP_BLOCK, integer6, 0, 0, cic);
            }
            this.generateBox(bhs, cic, 2, 0, 3, 4, 0, 9, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 9, 0, 3, 11, 0, 9, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 4, 0, 9, 9, 0, 11, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.placeBlock(bhs, OceanMonumentPenthouse.BASE_LIGHT, 5, 0, 8, cic);
            this.placeBlock(bhs, OceanMonumentPenthouse.BASE_LIGHT, 8, 0, 8, cic);
            this.placeBlock(bhs, OceanMonumentPenthouse.BASE_LIGHT, 10, 0, 10, cic);
            this.placeBlock(bhs, OceanMonumentPenthouse.BASE_LIGHT, 3, 0, 10, cic);
            this.generateBox(bhs, cic, 3, 0, 3, 3, 0, 7, OceanMonumentPenthouse.BASE_BLACK, OceanMonumentPenthouse.BASE_BLACK, false);
            this.generateBox(bhs, cic, 10, 0, 3, 10, 0, 7, OceanMonumentPenthouse.BASE_BLACK, OceanMonumentPenthouse.BASE_BLACK, false);
            this.generateBox(bhs, cic, 6, 0, 10, 7, 0, 10, OceanMonumentPenthouse.BASE_BLACK, OceanMonumentPenthouse.BASE_BLACK, false);
            int integer6 = 3;
            for (int integer7 = 0; integer7 < 2; ++integer7) {
                for (int integer8 = 2; integer8 <= 8; integer8 += 3) {
                    this.generateBox(bhs, cic, integer6, 0, integer8, integer6, 2, integer8, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
                }
                integer6 = 10;
            }
            this.generateBox(bhs, cic, 5, 0, 10, 5, 2, 10, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 8, 0, 10, 8, 2, 10, OceanMonumentPenthouse.BASE_LIGHT, OceanMonumentPenthouse.BASE_LIGHT, false);
            this.generateBox(bhs, cic, 6, -1, 7, 7, -1, 8, OceanMonumentPenthouse.BASE_BLACK, OceanMonumentPenthouse.BASE_BLACK, false);
            this.generateWaterBox(bhs, cic, 6, -1, 3, 7, -1, 4);
            this.spawnElder(bhs, cic, 6, 1, 6);
            return true;
        }
    }
    
    static class RoomDefinition {
        private final int index;
        private final RoomDefinition[] connections;
        private final boolean[] hasOpening;
        private boolean claimed;
        private boolean isSource;
        private int scanIndex;
        
        public RoomDefinition(final int integer) {
            this.connections = new RoomDefinition[6];
            this.hasOpening = new boolean[6];
            this.index = integer;
        }
        
        public void setConnection(final Direction fb, final RoomDefinition v) {
            this.connections[fb.get3DDataValue()] = v;
            v.connections[fb.getOpposite().get3DDataValue()] = this;
        }
        
        public void updateOpenings() {
            for (int integer2 = 0; integer2 < 6; ++integer2) {
                this.hasOpening[integer2] = (this.connections[integer2] != null);
            }
        }
        
        public boolean findSource(final int integer) {
            if (this.isSource) {
                return true;
            }
            this.scanIndex = integer;
            for (int integer2 = 0; integer2 < 6; ++integer2) {
                if (this.connections[integer2] != null && this.hasOpening[integer2] && this.connections[integer2].scanIndex != integer && this.connections[integer2].findSource(integer)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean isSpecial() {
            return this.index >= 75;
        }
        
        public int countOpenings() {
            int integer2 = 0;
            for (int integer3 = 0; integer3 < 6; ++integer3) {
                if (this.hasOpening[integer3]) {
                    ++integer2;
                }
            }
            return integer2;
        }
    }
    
    static class FitSimpleRoom implements MonumentRoomFitter {
        private FitSimpleRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            return true;
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            v.claimed = true;
            return new OceanMonumentSimpleRoom(fb, v, random);
        }
    }
    
    static class FitSimpleTopRoom implements MonumentRoomFitter {
        private FitSimpleTopRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            return !v.hasOpening[Direction.WEST.get3DDataValue()] && !v.hasOpening[Direction.EAST.get3DDataValue()] && !v.hasOpening[Direction.NORTH.get3DDataValue()] && !v.hasOpening[Direction.SOUTH.get3DDataValue()] && !v.hasOpening[Direction.UP.get3DDataValue()];
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            v.claimed = true;
            return new OceanMonumentSimpleTopRoom(fb, v);
        }
    }
    
    static class FitDoubleYRoom implements MonumentRoomFitter {
        private FitDoubleYRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            return v.hasOpening[Direction.UP.get3DDataValue()] && !v.connections[Direction.UP.get3DDataValue()].claimed;
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            v.claimed = true;
            v.connections[Direction.UP.get3DDataValue()].claimed = true;
            return new OceanMonumentDoubleYRoom(fb, v);
        }
    }
    
    static class FitDoubleXRoom implements MonumentRoomFitter {
        private FitDoubleXRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            return v.hasOpening[Direction.EAST.get3DDataValue()] && !v.connections[Direction.EAST.get3DDataValue()].claimed;
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            v.claimed = true;
            v.connections[Direction.EAST.get3DDataValue()].claimed = true;
            return new OceanMonumentDoubleXRoom(fb, v);
        }
    }
    
    static class FitDoubleZRoom implements MonumentRoomFitter {
        private FitDoubleZRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            return v.hasOpening[Direction.NORTH.get3DDataValue()] && !v.connections[Direction.NORTH.get3DDataValue()].claimed;
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            RoomDefinition v2 = v;
            if (!v.hasOpening[Direction.NORTH.get3DDataValue()] || v.connections[Direction.NORTH.get3DDataValue()].claimed) {
                v2 = v.connections[Direction.SOUTH.get3DDataValue()];
            }
            v2.claimed = true;
            v2.connections[Direction.NORTH.get3DDataValue()].claimed = true;
            return new OceanMonumentDoubleZRoom(fb, v2);
        }
    }
    
    static class FitDoubleXYRoom implements MonumentRoomFitter {
        private FitDoubleXYRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            if (v.hasOpening[Direction.EAST.get3DDataValue()] && !v.connections[Direction.EAST.get3DDataValue()].claimed && v.hasOpening[Direction.UP.get3DDataValue()] && !v.connections[Direction.UP.get3DDataValue()].claimed) {
                final RoomDefinition v2 = v.connections[Direction.EAST.get3DDataValue()];
                return v2.hasOpening[Direction.UP.get3DDataValue()] && !v2.connections[Direction.UP.get3DDataValue()].claimed;
            }
            return false;
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            v.claimed = true;
            v.connections[Direction.EAST.get3DDataValue()].claimed = true;
            v.connections[Direction.UP.get3DDataValue()].claimed = true;
            v.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
            return new OceanMonumentDoubleXYRoom(fb, v);
        }
    }
    
    static class FitDoubleYZRoom implements MonumentRoomFitter {
        private FitDoubleYZRoom() {
        }
        
        public boolean fits(final RoomDefinition v) {
            if (v.hasOpening[Direction.NORTH.get3DDataValue()] && !v.connections[Direction.NORTH.get3DDataValue()].claimed && v.hasOpening[Direction.UP.get3DDataValue()] && !v.connections[Direction.UP.get3DDataValue()].claimed) {
                final RoomDefinition v2 = v.connections[Direction.NORTH.get3DDataValue()];
                return v2.hasOpening[Direction.UP.get3DDataValue()] && !v2.connections[Direction.UP.get3DDataValue()].claimed;
            }
            return false;
        }
        
        public OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random) {
            v.claimed = true;
            v.connections[Direction.NORTH.get3DDataValue()].claimed = true;
            v.connections[Direction.UP.get3DDataValue()].claimed = true;
            v.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
            return new OceanMonumentDoubleYZRoom(fb, v);
        }
    }
    
    interface MonumentRoomFitter {
        boolean fits(final RoomDefinition v);
        
        OceanMonumentPiece create(final Direction fb, final RoomDefinition v, final Random random);
    }
}
