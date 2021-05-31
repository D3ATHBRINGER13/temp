package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

@FunctionalInterface
interface ComposableEntryContainer {
    public static final ComposableEntryContainer ALWAYS_FALSE = (coy, consumer) -> false;
    public static final ComposableEntryContainer ALWAYS_TRUE = (coy, consumer) -> true;
    
    boolean expand(final LootContext coy, final Consumer<LootPoolEntry> consumer);
    
    default ComposableEntryContainer and(final ComposableEntryContainer cpi) {
        Objects.requireNonNull(cpi);
        return (coy, consumer) -> this.expand(coy, consumer) && cpi.expand(coy, consumer);
    }
    
    default ComposableEntryContainer or(final ComposableEntryContainer cpi) {
        Objects.requireNonNull(cpi);
        return (coy, consumer) -> this.expand(coy, consumer) || cpi.expand(coy, consumer);
    }
}
