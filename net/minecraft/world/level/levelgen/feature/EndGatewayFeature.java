package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import java.util.Iterator;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndGatewayFeature extends Feature<EndGatewayConfiguration> {
    public EndGatewayFeature(final Function<Dynamic<?>, ? extends EndGatewayConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final EndGatewayConfiguration cbj) {
        for (final BlockPos ew2 : BlockPos.betweenClosed(ew.offset(-1, -2, -1), ew.offset(1, 2, 1))) {
            final boolean boolean9 = ew2.getX() == ew.getX();
            final boolean boolean10 = ew2.getY() == ew.getY();
            final boolean boolean11 = ew2.getZ() == ew.getZ();
            final boolean boolean12 = Math.abs(ew2.getY() - ew.getY()) == 2;
            if (boolean9 && boolean10 && boolean11) {
                final BlockPos ew3 = ew2.immutable();
                this.setBlock(bhs, ew3, Blocks.END_GATEWAY.defaultBlockState());
                cbj.getExit().ifPresent(ew4 -> {
                    final BlockEntity btw5 = bhs.getBlockEntity(ew3);
                    if (btw5 instanceof TheEndGatewayBlockEntity) {
                        final TheEndGatewayBlockEntity bux6 = (TheEndGatewayBlockEntity)btw5;
                        bux6.setExitPosition(ew4, cbj.isExitExact());
                        btw5.setChanged();
                    }
                });
            }
            else if (boolean10) {
                this.setBlock(bhs, ew2, Blocks.AIR.defaultBlockState());
            }
            else if (boolean12 && boolean9 && boolean11) {
                this.setBlock(bhs, ew2, Blocks.BEDROCK.defaultBlockState());
            }
            else if ((!boolean9 && !boolean11) || boolean12) {
                this.setBlock(bhs, ew2, Blocks.AIR.defaultBlockState());
            }
            else {
                this.setBlock(bhs, ew2, Blocks.BEDROCK.defaultBlockState());
            }
        }
        return true;
    }
}
