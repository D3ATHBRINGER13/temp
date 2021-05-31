package net.minecraft.world.item;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;

public class SuspiciousStewItem extends Item {
    public SuspiciousStewItem(final Properties a) {
        super(a);
    }
    
    public static void saveMobEffect(final ItemStack bcj, final MobEffect aig, final int integer) {
        final CompoundTag id4 = bcj.getOrCreateTag();
        final ListTag ik5 = id4.getList("Effects", 9);
        final CompoundTag id5 = new CompoundTag();
        id5.putByte("EffectId", (byte)MobEffect.getId(aig));
        id5.putInt("EffectDuration", integer);
        ik5.add(id5);
        id4.put("Effects", (Tag)ik5);
    }
    
    @Override
    public ItemStack finishUsingItem(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        super.finishUsingItem(bcj, bhr, aix);
        final CompoundTag id5 = bcj.getTag();
        if (id5 != null && id5.contains("Effects", 9)) {
            final ListTag ik6 = id5.getList("Effects", 10);
            for (int integer7 = 0; integer7 < ik6.size(); ++integer7) {
                int integer8 = 160;
                final CompoundTag id6 = ik6.getCompound(integer7);
                if (id6.contains("EffectDuration", 3)) {
                    integer8 = id6.getInt("EffectDuration");
                }
                final MobEffect aig10 = MobEffect.byId(id6.getByte("EffectId"));
                if (aig10 != null) {
                    aix.addEffect(new MobEffectInstance(aig10, integer8));
                }
            }
        }
        return new ItemStack(Items.BOWL);
    }
}
