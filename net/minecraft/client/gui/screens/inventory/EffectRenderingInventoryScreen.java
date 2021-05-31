package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import java.util.Iterator;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.Collection;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected boolean doRenderEffects;
    
    public EffectRenderingInventoryScreen(final T ayk, final Inventory awf, final Component jo) {
        super(ayk, awf, jo);
    }
    
    @Override
    protected void init() {
        super.init();
        this.checkEffectRendering();
    }
    
    protected void checkEffectRendering() {
        if (this.minecraft.player.getActiveEffects().isEmpty()) {
            this.leftPos = (this.width - this.imageWidth) / 2;
            this.doRenderEffects = false;
        }
        else {
            this.leftPos = 160 + (this.width - this.imageWidth - 200) / 2;
            this.doRenderEffects = true;
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        super.render(integer1, integer2, float3);
        if (this.doRenderEffects) {
            this.renderEffects();
        }
    }
    
    private void renderEffects() {
        final int integer2 = this.leftPos - 124;
        final Collection<MobEffectInstance> collection3 = this.minecraft.player.getActiveEffects();
        if (collection3.isEmpty()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        int integer3 = 33;
        if (collection3.size() > 5) {
            integer3 = 132 / (collection3.size() - 1);
        }
        final Iterable<MobEffectInstance> iterable5 = (Iterable<MobEffectInstance>)Ordering.natural().sortedCopy((Iterable)collection3);
        this.renderBackgrounds(integer2, integer3, iterable5);
        this.renderIcons(integer2, integer3, iterable5);
        this.renderLabels(integer2, integer3, iterable5);
    }
    
    private void renderBackgrounds(final int integer1, final int integer2, final Iterable<MobEffectInstance> iterable) {
        this.minecraft.getTextureManager().bind(EffectRenderingInventoryScreen.INVENTORY_LOCATION);
        int integer3 = this.topPos;
        for (final MobEffectInstance aii7 : iterable) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit(integer1, integer3, 0, 166, 140, 32);
            integer3 += integer2;
        }
    }
    
    private void renderIcons(final int integer1, final int integer2, final Iterable<MobEffectInstance> iterable) {
        this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_MOB_EFFECTS);
        final MobEffectTextureManager dxr5 = this.minecraft.getMobEffectTextures();
        int integer3 = this.topPos;
        for (final MobEffectInstance aii8 : iterable) {
            final MobEffect aig9 = aii8.getEffect();
            GuiComponent.blit(integer1 + 6, integer3 + 7, this.blitOffset, 18, 18, dxr5.get(aig9));
            integer3 += integer2;
        }
    }
    
    private void renderLabels(final int integer1, final int integer2, final Iterable<MobEffectInstance> iterable) {
        int integer3 = this.topPos;
        for (final MobEffectInstance aii7 : iterable) {
            String string8 = I18n.get(aii7.getEffect().getDescriptionId());
            if (aii7.getAmplifier() >= 1 && aii7.getAmplifier() <= 9) {
                string8 = string8 + ' ' + I18n.get(new StringBuilder().append("enchantment.level.").append(aii7.getAmplifier() + 1).toString());
            }
            this.font.drawShadow(string8, (float)(integer1 + 10 + 18), (float)(integer3 + 6), 16777215);
            final String string9 = MobEffectUtil.formatDuration(aii7, 1.0f);
            this.font.drawShadow(string9, (float)(integer1 + 10 + 18), (float)(integer3 + 6 + 10), 8355711);
            integer3 += integer2;
        }
    }
}
