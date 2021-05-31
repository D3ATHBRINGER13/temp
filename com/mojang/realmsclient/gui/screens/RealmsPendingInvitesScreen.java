package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.gui.RowButton;
import net.minecraft.realms.RealmListEntry;
import java.util.Arrays;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.RealmsGuiEventListener;
import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.realms.AbstractRealmsButton;
import java.util.Collection;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import java.util.stream.Collectors;
import java.util.List;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsPendingInvitesScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private String toolTip;
    private boolean loaded;
    private PendingInvitationSelectionList pendingInvitationSelectionList;
    private RealmsLabel titleLabel;
    private int selectedInvite;
    private RealmsButton acceptButton;
    private RealmsButton rejectButton;
    
    public RealmsPendingInvitesScreen(final RealmsScreen realmsScreen) {
        this.selectedInvite = -1;
        this.lastScreen = realmsScreen;
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        new Thread("Realms-pending-invitations-fetcher") {
            public void run() {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                try {
                    final List<PendingInvite> list3 = cvm2.pendingInvites().pendingInvites;
                    final List<PendingInvitationSelectionListEntry> list4 = (List<PendingInvitationSelectionListEntry>)list3.stream().map(pendingInvite -> new PendingInvitationSelectionListEntry(pendingInvite)).collect(Collectors.toList());
                    Realms.execute(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries((java.util.Collection<PendingInvitationSelectionListEntry>)list4));
                }
                catch (RealmsServiceException cvu3) {
                    RealmsPendingInvitesScreen.LOGGER.error("Couldn't list invites");
                }
                finally {
                    RealmsPendingInvitesScreen.this.loaded = true;
                }
            }
        }.start();
        this.buttonsAdd(this.acceptButton = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString("mco.invites.button.accept")) {
            @Override
            public void onPress() {
                RealmsPendingInvitesScreen.this.accept(RealmsPendingInvitesScreen.this.selectedInvite);
                RealmsPendingInvitesScreen.this.selectedInvite = -1;
                RealmsPendingInvitesScreen.this.updateButtonStates();
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString("gui.done")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsMainScreen(RealmsPendingInvitesScreen.this.lastScreen));
            }
        });
        this.buttonsAdd(this.rejectButton = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString("mco.invites.button.reject")) {
            @Override
            public void onPress() {
                RealmsPendingInvitesScreen.this.reject(RealmsPendingInvitesScreen.this.selectedInvite);
                RealmsPendingInvitesScreen.this.selectedInvite = -1;
                RealmsPendingInvitesScreen.this.updateButtonStates();
            }
        });
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.invites.title"), this.width() / 2, 12, 16777215));
        this.addWidget(this.pendingInvitationSelectionList);
        this.narrateLabels();
        this.updateButtonStates();
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(new RealmsMainScreen(this.lastScreen));
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void updateList(final int integer) {
        this.pendingInvitationSelectionList.removeAtIndex(integer);
    }
    
    private void reject(final int integer) {
        if (integer < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-reject-invitation") {
                public void run() {
                    try {
                        final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                        cvm2.rejectInvitation(((PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(integer)).pendingInvite.invitationId);
                        Realms.execute(() -> RealmsPendingInvitesScreen.this.updateList(integer));
                    }
                    catch (RealmsServiceException cvu2) {
                        RealmsPendingInvitesScreen.LOGGER.error("Couldn't reject invite");
                    }
                }
            }.start();
        }
    }
    
    private void accept(final int integer) {
        if (integer < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-accept-invitation") {
                public void run() {
                    try {
                        final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                        cvm2.acceptInvitation(((PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(integer)).pendingInvite.invitationId);
                        Realms.execute(() -> RealmsPendingInvitesScreen.this.updateList(integer));
                    }
                    catch (RealmsServiceException cvu2) {
                        RealmsPendingInvitesScreen.LOGGER.error("Couldn't accept invite");
                    }
                }
            }.start();
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.renderBackground();
        this.pendingInvitationSelectionList.render(integer1, integer2, float3);
        this.titleLabel.render(this);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, integer1, integer2);
        }
        if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
            this.drawCenteredString(RealmsScreen.getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 16777215);
        }
        super.render(integer1, integer2, float3);
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
    
    private void updateButtonStates() {
        this.acceptButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
        this.rejectButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
    }
    
    private boolean shouldAcceptAndRejectButtonBeVisible(final int integer) {
        return integer != -1;
    }
    
    public static String getAge(final PendingInvite pendingInvite) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - pendingInvite.date.getTime());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class PendingInvitationSelectionList extends RealmsObjectSelectionList<PendingInvitationSelectionListEntry> {
        public PendingInvitationSelectionList() {
            super(RealmsPendingInvitesScreen.this.width(), RealmsPendingInvitesScreen.this.height(), 32, RealmsPendingInvitesScreen.this.height() - 40, 36);
        }
        
        public void removeAtIndex(final int integer) {
            this.remove(integer);
        }
        
        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }
        
        @Override
        public int getRowWidth() {
            return 260;
        }
        
        @Override
        public boolean isFocused() {
            return RealmsPendingInvitesScreen.this.isFocused(this);
        }
        
        @Override
        public void renderBackground() {
            RealmsPendingInvitesScreen.this.renderBackground();
        }
        
        @Override
        public void selectItem(final int integer) {
            this.setSelected(integer);
            if (integer != -1) {
                final List<PendingInvitationSelectionListEntry> list3 = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
                final PendingInvite pendingInvite4 = ((PendingInvitationSelectionListEntry)list3.get(integer)).pendingInvite;
                final String string5 = RealmsScreen.getLocalizedString("narrator.select.list.position", integer + 1, list3.size());
                final String string6 = Realms.joinNarrations((Iterable<String>)Arrays.asList((Object[])new String[] { pendingInvite4.worldName, pendingInvite4.worldOwnerName, RealmsPendingInvitesScreen.getAge(pendingInvite4), string5 }));
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string6));
            }
            this.selectInviteListItem(integer);
        }
        
        public void selectInviteListItem(final int integer) {
            RealmsPendingInvitesScreen.this.selectedInvite = integer;
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }
    }
    
    class PendingInvitationSelectionListEntry extends RealmListEntry {
        final PendingInvite pendingInvite;
        private final List<RowButton> rowButtons;
        
        PendingInvitationSelectionListEntry(final PendingInvite pendingInvite) {
            this.pendingInvite = pendingInvite;
            this.rowButtons = (List<RowButton>)Arrays.asList((Object[])new RowButton[] { new AcceptRowButton(), new RejectRowButton() });
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderPendingInvitationItem(this.pendingInvite, integer3, integer2, integer6, integer7);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            RowButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, integer, double1, double2);
            return true;
        }
        
        private void renderPendingInvitationItem(final PendingInvite pendingInvite, final int integer2, final int integer3, final int integer4, final int integer5) {
            RealmsPendingInvitesScreen.this.drawString(pendingInvite.worldName, integer2 + 38, integer3 + 1, 16777215);
            RealmsPendingInvitesScreen.this.drawString(pendingInvite.worldOwnerName, integer2 + 38, integer3 + 12, 7105644);
            RealmsPendingInvitesScreen.this.drawString(RealmsPendingInvitesScreen.getAge(pendingInvite), integer2 + 38, integer3 + 24, 7105644);
            RowButton.drawButtonsInRow(this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, integer2, integer3, integer4, integer5);
            RealmsTextureManager.withBoundFace(pendingInvite.worldOwnerUuid, () -> {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(integer2, integer3, 8.0f, 8.0f, 8, 8, 32, 32, 64, 64);
                RealmsScreen.blit(integer2, integer3, 40.0f, 8.0f, 8, 8, 32, 32, 64, 64);
            });
        }
        
        class AcceptRowButton extends RowButton {
            AcceptRowButton() {
                super(15, 15, 215, 5);
            }
            
            @Override
            protected void draw(final int integer1, final int integer2, final boolean boolean3) {
                RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.pushMatrix();
                RealmsScreen.blit(integer1, integer2, boolean3 ? 19.0f : 0.0f, 0.0f, 18, 18, 37, 18);
                GlStateManager.popMatrix();
                if (boolean3) {
                    RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.accept");
                }
            }
            
            @Override
            public void onClick(final int integer) {
                RealmsPendingInvitesScreen.this.accept(integer);
            }
        }
        
        class RejectRowButton extends RowButton {
            RejectRowButton() {
                super(15, 15, 235, 5);
            }
            
            @Override
            protected void draw(final int integer1, final int integer2, final boolean boolean3) {
                RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.pushMatrix();
                RealmsScreen.blit(integer1, integer2, boolean3 ? 19.0f : 0.0f, 0.0f, 18, 18, 37, 18);
                GlStateManager.popMatrix();
                if (boolean3) {
                    RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.reject");
                }
            }
            
            @Override
            public void onClick(final int integer) {
                RealmsPendingInvitesScreen.this.reject(integer);
            }
        }
    }
}
