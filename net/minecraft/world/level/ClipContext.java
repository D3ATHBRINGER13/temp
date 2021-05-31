package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.Vec3;

public class ClipContext {
    private final Vec3 from;
    private final Vec3 to;
    private final Block block;
    private final Fluid fluid;
    private final CollisionContext collisionContext;
    
    public ClipContext(final Vec3 csi1, final Vec3 csi2, final Block a, final Fluid b, final Entity aio) {
        this.from = csi1;
        this.to = csi2;
        this.block = a;
        this.fluid = b;
        this.collisionContext = CollisionContext.of(aio);
    }
    
    public Vec3 getTo() {
        return this.to;
    }
    
    public Vec3 getFrom() {
        return this.from;
    }
    
    public VoxelShape getBlockShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return this.block.get(bvt, bhb, ew, this.collisionContext);
    }
    
    public VoxelShape getFluidShape(final FluidState clk, final BlockGetter bhb, final BlockPos ew) {
        return this.fluid.canPick(clk) ? clk.getShape(bhb, ew) : Shapes.empty();
    }
    
    public enum Block implements ShapeGetter {
        COLLIDER(BlockState::getCollisionShape), 
        OUTLINE(BlockState::getShape);
        
        private final ShapeGetter shapeGetter;
        
        private Block(final ShapeGetter c) {
            this.shapeGetter = c;
        }
        
        public VoxelShape get(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
            return this.shapeGetter.get(bvt, bhb, ew, csn);
        }
    }
    
    public enum Fluid {
        NONE((Predicate<FluidState>)(clk -> false)), 
        SOURCE_ONLY((Predicate<FluidState>)FluidState::isSource), 
        ANY((Predicate<FluidState>)(clk -> !clk.isEmpty()));
        
        private final Predicate<FluidState> canPick;
        
        private Fluid(final Predicate<FluidState> predicate) {
            this.canPick = predicate;
        }
        
        public boolean canPick(final FluidState clk) {
            return this.canPick.test(clk);
        }
    }
    
    public interface ShapeGetter {
        VoxelShape get(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn);
    }
}
