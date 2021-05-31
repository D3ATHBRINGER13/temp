package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlaceGhostRecipePacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private ResourceLocation recipe;
    
    public ClientboundPlaceGhostRecipePacket() {
    }
    
    public ClientboundPlaceGhostRecipePacket(final int integer, final Recipe<?> ber) {
        this.containerId = integer;
        this.recipe = ber.getId();
    }
    
    public ResourceLocation getRecipe() {
        return this.recipe;
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
        this.recipe = je.readResourceLocation();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeResourceLocation(this.recipe);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handlePlaceRecipe(this);
    }
}
