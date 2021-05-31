package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Random;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class GroundBushFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private final BlockState leaf;
    private final BlockState trunk;
    
    public GroundBushFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final BlockState bvt2, final BlockState bvt3) {
        super(function, false);
        this.trunk = bvt2;
        this.leaf = bvt3;
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, BlockPos ew, final BoundingBox cic) {
        ew = bhw.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ew).below();
        if (AbstractTreeFeature.isGrassOrDirt(bhw, ew)) {
            ew = ew.above();
            this.setBlock(set, bhw, ew, this.trunk, cic);
            for (int integer7 = ew.getY(); integer7 <= ew.getY() + 2; ++integer7) {
                final int integer8 = integer7 - ew.getY();
                for (int integer9 = 2 - integer8, integer10 = ew.getX() - integer9; integer10 <= ew.getX() + integer9; ++integer10) {
                    final int integer11 = integer10 - ew.getX();
                    for (int integer12 = ew.getZ() - integer9; integer12 <= ew.getZ() + integer9; ++integer12) {
                        final int integer13 = integer12 - ew.getZ();
                        if (Math.abs(integer11) != integer9 || Math.abs(integer13) != integer9 || random.nextInt(2) != 0) {
                            final BlockPos ew2 = new BlockPos(integer10, integer7, integer12);
                            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                                this.setBlock(set, bhw, ew2, this.leaf, cic);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
