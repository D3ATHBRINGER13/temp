package net.minecraft.world.level.block.state.predicate;

import javax.annotation.Nullable;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;

public class BlockMaterialPredicate implements Predicate<BlockState> {
    private static final BlockMaterialPredicate AIR;
    private final Material material;
    
    private BlockMaterialPredicate(final Material clo) {
        this.material = clo;
    }
    
    public static BlockMaterialPredicate forMaterial(final Material clo) {
        return (clo == Material.AIR) ? BlockMaterialPredicate.AIR : new BlockMaterialPredicate(clo);
    }
    
    public boolean test(@Nullable final BlockState bvt) {
        return bvt != null && bvt.getMaterial() == this.material;
    }
    
    static {
        AIR = new BlockMaterialPredicate(Material.AIR) {
            @Override
            public boolean test(@Nullable final BlockState bvt) {
                return bvt != null && bvt.isAir();
            }
        };
    }
}
