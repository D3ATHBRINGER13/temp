package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface ProgressListener {
    void progressStartNoAbort(final Component jo);
    
    void progressStart(final Component jo);
    
    void progressStage(final Component jo);
    
    void progressStagePercentage(final int integer);
    
    void stop();
}
