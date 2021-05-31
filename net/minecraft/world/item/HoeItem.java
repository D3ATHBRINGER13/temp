package net.minecraft.world.item;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import java.util.Map;

public class HoeItem extends TieredItem {
    private final float attackSpeed;
    protected static final Map<Block, BlockState> TILLABLES;
    
    public HoeItem(final Tier bdn, final float float2, final Properties a) {
        super(bdn, a);
        this.attackSpeed = float2;
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        if (bdu.getClickedFace() != Direction.DOWN && bhr3.getBlockState(ew4.above()).isAir()) {
            final BlockState bvt5 = (BlockState)HoeItem.TILLABLES.get(bhr3.getBlockState(ew4).getBlock());
            if (bvt5 != null) {
                final Player awg6 = bdu.getPlayer();
                bhr3.playSound(awg6, ew4, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!bhr3.isClientSide) {
                    bhr3.setBlock(ew4, bvt5, 11);
                    if (awg6 != null) {
                        bdu.getItemInHand().<Player>hurtAndBreak(1, awg6, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(bdu.getHand())));
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public boolean hurtEnemy(final ItemStack bcj, final LivingEntity aix2, final LivingEntity aix3) {
        bcj.<LivingEntity>hurtAndBreak(1, aix3, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        return true;
    }
    
    @Override
    public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot ait) {
        final Multimap<String, AttributeModifier> multimap3 = super.getDefaultAttributeModifiers(ait);
        if (ait == EquipmentSlot.MAINHAND) {
            multimap3.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(HoeItem.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 0.0, AttributeModifier.Operation.ADDITION));
            multimap3.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(HoeItem.BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
        }
        return multimap3;
    }
    
    static {
        TILLABLES = (Map)Maps.newHashMap((Map)ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));
    }
}
