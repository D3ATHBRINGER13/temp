package net.minecraft.world.item;

import net.minecraft.core.Rotations;
import java.util.Random;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;

public class ArmorStandItem extends Item {
    public ArmorStandItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Direction fb3 = bdu.getClickedFace();
        if (fb3 == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        final Level bhr4 = bdu.getLevel();
        final BlockPlaceContext ban5 = new BlockPlaceContext(bdu);
        final BlockPos ew6 = ban5.getClickedPos();
        final BlockPos ew7 = ew6.above();
        if (!ban5.canPlace() || !bhr4.getBlockState(ew7).canBeReplaced(ban5)) {
            return InteractionResult.FAIL;
        }
        final double double8 = ew6.getX();
        final double double9 = ew6.getY();
        final double double10 = ew6.getZ();
        final List<Entity> list14 = bhr4.getEntities(null, new AABB(double8, double9, double10, double8 + 1.0, double9 + 2.0, double10 + 1.0));
        if (!list14.isEmpty()) {
            return InteractionResult.FAIL;
        }
        final ItemStack bcj15 = bdu.getItemInHand();
        if (!bhr4.isClientSide) {
            bhr4.removeBlock(ew6, false);
            bhr4.removeBlock(ew7, false);
            final ArmorStand atl16 = new ArmorStand(bhr4, double8 + 0.5, double9, double10 + 0.5);
            final float float17 = Mth.floor((Mth.wrapDegrees(bdu.getRotation() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            atl16.moveTo(double8 + 0.5, double9, double10 + 0.5, float17, 0.0f);
            this.randomizePose(atl16, bhr4.random);
            EntityType.updateCustomEntityTag(bhr4, bdu.getPlayer(), atl16, bcj15.getTag());
            bhr4.addFreshEntity(atl16);
            bhr4.playSound(null, atl16.x, atl16.y, atl16.z, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75f, 0.8f);
        }
        bcj15.shrink(1);
        return InteractionResult.SUCCESS;
    }
    
    private void randomizePose(final ArmorStand atl, final Random random) {
        Rotations fo4 = atl.getHeadPose();
        float float6 = random.nextFloat() * 5.0f;
        final float float7 = random.nextFloat() * 20.0f - 10.0f;
        Rotations fo5 = new Rotations(fo4.getX() + float6, fo4.getY() + float7, fo4.getZ());
        atl.setHeadPose(fo5);
        fo4 = atl.getBodyPose();
        float6 = random.nextFloat() * 10.0f - 5.0f;
        fo5 = new Rotations(fo4.getX(), fo4.getY() + float6, fo4.getZ());
        atl.setBodyPose(fo5);
    }
}
