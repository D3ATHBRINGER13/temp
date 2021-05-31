package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ServerboundRecipeBookUpdatePacket implements Packet<ServerGamePacketListener> {
    private Purpose purpose;
    private ResourceLocation recipe;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    private boolean blastFurnaceGuiOpen;
    private boolean blastFurnaceFilteringCraftable;
    private boolean smokerGuiOpen;
    private boolean smokerFilteringCraftable;
    
    public ServerboundRecipeBookUpdatePacket() {
    }
    
    public ServerboundRecipeBookUpdatePacket(final Recipe<?> ber) {
        this.purpose = Purpose.SHOWN;
        this.recipe = ber.getId();
    }
    
    public ServerboundRecipeBookUpdatePacket(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4, final boolean boolean5, final boolean boolean6) {
        this.purpose = Purpose.SETTINGS;
        this.guiOpen = boolean1;
        this.filteringCraftable = boolean2;
        this.furnaceGuiOpen = boolean3;
        this.furnaceFilteringCraftable = boolean4;
        this.blastFurnaceGuiOpen = boolean5;
        this.blastFurnaceFilteringCraftable = boolean6;
        this.smokerGuiOpen = boolean5;
        this.smokerFilteringCraftable = boolean6;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.purpose = je.<Purpose>readEnum(Purpose.class);
        if (this.purpose == Purpose.SHOWN) {
            this.recipe = je.readResourceLocation();
        }
        else if (this.purpose == Purpose.SETTINGS) {
            this.guiOpen = je.readBoolean();
            this.filteringCraftable = je.readBoolean();
            this.furnaceGuiOpen = je.readBoolean();
            this.furnaceFilteringCraftable = je.readBoolean();
            this.blastFurnaceGuiOpen = je.readBoolean();
            this.blastFurnaceFilteringCraftable = je.readBoolean();
            this.smokerGuiOpen = je.readBoolean();
            this.smokerFilteringCraftable = je.readBoolean();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.purpose);
        if (this.purpose == Purpose.SHOWN) {
            je.writeResourceLocation(this.recipe);
        }
        else if (this.purpose == Purpose.SETTINGS) {
            je.writeBoolean(this.guiOpen);
            je.writeBoolean(this.filteringCraftable);
            je.writeBoolean(this.furnaceGuiOpen);
            je.writeBoolean(this.furnaceFilteringCraftable);
            je.writeBoolean(this.blastFurnaceGuiOpen);
            je.writeBoolean(this.blastFurnaceFilteringCraftable);
            je.writeBoolean(this.smokerGuiOpen);
            je.writeBoolean(this.smokerFilteringCraftable);
        }
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleRecipeBookUpdatePacket(this);
    }
    
    public Purpose getPurpose() {
        return this.purpose;
    }
    
    public ResourceLocation getRecipe() {
        return this.recipe;
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
    
    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }
    
    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }
    
    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }
    
    public boolean isSmokerFilteringCraftable() {
        return this.smokerFilteringCraftable;
    }
    
    public enum Purpose {
        SHOWN, 
        SETTINGS;
    }
}
