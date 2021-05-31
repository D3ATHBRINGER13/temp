package net.minecraft.world.level.block;

import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.Container;
import net.minecraft.core.BlockSource;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.dispenser.DispenseItemBehavior;

public class DropperBlock extends DispenserBlock {
    private static final DispenseItemBehavior DISPENSE_BEHAVIOUR;
    
    public DropperBlock(final Properties c) {
        super(c);
    }
    
    @Override
    protected DispenseItemBehavior getDispenseMethod(final ItemStack bcj) {
        return DropperBlock.DISPENSE_BEHAVIOUR;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new DropperBlockEntity();
    }
    
    @Override
    protected void dispenseFrom(final Level bhr, final BlockPos ew) {
        final BlockSourceImpl ey4 = new BlockSourceImpl(bhr, ew);
        final DispenserBlockEntity buf5 = ey4.<DispenserBlockEntity>getEntity();
        final int integer6 = buf5.getRandomSlot();
        if (integer6 < 0) {
            bhr.levelEvent(1001, ew, 0);
            return;
        }
        final ItemStack bcj7 = buf5.getItem(integer6);
        if (bcj7.isEmpty()) {
            return;
        }
        final Direction fb8 = bhr.getBlockState(ew).<Direction>getValue((Property<Direction>)DropperBlock.FACING);
        final Container ahc9 = HopperBlockEntity.getContainerAt(bhr, ew.relative(fb8));
        ItemStack bcj8;
        if (ahc9 == null) {
            bcj8 = DropperBlock.DISPENSE_BEHAVIOUR.dispense(ey4, bcj7);
        }
        else {
            bcj8 = HopperBlockEntity.addItem(buf5, ahc9, bcj7.copy().split(1), fb8.getOpposite());
            if (bcj8.isEmpty()) {
                bcj8 = bcj7.copy();
                bcj8.shrink(1);
            }
            else {
                bcj8 = bcj7.copy();
            }
        }
        buf5.setItem(integer6, bcj8);
    }
    
    static {
        DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();
    }
}
