package net.minecraft.client.sounds;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import java.io.Closeable;

public interface AudioStream extends Closeable {
    AudioFormat getFormat();
    
    ByteBuffer readAll() throws IOException;
    
    @Nullable
    ByteBuffer read(final int integer) throws IOException;
}
