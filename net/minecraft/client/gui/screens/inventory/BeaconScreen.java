package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.language.I18n;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BeaconMenu;

public class BeaconScreen extends AbstractContainerScreen<BeaconMenu> {
    private static final ResourceLocation BEACON_LOCATION;
    private BeaconConfirmButton confirmButton;
    private boolean initPowerButtons;
    private MobEffect primary;
    private MobEffect secondary;
    
    public BeaconScreen(final BeaconMenu ayn, final Inventory awf, final Component jo) {
        super(ayn, awf, jo);
        this.imageWidth = 230;
        this.imageHeight = 219;
        ayn.addSlotListener(new ContainerListener() {
            public void refreshContainer(final AbstractContainerMenu ayk, final NonNullList<ItemStack> fk) {
            }
            
            public void slotChanged(final AbstractContainerMenu ayk, final int integer, final ItemStack bcj) {
            }
            
            public void setContainerData(final AbstractContainerMenu ayk, final int integer2, final int integer3) {
                BeaconScreen.this.primary = ayn.getPrimaryEffect();
                BeaconScreen.this.secondary = ayn.getSecondaryEffect();
                BeaconScreen.this.initPowerButtons = true;
            }
        });
    }
    
    @Override
    protected void init() {
        super.init();
        this.confirmButton = this.<BeaconConfirmButton>addButton(new BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
        this.<BeaconCancelButton>addButton(new BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
        this.initPowerButtons = true;
        this.confirmButton.active = false;
    }
    
    @Override
    public void tick() {
        super.tick();
        final int integer2 = ((BeaconMenu)this.menu).getLevels();
        if (this.initPowerButtons && integer2 >= 0) {
            this.initPowerButtons = false;
            for (int integer3 = 0; integer3 <= 2; ++integer3) {
                final int integer4 = BeaconBlockEntity.BEACON_EFFECTS[integer3].length;
                final int integer5 = integer4 * 22 + (integer4 - 1) * 2;
                for (int integer6 = 0; integer6 < integer4; ++integer6) {
                    final MobEffect aig7 = BeaconBlockEntity.BEACON_EFFECTS[integer3][integer6];
                    final BeaconPowerButton c8 = new BeaconPowerButton(this.leftPos + 76 + integer6 * 24 - integer5 / 2, this.topPos + 22 + integer3 * 25, aig7, true);
                    this.<BeaconPowerButton>addButton(c8);
                    if (integer3 >= integer2) {
                        c8.active = false;
                    }
                    else if (aig7 == this.primary) {
                        c8.setSelected(true);
                    }
                }
            }
            int integer3 = 3;
            final int integer4 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
            final int integer5 = integer4 * 22 + (integer4 - 1) * 2;
            for (int integer6 = 0; integer6 < integer4 - 1; ++integer6) {
                final MobEffect aig7 = BeaconBlockEntity.BEACON_EFFECTS[3][integer6];
                final BeaconPowerButton c8 = new BeaconPowerButton(this.leftPos + 167 + integer6 * 24 - integer5 / 2, this.topPos + 47, aig7, false);
                this.<BeaconPowerButton>addButton(c8);
                if (3 >= integer2) {
                    c8.active = false;
                }
                else if (aig7 == this.secondary) {
                    c8.setSelected(true);
                }
            }
            if (this.primary != null) {
                final BeaconPowerButton c9 = new BeaconPowerButton(this.leftPos + 167 + (integer4 - 1) * 24 - integer5 / 2, this.topPos + 47, this.primary, false);
                this.<BeaconPowerButton>addButton(c9);
                if (3 >= integer2) {
                    c9.active = false;
                }
                else if (this.primary == this.secondary) {
                    c9.setSelected(true);
                }
            }
        }
        this.confirmButton.active = (((BeaconMenu)this.menu).hasPayment() && this.primary != null);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        Lighting.turnOff();
        this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.primary"), 62, 10, 14737632);
        this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.secondary"), 169, 10, 14737632);
        for (final AbstractWidget czg5 : this.buttons) {
            if (czg5.isHovered()) {
                czg5.renderToolTip(integer1 - this.leftPos, integer2 - this.topPos);
                break;
            }
        }
        Lighting.turnOnGui();
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BeaconScreen.BEACON_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        this.itemRenderer.blitOffset = 100.0f;
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), integer4 + 42, integer5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), integer4 + 42 + 22, integer5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), integer4 + 42 + 44, integer5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), integer4 + 42 + 66, integer5 + 109);
        this.itemRenderer.blitOffset = 0.0f;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    static {
        BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
    }
    
    abstract static class BeaconScreenButton extends AbstractButton {
        private boolean selected;
        
        protected BeaconScreenButton(final int integer1, final int integer2) {
            super(integer1, integer2, 22, 22, "");
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            Minecraft.getInstance().getTextureManager().bind(BeaconScreen.BEACON_LOCATION);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            final int integer3 = 219;
            int integer4 = 0;
            if (!this.active) {
                integer4 += this.width * 2;
            }
            else if (this.selected) {
                integer4 += this.width * 1;
            }
            else if (this.isHovered()) {
                integer4 += this.width * 3;
            }
            this.blit(this.x, this.y, integer4, 219, this.width, this.height);
            this.renderIcon();
        }
        
        protected abstract void renderIcon();
        
        public boolean isSelected() {
            return this.selected;
        }
        
        public void setSelected(final boolean boolean1) {
            this.selected = boolean1;
        }
    }
    
    class BeaconPowerButton extends BeaconScreenButton {
        private final MobEffect effect;
        private final TextureAtlasSprite sprite;
        private final boolean isPrimary;
        
        public BeaconPowerButton(final int integer2, final int integer3, final MobEffect aig, final boolean boolean5) {
            super(integer2, integer3);
            this.effect = aig;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(aig);
            this.isPrimary = boolean5;
        }
        
        @Override
        public void onPress() {
            if (this.isSelected()) {
                return;
            }
            if (this.isPrimary) {
                BeaconScreen.this.primary = this.effect;
            }
            else {
                BeaconScreen.this.secondary = this.effect;
            }
            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
        }
        
        @Override
        public void renderToolTip(final int integer1, final int integer2) {
            String string4 = I18n.get(this.effect.getDescriptionId());
            if (!this.isPrimary && this.effect != MobEffects.REGENERATION) {
                string4 += " II";
            }
            BeaconScreen.this.renderTooltip(string4, integer1, integer2);
        }
        
        @Override
        protected void renderIcon() {
            Minecraft.getInstance().getTextureManager().bind(TextureAtlas.LOCATION_MOB_EFFECTS);
            GuiComponent.blit(this.x + 2, this.y + 2, this.blitOffset, 18, 18, this.sprite);
        }
    }
    
    abstract static class BeaconSpriteScreenButton extends BeaconScreenButton {
        private final int iconX;
        private final int iconY;
        
        protected BeaconSpriteScreenButton(final int integer1, final int integer2, final int integer3, final int integer4) {
            super(integer1, integer2);
            this.iconX = integer3;
            this.iconY = integer4;
        }
        
        @Override
        protected void renderIcon() {
            this.blit(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
        }
    }
    
    class BeaconConfirmButton extends BeaconSpriteScreenButton {
        public BeaconConfirmButton(final int integer2, final int integer3) {
            super(integer2, integer3, 90, 220);
        }
        
        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(MobEffect.getId(BeaconScreen.this.primary), MobEffect.getId(BeaconScreen.this.secondary)));
            BeaconScreen.this.minecraft.player.connection.send(new ServerboundContainerClosePacket(BeaconScreen.this.minecraft.player.containerMenu.containerId));
            BeaconScreen.this.minecraft.setScreen(null);
        }
        
        @Override
        public void renderToolTip(final int integer1, final int integer2) {
            BeaconScreen.this.renderTooltip(I18n.get("gui.done"), integer1, integer2);
        }
    }
    
    class BeaconCancelButton extends BeaconSpriteScreenButton {
        public BeaconCancelButton(final int integer2, final int integer3) {
            super(integer2, integer3, 112, 220);
        }
        
        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.player.connection.send(new ServerboundContainerClosePacket(BeaconScreen.this.minecraft.player.containerMenu.containerId));
            BeaconScreen.this.minecraft.setScreen(null);
        }
        
        @Override
        public void renderToolTip(final int integer1, final int integer2) {
            BeaconScreen.this.renderTooltip(I18n.get("gui.cancel"), integer1, integer2);
        }
    }
}
