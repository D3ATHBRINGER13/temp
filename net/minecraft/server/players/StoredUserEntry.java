package net.minecraft.server.players;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class StoredUserEntry<T> {
    @Nullable
    private final T user;
    
    public StoredUserEntry(final T object) {
        this.user = object;
    }
    
    protected StoredUserEntry(@Nullable final T object, final JsonObject jsonObject) {
        this.user = object;
    }
    
    @Nullable
    T getUser() {
        return this.user;
    }
    
    boolean hasExpired() {
        return false;
    }
    
    protected void serialize(final JsonObject jsonObject) {
    }
}
