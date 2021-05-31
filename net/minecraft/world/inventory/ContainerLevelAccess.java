package net.minecraft.world.inventory;

import java.util.function.BiConsumer;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ContainerLevelAccess {
    public static final ContainerLevelAccess NULL = new ContainerLevelAccess() {
        public <T> Optional<T> evaluate(final BiFunction<Level, BlockPos, T> biFunction) {
            return (Optional<T>)Optional.empty();
        }
    };
    
    default ContainerLevelAccess create(final Level bhr, final BlockPos ew) {
        return new ContainerLevelAccess() {
            public <T> Optional<T> evaluate(final BiFunction<Level, BlockPos, T> biFunction) {
                return (Optional<T>)Optional.of(biFunction.apply(bhr, ew));
            }
        };
    }
    
     <T> Optional<T> evaluate(final BiFunction<Level, BlockPos, T> biFunction);
    
    default <T> T evaluate(final BiFunction<Level, BlockPos, T> biFunction, final T object) {
        return (T)this.<T>evaluate(biFunction).orElse(object);
    }
    
    default void execute(final BiConsumer<Level, BlockPos> biConsumer) {
        this.evaluate((java.util.function.BiFunction<Level, BlockPos, Object>)((bhr, ew) -> {
            biConsumer.accept(bhr, ew);
            return Optional.empty();
        }));
    }
}
