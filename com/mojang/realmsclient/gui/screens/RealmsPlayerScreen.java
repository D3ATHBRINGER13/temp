package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.apache.logging.log4j.LogManager;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.Tezzelator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.client.RealmsClient;
import java.util.Iterator;
import net.minecraft.realms.RealmsGuiEventListener;
import com.mojang.realmsclient.dto.PlayerInfo;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.RealmsServer;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsPlayerScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private String toolTip;
    private final RealmsConfigureWorldScreen lastScreen;
    private final RealmsServer serverData;
    private InvitedObjectSelectionList invitedObjectSelectionList;
    private int column1_x;
    private int column_width;
    private int column2_x;
    private RealmsButton removeButton;
    private RealmsButton opdeopButton;
    private int selectedInvitedIndex;
    private String selectedInvited;
    private int player;
    private boolean stateChanged;
    private RealmsLabel titleLabel;
    
    public RealmsPlayerScreen(final RealmsConfigureWorldScreen cwg, final RealmsServer realmsServer) {
        this.selectedInvitedIndex = -1;
        this.player = -1;
        this.lastScreen = cwg;
        this.serverData = realmsServer;
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public void init() {
        this.column1_x = this.width() / 2 - 160;
        this.column_width = 150;
        this.column2_x = this.width() / 2 + 12;
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(new RealmsButton(1, this.column2_x, RealmsConstants.row(1), this.column_width + 10, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.invite")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsInviteScreen(RealmsPlayerScreen.this.lastScreen, RealmsPlayerScreen.this, RealmsPlayerScreen.this.serverData));
            }
        });
        this.buttonsAdd(this.removeButton = new RealmsButton(4, this.column2_x, RealmsConstants.row(7), this.column_width + 10, 20, RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip")) {
            @Override
            public void onPress() {
                RealmsPlayerScreen.this.uninvite(RealmsPlayerScreen.this.player);
            }
        });
        this.buttonsAdd(this.opdeopButton = new RealmsButton(5, this.column2_x, RealmsConstants.row(9), this.column_width + 10, 20, RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip")) {
            @Override
            public void onPress() {
                if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(RealmsPlayerScreen.this.player)).isOperator()) {
                    RealmsPlayerScreen.this.deop(RealmsPlayerScreen.this.player);
                }
                else {
                    RealmsPlayerScreen.this.op(RealmsPlayerScreen.this.player);
                }
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.column2_x + this.column_width / 2 + 2, RealmsConstants.row(12), this.column_width / 2 + 10 - 2, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                RealmsPlayerScreen.this.backButtonClicked();
            }
        });
        (this.invitedObjectSelectionList = new InvitedObjectSelectionList()).setLeftPos(this.column1_x);
        this.addWidget(this.invitedObjectSelectionList);
        for (final PlayerInfo playerInfo3 : this.serverData.players) {
            this.invitedObjectSelectionList.addEntry(playerInfo3);
        }
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.configure.world.players.title"), this.width() / 2, 17, 16777215));
        this.narrateLabels();
        this.updateButtonStates();
    }
    
    private void updateButtonStates() {
        this.removeButton.setVisible(this.shouldRemoveAndOpdeopButtonBeVisible(this.player));
        this.opdeopButton.setVisible(this.shouldRemoveAndOpdeopButtonBeVisible(this.player));
    }
    
    private boolean shouldRemoveAndOpdeopButtonBeVisible(final int integer) {
        return integer != -1;
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void backButtonClicked() {
        if (this.stateChanged) {
            Realms.setScreen(this.lastScreen.getNewScreen());
        }
        else {
            Realms.setScreen(this.lastScreen);
        }
    }
    
    private void op(final int integer) {
        this.updateButtonStates();
        final RealmsClient cvm3 = RealmsClient.createRealmsClient();
        final String string4 = ((PlayerInfo)this.serverData.players.get(integer)).getUuid();
        try {
            this.updateOps(cvm3.op(this.serverData.id, string4));
        }
        catch (RealmsServiceException cvu5) {
            RealmsPlayerScreen.LOGGER.error("Couldn't op the user");
        }
    }
    
    private void deop(final int integer) {
        this.updateButtonStates();
        final RealmsClient cvm3 = RealmsClient.createRealmsClient();
        final String string4 = ((PlayerInfo)this.serverData.players.get(integer)).getUuid();
        try {
            this.updateOps(cvm3.deop(this.serverData.id, string4));
        }
        catch (RealmsServiceException cvu5) {
            RealmsPlayerScreen.LOGGER.error("Couldn't deop the user");
        }
    }
    
    private void updateOps(final Ops ops) {
        for (final PlayerInfo playerInfo4 : this.serverData.players) {
            playerInfo4.setOperator(ops.ops.contains(playerInfo4.getName()));
        }
    }
    
    private void uninvite(final int integer) {
        this.updateButtonStates();
        if (integer >= 0 && integer < this.serverData.players.size()) {
            final PlayerInfo playerInfo3 = (PlayerInfo)this.serverData.players.get(integer);
            this.selectedInvited = playerInfo3.getUuid();
            this.selectedInvitedIndex = integer;
            final RealmsConfirmScreen cwh4 = new RealmsConfirmScreen((RealmsScreen)this, "Question", RealmsScreen.getLocalizedString("mco.configure.world.uninvite.question") + " '" + playerInfo3.getName() + "' ?", 2);
            Realms.setScreen(cwh4);
        }
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (integer == 2) {
            if (boolean1) {
                final RealmsClient cvm4 = RealmsClient.createRealmsClient();
                try {
                    cvm4.uninvite(this.serverData.id, this.selectedInvited);
                }
                catch (RealmsServiceException cvu5) {
                    RealmsPlayerScreen.LOGGER.error("Couldn't uninvite user");
                }
                this.deleteFromInvitedList(this.selectedInvitedIndex);
                this.player = -1;
                this.updateButtonStates();
            }
            this.stateChanged = true;
            Realms.setScreen(this);
        }
    }
    
    private void deleteFromInvitedList(final int integer) {
        this.serverData.players.remove(integer);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.renderBackground();
        if (this.invitedObjectSelectionList != null) {
            this.invitedObjectSelectionList.render(integer1, integer2, float3);
        }
        final int integer3 = RealmsConstants.row(12) + 20;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tezzelator tezzelator6 = Tezzelator.instance;
        RealmsScreen.bind("textures/gui/options_background.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float4 = 32.0f;
        tezzelator6.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
        tezzelator6.vertex(0.0, this.height(), 0.0).tex(0.0, (this.height() - integer3) / 32.0f + 0.0f).color(64, 64, 64, 255).endVertex();
        tezzelator6.vertex(this.width(), this.height(), 0.0).tex(this.width() / 32.0f, (this.height() - integer3) / 32.0f + 0.0f).color(64, 64, 64, 255).endVertex();
        tezzelator6.vertex(this.width(), integer3, 0.0).tex(this.width() / 32.0f, 0.0).color(64, 64, 64, 255).endVertex();
        tezzelator6.vertex(0.0, integer3, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
        tezzelator6.end();
        this.titleLabel.render(this);
        if (this.serverData != null && this.serverData.players != null) {
            this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.invited") + " (" + this.serverData.players.size() + ")", this.column1_x, RealmsConstants.row(0), 10526880);
        }
        else {
            this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.invited"), this.column1_x, RealmsConstants.row(0), 10526880);
        }
        super.render(integer1, integer2, float3);
        if (this.serverData == null) {
            return;
        }
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, integer1, integer2);
        }
    }
    
    protected void renderMousehoverTooltip(final String string, final int integer2, final int integer3) {
        if (string == null) {
            return;
        }
        final int integer4 = integer2 + 12;
        final int integer5 = integer3 - 12;
        final int integer6 = this.fontWidth(string);
        this.fillGradient(integer4 - 3, integer5 - 3, integer4 + integer6 + 3, integer5 + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, integer4, integer5, 16777215);
    }
    
    private void drawRemoveIcon(final int integer1, final int integer2, final int integer3, final int integer4) {
        final boolean boolean6 = integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 9 && integer4 < RealmsConstants.row(12) + 20 && integer4 > RealmsConstants.row(1);
        RealmsScreen.bind("realms:textures/gui/realms/cross_player_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, boolean6 ? 7.0f : 0.0f, 8, 7, 8, 14);
        GlStateManager.popMatrix();
        if (boolean6) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip");
        }
    }
    
    private void drawOpped(final int integer1, final int integer2, final int integer3, final int integer4) {
        final boolean boolean6 = integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 9 && integer4 < RealmsConstants.row(12) + 20 && integer4 > RealmsConstants.row(1);
        RealmsScreen.bind("realms:textures/gui/realms/op_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, boolean6 ? 8.0f : 0.0f, 8, 8, 8, 16);
        GlStateManager.popMatrix();
        if (boolean6) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip");
        }
    }
    
    private void drawNormal(final int integer1, final int integer2, final int integer3, final int integer4) {
        final boolean boolean6 = integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 9 && integer4 < RealmsConstants.row(12) + 20 && integer4 > RealmsConstants.row(1);
        RealmsScreen.bind("realms:textures/gui/realms/user_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, boolean6 ? 8.0f : 0.0f, 8, 8, 8, 16);
        GlStateManager.popMatrix();
        if (boolean6) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip");
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class InvitedObjectSelectionList extends RealmsObjectSelectionList {
        public InvitedObjectSelectionList() {
            super(RealmsPlayerScreen.this.column_width + 10, RealmsConstants.row(12) + 20, RealmsConstants.row(1), RealmsConstants.row(12) + 20, 13);
        }
        
        public void addEntry(final PlayerInfo playerInfo) {
            this.addEntry(new InvitedObjectSelectionListEntry(playerInfo));
        }
        
        @Override
        public int getRowWidth() {
            return (int)(this.width() * 1.0);
        }
        
        @Override
        public boolean isFocused() {
            return RealmsPlayerScreen.this.isFocused(this);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            if (integer == 0 && double1 < this.getScrollbarPosition() && double2 >= this.y0() && double2 <= this.y1()) {
                final int integer2 = RealmsPlayerScreen.this.column1_x;
                final int integer3 = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
                final int integer4 = (int)Math.floor(double2 - this.y0()) - this.headerHeight() + this.getScroll() - 4;
                final int integer5 = integer4 / this.itemHeight();
                if (double1 >= integer2 && double1 <= integer3 && integer5 >= 0 && integer4 >= 0 && integer5 < this.getItemCount()) {
                    this.selectItem(integer5);
                    this.itemClicked(integer4, integer5, double1, double2, this.width());
                }
                return true;
            }
            return super.mouseClicked(double1, double2, integer);
        }
        
        @Override
        public void itemClicked(final int integer1, final int integer2, final double double3, final double double4, final int integer5) {
            if (integer2 < 0 || integer2 > RealmsPlayerScreen.this.serverData.players.size() || RealmsPlayerScreen.this.toolTip == null) {
                return;
            }
            if (RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip")) || RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip"))) {
                if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(integer2)).isOperator()) {
                    RealmsPlayerScreen.this.deop(integer2);
                }
                else {
                    RealmsPlayerScreen.this.op(integer2);
                }
            }
            else if (RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
                RealmsPlayerScreen.this.uninvite(integer2);
            }
        }
        
        @Override
        public void selectItem(final int integer) {
            this.setSelected(integer);
            if (integer != -1) {
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(integer)).getName()));
            }
            this.selectInviteListItem(integer);
        }
        
        public void selectInviteListItem(final int integer) {
            RealmsPlayerScreen.this.player = integer;
            RealmsPlayerScreen.this.updateButtonStates();
        }
        
        @Override
        public void renderBackground() {
            RealmsPlayerScreen.this.renderBackground();
        }
        
        @Override
        public int getScrollbarPosition() {
            return RealmsPlayerScreen.this.column1_x + this.width() - 5;
        }
        
        @Override
        public int getItemCount() {
            return (RealmsPlayerScreen.this.serverData == null) ? 1 : RealmsPlayerScreen.this.serverData.players.size();
        }
        
        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 13;
        }
    }
    
    class InvitedObjectSelectionListEntry extends RealmListEntry {
        final PlayerInfo mPlayerInfo;
        
        public InvitedObjectSelectionListEntry(final PlayerInfo playerInfo) {
            this.mPlayerInfo = playerInfo;
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderInvitedItem(this.mPlayerInfo, integer3, integer2, integer6, integer7);
        }
        
        private void renderInvitedItem(final PlayerInfo playerInfo, final int integer2, final int integer3, final int integer4, final int integer5) {
            int integer6;
            if (!playerInfo.getAccepted()) {
                integer6 = 10526880;
            }
            else if (playerInfo.getOnline()) {
                integer6 = 8388479;
            }
            else {
                integer6 = 16777215;
            }
            RealmsPlayerScreen.this.drawString(playerInfo.getName(), RealmsPlayerScreen.this.column1_x + 3 + 12, integer3 + 1, integer6);
            if (playerInfo.isOperator()) {
                RealmsPlayerScreen.this.drawOpped(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, integer3 + 1, integer4, integer5);
            }
            else {
                RealmsPlayerScreen.this.drawNormal(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, integer3 + 1, integer4, integer5);
            }
            RealmsPlayerScreen.this.drawRemoveIcon(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, integer3 + 2, integer4, integer5);
            RealmsPlayerScreen.this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.activityfeed.disabled"), RealmsPlayerScreen.this.column2_x, RealmsConstants.row(5), 10526880);
            RealmsTextureManager.withBoundFace(playerInfo.getUuid(), () -> {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, integer3 + 1, 8.0f, 8.0f, 8, 8, 8, 8, 64, 64);
                RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, integer3 + 1, 40.0f, 8.0f, 8, 8, 8, 8, 64, 64);
            });
        }
    }
}
