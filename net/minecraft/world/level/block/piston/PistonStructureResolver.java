package net.minecraft.world.level.block.piston;

import java.util.Collection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PistonStructureResolver {
    private final Level level;
    private final BlockPos pistonPos;
    private final boolean extending;
    private final BlockPos startPos;
    private final Direction pushDirection;
    private final List<BlockPos> toPush;
    private final List<BlockPos> toDestroy;
    private final Direction pistonDirection;
    
    public PistonStructureResolver(final Level bhr, final BlockPos ew, final Direction fb, final boolean boolean4) {
        this.toPush = (List<BlockPos>)Lists.newArrayList();
        this.toDestroy = (List<BlockPos>)Lists.newArrayList();
        this.level = bhr;
        this.pistonPos = ew;
        this.pistonDirection = fb;
        this.extending = boolean4;
        if (boolean4) {
            this.pushDirection = fb;
            this.startPos = ew.relative(fb);
        }
        else {
            this.pushDirection = fb.getOpposite();
            this.startPos = ew.relative(fb, 2);
        }
    }
    
    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        final BlockState bvt2 = this.level.getBlockState(this.startPos);
        if (!PistonBaseBlock.isPushable(bvt2, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending && bvt2.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(this.startPos);
                return true;
            }
            return false;
        }
        else {
            if (!this.addBlockLine(this.startPos, this.pushDirection)) {
                return false;
            }
            for (int integer3 = 0; integer3 < this.toPush.size(); ++integer3) {
                final BlockPos ew4 = (BlockPos)this.toPush.get(integer3);
                if (this.level.getBlockState(ew4).getBlock() == Blocks.SLIME_BLOCK && !this.addBranchingBlocks(ew4)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private boolean addBlockLine(final BlockPos ew, final Direction fb) {
        BlockState bvt4 = this.level.getBlockState(ew);
        Block bmv5 = bvt4.getBlock();
        if (bvt4.isAir()) {
            return true;
        }
        if (!PistonBaseBlock.isPushable(bvt4, this.level, ew, this.pushDirection, false, fb)) {
            return true;
        }
        if (ew.equals(this.pistonPos)) {
            return true;
        }
        if (this.toPush.contains(ew)) {
            return true;
        }
        int integer6 = 1;
        if (integer6 + this.toPush.size() > 12) {
            return false;
        }
        while (bmv5 == Blocks.SLIME_BLOCK) {
            final BlockPos ew2 = ew.relative(this.pushDirection.getOpposite(), integer6);
            bvt4 = this.level.getBlockState(ew2);
            bmv5 = bvt4.getBlock();
            if (bvt4.isAir() || !PistonBaseBlock.isPushable(bvt4, this.level, ew2, this.pushDirection, false, this.pushDirection.getOpposite())) {
                break;
            }
            if (ew2.equals(this.pistonPos)) {
                break;
            }
            if (++integer6 + this.toPush.size() > 12) {
                return false;
            }
        }
        int integer7 = 0;
        for (int integer8 = integer6 - 1; integer8 >= 0; --integer8) {
            this.toPush.add(ew.relative(this.pushDirection.getOpposite(), integer8));
            ++integer7;
        }
        int integer8 = 1;
        while (true) {
            final BlockPos ew3 = ew.relative(this.pushDirection, integer8);
            final int integer9 = this.toPush.indexOf(ew3);
            if (integer9 > -1) {
                this.reorderListAtCollision(integer7, integer9);
                for (int integer10 = 0; integer10 <= integer9 + integer7; ++integer10) {
                    final BlockPos ew4 = (BlockPos)this.toPush.get(integer10);
                    if (this.level.getBlockState(ew4).getBlock() == Blocks.SLIME_BLOCK && !this.addBranchingBlocks(ew4)) {
                        return false;
                    }
                }
                return true;
            }
            bvt4 = this.level.getBlockState(ew3);
            if (bvt4.isAir()) {
                return true;
            }
            if (!PistonBaseBlock.isPushable(bvt4, this.level, ew3, this.pushDirection, true, this.pushDirection) || ew3.equals(this.pistonPos)) {
                return false;
            }
            if (bvt4.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(ew3);
                return true;
            }
            if (this.toPush.size() >= 12) {
                return false;
            }
            this.toPush.add(ew3);
            ++integer7;
            ++integer8;
        }
    }
    
    private void reorderListAtCollision(final int integer1, final int integer2) {
        final List<BlockPos> list4 = (List<BlockPos>)Lists.newArrayList();
        final List<BlockPos> list5 = (List<BlockPos>)Lists.newArrayList();
        final List<BlockPos> list6 = (List<BlockPos>)Lists.newArrayList();
        list4.addAll((Collection)this.toPush.subList(0, integer2));
        list5.addAll((Collection)this.toPush.subList(this.toPush.size() - integer1, this.toPush.size()));
        list6.addAll((Collection)this.toPush.subList(integer2, this.toPush.size() - integer1));
        this.toPush.clear();
        this.toPush.addAll((Collection)list4);
        this.toPush.addAll((Collection)list5);
        this.toPush.addAll((Collection)list6);
    }
    
    private boolean addBranchingBlocks(final BlockPos ew) {
        for (final Direction fb6 : Direction.values()) {
            if (fb6.getAxis() != this.pushDirection.getAxis() && !this.addBlockLine(ew.relative(fb6), fb6)) {
                return false;
            }
        }
        return true;
    }
    
    public List<BlockPos> getToPush() {
        return this.toPush;
    }
    
    public List<BlockPos> getToDestroy() {
        return this.toDestroy;
    }
}
