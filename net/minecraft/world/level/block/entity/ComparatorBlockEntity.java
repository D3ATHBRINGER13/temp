package net.minecraft.world.level.block.entity;

import net.minecraft.nbt.CompoundTag;

public class ComparatorBlockEntity extends BlockEntity {
    private int output;
    
    public ComparatorBlockEntity() {
        super(BlockEntityType.COMPARATOR);
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.putInt("OutputSignal", this.output);
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.output = id.getInt("OutputSignal");
    }
    
    public int getOutputSignal() {
        return this.output;
    }
    
    public void setOutputSignal(final int integer) {
        this.output = integer;
    }
}
