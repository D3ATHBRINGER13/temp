package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.chat.NarratorChatListener;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ObjectSelectionList;
import java.util.Collections;
import net.minecraft.world.item.Items;
import java.util.Arrays;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import com.google.common.collect.Lists;
import java.util.Iterator;
import com.google.common.collect.Maps;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.ItemLike;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import java.util.List;

public class PresetFlatWorldScreen extends Screen {
    private static final List<PresetInfo> PRESETS;
    private final CreateFlatWorldScreen parent;
    private String shareText;
    private String listText;
    private PresetsList list;
    private Button selectButton;
    private EditBox export;
    
    public PresetFlatWorldScreen(final CreateFlatWorldScreen dbq) {
        super(new TranslatableComponent("createWorld.customize.presets.title", new Object[0]));
        this.parent = dbq;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.shareText = I18n.get("createWorld.customize.presets.share");
        this.listText = I18n.get("createWorld.customize.presets.list");
        (this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText)).setMaxLength(1230);
        this.export.setValue(this.parent.saveLayerString());
        this.children.add(this.export);
        this.list = new PresetsList();
        this.children.add(this.list);
        this.selectButton = this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("createWorld.customize.presets.select"), czi -> {
            this.parent.loadLayers(this.export.getValue());
            this.minecraft.setScreen(this.parent);
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(this.parent)));
        this.updateButtonValidity(this.list.getSelected() != null);
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return this.list.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.export.getValue();
        this.init(cyc, integer2, integer3);
        this.export.setValue(string5);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.list.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
        this.drawString(this.font, this.shareText, 50, 30, 10526880);
        this.drawString(this.font, this.listText, 50, 70, 10526880);
        this.export.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public void tick() {
        this.export.tick();
        super.tick();
    }
    
    public void updateButtonValidity(final boolean boolean1) {
        this.selectButton.active = (boolean1 || this.export.getValue().length() > 1);
    }
    
    private static void preset(final String string, final ItemLike bhq, final Biome bio, final List<String> list, final FlatLayerInfo... arr) {
        final FlatLevelGeneratorSettings cfx6 = ChunkGeneratorType.FLAT.createSettings();
        for (int integer7 = arr.length - 1; integer7 >= 0; --integer7) {
            cfx6.getLayersInfo().add(arr[integer7]);
        }
        cfx6.setBiome(bio);
        cfx6.updateLayers();
        for (final String string2 : list) {
            cfx6.getStructuresOptions().put(string2, Maps.newHashMap());
        }
        PresetFlatWorldScreen.PRESETS.add(new PresetInfo(bhq.asItem(), string, cfx6.toString()));
    }
    
    static {
        PRESETS = (List)Lists.newArrayList();
        preset(I18n.get("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, (List<String>)Arrays.asList((Object[])new String[] { "village" }), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, (List<String>)Arrays.asList((Object[])new String[] { "biome_1", "dungeon", "decoration", "stronghold", "mineshaft" }), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, (List<String>)Arrays.asList((Object[])new String[] { "biome_1", "oceanmonument" }), new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, (List<String>)Arrays.asList((Object[])new String[] { "village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost" }), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, (List<String>)Arrays.asList((Object[])new String[] { "village", "biome_1" }), new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, (List<String>)Arrays.asList((Object[])new String[] { "village", "biome_1" }), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
        preset(I18n.get("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, (List<String>)Arrays.asList((Object[])new String[] { "village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon" }), new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, (List<String>)Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        preset(I18n.get("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, (List<String>)Arrays.asList((Object[])new String[] { "decoration" }), new FlatLayerInfo(1, Blocks.AIR));
    }
    
    class PresetsList extends ObjectSelectionList<Entry> {
        public PresetsList() {
            super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);
            for (int integer3 = 0; integer3 < PresetFlatWorldScreen.PRESETS.size(); ++integer3) {
                this.addEntry(new Entry());
            }
        }
        
        @Override
        public void setSelected(@Nullable final Entry a) {
            super.setSelected(a);
            if (a != null) {
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new Object[] { ((PresetInfo)PresetFlatWorldScreen.PRESETS.get(this.children().indexOf(a))).name }).getString());
            }
        }
        
        @Override
        protected void moveSelection(final int integer) {
            super.moveSelection(integer);
            PresetFlatWorldScreen.this.updateButtonValidity(true);
        }
        
        @Override
        protected boolean isFocused() {
            return PresetFlatWorldScreen.this.getFocused() == this;
        }
        
        @Override
        public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
            if (super.keyPressed(integer1, integer2, integer3)) {
                return true;
            }
            if ((integer1 == 257 || integer1 == 335) && this.getSelected() != null) {
                this.getSelected().select();
            }
            return false;
        }
        
        public class Entry extends ObjectSelectionList.Entry<Entry> {
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                final PresetInfo a11 = (PresetInfo)PresetFlatWorldScreen.PRESETS.get(integer1);
                this.blitSlot(integer3, integer2, a11.icon);
                PresetFlatWorldScreen.this.font.draw(a11.name, (float)(integer3 + 18 + 5), (float)(integer2 + 6), 16777215);
            }
            
            public boolean mouseClicked(final double double1, final double double2, final int integer) {
                if (integer == 0) {
                    this.select();
                }
                return false;
            }
            
            private void select() {
                PresetsList.this.setSelected(this);
                PresetFlatWorldScreen.this.updateButtonValidity(true);
                PresetFlatWorldScreen.this.export.setValue(((PresetInfo)PresetFlatWorldScreen.PRESETS.get(PresetsList.this.children().indexOf(this))).value);
                PresetFlatWorldScreen.this.export.moveCursorToStart();
            }
            
            private void blitSlot(final int integer1, final int integer2, final Item bce) {
                this.blitSlotBg(integer1 + 1, integer2 + 1);
                GlStateManager.enableRescaleNormal();
                Lighting.turnOnGui();
                PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(bce), integer1 + 2, integer2 + 2);
                Lighting.turnOff();
                GlStateManager.disableRescaleNormal();
            }
            
            private void blitSlotBg(final int integer1, final int integer2) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                PresetsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
                GuiComponent.blit(integer1, integer2, PresetFlatWorldScreen.this.blitOffset, 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
    
    static class PresetInfo {
        public final Item icon;
        public final String name;
        public final String value;
        
        public PresetInfo(final Item bce, final String string2, final String string3) {
            this.icon = bce;
            this.name = string2;
            this.value = string3;
        }
    }
}
