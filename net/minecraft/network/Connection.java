package net.minecraft.network;

import java.util.function.Supplier;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.security.Key;
import net.minecraft.util.Crypt;
import javax.crypto.SecretKey;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Epoll;
import java.net.InetAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.local.LocalChannel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import net.minecraft.server.RunningOnDifferentThreadException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import io.netty.handler.timeout.TimeoutException;
import net.minecraft.network.chat.TranslatableComponent;
import io.netty.channel.ChannelHandlerContext;
import com.google.common.collect.Queues;
import net.minecraft.network.chat.Component;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import java.util.Queue;
import net.minecraft.network.protocol.PacketFlow;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.LazyLoadedValue;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.protocol.Packet;
import io.netty.channel.SimpleChannelInboundHandler;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
    private static final Logger LOGGER;
    public static final Marker ROOT_MARKER;
    public static final Marker PACKET_MARKER;
    public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL;
    public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP;
    public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP;
    public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP;
    private final PacketFlow receiving;
    private final Queue<PacketHolder> queue;
    private Channel channel;
    private SocketAddress address;
    private PacketListener packetListener;
    private Component disconnectedReason;
    private boolean encrypted;
    private boolean disconnectionHandled;
    private int receivedPackets;
    private int sentPackets;
    private float averageReceivedPackets;
    private float averageSentPackets;
    private int tickCount;
    private boolean handlingFault;
    
    public Connection(final PacketFlow kd) {
        this.queue = (Queue<PacketHolder>)Queues.newConcurrentLinkedQueue();
        this.receiving = kd;
    }
    
    public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        this.channel = channelHandlerContext.channel();
        this.address = this.channel.remoteAddress();
        try {
            this.setProtocol(ConnectionProtocol.HANDSHAKING);
        }
        catch (Throwable throwable3) {
            Connection.LOGGER.fatal(throwable3);
        }
    }
    
    public void setProtocol(final ConnectionProtocol jd) {
        this.channel.attr((AttributeKey)Connection.ATTRIBUTE_PROTOCOL).set(jd);
        this.channel.config().setAutoRead(true);
        Connection.LOGGER.debug("Enabled auto read");
    }
    
    public void channelInactive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        this.disconnect(new TranslatableComponent("disconnect.endOfStream", new Object[0]));
    }
    
    public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable throwable) {
        if (throwable instanceof SkipPacketException) {
            Connection.LOGGER.debug("Skipping packet due to errors", throwable.getCause());
            return;
        }
        final boolean boolean4 = !this.handlingFault;
        this.handlingFault = true;
        if (!this.channel.isOpen()) {
            return;
        }
        if (throwable instanceof TimeoutException) {
            Connection.LOGGER.debug("Timeout", throwable);
            this.disconnect(new TranslatableComponent("disconnect.timeout", new Object[0]));
        }
        else {
            final Component jo5 = new TranslatableComponent("disconnect.genericReason", new Object[] { new StringBuilder().append("Internal Exception: ").append(throwable).toString() });
            if (boolean4) {
                Connection.LOGGER.debug("Failed to sent packet", throwable);
                this.send(new ClientboundDisconnectPacket(jo5), (future -> this.disconnect(jo5)));
                this.setReadOnly();
            }
            else {
                Connection.LOGGER.debug("Double fault", throwable);
                this.disconnect(jo5);
            }
        }
    }
    
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet<?> kc) throws Exception {
        if (this.channel.isOpen()) {
            try {
                Connection.genericsFtw(kc, this.packetListener);
            }
            catch (RunningOnDifferentThreadException ex) {}
            ++this.receivedPackets;
        }
    }
    
    private static <T extends PacketListener> void genericsFtw(final Packet<T> kc, final PacketListener jh) {
        kc.handle((T)jh);
    }
    
    public void setListener(final PacketListener jh) {
        Validate.notNull(jh, "packetListener", new Object[0]);
        Connection.LOGGER.debug("Set listener of {} to {}", this, jh);
        this.packetListener = jh;
    }
    
    public void send(final Packet<?> kc) {
        this.send(kc, null);
    }
    
    public void send(final Packet<?> kc, @Nullable final GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        if (this.isConnected()) {
            this.flushQueue();
            this.sendPacket(kc, genericFutureListener);
        }
        else {
            this.queue.add(new PacketHolder(kc, genericFutureListener));
        }
    }
    
    private void sendPacket(final Packet<?> kc, @Nullable final GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        final ConnectionProtocol jd4 = ConnectionProtocol.getProtocolForPacket(kc);
        final ConnectionProtocol jd5 = (ConnectionProtocol)this.channel.attr((AttributeKey)Connection.ATTRIBUTE_PROTOCOL).get();
        ++this.sentPackets;
        if (jd5 != jd4) {
            Connection.LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (jd4 != jd5) {
                this.setProtocol(jd4);
            }
            final ChannelFuture channelFuture6 = this.channel.writeAndFlush(kc);
            if (genericFutureListener != null) {
                channelFuture6.addListener((GenericFutureListener)genericFutureListener);
            }
            channelFuture6.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else {
            this.channel.eventLoop().execute(() -> {
                if (jd4 != jd5) {
                    this.setProtocol(jd4);
                }
                final ChannelFuture channelFuture6 = this.channel.writeAndFlush(kc);
                if (genericFutureListener != null) {
                    channelFuture6.addListener(genericFutureListener);
                }
                channelFuture6.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }
    
    private void flushQueue() {
        if (this.channel == null || !this.channel.isOpen()) {
            return;
        }
        synchronized (this.queue) {
            PacketHolder a3;
            while ((a3 = (PacketHolder)this.queue.poll()) != null) {
                this.sendPacket(a3.packet, a3.listener);
            }
        }
    }
    
    public void tick() {
        this.flushQueue();
        if (this.packetListener instanceof ServerLoginPacketListenerImpl) {
            ((ServerLoginPacketListenerImpl)this.packetListener).tick();
        }
        if (this.packetListener instanceof ServerGamePacketListenerImpl) {
            ((ServerGamePacketListenerImpl)this.packetListener).tick();
        }
        if (this.channel != null) {
            this.channel.flush();
        }
        if (this.tickCount++ % 20 == 0) {
            this.averageSentPackets = this.averageSentPackets * 0.75f + this.sentPackets * 0.25f;
            this.averageReceivedPackets = this.averageReceivedPackets * 0.75f + this.receivedPackets * 0.25f;
            this.sentPackets = 0;
            this.receivedPackets = 0;
        }
    }
    
    public SocketAddress getRemoteAddress() {
        return this.address;
    }
    
    public void disconnect(final Component jo) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectedReason = jo;
        }
    }
    
    public boolean isMemoryConnection() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }
    
    public static Connection connectToServer(final InetAddress inetAddress, final int integer, final boolean boolean3) {
        final Connection jc4 = new Connection(PacketFlow.CLIENTBOUND);
        Class<? extends SocketChannel> class5;
        LazyLoadedValue<? extends EventLoopGroup> zv6;
        if (Epoll.isAvailable() && boolean3) {
            class5 = EpollSocketChannel.class;
            zv6 = Connection.NETWORK_EPOLL_WORKER_GROUP;
        }
        else {
            class5 = NioSocketChannel.class;
            zv6 = Connection.NETWORK_WORKER_GROUP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)zv6.get())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException ex) {}
                channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new Varint21FrameDecoder()).addLast("decoder", (ChannelHandler)new PacketDecoder(PacketFlow.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new Varint21LengthFieldPrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(PacketFlow.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)jc4);
            }
        })).channel((Class)class5)).connect(inetAddress, integer).syncUninterruptibly();
        return jc4;
    }
    
    public static Connection connectToLocalServer(final SocketAddress socketAddress) {
        final Connection jc2 = new Connection(PacketFlow.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.LOCAL_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel channel) throws Exception {
                channel.pipeline().addLast("packet_handler", (ChannelHandler)jc2);
            }
        })).channel((Class)LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
        return jc2;
    }
    
    public void setEncryptionKey(final SecretKey secretKey) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new CipherDecoder(Crypt.getCipher(2, (Key)secretKey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new CipherEncoder(Crypt.getCipher(1, (Key)secretKey)));
    }
    
    public boolean isEncrypted() {
        return this.encrypted;
    }
    
    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }
    
    public boolean isConnecting() {
        return this.channel == null;
    }
    
    public PacketListener getPacketListener() {
        return this.packetListener;
    }
    
    @Nullable
    public Component getDisconnectedReason() {
        return this.disconnectedReason;
    }
    
    public void setReadOnly() {
        this.channel.config().setAutoRead(false);
    }
    
    public void setupCompression(final int integer) {
        if (integer >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(integer);
            }
            else {
                this.channel.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new CompressionDecoder(integer));
            }
            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                ((CompressionEncoder)this.channel.pipeline().get("compress")).setThreshold(integer);
            }
            else {
                this.channel.pipeline().addBefore("encoder", "compress", (ChannelHandler)new CompressionEncoder(integer));
            }
        }
        else {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }
    
    public void handleDisconnection() {
        if (this.channel == null || this.channel.isOpen()) {
            return;
        }
        if (this.disconnectionHandled) {
            Connection.LOGGER.warn("handleDisconnection() called twice");
        }
        else {
            this.disconnectionHandled = true;
            if (this.getDisconnectedReason() != null) {
                this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            }
            else if (this.getPacketListener() != null) {
                this.getPacketListener().onDisconnect(new TranslatableComponent("multiplayer.disconnect.generic", new Object[0]));
            }
        }
    }
    
    public float getAverageReceivedPackets() {
        return this.averageReceivedPackets;
    }
    
    public float getAverageSentPackets() {
        return this.averageSentPackets;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ROOT_MARKER = MarkerManager.getMarker("NETWORK");
        PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", Connection.ROOT_MARKER);
        ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
        NETWORK_WORKER_GROUP = new LazyLoadedValue<NioEventLoopGroup>((java.util.function.Supplier<NioEventLoopGroup>)(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())));
        NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue<EpollEventLoopGroup>((java.util.function.Supplier<EpollEventLoopGroup>)(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())));
        LOCAL_WORKER_GROUP = new LazyLoadedValue<DefaultEventLoopGroup>((java.util.function.Supplier<DefaultEventLoopGroup>)(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())));
    }
    
    static class PacketHolder {
        private final Packet<?> packet;
        @Nullable
        private final GenericFutureListener<? extends Future<? super Void>> listener;
        
        public PacketHolder(final Packet<?> kc, @Nullable final GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
            this.packet = kc;
            this.listener = genericFutureListener;
        }
    }
}
