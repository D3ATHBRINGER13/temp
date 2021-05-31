package net.minecraft.world.item;

import net.minecraft.world.level.dimension.end.EndDragonFight;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;

public class EndCrystalItem extends Item {
    public EndCrystalItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (bvt5.getBlock() != Blocks.OBSIDIAN && bvt5.getBlock() != Blocks.BEDROCK) {
            return InteractionResult.FAIL;
        }
        final BlockPos ew5 = ew4.above();
        if (!bhr3.isEmptyBlock(ew5)) {
            return InteractionResult.FAIL;
        }
        final double double7 = ew5.getX();
        final double double8 = ew5.getY();
        final double double9 = ew5.getZ();
        final List<Entity> list13 = bhr3.getEntities(null, new AABB(double7, double8, double9, double7 + 1.0, double8 + 2.0, double9 + 1.0));
        if (!list13.isEmpty()) {
            return InteractionResult.FAIL;
        }
        if (!bhr3.isClientSide) {
            final EndCrystal aso14 = new EndCrystal(bhr3, double7 + 0.5, double8, double9 + 0.5);
            aso14.setShowBottom(false);
            bhr3.addFreshEntity(aso14);
            if (bhr3.dimension instanceof TheEndDimension) {
                final EndDragonFight byr15 = ((TheEndDimension)bhr3.dimension).getDragonFight();
                byr15.tryRespawn();
            }
        }
        bdu.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return true;
    }
}
