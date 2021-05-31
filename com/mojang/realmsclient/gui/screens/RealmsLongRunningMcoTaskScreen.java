package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.LongRunningTask;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final int BUTTON_CANCEL_ID = 666;
    private final int BUTTON_BACK_ID = 667;
    private final RealmsScreen lastScreen;
    private final LongRunningTask taskThread;
    private volatile String title;
    private volatile boolean error;
    private volatile String errorMessage;
    private volatile boolean aborted;
    private int animTicks;
    private final LongRunningTask task;
    private final int buttonLength = 212;
    public static final String[] symbols;
    
    public RealmsLongRunningMcoTaskScreen(final RealmsScreen realmsScreen, final LongRunningTask cvx) {
        this.title = "";
        this.lastScreen = realmsScreen;
        (this.task = cvx).setScreen(this);
        this.taskThread = cvx;
    }
    
    public void start() {
        final Thread thread2 = new Thread((Runnable)this.taskThread, "Realms-long-running-task");
        thread2.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new RealmsDefaultUncaughtExceptionHandler(RealmsLongRunningMcoTaskScreen.LOGGER));
        thread2.start();
    }
    
    @Override
    public void tick() {
        super.tick();
        Realms.narrateRepeatedly(this.title);
        ++this.animTicks;
        this.task.tick();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.cancelOrBackButtonClicked();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void init() {
        this.task.init();
        this.buttonsAdd(new RealmsButton(666, this.width() / 2 - 106, RealmsConstants.row(12), 212, 20, RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
            }
        });
    }
    
    private void cancelOrBackButtonClicked() {
        this.aborted = true;
        this.task.abortTask();
        Realms.setScreen(this.lastScreen);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.title, this.width() / 2, RealmsConstants.row(3), 16777215);
        if (!this.error) {
            this.drawCenteredString(RealmsLongRunningMcoTaskScreen.symbols[this.animTicks % RealmsLongRunningMcoTaskScreen.symbols.length], this.width() / 2, RealmsConstants.row(8), 8421504);
        }
        if (this.error) {
            this.drawCenteredString(this.errorMessage, this.width() / 2, RealmsConstants.row(8), 16711680);
        }
        super.render(integer1, integer2, float3);
    }
    
    public void error(final String string) {
        this.error = true;
        Realms.narrateNow(this.errorMessage = string);
        this.buttonsClear();
        this.buttonsAdd(new RealmsButton(667, this.width() / 2 - 106, this.height() / 4 + 120 + 12, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
            }
        });
    }
    
    public void setTitle(final String string) {
        this.title = string;
    }
    
    public boolean aborted() {
        return this.aborted;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        symbols = new String[] { "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _" };
    }
}
