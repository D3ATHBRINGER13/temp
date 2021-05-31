package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import java.util.Iterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.google.common.collect.Lists;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import java.util.Random;
import java.util.List;

public class MineShaftPieces {
    private static MineShaftPiece createRandomShaftPiece(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, @Nullable final Direction fb, final int integer7, final MineshaftFeature.Type b) {
        final int integer8 = random.nextInt(100);
        if (integer8 >= 80) {
            final BoundingBox cic10 = MineShaftCrossing.findCrossing(list, random, integer3, integer4, integer5, fb);
            if (cic10 != null) {
                return new MineShaftCrossing(integer7, cic10, fb, b);
            }
        }
        else if (integer8 >= 70) {
            final BoundingBox cic10 = MineShaftStairs.findStairs(list, random, integer3, integer4, integer5, fb);
            if (cic10 != null) {
                return new MineShaftStairs(integer7, cic10, fb, b);
            }
        }
        else {
            final BoundingBox cic10 = MineShaftCorridor.findCorridorSize(list, random, integer3, integer4, integer5, fb);
            if (cic10 != null) {
                return new MineShaftCorridor(integer7, random, cic10, fb, b);
            }
        }
        return null;
    }
    
    private static MineShaftPiece generateAndAddPiece(final StructurePiece civ, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, final Direction fb, final int integer8) {
        if (integer8 > 8) {
            return null;
        }
        if (Math.abs(integer4 - civ.getBoundingBox().x0) > 80 || Math.abs(integer6 - civ.getBoundingBox().z0) > 80) {
            return null;
        }
        final MineshaftFeature.Type b9 = ((MineShaftPiece)civ).type;
        final MineShaftPiece c10 = createRandomShaftPiece(list, random, integer4, integer5, integer6, fb, integer8 + 1, b9);
        if (c10 != null) {
            list.add(c10);
            c10.addChildren(civ, list, random);
        }
        return c10;
    }
    
    abstract static class MineShaftPiece extends StructurePiece {
        protected MineshaftFeature.Type type;
        
        public MineShaftPiece(final StructurePieceType cev, final int integer, final MineshaftFeature.Type b) {
            super(cev, integer);
            this.type = b;
        }
        
        public MineShaftPiece(final StructurePieceType cev, final CompoundTag id) {
            super(cev, id);
            this.type = MineshaftFeature.Type.byId(id.getInt("MST"));
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            id.putInt("MST", this.type.ordinal());
        }
        
        protected BlockState getPlanksBlock() {
            switch (this.type) {
                default: {
                    return Blocks.OAK_PLANKS.defaultBlockState();
                }
                case MESA: {
                    return Blocks.DARK_OAK_PLANKS.defaultBlockState();
                }
            }
        }
        
        protected BlockState getFenceBlock() {
            switch (this.type) {
                default: {
                    return Blocks.OAK_FENCE.defaultBlockState();
                }
                case MESA: {
                    return Blocks.DARK_OAK_FENCE.defaultBlockState();
                }
            }
        }
        
        protected boolean isSupportingBox(final BlockGetter bhb, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6) {
            for (int integer7 = integer3; integer7 <= integer4; ++integer7) {
                if (this.getBlock(bhb, integer7, integer5 + 1, integer6, cic).isAir()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static class MineShaftRoom extends MineShaftPiece {
        private final List<BoundingBox> childEntranceBoxes;
        
        public MineShaftRoom(final int integer1, final Random random, final int integer3, final int integer4, final MineshaftFeature.Type b) {
            super(StructurePieceType.MINE_SHAFT_ROOM, integer1, b);
            this.childEntranceBoxes = (List<BoundingBox>)Lists.newLinkedList();
            this.type = b;
            this.boundingBox = new BoundingBox(integer3, 50, integer4, integer3 + 7 + random.nextInt(6), 54 + random.nextInt(6), integer4 + 7 + random.nextInt(6));
        }
        
        public MineShaftRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.MINE_SHAFT_ROOM, id);
            this.childEntranceBoxes = (List<BoundingBox>)Lists.newLinkedList();
            final ListTag ik4 = id.getList("Entrances", 11);
            for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                this.childEntranceBoxes.add(new BoundingBox(ik4.getIntArray(integer5)));
            }
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            final int integer5 = this.getGenDepth();
            int integer6 = this.boundingBox.getYSpan() - 3 - 1;
            if (integer6 <= 0) {
                integer6 = 1;
            }
            for (int integer7 = 0; integer7 < this.boundingBox.getXSpan(); integer7 += 4) {
                integer7 += random.nextInt(this.boundingBox.getXSpan());
                if (integer7 + 3 > this.boundingBox.getXSpan()) {
                    break;
                }
                final MineShaftPiece c8 = generateAndAddPiece(civ, list, random, this.boundingBox.x0 + integer7, this.boundingBox.y0 + random.nextInt(integer6) + 1, this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                if (c8 != null) {
                    final BoundingBox cic9 = c8.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(cic9.x0, cic9.y0, this.boundingBox.z0, cic9.x1, cic9.y1, this.boundingBox.z0 + 1));
                }
            }
            for (int integer7 = 0; integer7 < this.boundingBox.getXSpan(); integer7 += 4) {
                integer7 += random.nextInt(this.boundingBox.getXSpan());
                if (integer7 + 3 > this.boundingBox.getXSpan()) {
                    break;
                }
                final MineShaftPiece c8 = generateAndAddPiece(civ, list, random, this.boundingBox.x0 + integer7, this.boundingBox.y0 + random.nextInt(integer6) + 1, this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                if (c8 != null) {
                    final BoundingBox cic9 = c8.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(cic9.x0, cic9.y0, this.boundingBox.z1 - 1, cic9.x1, cic9.y1, this.boundingBox.z1));
                }
            }
            for (int integer7 = 0; integer7 < this.boundingBox.getZSpan(); integer7 += 4) {
                integer7 += random.nextInt(this.boundingBox.getZSpan());
                if (integer7 + 3 > this.boundingBox.getZSpan()) {
                    break;
                }
                final MineShaftPiece c8 = generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + random.nextInt(integer6) + 1, this.boundingBox.z0 + integer7, Direction.WEST, integer5);
                if (c8 != null) {
                    final BoundingBox cic9 = c8.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x0, cic9.y0, cic9.z0, this.boundingBox.x0 + 1, cic9.y1, cic9.z1));
                }
            }
            for (int integer7 = 0; integer7 < this.boundingBox.getZSpan(); integer7 += 4) {
                integer7 += random.nextInt(this.boundingBox.getZSpan());
                if (integer7 + 3 > this.boundingBox.getZSpan()) {
                    break;
                }
                final StructurePiece civ2 = generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + random.nextInt(integer6) + 1, this.boundingBox.z0 + integer7, Direction.EAST, integer5);
                if (civ2 != null) {
                    final BoundingBox cic9 = civ2.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x1 - 1, cic9.y0, cic9.z0, this.boundingBox.x1, cic9.y1, cic9.z1));
                }
            }
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.edgesLiquid(bhs, cic)) {
                return false;
            }
            this.generateBox(bhs, cic, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y0, this.boundingBox.z1, Blocks.DIRT.defaultBlockState(), MineShaftRoom.CAVE_AIR, true);
            this.generateBox(bhs, cic, this.boundingBox.x0, this.boundingBox.y0 + 1, this.boundingBox.z0, this.boundingBox.x1, Math.min(this.boundingBox.y0 + 3, this.boundingBox.y1), this.boundingBox.z1, MineShaftRoom.CAVE_AIR, MineShaftRoom.CAVE_AIR, false);
            for (final BoundingBox cic2 : this.childEntranceBoxes) {
                this.generateBox(bhs, cic, cic2.x0, cic2.y1 - 2, cic2.z0, cic2.x1, cic2.y1, cic2.z1, MineShaftRoom.CAVE_AIR, MineShaftRoom.CAVE_AIR, false);
            }
            this.generateUpperHalfSphere(bhs, cic, this.boundingBox.x0, this.boundingBox.y0 + 4, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1, MineShaftRoom.CAVE_AIR, false);
            return true;
        }
        
        @Override
        public void move(final int integer1, final int integer2, final int integer3) {
            super.move(integer1, integer2, integer3);
            for (final BoundingBox cic6 : this.childEntranceBoxes) {
                cic6.move(integer1, integer2, integer3);
            }
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            final ListTag ik3 = new ListTag();
            for (final BoundingBox cic5 : this.childEntranceBoxes) {
                ik3.add(cic5.createTag());
            }
            id.put("Entrances", (Tag)ik3);
        }
    }
    
    public static class MineShaftCorridor extends MineShaftPiece {
        private final boolean hasRails;
        private final boolean spiderCorridor;
        private boolean hasPlacedSpider;
        private final int numSections;
        
        public MineShaftCorridor(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.MINE_SHAFT_CORRIDOR, id);
            this.hasRails = id.getBoolean("hr");
            this.spiderCorridor = id.getBoolean("sc");
            this.hasPlacedSpider = id.getBoolean("hps");
            this.numSections = id.getInt("Num");
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putBoolean("hr", this.hasRails);
            id.putBoolean("sc", this.spiderCorridor);
            id.putBoolean("hps", this.hasPlacedSpider);
            id.putInt("Num", this.numSections);
        }
        
        public MineShaftCorridor(final int integer, final Random random, final BoundingBox cic, final Direction fb, final MineshaftFeature.Type b) {
            super(StructurePieceType.MINE_SHAFT_CORRIDOR, integer, b);
            this.setOrientation(fb);
            this.boundingBox = cic;
            this.hasRails = (random.nextInt(3) == 0);
            this.spiderCorridor = (!this.hasRails && random.nextInt(23) == 0);
            if (this.getOrientation().getAxis() == Direction.Axis.Z) {
                this.numSections = cic.getZSpan() / 5;
            }
            else {
                this.numSections = cic.getXSpan() / 5;
            }
        }
        
        public static BoundingBox findCorridorSize(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb) {
            final BoundingBox cic7 = new BoundingBox(integer3, integer4, integer5, integer3, integer4 + 3 - 1, integer5);
            int integer6;
            for (integer6 = random.nextInt(3) + 2; integer6 > 0; --integer6) {
                final int integer7 = integer6 * 5;
                switch (fb) {
                    default: {
                        cic7.x1 = integer3 + 3 - 1;
                        cic7.z0 = integer5 - (integer7 - 1);
                        break;
                    }
                    case SOUTH: {
                        cic7.x1 = integer3 + 3 - 1;
                        cic7.z1 = integer5 + integer7 - 1;
                        break;
                    }
                    case WEST: {
                        cic7.x0 = integer3 - (integer7 - 1);
                        cic7.z1 = integer5 + 3 - 1;
                        break;
                    }
                    case EAST: {
                        cic7.x1 = integer3 + integer7 - 1;
                        cic7.z1 = integer5 + 3 - 1;
                        break;
                    }
                }
                if (StructurePiece.findCollisionPiece(list, cic7) == null) {
                    break;
                }
            }
            if (integer6 > 0) {
                return cic7;
            }
            return null;
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            final int integer5 = this.getGenDepth();
            final int integer6 = random.nextInt(4);
            final Direction fb7 = this.getOrientation();
            if (fb7 != null) {
                switch (fb7) {
                    default: {
                        if (integer6 <= 1) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, fb7, integer5);
                            break;
                        }
                        if (integer6 == 2) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, Direction.WEST, integer5);
                            break;
                        }
                        generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, Direction.EAST, integer5);
                        break;
                    }
                    case SOUTH: {
                        if (integer6 <= 1) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, fb7, integer5);
                            break;
                        }
                        if (integer6 == 2) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 - 3, Direction.WEST, integer5);
                            break;
                        }
                        generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 - 3, Direction.EAST, integer5);
                        break;
                    }
                    case WEST: {
                        if (integer6 <= 1) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, fb7, integer5);
                            break;
                        }
                        if (integer6 == 2) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                            break;
                        }
                        generateAndAddPiece(civ, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                        break;
                    }
                    case EAST: {
                        if (integer6 <= 1) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, fb7, integer5);
                            break;
                        }
                        if (integer6 == 2) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                            break;
                        }
                        generateAndAddPiece(civ, list, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                        break;
                    }
                }
            }
            if (integer5 < 8) {
                if (fb7 == Direction.NORTH || fb7 == Direction.SOUTH) {
                    for (int integer7 = this.boundingBox.z0 + 3; integer7 + 3 <= this.boundingBox.z1; integer7 += 5) {
                        final int integer8 = random.nextInt(5);
                        if (integer8 == 0) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, integer7, Direction.WEST, integer5 + 1);
                        }
                        else if (integer8 == 1) {
                            generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, integer7, Direction.EAST, integer5 + 1);
                        }
                    }
                }
                else {
                    for (int integer7 = this.boundingBox.x0 + 3; integer7 + 3 <= this.boundingBox.x1; integer7 += 5) {
                        final int integer8 = random.nextInt(5);
                        if (integer8 == 0) {
                            generateAndAddPiece(civ, list, random, integer7, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, integer5 + 1);
                        }
                        else if (integer8 == 1) {
                            generateAndAddPiece(civ, list, random, integer7, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, integer5 + 1);
                        }
                    }
                }
            }
        }
        
        @Override
        protected boolean createChest(final LevelAccessor bhs, final BoundingBox cic, final Random random, final int integer4, final int integer5, final int integer6, final ResourceLocation qv) {
            final BlockPos ew9 = new BlockPos(this.getWorldX(integer4, integer6), this.getWorldY(integer5), this.getWorldZ(integer4, integer6));
            if (cic.isInside(ew9) && bhs.getBlockState(ew9).isAir() && !bhs.getBlockState(ew9.below()).isAir()) {
                final BlockState bvt10 = ((AbstractStateHolder<O, BlockState>)Blocks.RAIL.defaultBlockState()).<Comparable, RailShape>setValue((Property<Comparable>)RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
                this.placeBlock(bhs, bvt10, integer4, integer5, integer6, cic);
                final MinecartChest axy11 = new MinecartChest(bhs.getLevel(), ew9.getX() + 0.5f, ew9.getY() + 0.5f, ew9.getZ() + 0.5f);
                axy11.setLootTable(qv, random.nextLong());
                bhs.addFreshEntity(axy11);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.edgesLiquid(bhs, cic)) {
                return false;
            }
            final int integer6 = 0;
            final int integer7 = 2;
            final int integer8 = 0;
            final int integer9 = 2;
            final int integer10 = this.numSections * 5 - 1;
            final BlockState bvt11 = this.getPlanksBlock();
            this.generateBox(bhs, cic, 0, 0, 0, 2, 1, integer10, MineShaftCorridor.CAVE_AIR, MineShaftCorridor.CAVE_AIR, false);
            this.generateMaybeBox(bhs, cic, random, 0.8f, 0, 2, 0, 2, 2, integer10, MineShaftCorridor.CAVE_AIR, MineShaftCorridor.CAVE_AIR, false, false);
            if (this.spiderCorridor) {
                this.generateMaybeBox(bhs, cic, random, 0.6f, 0, 0, 0, 2, 1, integer10, Blocks.COBWEB.defaultBlockState(), MineShaftCorridor.CAVE_AIR, false, true);
            }
            for (int integer11 = 0; integer11 < this.numSections; ++integer11) {
                final int integer12 = 2 + integer11 * 5;
                this.placeSupport(bhs, cic, 0, 0, integer12, 2, 2, random);
                this.placeCobWeb(bhs, cic, random, 0.1f, 0, 2, integer12 - 1);
                this.placeCobWeb(bhs, cic, random, 0.1f, 2, 2, integer12 - 1);
                this.placeCobWeb(bhs, cic, random, 0.1f, 0, 2, integer12 + 1);
                this.placeCobWeb(bhs, cic, random, 0.1f, 2, 2, integer12 + 1);
                this.placeCobWeb(bhs, cic, random, 0.05f, 0, 2, integer12 - 2);
                this.placeCobWeb(bhs, cic, random, 0.05f, 2, 2, integer12 - 2);
                this.placeCobWeb(bhs, cic, random, 0.05f, 0, 2, integer12 + 2);
                this.placeCobWeb(bhs, cic, random, 0.05f, 2, 2, integer12 + 2);
                if (random.nextInt(100) == 0) {
                    this.createChest(bhs, cic, random, 2, 0, integer12 - 1, BuiltInLootTables.ABANDONED_MINESHAFT);
                }
                if (random.nextInt(100) == 0) {
                    this.createChest(bhs, cic, random, 0, 0, integer12 + 1, BuiltInLootTables.ABANDONED_MINESHAFT);
                }
                if (this.spiderCorridor && !this.hasPlacedSpider) {
                    final int integer13 = this.getWorldY(0);
                    final int integer14 = integer12 - 1 + random.nextInt(3);
                    final int integer15 = this.getWorldX(1, integer14);
                    final int integer16 = this.getWorldZ(1, integer14);
                    final BlockPos ew18 = new BlockPos(integer15, integer13, integer16);
                    if (cic.isInside(ew18) && this.isInterior(bhs, 1, 0, integer14, cic)) {
                        this.hasPlacedSpider = true;
                        bhs.setBlock(ew18, Blocks.SPAWNER.defaultBlockState(), 2);
                        final BlockEntity btw19 = bhs.getBlockEntity(ew18);
                        if (btw19 instanceof SpawnerBlockEntity) {
                            ((SpawnerBlockEntity)btw19).getSpawner().setEntityId(EntityType.CAVE_SPIDER);
                        }
                    }
                }
            }
            for (int integer11 = 0; integer11 <= 2; ++integer11) {
                for (int integer12 = 0; integer12 <= integer10; ++integer12) {
                    final int integer13 = -1;
                    final BlockState bvt12 = this.getBlock(bhs, integer11, -1, integer12, cic);
                    if (bvt12.isAir() && this.isInterior(bhs, integer11, -1, integer12, cic)) {
                        final int integer15 = -1;
                        this.placeBlock(bhs, bvt11, integer11, -1, integer12, cic);
                    }
                }
            }
            if (this.hasRails) {
                final BlockState bvt13 = ((AbstractStateHolder<O, BlockState>)Blocks.RAIL.defaultBlockState()).<RailShape, RailShape>setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);
                for (int integer12 = 0; integer12 <= integer10; ++integer12) {
                    final BlockState bvt14 = this.getBlock(bhs, 1, -1, integer12, cic);
                    if (!bvt14.isAir() && bvt14.isSolidRender(bhs, new BlockPos(this.getWorldX(1, integer12), this.getWorldY(-1), this.getWorldZ(1, integer12)))) {
                        final float float15 = this.isInterior(bhs, 1, 0, integer12, cic) ? 0.7f : 0.9f;
                        this.maybeGenerateBlock(bhs, cic, random, float15, 1, 0, integer12, bvt13);
                    }
                }
            }
            return true;
        }
        
        private void placeSupport(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final Random random) {
            if (!this.isSupportingBox(bhs, cic, integer3, integer7, integer6, integer5)) {
                return;
            }
            final BlockState bvt10 = this.getPlanksBlock();
            final BlockState bvt11 = this.getFenceBlock();
            this.generateBox(bhs, cic, integer3, integer4, integer5, integer3, integer6 - 1, integer5, ((AbstractStateHolder<O, BlockState>)bvt11).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), MineShaftCorridor.CAVE_AIR, false);
            this.generateBox(bhs, cic, integer7, integer4, integer5, integer7, integer6 - 1, integer5, ((AbstractStateHolder<O, BlockState>)bvt11).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), MineShaftCorridor.CAVE_AIR, false);
            if (random.nextInt(4) == 0) {
                this.generateBox(bhs, cic, integer3, integer6, integer5, integer3, integer6, integer5, bvt10, MineShaftCorridor.CAVE_AIR, false);
                this.generateBox(bhs, cic, integer7, integer6, integer5, integer7, integer6, integer5, bvt10, MineShaftCorridor.CAVE_AIR, false);
            }
            else {
                this.generateBox(bhs, cic, integer3, integer6, integer5, integer7, integer6, integer5, bvt10, MineShaftCorridor.CAVE_AIR, false);
                this.maybeGenerateBlock(bhs, cic, random, 0.05f, integer3 + 1, integer6, integer5 - 1, ((AbstractStateHolder<O, BlockState>)Blocks.WALL_TORCH.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)WallTorchBlock.FACING, Direction.NORTH));
                this.maybeGenerateBlock(bhs, cic, random, 0.05f, integer3 + 1, integer6, integer5 + 1, ((AbstractStateHolder<O, BlockState>)Blocks.WALL_TORCH.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)WallTorchBlock.FACING, Direction.SOUTH));
            }
        }
        
        private void placeCobWeb(final LevelAccessor bhs, final BoundingBox cic, final Random random, final float float4, final int integer5, final int integer6, final int integer7) {
            if (this.isInterior(bhs, integer5, integer6, integer7, cic)) {
                this.maybeGenerateBlock(bhs, cic, random, float4, integer5, integer6, integer7, Blocks.COBWEB.defaultBlockState());
            }
        }
    }
    
    public static class MineShaftCrossing extends MineShaftPiece {
        private final Direction direction;
        private final boolean isTwoFloored;
        
        public MineShaftCrossing(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.MINE_SHAFT_CROSSING, id);
            this.isTwoFloored = id.getBoolean("tf");
            this.direction = Direction.from2DDataValue(id.getInt("D"));
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putBoolean("tf", this.isTwoFloored);
            id.putInt("D", this.direction.get2DDataValue());
        }
        
        public MineShaftCrossing(final int integer, final BoundingBox cic, @Nullable final Direction fb, final MineshaftFeature.Type b) {
            super(StructurePieceType.MINE_SHAFT_CROSSING, integer, b);
            this.direction = fb;
            this.boundingBox = cic;
            this.isTwoFloored = (cic.getYSpan() > 3);
        }
        
        public static BoundingBox findCrossing(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb) {
            final BoundingBox cic7 = new BoundingBox(integer3, integer4, integer5, integer3, integer4 + 3 - 1, integer5);
            if (random.nextInt(4) == 0) {
                final BoundingBox boundingBox = cic7;
                boundingBox.y1 += 4;
            }
            switch (fb) {
                default: {
                    cic7.x0 = integer3 - 1;
                    cic7.x1 = integer3 + 3;
                    cic7.z0 = integer5 - 4;
                    break;
                }
                case SOUTH: {
                    cic7.x0 = integer3 - 1;
                    cic7.x1 = integer3 + 3;
                    cic7.z1 = integer5 + 3 + 1;
                    break;
                }
                case WEST: {
                    cic7.x0 = integer3 - 4;
                    cic7.z0 = integer5 - 1;
                    cic7.z1 = integer5 + 3;
                    break;
                }
                case EAST: {
                    cic7.x1 = integer3 + 3 + 1;
                    cic7.z0 = integer5 - 1;
                    cic7.z1 = integer5 + 3;
                    break;
                }
            }
            if (StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return cic7;
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            final int integer5 = this.getGenDepth();
            switch (this.direction) {
                default: {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, integer5);
                    break;
                }
                case SOUTH: {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, integer5);
                    break;
                }
                case WEST: {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, integer5);
                    break;
                }
                case EAST: {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                    generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, integer5);
                    break;
                }
            }
            if (this.isTwoFloored) {
                if (random.nextBoolean()) {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                }
                if (random.nextBoolean()) {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.WEST, integer5);
                }
                if (random.nextBoolean()) {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.EAST, integer5);
                }
                if (random.nextBoolean()) {
                    generateAndAddPiece(civ, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                }
            }
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.edgesLiquid(bhs, cic)) {
                return false;
            }
            final BlockState bvt6 = this.getPlanksBlock();
            if (this.isTwoFloored) {
                this.generateBox(bhs, cic, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
                this.generateBox(bhs, cic, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1 - 1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
                this.generateBox(bhs, cic, this.boundingBox.x0 + 1, this.boundingBox.y1 - 2, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
                this.generateBox(bhs, cic, this.boundingBox.x0, this.boundingBox.y1 - 2, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
                this.generateBox(bhs, cic, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3, this.boundingBox.z0 + 1, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3, this.boundingBox.z1 - 1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
            }
            else {
                this.generateBox(bhs, cic, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
                this.generateBox(bhs, cic, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, MineShaftCrossing.CAVE_AIR, MineShaftCrossing.CAVE_AIR, false);
            }
            this.placeSupportPillar(bhs, cic, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(bhs, cic, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            this.placeSupportPillar(bhs, cic, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(bhs, cic, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            for (int integer7 = this.boundingBox.x0; integer7 <= this.boundingBox.x1; ++integer7) {
                for (int integer8 = this.boundingBox.z0; integer8 <= this.boundingBox.z1; ++integer8) {
                    if (this.getBlock(bhs, integer7, this.boundingBox.y0 - 1, integer8, cic).isAir() && this.isInterior(bhs, integer7, this.boundingBox.y0 - 1, integer8, cic)) {
                        this.placeBlock(bhs, bvt6, integer7, this.boundingBox.y0 - 1, integer8, cic);
                    }
                }
            }
            return true;
        }
        
        private void placeSupportPillar(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6) {
            if (!this.getBlock(bhs, integer3, integer6 + 1, integer5, cic).isAir()) {
                this.generateBox(bhs, cic, integer3, integer4, integer5, integer3, integer6, integer5, this.getPlanksBlock(), MineShaftCrossing.CAVE_AIR, false);
            }
        }
    }
    
    public static class MineShaftStairs extends MineShaftPiece {
        public MineShaftStairs(final int integer, final BoundingBox cic, final Direction fb, final MineshaftFeature.Type b) {
            super(StructurePieceType.MINE_SHAFT_STAIRS, integer, b);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public MineShaftStairs(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.MINE_SHAFT_STAIRS, id);
        }
        
        public static BoundingBox findStairs(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb) {
            final BoundingBox cic7 = new BoundingBox(integer3, integer4 - 5, integer5, integer3, integer4 + 3 - 1, integer5);
            switch (fb) {
                default: {
                    cic7.x1 = integer3 + 3 - 1;
                    cic7.z0 = integer5 - 8;
                    break;
                }
                case SOUTH: {
                    cic7.x1 = integer3 + 3 - 1;
                    cic7.z1 = integer5 + 8;
                    break;
                }
                case WEST: {
                    cic7.x0 = integer3 - 8;
                    cic7.z1 = integer5 + 3 - 1;
                    break;
                }
                case EAST: {
                    cic7.x1 = integer3 + 8;
                    cic7.z1 = integer5 + 3 - 1;
                    break;
                }
            }
            if (StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return cic7;
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            final int integer5 = this.getGenDepth();
            final Direction fb6 = this.getOrientation();
            if (fb6 != null) {
                switch (fb6) {
                    default: {
                        generateAndAddPiece(civ, list, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, integer5);
                        break;
                    }
                    case SOUTH: {
                        generateAndAddPiece(civ, list, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, integer5);
                        break;
                    }
                    case WEST: {
                        generateAndAddPiece(civ, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, Direction.WEST, integer5);
                        break;
                    }
                    case EAST: {
                        generateAndAddPiece(civ, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, Direction.EAST, integer5);
                        break;
                    }
                }
            }
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (this.edgesLiquid(bhs, cic)) {
                return false;
            }
            this.generateBox(bhs, cic, 0, 5, 0, 2, 7, 1, MineShaftStairs.CAVE_AIR, MineShaftStairs.CAVE_AIR, false);
            this.generateBox(bhs, cic, 0, 0, 7, 2, 2, 8, MineShaftStairs.CAVE_AIR, MineShaftStairs.CAVE_AIR, false);
            for (int integer6 = 0; integer6 < 5; ++integer6) {
                this.generateBox(bhs, cic, 0, 5 - integer6 - ((integer6 < 4) ? 1 : 0), 2 + integer6, 2, 7 - integer6, 2 + integer6, MineShaftStairs.CAVE_AIR, MineShaftStairs.CAVE_AIR, false);
            }
            return true;
        }
    }
}
