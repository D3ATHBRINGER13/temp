package net.minecraft.world.item;

public class TieredItem extends Item {
    private final Tier tier;
    
    public TieredItem(final Tier bdn, final Properties a) {
        super(a.defaultDurability(bdn.getUses()));
        this.tier = bdn;
    }
    
    public Tier getTier() {
        return this.tier;
    }
    
    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }
    
    @Override
    public boolean isValidRepairItem(final ItemStack bcj1, final ItemStack bcj2) {
        return this.tier.getRepairIngredient().test(bcj2) || super.isValidRepairItem(bcj1, bcj2);
    }
}
