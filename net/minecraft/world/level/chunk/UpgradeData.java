package net.minecraft.world.level.chunk;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Blocks;
import com.google.common.collect.Sets;
import java.util.IdentityHashMap;
import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import java.util.Map;
import java.util.EnumSet;
import net.minecraft.core.Direction8;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER;
    public static final UpgradeData EMPTY;
    private static final Direction8[] DIRECTIONS;
    private final EnumSet<Direction8> sides;
    private final int[][] index;
    private static final Map<Block, BlockFixer> MAP;
    private static final Set<BlockFixer> CHUNKY_FIXERS;
    
    private UpgradeData() {
        this.sides = (EnumSet<Direction8>)EnumSet.noneOf((Class)Direction8.class);
        this.index = new int[16][];
    }
    
    public UpgradeData(final CompoundTag id) {
        this();
        if (id.contains("Indices", 10)) {
            final CompoundTag id2 = id.getCompound("Indices");
            for (int integer4 = 0; integer4 < this.index.length; ++integer4) {
                final String string5 = String.valueOf(integer4);
                if (id2.contains(string5, 11)) {
                    this.index[integer4] = id2.getIntArray(string5);
                }
            }
        }
        final int integer5 = id.getInt("Sides");
        for (final Direction8 fc7 : Direction8.values()) {
            if ((integer5 & 1 << fc7.ordinal()) != 0x0) {
                this.sides.add(fc7);
            }
        }
    }
    
    public void upgrade(final LevelChunk bxt) {
        this.upgradeInside(bxt);
        for (final Direction8 fc6 : UpgradeData.DIRECTIONS) {
            upgradeSides(bxt, fc6);
        }
        final Level bhr3 = bxt.getLevel();
        UpgradeData.CHUNKY_FIXERS.forEach(a -> a.processChunk(bhr3));
    }
    
    private static void upgradeSides(final LevelChunk bxt, final Direction8 fc) {
        final Level bhr3 = bxt.getLevel();
        if (!bxt.getUpgradeData().sides.remove(fc)) {
            return;
        }
        final Set<Direction> set4 = fc.getDirections();
        final int integer5 = 0;
        final int integer6 = 15;
        final boolean boolean7 = set4.contains(Direction.EAST);
        final boolean boolean8 = set4.contains(Direction.WEST);
        final boolean boolean9 = set4.contains(Direction.SOUTH);
        final boolean boolean10 = set4.contains(Direction.NORTH);
        final boolean boolean11 = set4.size() == 1;
        final ChunkPos bhd12 = bxt.getPos();
        final int integer7 = bhd12.getMinBlockX() + ((boolean11 && (boolean10 || boolean9)) ? 1 : (boolean8 ? 0 : 15));
        final int integer8 = bhd12.getMinBlockX() + ((boolean11 && (boolean10 || boolean9)) ? 14 : (boolean8 ? 0 : 15));
        final int integer9 = bhd12.getMinBlockZ() + ((boolean11 && (boolean7 || boolean8)) ? 1 : (boolean10 ? 0 : 15));
        final int integer10 = bhd12.getMinBlockZ() + ((boolean11 && (boolean7 || boolean8)) ? 14 : (boolean10 ? 0 : 15));
        final Direction[] arr17 = Direction.values();
        final BlockPos.MutableBlockPos a18 = new BlockPos.MutableBlockPos();
        for (final BlockPos ew20 : BlockPos.betweenClosed(integer7, 0, integer9, integer8, bhr3.getMaxBuildHeight() - 1, integer10)) {
            BlockState bvt22;
            final BlockState bvt21 = bvt22 = bhr3.getBlockState(ew20);
            for (final Direction fb26 : arr17) {
                a18.set(ew20).move(fb26);
                bvt22 = updateState(bvt22, fb26, bhr3, ew20, a18);
            }
            Block.updateOrDestroy(bvt21, bvt22, bhr3, ew20, 18);
        }
    }
    
    private static BlockState updateState(final BlockState bvt, final Direction fb, final LevelAccessor bhs, final BlockPos ew4, final BlockPos ew5) {
        return ((BlockFixer)UpgradeData.MAP.getOrDefault(bvt.getBlock(), BlockFixers.DEFAULT)).updateShape(bvt, fb, bhs.getBlockState(ew5), bhs, ew4, ew5);
    }
    
    private void upgradeInside(final LevelChunk bxt) {
        try (final BlockPos.PooledMutableBlockPos b3 = BlockPos.PooledMutableBlockPos.acquire();
             final BlockPos.PooledMutableBlockPos b4 = BlockPos.PooledMutableBlockPos.acquire()) {
            final ChunkPos bhd7 = bxt.getPos();
            final LevelAccessor bhs8 = bxt.getLevel();
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                final LevelChunkSection bxu10 = bxt.getSections()[integer9];
                final int[] arr11 = this.index[integer9];
                this.index[integer9] = null;
                if (bxu10 != null && arr11 != null) {
                    if (arr11.length > 0) {
                        final Direction[] arr12 = Direction.values();
                        final PalettedContainer<BlockState> bya13 = bxu10.getStates();
                        for (final int integer10 : arr11) {
                            final int integer11 = integer10 & 0xF;
                            final int integer12 = integer10 >> 8 & 0xF;
                            final int integer13 = integer10 >> 4 & 0xF;
                            b3.set(bhd7.getMinBlockX() + integer11, (integer9 << 4) + integer12, bhd7.getMinBlockZ() + integer13);
                            BlockState bvt22;
                            final BlockState bvt21 = bvt22 = bya13.get(integer10);
                            for (final Direction fb26 : arr12) {
                                b4.set(b3).move(fb26);
                                if (b3.getX() >> 4 == bhd7.x) {
                                    if (b3.getZ() >> 4 == bhd7.z) {
                                        bvt22 = updateState(bvt22, fb26, bhs8, b3, b4);
                                    }
                                }
                            }
                            Block.updateOrDestroy(bvt21, bvt22, bhs8, b3, 18);
                        }
                    }
                }
            }
            for (int integer9 = 0; integer9 < this.index.length; ++integer9) {
                if (this.index[integer9] != null) {
                    UpgradeData.LOGGER.warn("Discarding update data for section {} for chunk ({} {})", integer9, bhd7.x, bhd7.z);
                }
                this.index[integer9] = null;
            }
        }
    }
    
    public boolean isEmpty() {
        for (final int[] arr5 : this.index) {
            if (arr5 != null) {
                return false;
            }
        }
        return this.sides.isEmpty();
    }
    
    public CompoundTag write() {
        final CompoundTag id2 = new CompoundTag();
        final CompoundTag id3 = new CompoundTag();
        for (int integer4 = 0; integer4 < this.index.length; ++integer4) {
            final String string5 = String.valueOf(integer4);
            if (this.index[integer4] != null && this.index[integer4].length != 0) {
                id3.putIntArray(string5, this.index[integer4]);
            }
        }
        if (!id3.isEmpty()) {
            id2.put("Indices", (Tag)id3);
        }
        int integer4 = 0;
        for (final Direction8 fc6 : this.sides) {
            integer4 |= 1 << fc6.ordinal();
        }
        id2.putByte("Sides", (byte)integer4);
        return id2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY = new UpgradeData();
        DIRECTIONS = Direction8.values();
        MAP = (Map)new IdentityHashMap();
        CHUNKY_FIXERS = (Set)Sets.newHashSet();
    }
    
    public interface BlockFixer {
        BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6);
        
        default void processChunk(final LevelAccessor bhs) {
        }
    }
    
    enum BlockFixers implements BlockFixer {
        BLACKLIST(new Block[] { Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN }) {
            public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
                return bvt1;
            }
        }, 
        DEFAULT(new Block[0]) {
            public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
                return bvt1.updateShape(fb, bhs.getBlockState(ew6), bhs, ew5, ew6);
            }
        }, 
        CHEST(new Block[] { Blocks.CHEST, Blocks.TRAPPED_CHEST }) {
            public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
                if (bvt3.getBlock() == bvt1.getBlock() && fb.getAxis().isHorizontal() && bvt1.<ChestType>getValue(ChestBlock.TYPE) == ChestType.SINGLE && bvt3.<ChestType>getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
                    final Direction fb2 = bvt1.<Direction>getValue((Property<Direction>)ChestBlock.FACING);
                    if (fb.getAxis() != fb2.getAxis() && fb2 == bvt3.<Comparable>getValue((Property<Comparable>)ChestBlock.FACING)) {
                        final ChestType bwm9 = (fb == fb2.getClockWise()) ? ChestType.LEFT : ChestType.RIGHT;
                        bhs.setBlock(ew6, ((AbstractStateHolder<O, BlockState>)bvt3).<ChestType, ChestType>setValue(ChestBlock.TYPE, bwm9.getOpposite()), 18);
                        if (fb2 == Direction.NORTH || fb2 == Direction.EAST) {
                            final BlockEntity btw10 = bhs.getBlockEntity(ew5);
                            final BlockEntity btw11 = bhs.getBlockEntity(ew6);
                            if (btw10 instanceof ChestBlockEntity && btw11 instanceof ChestBlockEntity) {
                                ChestBlockEntity.swapContents((ChestBlockEntity)btw10, (ChestBlockEntity)btw11);
                            }
                        }
                        return ((AbstractStateHolder<O, BlockState>)bvt1).<ChestType, ChestType>setValue(ChestBlock.TYPE, bwm9);
                    }
                }
                return bvt1;
            }
        }, 
        LEAVES(true, new Block[] { Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES }) {
            private final ThreadLocal<List<ObjectSet<BlockPos>>> queue;
            
            {
                this.queue = (ThreadLocal<List<ObjectSet<BlockPos>>>)ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));
            }
            
            public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
                final BlockState bvt4 = bvt1.updateShape(fb, bhs.getBlockState(ew6), bhs, ew5, ew6);
                if (bvt1 != bvt4) {
                    final int integer9 = bvt4.<Integer>getValue((Property<Integer>)BlockStateProperties.DISTANCE);
                    final List<ObjectSet<BlockPos>> list10 = (List<ObjectSet<BlockPos>>)this.queue.get();
                    if (list10.isEmpty()) {
                        for (int integer10 = 0; integer10 < 7; ++integer10) {
                            list10.add(new ObjectOpenHashSet());
                        }
                    }
                    ((ObjectSet)list10.get(integer9)).add(ew5.immutable());
                }
                return bvt1;
            }
            
            public void processChunk(final LevelAccessor bhs) {
                final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos();
                final List<ObjectSet<BlockPos>> list4 = (List<ObjectSet<BlockPos>>)this.queue.get();
                for (int integer5 = 2; integer5 < list4.size(); ++integer5) {
                    final int integer6 = integer5 - 1;
                    final ObjectSet<BlockPos> objectSet7 = (ObjectSet<BlockPos>)list4.get(integer6);
                    final ObjectSet<BlockPos> objectSet8 = (ObjectSet<BlockPos>)list4.get(integer5);
                    for (final BlockPos ew10 : objectSet7) {
                        final BlockState bvt11 = bhs.getBlockState(ew10);
                        if (bvt11.<Integer>getValue((Property<Integer>)BlockStateProperties.DISTANCE) < integer6) {
                            continue;
                        }
                        bhs.setBlock(ew10, ((AbstractStateHolder<O, BlockState>)bvt11).<Comparable, Integer>setValue((Property<Comparable>)BlockStateProperties.DISTANCE, integer6), 18);
                        if (integer5 == 7) {
                            continue;
                        }
                        for (final Direction fb15 : UpgradeData$BlockFixers$4.DIRECTIONS) {
                            a3.set(ew10).move(fb15);
                            final BlockState bvt12 = bhs.getBlockState(a3);
                            if (bvt12.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.DISTANCE) && bvt11.<Integer>getValue((Property<Integer>)BlockStateProperties.DISTANCE) > integer5) {
                                objectSet8.add(a3.immutable());
                            }
                        }
                    }
                }
                list4.clear();
            }
        }, 
        STEM_BLOCK(new Block[] { Blocks.MELON_STEM, Blocks.PUMPKIN_STEM }) {
            public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
                if (bvt1.<Integer>getValue((Property<Integer>)StemBlock.AGE) == 7) {
                    final StemGrownBlock bsh8 = ((StemBlock)bvt1.getBlock()).getFruit();
                    if (bvt3.getBlock() == bsh8) {
                        return ((AbstractStateHolder<O, BlockState>)bsh8.getAttachedStem().defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)HorizontalDirectionalBlock.FACING, fb);
                    }
                }
                return bvt1;
            }
        };
        
        public static final Direction[] DIRECTIONS;
        
        private BlockFixers(final Block[] arr) {
            this(false, arr);
        }
        
        private BlockFixers(final boolean boolean3, final Block[] arr) {
            for (final Block bmv9 : arr) {
                UpgradeData.MAP.put(bmv9, this);
            }
            if (boolean3) {
                UpgradeData.CHUNKY_FIXERS.add(this);
            }
        }
        
        static {
            DIRECTIONS = Direction.values();
        }
    }
}
