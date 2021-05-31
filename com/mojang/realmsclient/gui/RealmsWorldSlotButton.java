package com.mojang.realmsclient.gui;

import javax.annotation.Nonnull;
import net.minecraft.realms.RealmsScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.realms.RealmsMth;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.realms.RealmsButtonProxy;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.function.Consumer;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.function.Supplier;
import net.minecraft.realms.RealmsButton;

public class RealmsWorldSlotButton extends RealmsButton {
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<String> toolTipSetter;
    private final Listener listener;
    private final int slotIndex;
    private int animTick;
    private State state;
    
    public RealmsWorldSlotButton(final int integer1, final int integer2, final int integer3, final int integer4, final Supplier<RealmsServer> supplier, final Consumer<String> consumer, final int integer7, final int integer8, final Listener b) {
        super(integer7, integer1, integer2, integer3, integer4, "");
        this.serverDataProvider = supplier;
        this.slotIndex = integer8;
        this.toolTipSetter = consumer;
        this.listener = b;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public void tick() {
        ++this.animTick;
        final RealmsServer realmsServer2 = (RealmsServer)this.serverDataProvider.get();
        if (realmsServer2 == null) {
            return;
        }
        final RealmsWorldOptions realmsWorldOptions5 = (RealmsWorldOptions)realmsServer2.slots.get(this.slotIndex);
        final boolean boolean10 = this.slotIndex == 4;
        boolean boolean11;
        String string4;
        long long6;
        String string5;
        boolean boolean12;
        if (boolean10) {
            boolean11 = realmsServer2.worldType.equals(RealmsServer.WorldType.MINIGAME);
            string4 = "Minigame";
            long6 = realmsServer2.minigameId;
            string5 = realmsServer2.minigameImage;
            boolean12 = (realmsServer2.minigameId == -1);
        }
        else {
            boolean11 = (realmsServer2.activeSlot == this.slotIndex && !realmsServer2.worldType.equals(RealmsServer.WorldType.MINIGAME));
            string4 = realmsWorldOptions5.getSlotName(this.slotIndex);
            long6 = realmsWorldOptions5.templateId;
            string5 = realmsWorldOptions5.templateImage;
            boolean12 = realmsWorldOptions5.empty;
        }
        String string6 = null;
        Action a11;
        if (boolean11) {
            final boolean boolean13 = realmsServer2.state == RealmsServer.State.OPEN || realmsServer2.state == RealmsServer.State.CLOSED;
            if (realmsServer2.expired || !boolean13) {
                a11 = Action.NOTHING;
            }
            else {
                a11 = Action.JOIN;
                string6 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.active");
            }
        }
        else if (boolean10) {
            if (realmsServer2.expired) {
                a11 = Action.NOTHING;
            }
            else {
                a11 = Action.SWITCH_SLOT;
                string6 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.minigame");
            }
        }
        else {
            a11 = Action.SWITCH_SLOT;
            string6 = Realms.getLocalizedString("mco.configure.world.slot.tooltip");
        }
        this.state = new State(boolean11, string4, long6, string5, boolean12, boolean10, a11, string6);
        String string7;
        if (a11 == Action.NOTHING) {
            string7 = string4;
        }
        else if (boolean10) {
            if (boolean12) {
                string7 = string6;
            }
            else {
                string7 = string6 + " " + string4 + " " + realmsServer2.minigameName;
            }
        }
        else {
            string7 = string6 + " " + string4;
        }
        this.setMessage(string7);
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        if (this.state == null) {
            return;
        }
        final RealmsButtonProxy realmsButtonProxy5 = this.getProxy();
        this.drawSlotFrame(realmsButtonProxy5.x, realmsButtonProxy5.y, integer1, integer2, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
    }
    
    private void drawSlotFrame(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5, final String string6, final int integer7, final long long8, @Nullable final String string9, final boolean boolean10, final boolean boolean11, final Action a, @Nullable final String string13) {
        final boolean boolean12 = this.getProxy().isHovered();
        if (this.getProxy().isMouseOver(integer3, integer4) && string13 != null) {
            this.toolTipSetter.accept(string13);
        }
        if (boolean11) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(long8), string9);
        }
        else if (boolean10) {
            Realms.bind("realms:textures/gui/realms/empty_frame.png");
        }
        else if (string9 != null && long8 != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(long8), string9);
        }
        else if (integer7 == 1) {
            Realms.bind("textures/gui/title/background/panorama_0.png");
        }
        else if (integer7 == 2) {
            Realms.bind("textures/gui/title/background/panorama_2.png");
        }
        else if (integer7 == 3) {
            Realms.bind("textures/gui/title/background/panorama_3.png");
        }
        if (boolean5) {
            final float float17 = 0.85f + 0.15f * RealmsMth.cos(this.animTick * 0.2f);
            GlStateManager.color4f(float17, float17, float17, 1.0f);
        }
        else {
            GlStateManager.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsScreen.blit(integer1 + 3, integer2 + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        Realms.bind("realms:textures/gui/realms/slot_frame.png");
        final boolean boolean13 = boolean12 && a != Action.NOTHING;
        if (boolean13) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        else if (boolean5) {
            GlStateManager.color4f(0.8f, 0.8f, 0.8f, 1.0f);
        }
        else {
            GlStateManager.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(string6, integer1 + 40, integer2 + 66, 16777215);
    }
    
    @Override
    public void onPress() {
        this.listener.onSlotClick(this.slotIndex, this.state.action, this.state.minigame, this.state.empty);
    }
    
    public enum Action {
        NOTHING, 
        SWITCH_SLOT, 
        JOIN;
    }
    
    public static class State {
        final boolean isCurrentlyActiveSlot;
        final String slotName;
        final long imageId;
        public final String image;
        public final boolean empty;
        final boolean minigame;
        public final Action action;
        final String actionPrompt;
        
        State(final boolean boolean1, final String string2, final long long3, @Nullable final String string4, final boolean boolean5, final boolean boolean6, @Nonnull final Action a, @Nullable final String string8) {
            this.isCurrentlyActiveSlot = boolean1;
            this.slotName = string2;
            this.imageId = long3;
            this.image = string4;
            this.empty = boolean5;
            this.minigame = boolean6;
            this.action = a;
            this.actionPrompt = string8;
        }
    }
    
    public interface Listener {
        void onSlotClick(final int integer, @Nonnull final Action a, final boolean boolean3, final boolean boolean4);
    }
}
