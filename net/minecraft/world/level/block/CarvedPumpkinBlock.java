package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.BlockPlaceContext;
import java.util.Iterator;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING;
    @Nullable
    private BlockPattern snowGolemBase;
    @Nullable
    private BlockPattern snowGolemFull;
    @Nullable
    private BlockPattern ironGolemBase;
    @Nullable
    private BlockPattern ironGolemFull;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE;
    
    protected CarvedPumpkinBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)CarvedPumpkinBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        this.trySpawnGolem(bhr, ew);
    }
    
    public boolean canSpawnGolem(final LevelReader bhu, final BlockPos ew) {
        return this.getOrCreateSnowGolemBase().find(bhu, ew) != null || this.getOrCreateIronGolemBase().find(bhu, ew) != null;
    }
    
    private void trySpawnGolem(final Level bhr, final BlockPos ew) {
        BlockPattern.BlockPatternMatch b4 = this.getOrCreateSnowGolemFull().find(bhr, ew);
        if (b4 != null) {
            for (int integer5 = 0; integer5 < this.getOrCreateSnowGolemFull().getHeight(); ++integer5) {
                final BlockInWorld bvx6 = b4.getBlock(0, integer5, 0);
                bhr.setBlock(bvx6.getPos(), Blocks.AIR.defaultBlockState(), 2);
                bhr.levelEvent(2001, bvx6.getPos(), Block.getId(bvx6.getState()));
            }
            final SnowGolem aru5 = EntityType.SNOW_GOLEM.create(bhr);
            final BlockPos ew2 = b4.getBlock(0, 2, 0).getPos();
            aru5.moveTo(ew2.getX() + 0.5, ew2.getY() + 0.05, ew2.getZ() + 0.5, 0.0f, 0.0f);
            bhr.addFreshEntity(aru5);
            for (final ServerPlayer vl8 : bhr.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)ServerPlayer.class, aru5.getBoundingBox().inflate(5.0))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(vl8, aru5);
            }
            for (int integer6 = 0; integer6 < this.getOrCreateSnowGolemFull().getHeight(); ++integer6) {
                final BlockInWorld bvx7 = b4.getBlock(0, integer6, 0);
                bhr.blockUpdated(bvx7.getPos(), Blocks.AIR);
            }
        }
        else {
            b4 = this.getOrCreateIronGolemFull().find(bhr, ew);
            if (b4 != null) {
                for (int integer5 = 0; integer5 < this.getOrCreateIronGolemFull().getWidth(); ++integer5) {
                    for (int integer7 = 0; integer7 < this.getOrCreateIronGolemFull().getHeight(); ++integer7) {
                        final BlockInWorld bvx8 = b4.getBlock(integer5, integer7, 0);
                        bhr.setBlock(bvx8.getPos(), Blocks.AIR.defaultBlockState(), 2);
                        bhr.levelEvent(2001, bvx8.getPos(), Block.getId(bvx8.getState()));
                    }
                }
                final BlockPos ew3 = b4.getBlock(1, 2, 0).getPos();
                final IronGolem ari6 = EntityType.IRON_GOLEM.create(bhr);
                ari6.setPlayerCreated(true);
                ari6.moveTo(ew3.getX() + 0.5, ew3.getY() + 0.05, ew3.getZ() + 0.5, 0.0f, 0.0f);
                bhr.addFreshEntity(ari6);
                for (final ServerPlayer vl8 : bhr.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)ServerPlayer.class, ari6.getBoundingBox().inflate(5.0))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(vl8, ari6);
                }
                for (int integer6 = 0; integer6 < this.getOrCreateIronGolemFull().getWidth(); ++integer6) {
                    for (int integer8 = 0; integer8 < this.getOrCreateIronGolemFull().getHeight(); ++integer8) {
                        final BlockInWorld bvx9 = b4.getBlock(integer6, integer8, 0);
                        bhr.blockUpdated(bvx9.getPos(), Blocks.AIR);
                    }
                }
            }
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)CarvedPumpkinBlock.FACING, ban.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CarvedPumpkinBlock.FACING);
    }
    
    private BlockPattern getOrCreateSnowGolemBase() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemBase;
    }
    
    private BlockPattern getOrCreateSnowGolemFull() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(CarvedPumpkinBlock.PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemFull;
    }
    
    private BlockPattern getOrCreateIronGolemBase() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState((Predicate<BlockState>)BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return this.ironGolemBase;
    }
    
    private BlockPattern getOrCreateIronGolemFull() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(CarvedPumpkinBlock.PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState((Predicate<BlockState>)BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return this.ironGolemFull;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        PUMPKINS_PREDICATE = (bvt -> bvt != null && (bvt.getBlock() == Blocks.CARVED_PUMPKIN || bvt.getBlock() == Blocks.JACK_O_LANTERN));
    }
}
