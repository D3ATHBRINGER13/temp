package net.minecraft.world.level.block;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.stats.Stats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class TrappedChestBlock extends ChestBlock {
    public TrappedChestBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new TrappedChestBlockEntity();
    }
    
    @Override
    protected Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return Mth.clamp(ChestBlockEntity.getOpenCount(bhb, ew), 0, 15);
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (fb == Direction.UP) {
            return bvt.getSignal(bhb, ew, fb);
        }
        return 0;
    }
}
