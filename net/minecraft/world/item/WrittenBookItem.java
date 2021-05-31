package net.minecraft.world.item;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class WrittenBookItem extends Item {
    public WrittenBookItem(final Properties a) {
        super(a);
    }
    
    public static boolean makeSureTagIsValid(@Nullable final CompoundTag id) {
        if (!WritableBookItem.makeSureTagIsValid(id)) {
            return false;
        }
        if (!id.contains("title", 8)) {
            return false;
        }
        final String string2 = id.getString("title");
        return string2.length() <= 32 && id.contains("author", 8);
    }
    
    public static int getGeneration(final ItemStack bcj) {
        return bcj.getTag().getInt("generation");
    }
    
    public static int getPageCount(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        return (id2 != null) ? id2.getList("pages", 8).size() : 0;
    }
    
    @Override
    public Component getName(final ItemStack bcj) {
        if (bcj.hasTag()) {
            final CompoundTag id3 = bcj.getTag();
            final String string4 = id3.getString("title");
            if (!StringUtil.isNullOrEmpty(string4)) {
                return new TextComponent(string4);
            }
        }
        return super.getName(bcj);
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        if (bcj.hasTag()) {
            final CompoundTag id6 = bcj.getTag();
            final String string7 = id6.getString("author");
            if (!StringUtil.isNullOrEmpty(string7)) {
                list.add(new TranslatableComponent("book.byAuthor", new Object[] { string7 }).withStyle(ChatFormatting.GRAY));
            }
            list.add(new TranslatableComponent(new StringBuilder().append("book.generation.").append(id6.getInt("generation")).toString(), new Object[0]).withStyle(ChatFormatting.GRAY));
        }
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (bvt5.getBlock() == Blocks.LECTERN) {
            return LecternBlock.tryPlaceBook(bhr3, ew4, bvt5, bdu.getItemInHand()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        awg.openItemGui(bcj5, ahi);
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    public static boolean resolveBookComponents(final ItemStack bcj, @Nullable final CommandSourceStack cd, @Nullable final Player awg) {
        final CompoundTag id4 = bcj.getTag();
        if (id4 == null || id4.getBoolean("resolved")) {
            return false;
        }
        id4.putBoolean("resolved", true);
        if (!makeSureTagIsValid(id4)) {
            return false;
        }
        final ListTag ik5 = id4.getList("pages", 8);
        for (int integer6 = 0; integer6 < ik5.size(); ++integer6) {
            final String string7 = ik5.getString(integer6);
            Component jo8;
            try {
                jo8 = Component.Serializer.fromJsonLenient(string7);
                jo8 = ComponentUtils.updateForEntity(cd, jo8, awg, 0);
            }
            catch (Exception exception9) {
                jo8 = new TextComponent(string7);
            }
            ik5.set(integer6, new StringTag(Component.Serializer.toJson(jo8)));
        }
        id4.put("pages", (Tag)ik5);
        return true;
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return true;
    }
}
