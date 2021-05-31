package net.minecraft.server.packs.resources;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.profiling.ProfileResults;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.Util;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Unit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.List;
import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.Logger;

public class ProfiledReloadInstance extends SimpleReloadInstance<State> {
    private static final Logger LOGGER;
    private final Stopwatch total;
    
    public ProfiledReloadInstance(final ResourceManager xi, final List<PreparableReloadListener> list, final Executor executor3, final Executor executor4, final CompletableFuture<Unit> completableFuture) {
        final AtomicLong atomicLong7;
        final AtomicLong atomicLong8;
        final ActiveProfiler agg9;
        final ActiveProfiler agg10;
        final CompletableFuture<Void> completableFuture2;
        super(executor3, executor4, xi, list, (a, xi, xd, executor5, executor6) -> {
            atomicLong7 = new AtomicLong();
            atomicLong8 = new AtomicLong();
            agg9 = new ActiveProfiler(Util.getNanos(), () -> 0);
            agg10 = new ActiveProfiler(Util.getNanos(), () -> 0);
            completableFuture2 = xd.reload(a, xi, agg9, agg10, runnable -> executor5.execute(() -> {
                final long long3 = Util.getNanos();
                runnable.run();
                atomicLong7.addAndGet(Util.getNanos() - long3);
            }), runnable -> executor6.execute(() -> {
                final long long3 = Util.getNanos();
                runnable.run();
                atomicLong8.addAndGet(Util.getNanos() - long3);
            }));
            return completableFuture2.thenApplyAsync(void6 -> new State(xd.getClass().getSimpleName(), agg9.getResults(), agg10.getResults(), atomicLong7, atomicLong8), executor4);
        }, completableFuture);
        (this.total = Stopwatch.createUnstarted()).start();
        this.allDone.thenAcceptAsync(this::finish, executor4);
    }
    
    private void finish(final List<State> list) {
        this.total.stop();
        int integer3 = 0;
        ProfiledReloadInstance.LOGGER.info(new StringBuilder().append("Resource reload finished after ").append(this.total.elapsed(TimeUnit.MILLISECONDS)).append(" ms").toString());
        for (final State a5 : list) {
            final ProfileResults agm6 = a5.preparationResult;
            final ProfileResults agm7 = a5.reloadResult;
            final int integer4 = (int)(a5.preparationNanos.get() / 1000000.0);
            final int integer5 = (int)(a5.reloadNanos.get() / 1000000.0);
            final int integer6 = integer4 + integer5;
            final String string11 = a5.name;
            ProfiledReloadInstance.LOGGER.info(string11 + " took approximately " + integer6 + " ms (" + integer4 + " ms preparing, " + integer5 + " ms applying)");
            final String string12 = agm6.getProfilerResults();
            if (string12.length() > 0) {
                ProfiledReloadInstance.LOGGER.debug(string11 + " preparations:\n" + string12);
            }
            final String string13 = agm7.getProfilerResults();
            if (string13.length() > 0) {
                ProfiledReloadInstance.LOGGER.debug(string11 + " reload:\n" + string13);
            }
            ProfiledReloadInstance.LOGGER.info("----------");
            integer3 += integer5;
        }
        ProfiledReloadInstance.LOGGER.info(new StringBuilder().append("Total blocking time: ").append(integer3).append(" ms").toString());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class State {
        private final String name;
        private final ProfileResults preparationResult;
        private final ProfileResults reloadResult;
        private final AtomicLong preparationNanos;
        private final AtomicLong reloadNanos;
        
        private State(final String string, final ProfileResults agm2, final ProfileResults agm3, final AtomicLong atomicLong4, final AtomicLong atomicLong5) {
            this.name = string;
            this.preparationResult = agm2;
            this.reloadResult = agm3;
            this.preparationNanos = atomicLong4;
            this.reloadNanos = atomicLong5;
        }
    }
}
