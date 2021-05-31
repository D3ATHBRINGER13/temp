package net.minecraft.world.item.trading;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MerchantOffer {
    private final ItemStack baseCostA;
    private final ItemStack costB;
    private final ItemStack result;
    private int uses;
    private final int maxUses;
    private boolean rewardExp;
    private int specialPriceDiff;
    private int demand;
    private float priceMultiplier;
    private int xp;
    
    public MerchantOffer(final CompoundTag id) {
        this.rewardExp = true;
        this.xp = 1;
        this.baseCostA = ItemStack.of(id.getCompound("buy"));
        this.costB = ItemStack.of(id.getCompound("buyB"));
        this.result = ItemStack.of(id.getCompound("sell"));
        this.uses = id.getInt("uses");
        if (id.contains("maxUses", 99)) {
            this.maxUses = id.getInt("maxUses");
        }
        else {
            this.maxUses = 4;
        }
        if (id.contains("rewardExp", 1)) {
            this.rewardExp = id.getBoolean("rewardExp");
        }
        if (id.contains("xp", 3)) {
            this.xp = id.getInt("xp");
        }
        if (id.contains("priceMultiplier", 5)) {
            this.priceMultiplier = id.getFloat("priceMultiplier");
        }
        this.specialPriceDiff = id.getInt("specialPrice");
        this.demand = id.getInt("demand");
    }
    
    public MerchantOffer(final ItemStack bcj1, final ItemStack bcj2, final int integer3, final int integer4, final float float5) {
        this(bcj1, ItemStack.EMPTY, bcj2, integer3, integer4, float5);
    }
    
    public MerchantOffer(final ItemStack bcj1, final ItemStack bcj2, final ItemStack bcj3, final int integer4, final int integer5, final float float6) {
        this(bcj1, bcj2, bcj3, 0, integer4, integer5, float6);
    }
    
    public MerchantOffer(final ItemStack bcj1, final ItemStack bcj2, final ItemStack bcj3, final int integer4, final int integer5, final int integer6, final float float7) {
        this(bcj1, bcj2, bcj3, integer4, integer5, integer6, float7, 0);
    }
    
    public MerchantOffer(final ItemStack bcj1, final ItemStack bcj2, final ItemStack bcj3, final int integer4, final int integer5, final int integer6, final float float7, final int integer8) {
        this.rewardExp = true;
        this.xp = 1;
        this.baseCostA = bcj1;
        this.costB = bcj2;
        this.result = bcj3;
        this.uses = integer4;
        this.maxUses = integer5;
        this.xp = integer6;
        this.priceMultiplier = float7;
        this.demand = integer8;
    }
    
    public ItemStack getBaseCostA() {
        return this.baseCostA;
    }
    
    public ItemStack getCostA() {
        final int integer2 = this.baseCostA.getCount();
        final ItemStack bcj3 = this.baseCostA.copy();
        final int integer3 = Math.max(0, Mth.floor(integer2 * this.demand * this.priceMultiplier));
        bcj3.setCount(Mth.clamp(integer2 + integer3 + this.specialPriceDiff, 1, this.baseCostA.getItem().getMaxStackSize()));
        return bcj3;
    }
    
    public ItemStack getCostB() {
        return this.costB;
    }
    
    public ItemStack getResult() {
        return this.result;
    }
    
    public void updateDemand() {
        this.demand = this.demand + this.uses - (this.maxUses - this.uses);
    }
    
    public ItemStack assemble() {
        return this.result.copy();
    }
    
    public int getUses() {
        return this.uses;
    }
    
    public void resetUses() {
        this.uses = 0;
    }
    
    public int getMaxUses() {
        return this.maxUses;
    }
    
    public void increaseUses() {
        ++this.uses;
    }
    
    public int getDemand() {
        return this.demand;
    }
    
    public void addToSpecialPriceDiff(final int integer) {
        this.specialPriceDiff += integer;
    }
    
    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }
    
    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }
    
    public void setSpecialPriceDiff(final int integer) {
        this.specialPriceDiff = integer;
    }
    
    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }
    
    public int getXp() {
        return this.xp;
    }
    
    public boolean isOutOfStock() {
        return this.uses >= this.maxUses;
    }
    
    public void setToOutOfStock() {
        this.uses = this.maxUses;
    }
    
    public boolean shouldRewardExp() {
        return this.rewardExp;
    }
    
    public CompoundTag createTag() {
        final CompoundTag id2 = new CompoundTag();
        id2.put("buy", (Tag)this.baseCostA.save(new CompoundTag()));
        id2.put("sell", (Tag)this.result.save(new CompoundTag()));
        id2.put("buyB", (Tag)this.costB.save(new CompoundTag()));
        id2.putInt("uses", this.uses);
        id2.putInt("maxUses", this.maxUses);
        id2.putBoolean("rewardExp", this.rewardExp);
        id2.putInt("xp", this.xp);
        id2.putFloat("priceMultiplier", this.priceMultiplier);
        id2.putInt("specialPrice", this.specialPriceDiff);
        id2.putInt("demand", this.demand);
        return id2;
    }
    
    public boolean satisfiedBy(final ItemStack bcj1, final ItemStack bcj2) {
        return this.isRequiredItem(bcj1, this.getCostA()) && bcj1.getCount() >= this.getCostA().getCount() && this.isRequiredItem(bcj2, this.costB) && bcj2.getCount() >= this.costB.getCount();
    }
    
    private boolean isRequiredItem(final ItemStack bcj1, final ItemStack bcj2) {
        if (bcj2.isEmpty() && bcj1.isEmpty()) {
            return true;
        }
        final ItemStack bcj3 = bcj1.copy();
        if (bcj3.getItem().canBeDepleted()) {
            bcj3.setDamageValue(bcj3.getDamageValue());
        }
        return ItemStack.isSame(bcj3, bcj2) && (!bcj2.hasTag() || (bcj3.hasTag() && NbtUtils.compareNbt(bcj2.getTag(), bcj3.getTag(), false)));
    }
    
    public boolean take(final ItemStack bcj1, final ItemStack bcj2) {
        if (!this.satisfiedBy(bcj1, bcj2)) {
            return false;
        }
        bcj1.shrink(this.getCostA().getCount());
        if (!this.getCostB().isEmpty()) {
            bcj2.shrink(this.getCostB().getCount());
        }
        return true;
    }
}
