package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.AnvilMenu;

public class AnvilScreen extends AbstractContainerScreen<AnvilMenu> implements ContainerListener {
    private static final ResourceLocation ANVIL_LOCATION;
    private EditBox name;
    
    public AnvilScreen(final AnvilMenu aym, final Inventory awf, final Component jo) {
        super(aym, awf, jo);
    }
    
    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        final int integer2 = (this.width - this.imageWidth) / 2;
        final int integer3 = (this.height - this.imageHeight) / 2;
        (this.name = new EditBox(this.font, integer2 + 62, integer3 + 24, 103, 12, I18n.get("container.repair"))).setCanLoseFocus(false);
        this.name.changeFocus(true);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(35);
        this.name.setResponder((Consumer<String>)this::onNameChanged);
        this.children.add(this.name);
        ((AnvilMenu)this.menu).addSlotListener(this);
        this.setInitialFocus(this.name);
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.name.getValue();
        this.init(cyc, integer2, integer3);
        this.name.setValue(string5);
    }
    
    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        ((AnvilMenu)this.menu).removeSlotListener(this);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.minecraft.player.closeContainer();
        }
        return this.name.keyPressed(integer1, integer2, integer3) || this.name.canConsumeInput() || super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.font.draw(this.title.getColoredString(), 60.0f, 6.0f, 4210752);
        final int integer3 = ((AnvilMenu)this.menu).getCost();
        if (integer3 > 0) {
            int integer4 = 8453920;
            boolean boolean6 = true;
            String string7 = I18n.get("container.repair.cost", integer3);
            if (integer3 >= 40 && !this.minecraft.player.abilities.instabuild) {
                string7 = I18n.get("container.repair.expensive");
                integer4 = 16736352;
            }
            else if (!((AnvilMenu)this.menu).getSlot(2).hasItem()) {
                boolean6 = false;
            }
            else if (!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.inventory.player)) {
                integer4 = 16736352;
            }
            if (boolean6) {
                final int integer5 = this.imageWidth - 8 - this.font.width(string7) - 2;
                final int integer6 = 69;
                GuiComponent.fill(integer5 - 2, 67, this.imageWidth - 8, 79, 1325400064);
                this.font.drawShadow(string7, (float)integer5, 69.0f, integer4);
            }
        }
        GlStateManager.enableLighting();
    }
    
    private void onNameChanged(final String string) {
        if (string.isEmpty()) {
            return;
        }
        String string2 = string;
        final Slot azx4 = ((AnvilMenu)this.menu).getSlot(0);
        if (azx4 != null && azx4.hasItem() && !azx4.getItem().hasCustomHoverName() && string2.equals(azx4.getItem().getHoverName().getString())) {
            string2 = "";
        }
        ((AnvilMenu)this.menu).setItemName(string2);
        this.minecraft.player.connection.send(new ServerboundRenameItemPacket(string2));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.name.render(integer1, integer2, float3);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(AnvilScreen.ANVIL_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(integer4 + 59, integer5 + 20, 0, this.imageHeight + (((AnvilMenu)this.menu).getSlot(0).hasItem() ? 0 : 16), 110, 16);
        if ((((AnvilMenu)this.menu).getSlot(0).hasItem() || ((AnvilMenu)this.menu).getSlot(1).hasItem()) && !((AnvilMenu)this.menu).getSlot(2).hasItem()) {
            this.blit(integer4 + 99, integer5 + 45, this.imageWidth, 0, 28, 21);
        }
    }
    
    @Override
    public void refreshContainer(final AbstractContainerMenu ayk, final NonNullList<ItemStack> fk) {
        this.slotChanged(ayk, 0, ayk.getSlot(0).getItem());
    }
    
    @Override
    public void slotChanged(final AbstractContainerMenu ayk, final int integer, final ItemStack bcj) {
        if (integer == 0) {
            this.name.setValue(bcj.isEmpty() ? "" : bcj.getHoverName().getString());
            this.name.setEditable(!bcj.isEmpty());
        }
    }
    
    @Override
    public void setContainerData(final AbstractContainerMenu ayk, final int integer2, final int integer3) {
    }
    
    static {
        ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
    }
}
