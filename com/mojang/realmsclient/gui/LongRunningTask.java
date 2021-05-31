package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;

public abstract class LongRunningTask implements Runnable {
    protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;
    
    public void setScreen(final RealmsLongRunningMcoTaskScreen cwo) {
        this.longRunningMcoTaskScreen = cwo;
    }
    
    public void error(final String string) {
        this.longRunningMcoTaskScreen.error(string);
    }
    
    public void setTitle(final String string) {
        this.longRunningMcoTaskScreen.setTitle(string);
    }
    
    public boolean aborted() {
        return this.longRunningMcoTaskScreen.aborted();
    }
    
    public void tick() {
    }
    
    public void init() {
    }
    
    public void abortTask() {
    }
}
