package net.minecraft.world.item;

import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import java.util.UUID;

public class ArmorItem extends Item {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT;
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR;
    protected final EquipmentSlot slot;
    protected final int defense;
    protected final float toughness;
    protected final ArmorMaterial material;
    
    public static ItemStack dispenseArmor(final BlockSource ex, final ItemStack bcj) {
        final BlockPos ew3 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
        final List<LivingEntity> list4 = ex.getLevel().<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, new AABB(ew3), (java.util.function.Predicate<? super LivingEntity>)EntitySelector.NO_SPECTATORS.and((Predicate)new EntitySelector.MobCanWearArmourEntitySelector(bcj)));
        if (list4.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final LivingEntity aix5 = (LivingEntity)list4.get(0);
        final EquipmentSlot ait6 = Mob.getEquipmentSlotForItem(bcj);
        final ItemStack bcj2 = bcj.split(1);
        aix5.setItemSlot(ait6, bcj2);
        if (aix5 instanceof Mob) {
            ((Mob)aix5).setDropChance(ait6, 2.0f);
            ((Mob)aix5).setPersistenceRequired();
        }
        return bcj;
    }
    
    public ArmorItem(final ArmorMaterial bae, final EquipmentSlot ait, final Properties a) {
        super(a.defaultDurability(bae.getDurabilityForSlot(ait)));
        this.material = bae;
        this.slot = ait;
        this.defense = bae.getDefenseForSlot(ait);
        this.toughness = bae.getToughness();
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }
    
    public EquipmentSlot getSlot() {
        return this.slot;
    }
    
    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }
    
    public ArmorMaterial getMaterial() {
        return this.material;
    }
    
    @Override
    public boolean isValidRepairItem(final ItemStack bcj1, final ItemStack bcj2) {
        return this.material.getRepairIngredient().test(bcj2) || super.isValidRepairItem(bcj1, bcj2);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final EquipmentSlot ait6 = Mob.getEquipmentSlotForItem(bcj5);
        final ItemStack bcj6 = awg.getItemBySlot(ait6);
        if (bcj6.isEmpty()) {
            awg.setItemSlot(ait6, bcj5.copy());
            bcj5.setCount(0);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
    
    @Override
    public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot ait) {
        final Multimap<String, AttributeModifier> multimap3 = super.getDefaultAttributeModifiers(ait);
        if (ait == this.slot) {
            multimap3.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ArmorItem.ARMOR_MODIFIER_UUID_PER_SLOT[ait.getIndex()], "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
            multimap3.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ArmorItem.ARMOR_MODIFIER_UUID_PER_SLOT[ait.getIndex()], "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        }
        return multimap3;
    }
    
    public int getDefense() {
        return this.defense;
    }
    
    static {
        ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };
        DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final ItemStack bcj2 = ArmorItem.dispenseArmor(ex, bcj);
                return bcj2.isEmpty() ? super.execute(ex, bcj) : bcj2;
            }
        };
    }
}
