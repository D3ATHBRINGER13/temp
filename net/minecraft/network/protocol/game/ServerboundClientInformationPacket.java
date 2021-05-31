package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.network.protocol.Packet;

public class ServerboundClientInformationPacket implements Packet<ServerGamePacketListener> {
    private String language;
    private int viewDistance;
    private ChatVisiblity chatVisibility;
    private boolean chatColors;
    private int modelCustomisation;
    private HumanoidArm mainHand;
    
    public ServerboundClientInformationPacket() {
    }
    
    public ServerboundClientInformationPacket(final String string, final int integer2, final ChatVisiblity awe, final boolean boolean4, final int integer5, final HumanoidArm aiw) {
        this.language = string;
        this.viewDistance = integer2;
        this.chatVisibility = awe;
        this.chatColors = boolean4;
        this.modelCustomisation = integer5;
        this.mainHand = aiw;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.language = je.readUtf(16);
        this.viewDistance = je.readByte();
        this.chatVisibility = je.<ChatVisiblity>readEnum(ChatVisiblity.class);
        this.chatColors = je.readBoolean();
        this.modelCustomisation = je.readUnsignedByte();
        this.mainHand = je.<HumanoidArm>readEnum(HumanoidArm.class);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeUtf(this.language);
        je.writeByte(this.viewDistance);
        je.writeEnum(this.chatVisibility);
        je.writeBoolean(this.chatColors);
        je.writeByte(this.modelCustomisation);
        je.writeEnum(this.mainHand);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleClientInformation(this);
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public ChatVisiblity getChatVisibility() {
        return this.chatVisibility;
    }
    
    public boolean getChatColors() {
        return this.chatColors;
    }
    
    public int getModelCustomisation() {
        return this.modelCustomisation;
    }
    
    public HumanoidArm getMainHand() {
        return this.mainHand;
    }
}
