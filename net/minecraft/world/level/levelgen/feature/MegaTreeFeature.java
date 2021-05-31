package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.LevelWriter;
import java.util.Set;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MegaTreeFeature<T extends FeatureConfiguration> extends AbstractTreeFeature<T> {
    protected final int baseHeight;
    protected final BlockState trunk;
    protected final BlockState leaf;
    protected final int heightInterval;
    
    public MegaTreeFeature(final Function<Dynamic<?>, ? extends T> function, final boolean boolean2, final int integer3, final int integer4, final BlockState bvt5, final BlockState bvt6) {
        super(function, boolean2);
        this.baseHeight = integer3;
        this.heightInterval = integer4;
        this.trunk = bvt5;
        this.leaf = bvt6;
    }
    
    protected int calcTreeHeigth(final Random random) {
        int integer3 = random.nextInt(3) + this.baseHeight;
        if (this.heightInterval > 1) {
            integer3 += random.nextInt(this.heightInterval);
        }
        return integer3;
    }
    
    private boolean checkIsFree(final LevelSimulatedReader bhx, final BlockPos ew, final int integer) {
        boolean boolean5 = true;
        if (ew.getY() < 1 || ew.getY() + integer + 1 > 256) {
            return false;
        }
        for (int integer2 = 0; integer2 <= 1 + integer; ++integer2) {
            int integer3 = 2;
            if (integer2 == 0) {
                integer3 = 1;
            }
            else if (integer2 >= 1 + integer - 2) {
                integer3 = 2;
            }
            for (int integer4 = -integer3; integer4 <= integer3 && boolean5; ++integer4) {
                for (int integer5 = -integer3; integer5 <= integer3 && boolean5; ++integer5) {
                    if (ew.getY() + integer2 < 0 || ew.getY() + integer2 >= 256 || !AbstractTreeFeature.isFree(bhx, ew.offset(integer4, integer2, integer5))) {
                        boolean5 = false;
                    }
                }
            }
        }
        return boolean5;
    }
    
    private boolean makeDirtFloor(final LevelSimulatedRW bhw, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        if (!AbstractTreeFeature.isGrassOrDirt(bhw, ew2) || ew.getY() < 2) {
            return false;
        }
        this.setDirtAt(bhw, ew2);
        this.setDirtAt(bhw, ew2.east());
        this.setDirtAt(bhw, ew2.south());
        this.setDirtAt(bhw, ew2.south().east());
        return true;
    }
    
    protected boolean prepareTree(final LevelSimulatedRW bhw, final BlockPos ew, final int integer) {
        return this.checkIsFree(bhw, ew, integer) && this.makeDirtFloor(bhw, ew);
    }
    
    protected void placeDoubleTrunkLeaves(final LevelSimulatedRW bhw, final BlockPos ew, final int integer, final BoundingBox cic, final Set<BlockPos> set) {
        final int integer2 = integer * integer;
        for (int integer3 = -integer; integer3 <= integer + 1; ++integer3) {
            for (int integer4 = -integer; integer4 <= integer + 1; ++integer4) {
                final int integer5 = Math.min(Math.abs(integer3), Math.abs(integer3 - 1));
                final int integer6 = Math.min(Math.abs(integer4), Math.abs(integer4 - 1));
                if (integer5 + integer6 < 7) {
                    if (integer5 * integer5 + integer6 * integer6 <= integer2) {
                        final BlockPos ew2 = ew.offset(integer3, 0, integer4);
                        if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                            this.setBlock(set, bhw, ew2, this.leaf, cic);
                        }
                    }
                }
            }
        }
    }
    
    protected void placeSingleTrunkLeaves(final LevelSimulatedRW bhw, final BlockPos ew, final int integer, final BoundingBox cic, final Set<BlockPos> set) {
        final int integer2 = integer * integer;
        for (int integer3 = -integer; integer3 <= integer; ++integer3) {
            for (int integer4 = -integer; integer4 <= integer; ++integer4) {
                if (integer3 * integer3 + integer4 * integer4 <= integer2) {
                    final BlockPos ew2 = ew.offset(integer3, 0, integer4);
                    if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                        this.setBlock(set, bhw, ew2, this.leaf, cic);
                    }
                }
            }
        }
    }
}
