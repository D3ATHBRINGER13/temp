package net.minecraft.world.item;

import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import java.util.Random;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.Entity;
import java.util.function.Consumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public class CrossbowItem extends ProjectileWeaponItem {
    private boolean startSoundPlayed;
    private boolean midLoadSoundPlayed;
    
    public CrossbowItem(final Properties a) {
        super(a);
        this.startSoundPlayed = false;
        this.midLoadSoundPlayed = false;
        this.addProperty(new ResourceLocation("pull"), (bcj, bhr, aix) -> {
            if (aix == null || bcj.getItem() != this) {
                return 0.0f;
            }
            else if (isCharged(bcj)) {
                return 0.0f;
            }
            else {
                return (bcj.getUseDuration() - aix.getUseItemRemainingTicks()) / (float)getChargeDuration(bcj);
            }
        });
        this.addProperty(new ResourceLocation("pulling"), (bcj, bhr, aix) -> (aix != null && aix.isUsingItem() && aix.getUseItem() == bcj && !isCharged(bcj)) ? 1.0f : 0.0f);
        this.addProperty(new ResourceLocation("charged"), (bcj, bhr, aix) -> (aix != null && isCharged(bcj)) ? 1.0f : 0.0f);
        this.addProperty(new ResourceLocation("firework"), (bcj, bhr, aix) -> (aix != null && isCharged(bcj) && containsChargedProjectile(bcj, Items.FIREWORK_ROCKET)) ? 1.0f : 0.0f);
    }
    
    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return CrossbowItem.ARROW_OR_FIREWORK;
    }
    
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return CrossbowItem.ARROW_ONLY;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (isCharged(bcj5)) {
            performShooting(bhr, awg, ahi, bcj5, getShootingPower(bcj5), 1.0f);
            setCharged(bcj5, false);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        if (!awg.getProjectile(bcj5).isEmpty()) {
            if (!isCharged(bcj5)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                awg.startUsingItem(ahi);
            }
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
    
    @Override
    public void releaseUsing(final ItemStack bcj, final Level bhr, final LivingEntity aix, final int integer) {
        final int integer2 = this.getUseDuration(bcj) - integer;
        final float float7 = getPowerForTime(integer2, bcj);
        if (float7 >= 1.0f && !isCharged(bcj) && tryLoadProjectiles(aix, bcj)) {
            setCharged(bcj, true);
            final SoundSource yq8 = (aix instanceof Player) ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            bhr.playSound(null, aix.x, aix.y, aix.z, SoundEvents.CROSSBOW_LOADING_END, yq8, 1.0f, 1.0f / (CrossbowItem.random.nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }
    
    private static boolean tryLoadProjectiles(final LivingEntity aix, final ItemStack bcj) {
        final int integer3 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, bcj);
        final int integer4 = (integer3 == 0) ? 1 : 3;
        final boolean boolean5 = aix instanceof Player && ((Player)aix).abilities.instabuild;
        ItemStack bcj2 = aix.getProjectile(bcj);
        ItemStack bcj3 = bcj2.copy();
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            if (integer5 > 0) {
                bcj2 = bcj3.copy();
            }
            if (bcj2.isEmpty() && boolean5) {
                bcj2 = new ItemStack(Items.ARROW);
                bcj3 = bcj2.copy();
            }
            if (!loadProjectile(aix, bcj, bcj2, integer5 > 0, boolean5)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean loadProjectile(final LivingEntity aix, final ItemStack bcj2, final ItemStack bcj3, final boolean boolean4, final boolean boolean5) {
        if (bcj3.isEmpty()) {
            return false;
        }
        final boolean boolean6 = boolean5 && bcj3.getItem() instanceof ArrowItem;
        ItemStack bcj4;
        if (!boolean6 && !boolean5 && !boolean4) {
            bcj4 = bcj3.split(1);
            if (bcj3.isEmpty() && aix instanceof Player) {
                ((Player)aix).inventory.removeItem(bcj3);
            }
        }
        else {
            bcj4 = bcj3.copy();
        }
        addChargedProjectile(bcj2, bcj4);
        return true;
    }
    
    public static boolean isCharged(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        return id2 != null && id2.getBoolean("Charged");
    }
    
    public static void setCharged(final ItemStack bcj, final boolean boolean2) {
        final CompoundTag id3 = bcj.getOrCreateTag();
        id3.putBoolean("Charged", boolean2);
    }
    
    private static void addChargedProjectile(final ItemStack bcj1, final ItemStack bcj2) {
        final CompoundTag id3 = bcj1.getOrCreateTag();
        ListTag ik4;
        if (id3.contains("ChargedProjectiles", 9)) {
            ik4 = id3.getList("ChargedProjectiles", 10);
        }
        else {
            ik4 = new ListTag();
        }
        final CompoundTag id4 = new CompoundTag();
        bcj2.save(id4);
        ik4.add(id4);
        id3.put("ChargedProjectiles", (Tag)ik4);
    }
    
    private static List<ItemStack> getChargedProjectiles(final ItemStack bcj) {
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayList();
        final CompoundTag id3 = bcj.getTag();
        if (id3 != null && id3.contains("ChargedProjectiles", 9)) {
            final ListTag ik4 = id3.getList("ChargedProjectiles", 10);
            if (ik4 != null) {
                for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                    final CompoundTag id4 = ik4.getCompound(integer5);
                    list2.add(ItemStack.of(id4));
                }
            }
        }
        return list2;
    }
    
    private static void clearChargedProjectiles(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        if (id2 != null) {
            final ListTag ik3 = id2.getList("ChargedProjectiles", 9);
            ik3.clear();
            id2.put("ChargedProjectiles", (Tag)ik3);
        }
    }
    
    private static boolean containsChargedProjectile(final ItemStack bcj, final Item bce) {
        return getChargedProjectiles(bcj).stream().anyMatch(bcj -> bcj.getItem() == bce);
    }
    
    private static void shootProjectile(final Level bhr, final LivingEntity aix, final InteractionHand ahi, final ItemStack bcj4, final ItemStack bcj5, final float float6, final boolean boolean7, final float float8, final float float9, final float float10) {
        if (bhr.isClientSide) {
            return;
        }
        final boolean boolean8 = bcj5.getItem() == Items.FIREWORK_ROCKET;
        Projectile awv12;
        if (boolean8) {
            awv12 = new FireworkRocketEntity(bhr, bcj5, aix.x, aix.y + aix.getEyeHeight() - 0.15000000596046448, aix.z, true);
        }
        else {
            awv12 = getArrow(bhr, aix, bcj4, bcj5);
            if (boolean7 || float10 != 0.0f) {
                ((AbstractArrow)awv12).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
        }
        if (aix instanceof CrossbowAttackMob) {
            final CrossbowAttackMob auf13 = (CrossbowAttackMob)aix;
            auf13.shootProjectile(auf13.getTarget(), bcj4, awv12, float10);
        }
        else {
            final Vec3 csi13 = aix.getUpVector(1.0f);
            final Quaternion a14 = new Quaternion(new Vector3f(csi13), float10, true);
            final Vec3 csi14 = aix.getViewVector(1.0f);
            final Vector3f b16 = new Vector3f(csi14);
            b16.transform(a14);
            awv12.shoot(b16.x(), b16.y(), b16.z(), float8, float9);
        }
        bcj4.<LivingEntity>hurtAndBreak(boolean8 ? 3 : 1, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(ahi)));
        bhr.addFreshEntity((Entity)awv12);
        bhr.playSound(null, aix.x, aix.y, aix.z, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0f, float6);
    }
    
    private static AbstractArrow getArrow(final Level bhr, final LivingEntity aix, final ItemStack bcj3, final ItemStack bcj4) {
        final ArrowItem bah5 = (ArrowItem)((bcj4.getItem() instanceof ArrowItem) ? bcj4.getItem() : Items.ARROW);
        final AbstractArrow awk6 = bah5.createArrow(bhr, bcj4, aix);
        if (aix instanceof Player) {
            awk6.setCritArrow(true);
        }
        awk6.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        awk6.setShotFromCrossbow(true);
        final int integer7 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, bcj3);
        if (integer7 > 0) {
            awk6.setPierceLevel((byte)integer7);
        }
        return awk6;
    }
    
    public static void performShooting(final Level bhr, final LivingEntity aix, final InteractionHand ahi, final ItemStack bcj, final float float5, final float float6) {
        final List<ItemStack> list7 = getChargedProjectiles(bcj);
        final float[] arr8 = getShotPitches(aix.getRandom());
        for (int integer9 = 0; integer9 < list7.size(); ++integer9) {
            final ItemStack bcj2 = (ItemStack)list7.get(integer9);
            final boolean boolean11 = aix instanceof Player && ((Player)aix).abilities.instabuild;
            if (!bcj2.isEmpty()) {
                if (integer9 == 0) {
                    shootProjectile(bhr, aix, ahi, bcj, bcj2, arr8[integer9], boolean11, float5, float6, 0.0f);
                }
                else if (integer9 == 1) {
                    shootProjectile(bhr, aix, ahi, bcj, bcj2, arr8[integer9], boolean11, float5, float6, -10.0f);
                }
                else if (integer9 == 2) {
                    shootProjectile(bhr, aix, ahi, bcj, bcj2, arr8[integer9], boolean11, float5, float6, 10.0f);
                }
            }
        }
        onCrossbowShot(bhr, aix, bcj);
    }
    
    private static float[] getShotPitches(final Random random) {
        final boolean boolean2 = random.nextBoolean();
        return new float[] { 1.0f, getRandomShotPitch(boolean2), getRandomShotPitch(!boolean2) };
    }
    
    private static float getRandomShotPitch(final boolean boolean1) {
        final float float2 = boolean1 ? 0.63f : 0.43f;
        return 1.0f / (CrossbowItem.random.nextFloat() * 0.5f + 1.8f) + float2;
    }
    
    private static void onCrossbowShot(final Level bhr, final LivingEntity aix, final ItemStack bcj) {
        if (aix instanceof ServerPlayer) {
            final ServerPlayer vl4 = (ServerPlayer)aix;
            if (!bhr.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(vl4, bcj);
            }
            vl4.awardStat(Stats.ITEM_USED.get(bcj.getItem()));
        }
        clearChargedProjectiles(bcj);
    }
    
    @Override
    public void onUseTick(final Level bhr, final LivingEntity aix, final ItemStack bcj, final int integer) {
        if (!bhr.isClientSide) {
            final int integer2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, bcj);
            final SoundEvent yo7 = this.getStartSound(integer2);
            final SoundEvent yo8 = (integer2 == 0) ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            final float float9 = (bcj.getUseDuration() - integer) / (float)getChargeDuration(bcj);
            if (float9 < 0.2f) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }
            if (float9 >= 0.2f && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                bhr.playSound(null, aix.x, aix.y, aix.z, yo7, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
            if (float9 >= 0.5f && yo8 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                bhr.playSound(null, aix.x, aix.y, aix.z, yo8, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
        }
    }
    
    @Override
    public int getUseDuration(final ItemStack bcj) {
        return getChargeDuration(bcj) + 3;
    }
    
    public static int getChargeDuration(final ItemStack bcj) {
        final int integer2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, bcj);
        return (integer2 == 0) ? 25 : (25 - 5 * integer2);
    }
    
    @Override
    public UseAnim getUseAnimation(final ItemStack bcj) {
        return UseAnim.CROSSBOW;
    }
    
    private SoundEvent getStartSound(final int integer) {
        switch (integer) {
            case 1: {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            }
            case 2: {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            }
            case 3: {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            }
            default: {
                return SoundEvents.CROSSBOW_LOADING_START;
            }
        }
    }
    
    private static float getPowerForTime(final int integer, final ItemStack bcj) {
        float float3 = integer / (float)getChargeDuration(bcj);
        if (float3 > 1.0f) {
            float3 = 1.0f;
        }
        return float3;
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        final List<ItemStack> list2 = getChargedProjectiles(bcj);
        if (!isCharged(bcj) || list2.isEmpty()) {
            return;
        }
        final ItemStack bcj2 = (ItemStack)list2.get(0);
        list.add(new TranslatableComponent("item.minecraft.crossbow.projectile", new Object[0]).append(" ").append(bcj2.getDisplayName()));
        if (bdr.isAdvanced() && bcj2.getItem() == Items.FIREWORK_ROCKET) {
            final List<Component> list3 = (List<Component>)Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(bcj2, bhr, list3, bdr);
            if (!list3.isEmpty()) {
                for (int integer9 = 0; integer9 < list3.size(); ++integer9) {
                    list3.set(integer9, new TextComponent("  ").append((Component)list3.get(integer9)).withStyle(ChatFormatting.GRAY));
                }
                list.addAll((Collection)list3);
            }
        }
    }
    
    private static float getShootingPower(final ItemStack bcj) {
        if (bcj.getItem() == Items.CROSSBOW && containsChargedProjectile(bcj, Items.FIREWORK_ROCKET)) {
            return 1.6f;
        }
        return 3.15f;
    }
}
