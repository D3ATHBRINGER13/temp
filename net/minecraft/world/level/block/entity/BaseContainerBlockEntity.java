package net.minecraft.world.level.block.entity;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.Nameable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Container;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
    private LockCode lockKey;
    private Component name;
    
    protected BaseContainerBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
        this.lockKey = LockCode.NO_LOCK;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.lockKey = LockCode.fromTag(id);
        if (id.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(id.getString("CustomName"));
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        this.lockKey.addToTag(id);
        if (this.name != null) {
            id.putString("CustomName", Component.Serializer.toJson(this.name));
        }
        return id;
    }
    
    public void setCustomName(final Component jo) {
        this.name = jo;
    }
    
    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.getDefaultName();
    }
    
    @Override
    public Component getDisplayName() {
        return this.getName();
    }
    
    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }
    
    protected abstract Component getDefaultName();
    
    public boolean canOpen(final Player awg) {
        return canUnlock(awg, this.lockKey, this.getDisplayName());
    }
    
    public static boolean canUnlock(final Player awg, final LockCode ahl, final Component jo) {
        if (awg.isSpectator() || ahl.unlocksWith(awg.getMainHandItem())) {
            return true;
        }
        awg.displayClientMessage(new TranslatableComponent("container.isLocked", new Object[] { jo }), true);
        awg.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0f, 1.0f);
        return false;
    }
    
    @Nullable
    public AbstractContainerMenu createMenu(final int integer, final Inventory awf, final Player awg) {
        if (this.canOpen(awg)) {
            return this.createMenu(integer, awf);
        }
        return null;
    }
    
    protected abstract AbstractContainerMenu createMenu(final int integer, final Inventory awf);
}
