package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public interface Culler {
    boolean isVisible(final AABB csc);
    
    void prepare(final double double1, final double double2, final double double3);
}
