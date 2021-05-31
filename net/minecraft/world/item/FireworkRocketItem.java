package net.minecraft.world.item;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import java.util.Collection;
import net.minecraft.network.chat.TextComponent;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.InteractionResult;

public class FireworkRocketItem extends Item {
    public FireworkRocketItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        if (!bhr3.isClientSide) {
            final ItemStack bcj4 = bdu.getItemInHand();
            final Vec3 csi5 = bdu.getClickLocation();
            final FireworkRocketEntity awr6 = new FireworkRocketEntity(bhr3, csi5.x, csi5.y, csi5.z, bcj4);
            bhr3.addFreshEntity(awr6);
            bcj4.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        if (awg.isFallFlying()) {
            final ItemStack bcj5 = awg.getItemInHand(ahi);
            if (!bhr.isClientSide) {
                bhr.addFreshEntity(new FireworkRocketEntity(bhr, bcj5, awg));
                if (!awg.abilities.instabuild) {
                    bcj5.shrink(1);
                }
            }
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, awg.getItemInHand(ahi));
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, awg.getItemInHand(ahi));
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        final CompoundTag id6 = bcj.getTagElement("Fireworks");
        if (id6 == null) {
            return;
        }
        if (id6.contains("Flight", 99)) {
            list.add(new TranslatableComponent("item.minecraft.firework_rocket.flight", new Object[0]).append(" ").append(String.valueOf((int)id6.getByte("Flight"))).withStyle(ChatFormatting.GRAY));
        }
        final ListTag ik7 = id6.getList("Explosions", 10);
        if (!ik7.isEmpty()) {
            for (int integer8 = 0; integer8 < ik7.size(); ++integer8) {
                final CompoundTag id7 = ik7.getCompound(integer8);
                final List<Component> list2 = (List<Component>)Lists.newArrayList();
                FireworkStarItem.appendHoverText(id7, list2);
                if (!list2.isEmpty()) {
                    for (int integer9 = 1; integer9 < list2.size(); ++integer9) {
                        list2.set(integer9, new TextComponent("  ").append((Component)list2.get(integer9)).withStyle(ChatFormatting.GRAY));
                    }
                    list.addAll((Collection)list2);
                }
            }
        }
    }
    
    public enum Shape {
        SMALL_BALL(0, "small_ball"), 
        LARGE_BALL(1, "large_ball"), 
        STAR(2, "star"), 
        CREEPER(3, "creeper"), 
        BURST(4, "burst");
        
        private static final Shape[] BY_ID;
        private final int id;
        private final String name;
        
        private Shape(final int integer3, final String string4) {
            this.id = integer3;
            this.name = string4;
        }
        
        public int getId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Shape byId(final int integer) {
            if (integer < 0 || integer >= Shape.BY_ID.length) {
                return Shape.SMALL_BALL;
            }
            return Shape.BY_ID[integer];
        }
        
        static {
            BY_ID = (Shape[])Arrays.stream((Object[])values()).sorted(Comparator.comparingInt(a -> a.id)).toArray(Shape[]::new);
        }
    }
}
