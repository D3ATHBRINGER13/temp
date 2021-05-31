package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import java.util.Random;

public class JunglePyramidPiece extends ScatteredFeaturePiece {
    private boolean placedMainChest;
    private boolean placedHiddenChest;
    private boolean placedTrap1;
    private boolean placedTrap2;
    private static final MossStoneSelector STONE_SELECTOR;
    
    public JunglePyramidPiece(final Random random, final int integer2, final int integer3) {
        super(StructurePieceType.JUNGLE_PYRAMID_PIECE, random, integer2, 64, integer3, 12, 10, 15);
    }
    
    public JunglePyramidPiece(final StructureManager cjp, final CompoundTag id) {
        super(StructurePieceType.JUNGLE_PYRAMID_PIECE, id);
        this.placedMainChest = id.getBoolean("placedMainChest");
        this.placedHiddenChest = id.getBoolean("placedHiddenChest");
        this.placedTrap1 = id.getBoolean("placedTrap1");
        this.placedTrap2 = id.getBoolean("placedTrap2");
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("placedMainChest", this.placedMainChest);
        id.putBoolean("placedHiddenChest", this.placedHiddenChest);
        id.putBoolean("placedTrap1", this.placedTrap1);
        id.putBoolean("placedTrap2", this.placedTrap2);
    }
    
    @Override
    public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
        if (!this.updateAverageGroundHeight(bhs, cic, 0)) {
            return false;
        }
        this.generateBox(bhs, cic, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 2, 1, 2, 9, 2, 2, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 2, 1, 12, 9, 2, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 2, 1, 3, 2, 2, 11, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 9, 1, 3, 9, 2, 11, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 1, 3, 1, 10, 6, 1, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 1, 3, 13, 10, 6, 13, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 1, 3, 2, 1, 6, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 10, 3, 2, 10, 6, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 2, 3, 2, 9, 3, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 2, 6, 2, 9, 6, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 3, 7, 3, 8, 7, 11, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 4, 8, 4, 7, 8, 10, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateAirBox(bhs, cic, 3, 1, 3, 8, 2, 11);
        this.generateAirBox(bhs, cic, 4, 3, 6, 7, 3, 9);
        this.generateAirBox(bhs, cic, 2, 4, 2, 9, 5, 12);
        this.generateAirBox(bhs, cic, 4, 6, 5, 7, 6, 9);
        this.generateAirBox(bhs, cic, 5, 7, 6, 6, 7, 8);
        this.generateAirBox(bhs, cic, 5, 1, 2, 6, 2, 2);
        this.generateAirBox(bhs, cic, 5, 2, 12, 6, 2, 12);
        this.generateAirBox(bhs, cic, 5, 5, 1, 6, 5, 1);
        this.generateAirBox(bhs, cic, 5, 5, 13, 6, 5, 13);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 1, 5, 5, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 10, 5, 5, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 1, 5, 9, cic);
        this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), 10, 5, 9, cic);
        for (int integer6 = 0; integer6 <= 14; integer6 += 14) {
            this.generateBox(bhs, cic, 2, 4, integer6, 2, 5, integer6, false, random, JunglePyramidPiece.STONE_SELECTOR);
            this.generateBox(bhs, cic, 4, 4, integer6, 4, 5, integer6, false, random, JunglePyramidPiece.STONE_SELECTOR);
            this.generateBox(bhs, cic, 7, 4, integer6, 7, 5, integer6, false, random, JunglePyramidPiece.STONE_SELECTOR);
            this.generateBox(bhs, cic, 9, 4, integer6, 9, 5, integer6, false, random, JunglePyramidPiece.STONE_SELECTOR);
        }
        this.generateBox(bhs, cic, 5, 6, 0, 6, 6, 0, false, random, JunglePyramidPiece.STONE_SELECTOR);
        for (int integer6 = 0; integer6 <= 11; integer6 += 11) {
            for (int integer7 = 2; integer7 <= 12; integer7 += 2) {
                this.generateBox(bhs, cic, integer6, 4, integer7, integer6, 5, integer7, false, random, JunglePyramidPiece.STONE_SELECTOR);
            }
            this.generateBox(bhs, cic, integer6, 6, 5, integer6, 6, 5, false, random, JunglePyramidPiece.STONE_SELECTOR);
            this.generateBox(bhs, cic, integer6, 6, 9, integer6, 6, 9, false, random, JunglePyramidPiece.STONE_SELECTOR);
        }
        this.generateBox(bhs, cic, 2, 7, 2, 2, 9, 2, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 9, 7, 2, 9, 9, 2, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 2, 7, 12, 2, 9, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 9, 7, 12, 9, 9, 12, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 4, 9, 4, 4, 9, 4, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 7, 9, 4, 7, 9, 4, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 4, 9, 10, 4, 9, 10, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 7, 9, 10, 7, 9, 10, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 5, 9, 7, 6, 9, 7, false, random, JunglePyramidPiece.STONE_SELECTOR);
        final BlockState bvt6 = ((AbstractStateHolder<O, BlockState>)Blocks.COBBLESTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.EAST);
        final BlockState bvt7 = ((AbstractStateHolder<O, BlockState>)Blocks.COBBLESTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.WEST);
        final BlockState bvt8 = ((AbstractStateHolder<O, BlockState>)Blocks.COBBLESTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.SOUTH);
        final BlockState bvt9 = ((AbstractStateHolder<O, BlockState>)Blocks.COBBLESTONE_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.NORTH);
        this.placeBlock(bhs, bvt9, 5, 9, 6, cic);
        this.placeBlock(bhs, bvt9, 6, 9, 6, cic);
        this.placeBlock(bhs, bvt8, 5, 9, 8, cic);
        this.placeBlock(bhs, bvt8, 6, 9, 8, cic);
        this.placeBlock(bhs, bvt9, 4, 0, 0, cic);
        this.placeBlock(bhs, bvt9, 5, 0, 0, cic);
        this.placeBlock(bhs, bvt9, 6, 0, 0, cic);
        this.placeBlock(bhs, bvt9, 7, 0, 0, cic);
        this.placeBlock(bhs, bvt9, 4, 1, 8, cic);
        this.placeBlock(bhs, bvt9, 4, 2, 9, cic);
        this.placeBlock(bhs, bvt9, 4, 3, 10, cic);
        this.placeBlock(bhs, bvt9, 7, 1, 8, cic);
        this.placeBlock(bhs, bvt9, 7, 2, 9, cic);
        this.placeBlock(bhs, bvt9, 7, 3, 10, cic);
        this.generateBox(bhs, cic, 4, 1, 9, 4, 1, 9, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 7, 1, 9, 7, 1, 9, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 4, 1, 10, 7, 2, 10, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 5, 4, 5, 6, 4, 5, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.placeBlock(bhs, bvt6, 4, 4, 5, cic);
        this.placeBlock(bhs, bvt7, 7, 4, 5, cic);
        for (int integer8 = 0; integer8 < 4; ++integer8) {
            this.placeBlock(bhs, bvt8, 5, 0 - integer8, 6 + integer8, cic);
            this.placeBlock(bhs, bvt8, 6, 0 - integer8, 6 + integer8, cic);
            this.generateAirBox(bhs, cic, 5, 0 - integer8, 7 + integer8, 6, 0 - integer8, 9 + integer8);
        }
        this.generateAirBox(bhs, cic, 1, -3, 12, 10, -1, 13);
        this.generateAirBox(bhs, cic, 1, -3, 1, 3, -1, 13);
        this.generateAirBox(bhs, cic, 1, -3, 1, 9, -1, 5);
        for (int integer8 = 1; integer8 <= 13; integer8 += 2) {
            this.generateBox(bhs, cic, 1, -3, integer8, 1, -2, integer8, false, random, JunglePyramidPiece.STONE_SELECTOR);
        }
        for (int integer8 = 2; integer8 <= 12; integer8 += 2) {
            this.generateBox(bhs, cic, 1, -1, integer8, 3, -1, integer8, false, random, JunglePyramidPiece.STONE_SELECTOR);
        }
        this.generateBox(bhs, cic, 2, -2, 1, 5, -2, 1, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 7, -2, 1, 9, -2, 1, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 6, -3, 1, 6, -3, 1, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 6, -1, 1, 6, -1, 1, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE_HOOK.defaultBlockState()).setValue((Property<Comparable>)TripWireHookBlock.FACING, Direction.EAST)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, true), 1, -3, 8, cic);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE_HOOK.defaultBlockState()).setValue((Property<Comparable>)TripWireHookBlock.FACING, Direction.WEST)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, true), 4, -3, 8, cic);
        this.placeBlock(bhs, ((((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE.defaultBlockState()).setValue((Property<Comparable>)TripWireBlock.EAST, true)).setValue((Property<Comparable>)TripWireBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.ATTACHED, true), 2, -3, 8, cic);
        this.placeBlock(bhs, ((((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE.defaultBlockState()).setValue((Property<Comparable>)TripWireBlock.EAST, true)).setValue((Property<Comparable>)TripWireBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.ATTACHED, true), 3, -3, 8, cic);
        final BlockState bvt10 = (((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 5, -3, 7, cic);
        this.placeBlock(bhs, bvt10, 5, -3, 6, cic);
        this.placeBlock(bhs, bvt10, 5, -3, 5, cic);
        this.placeBlock(bhs, bvt10, 5, -3, 4, cic);
        this.placeBlock(bhs, bvt10, 5, -3, 3, cic);
        this.placeBlock(bhs, bvt10, 5, -3, 2, cic);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 5, -3, 1, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE), 4, -3, 1, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3, -3, 1, cic);
        if (!this.placedTrap1) {
            this.placedTrap1 = this.createDispenser(bhs, cic, random, 3, -2, 1, Direction.NORTH, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
        }
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.SOUTH, true), 3, -2, 2, cic);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE_HOOK.defaultBlockState()).setValue((Property<Comparable>)TripWireHookBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, true), 7, -3, 1, cic);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE_HOOK.defaultBlockState()).setValue((Property<Comparable>)TripWireHookBlock.FACING, Direction.SOUTH)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, true), 7, -3, 5, cic);
        this.placeBlock(bhs, ((((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE.defaultBlockState()).setValue((Property<Comparable>)TripWireBlock.NORTH, true)).setValue((Property<Comparable>)TripWireBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.ATTACHED, true), 7, -3, 2, cic);
        this.placeBlock(bhs, ((((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE.defaultBlockState()).setValue((Property<Comparable>)TripWireBlock.NORTH, true)).setValue((Property<Comparable>)TripWireBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.ATTACHED, true), 7, -3, 3, cic);
        this.placeBlock(bhs, ((((AbstractStateHolder<O, BlockState>)Blocks.TRIPWIRE.defaultBlockState()).setValue((Property<Comparable>)TripWireBlock.NORTH, true)).setValue((Property<Comparable>)TripWireBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.ATTACHED, true), 7, -3, 4, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE), 8, -3, 6, cic);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 6, cic);
        this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, RedstoneSide.UP), 9, -3, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 4, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE), 9, -2, 4, cic);
        if (!this.placedTrap2) {
            this.placedTrap2 = this.createDispenser(bhs, cic, random, 9, -2, 3, Direction.WEST, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
        }
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.EAST, true), 8, -1, 3, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.EAST, true), 8, -2, 3, cic);
        if (!this.placedMainChest) {
            this.placedMainChest = this.createChest(bhs, cic, random, 8, -3, 3, BuiltInLootTables.JUNGLE_TEMPLE);
        }
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 2, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 1, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 4, -3, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -2, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -1, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 6, -3, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -2, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -1, 5, cic);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 5, cic);
        this.generateBox(bhs, cic, 9, -1, 1, 9, -1, 5, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateAirBox(bhs, cic, 8, -3, 8, 10, -1, 10);
        this.placeBlock(bhs, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -2, 11, cic);
        this.placeBlock(bhs, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -2, 11, cic);
        this.placeBlock(bhs, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -2, 11, cic);
        final BlockState bvt11 = (((AbstractStateHolder<O, BlockState>)Blocks.LEVER.defaultBlockState()).setValue((Property<Comparable>)LeverBlock.FACING, Direction.NORTH)).<AttachFace, AttachFace>setValue(LeverBlock.FACE, AttachFace.WALL);
        this.placeBlock(bhs, bvt11, 8, -2, 12, cic);
        this.placeBlock(bhs, bvt11, 9, -2, 12, cic);
        this.placeBlock(bhs, bvt11, 10, -2, 12, cic);
        this.generateBox(bhs, cic, 8, -3, 8, 8, -3, 10, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.generateBox(bhs, cic, 10, -3, 8, 10, -3, 10, false, random, JunglePyramidPiece.STONE_SELECTOR);
        this.placeBlock(bhs, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -2, 9, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE), 8, -2, 9, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REDSTONE_WIRE.defaultBlockState()).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 8, -2, 10, cic);
        this.placeBlock(bhs, Blocks.REDSTONE_WIRE.defaultBlockState(), 10, -1, 9, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.STICKY_PISTON.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)PistonBaseBlock.FACING, Direction.UP), 9, -2, 8, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.STICKY_PISTON.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)PistonBaseBlock.FACING, Direction.WEST), 10, -2, 8, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.STICKY_PISTON.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)PistonBaseBlock.FACING, Direction.WEST), 10, -1, 8, cic);
        this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.REPEATER.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, cic);
        if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.createChest(bhs, cic, random, 9, -3, 10, BuiltInLootTables.JUNGLE_TEMPLE);
        }
        return true;
    }
    
    static {
        STONE_SELECTOR = new MossStoneSelector();
    }
    
    static class MossStoneSelector extends BlockSelector {
        private MossStoneSelector() {
        }
        
        @Override
        public void next(final Random random, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
            if (random.nextFloat() < 0.4f) {
                this.next = Blocks.COBBLESTONE.defaultBlockState();
            }
            else {
                this.next = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            }
        }
    }
}
