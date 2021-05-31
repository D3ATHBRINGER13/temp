package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
    public static final BooleanProperty POWERED;
    private final Sensitivity sensitivity;
    
    protected PressurePlateBlock(final Sensitivity a, final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)PressurePlateBlock.POWERED, false));
        this.sensitivity = a;
    }
    
    @Override
    protected int getSignalForState(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)PressurePlateBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    protected BlockState setSignalForState(final BlockState bvt, final int integer) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)PressurePlateBlock.POWERED, integer > 0);
    }
    
    @Override
    protected void playOnSound(final LevelAccessor bhs, final BlockPos ew) {
        if (this.material == Material.WOOD) {
            bhs.playSound(null, ew, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3f, 0.8f);
        }
        else {
            bhs.playSound(null, ew, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3f, 0.6f);
        }
    }
    
    @Override
    protected void playOffSound(final LevelAccessor bhs, final BlockPos ew) {
        if (this.material == Material.WOOD) {
            bhs.playSound(null, ew, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3f, 0.7f);
        }
        else {
            bhs.playSound(null, ew, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3f, 0.5f);
        }
    }
    
    @Override
    protected int getSignalStrength(final Level bhr, final BlockPos ew) {
        final AABB csc4 = PressurePlateBlock.TOUCH_AABB.move(ew);
        List<? extends Entity> list5 = null;
        switch (this.sensitivity) {
            case EVERYTHING: {
                list5 = bhr.getEntities(null, csc4);
                break;
            }
            case MOBS: {
                list5 = bhr.getEntitiesOfClass((java.lang.Class<? extends Entity>)LivingEntity.class, csc4);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!list5.isEmpty()) {
            for (final Entity aio7 : list5) {
                if (!aio7.isIgnoringBlockTriggers()) {
                    return 15;
                }
            }
        }
        return 0;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(PressurePlateBlock.POWERED);
    }
    
    static {
        POWERED = BlockStateProperties.POWERED;
    }
    
    public enum Sensitivity {
        EVERYTHING, 
        MOBS;
    }
}
