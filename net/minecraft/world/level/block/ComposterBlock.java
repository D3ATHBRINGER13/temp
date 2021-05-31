package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import javax.annotation.Nullable;
import net.minecraft.world.SimpleContainer;
import java.util.function.Consumer;
import net.minecraft.Util;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.CollisionContext;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ItemLike;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.WorldlyContainerHolder;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
    public static final IntegerProperty LEVEL;
    public static final Object2FloatMap<ItemLike> COMPOSTABLES;
    public static final VoxelShape OUTER_SHAPE;
    private static final VoxelShape[] SHAPES;
    
    public static void bootStrap() {
        ComposterBlock.COMPOSTABLES.defaultReturnValue(-1.0f);
        final float float1 = 0.3f;
        final float float2 = 0.5f;
        final float float3 = 0.65f;
        final float float4 = 0.85f;
        final float float5 = 1.0f;
        add(0.3f, Items.JUNGLE_LEAVES);
        add(0.3f, Items.OAK_LEAVES);
        add(0.3f, Items.SPRUCE_LEAVES);
        add(0.3f, Items.DARK_OAK_LEAVES);
        add(0.3f, Items.ACACIA_LEAVES);
        add(0.3f, Items.BIRCH_LEAVES);
        add(0.3f, Items.OAK_SAPLING);
        add(0.3f, Items.SPRUCE_SAPLING);
        add(0.3f, Items.BIRCH_SAPLING);
        add(0.3f, Items.JUNGLE_SAPLING);
        add(0.3f, Items.ACACIA_SAPLING);
        add(0.3f, Items.DARK_OAK_SAPLING);
        add(0.3f, Items.BEETROOT_SEEDS);
        add(0.3f, Items.DRIED_KELP);
        add(0.3f, Items.GRASS);
        add(0.3f, Items.KELP);
        add(0.3f, Items.MELON_SEEDS);
        add(0.3f, Items.PUMPKIN_SEEDS);
        add(0.3f, Items.SEAGRASS);
        add(0.3f, Items.SWEET_BERRIES);
        add(0.3f, Items.WHEAT_SEEDS);
        add(0.5f, Items.DRIED_KELP_BLOCK);
        add(0.5f, Items.TALL_GRASS);
        add(0.5f, Items.CACTUS);
        add(0.5f, Items.SUGAR_CANE);
        add(0.5f, Items.VINE);
        add(0.5f, Items.MELON_SLICE);
        add(0.65f, Items.SEA_PICKLE);
        add(0.65f, Items.LILY_PAD);
        add(0.65f, Items.PUMPKIN);
        add(0.65f, Items.CARVED_PUMPKIN);
        add(0.65f, Items.MELON);
        add(0.65f, Items.APPLE);
        add(0.65f, Items.BEETROOT);
        add(0.65f, Items.CARROT);
        add(0.65f, Items.COCOA_BEANS);
        add(0.65f, Items.POTATO);
        add(0.65f, Items.WHEAT);
        add(0.65f, Items.BROWN_MUSHROOM);
        add(0.65f, Items.RED_MUSHROOM);
        add(0.65f, Items.MUSHROOM_STEM);
        add(0.65f, Items.DANDELION);
        add(0.65f, Items.POPPY);
        add(0.65f, Items.BLUE_ORCHID);
        add(0.65f, Items.ALLIUM);
        add(0.65f, Items.AZURE_BLUET);
        add(0.65f, Items.RED_TULIP);
        add(0.65f, Items.ORANGE_TULIP);
        add(0.65f, Items.WHITE_TULIP);
        add(0.65f, Items.PINK_TULIP);
        add(0.65f, Items.OXEYE_DAISY);
        add(0.65f, Items.CORNFLOWER);
        add(0.65f, Items.LILY_OF_THE_VALLEY);
        add(0.65f, Items.WITHER_ROSE);
        add(0.65f, Items.FERN);
        add(0.65f, Items.SUNFLOWER);
        add(0.65f, Items.LILAC);
        add(0.65f, Items.ROSE_BUSH);
        add(0.65f, Items.PEONY);
        add(0.65f, Items.LARGE_FERN);
        add(0.85f, Items.HAY_BLOCK);
        add(0.85f, Items.BROWN_MUSHROOM_BLOCK);
        add(0.85f, Items.RED_MUSHROOM_BLOCK);
        add(0.85f, Items.BREAD);
        add(0.85f, Items.BAKED_POTATO);
        add(0.85f, Items.COOKIE);
        add(1.0f, Items.CAKE);
        add(1.0f, Items.PUMPKIN_PIE);
    }
    
    private static void add(final float float1, final ItemLike bhq) {
        ComposterBlock.COMPOSTABLES.put(bhq.asItem(), float1);
    }
    
    public ComposterBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)ComposterBlock.LEVEL, 0));
    }
    
    public static void handleFill(final Level bhr, final BlockPos ew, final boolean boolean3) {
        final BlockState bvt4 = bhr.getBlockState(ew);
        bhr.playLocalSound(ew.getX(), ew.getY(), ew.getZ(), boolean3 ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        final double double5 = bvt4.getShape(bhr, ew).max(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        final double double6 = 0.13124999403953552;
        final double double7 = 0.737500011920929;
        final Random random11 = bhr.getRandom();
        for (int integer12 = 0; integer12 < 10; ++integer12) {
            final double double8 = random11.nextGaussian() * 0.02;
            final double double9 = random11.nextGaussian() * 0.02;
            final double double10 = random11.nextGaussian() * 0.02;
            bhr.addParticle(ParticleTypes.COMPOSTER, ew.getX() + 0.13124999403953552 + 0.737500011920929 * random11.nextFloat(), ew.getY() + double5 + random11.nextFloat() * (1.0 - double5), ew.getZ() + 0.13124999403953552 + 0.737500011920929 * random11.nextFloat(), double8, double9, double10);
        }
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return ComposterBlock.SHAPES[bvt.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL)];
    }
    
    @Override
    public VoxelShape getInteractionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return ComposterBlock.OUTER_SHAPE;
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return ComposterBlock.SHAPES[0];
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL) == 7) {
            bhr.getBlockTicks().scheduleTick(ew, bvt1.getBlock(), 20);
        }
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final int integer8 = bvt.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL);
        final ItemStack bcj9 = awg.getItemInHand(ahi);
        if (integer8 < 8 && ComposterBlock.COMPOSTABLES.containsKey(bcj9.getItem())) {
            if (integer8 < 7 && !bhr.isClientSide) {
                final boolean boolean10 = addItem(bvt, bhr, ew, bcj9);
                bhr.levelEvent(1500, ew, boolean10 ? 1 : 0);
                if (!awg.abilities.instabuild) {
                    bcj9.shrink(1);
                }
            }
            return true;
        }
        if (integer8 == 8) {
            if (!bhr.isClientSide) {
                final float float10 = 0.7f;
                final double double11 = bhr.random.nextFloat() * 0.7f + 0.15000000596046448;
                final double double12 = bhr.random.nextFloat() * 0.7f + 0.06000000238418579 + 0.6;
                final double double13 = bhr.random.nextFloat() * 0.7f + 0.15000000596046448;
                final ItemEntity atx17 = new ItemEntity(bhr, ew.getX() + double11, ew.getY() + double12, ew.getZ() + double13, new ItemStack(Items.BONE_MEAL));
                atx17.setDefaultPickUpDelay();
                bhr.addFreshEntity(atx17);
            }
            empty(bvt, bhr, ew);
            bhr.playSound(null, ew, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
    
    private static void empty(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
        bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)ComposterBlock.LEVEL, 0), 3);
    }
    
    private static boolean addItem(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final ItemStack bcj) {
        final int integer5 = bvt.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL);
        final float float6 = ComposterBlock.COMPOSTABLES.getFloat(bcj.getItem());
        if ((integer5 == 0 && float6 > 0.0f) || bhs.getRandom().nextDouble() < float6) {
            final int integer6 = integer5 + 1;
            bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)ComposterBlock.LEVEL, integer6), 3);
            if (integer6 == 7) {
                bhs.getBlockTicks().scheduleTick(ew, bvt.getBlock(), 20);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bvt.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL) == 7) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)ComposterBlock.LEVEL), 3);
            bhr.playSound(null, ew, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.tick(bvt, bhr, ew, random);
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return bvt.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ComposterBlock.LEVEL);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    @Override
    public WorldlyContainer getContainer(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
        final int integer5 = bvt.<Integer>getValue((Property<Integer>)ComposterBlock.LEVEL);
        if (integer5 == 8) {
            return new OutputContainer(bvt, bhs, ew, new ItemStack(Items.BONE_MEAL));
        }
        if (integer5 < 7) {
            return new InputContainer(bvt, bhs, ew);
        }
        return new EmptyContainer();
    }
    
    static {
        LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
        COMPOSTABLES = (Object2FloatMap)new Object2FloatOpenHashMap();
        OUTER_SHAPE = Shapes.block();
        SHAPES = Util.<VoxelShape[]>make(new VoxelShape[9], (java.util.function.Consumer<VoxelShape[]>)(arr -> {
            for (int integer2 = 0; integer2 < 8; ++integer2) {
                arr[integer2] = Shapes.join(ComposterBlock.OUTER_SHAPE, Block.box(2.0, Math.max(2, 1 + integer2 * 2), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
            }
            arr[8] = arr[7];
        }));
    }
    
    static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
        public EmptyContainer() {
            super(0);
        }
        
        @Override
        public int[] getSlotsForFace(final Direction fb) {
            return new int[0];
        }
        
        @Override
        public boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb) {
            return false;
        }
        
        @Override
        public boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb) {
            return false;
        }
    }
    
    static class OutputContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;
        
        public OutputContainer(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final ItemStack bcj) {
            super(bcj);
            this.state = bvt;
            this.level = bhs;
            this.pos = ew;
        }
        
        public int getMaxStackSize() {
            return 1;
        }
        
        @Override
        public int[] getSlotsForFace(final Direction fb) {
            return (fb == Direction.DOWN) ? new int[] { 0 } : new int[0];
        }
        
        @Override
        public boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb) {
            return false;
        }
        
        @Override
        public boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb) {
            return !this.changed && fb == Direction.DOWN && bcj.getItem() == Items.BONE_MEAL;
        }
        
        @Override
        public void setChanged() {
            empty(this.state, this.level, this.pos);
            this.changed = true;
        }
    }
    
    static class InputContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;
        
        public InputContainer(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
            super(1);
            this.state = bvt;
            this.level = bhs;
            this.pos = ew;
        }
        
        public int getMaxStackSize() {
            return 1;
        }
        
        @Override
        public int[] getSlotsForFace(final Direction fb) {
            return (fb == Direction.UP) ? new int[] { 0 } : new int[0];
        }
        
        @Override
        public boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb) {
            return !this.changed && fb == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(bcj.getItem());
        }
        
        @Override
        public boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb) {
            return false;
        }
        
        @Override
        public void setChanged() {
            final ItemStack bcj2 = this.getItem(0);
            if (!bcj2.isEmpty()) {
                this.changed = true;
                addItem(this.state, this.level, this.pos, bcj2);
                this.removeItemNoUpdate(0);
            }
        }
    }
}
