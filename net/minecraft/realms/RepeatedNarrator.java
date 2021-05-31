package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.client.gui.chat.NarratorChatListener;
import java.util.concurrent.atomic.AtomicReference;
import java.time.Duration;

class RepeatedNarrator {
    final Duration repeatDelay;
    private final float permitsPerSecond;
    final AtomicReference<Params> params;
    
    public RepeatedNarrator(final Duration duration) {
        this.repeatDelay = duration;
        this.params = (AtomicReference<Params>)new AtomicReference();
        final float float3 = duration.toMillis() / 1000.0f;
        this.permitsPerSecond = 1.0f / float3;
    }
    
    public void narrate(final String string) {
        final Params a3 = (Params)this.params.updateAndGet(a -> {
            if (a == null || !string.equals(a.narration)) {
                return new Params(string, RateLimiter.create((double)this.permitsPerSecond));
            }
            return a;
        });
        if (a3.rateLimiter.tryAcquire(1)) {
            final NarratorChatListener cyz4 = NarratorChatListener.INSTANCE;
            cyz4.handle(ChatType.SYSTEM, new TextComponent(string));
        }
    }
    
    static class Params {
        String narration;
        RateLimiter rateLimiter;
        
        Params(final String string, final RateLimiter rateLimiter) {
            this.narration = string;
            this.rateLimiter = rateLimiter;
        }
    }
}
