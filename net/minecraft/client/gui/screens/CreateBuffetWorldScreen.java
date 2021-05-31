package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.client.gui.chat.NarratorChatListener;
import javax.annotation.Nullable;
import java.util.Comparator;
import net.minecraft.client.gui.components.ObjectSelectionList;
import java.util.stream.Collectors;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import java.util.Objects;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class CreateBuffetWorldScreen extends Screen {
    private static final List<ResourceLocation> GENERATORS;
    private final CreateWorldScreen parent;
    private final CompoundTag optionsTag;
    private BiomeList list;
    private int generatorIndex;
    private Button doneButton;
    
    public CreateBuffetWorldScreen(final CreateWorldScreen dfq, final CompoundTag id) {
        super(new TranslatableComponent("createWorld.customize.buffet.title", new Object[0]));
        this.parent = dfq;
        this.optionsTag = id;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.<Button>addButton(new Button((this.width - 200) / 2, 40, 200, 20, I18n.get("createWorld.customize.buffet.generatortype") + " " + I18n.get(Util.makeDescriptionId("generator", (ResourceLocation)CreateBuffetWorldScreen.GENERATORS.get(this.generatorIndex))), czi -> {
            ++this.generatorIndex;
            if (this.generatorIndex >= CreateBuffetWorldScreen.GENERATORS.size()) {
                this.generatorIndex = 0;
            }
            czi.setMessage(I18n.get("createWorld.customize.buffet.generatortype") + " " + I18n.get(Util.makeDescriptionId("generator", (ResourceLocation)CreateBuffetWorldScreen.GENERATORS.get(this.generatorIndex))));
            return;
        }));
        this.list = new BiomeList();
        this.children.add(this.list);
        this.doneButton = this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("gui.done"), czi -> {
            this.parent.levelTypeOptions = this.saveOptions();
            this.minecraft.setScreen(this.parent);
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(this.parent)));
        this.loadOptions();
        this.updateButtonValidity();
    }
    
    private void loadOptions() {
        if (this.optionsTag.contains("chunk_generator", 10) && this.optionsTag.getCompound("chunk_generator").contains("type", 8)) {
            final ResourceLocation qv2 = new ResourceLocation(this.optionsTag.getCompound("chunk_generator").getString("type"));
            for (int integer3 = 0; integer3 < CreateBuffetWorldScreen.GENERATORS.size(); ++integer3) {
                if (((ResourceLocation)CreateBuffetWorldScreen.GENERATORS.get(integer3)).equals(qv2)) {
                    this.generatorIndex = integer3;
                    break;
                }
            }
        }
        if (this.optionsTag.contains("biome_source", 10) && this.optionsTag.getCompound("biome_source").contains("biomes", 9)) {
            final ListTag ik2 = this.optionsTag.getCompound("biome_source").getList("biomes", 8);
            for (int integer3 = 0; integer3 < ik2.size(); ++integer3) {
                final ResourceLocation qv3 = new ResourceLocation(ik2.getString(integer3));
                this.list.setSelected((BiomeList.Entry)this.list.children().stream().filter(a -> Objects.equals(a.key, qv3)).findFirst().orElse(null));
            }
        }
        this.optionsTag.remove("chunk_generator");
        this.optionsTag.remove("biome_source");
    }
    
    private CompoundTag saveOptions() {
        final CompoundTag id2 = new CompoundTag();
        final CompoundTag id3 = new CompoundTag();
        id3.putString("type", Registry.BIOME_SOURCE_TYPE.getKey(BiomeSourceType.FIXED).toString());
        final CompoundTag id4 = new CompoundTag();
        final ListTag ik5 = new ListTag();
        ik5.add(new StringTag(this.list.getSelected().key.toString()));
        id4.put("biomes", (Tag)ik5);
        id3.put("options", (Tag)id4);
        final CompoundTag id5 = new CompoundTag();
        final CompoundTag id6 = new CompoundTag();
        id5.putString("type", ((ResourceLocation)CreateBuffetWorldScreen.GENERATORS.get(this.generatorIndex)).toString());
        id6.putString("default_block", "minecraft:stone");
        id6.putString("default_fluid", "minecraft:water");
        id5.put("options", (Tag)id6);
        id2.put("biome_source", (Tag)id3);
        id2.put("chunk_generator", (Tag)id5);
        return id2;
    }
    
    public void updateButtonValidity() {
        this.doneButton.active = (this.list.getSelected() != null);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderDirtBackground(0);
        this.list.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
        this.drawCenteredString(this.font, I18n.get("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
        this.drawCenteredString(this.font, I18n.get("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
        super.render(integer1, integer2, float3);
    }
    
    static {
        GENERATORS = (List)Registry.CHUNK_GENERATOR_TYPE.keySet().stream().filter(qv -> Registry.CHUNK_GENERATOR_TYPE.get(qv).isPublic()).collect(Collectors.toList());
    }
    
    class BiomeList extends ObjectSelectionList<Entry> {
        private BiomeList() {
            super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 80, CreateBuffetWorldScreen.this.height - 37, 16);
            Registry.BIOME.keySet().stream().sorted(Comparator.comparing(qv -> Registry.BIOME.get(qv).getName().getString())).forEach(qv -> this.addEntry(new Entry(qv)));
        }
        
        @Override
        protected boolean isFocused() {
            return CreateBuffetWorldScreen.this.getFocused() == this;
        }
        
        @Override
        public void setSelected(@Nullable final Entry a) {
            super.setSelected(a);
            if (a != null) {
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new Object[] { Registry.BIOME.get(a.key).getName().getString() }).getString());
            }
        }
        
        @Override
        protected void moveSelection(final int integer) {
            super.moveSelection(integer);
            CreateBuffetWorldScreen.this.updateButtonValidity();
        }
        
        class Entry extends ObjectSelectionList.Entry<Entry> {
            private final ResourceLocation key;
            
            public Entry(final ResourceLocation qv) {
                this.key = qv;
            }
            
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                BiomeList.this.drawString(CreateBuffetWorldScreen.this.font, Registry.BIOME.get(this.key).getName().getString(), integer3 + 5, integer2 + 2, 16777215);
            }
            
            public boolean mouseClicked(final double double1, final double double2, final int integer) {
                if (integer == 0) {
                    BiomeList.this.setSelected(this);
                    CreateBuffetWorldScreen.this.updateButtonValidity();
                    return true;
                }
                return false;
            }
        }
    }
}
