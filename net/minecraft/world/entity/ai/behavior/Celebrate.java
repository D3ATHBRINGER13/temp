package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.FireworkRocketItem;
import com.google.common.collect.Lists;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import java.util.Random;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.npc.Villager;

public class Celebrate extends Behavior<Villager> {
    @Nullable
    private Raid currentRaid;
    
    public Celebrate(final int integer1, final int integer2) {
        super((Map)ImmutableMap.of(), integer1, integer2);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        this.currentRaid = vk.getRaidAt(new BlockPos(avt));
        return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(vk, avt);
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return this.currentRaid != null && !this.currentRaid.isStopped();
    }
    
    @Override
    protected void stop(final ServerLevel vk, final Villager avt, final long long3) {
        this.currentRaid = null;
        avt.getBrain().updateActivity(vk.getDayTime(), vk.getGameTime());
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        final Random random6 = avt.getRandom();
        if (random6.nextInt(100) == 0) {
            avt.playCelebrateSound();
        }
        if (random6.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(vk, avt)) {
            final DyeColor bbg7 = DyeColor.values()[random6.nextInt(DyeColor.values().length)];
            final int integer8 = random6.nextInt(3);
            final ItemStack bcj9 = this.getFirework(bbg7, integer8);
            final FireworkRocketEntity awr10 = new FireworkRocketEntity(avt.level, avt.x, avt.y + avt.getEyeHeight(), avt.z, bcj9);
            avt.level.addFreshEntity(awr10);
        }
    }
    
    private ItemStack getFirework(final DyeColor bbg, final int integer) {
        final ItemStack bcj4 = new ItemStack(Items.FIREWORK_ROCKET, 1);
        final ItemStack bcj5 = new ItemStack(Items.FIREWORK_STAR);
        final CompoundTag id6 = bcj5.getOrCreateTagElement("Explosion");
        final List<Integer> list7 = (List<Integer>)Lists.newArrayList();
        list7.add(bbg.getFireworkColor());
        id6.putIntArray("Colors", list7);
        id6.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getId());
        final CompoundTag id7 = bcj4.getOrCreateTagElement("Fireworks");
        final ListTag ik9 = new ListTag();
        final CompoundTag id8 = bcj5.getTagElement("Explosion");
        if (id8 != null) {
            ik9.add(id8);
        }
        id7.putByte("Flight", (byte)integer);
        if (!ik9.isEmpty()) {
            id7.put("Explosions", (Tag)ik9);
        }
        return bcj4;
    }
}
