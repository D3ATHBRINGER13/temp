package net.minecraft.world.entity.ai.goal;

import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;

public class CatLieOnBedGoal extends MoveToBlockGoal {
    private final Cat cat;
    
    public CatLieOnBedGoal(final Cat arb, final double double2, final int integer) {
        super(arb, double2, integer, 6);
        this.cat = arb;
        this.verticalSearchStart = -2;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP, (Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        return this.cat.isTame() && !this.cat.isSitting() && !this.cat.isLying() && super.canUse();
    }
    
    @Override
    public void start() {
        super.start();
        this.cat.getSitGoal().wantToSit(false);
    }
    
    @Override
    protected int nextStartTick(final PathfinderMob aje) {
        return 40;
    }
    
    @Override
    public void stop() {
        super.stop();
        this.cat.setLying(false);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.cat.getSitGoal().wantToSit(false);
        if (!this.isReachedTarget()) {
            this.cat.setLying(false);
        }
        else if (!this.cat.isLying()) {
            this.cat.setLying(true);
        }
    }
    
    @Override
    protected boolean isValidTarget(final LevelReader bhu, final BlockPos ew) {
        return bhu.isEmptyBlock(ew.above()) && bhu.getBlockState(ew).getBlock().is(BlockTags.BEDS);
    }
}
