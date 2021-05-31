package net.minecraft.world.level.block;

import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSkullBlock extends BaseEntityBlock {
    private final SkullBlock.Type type;
    
    public AbstractSkullBlock(final SkullBlock.Type a, final Properties c) {
        super(c);
        this.type = a;
    }
    
    @Override
    public boolean hasCustomBreakingProgress(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new SkullBlockEntity();
    }
    
    public SkullBlock.Type getType() {
        return this.type;
    }
}
