package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.chat.NarratorChatListener;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.client.resources.language.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.Option;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.Options;

public class LanguageSelectScreen extends Screen {
    protected final Screen lastScreen;
    private LanguageSelectionList packSelectionList;
    private final Options options;
    private final LanguageManager languageManager;
    private OptionButton forceUnicodeButton;
    private Button doneButton;
    
    public LanguageSelectScreen(final Screen dcl, final Options cyg, final LanguageManager dxz) {
        super(new TranslatableComponent("options.language", new Object[0]));
        this.lastScreen = dcl;
        this.options = cyg;
        this.languageManager = dxz;
    }
    
    @Override
    protected void init() {
        this.packSelectionList = new LanguageSelectionList(this.minecraft);
        this.children.add(this.packSelectionList);
        this.forceUnicodeButton = this.<OptionButton>addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, Option.FORCE_UNICODE_FONT, Option.FORCE_UNICODE_FONT.getMessage(this.options), czi -> {
            Option.FORCE_UNICODE_FONT.toggle(this.options);
            this.options.save();
            czi.setMessage(Option.FORCE_UNICODE_FONT.getMessage(this.options));
            this.minecraft.resizeDisplay();
            return;
        }));
        final LanguageSelectionList.Entry a3;
        this.doneButton = this.<Button>addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.get("gui.done"), czi -> {
            a3 = this.packSelectionList.getSelected();
            if (a3 != null && !a3.language.getCode().equals(this.languageManager.getSelected().getCode())) {
                this.languageManager.setSelected(a3.language);
                this.options.languageCode = a3.language.getCode();
                this.minecraft.reloadResourcePacks();
                this.font.setBidirectional(this.languageManager.isBidirectional());
                this.doneButton.setMessage(I18n.get("gui.done"));
                this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getMessage(this.options));
                this.options.save();
            }
            this.minecraft.setScreen(this.lastScreen);
            return;
        }));
        super.init();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.packSelectionList.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.font, "(" + I18n.get("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.render(integer1, integer2, float3);
    }
    
    class LanguageSelectionList extends ObjectSelectionList<Entry> {
        public LanguageSelectionList(final Minecraft cyc) {
            super(cyc, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height, 32, LanguageSelectScreen.this.height - 65 + 4, 18);
            for (final Language dxy5 : LanguageSelectScreen.this.languageManager.getLanguages()) {
                final Entry a6 = new Entry(dxy5);
                this.addEntry(a6);
                if (LanguageSelectScreen.this.languageManager.getSelected().getCode().equals(dxy5.getCode())) {
                    this.setSelected(a6);
                }
            }
            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }
        
        @Override
        protected int getScrollbarPosition() {
            return super.getScrollbarPosition() + 20;
        }
        
        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }
        
        @Override
        public void setSelected(@Nullable final Entry a) {
            super.setSelected(a);
            if (a != null) {
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new Object[] { a.language }).getString());
            }
        }
        
        @Override
        protected void renderBackground() {
            LanguageSelectScreen.this.renderBackground();
        }
        
        @Override
        protected boolean isFocused() {
            return LanguageSelectScreen.this.getFocused() == this;
        }
        
        public class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Language language;
            
            public Entry(final Language dxy) {
                this.language = dxy;
            }
            
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                LanguageSelectScreen.this.font.setBidirectional(true);
                LanguageSelectionList.this.drawCenteredString(LanguageSelectScreen.this.font, this.language.toString(), LanguageSelectionList.this.width / 2, integer2 + 1, 16777215);
                LanguageSelectScreen.this.font.setBidirectional(LanguageSelectScreen.this.languageManager.getSelected().isBidirectional());
            }
            
            public boolean mouseClicked(final double double1, final double double2, final int integer) {
                if (integer == 0) {
                    this.select();
                    return true;
                }
                return false;
            }
            
            private void select() {
                LanguageSelectionList.this.setSelected(this);
            }
        }
    }
}
