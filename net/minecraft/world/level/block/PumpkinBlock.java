package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PumpkinBlock extends StemGrownBlock {
    protected PumpkinBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final ItemStack bcj8 = awg.getItemInHand(ahi);
        if (bcj8.getItem() == Items.SHEARS) {
            if (!bhr.isClientSide) {
                final Direction fb9 = csd.getDirection();
                final Direction fb10 = (fb9.getAxis() == Direction.Axis.Y) ? awg.getDirection().getOpposite() : fb9;
                bhr.playSound(null, ew, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f);
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)Blocks.CARVED_PUMPKIN.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)CarvedPumpkinBlock.FACING, fb10), 11);
                final ItemEntity atx11 = new ItemEntity(bhr, ew.getX() + 0.5 + fb10.getStepX() * 0.65, ew.getY() + 0.1, ew.getZ() + 0.5 + fb10.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
                atx11.setDeltaMovement(0.05 * fb10.getStepX() + bhr.random.nextDouble() * 0.02, 0.05, 0.05 * fb10.getStepZ() + bhr.random.nextDouble() * 0.02);
                bhr.addFreshEntity(atx11);
                bcj8.<Player>hurtAndBreak(1, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
            }
            return true;
        }
        return super.use(bvt, bhr, ew, awg, ahi, csd);
    }
    
    @Override
    public StemBlock getStem() {
        return (StemBlock)Blocks.PUMPKIN_STEM;
    }
    
    @Override
    public AttachedStemBlock getAttachedStem() {
        return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
    }
}
