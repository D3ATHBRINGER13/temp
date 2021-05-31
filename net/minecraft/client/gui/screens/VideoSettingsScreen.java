package net.minecraft.client.gui.screens;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.FullscreenResolutionProgressOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.Options;

public class VideoSettingsScreen extends Screen {
    private final Screen lastScreen;
    private final Options options;
    private OptionsList list;
    private static final Option[] OPTIONS;
    private int oldMipmaps;
    
    public VideoSettingsScreen(final Screen dcl, final Options cyg) {
        super(new TranslatableComponent("options.videoTitle", new Object[0]));
        this.lastScreen = dcl;
        this.options = cyg;
    }
    
    @Override
    protected void init() {
        this.oldMipmaps = this.options.mipmapLevels;
        (this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25)).addBig(new FullscreenResolutionProgressOption(this.minecraft.window));
        this.list.addSmall(VideoSettingsScreen.OPTIONS);
        this.children.add(this.list);
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.get("gui.done"), czi -> {
            this.minecraft.options.save();
            this.minecraft.window.changeFullscreenVideoMode();
            this.minecraft.setScreen(this.lastScreen);
        }));
    }
    
    @Override
    public void removed() {
        if (this.options.mipmapLevels != this.oldMipmaps) {
            this.minecraft.getTextureAtlas().setMaxMipLevel(this.options.mipmapLevels);
            this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
            this.minecraft.getTextureAtlas().setFilter(false, this.options.mipmapLevels > 0);
            this.minecraft.delayTextureReload();
        }
        this.minecraft.options.save();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        final int integer2 = this.options.guiScale;
        if (super.mouseClicked(double1, double2, integer)) {
            if (this.options.guiScale != integer2) {
                this.minecraft.resizeDisplay();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        final int integer2 = this.options.guiScale;
        if (super.mouseReleased(double1, double2, integer)) {
            return true;
        }
        if (this.list.mouseReleased(double1, double2, integer)) {
            if (this.options.guiScale != integer2) {
                this.minecraft.resizeDisplay();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.list.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 5, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    static {
        OPTIONS = new Option[] { Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AMBIENT_OCCLUSION, Option.FRAMERATE_LIMIT, Option.ENABLE_VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.RENDER_CLOUDS, Option.USE_FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.BIOME_BLEND_RADIUS };
    }
}
