package net.minecraft.network;

import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import io.netty.util.AttributeKey;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.PacketFlow;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.protocol.Packet;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER;
    private static final Marker MARKER;
    private final PacketFlow flow;
    
    public PacketEncoder(final PacketFlow kd) {
        this.flow = kd;
    }
    
    protected void encode(final ChannelHandlerContext channelHandlerContext, final Packet<?> kc, final ByteBuf byteBuf) throws Exception {
        final ConnectionProtocol jd5 = (ConnectionProtocol)channelHandlerContext.channel().attr((AttributeKey)Connection.ATTRIBUTE_PROTOCOL).get();
        if (jd5 == null) {
            throw new RuntimeException(new StringBuilder().append("ConnectionProtocol unknown: ").append(kc).toString());
        }
        final Integer integer6 = jd5.getPacketId(this.flow, kc);
        if (PacketEncoder.LOGGER.isDebugEnabled()) {
            PacketEncoder.LOGGER.debug(PacketEncoder.MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr((AttributeKey)Connection.ATTRIBUTE_PROTOCOL).get(), integer6, kc.getClass().getName());
        }
        if (integer6 == null) {
            throw new IOException("Can't serialize unregistered packet");
        }
        final FriendlyByteBuf je7 = new FriendlyByteBuf(byteBuf);
        je7.writeVarInt(integer6);
        try {
            kc.write(je7);
        }
        catch (Throwable throwable8) {
            PacketEncoder.LOGGER.error(throwable8);
            if (kc.isSkippable()) {
                throw new SkipPacketException(throwable8);
            }
            throw throwable8;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MARKER = MarkerManager.getMarker("PACKET_SENT", Connection.PACKET_MARKER);
    }
}
