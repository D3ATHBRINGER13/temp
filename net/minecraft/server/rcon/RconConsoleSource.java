package net.minecraft.server.rcon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSource;

public class RconConsoleSource implements CommandSource {
    private final StringBuffer buffer;
    private final MinecraftServer server;
    
    public RconConsoleSource(final MinecraftServer minecraftServer) {
        this.buffer = new StringBuffer();
        this.server = minecraftServer;
    }
    
    public void prepareForCommand() {
        this.buffer.setLength(0);
    }
    
    public String getCommandResponse() {
        return this.buffer.toString();
    }
    
    public CommandSourceStack createCommandSourceStack() {
        final ServerLevel vk2 = this.server.getLevel(DimensionType.OVERWORLD);
        return new CommandSourceStack((CommandSource)this, new Vec3(vk2.getSharedSpawnPos()), Vec2.ZERO, vk2, 4, "Recon", (Component)new TextComponent("Rcon"), this.server, (Entity)null);
    }
    
    public void sendMessage(final Component jo) {
        this.buffer.append(jo.getString());
    }
    
    public boolean acceptsSuccess() {
        return true;
    }
    
    public boolean acceptsFailure() {
        return true;
    }
    
    public boolean shouldInformAdmins() {
        return this.server.shouldRconBroadcast();
    }
}
