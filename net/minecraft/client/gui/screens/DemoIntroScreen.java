package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class DemoIntroScreen extends Screen {
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION;
    
    public DemoIntroScreen() {
        super(new TranslatableComponent("demo.help.title", new Object[0]));
    }
    
    @Override
    protected void init() {
        final int integer2 = -16;
        this.<Button>addButton(new Button(this.width / 2 - 116, this.height / 2 + 62 - 16, 114, 20, I18n.get("demo.help.buy"), czi -> {
            czi.active = false;
            Util.getPlatform().openUri("http://www.minecraft.net/store?source=demo");
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 2, this.height / 2 + 62 - 16, 114, 20, I18n.get("demo.help.later"), czi -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }));
    }
    
    @Override
    public void renderBackground() {
        super.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(DemoIntroScreen.DEMO_BACKGROUND_LOCATION);
        final int integer2 = (this.width - 248) / 2;
        final int integer3 = (this.height - 166) / 2;
        this.blit(integer2, integer3, 0, 0, 248, 166);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final int integer3 = (this.width - 248) / 2 + 10;
        int integer4 = (this.height - 166) / 2 + 8;
        this.font.draw(this.title.getColoredString(), (float)integer3, (float)integer4, 2039583);
        integer4 += 12;
        final Options cyg7 = this.minecraft.options;
        this.font.draw(I18n.get("demo.help.movementShort", cyg7.keyUp.getTranslatedKeyMessage(), cyg7.keyLeft.getTranslatedKeyMessage(), cyg7.keyDown.getTranslatedKeyMessage(), cyg7.keyRight.getTranslatedKeyMessage()), (float)integer3, (float)integer4, 5197647);
        this.font.draw(I18n.get("demo.help.movementMouse"), (float)integer3, (float)(integer4 + 12), 5197647);
        this.font.draw(I18n.get("demo.help.jump", cyg7.keyJump.getTranslatedKeyMessage()), (float)integer3, (float)(integer4 + 24), 5197647);
        this.font.draw(I18n.get("demo.help.inventory", cyg7.keyInventory.getTranslatedKeyMessage()), (float)integer3, (float)(integer4 + 36), 5197647);
        this.font.drawWordWrap(I18n.get("demo.help.fullWrapped"), integer3, integer4 + 68, 218, 2039583);
        super.render(integer1, integer2, float3);
    }
    
    static {
        DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
    }
}
