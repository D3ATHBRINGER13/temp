package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Iterator;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Vec3i;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class EndPodiumFeature extends Feature<NoneFeatureConfiguration> {
    public static final BlockPos END_PODIUM_LOCATION;
    private final boolean active;
    
    public EndPodiumFeature(final boolean boolean1) {
        super(NoneFeatureConfiguration::deserialize);
        this.active = boolean1;
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (final BlockPos ew2 : BlockPos.betweenClosed(new BlockPos(ew.getX() - 4, ew.getY() - 1, ew.getZ() - 4), new BlockPos(ew.getX() + 4, ew.getY() + 32, ew.getZ() + 4))) {
            final boolean boolean9 = ew2.closerThan(ew, 2.5);
            if (boolean9 || ew2.closerThan(ew, 3.5)) {
                if (ew2.getY() < ew.getY()) {
                    if (boolean9) {
                        this.setBlock(bhs, ew2, Blocks.BEDROCK.defaultBlockState());
                    }
                    else {
                        if (ew2.getY() >= ew.getY()) {
                            continue;
                        }
                        this.setBlock(bhs, ew2, Blocks.END_STONE.defaultBlockState());
                    }
                }
                else if (ew2.getY() > ew.getY()) {
                    this.setBlock(bhs, ew2, Blocks.AIR.defaultBlockState());
                }
                else if (!boolean9) {
                    this.setBlock(bhs, ew2, Blocks.BEDROCK.defaultBlockState());
                }
                else if (this.active) {
                    this.setBlock(bhs, new BlockPos(ew2), Blocks.END_PORTAL.defaultBlockState());
                }
                else {
                    this.setBlock(bhs, new BlockPos(ew2), Blocks.AIR.defaultBlockState());
                }
            }
        }
        for (int integer7 = 0; integer7 < 4; ++integer7) {
            this.setBlock(bhs, ew.above(integer7), Blocks.BEDROCK.defaultBlockState());
        }
        final BlockPos ew3 = ew.above(2);
        for (final Direction fb9 : Direction.Plane.HORIZONTAL) {
            this.setBlock(bhs, ew3.relative(fb9), ((AbstractStateHolder<O, BlockState>)Blocks.WALL_TORCH.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)WallTorchBlock.FACING, fb9));
        }
        return true;
    }
    
    static {
        END_PODIUM_LOCATION = BlockPos.ZERO;
    }
}
