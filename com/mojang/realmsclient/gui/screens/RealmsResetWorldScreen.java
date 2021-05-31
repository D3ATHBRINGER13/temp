package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.realms.RealmsConfirmResultListener;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.realms.RealmsGuiEventListener;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import net.minecraft.realms.RealmsLabel;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.Logger;
import com.mojang.realmsclient.dto.WorldTemplate;

public class RealmsResetWorldScreen extends RealmsScreenWithCallback<WorldTemplate> {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private final RealmsServer serverData;
    private final RealmsScreen returnScreen;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private String title;
    private String subtitle;
    private String buttonTitle;
    private int subtitleColor;
    private final int BUTTON_CANCEL_ID = 0;
    private final int BUTTON_FRAME_START = 100;
    private WorldTemplatePaginatedList templates;
    private WorldTemplatePaginatedList adventuremaps;
    private WorldTemplatePaginatedList experiences;
    private WorldTemplatePaginatedList inspirations;
    public int slot;
    private ResetType typeToReset;
    private ResetWorldInfo worldInfoToReset;
    private WorldTemplate worldTemplateToReset;
    private String resetTitle;
    private int confirmationId;
    
    public RealmsResetWorldScreen(final RealmsScreen realmsScreen1, final RealmsServer realmsServer, final RealmsScreen realmsScreen3) {
        this.title = RealmsScreen.getLocalizedString("mco.reset.world.title");
        this.subtitle = RealmsScreen.getLocalizedString("mco.reset.world.warning");
        this.buttonTitle = RealmsScreen.getLocalizedString("gui.cancel");
        this.subtitleColor = 16711680;
        this.templates = null;
        this.adventuremaps = null;
        this.experiences = null;
        this.inspirations = null;
        this.slot = -1;
        this.typeToReset = ResetType.NONE;
        this.worldInfoToReset = null;
        this.worldTemplateToReset = null;
        this.resetTitle = null;
        this.confirmationId = -1;
        this.lastScreen = realmsScreen1;
        this.serverData = realmsServer;
        this.returnScreen = realmsScreen3;
    }
    
    public RealmsResetWorldScreen(final RealmsScreen realmsScreen1, final RealmsServer realmsServer, final RealmsScreen realmsScreen3, final String string4, final String string5, final int integer, final String string7) {
        this(realmsScreen1, realmsServer, realmsScreen3);
        this.title = string4;
        this.subtitle = string5;
        this.subtitleColor = integer;
        this.buttonTitle = string7;
    }
    
    public void setConfirmationId(final int integer) {
        this.confirmationId = integer;
    }
    
    public void setSlot(final int integer) {
        this.slot = integer;
    }
    
    public void setResetTitle(final String string) {
        this.resetTitle = string;
    }
    
    @Override
    public void init() {
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 40, RealmsConstants.row(14) - 10, 80, 20, this.buttonTitle) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsResetWorldScreen.this.lastScreen);
            }
        });
        new Thread("Realms-reset-world-fetcher") {
            public void run() {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                try {
                    final WorldTemplatePaginatedList worldTemplatePaginatedList3 = cvm2.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    final WorldTemplatePaginatedList worldTemplatePaginatedList4 = cvm2.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    final WorldTemplatePaginatedList worldTemplatePaginatedList5 = cvm2.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    final WorldTemplatePaginatedList worldTemplatePaginatedList6 = cvm2.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    Realms.execute(() -> {
                        RealmsResetWorldScreen.this.templates = worldTemplatePaginatedList3;
                        RealmsResetWorldScreen.this.adventuremaps = worldTemplatePaginatedList4;
                        RealmsResetWorldScreen.this.experiences = worldTemplatePaginatedList5;
                        RealmsResetWorldScreen.this.inspirations = worldTemplatePaginatedList6;
                    });
                }
                catch (RealmsServiceException cvu3) {
                    RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)cvu3);
                }
            }
        }.start();
        this.addWidget(this.titleLabel = new RealmsLabel(this.title, this.width() / 2, 7, 16777215));
        this.addWidget(this.subtitleLabel = new RealmsLabel(this.subtitle, this.width() / 2, 22, this.subtitleColor));
        this.buttonsAdd(new FrameButton(this.frame(1), RealmsConstants.row(0) + 10, RealmsScreen.getLocalizedString("mco.reset.world.generate"), -1L, "realms:textures/gui/realms/new_world.png", ResetType.GENERATE) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsResetNormalWorldScreen(RealmsResetWorldScreen.this, RealmsResetWorldScreen.this.title));
            }
        });
        this.buttonsAdd(new FrameButton(this.frame(2), RealmsConstants.row(0) + 10, RealmsScreen.getLocalizedString("mco.reset.world.upload"), -1L, "realms:textures/gui/realms/upload.png", ResetType.UPLOAD) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsSelectFileToUploadScreen(RealmsResetWorldScreen.this.serverData.id, (RealmsResetWorldScreen.this.slot != -1) ? RealmsResetWorldScreen.this.slot : RealmsResetWorldScreen.this.serverData.activeSlot, RealmsResetWorldScreen.this));
            }
        });
        this.buttonsAdd(new FrameButton(this.frame(3), RealmsConstants.row(0) + 10, RealmsScreen.getLocalizedString("mco.reset.world.template"), -1L, "realms:textures/gui/realms/survival_spawn.png", ResetType.SURVIVAL_SPAWN) {
            @Override
            public void onPress() {
                final RealmsSelectWorldTemplateScreen cwy2 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.NORMAL, RealmsResetWorldScreen.this.templates);
                cwy2.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.template"));
                Realms.setScreen(cwy2);
            }
        });
        this.buttonsAdd(new FrameButton(this.frame(1), RealmsConstants.row(6) + 20, RealmsScreen.getLocalizedString("mco.reset.world.adventure"), -1L, "realms:textures/gui/realms/adventure.png", ResetType.ADVENTURE) {
            @Override
            public void onPress() {
                final RealmsSelectWorldTemplateScreen cwy2 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.ADVENTUREMAP, RealmsResetWorldScreen.this.adventuremaps);
                cwy2.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.adventure"));
                Realms.setScreen(cwy2);
            }
        });
        this.buttonsAdd(new FrameButton(this.frame(2), RealmsConstants.row(6) + 20, RealmsScreen.getLocalizedString("mco.reset.world.experience"), -1L, "realms:textures/gui/realms/experience.png", ResetType.EXPERIENCE) {
            @Override
            public void onPress() {
                final RealmsSelectWorldTemplateScreen cwy2 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.EXPERIENCE, RealmsResetWorldScreen.this.experiences);
                cwy2.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.experience"));
                Realms.setScreen(cwy2);
            }
        });
        this.buttonsAdd(new FrameButton(this.frame(3), RealmsConstants.row(6) + 20, RealmsScreen.getLocalizedString("mco.reset.world.inspiration"), -1L, "realms:textures/gui/realms/inspiration.png", ResetType.INSPIRATION) {
            @Override
            public void onPress() {
                final RealmsSelectWorldTemplateScreen cwy2 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.INSPIRATION, RealmsResetWorldScreen.this.inspirations);
                cwy2.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.inspiration"));
                Realms.setScreen(cwy2);
            }
        });
        this.narrateLabels();
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return super.mouseClicked(double1, double2, integer);
    }
    
    private int frame(final int integer) {
        return this.width() / 2 - 130 + (integer - 1) * 100;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.titleLabel.render(this);
        this.subtitleLabel.render(this);
        super.render(integer1, integer2, float3);
    }
    
    private void drawFrame(final int integer1, final int integer2, final String string3, final long long4, final String string5, final ResetType b, final boolean boolean7, final boolean boolean8) {
        if (long4 == -1L) {
            RealmsScreen.bind(string5);
        }
        else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(long4), string5);
        }
        if (boolean7) {
            GlStateManager.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        else {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        RealmsScreen.blit(integer1 + 2, integer2 + 14, 0.0f, 0.0f, 56, 56, 56, 56);
        RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
        if (boolean7) {
            GlStateManager.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        else {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        RealmsScreen.blit(integer1, integer2 + 12, 0.0f, 0.0f, 60, 60, 60, 60);
        this.drawCenteredString(string3, integer1 + 30, integer2, boolean7 ? 10526880 : 16777215);
    }
    
    @Override
    void callback(final WorldTemplate worldTemplate) {
        if (worldTemplate != null) {
            if (this.slot == -1) {
                this.resetWorldWithTemplate(worldTemplate);
            }
            else {
                switch (worldTemplate.type) {
                    case WORLD_TEMPLATE: {
                        this.typeToReset = ResetType.SURVIVAL_SPAWN;
                        break;
                    }
                    case ADVENTUREMAP: {
                        this.typeToReset = ResetType.ADVENTURE;
                        break;
                    }
                    case EXPERIENCE: {
                        this.typeToReset = ResetType.EXPERIENCE;
                        break;
                    }
                    case INSPIRATION: {
                        this.typeToReset = ResetType.INSPIRATION;
                        break;
                    }
                }
                this.worldTemplateToReset = worldTemplate;
                this.switchSlot();
            }
        }
    }
    
    private void switchSlot() {
        this.switchSlot(this);
    }
    
    public void switchSlot(final RealmsScreen realmsScreen) {
        final RealmsTasks.SwitchSlotTask i3 = new RealmsTasks.SwitchSlotTask(this.serverData.id, this.slot, realmsScreen, 100);
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, i3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (integer == 100 && boolean1) {
            switch (this.typeToReset) {
                case ADVENTURE:
                case SURVIVAL_SPAWN:
                case EXPERIENCE:
                case INSPIRATION: {
                    if (this.worldTemplateToReset != null) {
                        this.resetWorldWithTemplate(this.worldTemplateToReset);
                        break;
                    }
                    break;
                }
                case GENERATE: {
                    if (this.worldInfoToReset != null) {
                        this.triggerResetWorld(this.worldInfoToReset);
                        break;
                    }
                    break;
                }
                default: {}
            }
            return;
        }
        if (boolean1) {
            Realms.setScreen(this.returnScreen);
            if (this.confirmationId != -1) {
                this.returnScreen.confirmResult(true, this.confirmationId);
            }
        }
    }
    
    public void resetWorldWithTemplate(final WorldTemplate worldTemplate) {
        final RealmsTasks.ResettingWorldTask f3 = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.returnScreen, worldTemplate);
        if (this.resetTitle != null) {
            f3.setResetTitle(this.resetTitle);
        }
        if (this.confirmationId != -1) {
            f3.setConfirmationId(this.confirmationId);
        }
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, f3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    public void resetWorld(final ResetWorldInfo c) {
        if (this.slot == -1) {
            this.triggerResetWorld(c);
        }
        else {
            this.typeToReset = ResetType.GENERATE;
            this.worldInfoToReset = c;
            this.switchSlot();
        }
    }
    
    private void triggerResetWorld(final ResetWorldInfo c) {
        final RealmsTasks.ResettingWorldTask f3 = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.returnScreen, c.seed, c.levelType, c.generateStructures);
        if (this.resetTitle != null) {
            f3.setResetTitle(this.resetTitle);
        }
        if (this.confirmationId != -1) {
            f3.setConfirmationId(this.confirmationId);
        }
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, f3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    enum ResetType {
        NONE, 
        GENERATE, 
        UPLOAD, 
        ADVENTURE, 
        SURVIVAL_SPAWN, 
        EXPERIENCE, 
        INSPIRATION;
    }
    
    public static class ResetWorldInfo {
        String seed;
        int levelType;
        boolean generateStructures;
        
        public ResetWorldInfo(final String string, final int integer, final boolean boolean3) {
            this.seed = string;
            this.levelType = integer;
            this.generateStructures = boolean3;
        }
    }
    
    abstract class FrameButton extends RealmsButton {
        private final long imageId;
        private final String image;
        private final ResetType resetType;
        
        public FrameButton(final int integer2, final int integer3, final String string4, final long long5, final String string6, final ResetType b) {
            super(100 + b.ordinal(), integer2, integer3, 60, 72, string4);
            this.imageId = long5;
            this.image = string6;
            this.resetType = b;
        }
        
        @Override
        public void tick() {
            super.tick();
        }
        
        @Override
        public void render(final int integer1, final int integer2, final float float3) {
            super.render(integer1, integer2, float3);
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            RealmsResetWorldScreen.this.drawFrame(this.x(), this.y(), this.getProxy().getMessage(), this.imageId, this.image, this.resetType, this.getProxy().isHovered(), this.getProxy().isMouseOver(integer1, integer2));
        }
    }
}
