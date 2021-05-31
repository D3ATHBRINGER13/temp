package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import javax.annotation.concurrent.Immutable;

@Immutable
public class LockCode {
    public static final LockCode NO_LOCK;
    private final String key;
    
    public LockCode(final String string) {
        this.key = string;
    }
    
    public boolean unlocksWith(final ItemStack bcj) {
        return this.key.isEmpty() || (!bcj.isEmpty() && bcj.hasCustomHoverName() && this.key.equals(bcj.getHoverName().getString()));
    }
    
    public void addToTag(final CompoundTag id) {
        if (!this.key.isEmpty()) {
            id.putString("Lock", this.key);
        }
    }
    
    public static LockCode fromTag(final CompoundTag id) {
        if (id.contains("Lock", 8)) {
            return new LockCode(id.getString("Lock"));
        }
        return LockCode.NO_LOCK;
    }
    
    static {
        NO_LOCK = new LockCode("");
    }
}
