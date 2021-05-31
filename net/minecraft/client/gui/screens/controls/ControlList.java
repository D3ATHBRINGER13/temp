package net.minecraft.client.gui.screens.controls;

import net.minecraft.client.gui.components.AbstractSelectionList;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import java.util.Collections;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class ControlList extends ContainerObjectSelectionList<Entry> {
    private final ControlsScreen controlsScreen;
    private int maxNameWidth;
    
    public ControlList(final ControlsScreen ddc, final Minecraft cyc) {
        super(cyc, ddc.width + 45, ddc.height, 43, ddc.height - 32, 20);
        this.controlsScreen = ddc;
        final KeyMapping[] arr4 = (KeyMapping[])ArrayUtils.clone((Object[])cyc.options.keyMappings);
        Arrays.sort((Object[])arr4);
        String string5 = null;
        for (final KeyMapping cxz9 : arr4) {
            final String string6 = cxz9.getCategory();
            if (!string6.equals(string5)) {
                string5 = string6;
                ((AbstractSelectionList<CategoryEntry>)this).addEntry(new CategoryEntry(string6));
            }
            final int integer11 = cyc.font.width(I18n.get(cxz9.getName()));
            if (integer11 > this.maxNameWidth) {
                this.maxNameWidth = integer11;
            }
            ((AbstractSelectionList<KeyEntry>)this).addEntry(new KeyEntry(cxz9));
        }
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }
    
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    }
    
    public class CategoryEntry extends Entry {
        private final String name;
        private final int width;
        
        public CategoryEntry(final String string) {
            this.name = I18n.get(string);
            this.width = ControlList.this.minecraft.font.width(this.name);
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            final Font font = ControlList.this.minecraft.font;
            final String name = this.name;
            final float float10 = (float)(ControlList.this.minecraft.screen.width / 2 - this.width / 2);
            final int n = integer2 + integer5;
            ControlList.this.minecraft.font.getClass();
            font.draw(name, float10, (float)(n - 9 - 1), 16777215);
        }
        
        @Override
        public boolean changeFocus(final boolean boolean1) {
            return false;
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }
    
    public class KeyEntry extends Entry {
        private final KeyMapping key;
        private final String name;
        private final Button changeButton;
        private final Button resetButton;
        
        private KeyEntry(final KeyMapping cxz) {
            this.key = cxz;
            this.name = I18n.get(cxz.getName());
            this.changeButton = new Button(0, 0, 75, 20, this.name, czi -> ControlList.this.controlsScreen.selectedKey = cxz) {
                @Override
                protected String getNarrationMessage() {
                    if (cxz.isUnbound()) {
                        return I18n.get("narrator.controls.unbound", KeyEntry.this.name);
                    }
                    return I18n.get("narrator.controls.bound", KeyEntry.this.name, super.getNarrationMessage());
                }
            };
            this.resetButton = new Button(0, 0, 50, 20, I18n.get("controls.reset"), czi -> {
                ControlList.this.minecraft.options.setKey(cxz, cxz.getDefaultKey());
                KeyMapping.resetMapping();
            }) {
                @Override
                protected String getNarrationMessage() {
                    return I18n.get("narrator.controls.reset", KeyEntry.this.name);
                }
            };
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            final boolean boolean9 = ControlList.this.controlsScreen.selectedKey == this.key;
            final Font font = ControlList.this.minecraft.font;
            final String name = this.name;
            final float float10 = (float)(integer3 + 90 - ControlList.this.maxNameWidth);
            final int n = integer2 + integer5 / 2;
            ControlList.this.minecraft.font.getClass();
            font.draw(name, float10, (float)(n - 9 / 2), 16777215);
            this.resetButton.x = integer3 + 190;
            this.resetButton.y = integer2;
            this.resetButton.active = !this.key.isDefault();
            this.resetButton.render(integer6, integer7, float9);
            this.changeButton.x = integer3 + 105;
            this.changeButton.y = integer2;
            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            boolean boolean10 = false;
            if (!this.key.isUnbound()) {
                for (final KeyMapping cxz16 : ControlList.this.minecraft.options.keyMappings) {
                    if (cxz16 != this.key && this.key.same(cxz16)) {
                        boolean10 = true;
                        break;
                    }
                }
            }
            if (boolean9) {
                this.changeButton.setMessage(new StringBuilder().append(ChatFormatting.WHITE).append("> ").append(ChatFormatting.YELLOW).append(this.changeButton.getMessage()).append(ChatFormatting.WHITE).append(" <").toString());
            }
            else if (boolean10) {
                this.changeButton.setMessage(ChatFormatting.RED + this.changeButton.getMessage());
            }
            this.changeButton.render(integer6, integer7, float9);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.changeButton, this.resetButton);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            return this.changeButton.mouseClicked(double1, double2, integer) || this.resetButton.mouseClicked(double1, double2, integer);
        }
        
        @Override
        public boolean mouseReleased(final double double1, final double double2, final int integer) {
            return this.changeButton.mouseReleased(double1, double2, integer) || this.resetButton.mouseReleased(double1, double2, integer);
        }
    }
}
