package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import java.util.Locale;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.RealmsServer;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsSubscriptionInfoScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private final RealmsServer serverData;
    private final RealmsScreen mainScreen;
    private final int BUTTON_BACK_ID = 0;
    private final int BUTTON_DELETE_ID = 1;
    private final int BUTTON_SUBSCRIPTION_ID = 2;
    private final String subscriptionTitle;
    private final String subscriptionStartLabelText;
    private final String timeLeftLabelText;
    private final String daysLeftLabelText;
    private int daysLeft;
    private String startDate;
    private Subscription.SubscriptionType type;
    private final String PURCHASE_LINK = "https://account.mojang.com/buy/realms";
    
    public RealmsSubscriptionInfoScreen(final RealmsScreen realmsScreen1, final RealmsServer realmsServer, final RealmsScreen realmsScreen3) {
        this.lastScreen = realmsScreen1;
        this.serverData = realmsServer;
        this.mainScreen = realmsScreen3;
        this.subscriptionTitle = RealmsScreen.getLocalizedString("mco.configure.world.subscription.title");
        this.subscriptionStartLabelText = RealmsScreen.getLocalizedString("mco.configure.world.subscription.start");
        this.timeLeftLabelText = RealmsScreen.getLocalizedString("mco.configure.world.subscription.timeleft");
        this.daysLeftLabelText = RealmsScreen.getLocalizedString("mco.configure.world.subscription.recurring.daysleft");
    }
    
    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        Realms.narrateNow(this.subscriptionTitle, this.subscriptionStartLabelText, this.startDate, this.timeLeftLabelText, this.daysLeftPresentation(this.daysLeft));
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(new RealmsButton(2, this.width() / 2 - 100, RealmsConstants.row(6), RealmsScreen.getLocalizedString("mco.configure.world.subscription.extend")) {
            @Override
            public void onPress() {
                final String string2 = "https://account.mojang.com/buy/realms?sid=" + RealmsSubscriptionInfoScreen.this.serverData.remoteSubscriptionId + "&pid=" + Realms.getUUID();
                Realms.setClipboard(string2);
                RealmsUtil.browseTo(string2);
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(12), RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsSubscriptionInfoScreen.this.lastScreen);
            }
        });
        if (this.serverData.expired) {
            this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.row(10), RealmsScreen.getLocalizedString("mco.configure.world.delete.button")) {
                @Override
                public void onPress() {
                    final String string2 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line1");
                    final String string3 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line2");
                    Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSubscriptionInfoScreen.this, RealmsLongConfirmationScreen.Type.Warning, string2, string3, true, 1));
                }
            });
        }
    }
    
    private void getSubscription(final long long1) {
        final RealmsClient cvm4 = RealmsClient.createRealmsClient();
        try {
            final Subscription subscription5 = cvm4.subscriptionFor(long1);
            this.daysLeft = subscription5.daysLeft;
            this.startDate = this.localPresentation(subscription5.startDate);
            this.type = subscription5.type;
        }
        catch (RealmsServiceException cvu5) {
            RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't get subscription");
            Realms.setScreen(new RealmsGenericErrorScreen(cvu5, this.lastScreen));
        }
        catch (IOException iOException5) {
            RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't parse response subscribing");
        }
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (integer == 1 && boolean1) {
            new Thread("Realms-delete-realm") {
                public void run() {
                    try {
                        final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                        cvm2.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
                    }
                    catch (RealmsServiceException cvu2) {
                        RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                        RealmsSubscriptionInfoScreen.LOGGER.error(cvu2);
                    }
                    catch (IOException iOException2) {
                        RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                        iOException2.printStackTrace();
                    }
                    Realms.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
                }
            }.start();
        }
        Realms.setScreen(this);
    }
    
    private String localPresentation(final long long1) {
        final Calendar calendar4 = (Calendar)new GregorianCalendar(TimeZone.getDefault());
        calendar4.setTimeInMillis(long1);
        return DateFormat.getDateTimeInstance().format(calendar4.getTime());
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
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final int integer3 = this.width() / 2 - 100;
        this.drawCenteredString(this.subscriptionTitle, this.width() / 2, 17, 16777215);
        this.drawString(this.subscriptionStartLabelText, integer3, RealmsConstants.row(0), 10526880);
        this.drawString(this.startDate, integer3, RealmsConstants.row(1), 16777215);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.drawString(this.timeLeftLabelText, integer3, RealmsConstants.row(3), 10526880);
        }
        else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.drawString(this.daysLeftLabelText, integer3, RealmsConstants.row(3), 10526880);
        }
        this.drawString(this.daysLeftPresentation(this.daysLeft), integer3, RealmsConstants.row(4), 16777215);
        super.render(integer1, integer2, float3);
    }
    
    private String daysLeftPresentation(final int integer) {
        if (integer == -1 && this.serverData.expired) {
            return RealmsScreen.getLocalizedString("mco.configure.world.subscription.expired");
        }
        if (integer <= 1) {
            return RealmsScreen.getLocalizedString("mco.configure.world.subscription.less_than_a_day");
        }
        final int integer2 = integer / 30;
        final int integer3 = integer % 30;
        final StringBuilder stringBuilder5 = new StringBuilder();
        if (integer2 > 0) {
            stringBuilder5.append(integer2).append(" ");
            if (integer2 == 1) {
                stringBuilder5.append(RealmsScreen.getLocalizedString("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
            }
            else {
                stringBuilder5.append(RealmsScreen.getLocalizedString("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
            }
        }
        if (integer3 > 0) {
            if (stringBuilder5.length() > 0) {
                stringBuilder5.append(", ");
            }
            stringBuilder5.append(integer3).append(" ");
            if (integer3 == 1) {
                stringBuilder5.append(RealmsScreen.getLocalizedString("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
            }
            else {
                stringBuilder5.append(RealmsScreen.getLocalizedString("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
            }
        }
        return stringBuilder5.toString();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
