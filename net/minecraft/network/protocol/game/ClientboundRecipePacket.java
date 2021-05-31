package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Iterator;
import java.io.IOException;
import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundRecipePacket implements Packet<ClientGamePacketListener> {
    private State state;
    private List<ResourceLocation> recipes;
    private List<ResourceLocation> toHighlight;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    
    public ClientboundRecipePacket() {
    }
    
    public ClientboundRecipePacket(final State a, final Collection<ResourceLocation> collection2, final Collection<ResourceLocation> collection3, final boolean boolean4, final boolean boolean5, final boolean boolean6, final boolean boolean7) {
        this.state = a;
        this.recipes = (List<ResourceLocation>)ImmutableList.copyOf((Collection)collection2);
        this.toHighlight = (List<ResourceLocation>)ImmutableList.copyOf((Collection)collection3);
        this.guiOpen = boolean4;
        this.filteringCraftable = boolean5;
        this.furnaceGuiOpen = boolean6;
        this.furnaceFilteringCraftable = boolean7;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddOrRemoveRecipes(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.state = je.<State>readEnum(State.class);
        this.guiOpen = je.readBoolean();
        this.filteringCraftable = je.readBoolean();
        this.furnaceGuiOpen = je.readBoolean();
        this.furnaceFilteringCraftable = je.readBoolean();
        int integer3 = je.readVarInt();
        this.recipes = (List<ResourceLocation>)Lists.newArrayList();
        for (int integer4 = 0; integer4 < integer3; ++integer4) {
            this.recipes.add(je.readResourceLocation());
        }
        if (this.state == State.INIT) {
            integer3 = je.readVarInt();
            this.toHighlight = (List<ResourceLocation>)Lists.newArrayList();
            for (int integer4 = 0; integer4 < integer3; ++integer4) {
                this.toHighlight.add(je.readResourceLocation());
            }
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.state);
        je.writeBoolean(this.guiOpen);
        je.writeBoolean(this.filteringCraftable);
        je.writeBoolean(this.furnaceGuiOpen);
        je.writeBoolean(this.furnaceFilteringCraftable);
        je.writeVarInt(this.recipes.size());
        for (final ResourceLocation qv4 : this.recipes) {
            je.writeResourceLocation(qv4);
        }
        if (this.state == State.INIT) {
            je.writeVarInt(this.toHighlight.size());
            for (final ResourceLocation qv4 : this.toHighlight) {
                je.writeResourceLocation(qv4);
            }
        }
    }
    
    public List<ResourceLocation> getRecipes() {
        return this.recipes;
    }
    
    public List<ResourceLocation> getHighlights() {
        return this.toHighlight;
    }
    
    public boolean isGuiOpen() {
        return this.guiOpen;
    }
    
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
    
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }
    
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }
    
    public State getState() {
        return this.state;
    }
    
    public enum State {
        INIT, 
        ADD, 
        REMOVE;
    }
}
