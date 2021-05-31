package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class BeardedStructureStart extends StructureStart {
    public BeardedStructureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
        super(ceu, integer2, integer3, bio, cic, integer6, long7);
    }
    
    @Override
    protected void calculateBoundingBox() {
        super.calculateBoundingBox();
        final int integer2 = 12;
        final BoundingBox boundingBox = this.boundingBox;
        boundingBox.x0 -= 12;
        final BoundingBox boundingBox2 = this.boundingBox;
        boundingBox2.y0 -= 12;
        final BoundingBox boundingBox3 = this.boundingBox;
        boundingBox3.z0 -= 12;
        final BoundingBox boundingBox4 = this.boundingBox;
        boundingBox4.x1 += 12;
        final BoundingBox boundingBox5 = this.boundingBox;
        boundingBox5.y1 += 12;
        final BoundingBox boundingBox6 = this.boundingBox;
        boundingBox6.z1 += 12;
    }
}
