package net.minecraft.server.dedicated;

import java.util.function.UnaryOperator;
import java.nio.file.Path;

public class DedicatedServerSettings {
    private final Path source;
    private DedicatedServerProperties properties;
    
    public DedicatedServerSettings(final Path path) {
        this.source = path;
        this.properties = DedicatedServerProperties.fromFile(path);
    }
    
    public DedicatedServerProperties getProperties() {
        return this.properties;
    }
    
    public void forceSave() {
        this.properties.store(this.source);
    }
    
    public DedicatedServerSettings update(final UnaryOperator<DedicatedServerProperties> unaryOperator) {
        (this.properties = (DedicatedServerProperties)unaryOperator.apply(this.properties)).store(this.source);
        return this;
    }
}
