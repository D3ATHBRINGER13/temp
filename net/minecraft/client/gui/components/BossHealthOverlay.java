package net.minecraft.client.gui.components;

import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import java.util.Iterator;
import net.minecraft.world.BossEvent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiComponent;

public class BossHealthOverlay extends GuiComponent {
    private static final ResourceLocation GUI_BARS_LOCATION;
    private final Minecraft minecraft;
    private final Map<UUID, LerpingBossEvent> events;
    
    public BossHealthOverlay(final Minecraft cyc) {
        this.events = (Map<UUID, LerpingBossEvent>)Maps.newLinkedHashMap();
        this.minecraft = cyc;
    }
    
    public void render() {
        if (this.events.isEmpty()) {
            return;
        }
        final int integer2 = this.minecraft.window.getGuiScaledWidth();
        int integer3 = 12;
        for (final LerpingBossEvent czq5 : this.events.values()) {
            final int integer4 = integer2 / 2 - 91;
            final int integer5 = integer3;
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(BossHealthOverlay.GUI_BARS_LOCATION);
            this.drawBar(integer4, integer5, czq5);
            final String string8 = czq5.getName().getColoredString();
            final int integer6 = this.minecraft.font.width(string8);
            final int integer7 = integer2 / 2 - integer6 / 2;
            final int integer8 = integer5 - 9;
            this.minecraft.font.drawShadow(string8, (float)integer7, (float)integer8, 16777215);
            final int n = integer3;
            final int n2 = 10;
            this.minecraft.font.getClass();
            integer3 = n + (n2 + 9);
            if (integer3 >= this.minecraft.window.getGuiScaledHeight() / 3) {
                break;
            }
        }
    }
    
    private void drawBar(final int integer1, final int integer2, final BossEvent agz) {
        this.blit(integer1, integer2, 0, agz.getColor().ordinal() * 5 * 2, 182, 5);
        if (agz.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            this.blit(integer1, integer2, 0, 80 + (agz.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        final int integer3 = (int)(agz.getPercent() * 183.0f);
        if (integer3 > 0) {
            this.blit(integer1, integer2, 0, agz.getColor().ordinal() * 5 * 2 + 5, integer3, 5);
            if (agz.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
                this.blit(integer1, integer2, 0, 80 + (agz.getOverlay().ordinal() - 1) * 5 * 2 + 5, integer3, 5);
            }
        }
    }
    
    public void update(final ClientboundBossEventPacket kt) {
        if (kt.getOperation() == ClientboundBossEventPacket.Operation.ADD) {
            this.events.put(kt.getId(), new LerpingBossEvent(kt));
        }
        else if (kt.getOperation() == ClientboundBossEventPacket.Operation.REMOVE) {
            this.events.remove(kt.getId());
        }
        else {
            ((LerpingBossEvent)this.events.get(kt.getId())).update(kt);
        }
    }
    
    public void reset() {
        this.events.clear();
    }
    
    public boolean shouldPlayMusic() {
        if (!this.events.isEmpty()) {
            for (final BossEvent agz3 : this.events.values()) {
                if (agz3.shouldPlayBossMusic()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean shouldDarkenScreen() {
        if (!this.events.isEmpty()) {
            for (final BossEvent agz3 : this.events.values()) {
                if (agz3.shouldDarkenScreen()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean shouldCreateWorldFog() {
        if (!this.events.isEmpty()) {
            for (final BossEvent agz3 : this.events.values()) {
                if (agz3.shouldCreateWorldFog()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    }
}
