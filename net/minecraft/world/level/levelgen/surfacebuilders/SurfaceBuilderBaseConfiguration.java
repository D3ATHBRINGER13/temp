package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.Blocks;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.block.state.BlockState;

public class SurfaceBuilderBaseConfiguration implements SurfaceBuilderConfiguration {
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underwaterMaterial;
    
    public SurfaceBuilderBaseConfiguration(final BlockState bvt1, final BlockState bvt2, final BlockState bvt3) {
        this.topMaterial = bvt1;
        this.underMaterial = bvt2;
        this.underwaterMaterial = bvt3;
    }
    
    public BlockState getTopMaterial() {
        return this.topMaterial;
    }
    
    public BlockState getUnderMaterial() {
        return this.underMaterial;
    }
    
    public BlockState getUnderwaterMaterial() {
        return this.underwaterMaterial;
    }
    
    public static SurfaceBuilderBaseConfiguration deserialize(final Dynamic<?> dynamic) {
        final BlockState bvt2 = (BlockState)dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        final BlockState bvt3 = (BlockState)dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        final BlockState bvt4 = (BlockState)dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        return new SurfaceBuilderBaseConfiguration(bvt2, bvt3, bvt4);
    }
}
