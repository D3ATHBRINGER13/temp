package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.GameType;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginPacket implements Packet<ClientGamePacketListener> {
    private int playerId;
    private boolean hardcore;
    private GameType gameType;
    private DimensionType dimension;
    private int maxPlayers;
    private LevelType levelType;
    private int chunkRadius;
    private boolean reducedDebugInfo;
    
    public ClientboundLoginPacket() {
    }
    
    public ClientboundLoginPacket(final int integer1, final GameType bho, final boolean boolean3, final DimensionType byn, final int integer5, final LevelType bhy, final int integer7, final boolean boolean8) {
        this.playerId = integer1;
        this.dimension = byn;
        this.gameType = bho;
        this.maxPlayers = integer5;
        this.hardcore = boolean3;
        this.levelType = bhy;
        this.chunkRadius = integer7;
        this.reducedDebugInfo = boolean8;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.playerId = je.readInt();
        int integer3 = je.readUnsignedByte();
        this.hardcore = ((integer3 & 0x8) == 0x8);
        integer3 &= 0xFFFFFFF7;
        this.gameType = GameType.byId(integer3);
        this.dimension = DimensionType.getById(je.readInt());
        this.maxPlayers = je.readUnsignedByte();
        this.levelType = LevelType.getLevelType(je.readUtf(16));
        if (this.levelType == null) {
            this.levelType = LevelType.NORMAL;
        }
        this.chunkRadius = je.readVarInt();
        this.reducedDebugInfo = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.playerId);
        int integer3 = this.gameType.getId();
        if (this.hardcore) {
            integer3 |= 0x8;
        }
        je.writeByte(integer3);
        je.writeInt(this.dimension.getId());
        je.writeByte(this.maxPlayers);
        je.writeUtf(this.levelType.getName());
        je.writeVarInt(this.chunkRadius);
        je.writeBoolean(this.reducedDebugInfo);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleLogin(this);
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    public GameType getGameType() {
        return this.gameType;
    }
    
    public DimensionType getDimension() {
        return this.dimension;
    }
    
    public LevelType getLevelType() {
        return this.levelType;
    }
    
    public int getChunkRadius() {
        return this.chunkRadius;
    }
    
    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }
}
