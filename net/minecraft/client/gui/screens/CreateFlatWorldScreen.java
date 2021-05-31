package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.world.item.Items;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import java.util.List;
import net.minecraft.client.resources.language.I18n;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;

public class CreateFlatWorldScreen extends Screen {
    private final CreateWorldScreen parent;
    private FlatLevelGeneratorSettings generator;
    private String columnType;
    private String columnHeight;
    private DetailsList list;
    private Button deleteLayerButton;
    
    public CreateFlatWorldScreen(final CreateWorldScreen dfq, final CompoundTag id) {
        super(new TranslatableComponent("createWorld.customize.flat.title", new Object[0]));
        this.generator = FlatLevelGeneratorSettings.getDefault();
        this.parent = dfq;
        this.loadLayers(id);
    }
    
    public String saveLayerString() {
        return this.generator.toString();
    }
    
    public CompoundTag saveLayers() {
        return (CompoundTag)this.generator.toObject((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue();
    }
    
    public void loadLayers(final String string) {
        this.generator = FlatLevelGeneratorSettings.fromString(string);
    }
    
    public void loadLayers(final CompoundTag id) {
        this.generator = FlatLevelGeneratorSettings.fromObject(new Dynamic((DynamicOps)NbtOps.INSTANCE, id));
    }
    
    @Override
    protected void init() {
        this.columnType = I18n.get("createWorld.customize.flat.tile");
        this.columnHeight = I18n.get("createWorld.customize.flat.height");
        this.list = new DetailsList();
        this.children.add(this.list);
        List<FlatLayerInfo> list3;
        int integer4;
        int integer5;
        this.deleteLayerButton = this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, I18n.get("createWorld.customize.flat.removeLayer"), czi -> {
            if (!this.hasValidSelection()) {
                return;
            }
            else {
                list3 = this.generator.getLayersInfo();
                integer4 = this.list.children().indexOf(((AbstractSelectionList<Object>)this.list).getSelected());
                integer5 = list3.size() - integer4 - 1;
                list3.remove(integer5);
                this.list.setSelected(list3.isEmpty() ? null : ((DetailsList.Entry)this.list.children().get(Math.min(integer4, list3.size() - 1))));
                this.generator.updateLayers();
                this.updateButtonValidity();
                return;
            }
        }));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, I18n.get("createWorld.customize.presets"), czi -> {
            this.minecraft.setScreen(new PresetFlatWorldScreen(this));
            this.generator.updateLayers();
            this.updateButtonValidity();
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("gui.done"), czi -> {
            this.parent.levelTypeOptions = this.saveLayers();
            this.minecraft.setScreen(this.parent);
            this.generator.updateLayers();
            this.updateButtonValidity();
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), czi -> {
            this.minecraft.setScreen(this.parent);
            this.generator.updateLayers();
            this.updateButtonValidity();
            return;
        }));
        this.generator.updateLayers();
        this.updateButtonValidity();
    }
    
    public void updateButtonValidity() {
        this.deleteLayerButton.active = this.hasValidSelection();
        this.list.resetRows();
    }
    
    private boolean hasValidSelection() {
        return this.list.getSelected() != null;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.list.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
        final int integer3 = this.width / 2 - 92 - 16;
        this.drawString(this.font, this.columnType, integer3, 32, 16777215);
        this.drawString(this.font, this.columnHeight, integer3 + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    class DetailsList extends ObjectSelectionList<Entry> {
        public DetailsList() {
            super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);
            for (int integer3 = 0; integer3 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++integer3) {
                this.addEntry(new Entry());
            }
        }
        
        @Override
        public void setSelected(@Nullable final Entry a) {
            super.setSelected(a);
            if (a != null) {
                final FlatLayerInfo cfw3 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - this.children().indexOf(a) - 1);
                final Item bce4 = cfw3.getBlockState().getBlock().asItem();
                if (bce4 != Items.AIR) {
                    NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new Object[] { bce4.getName(new ItemStack(bce4)) }).getString());
                }
            }
        }
        
        @Override
        protected void moveSelection(final int integer) {
            super.moveSelection(integer);
            CreateFlatWorldScreen.this.updateButtonValidity();
        }
        
        @Override
        protected boolean isFocused() {
            return CreateFlatWorldScreen.this.getFocused() == this;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return this.width - 70;
        }
        
        public void resetRows() {
            final int integer2 = this.children().indexOf(((AbstractSelectionList<Object>)this).getSelected());
            this.clearEntries();
            for (int integer3 = 0; integer3 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++integer3) {
                this.addEntry(new Entry());
            }
            final List<Entry> list3 = this.children();
            if (integer2 >= 0 && integer2 < list3.size()) {
                this.setSelected((Entry)list3.get(integer2));
            }
        }
        
        class Entry extends ObjectSelectionList.Entry<Entry> {
            private Entry() {
            }
            
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                final FlatLayerInfo cfw11 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - integer1 - 1);
                final BlockState bvt12 = cfw11.getBlockState();
                final Block bmv13 = bvt12.getBlock();
                Item bce14 = bmv13.asItem();
                if (bce14 == Items.AIR) {
                    if (bmv13 == Blocks.WATER) {
                        bce14 = Items.WATER_BUCKET;
                    }
                    else if (bmv13 == Blocks.LAVA) {
                        bce14 = Items.LAVA_BUCKET;
                    }
                }
                final ItemStack bcj15 = new ItemStack(bce14);
                final String string16 = bce14.getName(bcj15).getColoredString();
                this.blitSlot(integer3, integer2, bcj15);
                CreateFlatWorldScreen.this.font.draw(string16, (float)(integer3 + 18 + 5), (float)(integer2 + 3), 16777215);
                String string17;
                if (integer1 == 0) {
                    string17 = I18n.get("createWorld.customize.flat.layer.top", cfw11.getHeight());
                }
                else if (integer1 == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
                    string17 = I18n.get("createWorld.customize.flat.layer.bottom", cfw11.getHeight());
                }
                else {
                    string17 = I18n.get("createWorld.customize.flat.layer", cfw11.getHeight());
                }
                CreateFlatWorldScreen.this.font.draw(string17, (float)(integer3 + 2 + 213 - CreateFlatWorldScreen.this.font.width(string17)), (float)(integer2 + 3), 16777215);
            }
            
            public boolean mouseClicked(final double double1, final double double2, final int integer) {
                if (integer == 0) {
                    DetailsList.this.setSelected(this);
                    CreateFlatWorldScreen.this.updateButtonValidity();
                    return true;
                }
                return false;
            }
            
            private void blitSlot(final int integer1, final int integer2, final ItemStack bcj) {
                this.blitSlotBg(integer1 + 1, integer2 + 1);
                GlStateManager.enableRescaleNormal();
                if (!bcj.isEmpty()) {
                    Lighting.turnOnGui();
                    CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(bcj, integer1 + 2, integer2 + 2);
                    Lighting.turnOff();
                }
                GlStateManager.disableRescaleNormal();
            }
            
            private void blitSlotBg(final int integer1, final int integer2) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DetailsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
                GuiComponent.blit(integer1, integer2, CreateFlatWorldScreen.this.blitOffset, 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}
