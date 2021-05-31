package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;

public class CatSitOnBlockGoal extends MoveToBlockGoal {
    private final Cat cat;
    
    public CatSitOnBlockGoal(final Cat arb, final double double2) {
        super(arb, double2, 8);
        this.cat = arb;
    }
    
    @Override
    public boolean canUse() {
        return this.cat.isTame() && !this.cat.isSitting() && super.canUse();
    }
    
    @Override
    public void start() {
        super.start();
        this.cat.getSitGoal().wantToSit(false);
    }
    
    @Override
    public void stop() {
        super.stop();
        this.cat.setSitting(false);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.cat.getSitGoal().wantToSit(false);
        if (!this.isReachedTarget()) {
            this.cat.setSitting(false);
        }
        else if (!this.cat.isSitting()) {
            this.cat.setSitting(true);
        }
    }
    
    @Override
    protected boolean isValidTarget(final LevelReader bhu, final BlockPos ew) {
        if (!bhu.isEmptyBlock(ew.above())) {
            return false;
        }
        final BlockState bvt4 = bhu.getBlockState(ew);
        final Block bmv5 = bvt4.getBlock();
        if (bmv5 == Blocks.CHEST) {
            return ChestBlockEntity.getOpenCount(bhu, ew) < 1;
        }
        return (bmv5 == Blocks.FURNACE && bvt4.<Boolean>getValue((Property<Boolean>)FurnaceBlock.LIT)) || (bmv5.is(BlockTags.BEDS) && bvt4.<BedPart>getValue(BedBlock.PART) != BedPart.HEAD);
    }
}
