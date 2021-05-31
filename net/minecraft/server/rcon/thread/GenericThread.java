package net.minecraft.server.rcon.thread;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import com.google.common.collect.Lists;
import java.net.ServerSocket;
import java.net.DatagramSocket;
import java.util.List;
import net.minecraft.server.ServerInterface;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;

public abstract class GenericThread implements Runnable {
    private static final Logger LOGGER;
    private static final AtomicInteger UNIQUE_THREAD_ID;
    protected boolean running;
    protected final ServerInterface serverInterface;
    protected final String name;
    protected Thread thread;
    protected final int maxStopWait = 5;
    protected final List<DatagramSocket> datagramSockets;
    protected final List<ServerSocket> serverSockets;
    
    protected GenericThread(final ServerInterface ri, final String string) {
        this.datagramSockets = (List<DatagramSocket>)Lists.newArrayList();
        this.serverSockets = (List<ServerSocket>)Lists.newArrayList();
        this.serverInterface = ri;
        this.name = string;
        if (this.serverInterface.isDebugging()) {
            this.warn("Debugging is enabled, performance maybe reduced!");
        }
    }
    
    public synchronized void start() {
        (this.thread = new Thread((Runnable)this, this.name + " #" + GenericThread.UNIQUE_THREAD_ID.incrementAndGet())).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandlerWithName(GenericThread.LOGGER));
        this.thread.start();
        this.running = true;
    }
    
    public synchronized void stop() {
        this.running = false;
        if (null == this.thread) {
            return;
        }
        int integer2 = 0;
        while (this.thread.isAlive()) {
            try {
                this.thread.join(1000L);
                ++integer2;
                if (5 <= integer2) {
                    this.warn(new StringBuilder().append("Waited ").append(integer2).append(" seconds attempting force stop!").toString());
                    this.closeSockets(true);
                }
                else {
                    if (!this.thread.isAlive()) {
                        continue;
                    }
                    this.warn(new StringBuilder().append("Thread ").append(this).append(" (").append(this.thread.getState()).append(") failed to exit after ").append(integer2).append(" second(s)").toString());
                    this.warn("Stack:");
                    for (final StackTraceElement stackTraceElement6 : this.thread.getStackTrace()) {
                        this.warn(stackTraceElement6.toString());
                    }
                    this.thread.interrupt();
                }
            }
            catch (InterruptedException ex) {}
        }
        this.closeSockets(true);
        this.thread = null;
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    protected void debug(final String string) {
        this.serverInterface.debug(string);
    }
    
    protected void info(final String string) {
        this.serverInterface.info(string);
    }
    
    protected void warn(final String string) {
        this.serverInterface.warn(string);
    }
    
    protected void error(final String string) {
        this.serverInterface.error(string);
    }
    
    protected int currentPlayerCount() {
        return this.serverInterface.getPlayerCount();
    }
    
    protected void registerSocket(final DatagramSocket datagramSocket) {
        this.debug(new StringBuilder().append("registerSocket: ").append(datagramSocket).toString());
        this.datagramSockets.add(datagramSocket);
    }
    
    protected boolean closeSocket(final DatagramSocket datagramSocket, final boolean boolean2) {
        this.debug(new StringBuilder().append("closeSocket: ").append(datagramSocket).toString());
        if (null == datagramSocket) {
            return false;
        }
        boolean boolean3 = false;
        if (!datagramSocket.isClosed()) {
            datagramSocket.close();
            boolean3 = true;
        }
        if (boolean2) {
            this.datagramSockets.remove(datagramSocket);
        }
        return boolean3;
    }
    
    protected boolean closeSocket(final ServerSocket serverSocket) {
        return this.closeSocket(serverSocket, true);
    }
    
    protected boolean closeSocket(final ServerSocket serverSocket, final boolean boolean2) {
        this.debug(new StringBuilder().append("closeSocket: ").append(serverSocket).toString());
        if (null == serverSocket) {
            return false;
        }
        boolean boolean3 = false;
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
                boolean3 = true;
            }
        }
        catch (IOException iOException5) {
            this.warn("IO: " + iOException5.getMessage());
        }
        if (boolean2) {
            this.serverSockets.remove(serverSocket);
        }
        return boolean3;
    }
    
    protected void closeSockets() {
        this.closeSockets(false);
    }
    
    protected void closeSockets(final boolean boolean1) {
        int integer3 = 0;
        for (final DatagramSocket datagramSocket5 : this.datagramSockets) {
            if (this.closeSocket(datagramSocket5, false)) {
                ++integer3;
            }
        }
        this.datagramSockets.clear();
        for (final ServerSocket serverSocket5 : this.serverSockets) {
            if (this.closeSocket(serverSocket5, false)) {
                ++integer3;
            }
        }
        this.serverSockets.clear();
        if (boolean1 && 0 < integer3) {
            this.warn(new StringBuilder().append("Force closed ").append(integer3).append(" sockets").toString());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        UNIQUE_THREAD_ID = new AtomicInteger(0);
    }
}
