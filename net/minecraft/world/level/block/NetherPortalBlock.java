package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import com.google.common.cache.LoadingCache;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockLayer;
import javax.annotation.Nullable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameRules;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class NetherPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS;
    protected static final VoxelShape X_AXIS_AABB;
    protected static final VoxelShape Z_AXIS_AABB;
    
    public NetherPortalBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Direction.Axis, Direction.Axis>setValue(NetherPortalBlock.AXIS, Direction.Axis.X));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Direction.Axis>getValue(NetherPortalBlock.AXIS)) {
            case Z: {
                return NetherPortalBlock.Z_AXIS_AABB;
            }
            default: {
                return NetherPortalBlock.X_AXIS_AABB;
            }
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, BlockPos ew, final Random random) {
        if (bhr.dimension.isNaturalDimension() && bhr.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < bhr.getDifficulty().getId()) {
            while (bhr.getBlockState(ew).getBlock() == this) {
                ew = ew.below();
            }
            if (bhr.getBlockState(ew).isValidSpawn(bhr, ew, EntityType.ZOMBIE_PIGMAN)) {
                final Entity aio6 = EntityType.ZOMBIE_PIGMAN.spawn(bhr, null, null, null, ew.above(), MobSpawnType.STRUCTURE, false, false);
                if (aio6 != null) {
                    aio6.changingDimensionDelay = aio6.getDimensionChangingDelay();
                }
            }
        }
    }
    
    public boolean trySpawnPortal(final LevelAccessor bhs, final BlockPos ew) {
        final PortalShape a4 = this.isPortal(bhs, ew);
        if (a4 != null) {
            a4.createPortalBlocks();
            return true;
        }
        return false;
    }
    
    @Nullable
    public PortalShape isPortal(final LevelAccessor bhs, final BlockPos ew) {
        final PortalShape a4 = new PortalShape(bhs, ew, Direction.Axis.X);
        if (a4.isValid() && a4.numPortalBlocks == 0) {
            return a4;
        }
        final PortalShape a5 = new PortalShape(bhs, ew, Direction.Axis.Z);
        if (a5.isValid() && a5.numPortalBlocks == 0) {
            return a5;
        }
        return null;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final Direction.Axis a8 = fb.getAxis();
        final Direction.Axis a9 = bvt1.<Direction.Axis>getValue(NetherPortalBlock.AXIS);
        final boolean boolean10 = a9 != a8 && a8.isHorizontal();
        if (boolean10 || bvt3.getBlock() == this || new PortalShape(bhs, ew5, a9).isComplete()) {
            return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        }
        return Blocks.AIR.defaultBlockState();
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (!aio.isPassenger() && !aio.isVehicle() && aio.canChangeDimensions()) {
            aio.handleInsidePortal(ew);
        }
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (random.nextInt(100) == 0) {
            bhr.playLocalSound(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int integer6 = 0; integer6 < 4; ++integer6) {
            double double7 = ew.getX() + random.nextFloat();
            final double double8 = ew.getY() + random.nextFloat();
            double double9 = ew.getZ() + random.nextFloat();
            double double10 = (random.nextFloat() - 0.5) * 0.5;
            final double double11 = (random.nextFloat() - 0.5) * 0.5;
            double double12 = (random.nextFloat() - 0.5) * 0.5;
            final int integer7 = random.nextInt(2) * 2 - 1;
            if (bhr.getBlockState(ew.west()).getBlock() == this || bhr.getBlockState(ew.east()).getBlock() == this) {
                double9 = ew.getZ() + 0.5 + 0.25 * integer7;
                double12 = random.nextFloat() * 2.0f * integer7;
            }
            else {
                double7 = ew.getX() + 0.5 + 0.25 * integer7;
                double10 = random.nextFloat() * 2.0f * integer7;
            }
            bhr.addParticle(ParticleTypes.PORTAL, double7, double8, double9, double10, double11, double12);
        }
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        switch (brg) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90: {
                switch (bvt.<Direction.Axis>getValue(NetherPortalBlock.AXIS)) {
                    case X: {
                        return ((AbstractStateHolder<O, BlockState>)bvt).<Direction.Axis, Direction.Axis>setValue(NetherPortalBlock.AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return ((AbstractStateHolder<O, BlockState>)bvt).<Direction.Axis, Direction.Axis>setValue(NetherPortalBlock.AXIS, Direction.Axis.X);
                    }
                    default: {
                        return bvt;
                    }
                }
                break;
            }
            default: {
                return bvt;
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(NetherPortalBlock.AXIS);
    }
    
    public BlockPattern.BlockPatternMatch getPortalShape(final LevelAccessor bhs, final BlockPos ew) {
        Direction.Axis a4 = Direction.Axis.Z;
        PortalShape a5 = new PortalShape(bhs, ew, Direction.Axis.X);
        final LoadingCache<BlockPos, BlockInWorld> loadingCache6 = BlockPattern.createLevelCache(bhs, true);
        if (!a5.isValid()) {
            a4 = Direction.Axis.X;
            a5 = new PortalShape(bhs, ew, Direction.Axis.Z);
        }
        if (!a5.isValid()) {
            return new BlockPattern.BlockPatternMatch(ew, Direction.NORTH, Direction.UP, loadingCache6, 1, 1, 1);
        }
        final int[] arr7 = new int[Direction.AxisDirection.values().length];
        final Direction fb8 = a5.rightDir.getCounterClockWise();
        final BlockPos ew2 = a5.bottomLeft.above(a5.getHeight() - 1);
        for (final Direction.AxisDirection b13 : Direction.AxisDirection.values()) {
            final BlockPattern.BlockPatternMatch b14 = new BlockPattern.BlockPatternMatch((fb8.getAxisDirection() == b13) ? ew2 : ew2.relative(a5.rightDir, a5.getWidth() - 1), Direction.get(b13, a4), Direction.UP, loadingCache6, a5.getWidth(), a5.getHeight(), 1);
            for (int integer15 = 0; integer15 < a5.getWidth(); ++integer15) {
                for (int integer16 = 0; integer16 < a5.getHeight(); ++integer16) {
                    final BlockInWorld bvx17 = b14.getBlock(integer15, integer16, 1);
                    if (!bvx17.getState().isAir()) {
                        final int[] array = arr7;
                        final int ordinal = b13.ordinal();
                        ++array[ordinal];
                    }
                }
            }
        }
        Direction.AxisDirection b15 = Direction.AxisDirection.POSITIVE;
        for (final Direction.AxisDirection b16 : Direction.AxisDirection.values()) {
            if (arr7[b16.ordinal()] < arr7[b15.ordinal()]) {
                b15 = b16;
            }
        }
        return new BlockPattern.BlockPatternMatch((fb8.getAxisDirection() == b15) ? ew2 : ew2.relative(a5.rightDir, a5.getWidth() - 1), Direction.get(b15, a4), Direction.UP, loadingCache6, a5.getWidth(), a5.getHeight(), 1);
    }
    
    static {
        AXIS = BlockStateProperties.HORIZONTAL_AXIS;
        X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
        Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    }
    
    public static class PortalShape {
        private final LevelAccessor level;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private int numPortalBlocks;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;
        
        public PortalShape(final LevelAccessor bhs, BlockPos ew, final Direction.Axis a) {
            this.level = bhs;
            this.axis = a;
            if (a == Direction.Axis.X) {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            }
            else {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }
            for (BlockPos ew2 = ew; ew.getY() > ew2.getY() - 21 && ew.getY() > 0 && this.isEmpty(bhs.getBlockState(ew.below())); ew = ew.below()) {}
            final int integer6 = this.getDistanceUntilEdge(ew, this.leftDir) - 1;
            if (integer6 >= 0) {
                this.bottomLeft = ew.relative(this.leftDir, integer6);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }
            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }
        }
        
        protected int getDistanceUntilEdge(final BlockPos ew, final Direction fb) {
            int integer4;
            for (integer4 = 0; integer4 < 22; ++integer4) {
                final BlockPos ew2 = ew.relative(fb, integer4);
                if (!this.isEmpty(this.level.getBlockState(ew2))) {
                    break;
                }
                if (this.level.getBlockState(ew2.below()).getBlock() != Blocks.OBSIDIAN) {
                    break;
                }
            }
            final Block bmv5 = this.level.getBlockState(ew.relative(fb, integer4)).getBlock();
            if (bmv5 == Blocks.OBSIDIAN) {
                return integer4;
            }
            return 0;
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        protected int calculatePortalHeight() {
            this.height = 0;
        Label_0189:
            while (this.height < 21) {
                for (int integer2 = 0; integer2 < this.width; ++integer2) {
                    final BlockPos ew3 = this.bottomLeft.relative(this.rightDir, integer2).above(this.height);
                    final BlockState bvt4 = this.level.getBlockState(ew3);
                    if (!this.isEmpty(bvt4)) {
                        break Label_0189;
                    }
                    Block bmv5 = bvt4.getBlock();
                    if (bmv5 == Blocks.NETHER_PORTAL) {
                        ++this.numPortalBlocks;
                    }
                    if (integer2 == 0) {
                        bmv5 = this.level.getBlockState(ew3.relative(this.leftDir)).getBlock();
                        if (bmv5 != Blocks.OBSIDIAN) {
                            break Label_0189;
                        }
                    }
                    else if (integer2 == this.width - 1) {
                        bmv5 = this.level.getBlockState(ew3.relative(this.rightDir)).getBlock();
                        if (bmv5 != Blocks.OBSIDIAN) {
                            break Label_0189;
                        }
                    }
                }
                ++this.height;
            }
            for (int integer2 = 0; integer2 < this.width; ++integer2) {
                if (this.level.getBlockState(this.bottomLeft.relative(this.rightDir, integer2).above(this.height)).getBlock() != Blocks.OBSIDIAN) {
                    this.height = 0;
                    break;
                }
            }
            if (this.height > 21 || this.height < 3) {
                this.bottomLeft = null;
                this.width = 0;
                return this.height = 0;
            }
            return this.height;
        }
        
        protected boolean isEmpty(final BlockState bvt) {
            final Block bmv3 = bvt.getBlock();
            return bvt.isAir() || bmv3 == Blocks.FIRE || bmv3 == Blocks.NETHER_PORTAL;
        }
        
        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }
        
        public void createPortalBlocks() {
            for (int integer2 = 0; integer2 < this.width; ++integer2) {
                final BlockPos ew3 = this.bottomLeft.relative(this.rightDir, integer2);
                for (int integer3 = 0; integer3 < this.height; ++integer3) {
                    this.level.setBlock(ew3.above(integer3), ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_PORTAL.defaultBlockState()).<Direction.Axis, Direction.Axis>setValue(NetherPortalBlock.AXIS, this.axis), 18);
                }
            }
        }
        
        private boolean hasAllPortalBlocks() {
            return this.numPortalBlocks >= this.width * this.height;
        }
        
        public boolean isComplete() {
            return this.isValid() && this.hasAllPortalBlocks();
        }
    }
}
