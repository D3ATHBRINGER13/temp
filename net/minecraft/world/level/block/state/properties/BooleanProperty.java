package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;

public class BooleanProperty extends AbstractProperty<Boolean> {
    private final ImmutableSet<Boolean> values;
    
    protected BooleanProperty(final String string) {
        super(string, Boolean.class);
        this.values = (ImmutableSet<Boolean>)ImmutableSet.of(true, false);
    }
    
    public Collection<Boolean> getPossibleValues() {
        return (Collection<Boolean>)this.values;
    }
    
    public static BooleanProperty create(final String string) {
        return new BooleanProperty(string);
    }
    
    public Optional<Boolean> getValue(final String string) {
        if ("true".equals(string) || "false".equals(string)) {
            return (Optional<Boolean>)Optional.of(Boolean.valueOf(string));
        }
        return (Optional<Boolean>)Optional.empty();
    }
    
    public String getName(final Boolean boolean1) {
        return boolean1.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BooleanProperty && super.equals(object)) {
            final BooleanProperty bwl3 = (BooleanProperty)object;
            return this.values.equals(bwl3.values);
        }
        return false;
    }
    
    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }
}
