package net.minecraft.client.multiplayer;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.minecraft.util.Mth;
import com.google.common.collect.Iterables;
import java.nio.charset.StandardCharsets;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import java.net.UnknownHostException;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.Util;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.client.resources.language.I18n;
import java.net.InetAddress;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.network.Connection;
import java.util.List;
import org.apache.logging.log4j.Logger;
import com.google.common.base.Splitter;

public class ServerStatusPinger {
    private static final Splitter SPLITTER;
    private static final Logger LOGGER;
    private final List<Connection> connections;
    
    public ServerStatusPinger() {
        this.connections = (List<Connection>)Collections.synchronizedList((List)Lists.newArrayList());
    }
    
    public void pingServer(final ServerData dki) throws UnknownHostException {
        final ServerAddress dkh3 = ServerAddress.parseString(dki.ip);
        final Connection jc4 = Connection.connectToServer(InetAddress.getByName(dkh3.getHost()), dkh3.getPort(), false);
        this.connections.add(jc4);
        dki.motd = I18n.get("multiplayer.status.pinging");
        dki.ping = -1L;
        dki.playerList = null;
        jc4.setListener(new ClientStatusPacketListener() {
            private boolean success;
            private boolean receivedPing;
            private long pingStart;
            
            public void handleStatusResponse(final ClientboundStatusResponsePacket qe) {
                if (this.receivedPing) {
                    jc4.disconnect(new TranslatableComponent("multiplayer.status.unrequested", new Object[0]));
                    return;
                }
                this.receivedPing = true;
                final ServerStatus qf3 = qe.getStatus();
                if (qf3.getDescription() != null) {
                    dki.motd = qf3.getDescription().getColoredString();
                }
                else {
                    dki.motd = "";
                }
                if (qf3.getVersion() != null) {
                    dki.version = qf3.getVersion().getName();
                    dki.protocol = qf3.getVersion().getProtocol();
                }
                else {
                    dki.version = I18n.get("multiplayer.status.old");
                    dki.protocol = 0;
                }
                if (qf3.getPlayers() != null) {
                    dki.status = new StringBuilder().append(ChatFormatting.GRAY).append("").append(qf3.getPlayers().getNumPlayers()).append("").append(ChatFormatting.DARK_GRAY).append("/").append(ChatFormatting.GRAY).append(qf3.getPlayers().getMaxPlayers()).toString();
                    if (ArrayUtils.isNotEmpty((Object[])qf3.getPlayers().getSample())) {
                        final StringBuilder stringBuilder4 = new StringBuilder();
                        for (final GameProfile gameProfile8 : qf3.getPlayers().getSample()) {
                            if (stringBuilder4.length() > 0) {
                                stringBuilder4.append("\n");
                            }
                            stringBuilder4.append(gameProfile8.getName());
                        }
                        if (qf3.getPlayers().getSample().length < qf3.getPlayers().getNumPlayers()) {
                            if (stringBuilder4.length() > 0) {
                                stringBuilder4.append("\n");
                            }
                            stringBuilder4.append(I18n.get("multiplayer.status.and_more", qf3.getPlayers().getNumPlayers() - qf3.getPlayers().getSample().length));
                        }
                        dki.playerList = stringBuilder4.toString();
                    }
                }
                else {
                    dki.status = ChatFormatting.DARK_GRAY + I18n.get("multiplayer.status.unknown");
                }
                if (qf3.getFavicon() != null) {
                    final String string4 = qf3.getFavicon();
                    if (string4.startsWith("data:image/png;base64,")) {
                        dki.setIconB64(string4.substring("data:image/png;base64,".length()));
                    }
                    else {
                        ServerStatusPinger.LOGGER.error("Invalid server icon (unknown format)");
                    }
                }
                else {
                    dki.setIconB64(null);
                }
                this.pingStart = Util.getMillis();
                jc4.send(new ServerboundPingRequestPacket(this.pingStart));
                this.success = true;
            }
            
            public void handlePongResponse(final ClientboundPongResponsePacket qd) {
                final long long3 = this.pingStart;
                final long long4 = Util.getMillis();
                dki.ping = long4 - long3;
                jc4.disconnect(new TranslatableComponent("multiplayer.status.finished", new Object[0]));
            }
            
            public void onDisconnect(final Component jo) {
                if (!this.success) {
                    ServerStatusPinger.LOGGER.error("Can't ping {}: {}", dki.ip, jo.getString());
                    dki.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_connect");
                    dki.status = "";
                    ServerStatusPinger.this.pingLegacyServer(dki);
                }
            }
            
            public Connection getConnection() {
                return jc4;
            }
        });
        try {
            jc4.send(new ClientIntentionPacket(dkh3.getHost(), dkh3.getPort(), ConnectionProtocol.STATUS));
            jc4.send(new ServerboundStatusRequestPacket());
        }
        catch (Throwable throwable5) {
            ServerStatusPinger.LOGGER.error(throwable5);
        }
    }
    
    private void pingLegacyServer(final ServerData dki) {
        final ServerAddress dkh3 = ServerAddress.parseString(dki.ip);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException ex) {}
                channel.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)new SimpleChannelInboundHandler<ByteBuf>() {
                        public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
                            super.channelActive(channelHandlerContext);
                            final ByteBuf byteBuf3 = Unpooled.buffer();
                            try {
                                byteBuf3.writeByte(254);
                                byteBuf3.writeByte(1);
                                byteBuf3.writeByte(250);
                                char[] arr4 = "MC|PingHost".toCharArray();
                                byteBuf3.writeShort(arr4.length);
                                for (final char character8 : arr4) {
                                    byteBuf3.writeChar((int)character8);
                                }
                                byteBuf3.writeShort(7 + 2 * dkh3.getHost().length());
                                byteBuf3.writeByte(127);
                                arr4 = dkh3.getHost().toCharArray();
                                byteBuf3.writeShort(arr4.length);
                                for (final char character8 : arr4) {
                                    byteBuf3.writeChar((int)character8);
                                }
                                byteBuf3.writeInt(dkh3.getPort());
                                channelHandlerContext.channel().writeAndFlush(byteBuf3).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                            }
                            finally {
                                byteBuf3.release();
                            }
                        }
                        
                        protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf) throws Exception {
                            final short short4 = byteBuf.readUnsignedByte();
                            if (short4 == 255) {
                                final String string5 = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                                final String[] arr6 = (String[])Iterables.toArray(ServerStatusPinger.SPLITTER.split((CharSequence)string5), (Class)String.class);
                                if ("§1".equals(arr6[0])) {
                                    final int integer7 = Mth.getInt(arr6[1], 0);
                                    final String string6 = arr6[2];
                                    final String string7 = arr6[3];
                                    final int integer8 = Mth.getInt(arr6[4], -1);
                                    final int integer9 = Mth.getInt(arr6[5], -1);
                                    dki.protocol = -1;
                                    dki.version = string6;
                                    dki.motd = string7;
                                    dki.status = new StringBuilder().append(ChatFormatting.GRAY).append("").append(integer8).append("").append(ChatFormatting.DARK_GRAY).append("/").append(ChatFormatting.GRAY).append(integer9).toString();
                                }
                            }
                            channelHandlerContext.close();
                        }
                        
                        public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable throwable) throws Exception {
                            channelHandlerContext.close();
                        }
                    } });
            }
        })).channel((Class)NioSocketChannel.class)).connect(dkh3.getHost(), dkh3.getPort());
    }
    
    public void tick() {
        synchronized (this.connections) {
            final Iterator<Connection> iterator3 = (Iterator<Connection>)this.connections.iterator();
            while (iterator3.hasNext()) {
                final Connection jc4 = (Connection)iterator3.next();
                if (jc4.isConnected()) {
                    jc4.tick();
                }
                else {
                    iterator3.remove();
                    jc4.handleDisconnection();
                }
            }
        }
    }
    
    public void removeAll() {
        synchronized (this.connections) {
            final Iterator<Connection> iterator3 = (Iterator<Connection>)this.connections.iterator();
            while (iterator3.hasNext()) {
                final Connection jc4 = (Connection)iterator3.next();
                if (jc4.isConnected()) {
                    iterator3.remove();
                    jc4.disconnect(new TranslatableComponent("multiplayer.status.cancelled", new Object[0]));
                }
            }
        }
    }
    
    static {
        SPLITTER = Splitter.on('\0').limit(6);
        LOGGER = LogManager.getLogger();
    }
}
