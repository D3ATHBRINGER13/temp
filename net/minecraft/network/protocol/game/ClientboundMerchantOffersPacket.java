package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.network.protocol.Packet;

public class ClientboundMerchantOffersPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private MerchantOffers offers;
    private int villagerLevel;
    private int villagerXp;
    private boolean showProgress;
    private boolean canRestock;
    
    public ClientboundMerchantOffersPacket() {
    }
    
    public ClientboundMerchantOffersPacket(final int integer1, final MerchantOffers bgv, final int integer3, final int integer4, final boolean boolean5, final boolean boolean6) {
        this.containerId = integer1;
        this.offers = bgv;
        this.villagerLevel = integer3;
        this.villagerXp = integer4;
        this.showProgress = boolean5;
        this.canRestock = boolean6;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readVarInt();
        this.offers = MerchantOffers.createFromStream(je);
        this.villagerLevel = je.readVarInt();
        this.villagerXp = je.readVarInt();
        this.showProgress = je.readBoolean();
        this.canRestock = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.containerId);
        this.offers.writeToStream(je);
        je.writeVarInt(this.villagerLevel);
        je.writeVarInt(this.villagerXp);
        je.writeBoolean(this.showProgress);
        je.writeBoolean(this.canRestock);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleMerchantOffers(this);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public MerchantOffers getOffers() {
        return this.offers;
    }
    
    public int getVillagerLevel() {
        return this.villagerLevel;
    }
    
    public int getVillagerXp() {
        return this.villagerXp;
    }
    
    public boolean showProgress() {
        return this.showProgress;
    }
    
    public boolean canRestock() {
        return this.canRestock;
    }
}
