package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
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

public class CoralClawFeature extends CoralFeature {
    public CoralClawFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected boolean placeFeature(final LevelAccessor bhs, final Random random, final BlockPos ew, final BlockState bvt) {
        if (!this.placeCoralBlock(bhs, random, ew, bvt)) {
            return false;
        }
        final Direction fb6 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        final int integer7 = random.nextInt(2) + 2;
        final List<Direction> list8 = (List<Direction>)Lists.newArrayList((Object[])new Direction[] { fb6, fb6.getClockWise(), fb6.getCounterClockWise() });
        Collections.shuffle((List)list8, random);
        final List<Direction> list9 = (List<Direction>)list8.subList(0, integer7);
        for (final Direction fb7 : list9) {
            final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos(ew);
            final int integer8 = random.nextInt(2) + 1;
            a12.move(fb7);
            Direction fb8;
            int integer9;
            if (fb7 == fb6) {
                fb8 = fb6;
                integer9 = random.nextInt(3) + 2;
            }
            else {
                a12.move(Direction.UP);
                final Direction[] arr16 = { fb7, Direction.UP };
                fb8 = arr16[random.nextInt(arr16.length)];
                integer9 = random.nextInt(3) + 3;
            }
            for (int integer10 = 0; integer10 < integer8 && this.placeCoralBlock(bhs, random, a12, bvt); ++integer10) {
                a12.move(fb8);
            }
            a12.move(fb8.getOpposite());
            a12.move(Direction.UP);
            for (int integer10 = 0; integer10 < integer9; ++integer10) {
                a12.move(fb6);
                if (!this.placeCoralBlock(bhs, random, a12, bvt)) {
                    break;
                }
                if (random.nextFloat() < 0.25f) {
                    a12.move(Direction.UP);
                }
            }
        }
        return true;
    }
}
