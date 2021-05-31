package net.minecraft.data.structures;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.nbt.CompoundTag;

public class StructureUpdater implements SnbtToNbt.Filter {
    public CompoundTag apply(final String string, final CompoundTag id) {
        if (string.startsWith("data/minecraft/structures/")) {
            return updateStructure(patchVersion(id));
        }
        return id;
    }
    
    private static CompoundTag patchVersion(final CompoundTag id) {
        if (!id.contains("DataVersion", 99)) {
            id.putInt("DataVersion", 500);
        }
        return id;
    }
    
    private static CompoundTag updateStructure(final CompoundTag id) {
        final StructureTemplate cjt2 = new StructureTemplate();
        cjt2.load(NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, id, id.getInt("DataVersion")));
        return cjt2.save(new CompoundTag());
    }
}
