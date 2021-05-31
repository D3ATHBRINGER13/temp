package net.minecraft.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.EntityType;

public class HangingEntityItem extends Item {
    private final EntityType<? extends HangingEntity> type;
    
    public HangingEntityItem(final EntityType<? extends HangingEntity> ais, final Properties a) {
        super(a);
        this.type = ais;
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final BlockPos ew3 = bdu.getClickedPos();
        final Direction fb4 = bdu.getClickedFace();
        final BlockPos ew4 = ew3.relative(fb4);
        final Player awg6 = bdu.getPlayer();
        final ItemStack bcj7 = bdu.getItemInHand();
        if (awg6 != null && !this.mayPlace(awg6, fb4, bcj7, ew4)) {
            return InteractionResult.FAIL;
        }
        final Level bhr8 = bdu.getLevel();
        HangingEntity atm9;
        if (this.type == EntityType.PAINTING) {
            atm9 = new Painting(bhr8, ew4, fb4);
        }
        else {
            if (this.type != EntityType.ITEM_FRAME) {
                return InteractionResult.SUCCESS;
            }
            atm9 = new ItemFrame(bhr8, ew4, fb4);
        }
        final CompoundTag id10 = bcj7.getTag();
        if (id10 != null) {
            EntityType.updateCustomEntityTag(bhr8, awg6, atm9, id10);
        }
        if (atm9.survives()) {
            if (!bhr8.isClientSide) {
                atm9.playPlacementSound();
                bhr8.addFreshEntity(atm9);
            }
            bcj7.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
    
    protected boolean mayPlace(final Player awg, final Direction fb, final ItemStack bcj, final BlockPos ew) {
        return !fb.getAxis().isVertical() && awg.mayUseItemAt(ew, fb, bcj);
    }
}
