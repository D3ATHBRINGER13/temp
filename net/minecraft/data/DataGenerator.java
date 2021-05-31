package net.minecraft.data;

import net.minecraft.server.Bootstrap;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.util.List;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
    private static final Logger LOGGER;
    private final Collection<Path> inputFolders;
    private final Path outputFolder;
    private final List<DataProvider> providers;
    
    public DataGenerator(final Path path, final Collection<Path> collection) {
        this.providers = (List<DataProvider>)Lists.newArrayList();
        this.outputFolder = path;
        this.inputFolders = collection;
    }
    
    public Collection<Path> getInputFolders() {
        return this.inputFolders;
    }
    
    public Path getOutputFolder() {
        return this.outputFolder;
    }
    
    public void run() throws IOException {
        final HashCache gm2 = new HashCache(this.outputFolder, "cache");
        gm2.keep(this.getOutputFolder().resolve("version.json"));
        final Stopwatch stopwatch3 = Stopwatch.createStarted();
        final Stopwatch stopwatch4 = Stopwatch.createUnstarted();
        for (final DataProvider gl6 : this.providers) {
            DataGenerator.LOGGER.info("Starting provider: {}", gl6.getName());
            stopwatch4.start();
            gl6.run(gm2);
            stopwatch4.stop();
            DataGenerator.LOGGER.info("{} finished after {} ms", gl6.getName(), stopwatch4.elapsed(TimeUnit.MILLISECONDS));
            stopwatch4.reset();
        }
        DataGenerator.LOGGER.info("All providers took: {} ms", stopwatch3.elapsed(TimeUnit.MILLISECONDS));
        gm2.purgeStaleAndWrite();
    }
    
    public void addProvider(final DataProvider gl) {
        this.providers.add(gl);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        Bootstrap.bootStrap();
    }
}
