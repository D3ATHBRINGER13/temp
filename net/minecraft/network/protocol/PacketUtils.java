package net.minecraft.network.protocol;

import org.apache.logging.log4j.LogManager;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.Logger;

public class PacketUtils {
    private static final Logger LOGGER;
    
    public static <T extends PacketListener> void ensureRunningOnSameThread(final Packet<T> kc, final T jh, final ServerLevel vk) throws RunningOnDifferentThreadException {
        PacketUtils.<T>ensureRunningOnSameThread(kc, jh, vk.getServer());
    }
    
    public static <T extends PacketListener> void ensureRunningOnSameThread(final Packet<T> kc, final T jh, final BlockableEventLoop<?> agq) throws RunningOnDifferentThreadException {
        if (!agq.isSameThread()) {
            agq.execute(() -> {
                if (jh.getConnection().isConnected()) {
                    kc.handle(jh);
                }
                else {
                    PacketUtils.LOGGER.debug(new StringBuilder().append("Ignoring packet due to disconnection: ").append(kc).toString());
                }
            });
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
