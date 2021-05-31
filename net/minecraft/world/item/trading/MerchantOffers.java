package net.minecraft.world.item.trading;

import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;

public class MerchantOffers extends ArrayList<MerchantOffer> {
    public MerchantOffers() {
    }
    
    public MerchantOffers(final CompoundTag id) {
        final ListTag ik3 = id.getList("Recipes", 10);
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            this.add(new MerchantOffer(ik3.getCompound(integer4)));
        }
    }
    
    @Nullable
    public MerchantOffer getRecipeFor(final ItemStack bcj1, final ItemStack bcj2, final int integer) {
        if (integer <= 0 || integer >= this.size()) {
            for (int integer2 = 0; integer2 < this.size(); ++integer2) {
                final MerchantOffer bgu6 = (MerchantOffer)this.get(integer2);
                if (bgu6.satisfiedBy(bcj1, bcj2)) {
                    return bgu6;
                }
            }
            return null;
        }
        final MerchantOffer bgu7 = (MerchantOffer)this.get(integer);
        if (bgu7.satisfiedBy(bcj1, bcj2)) {
            return bgu7;
        }
        return null;
    }
    
    public void writeToStream(final FriendlyByteBuf je) {
        je.writeByte((byte)(this.size() & 0xFF));
        for (int integer3 = 0; integer3 < this.size(); ++integer3) {
            final MerchantOffer bgu4 = (MerchantOffer)this.get(integer3);
            je.writeItem(bgu4.getBaseCostA());
            je.writeItem(bgu4.getResult());
            final ItemStack bcj5 = bgu4.getCostB();
            je.writeBoolean(!bcj5.isEmpty());
            if (!bcj5.isEmpty()) {
                je.writeItem(bcj5);
            }
            je.writeBoolean(bgu4.isOutOfStock());
            je.writeInt(bgu4.getUses());
            je.writeInt(bgu4.getMaxUses());
            je.writeInt(bgu4.getXp());
            je.writeInt(bgu4.getSpecialPriceDiff());
            je.writeFloat(bgu4.getPriceMultiplier());
            je.writeInt(bgu4.getDemand());
        }
    }
    
    public static MerchantOffers createFromStream(final FriendlyByteBuf je) {
        final MerchantOffers bgv2 = new MerchantOffers();
        for (int integer3 = je.readByte() & 0xFF, integer4 = 0; integer4 < integer3; ++integer4) {
            final ItemStack bcj5 = je.readItem();
            final ItemStack bcj6 = je.readItem();
            ItemStack bcj7 = ItemStack.EMPTY;
            if (je.readBoolean()) {
                bcj7 = je.readItem();
            }
            final boolean boolean8 = je.readBoolean();
            final int integer5 = je.readInt();
            final int integer6 = je.readInt();
            final int integer7 = je.readInt();
            final int integer8 = je.readInt();
            final float float13 = je.readFloat();
            final int integer9 = je.readInt();
            final MerchantOffer bgu15 = new MerchantOffer(bcj5, bcj7, bcj6, integer5, integer6, integer7, float13, integer9);
            if (boolean8) {
                bgu15.setToOutOfStock();
            }
            bgu15.setSpecialPriceDiff(integer8);
            bgv2.add(bgu15);
        }
        return bgv2;
    }
    
    public CompoundTag createTag() {
        final CompoundTag id2 = new CompoundTag();
        final ListTag ik3 = new ListTag();
        for (int integer4 = 0; integer4 < this.size(); ++integer4) {
            final MerchantOffer bgu5 = (MerchantOffer)this.get(integer4);
            ik3.add(bgu5.createTag());
        }
        id2.put("Recipes", (Tag)ik3);
        return id2;
    }
}
