package net.minecraft.commands.arguments.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Set;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import java.util.function.Predicate;

public class BlockInput implements Predicate<BlockInWorld> {
    private final BlockState state;
    private final Set<Property<?>> properties;
    @Nullable
    private final CompoundTag tag;
    
    public BlockInput(final BlockState bvt, final Set<Property<?>> set, @Nullable final CompoundTag id) {
        this.state = bvt;
        this.properties = set;
        this.tag = id;
    }
    
    public BlockState getState() {
        return this.state;
    }
    
    public boolean test(final BlockInWorld bvx) {
        final BlockState bvt3 = bvx.getState();
        if (bvt3.getBlock() != this.state.getBlock()) {
            return false;
        }
        for (final Property<?> bww5 : this.properties) {
            if (bvt3.getValue(bww5) != this.state.getValue(bww5)) {
                return false;
            }
        }
        if (this.tag != null) {
            final BlockEntity btw4 = bvx.getEntity();
            return btw4 != null && NbtUtils.compareNbt(this.tag, btw4.save(new CompoundTag()), true);
        }
        return true;
    }
    
    public boolean place(final ServerLevel vk, final BlockPos ew, final int integer) {
        if (!vk.setBlock(ew, this.state, integer)) {
            return false;
        }
        if (this.tag != null) {
            final BlockEntity btw5 = vk.getBlockEntity(ew);
            if (btw5 != null) {
                final CompoundTag id6 = this.tag.copy();
                id6.putInt("x", ew.getX());
                id6.putInt("y", ew.getY());
                id6.putInt("z", ew.getZ());
                btw5.load(id6);
            }
        }
        return true;
    }
}
