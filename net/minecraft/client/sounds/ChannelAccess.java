package net.minecraft.client.sounds;

import java.util.Objects;
import java.util.Iterator;
import com.mojang.blaze3d.audio.Channel;
import java.util.stream.Stream;
import java.util.function.Consumer;
import com.google.common.collect.Sets;
import java.util.concurrent.Executor;
import com.mojang.blaze3d.audio.Library;
import java.util.Set;

public class ChannelAccess {
    private final Set<ChannelHandle> channels;
    private final Library library;
    private final Executor executor;
    
    public ChannelAccess(final Library ctq, final Executor executor) {
        this.channels = (Set<ChannelHandle>)Sets.newIdentityHashSet();
        this.library = ctq;
        this.executor = executor;
    }
    
    public ChannelHandle createHandle(final Library.Pool c) {
        final ChannelHandle a3 = new ChannelHandle();
        this.executor.execute(() -> {
            final Channel ctp4 = this.library.acquireChannel(c);
            if (ctp4 != null) {
                a3.channel = ctp4;
                this.channels.add(a3);
            }
        });
        return a3;
    }
    
    public void executeOnChannels(final Consumer<Stream<Channel>> consumer) {
        this.executor.execute(() -> consumer.accept(this.channels.stream().map(a -> a.channel).filter(Objects::nonNull)));
    }
    
    public void scheduleTick() {
        this.executor.execute(() -> {
            final Iterator<ChannelHandle> iterator2 = (Iterator<ChannelHandle>)this.channels.iterator();
            while (iterator2.hasNext()) {
                final ChannelHandle a3 = (ChannelHandle)iterator2.next();
                a3.channel.updateStream();
                if (a3.channel.stopped()) {
                    a3.release();
                    iterator2.remove();
                }
            }
        });
    }
    
    public void clear() {
        this.channels.forEach(ChannelHandle::release);
        this.channels.clear();
    }
    
    public class ChannelHandle {
        private Channel channel;
        private boolean stopped;
        
        public boolean isStopped() {
            return this.stopped;
        }
        
        public void execute(final Consumer<Channel> consumer) {
            ChannelAccess.this.executor.execute(() -> {
                if (this.channel != null) {
                    consumer.accept(this.channel);
                }
            });
        }
        
        public void release() {
            this.stopped = true;
            ChannelAccess.this.library.releaseChannel(this.channel);
            this.channel = null;
        }
    }
}
