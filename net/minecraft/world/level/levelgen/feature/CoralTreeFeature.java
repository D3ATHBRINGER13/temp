package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.core.Vec3i;
import java.util.List;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CoralTreeFeature extends CoralFeature {
    public CoralTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected boolean placeFeature(final LevelAccessor bhs, final Random random, final BlockPos ew, final BlockState bvt) {
        final BlockPos.MutableBlockPos a6 = new BlockPos.MutableBlockPos(ew);
        for (int integer7 = random.nextInt(3) + 1, integer8 = 0; integer8 < integer7; ++integer8) {
            if (!this.placeCoralBlock(bhs, random, a6, bvt)) {
                return true;
            }
            a6.move(Direction.UP);
        }
        final BlockPos ew2 = a6.immutable();
        final int integer9 = random.nextInt(3) + 2;
        final List<Direction> list10 = (List<Direction>)Lists.newArrayList((Iterable)Direction.Plane.HORIZONTAL);
        Collections.shuffle((List)list10, random);
        final List<Direction> list11 = (List<Direction>)list10.subList(0, integer9);
        for (final Direction fb13 : list11) {
            a6.set(ew2);
            a6.move(fb13);
            final int integer10 = random.nextInt(5) + 2;
            int integer11 = 0;
            for (int integer12 = 0; integer12 < integer10 && this.placeCoralBlock(bhs, random, a6, bvt); ++integer12) {
                ++integer11;
                a6.move(Direction.UP);
                if (integer12 == 0 || (integer11 >= 2 && random.nextFloat() < 0.25f)) {
                    a6.move(fb13);
                    integer11 = 0;
                }
            }
        }
        return true;
    }
}
