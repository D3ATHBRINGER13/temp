package net.minecraft.world.level.block.state.predicate;

import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;

public class BlockStatePredicate implements Predicate<BlockState> {
    public static final Predicate<BlockState> ANY;
    private final StateDefinition<Block, BlockState> definition;
    private final Map<Property<?>, Predicate<Object>> properties;
    
    private BlockStatePredicate(final StateDefinition<Block, BlockState> bvu) {
        this.properties = (Map<Property<?>, Predicate<Object>>)Maps.newHashMap();
        this.definition = bvu;
    }
    
    public static BlockStatePredicate forBlock(final Block bmv) {
        return new BlockStatePredicate(bmv.getStateDefinition());
    }
    
    public boolean test(@Nullable final BlockState bvt) {
        if (bvt == null || !bvt.getBlock().equals(this.definition.getOwner())) {
            return false;
        }
        if (this.properties.isEmpty()) {
            return true;
        }
        for (final Map.Entry<Property<?>, Predicate<Object>> entry4 : this.properties.entrySet()) {
            if (!this.<Comparable>applies(bvt, (Property<Comparable>)entry4.getKey(), (Predicate<Object>)entry4.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    protected <T extends Comparable<T>> boolean applies(final BlockState bvt, final Property<T> bww, final Predicate<Object> predicate) {
        final T comparable5 = bvt.<T>getValue(bww);
        return predicate.test(comparable5);
    }
    
    public <V extends Comparable<V>> BlockStatePredicate where(final Property<V> bww, final Predicate<Object> predicate) {
        if (!this.definition.getProperties().contains(bww)) {
            throw new IllegalArgumentException(new StringBuilder().append(this.definition).append(" cannot support property ").append(bww).toString());
        }
        this.properties.put(bww, predicate);
        return this;
    }
    
    static {
        ANY = (bvt -> true);
    }
}
