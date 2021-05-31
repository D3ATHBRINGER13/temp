package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class StructureBlock extends BaseEntityBlock {
    public static final EnumProperty<StructureMode> MODE;
    
    protected StructureBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new StructureBlockEntity();
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        return btw8 instanceof StructureBlockEntity && ((StructureBlockEntity)btw8).usedBy(awg);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        if (bhr.isClientSide) {
            return;
        }
        if (aix != null) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof StructureBlockEntity) {
                ((StructureBlockEntity)btw7).createdBy(aix);
            }
        }
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<StructureMode, StructureMode>setValue(StructureBlock.MODE, StructureMode.DATA);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(StructureBlock.MODE);
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew3);
        if (!(btw8 instanceof StructureBlockEntity)) {
            return;
        }
        final StructureBlockEntity buw9 = (StructureBlockEntity)btw8;
        final boolean boolean7 = bhr.hasNeighborSignal(ew3);
        final boolean boolean8 = buw9.isPowered();
        if (boolean7 && !boolean8) {
            buw9.setPowered(true);
            this.trigger(buw9);
        }
        else if (!boolean7 && boolean8) {
            buw9.setPowered(false);
        }
    }
    
    private void trigger(final StructureBlockEntity buw) {
        switch (buw.getMode()) {
            case SAVE: {
                buw.saveStructure(false);
                break;
            }
            case LOAD: {
                buw.loadStructure(false);
                break;
            }
            case CORNER: {
                buw.unloadStructure();
            }
        }
    }
    
    static {
        MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;
    }
}
