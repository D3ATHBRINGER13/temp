package net.minecraft.world.level.saveddata;

public class SaveDataDirtyRunnable implements Runnable {
    private final SavedData savedData;
    
    public SaveDataDirtyRunnable(final SavedData coc) {
        this.savedData = coc;
    }
    
    public void run() {
        this.savedData.setDirty();
    }
}
