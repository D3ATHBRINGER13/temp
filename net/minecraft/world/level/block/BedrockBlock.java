package net.minecraft.world.level.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BedrockBlock extends Block {
    public BedrockBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return false;
    }
}
