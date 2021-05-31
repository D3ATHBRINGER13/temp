package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BonusChestFeature extends Feature<NoneFeatureConfiguration> {
    public BonusChestFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (BlockState bvt7 = bhs.getBlockState(ew); (bvt7.isAir() || bvt7.is(BlockTags.LEAVES)) && ew.getY() > 1; ew = ew.below(), bvt7 = bhs.getBlockState(ew)) {}
        if (ew.getY() < 1) {
            return false;
        }
        ew = ew.above();
        for (int integer8 = 0; integer8 < 4; ++integer8) {
            final BlockPos ew2 = ew.offset(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
            if (bhs.isEmptyBlock(ew2)) {
                bhs.setBlock(ew2, Blocks.CHEST.defaultBlockState(), 2);
                RandomizableContainerBlockEntity.setLootTable(bhs, random, ew2, BuiltInLootTables.SPAWN_BONUS_CHEST);
                final BlockState bvt8 = Blocks.TORCH.defaultBlockState();
                for (final Direction fb12 : Direction.Plane.HORIZONTAL) {
                    final BlockPos ew3 = ew2.relative(fb12);
                    if (bvt8.canSurvive(bhs, ew3)) {
                        bhs.setBlock(ew3, bvt8, 2);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
