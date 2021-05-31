package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.block.state.BlockState;

public class NetherSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    private static final BlockState AIR;
    private static final BlockState NETHERRACK;
    private static final BlockState GRAVEL;
    private static final BlockState SOUL_SAND;
    protected long seed;
    protected PerlinNoise decorationNoise;
    
    public NetherSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        final int integer11 = integer10 + 1;
        final int integer12 = integer4 & 0xF;
        final int integer13 = integer5 & 0xF;
        final double double8 = 0.03125;
        final boolean boolean21 = this.decorationNoise.getValue(integer4 * 0.03125, integer5 * 0.03125, 0.0) + random.nextDouble() * 0.2 > 0.0;
        final boolean boolean22 = this.decorationNoise.getValue(integer4 * 0.03125, 109.0, integer5 * 0.03125) + random.nextDouble() * 0.2 > 0.0;
        final int integer14 = (int)(double7 / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final BlockPos.MutableBlockPos a24 = new BlockPos.MutableBlockPos();
        int integer15 = -1;
        BlockState bvt10 = NetherSurfaceBuilder.NETHERRACK;
        BlockState bvt11 = NetherSurfaceBuilder.NETHERRACK;
        for (int integer16 = 127; integer16 >= 0; --integer16) {
            a24.set(integer12, integer16, integer13);
            final BlockState bvt12 = bxh.getBlockState(a24);
            if (bvt12.getBlock() == null || bvt12.isAir()) {
                integer15 = -1;
            }
            else if (bvt12.getBlock() == bvt8.getBlock()) {
                if (integer15 == -1) {
                    if (integer14 <= 0) {
                        bvt10 = NetherSurfaceBuilder.AIR;
                        bvt11 = NetherSurfaceBuilder.NETHERRACK;
                    }
                    else if (integer16 >= integer11 - 4 && integer16 <= integer11 + 1) {
                        bvt10 = NetherSurfaceBuilder.NETHERRACK;
                        bvt11 = NetherSurfaceBuilder.NETHERRACK;
                        if (boolean22) {
                            bvt10 = NetherSurfaceBuilder.GRAVEL;
                            bvt11 = NetherSurfaceBuilder.NETHERRACK;
                        }
                        if (boolean21) {
                            bvt10 = NetherSurfaceBuilder.SOUL_SAND;
                            bvt11 = NetherSurfaceBuilder.SOUL_SAND;
                        }
                    }
                    if (integer16 < integer11 && (bvt10 == null || bvt10.isAir())) {
                        bvt10 = bvt9;
                    }
                    integer15 = integer14;
                    if (integer16 >= integer11 - 1) {
                        bxh.setBlockState(a24, bvt10, false);
                    }
                    else {
                        bxh.setBlockState(a24, bvt11, false);
                    }
                }
                else if (integer15 > 0) {
                    --integer15;
                    bxh.setBlockState(a24, bvt11, false);
                }
            }
        }
    }
    
    @Override
    public void initNoise(final long long1) {
        if (this.seed != long1 || this.decorationNoise == null) {
            this.decorationNoise = new PerlinNoise(new WorldgenRandom(long1), 4);
        }
        this.seed = long1;
    }
    
    static {
        AIR = Blocks.CAVE_AIR.defaultBlockState();
        NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
        GRAVEL = Blocks.GRAVEL.defaultBlockState();
        SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
    }
}
