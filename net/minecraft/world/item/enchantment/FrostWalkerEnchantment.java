package net.minecraft.world.item.enchantment;

import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;

public class FrostWalkerEnchantment extends Enchantment {
    public FrostWalkerEnchantment(final Rarity a, final EquipmentSlot... arr) {
        super(a, EnchantmentCategory.ARMOR_FEET, arr);
    }
    
    @Override
    public int getMinCost(final int integer) {
        return integer * 10;
    }
    
    @Override
    public int getMaxCost(final int integer) {
        return this.getMinCost(integer) + 15;
    }
    
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    
    @Override
    public int getMaxLevel() {
        return 2;
    }
    
    public static void onEntityMoved(final LivingEntity aix, final Level bhr, final BlockPos ew, final int integer) {
        if (!aix.onGround) {
            return;
        }
        final BlockState bvt5 = Blocks.FROSTED_ICE.defaultBlockState();
        final float float6 = (float)Math.min(16, 2 + integer);
        final BlockPos.MutableBlockPos a7 = new BlockPos.MutableBlockPos();
        for (final BlockPos ew2 : BlockPos.betweenClosed(ew.offset(-float6, -1.0, -float6), ew.offset(float6, -1.0, float6))) {
            if (ew2.closerThan(aix.position(), float6)) {
                a7.set(ew2.getX(), ew2.getY() + 1, ew2.getZ());
                final BlockState bvt6 = bhr.getBlockState(a7);
                if (!bvt6.isAir()) {
                    continue;
                }
                final BlockState bvt7 = bhr.getBlockState(ew2);
                if (bvt7.getMaterial() != Material.WATER || bvt7.<Integer>getValue((Property<Integer>)LiquidBlock.LEVEL) != 0 || !bvt5.canSurvive(bhr, ew2) || !bhr.isUnobstructed(bvt5, ew2, CollisionContext.empty())) {
                    continue;
                }
                bhr.setBlockAndUpdate(ew2, bvt5);
                bhr.getBlockTicks().scheduleTick(ew2, Blocks.FROSTED_ICE, Mth.nextInt(aix.getRandom(), 60, 120));
            }
        }
    }
    
    public boolean checkCompatibility(final Enchantment bfs) {
        return super.checkCompatibility(bfs) && bfs != Enchantments.DEPTH_STRIDER;
    }
}
