package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import java.util.Optional;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BedBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<BedPart> PART;
    public static final BooleanProperty OCCUPIED;
    protected static final VoxelShape BASE;
    protected static final VoxelShape LEG_NORTH_WEST;
    protected static final VoxelShape LEG_SOUTH_WEST;
    protected static final VoxelShape LEG_NORTH_EAST;
    protected static final VoxelShape LEG_SOUTH_EAST;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    private final DyeColor color;
    
    public BedBlock(final DyeColor bbg, final Properties c) {
        super(c);
        this.color = bbg;
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue(BedBlock.PART, BedPart.FOOT)).<Comparable, Boolean>setValue((Property<Comparable>)BedBlock.OCCUPIED, false));
    }
    
    @Override
    public MaterialColor getMapColor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        if (bvt.<BedPart>getValue(BedBlock.PART) == BedPart.FOOT) {
            return this.color.getMaterialColor();
        }
        return MaterialColor.WOOL;
    }
    
    @Nullable
    public static Direction getBedOrientation(final BlockGetter bhb, final BlockPos ew) {
        final BlockState bvt3 = bhb.getBlockState(ew);
        return (bvt3.getBlock() instanceof BedBlock) ? bvt3.<Direction>getValue((Property<Direction>)BedBlock.FACING) : null;
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        if (bvt.<BedPart>getValue(BedBlock.PART) != BedPart.HEAD) {
            ew = ew.relative(bvt.<Direction>getValue((Property<Direction>)BedBlock.FACING));
            bvt = bhr.getBlockState(ew);
            if (bvt.getBlock() != this) {
                return true;
            }
        }
        if (!bhr.dimension.mayRespawn() || bhr.getBiome(ew) == Biomes.NETHER) {
            bhr.removeBlock(ew, false);
            final BlockPos ew2 = ew.relative(bvt.<Direction>getValue((Property<Direction>)BedBlock.FACING).getOpposite());
            if (bhr.getBlockState(ew2).getBlock() == this) {
                bhr.removeBlock(ew2, false);
            }
            bhr.explode(null, DamageSource.netherBedExplosion(), ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, 5.0f, true, Explosion.BlockInteraction.DESTROY);
            return true;
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)BedBlock.OCCUPIED)) {
            awg.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied", new Object[0]), true);
            return true;
        }
        awg.startSleepInBed(ew).ifLeft(a -> {
            if (a != null) {
                awg.displayClientMessage(a.getMessage(), true);
            }
        });
        return true;
    }
    
    @Override
    public void fallOn(final Level bhr, final BlockPos ew, final Entity aio, final float float4) {
        super.fallOn(bhr, ew, aio, float4 * 0.5f);
    }
    
    @Override
    public void updateEntityAfterFallOn(final BlockGetter bhb, final Entity aio) {
        if (aio.isSneaking()) {
            super.updateEntityAfterFallOn(bhb, aio);
        }
        else {
            final Vec3 csi4 = aio.getDeltaMovement();
            if (csi4.y < 0.0) {
                final double double5 = (aio instanceof LivingEntity) ? 1.0 : 0.8;
                aio.setDeltaMovement(csi4.x, -csi4.y * 0.6600000262260437 * double5, csi4.z);
            }
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb != getNeighbourDirection(bvt1.<BedPart>getValue(BedBlock.PART), bvt1.<Direction>getValue((Property<Direction>)BedBlock.FACING))) {
            return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        }
        if (bvt3.getBlock() == this && bvt3.<BedPart>getValue(BedBlock.PART) != bvt1.<BedPart>getValue(BedBlock.PART)) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Comparable>setValue((Property<Comparable>)BedBlock.OCCUPIED, (Comparable)bvt3.<V>getValue((Property<V>)BedBlock.OCCUPIED));
        }
        return Blocks.AIR.defaultBlockState();
    }
    
    private static Direction getNeighbourDirection(final BedPart bwi, final Direction fb) {
        return (bwi == BedPart.FOOT) ? fb : fb.getOpposite();
    }
    
    @Override
    public void playerDestroy(final Level bhr, final Player awg, final BlockPos ew, final BlockState bvt, @Nullable final BlockEntity btw, final ItemStack bcj) {
        super.playerDestroy(bhr, awg, ew, Blocks.AIR.defaultBlockState(), btw, bcj);
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        final BedPart bwi6 = bvt.<BedPart>getValue(BedBlock.PART);
        final BlockPos ew2 = ew.relative(getNeighbourDirection(bwi6, bvt.<Direction>getValue((Property<Direction>)BedBlock.FACING)));
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt2.getBlock() == this && bvt2.<BedPart>getValue(BedBlock.PART) != bwi6) {
            bhr.setBlock(ew2, Blocks.AIR.defaultBlockState(), 35);
            bhr.levelEvent(awg, 2001, ew2, Block.getId(bvt2));
            if (!bhr.isClientSide && !awg.isCreative()) {
                final ItemStack bcj9 = awg.getMainHandItem();
                Block.dropResources(bvt, bhr, ew, null, awg, bcj9);
                Block.dropResources(bvt2, bhr, ew2, null, awg, bcj9);
            }
            awg.awardStat(Stats.BLOCK_MINED.get(this));
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Direction fb3 = ban.getHorizontalDirection();
        final BlockPos ew4 = ban.getClickedPos();
        final BlockPos ew5 = ew4.relative(fb3);
        if (ban.getLevel().getBlockState(ew5).canBeReplaced(ban)) {
            return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)BedBlock.FACING, fb3);
        }
        return null;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)BedBlock.FACING);
        final Direction fb7 = (bvt.<BedPart>getValue(BedBlock.PART) == BedPart.HEAD) ? fb6 : fb6.getOpposite();
        switch (fb7) {
            case NORTH: {
                return BedBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return BedBlock.SOUTH_SHAPE;
            }
            case WEST: {
                return BedBlock.WEST_SHAPE;
            }
            default: {
                return BedBlock.EAST_SHAPE;
            }
        }
    }
    
    @Override
    public boolean hasCustomBreakingProgress(final BlockState bvt) {
        return true;
    }
    
    public static Optional<Vec3> findStandUpPosition(final EntityType<?> ais, final LevelReader bhu, final BlockPos ew, int integer) {
        final Direction fb5 = bhu.getBlockState(ew).<Direction>getValue((Property<Direction>)BedBlock.FACING);
        final int integer2 = ew.getX();
        final int integer3 = ew.getY();
        final int integer4 = ew.getZ();
        for (int integer5 = 0; integer5 <= 1; ++integer5) {
            final int integer6 = integer2 - fb5.getStepX() * integer5 - 1;
            final int integer7 = integer4 - fb5.getStepZ() * integer5 - 1;
            final int integer8 = integer6 + 2;
            final int integer9 = integer7 + 2;
            for (int integer10 = integer6; integer10 <= integer8; ++integer10) {
                for (int integer11 = integer7; integer11 <= integer9; ++integer11) {
                    final BlockPos ew2 = new BlockPos(integer10, integer3, integer11);
                    final Optional<Vec3> optional17 = getStandingLocationAtOrBelow(ais, bhu, ew2);
                    if (optional17.isPresent()) {
                        if (integer <= 0) {
                            return optional17;
                        }
                        --integer;
                    }
                }
            }
        }
        return (Optional<Vec3>)Optional.empty();
    }
    
    protected static Optional<Vec3> getStandingLocationAtOrBelow(final EntityType<?> ais, final LevelReader bhu, final BlockPos ew) {
        final VoxelShape ctc4 = bhu.getBlockState(ew).getCollisionShape(bhu, ew);
        if (ctc4.max(Direction.Axis.Y) > 0.4375) {
            return (Optional<Vec3>)Optional.empty();
        }
        final BlockPos.MutableBlockPos a5 = new BlockPos.MutableBlockPos(ew);
        while (a5.getY() >= 0 && ew.getY() - a5.getY() <= 2 && bhu.getBlockState(a5).getCollisionShape(bhu, a5).isEmpty()) {
            a5.move(Direction.DOWN);
        }
        final VoxelShape ctc5 = bhu.getBlockState(a5).getCollisionShape(bhu, a5);
        if (ctc5.isEmpty()) {
            return (Optional<Vec3>)Optional.empty();
        }
        final double double7 = a5.getY() + ctc5.max(Direction.Axis.Y) + 2.0E-7;
        if (ew.getY() - double7 > 2.0) {
            return (Optional<Vec3>)Optional.empty();
        }
        final float float9 = ais.getWidth() / 2.0f;
        final Vec3 csi10 = new Vec3(a5.getX() + 0.5, double7, a5.getZ() + 0.5);
        if (bhu.noCollision(new AABB(csi10.x - float9, csi10.y, csi10.z - float9, csi10.x + float9, csi10.y + ais.getHeight(), csi10.z + float9))) {
            return (Optional<Vec3>)Optional.of(csi10);
        }
        return (Optional<Vec3>)Optional.empty();
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BedBlock.FACING, BedBlock.PART, BedBlock.OCCUPIED);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new BedBlockEntity(this.color);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        super.setPlacedBy(bhr, ew, bvt, aix, bcj);
        if (!bhr.isClientSide) {
            final BlockPos ew2 = ew.relative(bvt.<Direction>getValue((Property<Direction>)BedBlock.FACING));
            bhr.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)bvt).<BedPart, BedPart>setValue(BedBlock.PART, BedPart.HEAD), 3);
            bhr.blockUpdated(ew, Blocks.AIR);
            bvt.updateNeighbourShapes(bhr, ew, 3);
        }
    }
    
    public DyeColor getColor() {
        return this.color;
    }
    
    @Override
    public long getSeed(final BlockState bvt, final BlockPos ew) {
        final BlockPos ew2 = ew.relative(bvt.<Direction>getValue((Property<Direction>)BedBlock.FACING), (bvt.<BedPart>getValue(BedBlock.PART) != BedPart.HEAD) ? 1 : 0);
        return Mth.getSeed(ew2.getX(), ew.getY(), ew2.getZ());
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        PART = BlockStateProperties.BED_PART;
        OCCUPIED = BlockStateProperties.OCCUPIED;
        BASE = Block.box(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
        LEG_NORTH_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
        LEG_SOUTH_WEST = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
        LEG_NORTH_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
        LEG_SOUTH_EAST = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
        NORTH_SHAPE = Shapes.or(BedBlock.BASE, BedBlock.LEG_NORTH_WEST, BedBlock.LEG_NORTH_EAST);
        SOUTH_SHAPE = Shapes.or(BedBlock.BASE, BedBlock.LEG_SOUTH_WEST, BedBlock.LEG_SOUTH_EAST);
        WEST_SHAPE = Shapes.or(BedBlock.BASE, BedBlock.LEG_NORTH_WEST, BedBlock.LEG_SOUTH_WEST);
        EAST_SHAPE = Shapes.or(BedBlock.BASE, BedBlock.LEG_NORTH_EAST, BedBlock.LEG_SOUTH_EAST);
    }
}
