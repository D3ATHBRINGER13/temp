package net.minecraft.world.level;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.WeighedRandom;

public class SpawnData extends WeighedRandom.WeighedRandomItem {
    private final CompoundTag tag;
    
    public SpawnData() {
        super(1);
        (this.tag = new CompoundTag()).putString("id", "minecraft:pig");
    }
    
    public SpawnData(final CompoundTag id) {
        this(id.contains("Weight", 99) ? id.getInt("Weight") : 1, id.getCompound("Entity"));
    }
    
    public SpawnData(final int integer, final CompoundTag id) {
        super(integer);
        this.tag = id;
    }
    
    public CompoundTag save() {
        final CompoundTag id2 = new CompoundTag();
        if (!this.tag.contains("id", 8)) {
            this.tag.putString("id", "minecraft:pig");
        }
        else if (!this.tag.getString("id").contains(":")) {
            this.tag.putString("id", new ResourceLocation(this.tag.getString("id")).toString());
        }
        id2.put("Entity", (Tag)this.tag);
        id2.putInt("Weight", this.weight);
        return id2;
    }
    
    public CompoundTag getTag() {
        return this.tag;
    }
}
