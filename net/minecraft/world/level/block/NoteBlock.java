package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class NoteBlock extends Block {
    public static final EnumProperty<NoteBlockInstrument> INSTRUMENT;
    public static final BooleanProperty POWERED;
    public static final IntegerProperty NOTE;
    
    public NoteBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.HARP)).setValue((Property<Comparable>)NoteBlock.NOTE, 0)).<Comparable, Boolean>setValue((Property<Comparable>)NoteBlock.POWERED, false));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<NoteBlockInstrument, NoteBlockInstrument>setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.byState(ban.getLevel().getBlockState(ban.getClickedPos().below())));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<NoteBlockInstrument, NoteBlockInstrument>setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.byState(bvt3));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        final boolean boolean7 = bhr.hasNeighborSignal(ew3);
        if (boolean7 != bvt.<Boolean>getValue((Property<Boolean>)NoteBlock.POWERED)) {
            if (boolean7) {
                this.playNote(bhr, ew3);
            }
            bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)NoteBlock.POWERED, boolean7), 3);
        }
    }
    
    private void playNote(final Level bhr, final BlockPos ew) {
        if (bhr.getBlockState(ew.above()).isAir()) {
            bhr.blockEvent(ew, this, 0, 0);
        }
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)NoteBlock.NOTE);
        bhr.setBlock(ew, bvt, 3);
        this.playNote(bhr, ew);
        awg.awardStat(Stats.TUNE_NOTEBLOCK);
        return true;
    }
    
    @Override
    public void attack(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        if (bhr.isClientSide) {
            return;
        }
        this.playNote(bhr, ew);
        awg.awardStat(Stats.PLAY_NOTEBLOCK);
    }
    
    @Override
    public boolean triggerEvent(final BlockState bvt, final Level bhr, final BlockPos ew, final int integer4, final int integer5) {
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)NoteBlock.NOTE);
        final float float8 = (float)Math.pow(2.0, (integer6 - 12) / 12.0);
        bhr.playSound(null, ew, bvt.<NoteBlockInstrument>getValue(NoteBlock.INSTRUMENT).getSoundEvent(), SoundSource.RECORDS, 3.0f, float8);
        bhr.addParticle(ParticleTypes.NOTE, ew.getX() + 0.5, ew.getY() + 1.2, ew.getZ() + 0.5, integer6 / 24.0, 0.0, 0.0);
        return true;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(NoteBlock.INSTRUMENT, NoteBlock.POWERED, NoteBlock.NOTE);
    }
    
    static {
        INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
        POWERED = BlockStateProperties.POWERED;
        NOTE = BlockStateProperties.NOTE;
    }
}
