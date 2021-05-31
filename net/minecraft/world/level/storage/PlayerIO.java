package net.minecraft.world.level.storage;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface PlayerIO {
    void save(final Player awg);
    
    @Nullable
    CompoundTag load(final Player awg);
}
