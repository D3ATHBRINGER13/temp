package net.minecraft.world.level.block.entity;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Clearable;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
    private ItemStack record;
    
    public JukeboxBlockEntity() {
        super(BlockEntityType.JUKEBOX);
        this.record = ItemStack.EMPTY;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        if (id.contains("RecordItem", 10)) {
            this.setRecord(ItemStack.of(id.getCompound("RecordItem")));
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (!this.getRecord().isEmpty()) {
            id.put("RecordItem", (Tag)this.getRecord().save(new CompoundTag()));
        }
        return id;
    }
    
    public ItemStack getRecord() {
        return this.record;
    }
    
    public void setRecord(final ItemStack bcj) {
        this.record = bcj;
        this.setChanged();
    }
    
    @Override
    public void clearContent() {
        this.setRecord(ItemStack.EMPTY);
    }
}
