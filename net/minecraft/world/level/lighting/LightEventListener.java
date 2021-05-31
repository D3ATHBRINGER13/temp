package net.minecraft.world.level.lighting;

import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos;

public interface LightEventListener {
    default void updateSectionStatus(final BlockPos ew, final boolean boolean2) {
        this.updateSectionStatus(SectionPos.of(ew), boolean2);
    }
    
    void updateSectionStatus(final SectionPos fp, final boolean boolean2);
}
