package net.minecraft.client.multiplayer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import java.util.Iterator;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;
import java.util.Map;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public class ClientAdvancements {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    private final AdvancementList advancements;
    private final Map<Advancement, AdvancementProgress> progress;
    @Nullable
    private Listener listener;
    @Nullable
    private Advancement selectedTab;
    
    public ClientAdvancements(final Minecraft cyc) {
        this.advancements = new AdvancementList();
        this.progress = (Map<Advancement, AdvancementProgress>)Maps.newHashMap();
        this.minecraft = cyc;
    }
    
    public void update(final ClientboundUpdateAdvancementsPacket nn) {
        if (nn.shouldReset()) {
            this.advancements.clear();
            this.progress.clear();
        }
        this.advancements.remove(nn.getRemoved());
        this.advancements.add(nn.getAdded());
        for (final Map.Entry<ResourceLocation, AdvancementProgress> entry4 : nn.getProgress().entrySet()) {
            final Advancement q5 = this.advancements.get((ResourceLocation)entry4.getKey());
            if (q5 != null) {
                final AdvancementProgress s6 = (AdvancementProgress)entry4.getValue();
                s6.update(q5.getCriteria(), q5.getRequirements());
                this.progress.put(q5, s6);
                if (this.listener != null) {
                    this.listener.onUpdateAdvancementProgress(q5, s6);
                }
                if (nn.shouldReset() || !s6.isDone() || q5.getDisplay() == null || !q5.getDisplay().shouldShowToast()) {
                    continue;
                }
                this.minecraft.getToasts().addToast(new AdvancementToast(q5));
            }
            else {
                ClientAdvancements.LOGGER.warn("Server informed client about progress for unknown advancement {}", entry4.getKey());
            }
        }
    }
    
    public AdvancementList getAdvancements() {
        return this.advancements;
    }
    
    public void setSelectedTab(@Nullable final Advancement q, final boolean boolean2) {
        final ClientPacketListener dkc4 = this.minecraft.getConnection();
        if (dkc4 != null && q != null && boolean2) {
            dkc4.send(ServerboundSeenAdvancementsPacket.openedTab(q));
        }
        if (this.selectedTab != q) {
            this.selectedTab = q;
            if (this.listener != null) {
                this.listener.onSelectedTabChanged(q);
            }
        }
    }
    
    public void setListener(@Nullable final Listener a) {
        this.listener = a;
        this.advancements.setListener(a);
        if (a != null) {
            for (final Map.Entry<Advancement, AdvancementProgress> entry4 : this.progress.entrySet()) {
                a.onUpdateAdvancementProgress((Advancement)entry4.getKey(), (AdvancementProgress)entry4.getValue());
            }
            a.onSelectedTabChanged(this.selectedTab);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public interface Listener extends AdvancementList.Listener {
        void onUpdateAdvancementProgress(final Advancement q, final AdvancementProgress s);
        
        void onSelectedTabChanged(@Nullable final Advancement q);
    }
}
