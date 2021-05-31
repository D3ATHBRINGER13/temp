package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Random;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class MegaPineTreeFeature extends MegaTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState TRUNK;
    private static final BlockState LEAF;
    private static final BlockState PODZOL;
    private final boolean isSpruce;
    
    public MegaPineTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2, final boolean boolean3) {
        super(function, boolean2, 13, 15, MegaPineTreeFeature.TRUNK, MegaPineTreeFeature.LEAF);
        this.isSpruce = boolean3;
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = this.calcTreeHeigth(random);
        if (!this.prepareTree(bhw, ew, integer7)) {
            return false;
        }
        this.createCrown(bhw, ew.getX(), ew.getZ(), ew.getY() + integer7, 0, random, cic, set);
        for (int integer8 = 0; integer8 < integer7; ++integer8) {
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.above(integer8))) {
                this.setBlock(set, bhw, ew.above(integer8), this.trunk, cic);
            }
            if (integer8 < integer7 - 1) {
                if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.offset(1, integer8, 0))) {
                    this.setBlock(set, bhw, ew.offset(1, integer8, 0), this.trunk, cic);
                }
                if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.offset(1, integer8, 1))) {
                    this.setBlock(set, bhw, ew.offset(1, integer8, 1), this.trunk, cic);
                }
                if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.offset(0, integer8, 1))) {
                    this.setBlock(set, bhw, ew.offset(0, integer8, 1), this.trunk, cic);
                }
            }
        }
        this.postPlaceTree(bhw, random, ew);
        return true;
    }
    
    private void createCrown(final LevelSimulatedRW bhw, final int integer2, final int integer3, final int integer4, final int integer5, final Random random, final BoundingBox cic, final Set<BlockPos> set) {
        final int integer6 = random.nextInt(5) + (this.isSpruce ? this.baseHeight : 3);
        int integer7 = 0;
        for (int integer8 = integer4 - integer6; integer8 <= integer4; ++integer8) {
            final int integer9 = integer4 - integer8;
            final int integer10 = integer5 + Mth.floor(integer9 / (float)integer6 * 3.5f);
            this.placeDoubleTrunkLeaves(bhw, new BlockPos(integer2, integer8, integer3), integer10 + ((integer9 > 0 && integer10 == integer7 && (integer8 & 0x1) == 0x0) ? 1 : 0), cic, set);
            integer7 = integer10;
        }
    }
    
    public void postPlaceTree(final LevelSimulatedRW bhw, final Random random, final BlockPos ew) {
        this.placePodzolCircle(bhw, ew.west().north());
        this.placePodzolCircle(bhw, ew.east(2).north());
        this.placePodzolCircle(bhw, ew.west().south(2));
        this.placePodzolCircle(bhw, ew.east(2).south(2));
        for (int integer5 = 0; integer5 < 5; ++integer5) {
            final int integer6 = random.nextInt(64);
            final int integer7 = integer6 % 8;
            final int integer8 = integer6 / 8;
            if (integer7 == 0 || integer7 == 7 || integer8 == 0 || integer8 == 7) {
                this.placePodzolCircle(bhw, ew.offset(-3 + integer7, 0, -3 + integer8));
            }
        }
    }
    
    private void placePodzolCircle(final LevelSimulatedRW bhw, final BlockPos ew) {
        for (int integer4 = -2; integer4 <= 2; ++integer4) {
            for (int integer5 = -2; integer5 <= 2; ++integer5) {
                if (Math.abs(integer4) != 2 || Math.abs(integer5) != 2) {
                    this.placePodzolAt(bhw, ew.offset(integer4, 0, integer5));
                }
            }
        }
    }
    
    private void placePodzolAt(final LevelSimulatedRW bhw, final BlockPos ew) {
        for (int integer4 = 2; integer4 >= -3; --integer4) {
            final BlockPos ew2 = ew.above(integer4);
            if (AbstractTreeFeature.isGrassOrDirt(bhw, ew2)) {
                this.setBlock(bhw, ew2, MegaPineTreeFeature.PODZOL);
                break;
            }
            if (!AbstractTreeFeature.isAir(bhw, ew2) && integer4 < 0) {
                break;
            }
        }
    }
    
    static {
        TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
        LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();
        PODZOL = Blocks.PODZOL.defaultBlockState();
    }
}
