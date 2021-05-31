package net.minecraft.realms;

import net.minecraft.world.level.storage.LevelStorageException;
import java.util.Iterator;
import net.minecraft.world.level.storage.LevelSummary;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.storage.LevelStorageSource;

public class RealmsAnvilLevelStorageSource {
    private final LevelStorageSource levelStorageSource;
    
    public RealmsAnvilLevelStorageSource(final LevelStorageSource coq) {
        this.levelStorageSource = coq;
    }
    
    public String getName() {
        return this.levelStorageSource.getName();
    }
    
    public boolean levelExists(final String string) {
        return this.levelStorageSource.levelExists(string);
    }
    
    public boolean convertLevel(final String string, final ProgressListener zz) {
        return this.levelStorageSource.convertLevel(string, zz);
    }
    
    public boolean requiresConversion(final String string) {
        return this.levelStorageSource.requiresConversion(string);
    }
    
    public boolean isNewLevelIdAcceptable(final String string) {
        return this.levelStorageSource.isNewLevelIdAcceptable(string);
    }
    
    public boolean deleteLevel(final String string) {
        return this.levelStorageSource.deleteLevel(string);
    }
    
    public void renameLevel(final String string1, final String string2) {
        this.levelStorageSource.renameLevel(string1, string2);
    }
    
    public List<RealmsLevelSummary> getLevelList() throws LevelStorageException {
        final List<RealmsLevelSummary> list2 = (List<RealmsLevelSummary>)Lists.newArrayList();
        for (final LevelSummary cor4 : this.levelStorageSource.getLevelList()) {
            list2.add(new RealmsLevelSummary(cor4));
        }
        return list2;
    }
}
