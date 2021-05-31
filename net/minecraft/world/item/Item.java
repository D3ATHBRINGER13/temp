package net.minecraft.world.item;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.tags.Tag;
import com.google.common.collect.HashMultimap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.world.food.FoodProperties;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import java.util.Random;
import java.util.UUID;
import net.minecraft.world.level.block.Block;
import java.util.Map;
import net.minecraft.world.level.ItemLike;

public class Item implements ItemLike {
    public static final Map<Block, Item> BY_BLOCK;
    private static final ItemPropertyFunction PROPERTY_DAMAGED;
    private static final ItemPropertyFunction PROPERTY_DAMAGE;
    private static final ItemPropertyFunction PROPERTY_LEFTHANDED;
    private static final ItemPropertyFunction PROPERTY_COOLDOWN;
    private static final ItemPropertyFunction PROPERTY_CUSTOM_MODEL_DATA;
    protected static final UUID BASE_ATTACK_DAMAGE_UUID;
    protected static final UUID BASE_ATTACK_SPEED_UUID;
    protected static final Random random;
    private final Map<ResourceLocation, ItemPropertyFunction> properties;
    protected final CreativeModeTab category;
    private final Rarity rarity;
    private final int maxStackSize;
    private final int maxDamage;
    private final Item craftingRemainingItem;
    @Nullable
    private String descriptionId;
    @Nullable
    private final FoodProperties foodProperties;
    
    public static int getId(final Item bce) {
        return (bce == null) ? 0 : Registry.ITEM.getId(bce);
    }
    
    public static Item byId(final int integer) {
        return Registry.ITEM.byId(integer);
    }
    
    @Deprecated
    public static Item byBlock(final Block bmv) {
        return (Item)Item.BY_BLOCK.getOrDefault(bmv, Items.AIR);
    }
    
    public Item(final Properties a) {
        this.properties = (Map<ResourceLocation, ItemPropertyFunction>)Maps.newHashMap();
        this.addProperty(new ResourceLocation("lefthanded"), Item.PROPERTY_LEFTHANDED);
        this.addProperty(new ResourceLocation("cooldown"), Item.PROPERTY_COOLDOWN);
        this.addProperty(new ResourceLocation("custom_model_data"), Item.PROPERTY_CUSTOM_MODEL_DATA);
        this.category = a.category;
        this.rarity = a.rarity;
        this.craftingRemainingItem = a.craftingRemainingItem;
        this.maxDamage = a.maxDamage;
        this.maxStackSize = a.maxStackSize;
        this.foodProperties = a.foodProperties;
        if (this.maxDamage > 0) {
            this.addProperty(new ResourceLocation("damaged"), Item.PROPERTY_DAMAGED);
            this.addProperty(new ResourceLocation("damage"), Item.PROPERTY_DAMAGE);
        }
    }
    
    public void onUseTick(final Level bhr, final LivingEntity aix, final ItemStack bcj, final int integer) {
    }
    
    @Nullable
    public ItemPropertyFunction getProperty(final ResourceLocation qv) {
        return (ItemPropertyFunction)this.properties.get(qv);
    }
    
    public boolean hasProperties() {
        return !this.properties.isEmpty();
    }
    
    public boolean verifyTagAfterLoad(final CompoundTag id) {
        return false;
    }
    
    public boolean canAttackBlock(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        return true;
    }
    
    public Item asItem() {
        return this;
    }
    
    public final void addProperty(final ResourceLocation qv, final ItemPropertyFunction bci) {
        this.properties.put(qv, bci);
    }
    
    public InteractionResult useOn(final UseOnContext bdu) {
        return InteractionResult.PASS;
    }
    
    public float getDestroySpeed(final ItemStack bcj, final BlockState bvt) {
        return 1.0f;
    }
    
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        if (!this.isEdible()) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, awg.getItemInHand(ahi));
        }
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (awg.canEat(this.getFoodProperties().canAlwaysEat())) {
            awg.startUsingItem(ahi);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
    
    public ItemStack finishUsingItem(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        if (this.isEdible()) {
            return aix.eat(bhr, bcj);
        }
        return bcj;
    }
    
    public final int getMaxStackSize() {
        return this.maxStackSize;
    }
    
    public final int getMaxDamage() {
        return this.maxDamage;
    }
    
    public boolean canBeDepleted() {
        return this.maxDamage > 0;
    }
    
    public boolean hurtEnemy(final ItemStack bcj, final LivingEntity aix2, final LivingEntity aix3) {
        return false;
    }
    
    public boolean mineBlock(final ItemStack bcj, final Level bhr, final BlockState bvt, final BlockPos ew, final LivingEntity aix) {
        return false;
    }
    
    public boolean canDestroySpecial(final BlockState bvt) {
        return false;
    }
    
    public boolean interactEnemy(final ItemStack bcj, final Player awg, final LivingEntity aix, final InteractionHand ahi) {
        return false;
    }
    
    public Component getDescription() {
        return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
    }
    
    public String toString() {
        return Registry.ITEM.getKey(this).getPath();
    }
    
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("item", Registry.ITEM.getKey(this));
        }
        return this.descriptionId;
    }
    
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
    
    public String getDescriptionId(final ItemStack bcj) {
        return this.getDescriptionId();
    }
    
    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }
    
    @Nullable
    public final Item getCraftingRemainingItem() {
        return this.craftingRemainingItem;
    }
    
    public boolean hasCraftingRemainingItem() {
        return this.craftingRemainingItem != null;
    }
    
    public void inventoryTick(final ItemStack bcj, final Level bhr, final Entity aio, final int integer, final boolean boolean5) {
    }
    
    public void onCraftedBy(final ItemStack bcj, final Level bhr, final Player awg) {
    }
    
    public boolean isComplex() {
        return false;
    }
    
    public UseAnim getUseAnimation(final ItemStack bcj) {
        return bcj.getItem().isEdible() ? UseAnim.EAT : UseAnim.NONE;
    }
    
    public int getUseDuration(final ItemStack bcj) {
        if (bcj.getItem().isEdible()) {
            return this.getFoodProperties().isFastFood() ? 16 : 32;
        }
        return 0;
    }
    
    public void releaseUsing(final ItemStack bcj, final Level bhr, final LivingEntity aix, final int integer) {
    }
    
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
    }
    
    public Component getName(final ItemStack bcj) {
        return new TranslatableComponent(this.getDescriptionId(bcj), new Object[0]);
    }
    
    public boolean isFoil(final ItemStack bcj) {
        return bcj.isEnchanted();
    }
    
    public Rarity getRarity(final ItemStack bcj) {
        if (!bcj.isEnchanted()) {
            return this.rarity;
        }
        switch (this.rarity) {
            case COMMON:
            case UNCOMMON: {
                return Rarity.RARE;
            }
            case RARE: {
                return Rarity.EPIC;
            }
            default: {
                return this.rarity;
            }
        }
    }
    
    public boolean isEnchantable(final ItemStack bcj) {
        return this.getMaxStackSize() == 1 && this.canBeDepleted();
    }
    
    protected static HitResult getPlayerPOVHitResult(final Level bhr, final Player awg, final ClipContext.Fluid b) {
        final float float4 = awg.xRot;
        final float float5 = awg.yRot;
        final Vec3 csi6 = awg.getEyePosition(1.0f);
        final float float6 = Mth.cos(-float5 * 0.017453292f - 3.1415927f);
        final float float7 = Mth.sin(-float5 * 0.017453292f - 3.1415927f);
        final float float8 = -Mth.cos(-float4 * 0.017453292f);
        final float float9 = Mth.sin(-float4 * 0.017453292f);
        final float float10 = float7 * float8;
        final float float11 = float9;
        final float float12 = float6 * float8;
        final double double14 = 5.0;
        final Vec3 csi7 = csi6.add(float10 * 5.0, float11 * 5.0, float12 * 5.0);
        return bhr.clip(new ClipContext(csi6, csi7, ClipContext.Block.OUTLINE, b, awg));
    }
    
    public int getEnchantmentValue() {
        return 0;
    }
    
    public void fillItemCategory(final CreativeModeTab bba, final NonNullList<ItemStack> fk) {
        if (this.allowdedIn(bba)) {
            fk.add(new ItemStack(this));
        }
    }
    
    protected boolean allowdedIn(final CreativeModeTab bba) {
        final CreativeModeTab bba2 = this.getItemCategory();
        return bba2 != null && (bba == CreativeModeTab.TAB_SEARCH || bba == bba2);
    }
    
    @Nullable
    public final CreativeModeTab getItemCategory() {
        return this.category;
    }
    
    public boolean isValidRepairItem(final ItemStack bcj1, final ItemStack bcj2) {
        return false;
    }
    
    public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot ait) {
        return (Multimap<String, AttributeModifier>)HashMultimap.create();
    }
    
    public boolean useOnRelease(final ItemStack bcj) {
        return bcj.getItem() == Items.CROSSBOW;
    }
    
    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }
    
    public boolean is(final Tag<Item> zg) {
        return zg.contains(this);
    }
    
    public boolean isEdible() {
        return this.foodProperties != null;
    }
    
    @Nullable
    public FoodProperties getFoodProperties() {
        return this.foodProperties;
    }
    
    static {
        BY_BLOCK = (Map)Maps.newHashMap();
        PROPERTY_DAMAGED = ((bcj, bhr, aix) -> bcj.isDamaged() ? 1.0f : 0.0f);
        PROPERTY_DAMAGE = ((bcj, bhr, aix) -> Mth.clamp(bcj.getDamageValue() / (float)bcj.getMaxDamage(), 0.0f, 1.0f));
        PROPERTY_LEFTHANDED = ((bcj, bhr, aix) -> (aix == null || aix.getMainArm() == HumanoidArm.RIGHT) ? 0.0f : 1.0f);
        PROPERTY_COOLDOWN = ((bcj, bhr, aix) -> (aix instanceof Player) ? aix.getCooldowns().getCooldownPercent(bcj.getItem(), 0.0f) : 0.0f);
        PROPERTY_CUSTOM_MODEL_DATA = ((bcj, bhr, aix) -> bcj.hasTag() ? ((float)bcj.getTag().getInt("CustomModelData")) : 0.0f);
        BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
        BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
        random = new Random();
    }
    
    public static class Properties {
        private int maxStackSize;
        private int maxDamage;
        private Item craftingRemainingItem;
        private CreativeModeTab category;
        private Rarity rarity;
        private FoodProperties foodProperties;
        
        public Properties() {
            this.maxStackSize = 64;
            this.rarity = Rarity.COMMON;
        }
        
        public Properties food(final FoodProperties ayh) {
            this.foodProperties = ayh;
            return this;
        }
        
        public Properties stacksTo(final int integer) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            }
            this.maxStackSize = integer;
            return this;
        }
        
        public Properties defaultDurability(final int integer) {
            return (this.maxDamage == 0) ? this.durability(integer) : this;
        }
        
        public Properties durability(final int integer) {
            this.maxDamage = integer;
            this.maxStackSize = 1;
            return this;
        }
        
        public Properties craftRemainder(final Item bce) {
            this.craftingRemainingItem = bce;
            return this;
        }
        
        public Properties tab(final CreativeModeTab bba) {
            this.category = bba;
            return this;
        }
        
        public Properties rarity(final Rarity bcw) {
            this.rarity = bcw;
            return this;
        }
    }
}
