package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEntityBlock extends Block implements EntityBlock {
    protected BaseEntityBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.INVISIBLE;
    }
    
    @Override
    public boolean triggerEvent(final BlockState bvt, final Level bhr, final BlockPos ew, final int integer4, final int integer5) {
        super.triggerEvent(bvt, bhr, ew, integer4, integer5);
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        return btw7 != null && btw7.triggerEvent(integer4, integer5);
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        return (btw5 instanceof MenuProvider) ? btw5 : null;
    }
}
