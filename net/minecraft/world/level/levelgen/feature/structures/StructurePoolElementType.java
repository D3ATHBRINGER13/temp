package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;

public interface StructurePoolElementType extends Deserializer<StructurePoolElement> {
    public static final StructurePoolElementType SINGLE = register("single_pool_element", SinglePoolElement::new);
    public static final StructurePoolElementType LIST = register("list_pool_element", ListPoolElement::new);
    public static final StructurePoolElementType FEATURE = register("feature_pool_element", FeaturePoolElement::new);
    public static final StructurePoolElementType EMPTY = register("empty_pool_element", dynamic -> EmptyPoolElement.INSTANCE);
    
    default StructurePoolElementType register(final String string, final StructurePoolElementType cfs) {
        return Registry.<StructurePoolElementType>register(Registry.STRUCTURE_POOL_ELEMENT, string, cfs);
    }
}
