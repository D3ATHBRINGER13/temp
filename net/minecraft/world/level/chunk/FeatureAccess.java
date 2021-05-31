package net.minecraft.world.level.chunk;

import java.util.Map;
import it.unimi.dsi.fastutil.longs.LongSet;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.BlockGetter;

public interface FeatureAccess extends BlockGetter {
    @Nullable
    StructureStart getStartForFeature(final String string);
    
    void setStartForFeature(final String string, final StructureStart ciw);
    
    LongSet getReferencesForFeature(final String string);
    
    void addReferenceForFeature(final String string, final long long2);
    
    Map<String, LongSet> getAllReferences();
    
    void setAllReferences(final Map<String, LongSet> map);
}
