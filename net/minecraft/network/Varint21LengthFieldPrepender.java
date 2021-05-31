package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class Varint21LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
    protected void encode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf2, final ByteBuf byteBuf3) throws Exception {
        final int integer5 = byteBuf2.readableBytes();
        final int integer6 = FriendlyByteBuf.getVarIntSize(integer5);
        if (integer6 > 3) {
            throw new IllegalArgumentException(new StringBuilder().append("unable to fit ").append(integer5).append(" into ").append(3).toString());
        }
        final FriendlyByteBuf je7 = new FriendlyByteBuf(byteBuf3);
        je7.ensureWritable(integer6 + integer5);
        je7.writeVarInt(integer5);
        je7.writeBytes(byteBuf2, byteBuf2.readerIndex(), integer5);
    }
}
