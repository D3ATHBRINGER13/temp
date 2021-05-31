package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
    private static final Logger LOGGER;
    public static final Attribute MAX_HEALTH;
    public static final Attribute FOLLOW_RANGE;
    public static final Attribute KNOCKBACK_RESISTANCE;
    public static final Attribute MOVEMENT_SPEED;
    public static final Attribute FLYING_SPEED;
    public static final Attribute ATTACK_DAMAGE;
    public static final Attribute ATTACK_KNOCKBACK;
    public static final Attribute ATTACK_SPEED;
    public static final Attribute ARMOR;
    public static final Attribute ARMOR_TOUGHNESS;
    public static final Attribute LUCK;
    
    public static ListTag saveAttributes(final BaseAttributeMap ajr) {
        final ListTag ik2 = new ListTag();
        for (final AttributeInstance ajo4 : ajr.getAttributes()) {
            ik2.add(saveAttribute(ajo4));
        }
        return ik2;
    }
    
    private static CompoundTag saveAttribute(final AttributeInstance ajo) {
        final CompoundTag id2 = new CompoundTag();
        final Attribute ajn3 = ajo.getAttribute();
        id2.putString("Name", ajn3.getName());
        id2.putDouble("Base", ajo.getBaseValue());
        final Collection<AttributeModifier> collection4 = ajo.getModifiers();
        if (collection4 != null && !collection4.isEmpty()) {
            final ListTag ik5 = new ListTag();
            for (final AttributeModifier ajp7 : collection4) {
                if (ajp7.isSerializable()) {
                    ik5.add(saveAttributeModifier(ajp7));
                }
            }
            id2.put("Modifiers", (Tag)ik5);
        }
        return id2;
    }
    
    public static CompoundTag saveAttributeModifier(final AttributeModifier ajp) {
        final CompoundTag id2 = new CompoundTag();
        id2.putString("Name", ajp.getName());
        id2.putDouble("Amount", ajp.getAmount());
        id2.putInt("Operation", ajp.getOperation().toValue());
        id2.putUUID("UUID", ajp.getId());
        return id2;
    }
    
    public static void loadAttributes(final BaseAttributeMap ajr, final ListTag ik) {
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            final AttributeInstance ajo5 = ajr.getInstance(id4.getString("Name"));
            if (ajo5 == null) {
                SharedMonsterAttributes.LOGGER.warn("Ignoring unknown attribute '{}'", id4.getString("Name"));
            }
            else {
                loadAttribute(ajo5, id4);
            }
        }
    }
    
    private static void loadAttribute(final AttributeInstance ajo, final CompoundTag id) {
        ajo.setBaseValue(id.getDouble("Base"));
        if (id.contains("Modifiers", 9)) {
            final ListTag ik3 = id.getList("Modifiers", 10);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                final AttributeModifier ajp5 = loadAttributeModifier(ik3.getCompound(integer4));
                if (ajp5 != null) {
                    final AttributeModifier ajp6 = ajo.getModifier(ajp5.getId());
                    if (ajp6 != null) {
                        ajo.removeModifier(ajp6);
                    }
                    ajo.addModifier(ajp5);
                }
            }
        }
    }
    
    @Nullable
    public static AttributeModifier loadAttributeModifier(final CompoundTag id) {
        final UUID uUID2 = id.getUUID("UUID");
        try {
            final AttributeModifier.Operation a3 = AttributeModifier.Operation.fromValue(id.getInt("Operation"));
            return new AttributeModifier(uUID2, id.getString("Name"), id.getDouble("Amount"), a3);
        }
        catch (Exception exception3) {
            SharedMonsterAttributes.LOGGER.warn("Unable to create attribute: {}", exception3.getMessage());
            return null;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MAX_HEALTH = new RangedAttribute((Attribute)null, "generic.maxHealth", 20.0, 0.0, 1024.0).importLegacyName("Max Health").setSyncable(true);
        FOLLOW_RANGE = new RangedAttribute((Attribute)null, "generic.followRange", 32.0, 0.0, 2048.0).importLegacyName("Follow Range");
        KNOCKBACK_RESISTANCE = new RangedAttribute((Attribute)null, "generic.knockbackResistance", 0.0, 0.0, 1.0).importLegacyName("Knockback Resistance");
        MOVEMENT_SPEED = new RangedAttribute((Attribute)null, "generic.movementSpeed", 0.699999988079071, 0.0, 1024.0).importLegacyName("Movement Speed").setSyncable(true);
        FLYING_SPEED = new RangedAttribute((Attribute)null, "generic.flyingSpeed", 0.4000000059604645, 0.0, 1024.0).importLegacyName("Flying Speed").setSyncable(true);
        ATTACK_DAMAGE = new RangedAttribute((Attribute)null, "generic.attackDamage", 2.0, 0.0, 2048.0);
        ATTACK_KNOCKBACK = new RangedAttribute((Attribute)null, "generic.attackKnockback", 0.0, 0.0, 5.0);
        ATTACK_SPEED = new RangedAttribute((Attribute)null, "generic.attackSpeed", 4.0, 0.0, 1024.0).setSyncable(true);
        ARMOR = new RangedAttribute((Attribute)null, "generic.armor", 0.0, 0.0, 30.0).setSyncable(true);
        ARMOR_TOUGHNESS = new RangedAttribute((Attribute)null, "generic.armorToughness", 0.0, 0.0, 20.0).setSyncable(true);
        LUCK = new RangedAttribute((Attribute)null, "generic.luck", 0.0, -1024.0, 1024.0).setSyncable(true);
    }
}
