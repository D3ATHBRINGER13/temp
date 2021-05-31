package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlaceRecipePacket implements Packet<ServerGamePacketListener> {
    private int containerId;
    private ResourceLocation recipe;
    private boolean shiftDown;
    
    public ServerboundPlaceRecipePacket() {
    }
    
    public ServerboundPlaceRecipePacket(final int integer, final Recipe<?> ber, final boolean boolean3) {
        this.containerId = integer;
        this.recipe = ber.getId();
        this.shiftDown = boolean3;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
        this.recipe = je.readResourceLocation();
        this.shiftDown = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeResourceLocation(this.recipe);
        je.writeBoolean(this.shiftDown);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handlePlaceRecipe(this);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public ResourceLocation getRecipe() {
        return this.recipe;
    }
    
    public boolean isShiftDown() {
        return this.shiftDown;
    }
}
