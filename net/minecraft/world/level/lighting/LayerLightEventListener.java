package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.core.SectionPos;

public interface LayerLightEventListener extends LightEventListener {
    @Nullable
    DataLayer getDataLayerData(final SectionPos fp);
    
    int getLightValue(final BlockPos ew);
    
    public enum DummyLightLayerEventListener implements LayerLightEventListener {
        INSTANCE;
        
        @Nullable
        public DataLayer getDataLayerData(final SectionPos fp) {
            return null;
        }
        
        public int getLightValue(final BlockPos ew) {
            return 0;
        }
        
        public void updateSectionStatus(final SectionPos fp, final boolean boolean2) {
        }
    }
}
