package net.minecraft.world.item;

import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.BlockGetter;
import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SwordItem extends TieredItem {
    private final float attackDamage;
    private final float attackSpeed;
    
    public SwordItem(final Tier bdn, final int integer, final float float3, final Properties a) {
        super(bdn, a);
        this.attackSpeed = float3;
        this.attackDamage = integer + bdn.getAttackDamageBonus();
    }
    
    public float getDamage() {
        return this.attackDamage;
    }
    
    @Override
    public boolean canAttackBlock(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        return !awg.isCreative();
    }
    
    @Override
    public float getDestroySpeed(final ItemStack bcj, final BlockState bvt) {
        final Block bmv4 = bvt.getBlock();
        if (bmv4 == Blocks.COBWEB) {
            return 15.0f;
        }
        final Material clo5 = bvt.getMaterial();
        if (clo5 == Material.PLANT || clo5 == Material.REPLACEABLE_PLANT || clo5 == Material.CORAL || bvt.is(BlockTags.LEAVES) || clo5 == Material.VEGETABLE) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    @Override
    public boolean hurtEnemy(final ItemStack bcj, final LivingEntity aix2, final LivingEntity aix3) {
        bcj.<LivingEntity>hurtAndBreak(1, aix3, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        return true;
    }
    
    @Override
    public boolean mineBlock(final ItemStack bcj, final Level bhr, final BlockState bvt, final BlockPos ew, final LivingEntity aix) {
        if (bvt.getDestroySpeed(bhr, ew) != 0.0f) {
            bcj.<LivingEntity>hurtAndBreak(2, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        }
        return true;
    }
    
    @Override
    public boolean canDestroySpecial(final BlockState bvt) {
        return bvt.getBlock() == Blocks.COBWEB;
    }
    
    @Override
    public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot ait) {
        final Multimap<String, AttributeModifier> multimap3 = super.getDefaultAttributeModifiers(ait);
        if (ait == EquipmentSlot.MAINHAND) {
            multimap3.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(SwordItem.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
            multimap3.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SwordItem.BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
        }
        return multimap3;
    }
}
