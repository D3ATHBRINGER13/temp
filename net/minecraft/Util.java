package net.minecraft;

import java.net.URISyntaxException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.io.IOException;
import java.security.PrivilegedActionException;
import org.apache.commons.io.IOUtils;
import java.security.AccessController;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import java.util.concurrent.ForkJoinWorkerThread;
import net.minecraft.server.Bootstrap;
import java.util.concurrent.CompletionException;
import java.util.UUID;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Hash;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.List;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;
import java.util.stream.Stream;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import com.google.common.util.concurrent.MoreExecutors;
import net.minecraft.util.Mth;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.stream.Collector;
import org.apache.logging.log4j.Logger;
import java.util.function.LongSupplier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {
    private static final AtomicInteger WORKER_COUNT;
    private static final ExecutorService BACKGROUND_EXECUTOR;
    public static LongSupplier timeSource;
    private static final Logger LOGGER;
    
    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }
    
    public static <T extends Comparable<T>> String getPropertyName(final Property<T> bww, final Object object) {
        return bww.getName((T)object);
    }
    
    public static String makeDescriptionId(final String string, @Nullable final ResourceLocation qv) {
        if (qv == null) {
            return string + ".unregistered_sadface";
        }
        return string + '.' + qv.getNamespace() + '.' + qv.getPath().replace('/', '.');
    }
    
    public static long getMillis() {
        return getNanos() / 1000000L;
    }
    
    public static long getNanos() {
        return Util.timeSource.getAsLong();
    }
    
    public static long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }
    
    private static ExecutorService makeBackgroundExecutor() {
        final int integer1 = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
        ExecutorService executorService2;
        if (integer1 <= 0) {
            executorService2 = (ExecutorService)MoreExecutors.newDirectExecutorService();
        }
        else {
            executorService2 = (ExecutorService)new ForkJoinPool(integer1, forkJoinPool -> {
                final ForkJoinWorkerThread forkJoinWorkerThread2 = new ForkJoinWorkerThread(forkJoinPool) {};
                forkJoinWorkerThread2.setName(new StringBuilder().append("Server-Worker-").append(Util.WORKER_COUNT.getAndIncrement()).toString());
                return forkJoinWorkerThread2;
            }, (thread, throwable) -> {
                if (throwable instanceof CompletionException) {
                    throwable = throwable.getCause();
                }
                if (throwable instanceof ReportedException) {
                    Bootstrap.realStdoutPrintln(((ReportedException)throwable).getReport().getFriendlyReport());
                    System.exit(-1);
                }
                Util.LOGGER.error(String.format("Caught exception in thread %s", new Object[] { thread }), throwable);
            }, true);
        }
        return executorService2;
    }
    
    public static Executor backgroundExecutor() {
        return (Executor)Util.BACKGROUND_EXECUTOR;
    }
    
    public static void shutdownBackgroundExecutor() {
        Util.BACKGROUND_EXECUTOR.shutdown();
        boolean boolean1;
        try {
            boolean1 = Util.BACKGROUND_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException interruptedException2) {
            boolean1 = false;
        }
        if (!boolean1) {
            Util.BACKGROUND_EXECUTOR.shutdownNow();
        }
    }
    
    public static <T> CompletableFuture<T> failedFuture(final Throwable throwable) {
        final CompletableFuture<T> completableFuture2 = (CompletableFuture<T>)new CompletableFuture();
        completableFuture2.completeExceptionally(throwable);
        return completableFuture2;
    }
    
    public static OS getPlatform() {
        final String string1 = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (string1.contains("win")) {
            return OS.WINDOWS;
        }
        if (string1.contains("mac")) {
            return OS.OSX;
        }
        if (string1.contains("solaris")) {
            return OS.SOLARIS;
        }
        if (string1.contains("sunos")) {
            return OS.SOLARIS;
        }
        if (string1.contains("linux")) {
            return OS.LINUX;
        }
        if (string1.contains("unix")) {
            return OS.LINUX;
        }
        return OS.UNKNOWN;
    }
    
    public static Stream<String> getVmArguments() {
        final RuntimeMXBean runtimeMXBean1 = ManagementFactory.getRuntimeMXBean();
        return (Stream<String>)runtimeMXBean1.getInputArguments().stream().filter(string -> string.startsWith("-X"));
    }
    
    public static <T> T lastOf(final List<T> list) {
        return (T)list.get(list.size() - 1);
    }
    
    public static <T> T findNextInIterable(final Iterable<T> iterable, @Nullable final T object) {
        final Iterator<T> iterator3 = (Iterator<T>)iterable.iterator();
        final T object2 = (T)iterator3.next();
        if (object != null) {
            for (T object3 = object2; object3 != object; object3 = (T)iterator3.next()) {
                if (iterator3.hasNext()) {}
            }
            if (iterator3.hasNext()) {
                return (T)iterator3.next();
            }
        }
        return object2;
    }
    
    public static <T> T findPreviousInIterable(final Iterable<T> iterable, @Nullable final T object) {
        final Iterator<T> iterator3 = (Iterator<T>)iterable.iterator();
        T object2 = null;
        while (iterator3.hasNext()) {
            final T object3 = (T)iterator3.next();
            if (object3 == object) {
                if (object2 == null) {
                    object2 = (T)(iterator3.hasNext() ? Iterators.getLast((Iterator)iterator3) : object);
                    break;
                }
                break;
            }
            else {
                object2 = object3;
            }
        }
        return object2;
    }
    
    public static <T> T make(final Supplier<T> supplier) {
        return (T)supplier.get();
    }
    
    public static <T> T make(final T object, final Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
    
    public static <K> Hash.Strategy<K> identityStrategy() {
        return (Hash.Strategy<K>)IdentityStrategy.INSTANCE;
    }
    
    public static <V> CompletableFuture<List<V>> sequence(final List<? extends CompletableFuture<? extends V>> list) {
        final List<V> list2 = (List<V>)Lists.newArrayListWithCapacity(list.size());
        final CompletableFuture<?>[] arr3 = new CompletableFuture[list.size()];
        final CompletableFuture<Void> completableFuture4 = (CompletableFuture<Void>)new CompletableFuture();
        list.forEach(completableFuture4 -> {
            final int integer5 = list2.size();
            list2.add(null);
            arr3[integer5] = completableFuture4.whenComplete((object, throwable) -> {
                if (throwable != null) {
                    completableFuture4.completeExceptionally(throwable);
                }
                else {
                    list2.set(integer5, object);
                }
            });
        });
        return (CompletableFuture<List<V>>)CompletableFuture.allOf((CompletableFuture[])arr3).applyToEither((CompletionStage)completableFuture4, void2 -> list2);
    }
    
    public static <T> Stream<T> toStream(final Optional<? extends T> optional) {
        return (Stream<T>)DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
    }
    
    public static <T> Optional<T> ifElse(final Optional<T> optional, final Consumer<T> consumer, final Runnable runnable) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        }
        else {
            runnable.run();
        }
        return optional;
    }
    
    public static Runnable name(final Runnable runnable, final Supplier<String> supplier) {
        return runnable;
    }
    
    public static Optional<UUID> readUUID(final String string, final Dynamic<?> dynamic) {
        return (Optional<UUID>)dynamic.get(string + "Most").asNumber().flatMap(number -> dynamic.get(string + "Least").asNumber().map(number2 -> new UUID(number.longValue(), number2.longValue())));
    }
    
    public static <T> Dynamic<T> writeUUID(final String string, final UUID uUID, final Dynamic<T> dynamic) {
        return (Dynamic<T>)dynamic.set(string + "Most", dynamic.createLong(uUID.getMostSignificantBits())).set(string + "Least", dynamic.createLong(uUID.getLeastSignificantBits()));
    }
    
    static {
        WORKER_COUNT = new AtomicInteger(1);
        BACKGROUND_EXECUTOR = makeBackgroundExecutor();
        Util.timeSource = System::nanoTime;
        LOGGER = LogManager.getLogger();
    }
    
    public enum OS {
        LINUX, 
        SOLARIS, 
        WINDOWS {
            @Override
            protected String[] getOpenUrlArguments(final URL uRL) {
                return new String[] { "rundll32", "url.dll,FileProtocolHandler", uRL.toString() };
            }
        }, 
        OSX {
            @Override
            protected String[] getOpenUrlArguments(final URL uRL) {
                return new String[] { "open", uRL.toString() };
            }
        }, 
        UNKNOWN;
        
        public void openUrl(final URL uRL) {
            try {
                final Process process3 = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getOpenUrlArguments(uRL)));
                for (final String string5 : IOUtils.readLines(process3.getErrorStream())) {
                    Util.LOGGER.error(string5);
                }
                process3.getInputStream().close();
                process3.getErrorStream().close();
                process3.getOutputStream().close();
            }
            catch (PrivilegedActionException | IOException ex2) {
                final Exception ex;
                final Exception exception3 = ex;
                Util.LOGGER.error("Couldn't open url '{}'", uRL, exception3);
            }
        }
        
        public void openUri(final URI uRI) {
            try {
                this.openUrl(uRI.toURL());
            }
            catch (MalformedURLException malformedURLException3) {
                Util.LOGGER.error("Couldn't open uri '{}'", uRI, malformedURLException3);
            }
        }
        
        public void openFile(final File file) {
            try {
                this.openUrl(file.toURI().toURL());
            }
            catch (MalformedURLException malformedURLException3) {
                Util.LOGGER.error("Couldn't open file '{}'", file, malformedURLException3);
            }
        }
        
        protected String[] getOpenUrlArguments(final URL uRL) {
            String string3 = uRL.toString();
            if ("file".equals(uRL.getProtocol())) {
                string3 = string3.replace("file:", "file://");
            }
            return new String[] { "xdg-open", string3 };
        }
        
        public void openUri(final String string) {
            try {
                this.openUrl(new URI(string).toURL());
            }
            catch (URISyntaxException | MalformedURLException | IllegalArgumentException ex2) {
                final Exception ex;
                final Exception exception3 = ex;
                Util.LOGGER.error("Couldn't open uri '{}'", string, exception3);
            }
        }
    }
    
    enum IdentityStrategy implements Hash.Strategy<Object> {
        INSTANCE;
        
        public int hashCode(final Object object) {
            return System.identityHashCode(object);
        }
        
        public boolean equals(final Object object1, final Object object2) {
            return object1 == object2;
        }
    }
}
