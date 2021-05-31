package net.minecraft.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.entity.EntityType;

public class FishBucketItem extends BucketItem {
    private final EntityType<?> type;
    
    public FishBucketItem(final EntityType<?> ais, final Fluid clj, final Properties a) {
        super(clj, a);
        this.type = ais;
    }
    
    @Override
    public void checkExtraContent(final Level bhr, final ItemStack bcj, final BlockPos ew) {
        if (!bhr.isClientSide) {
            this.spawn(bhr, bcj, ew);
        }
    }
    
    @Override
    protected void playEmptySound(@Nullable final Player awg, final LevelAccessor bhs, final BlockPos ew) {
        bhs.playSound(awg, ew, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }
    
    private void spawn(final Level bhr, final ItemStack bcj, final BlockPos ew) {
        final Entity aio5 = this.type.spawn(bhr, bcj, null, ew, MobSpawnType.BUCKET, true, false);
        if (aio5 != null) {
            ((AbstractFish)aio5).setFromBucket(true);
        }
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        if (this.type == EntityType.TROPICAL_FISH) {
            final CompoundTag id6 = bcj.getTag();
            if (id6 != null && id6.contains("BucketVariantTag", 3)) {
                final int integer7 = id6.getInt("BucketVariantTag");
                final ChatFormatting[] arr8 = { ChatFormatting.ITALIC, ChatFormatting.GRAY };
                final String string9 = new StringBuilder().append("color.minecraft.").append(TropicalFish.getBaseColor(integer7)).toString();
                final String string10 = new StringBuilder().append("color.minecraft.").append(TropicalFish.getPatternColor(integer7)).toString();
                for (int integer8 = 0; integer8 < TropicalFish.COMMON_VARIANTS.length; ++integer8) {
                    if (integer7 == TropicalFish.COMMON_VARIANTS[integer8]) {
                        list.add(new TranslatableComponent(TropicalFish.getPredefinedName(integer8), new Object[0]).withStyle(arr8));
                        return;
                    }
                }
                list.add(new TranslatableComponent(TropicalFish.getFishTypeName(integer7), new Object[0]).withStyle(arr8));
                final Component jo11 = new TranslatableComponent(string9, new Object[0]);
                if (!string9.equals(string10)) {
                    jo11.append(", ").append(new TranslatableComponent(string10, new Object[0]));
                }
                jo11.withStyle(arr8);
                list.add(jo11);
            }
        }
    }
}
