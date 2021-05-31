package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.protocol.Packet;

public class ClientboundRespawnPacket implements Packet<ClientGamePacketListener> {
    private DimensionType dimension;
    private GameType playerGameType;
    private LevelType levelType;
    
    public ClientboundRespawnPacket() {
    }
    
    public ClientboundRespawnPacket(final DimensionType byn, final LevelType bhy, final GameType bho) {
        this.dimension = byn;
        this.playerGameType = bho;
        this.levelType = bhy;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleRespawn(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.dimension = DimensionType.getById(je.readInt());
        this.playerGameType = GameType.byId(je.readUnsignedByte());
        this.levelType = LevelType.getLevelType(je.readUtf(16));
        if (this.levelType == null) {
            this.levelType = LevelType.NORMAL;
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.dimension.getId());
        je.writeByte(this.playerGameType.getId());
        je.writeUtf(this.levelType.getName());
    }
    
    public DimensionType getDimension() {
        return this.dimension;
    }
    
    public GameType getPlayerGameType() {
        return this.playerGameType;
    }
    
    public LevelType getLevelType() {
        return this.levelType;
    }
}
