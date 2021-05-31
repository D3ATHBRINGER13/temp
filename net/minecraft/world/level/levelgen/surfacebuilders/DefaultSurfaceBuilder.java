package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DefaultSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    public DefaultSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        this.apply(random, bxh, bio, integer4, integer5, integer6, double7, bvt8, bvt9, cki.getTopMaterial(), cki.getUnderMaterial(), cki.getUnderwaterMaterial(), integer10);
    }
    
    protected void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final BlockState bvt10, final BlockState bvt11, final BlockState bvt12, final int integer13) {
        BlockState bvt13 = bvt10;
        BlockState bvt14 = bvt11;
        final BlockPos.MutableBlockPos a18 = new BlockPos.MutableBlockPos();
        int integer14 = -1;
        final int integer15 = (int)(double7 / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final int integer16 = integer4 & 0xF;
        final int integer17 = integer5 & 0xF;
        for (int integer18 = integer6; integer18 >= 0; --integer18) {
            a18.set(integer16, integer18, integer17);
            final BlockState bvt15 = bxh.getBlockState(a18);
            if (bvt15.isAir()) {
                integer14 = -1;
            }
            else if (bvt15.getBlock() == bvt8.getBlock()) {
                if (integer14 == -1) {
                    if (integer15 <= 0) {
                        bvt13 = Blocks.AIR.defaultBlockState();
                        bvt14 = bvt8;
                    }
                    else if (integer18 >= integer13 - 4 && integer18 <= integer13 + 1) {
                        bvt13 = bvt10;
                        bvt14 = bvt11;
                    }
                    if (integer18 < integer13 && (bvt13 == null || bvt13.isAir())) {
                        if (bio.getTemperature(a18.set(integer4, integer18, integer5)) < 0.15f) {
                            bvt13 = Blocks.ICE.defaultBlockState();
                        }
                        else {
                            bvt13 = bvt9;
                        }
                        a18.set(integer16, integer18, integer17);
                    }
                    integer14 = integer15;
                    if (integer18 >= integer13 - 1) {
                        bxh.setBlockState(a18, bvt13, false);
                    }
                    else if (integer18 < integer13 - 7 - integer15) {
                        bvt13 = Blocks.AIR.defaultBlockState();
                        bvt14 = bvt8;
                        bxh.setBlockState(a18, bvt12, false);
                    }
                    else {
                        bxh.setBlockState(a18, bvt14, false);
                    }
                }
                else if (integer14 > 0) {
                    --integer14;
                    bxh.setBlockState(a18, bvt14, false);
                    if (integer14 == 0 && bvt14.getBlock() == Blocks.SAND && integer15 > 1) {
                        integer14 = random.nextInt(4) + Math.max(0, integer18 - 63);
                        bvt14 = ((bvt14.getBlock() == Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState());
                    }
                }
            }
        }
    }
}
