package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.stream.Collector;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.GameRules;
import java.util.Random;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FireBlock extends Block {
    public static final IntegerProperty AGE;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    private final Object2IntMap<Block> flameOdds;
    private final Object2IntMap<Block> burnOdds;
    
    protected FireBlock(final Properties c) {
        super(c);
        this.flameOdds = (Object2IntMap<Block>)new Object2IntOpenHashMap();
        this.burnOdds = (Object2IntMap<Block>)new Object2IntOpenHashMap();
        this.registerDefaultState((((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)FireBlock.AGE, 0)).setValue((Property<Comparable>)FireBlock.NORTH, false)).setValue((Property<Comparable>)FireBlock.EAST, false)).setValue((Property<Comparable>)FireBlock.SOUTH, false)).setValue((Property<Comparable>)FireBlock.WEST, false)).<Comparable, Boolean>setValue((Property<Comparable>)FireBlock.UP, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.empty();
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (this.canSurvive(bvt1, bhs, ew5)) {
            return ((AbstractStateHolder<O, BlockState>)this.getStateForPlacement(bhs, ew5)).<Comparable, Comparable>setValue((Property<Comparable>)FireBlock.AGE, (Comparable)bvt1.<V>getValue((Property<V>)FireBlock.AGE));
        }
        return Blocks.AIR.defaultBlockState();
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return this.getStateForPlacement(ban.getLevel(), ban.getClickedPos());
    }
    
    public BlockState getStateForPlacement(final BlockGetter bhb, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt5 = bhb.getBlockState(ew2);
        if (this.canBurn(bvt5) || bvt5.isFaceSturdy(bhb, ew2, Direction.UP)) {
            return this.defaultBlockState();
        }
        BlockState bvt6 = this.defaultBlockState();
        for (final Direction fb10 : Direction.values()) {
            final BooleanProperty bwl11 = (BooleanProperty)FireBlock.PROPERTY_BY_DIRECTION.get(fb10);
            if (bwl11 != null) {
                bvt6 = ((AbstractStateHolder<O, BlockState>)bvt6).<Comparable, Boolean>setValue((Property<Comparable>)bwl11, this.canBurn(bhb.getBlockState(ew.relative(fb10))));
            }
        }
        return bvt6;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        return bhu.getBlockState(ew2).isFaceSturdy(bhu, ew2, Direction.UP) || this.isValidFireLocation(bhu, ew);
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 30;
    }
    
    @Override
    public void tick(BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bhr.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.removeBlock(ew, false);
        }
        final Block bmv6 = bhr.getBlockState(ew.below()).getBlock();
        final boolean boolean7 = (bhr.dimension instanceof TheEndDimension && bmv6 == Blocks.BEDROCK) || bmv6 == Blocks.NETHERRACK || bmv6 == Blocks.MAGMA_BLOCK;
        final int integer8 = bvt.<Integer>getValue((Property<Integer>)FireBlock.AGE);
        if (!boolean7 && bhr.isRaining() && this.isNearRain(bhr, ew) && random.nextFloat() < 0.2f + integer8 * 0.03f) {
            bhr.removeBlock(ew, false);
            return;
        }
        final int integer9 = Math.min(15, integer8 + random.nextInt(3) / 2);
        if (integer8 != integer9) {
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)FireBlock.AGE, integer9);
            bhr.setBlock(ew, bvt, 4);
        }
        if (!boolean7) {
            bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr) + random.nextInt(10));
            if (!this.isValidFireLocation(bhr, ew)) {
                final BlockPos ew2 = ew.below();
                if (!bhr.getBlockState(ew2).isFaceSturdy(bhr, ew2, Direction.UP) || integer8 > 3) {
                    bhr.removeBlock(ew, false);
                }
                return;
            }
            if (integer8 == 15 && random.nextInt(4) == 0 && !this.canBurn(bhr.getBlockState(ew.below()))) {
                bhr.removeBlock(ew, false);
                return;
            }
        }
        final boolean boolean8 = bhr.isHumidAt(ew);
        final int integer10 = boolean8 ? -50 : 0;
        this.checkBurnOut(bhr, ew.east(), 300 + integer10, random, integer8);
        this.checkBurnOut(bhr, ew.west(), 300 + integer10, random, integer8);
        this.checkBurnOut(bhr, ew.below(), 250 + integer10, random, integer8);
        this.checkBurnOut(bhr, ew.above(), 250 + integer10, random, integer8);
        this.checkBurnOut(bhr, ew.north(), 300 + integer10, random, integer8);
        this.checkBurnOut(bhr, ew.south(), 300 + integer10, random, integer8);
        final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
        for (int integer11 = -1; integer11 <= 1; ++integer11) {
            for (int integer12 = -1; integer12 <= 1; ++integer12) {
                for (int integer13 = -1; integer13 <= 4; ++integer13) {
                    if (integer11 != 0 || integer13 != 0 || integer12 != 0) {
                        int integer14 = 100;
                        if (integer13 > 1) {
                            integer14 += (integer13 - 1) * 100;
                        }
                        a12.set(ew).move(integer11, integer13, integer12);
                        final int integer15 = this.getFireOdds(bhr, a12);
                        if (integer15 > 0) {
                            int integer16 = (integer15 + 40 + bhr.getDifficulty().getId() * 7) / (integer8 + 30);
                            if (boolean8) {
                                integer16 /= 2;
                            }
                            if (integer16 > 0 && random.nextInt(integer14) <= integer16) {
                                if (!bhr.isRaining() || !this.isNearRain(bhr, a12)) {
                                    final int integer17 = Math.min(15, integer8 + random.nextInt(5) / 4);
                                    bhr.setBlock(a12, ((AbstractStateHolder<O, BlockState>)this.getStateForPlacement(bhr, a12)).<Comparable, Integer>setValue((Property<Comparable>)FireBlock.AGE, integer17), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected boolean isNearRain(final Level bhr, final BlockPos ew) {
        return bhr.isRainingAt(ew) || bhr.isRainingAt(ew.west()) || bhr.isRainingAt(ew.east()) || bhr.isRainingAt(ew.north()) || bhr.isRainingAt(ew.south());
    }
    
    private int getBurnOdd(final BlockState bvt) {
        if (bvt.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.WATERLOGGED) && bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED)) {
            return 0;
        }
        return this.burnOdds.getInt(bvt.getBlock());
    }
    
    private int getFlameOdds(final BlockState bvt) {
        if (bvt.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.WATERLOGGED) && bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED)) {
            return 0;
        }
        return this.flameOdds.getInt(bvt.getBlock());
    }
    
    private void checkBurnOut(final Level bhr, final BlockPos ew, final int integer3, final Random random, final int integer5) {
        final int integer6 = this.getBurnOdd(bhr.getBlockState(ew));
        if (random.nextInt(integer3) < integer6) {
            final BlockState bvt8 = bhr.getBlockState(ew);
            if (random.nextInt(integer5 + 10) < 5 && !bhr.isRainingAt(ew)) {
                final int integer7 = Math.min(integer5 + random.nextInt(5) / 4, 15);
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)this.getStateForPlacement(bhr, ew)).<Comparable, Integer>setValue((Property<Comparable>)FireBlock.AGE, integer7), 3);
            }
            else {
                bhr.removeBlock(ew, false);
            }
            final Block bmv9 = bvt8.getBlock();
            if (bmv9 instanceof TntBlock) {
                final TntBlock tntBlock = (TntBlock)bmv9;
                TntBlock.explode(bhr, ew);
            }
        }
    }
    
    private boolean isValidFireLocation(final BlockGetter bhb, final BlockPos ew) {
        for (final Direction fb7 : Direction.values()) {
            if (this.canBurn(bhb.getBlockState(ew.relative(fb7)))) {
                return true;
            }
        }
        return false;
    }
    
    private int getFireOdds(final LevelReader bhu, final BlockPos ew) {
        if (!bhu.isEmptyBlock(ew)) {
            return 0;
        }
        int integer4 = 0;
        for (final Direction fb8 : Direction.values()) {
            final BlockState bvt9 = bhu.getBlockState(ew.relative(fb8));
            integer4 = Math.max(this.getFlameOdds(bvt9), integer4);
        }
        return integer4;
    }
    
    public boolean canBurn(final BlockState bvt) {
        return this.getFlameOdds(bvt) > 0;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        if ((bhr.dimension.getType() == DimensionType.OVERWORLD || bhr.dimension.getType() == DimensionType.NETHER) && ((NetherPortalBlock)Blocks.NETHER_PORTAL).trySpawnPortal(bhr, ew)) {
            return;
        }
        if (!bvt1.canSurvive(bhr, ew)) {
            bhr.removeBlock(ew, false);
            return;
        }
        bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr) + bhr.random.nextInt(10));
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (random.nextInt(24) == 0) {
            bhr.playLocalSound(ew.getX() + 0.5f, ew.getY() + 0.5f, ew.getZ() + 0.5f, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
        }
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (this.canBurn(bvt2) || bvt2.isFaceSturdy(bhr, ew2, Direction.UP)) {
            for (int integer8 = 0; integer8 < 3; ++integer8) {
                final double double9 = ew.getX() + random.nextDouble();
                final double double10 = ew.getY() + random.nextDouble() * 0.5 + 0.5;
                final double double11 = ew.getZ() + random.nextDouble();
                bhr.addParticle(ParticleTypes.LARGE_SMOKE, double9, double10, double11, 0.0, 0.0, 0.0);
            }
        }
        else {
            if (this.canBurn(bhr.getBlockState(ew.west()))) {
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    final double double9 = ew.getX() + random.nextDouble() * 0.10000000149011612;
                    final double double10 = ew.getY() + random.nextDouble();
                    final double double11 = ew.getZ() + random.nextDouble();
                    bhr.addParticle(ParticleTypes.LARGE_SMOKE, double9, double10, double11, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(bhr.getBlockState(ew.east()))) {
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    final double double9 = ew.getX() + 1 - random.nextDouble() * 0.10000000149011612;
                    final double double10 = ew.getY() + random.nextDouble();
                    final double double11 = ew.getZ() + random.nextDouble();
                    bhr.addParticle(ParticleTypes.LARGE_SMOKE, double9, double10, double11, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(bhr.getBlockState(ew.north()))) {
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    final double double9 = ew.getX() + random.nextDouble();
                    final double double10 = ew.getY() + random.nextDouble();
                    final double double11 = ew.getZ() + random.nextDouble() * 0.10000000149011612;
                    bhr.addParticle(ParticleTypes.LARGE_SMOKE, double9, double10, double11, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(bhr.getBlockState(ew.south()))) {
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    final double double9 = ew.getX() + random.nextDouble();
                    final double double10 = ew.getY() + random.nextDouble();
                    final double double11 = ew.getZ() + 1 - random.nextDouble() * 0.10000000149011612;
                    bhr.addParticle(ParticleTypes.LARGE_SMOKE, double9, double10, double11, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(bhr.getBlockState(ew.above()))) {
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    final double double9 = ew.getX() + random.nextDouble();
                    final double double10 = ew.getY() + 1 - random.nextDouble() * 0.10000000149011612;
                    final double double11 = ew.getZ() + random.nextDouble();
                    bhr.addParticle(ParticleTypes.LARGE_SMOKE, double9, double10, double11, 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(FireBlock.AGE, FireBlock.NORTH, FireBlock.EAST, FireBlock.SOUTH, FireBlock.WEST, FireBlock.UP);
    }
    
    public void setFlammable(final Block bmv, final int integer2, final int integer3) {
        this.flameOdds.put(bmv, integer2);
        this.burnOdds.put(bmv, integer3);
    }
    
    public static void bootStrap() {
        final FireBlock bow1 = (FireBlock)Blocks.FIRE;
        bow1.setFlammable(Blocks.OAK_PLANKS, 5, 20);
        bow1.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
        bow1.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
        bow1.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
        bow1.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
        bow1.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
        bow1.setFlammable(Blocks.OAK_SLAB, 5, 20);
        bow1.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
        bow1.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
        bow1.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
        bow1.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
        bow1.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
        bow1.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
        bow1.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        bow1.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
        bow1.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        bow1.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        bow1.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
        bow1.setFlammable(Blocks.OAK_FENCE, 5, 20);
        bow1.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
        bow1.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
        bow1.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
        bow1.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
        bow1.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
        bow1.setFlammable(Blocks.OAK_STAIRS, 5, 20);
        bow1.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
        bow1.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
        bow1.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
        bow1.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
        bow1.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
        bow1.setFlammable(Blocks.OAK_LOG, 5, 5);
        bow1.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
        bow1.setFlammable(Blocks.BIRCH_LOG, 5, 5);
        bow1.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
        bow1.setFlammable(Blocks.ACACIA_LOG, 5, 5);
        bow1.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
        bow1.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
        bow1.setFlammable(Blocks.OAK_WOOD, 5, 5);
        bow1.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
        bow1.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
        bow1.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
        bow1.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
        bow1.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
        bow1.setFlammable(Blocks.OAK_LEAVES, 30, 60);
        bow1.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
        bow1.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
        bow1.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
        bow1.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
        bow1.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
        bow1.setFlammable(Blocks.BOOKSHELF, 30, 20);
        bow1.setFlammable(Blocks.TNT, 15, 100);
        bow1.setFlammable(Blocks.GRASS, 60, 100);
        bow1.setFlammable(Blocks.FERN, 60, 100);
        bow1.setFlammable(Blocks.DEAD_BUSH, 60, 100);
        bow1.setFlammable(Blocks.SUNFLOWER, 60, 100);
        bow1.setFlammable(Blocks.LILAC, 60, 100);
        bow1.setFlammable(Blocks.ROSE_BUSH, 60, 100);
        bow1.setFlammable(Blocks.PEONY, 60, 100);
        bow1.setFlammable(Blocks.TALL_GRASS, 60, 100);
        bow1.setFlammable(Blocks.LARGE_FERN, 60, 100);
        bow1.setFlammable(Blocks.DANDELION, 60, 100);
        bow1.setFlammable(Blocks.POPPY, 60, 100);
        bow1.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
        bow1.setFlammable(Blocks.ALLIUM, 60, 100);
        bow1.setFlammable(Blocks.AZURE_BLUET, 60, 100);
        bow1.setFlammable(Blocks.RED_TULIP, 60, 100);
        bow1.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
        bow1.setFlammable(Blocks.WHITE_TULIP, 60, 100);
        bow1.setFlammable(Blocks.PINK_TULIP, 60, 100);
        bow1.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
        bow1.setFlammable(Blocks.CORNFLOWER, 60, 100);
        bow1.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
        bow1.setFlammable(Blocks.WITHER_ROSE, 60, 100);
        bow1.setFlammable(Blocks.WHITE_WOOL, 30, 60);
        bow1.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
        bow1.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
        bow1.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
        bow1.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
        bow1.setFlammable(Blocks.LIME_WOOL, 30, 60);
        bow1.setFlammable(Blocks.PINK_WOOL, 30, 60);
        bow1.setFlammable(Blocks.GRAY_WOOL, 30, 60);
        bow1.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
        bow1.setFlammable(Blocks.CYAN_WOOL, 30, 60);
        bow1.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
        bow1.setFlammable(Blocks.BLUE_WOOL, 30, 60);
        bow1.setFlammable(Blocks.BROWN_WOOL, 30, 60);
        bow1.setFlammable(Blocks.GREEN_WOOL, 30, 60);
        bow1.setFlammable(Blocks.RED_WOOL, 30, 60);
        bow1.setFlammable(Blocks.BLACK_WOOL, 30, 60);
        bow1.setFlammable(Blocks.VINE, 15, 100);
        bow1.setFlammable(Blocks.COAL_BLOCK, 5, 5);
        bow1.setFlammable(Blocks.HAY_BLOCK, 60, 20);
        bow1.setFlammable(Blocks.WHITE_CARPET, 60, 20);
        bow1.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
        bow1.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
        bow1.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
        bow1.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
        bow1.setFlammable(Blocks.LIME_CARPET, 60, 20);
        bow1.setFlammable(Blocks.PINK_CARPET, 60, 20);
        bow1.setFlammable(Blocks.GRAY_CARPET, 60, 20);
        bow1.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
        bow1.setFlammable(Blocks.CYAN_CARPET, 60, 20);
        bow1.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
        bow1.setFlammable(Blocks.BLUE_CARPET, 60, 20);
        bow1.setFlammable(Blocks.BROWN_CARPET, 60, 20);
        bow1.setFlammable(Blocks.GREEN_CARPET, 60, 20);
        bow1.setFlammable(Blocks.RED_CARPET, 60, 20);
        bow1.setFlammable(Blocks.BLACK_CARPET, 60, 20);
        bow1.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
        bow1.setFlammable(Blocks.BAMBOO, 60, 60);
        bow1.setFlammable(Blocks.SCAFFOLDING, 60, 60);
        bow1.setFlammable(Blocks.LECTERN, 30, 20);
        bow1.setFlammable(Blocks.COMPOSTER, 5, 20);
        bow1.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
    }
    
    static {
        AGE = BlockStateProperties.AGE_15;
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        UP = PipeBlock.UP;
        PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect((Collector)Util.toMap());
    }
}
