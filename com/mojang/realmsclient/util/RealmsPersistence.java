package com.mojang.realmsclient.util;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import java.io.File;
import net.minecraft.realms.Realms;

public class RealmsPersistence {
    public static RealmsPersistenceData readFile() {
        final File file1 = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
        final Gson gson2 = new Gson();
        try {
            return (RealmsPersistenceData)gson2.fromJson(FileUtils.readFileToString(file1), (Class)RealmsPersistenceData.class);
        }
        catch (IOException iOException3) {
            return new RealmsPersistenceData();
        }
    }
    
    public static void writeFile(final RealmsPersistenceData a) {
        final File file2 = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
        final Gson gson3 = new Gson();
        final String string4 = gson3.toJson(a);
        try {
            FileUtils.writeStringToFile(file2, string4);
        }
        catch (IOException ex) {}
    }
    
    public static class RealmsPersistenceData {
        public String newsLink;
        public boolean hasUnreadNews;
        
        private RealmsPersistenceData() {
            this.hasUnreadNews = false;
        }
    }
}
