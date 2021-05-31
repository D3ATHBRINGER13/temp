package net.minecraft.util.thread;

import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface ProcessorHandle<Msg> extends AutoCloseable {
    String name();
    
    void tell(final Msg object);
    
    default void close() {
    }
    
    default <Source> CompletableFuture<Source> ask(final Function<? super ProcessorHandle<Source>, ? extends Msg> function) {
        final CompletableFuture<Source> completableFuture3 = (CompletableFuture<Source>)new CompletableFuture();
        final Msg object4 = (Msg)function.apply(ProcessorHandle.of("ask future procesor handle", (java.util.function.Consumer<Object>)completableFuture3::complete));
        this.tell(object4);
        return completableFuture3;
    }
    
    default <Msg> ProcessorHandle<Msg> of(final String string, final Consumer<Msg> consumer) {
        return new ProcessorHandle<Msg>() {
            public String name() {
                return string;
            }
            
            public void tell(final Msg object) {
                consumer.accept(object);
            }
            
            public String toString() {
                return string;
            }
        };
    }
}
