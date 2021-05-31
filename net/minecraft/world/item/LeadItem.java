package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;

public class LeadItem extends Item {
    public LeadItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final Block bmv5 = bhr3.getBlockState(ew4).getBlock();
        if (bmv5.is(BlockTags.FENCES)) {
            final Player awg6 = bdu.getPlayer();
            if (!bhr3.isClientSide && awg6 != null) {
                bindPlayerMobs(awg6, bhr3, ew4);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    public static boolean bindPlayerMobs(final Player awg, final Level bhr, final BlockPos ew) {
        LeashFenceKnotEntity ato4 = null;
        boolean boolean5 = false;
        final double double6 = 7.0;
        final int integer8 = ew.getX();
        final int integer9 = ew.getY();
        final int integer10 = ew.getZ();
        final List<Mob> list11 = bhr.<Mob>getEntitiesOfClass((java.lang.Class<? extends Mob>)Mob.class, new AABB(integer8 - 7.0, integer9 - 7.0, integer10 - 7.0, integer8 + 7.0, integer9 + 7.0, integer10 + 7.0));
        for (final Mob aiy13 : list11) {
            if (aiy13.getLeashHolder() == awg) {
                if (ato4 == null) {
                    ato4 = LeashFenceKnotEntity.getOrCreateKnot(bhr, ew);
                }
                aiy13.setLeashedTo(ato4, true);
                boolean5 = true;
            }
        }
        return boolean5;
    }
}
