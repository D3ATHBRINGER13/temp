package net.minecraft.world.item.alchemy;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class PotionUtils {
    public static List<MobEffectInstance> getMobEffects(final ItemStack bcj) {
        return getAllEffects(bcj.getTag());
    }
    
    public static List<MobEffectInstance> getAllEffects(final Potion bdy, final Collection<MobEffectInstance> collection) {
        final List<MobEffectInstance> list3 = (List<MobEffectInstance>)Lists.newArrayList();
        list3.addAll((Collection)bdy.getEffects());
        list3.addAll((Collection)collection);
        return list3;
    }
    
    public static List<MobEffectInstance> getAllEffects(@Nullable final CompoundTag id) {
        final List<MobEffectInstance> list2 = (List<MobEffectInstance>)Lists.newArrayList();
        list2.addAll((Collection)getPotion(id).getEffects());
        getCustomEffects(id, list2);
        return list2;
    }
    
    public static List<MobEffectInstance> getCustomEffects(final ItemStack bcj) {
        return getCustomEffects(bcj.getTag());
    }
    
    public static List<MobEffectInstance> getCustomEffects(@Nullable final CompoundTag id) {
        final List<MobEffectInstance> list2 = (List<MobEffectInstance>)Lists.newArrayList();
        getCustomEffects(id, list2);
        return list2;
    }
    
    public static void getCustomEffects(@Nullable final CompoundTag id, final List<MobEffectInstance> list) {
        if (id != null && id.contains("CustomPotionEffects", 9)) {
            final ListTag ik3 = id.getList("CustomPotionEffects", 10);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                final CompoundTag id2 = ik3.getCompound(integer4);
                final MobEffectInstance aii6 = MobEffectInstance.load(id2);
                if (aii6 != null) {
                    list.add(aii6);
                }
            }
        }
    }
    
    public static int getColor(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        if (id2 != null && id2.contains("CustomPotionColor", 99)) {
            return id2.getInt("CustomPotionColor");
        }
        return (getPotion(bcj) == Potions.EMPTY) ? 16253176 : getColor((Collection<MobEffectInstance>)getMobEffects(bcj));
    }
    
    public static int getColor(final Potion bdy) {
        return (bdy == Potions.EMPTY) ? 16253176 : getColor((Collection<MobEffectInstance>)bdy.getEffects());
    }
    
    public static int getColor(final Collection<MobEffectInstance> collection) {
        final int integer2 = 3694022;
        if (collection.isEmpty()) {
            return 3694022;
        }
        float float3 = 0.0f;
        float float4 = 0.0f;
        float float5 = 0.0f;
        int integer3 = 0;
        for (final MobEffectInstance aii8 : collection) {
            if (!aii8.isVisible()) {
                continue;
            }
            final int integer4 = aii8.getEffect().getColor();
            final int integer5 = aii8.getAmplifier() + 1;
            float3 += integer5 * (integer4 >> 16 & 0xFF) / 255.0f;
            float4 += integer5 * (integer4 >> 8 & 0xFF) / 255.0f;
            float5 += integer5 * (integer4 >> 0 & 0xFF) / 255.0f;
            integer3 += integer5;
        }
        if (integer3 == 0) {
            return 0;
        }
        float3 = float3 / integer3 * 255.0f;
        float4 = float4 / integer3 * 255.0f;
        float5 = float5 / integer3 * 255.0f;
        return (int)float3 << 16 | (int)float4 << 8 | (int)float5;
    }
    
    public static Potion getPotion(final ItemStack bcj) {
        return getPotion(bcj.getTag());
    }
    
    public static Potion getPotion(@Nullable final CompoundTag id) {
        if (id == null) {
            return Potions.EMPTY;
        }
        return Potion.byName(id.getString("Potion"));
    }
    
    public static ItemStack setPotion(final ItemStack bcj, final Potion bdy) {
        final ResourceLocation qv3 = Registry.POTION.getKey(bdy);
        if (bdy == Potions.EMPTY) {
            bcj.removeTagKey("Potion");
        }
        else {
            bcj.getOrCreateTag().putString("Potion", qv3.toString());
        }
        return bcj;
    }
    
    public static ItemStack setCustomEffects(final ItemStack bcj, final Collection<MobEffectInstance> collection) {
        if (collection.isEmpty()) {
            return bcj;
        }
        final CompoundTag id3 = bcj.getOrCreateTag();
        final ListTag ik4 = id3.getList("CustomPotionEffects", 9);
        for (final MobEffectInstance aii6 : collection) {
            ik4.add(aii6.save(new CompoundTag()));
        }
        id3.put("CustomPotionEffects", (Tag)ik4);
        return bcj;
    }
    
    public static void addPotionTooltip(final ItemStack bcj, final List<Component> list, final float float3) {
        final List<MobEffectInstance> list2 = getMobEffects(bcj);
        final List<Tuple<String, AttributeModifier>> list3 = (List<Tuple<String, AttributeModifier>>)Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(new TranslatableComponent("effect.none", new Object[0]).withStyle(ChatFormatting.GRAY));
        }
        else {
            for (final MobEffectInstance aii7 : list2) {
                final Component jo8 = new TranslatableComponent(aii7.getDescriptionId(), new Object[0]);
                final MobEffect aig9 = aii7.getEffect();
                final Map<Attribute, AttributeModifier> map10 = aig9.getAttributeModifiers();
                if (!map10.isEmpty()) {
                    for (final Map.Entry<Attribute, AttributeModifier> entry12 : map10.entrySet()) {
                        final AttributeModifier ajp13 = (AttributeModifier)entry12.getValue();
                        final AttributeModifier ajp14 = new AttributeModifier(ajp13.getName(), aig9.getAttributeModifierValue(aii7.getAmplifier(), ajp13), ajp13.getOperation());
                        list3.add(new Tuple(((Attribute)entry12.getKey()).getName(), ajp14));
                    }
                }
                if (aii7.getAmplifier() > 0) {
                    jo8.append(" ").append(new TranslatableComponent(new StringBuilder().append("potion.potency.").append(aii7.getAmplifier()).toString(), new Object[0]));
                }
                if (aii7.getDuration() > 20) {
                    jo8.append(" (").append(MobEffectUtil.formatDuration(aii7, float3)).append(")");
                }
                list.add(jo8.withStyle(aig9.getCategory().getTooltipFormatting()));
            }
        }
        if (!list3.isEmpty()) {
            list.add(new TextComponent(""));
            list.add(new TranslatableComponent("potion.whenDrank", new Object[0]).withStyle(ChatFormatting.DARK_PURPLE));
            for (final Tuple<String, AttributeModifier> aaf7 : list3) {
                final AttributeModifier ajp15 = aaf7.getB();
                final double double9 = ajp15.getAmount();
                double double10;
                if (ajp15.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || ajp15.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    double10 = ajp15.getAmount() * 100.0;
                }
                else {
                    double10 = ajp15.getAmount();
                }
                if (double9 > 0.0) {
                    list.add(new TranslatableComponent(new StringBuilder().append("attribute.modifier.plus.").append(ajp15.getOperation().toValue()).toString(), new Object[] { ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(double10), new TranslatableComponent("attribute.name." + (String)aaf7.getA(), new Object[0]) }).withStyle(ChatFormatting.BLUE));
                }
                else {
                    if (double9 >= 0.0) {
                        continue;
                    }
                    double10 *= -1.0;
                    list.add(new TranslatableComponent(new StringBuilder().append("attribute.modifier.take.").append(ajp15.getOperation().toValue()).toString(), new Object[] { ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(double10), new TranslatableComponent("attribute.name." + (String)aaf7.getA(), new Object[0]) }).withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}
