package net.minecraft.world.level.block;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import java.util.Random;

public class OreBlock extends Block {
    public OreBlock(final Properties c) {
        super(c);
    }
    
    protected int xpOnDrop(final Random random) {
        if (this == Blocks.COAL_ORE) {
            return Mth.nextInt(random, 0, 2);
        }
        if (this == Blocks.DIAMOND_ORE) {
            return Mth.nextInt(random, 3, 7);
        }
        if (this == Blocks.EMERALD_ORE) {
            return Mth.nextInt(random, 3, 7);
        }
        if (this == Blocks.LAPIS_ORE) {
            return Mth.nextInt(random, 2, 5);
        }
        if (this == Blocks.NETHER_QUARTZ_ORE) {
            return Mth.nextInt(random, 2, 5);
        }
        return 0;
    }
    
    @Override
    public void spawnAfterBreak(final BlockState bvt, final Level bhr, final BlockPos ew, final ItemStack bcj) {
        super.spawnAfterBreak(bvt, bhr, ew, bcj);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, bcj) == 0) {
            final int integer6 = this.xpOnDrop(bhr.random);
            if (integer6 > 0) {
                this.popExperience(bhr, ew, integer6);
            }
        }
    }
}
