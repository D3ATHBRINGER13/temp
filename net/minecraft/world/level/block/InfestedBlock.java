package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;

public class InfestedBlock extends Block {
    private final Block hostBlock;
    private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK;
    
    public InfestedBlock(final Block bmv, final Properties c) {
        super(c);
        this.hostBlock = bmv;
        InfestedBlock.BLOCK_BY_HOST_BLOCK.put(bmv, this);
    }
    
    public Block getHostBlock() {
        return this.hostBlock;
    }
    
    public static boolean isCompatibleHostBlock(final BlockState bvt) {
        return InfestedBlock.BLOCK_BY_HOST_BLOCK.containsKey(bvt.getBlock());
    }
    
    @Override
    public void spawnAfterBreak(final BlockState bvt, final Level bhr, final BlockPos ew, final ItemStack bcj) {
        super.spawnAfterBreak(bvt, bhr, ew, bcj);
        if (!bhr.isClientSide && bhr.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, bcj) == 0) {
            final Silverfish avc6 = EntityType.SILVERFISH.create(bhr);
            avc6.moveTo(ew.getX() + 0.5, ew.getY(), ew.getZ() + 0.5, 0.0f, 0.0f);
            bhr.addFreshEntity(avc6);
            avc6.spawnAnim();
        }
    }
    
    public static BlockState stateByHostBlock(final Block bmv) {
        return ((Block)InfestedBlock.BLOCK_BY_HOST_BLOCK.get(bmv)).defaultBlockState();
    }
    
    static {
        BLOCK_BY_HOST_BLOCK = (Map)Maps.newIdentityHashMap();
    }
}
