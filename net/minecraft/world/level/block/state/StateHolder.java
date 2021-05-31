package net.minecraft.world.level.block.state;

import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.Logger;

public interface StateHolder<C> {
    public static final Logger LOGGER = LogManager.getLogger();
    
     <T extends Comparable<T>> T getValue(final Property<T> bww);
    
     <T extends Comparable<T>, V extends T> C setValue(final Property<T> bww, final V comparable);
    
    ImmutableMap<Property<?>, Comparable<?>> getValues();
    
    default <T extends Comparable<T>> String getName(final Property<T> bww, final Comparable<?> comparable) {
        return bww.getName((T)comparable);
    }
    
    default <S extends StateHolder<S>, T extends Comparable<T>> S setValueHelper(final S bvv, final Property<T> bww, final String string3, final String string4, final String string5) {
        final Optional<T> optional6 = bww.getValue(string5);
        if (optional6.isPresent()) {
            return bvv.<T, Comparable>setValue(bww, optional6.get());
        }
        StateHolder.LOGGER.warn("Unable to read property: {} with value: {} for input: {}", string3, string5, string4);
        return bvv;
    }
}
