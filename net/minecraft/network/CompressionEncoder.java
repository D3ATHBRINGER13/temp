package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Deflater;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private final byte[] encodeBuf;
    private final Deflater deflater;
    private int threshold;
    
    public CompressionEncoder(final int integer) {
        this.encodeBuf = new byte[8192];
        this.threshold = integer;
        this.deflater = new Deflater();
    }
    
    protected void encode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf2, final ByteBuf byteBuf3) throws Exception {
        final int integer5 = byteBuf2.readableBytes();
        final FriendlyByteBuf je6 = new FriendlyByteBuf(byteBuf3);
        if (integer5 < this.threshold) {
            je6.writeVarInt(0);
            je6.writeBytes(byteBuf2);
        }
        else {
            final byte[] arr7 = new byte[integer5];
            byteBuf2.readBytes(arr7);
            je6.writeVarInt(arr7.length);
            this.deflater.setInput(arr7, 0, integer5);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                final int integer6 = this.deflater.deflate(this.encodeBuf);
                je6.writeBytes(this.encodeBuf, 0, integer6);
            }
            this.deflater.reset();
        }
    }
    
    public void setThreshold(final int integer) {
        this.threshold = integer;
    }
}
