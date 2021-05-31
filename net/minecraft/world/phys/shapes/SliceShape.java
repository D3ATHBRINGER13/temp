package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;

public class SliceShape extends VoxelShape {
    private final VoxelShape delegate;
    private final Direction.Axis axis;
    private static final DoubleList SLICE_COORDS;
    
    public SliceShape(final VoxelShape ctc, final Direction.Axis a, final int integer) {
        super(makeSlice(ctc.shape, a, integer));
        this.delegate = ctc;
        this.axis = a;
    }
    
    private static DiscreteVoxelShape makeSlice(final DiscreteVoxelShape csr, final Direction.Axis a, final int integer) {
        return new SubShape(csr, a.choose(integer, 0, 0), a.choose(0, integer, 0), a.choose(0, 0, integer), a.choose(integer + 1, csr.xSize, csr.xSize), a.choose(csr.ySize, integer + 1, csr.ySize), a.choose(csr.zSize, csr.zSize, integer + 1));
    }
    
    @Override
    protected DoubleList getCoords(final Direction.Axis a) {
        if (a == this.axis) {
            return SliceShape.SLICE_COORDS;
        }
        return this.delegate.getCoords(a);
    }
    
    static {
        SLICE_COORDS = (DoubleList)new CubePointRange(1);
    }
}
