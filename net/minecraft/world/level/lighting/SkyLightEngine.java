package net.minecraft.world.level.lighting;

import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.core.BlockPos;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.core.Direction;

public final class SkyLightEngine extends LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
    private static final Direction[] DIRECTIONS;
    private static final Direction[] HORIZONTALS;
    
    public SkyLightEngine(final LightChunkGetter bxv) {
        super(bxv, LightLayer.SKY, new SkyLightSectionStorage(bxv));
    }
    
    @Override
    protected int computeLevelFromNeighbor(final long long1, final long long2, int integer) {
        if (long2 == Long.MAX_VALUE) {
            return 15;
        }
        if (long1 == Long.MAX_VALUE) {
            if (!((SkyLightSectionStorage)this.storage).hasLightSource(long2)) {
                return 15;
            }
            integer = 0;
        }
        if (integer >= 15) {
            return integer;
        }
        final AtomicInteger atomicInteger7 = new AtomicInteger();
        final BlockState bvt8 = this.getStateAndOpacity(long2, atomicInteger7);
        if (atomicInteger7.get() >= 15) {
            return 15;
        }
        final int integer2 = BlockPos.getX(long1);
        final int integer3 = BlockPos.getY(long1);
        final int integer4 = BlockPos.getZ(long1);
        final int integer5 = BlockPos.getX(long2);
        final int integer6 = BlockPos.getY(long2);
        final int integer7 = BlockPos.getZ(long2);
        final boolean boolean15 = integer2 == integer5 && integer4 == integer7;
        final int integer8 = Integer.signum(integer5 - integer2);
        final int integer9 = Integer.signum(integer6 - integer3);
        final int integer10 = Integer.signum(integer7 - integer4);
        Direction fb19;
        if (long1 == Long.MAX_VALUE) {
            fb19 = Direction.DOWN;
        }
        else {
            fb19 = Direction.fromNormal(integer8, integer9, integer10);
        }
        final BlockState bvt9 = this.getStateAndOpacity(long1, null);
        if (fb19 != null) {
            final VoxelShape ctc21 = this.getShape(bvt9, long1, fb19);
            final VoxelShape ctc22 = this.getShape(bvt8, long2, fb19.getOpposite());
            if (Shapes.faceShapeOccludes(ctc21, ctc22)) {
                return 15;
            }
        }
        else {
            final VoxelShape ctc21 = this.getShape(bvt9, long1, Direction.DOWN);
            if (Shapes.faceShapeOccludes(ctc21, Shapes.empty())) {
                return 15;
            }
            final int integer11 = boolean15 ? -1 : 0;
            final Direction fb20 = Direction.fromNormal(integer8, integer11, integer10);
            if (fb20 == null) {
                return 15;
            }
            final VoxelShape ctc23 = this.getShape(bvt8, long2, fb20.getOpposite());
            if (Shapes.faceShapeOccludes(Shapes.empty(), ctc23)) {
                return 15;
            }
        }
        final boolean boolean16 = long1 == Long.MAX_VALUE || (boolean15 && integer3 > integer6);
        if (boolean16 && integer == 0 && atomicInteger7.get() == 0) {
            return 0;
        }
        return integer + Math.max(1, atomicInteger7.get());
    }
    
    @Override
    protected void checkNeighborsAfterUpdate(final long long1, final int integer, final boolean boolean3) {
        final long long2 = SectionPos.blockToSection(long1);
        final int integer2 = BlockPos.getY(long1);
        final int integer3 = SectionPos.sectionRelative(integer2);
        final int integer4 = SectionPos.blockToSectionCoord(integer2);
        int integer5;
        if (integer3 != 0) {
            integer5 = 0;
        }
        else {
            int integer6;
            for (integer6 = 0; !((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.offset(long2, 0, -integer6 - 1, 0)) && ((SkyLightSectionStorage)this.storage).hasSectionsBelow(integer4 - integer6 - 1); ++integer6) {}
            integer5 = integer6;
        }
        final long long3 = BlockPos.offset(long1, 0, -1 - integer5 * 16, 0);
        final long long4 = SectionPos.blockToSection(long3);
        if (long2 == long4 || ((SkyLightSectionStorage)this.storage).storingLightForSection(long4)) {
            this.checkNeighbor(long1, long3, integer, boolean3);
        }
        final long long5 = BlockPos.offset(long1, Direction.UP);
        final long long6 = SectionPos.blockToSection(long5);
        if (long2 == long6 || ((SkyLightSectionStorage)this.storage).storingLightForSection(long6)) {
            this.checkNeighbor(long1, long5, integer, boolean3);
        }
        for (final Direction fb23 : SkyLightEngine.HORIZONTALS) {
            int integer7 = 0;
            do {
                final long long7 = BlockPos.offset(long1, fb23.getStepX(), -integer7, fb23.getStepZ());
                final long long8 = SectionPos.blockToSection(long7);
                if (long2 == long8) {
                    this.checkNeighbor(long1, long7, integer, boolean3);
                    break;
                }
                if (!((SkyLightSectionStorage)this.storage).storingLightForSection(long8)) {
                    continue;
                }
                this.checkNeighbor(long1, long7, integer, boolean3);
            } while (++integer7 <= integer5 * 16);
        }
    }
    
    @Override
    protected int getComputedLevel(final long long1, final long long2, final int integer) {
        int integer2 = integer;
        if (Long.MAX_VALUE != long2) {
            final int integer3 = this.computeLevelFromNeighbor(Long.MAX_VALUE, long1, 0);
            if (integer2 > integer3) {
                integer2 = integer3;
            }
            if (integer2 == 0) {
                return integer2;
            }
        }
        final long long3 = SectionPos.blockToSection(long1);
        final DataLayer bxn10 = ((SkyLightSectionStorage)this.storage).getDataLayer(long3, true);
        for (final Direction fb14 : SkyLightEngine.DIRECTIONS) {
            long long4 = BlockPos.offset(long1, fb14);
            long long5 = SectionPos.blockToSection(long4);
            DataLayer bxn11;
            if (long3 == long5) {
                bxn11 = bxn10;
            }
            else {
                bxn11 = ((SkyLightSectionStorage)this.storage).getDataLayer(long5, true);
            }
            if (bxn11 != null) {
                if (long4 != long2) {
                    final int integer4 = this.computeLevelFromNeighbor(long4, long1, this.getLevel(bxn11, long4));
                    if (integer2 > integer4) {
                        integer2 = integer4;
                    }
                    if (integer2 == 0) {
                        return integer2;
                    }
                }
            }
            else if (fb14 != Direction.DOWN) {
                for (long4 = BlockPos.getFlatIndex(long4); !((SkyLightSectionStorage)this.storage).storingLightForSection(long5) && !((SkyLightSectionStorage)this.storage).isAboveData(long5); long5 = SectionPos.offset(long5, Direction.UP), long4 = BlockPos.offset(long4, 0, 16, 0)) {}
                final DataLayer bxn12 = ((SkyLightSectionStorage)this.storage).getDataLayer(long5, true);
                if (long4 != long2) {
                    int integer5;
                    if (bxn12 != null) {
                        integer5 = this.computeLevelFromNeighbor(long4, long1, this.getLevel(bxn12, long4));
                    }
                    else {
                        integer5 = (((SkyLightSectionStorage)this.storage).lightOnInSection(long5) ? 0 : 15);
                    }
                    if (integer2 > integer5) {
                        integer2 = integer5;
                    }
                    if (integer2 == 0) {
                        return integer2;
                    }
                }
            }
        }
        return integer2;
    }
    
    @Override
    protected void checkNode(long long1) {
        ((SkyLightSectionStorage)this.storage).runAllUpdates();
        long long2 = SectionPos.blockToSection(long1);
        if (((SkyLightSectionStorage)this.storage).storingLightForSection(long2)) {
            super.checkNode(long1);
        }
        else {
            for (long1 = BlockPos.getFlatIndex(long1); !((SkyLightSectionStorage)this.storage).storingLightForSection(long2) && !((SkyLightSectionStorage)this.storage).isAboveData(long2); long2 = SectionPos.offset(long2, Direction.UP), long1 = BlockPos.offset(long1, 0, 16, 0)) {}
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(long2)) {
                super.checkNode(long1);
            }
        }
    }
    
    @Override
    public String getDebugData(final long long1) {
        return super.getDebugData(long1) + (((SkyLightSectionStorage)this.storage).isAboveData(long1) ? "*" : "");
    }
    
    static {
        DIRECTIONS = Direction.values();
        HORIZONTALS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    }
}
