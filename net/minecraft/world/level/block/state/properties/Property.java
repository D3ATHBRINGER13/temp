package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import java.util.Collection;

public interface Property<T extends Comparable<T>> {
    String getName();
    
    Collection<T> getPossibleValues();
    
    Class<T> getValueClass();
    
    Optional<T> getValue(final String string);
    
    String getName(final T comparable);
}
