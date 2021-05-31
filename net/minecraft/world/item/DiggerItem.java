package net.minecraft.world.item;

import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public class DiggerItem extends TieredItem {
    private final Set<Block> blocks;
    protected final float speed;
    protected final float attackDamage;
    protected final float attackSpeed;
    
    protected DiggerItem(final float float1, final float float2, final Tier bdn, final Set<Block> set, final Properties a) {
        super(bdn, a);
        this.blocks = set;
        this.speed = bdn.getSpeed();
        this.attackDamage = float1 + bdn.getAttackDamageBonus();
        this.attackSpeed = float2;
    }
    
    @Override
    public float getDestroySpeed(final ItemStack bcj, final BlockState bvt) {
        return this.blocks.contains(bvt.getBlock()) ? this.speed : 1.0f;
    }
    
    @Override
    public boolean hurtEnemy(final ItemStack bcj, final LivingEntity aix2, final LivingEntity aix3) {
        bcj.<LivingEntity>hurtAndBreak(2, aix3, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        return true;
    }
    
    @Override
    public boolean mineBlock(final ItemStack bcj, final Level bhr, final BlockState bvt, final BlockPos ew, final LivingEntity aix) {
        if (!bhr.isClientSide && bvt.getDestroySpeed(bhr, ew) != 0.0f) {
            bcj.<LivingEntity>hurtAndBreak(1, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        }
        return true;
    }
    
    @Override
    public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot ait) {
        final Multimap<String, AttributeModifier> multimap3 = super.getDefaultAttributeModifiers(ait);
        if (ait == EquipmentSlot.MAINHAND) {
            multimap3.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DiggerItem.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
            multimap3.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(DiggerItem.BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
        }
        return multimap3;
    }
}
