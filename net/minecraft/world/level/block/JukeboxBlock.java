package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class JukeboxBlock extends BaseEntityBlock {
    public static final BooleanProperty HAS_RECORD;
    
    protected JukeboxBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)JukeboxBlock.HAS_RECORD, false));
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bvt.<Boolean>getValue((Property<Boolean>)JukeboxBlock.HAS_RECORD)) {
            this.dropRecording(bhr, ew);
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)JukeboxBlock.HAS_RECORD, false);
            bhr.setBlock(ew, bvt, 2);
            return true;
        }
        return false;
    }
    
    public void setRecord(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final ItemStack bcj) {
        final BlockEntity btw6 = bhs.getBlockEntity(ew);
        if (!(btw6 instanceof JukeboxBlockEntity)) {
            return;
        }
        ((JukeboxBlockEntity)btw6).setRecord(bcj.copy());
        bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)JukeboxBlock.HAS_RECORD, true), 2);
    }
    
    private void dropRecording(final Level bhr, final BlockPos ew) {
        if (bhr.isClientSide) {
            return;
        }
        final BlockEntity btw4 = bhr.getBlockEntity(ew);
        if (!(btw4 instanceof JukeboxBlockEntity)) {
            return;
        }
        final JukeboxBlockEntity bun5 = (JukeboxBlockEntity)btw4;
        final ItemStack bcj6 = bun5.getRecord();
        if (bcj6.isEmpty()) {
            return;
        }
        bhr.levelEvent(1010, ew, 0);
        bun5.clearContent();
        final float float7 = 0.7f;
        final double double8 = bhr.random.nextFloat() * 0.7f + 0.15000000596046448;
        final double double9 = bhr.random.nextFloat() * 0.7f + 0.06000000238418579 + 0.6;
        final double double10 = bhr.random.nextFloat() * 0.7f + 0.15000000596046448;
        final ItemStack bcj7 = bcj6.copy();
        final ItemEntity atx15 = new ItemEntity(bhr, ew.getX() + double8, ew.getY() + double9, ew.getZ() + double10, bcj7);
        atx15.setDefaultPickUpDelay();
        bhr.addFreshEntity(atx15);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        this.dropRecording(bhr, ew);
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new JukeboxBlockEntity();
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof JukeboxBlockEntity) {
            final Item bce6 = ((JukeboxBlockEntity)btw5).getRecord().getItem();
            if (bce6 instanceof RecordItem) {
                return ((RecordItem)bce6).getAnalogOutput();
            }
        }
        return 0;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(JukeboxBlock.HAS_RECORD);
    }
    
    static {
        HAS_RECORD = BlockStateProperties.HAS_RECORD;
    }
}
